package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

/**
 * 消息中心，用来存放消息的。
 * 
 * @author zyx
 *
 */
public class MessageCenter {
	//private static Logger logger = LoggerFactory.getLogger(MessageCenter.class);
	private static final String TAG = MessageCenter.class.getName();
	
	/**
	 * 消息订阅者树。
	 */
	private static ConsumerTree consumerTree = new ConsumerTree();
	
	/**
	 * 使用消费者事物的路径为key的消费者的缓存。
	 */
	private static Map<String, MessageConsumer> consumerCache = new HashMap<String, MessageConsumer>();
	
	private static List<Message> messages = new CopyOnWriteArrayList<Message>();
	private static Object lockObj = new Object();
	
	static {
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						int ignoreCount = 0;
						while(messages.size() > 0) {
							Message message = messages.remove(0);
							if(message == null) {
								continue; 
							}
							if(messages.size() > 10000) {
								ignoreCount ++;
								continue;
							}
							if(ignoreCount > 0) {
								Message mesg = new Message("/system/message/ingore", ignoreCount, null);
								handleMessage(mesg);
								ignoreCount = 0;
							}
							
							handleMessage(message);
							//System.out.println(message.getTopic());
							
						}
					}catch(Exception e) {	
						Executor.error(TAG, "Handle system message error", e);
					}
					
					if(messages.size() == 0) {
						synchronized(lockObj) {
							try {
								lockObj.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}, "xworker message handle thread").start();
	}
	
	private static void handleMessage(Message message) {
		String topic = message.getTopic();
		List<MessageConsumer> cs = consumerTree.getConsumers(topic);
		if(cs != null) {
			for(MessageConsumer mc : cs) {
				try {
					mc.handleMessage(message);
				}catch(Exception e) {
					Executor.error(TAG, "Handle system message error, handler=" + mc 
							+ ",topic=" + topic, e);
				}
			}
		}
	}
	
	/**
	 * 注册或更新一个消息消费者。
	 * 
	 * @param thing
	 * @param actionContext
	 */
	public static void registConsumer(Thing thing, ActionContext actionContext) {
		if(thing == null) {
			return;
		}

		List<String> topics = thing.doAction("getTopics", actionContext);
		if(topics == null || topics.size() == 0) {
			//消费者一定要有监听的主题
			return;
		}
		
		//已经移除监听的主题
		List<String> removedTopics = new ArrayList<String>();
		//新增的要监听的主题
		List<String> addedTopics = new ArrayList<String>();
		
		MessageConsumer mc = consumerCache.get(thing.getMetadata().getPath());
		if(mc != null) {			
			for(String t1 : mc.topics) {
				boolean have = false;
				for(String t2 : topics) {
					if(t1.equals(t2)) {
						have = true;
						break;
					}
				}
				if(have) {
					//旧的主题中不包含在新的主题中，删除
					removedTopics.add(t1);
				}
			}
			
			for(String t1 : topics) {
				boolean have = false;
				for(String t2 : mc.topics) {
					if(t1.equals(t2)) {
						have = true;
						break;
					}
				}
				if(have) {
					//新的主题中不包含在旧的主题中，是新增的
					addedTopics.add(t1);
				}
			}
			
		}else {
			addedTopics = topics;
			mc = new MessageConsumer(thing, topics, actionContext);
		}
		
		consumerCache.put(thing.getMetadata().getPath(), mc);
		for(String topic : removedTopics) {
			consumerTree.unsubscribe(mc, topic);
		}
		
		for(String topic : addedTopics) {
			consumerTree.subscribe(mc, topic);
		}
	}
	
	/**
	 * 消费者订阅一个主题。
	 * 
	 * @param consumer
	 * @param topic
	 */
	public static void subscribe(MessageConsumer consumer, String topic) {
		consumerTree.subscribe(consumer, topic);
	}
	
	/**
	 * 消费者取消订阅一个主题。
	 * 
	 * @param consumer
	 * @param topic
	 */
	public static void unsubscribe(MessageConsumer consumer, String topic) {
		consumerTree.unsubscribe(consumer, topic);
	}
	
	public static void updateConsumer(Thing thing, ActionContext actionContext) {
		registConsumer(thing, actionContext);
	}
	
	public static void unregistConsumer(Thing thing) {
		if(thing == null) {
			return;
		}
		
		unregistConsumer(thing.getMetadata().getPath());
	}
	
	public static void unregistConsumer(String thingPath) {
		if(thingPath == null) {
			return;
		}
		
		MessageConsumer mc = consumerCache.get(thingPath);
		if(mc != null) {	
			for(String topic : mc.topics) {
				consumerTree.unsubscribe(mc, topic);
			}
		}
	}
	
	public static List<MessageConsumer> getConsumers(String topic){
		return consumerTree.getConsumers(topic);
	}
		
	/**
	 * 快速发布一个消息。
	 * 
	 * @param topic
	 * @param content
	 * @param actionContext
	 */
	public static void publish(String topic, Object content, ActionContext actionContext) {
		Message message = new Message(topic, content, actionContext);
		publish(message);
	}
	
	/**
	 * 发布消息，消息是异步处理的。
	 * 
	 * @param message
	 */
	public static void publish(Message message) {
		messages.add(message);
		synchronized(lockObj) {
			lockObj.notifyAll();
		}
	}
}
