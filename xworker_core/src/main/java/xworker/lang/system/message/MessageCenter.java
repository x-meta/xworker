package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 消息中心，用来存放消息的。
 * 
 * @author zyx
 *
 */
public class MessageCenter {
	private static Logger logger = LoggerFactory.getLogger(MessageCenter.class);
	
	/**
	 * 使用消息主题为key的消息消费者的缓存列表。
	 */
	private static Map<String, List<MessageConsumer>> consumers = new HashMap<String, List<MessageConsumer>>();
	
	/**
	 * 使用消费者事物的路径为key的消费者的缓存。
	 */
	private static Map<String, MessageConsumer> consumerCache = new HashMap<String, MessageConsumer>();
	
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
			List<MessageConsumer> cs = consumers.get(topic);
			if(cs != null) {
				cs.remove(mc);
			}
		}
		
		for(String topic : addedTopics) {
			List<MessageConsumer> cs = consumers.get(topic);
			if(cs == null) {
				cs = new CopyOnWriteArrayList<MessageConsumer>();
				consumers.put(topic, cs);
			}
			
			if(!cs.contains(mc)) {
				cs.add(mc);
			}
		}
	}
	
	public static void updateConsumer(Thing thing, ActionContext actionContext) {
		registConsumer(thing, actionContext);
	}
	
	public static void unregistConsumer(Thing thing) {
		if(thing == null) {
			return;
		}
		
		MessageConsumer mc = consumerCache.get(thing.getMetadata().getPath());
		if(mc != null) {	
			for(String topic : mc.topics) {
				List<MessageConsumer> cs = consumers.get(topic);
				if(cs != null) {
					cs.remove(mc);
				}
			}
		}
	}
	
	public static List<MessageConsumer> getConsumers(String topic){
		return consumers.get(topic);
	}
	
	public static void sendMessage(Message message) {
		String topic = message.getTopic();
		List<MessageConsumer> cs = consumers.get(topic);
		if(cs != null) {
			for(MessageConsumer mc : cs) {
				try {
					mc.handleMessage(message);
				}catch(Exception e) {
					logger.error("Handle system message error, handler=" + mc.thing.getMetadata().getPath(), e);
				}
			}
		}
	}
}
