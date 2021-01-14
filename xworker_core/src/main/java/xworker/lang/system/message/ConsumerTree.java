package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumerTree {
	ConsumerTreeItem rootItem = new ConsumerTreeItem("/", null);
	
	/**
	 * 消费者订阅主题。
	 * 
	 * @param consumer
	 * @param topic
	 */
	public void subscribe(MessageConsumer consumer, String topic) {		
		if(topic == null || "".equals(topic)) {
			//不能订阅空主题，如果要订阅所有主题，使用通配符
			return;
		}
		
		synchronized(rootItem) {
			String topics[] = topic.split("[/]");
			ConsumerTreeItem item = rootItem;
			for(String topicName : topics) {
				topicName = topicName.trim();
				if("".equals(topicName)) {
					continue;
				}
				
				ConsumerTreeItem it = item.items.get(topicName);
				if(it == null) {
					it = new ConsumerTreeItem(topicName, item);
					item.items.put(topicName, it);
				}				
				
				item = it;
			}
			
			if(item.comsumers.contains(consumer) == false) {
				item.comsumers.add(consumer);
			}
			//item.comsumers.add(consumer);
		}			
	}
	
	/**
	 * 取消订阅一个主题。
	 * 
	 * @param consumer
	 * @param topic
	 */
	public void unsubscribe(MessageConsumer consumer, String topic) {		
		if(topic == null || "".equals(topic)) {
			//不能订阅空主题，如果要订阅所有主题，使用通配符
			return;
		}
		
		synchronized(rootItem) {
			String topics[] = topic.split("[/]");
			ConsumerTreeItem item = rootItem;
			for(String topicName : topics) {
				topicName = topicName.trim();
				if("".equals(topicName)) {
					continue;
				}
				
				ConsumerTreeItem it = item.items.get(topicName);								
				item = it;
				if(item == null) {
					break;
				}
			}
			
			if(item != null) {
				item.comsumers.remove(consumer);
			}
		}			
	}
	
	/**
	 * 根据主题返回所有订阅者。
	 * 
	 *  @param topic
	 */
	public List<MessageConsumer> getConsumers(String topic){
		if(topic == null || "".equals(topic)) {
			//不能订阅空主题，如果要订阅所有主题，使用通配符
			return Collections.emptyList();
		}
		
		List<MessageConsumer> consumers = new ArrayList<MessageConsumer>();
		//候选者条目列表
		List<ConsumerTreeItem> candidates = new ArrayList<ConsumerTreeItem>();
		candidates.add(rootItem);
		
		String topics[] = topic.split("[/]");
		for(String topicName : topics) {
			topicName = topicName.trim();
			if(!"".equals(topicName)) {
				//过滤上一次遗留的
				
				List<ConsumerTreeItem> nextItems = new ArrayList<ConsumerTreeItem>();
				for(int i=0; i<candidates.size(); i++) {
					ConsumerTreeItem candidate = candidates.get(i);
					if("#".equals(candidate.name)) {
						//通配符的，保留
					} else {
						candidates.remove(i);
						i--;
					}
					
					ConsumerTreeItem nextItem = null;
					ConsumerTreeItem nextAllItem = null;
					nextItem = candidate.items.get(topicName);
					nextAllItem = candidate.items.get("#");
					
					if(nextItem != null) {
						nextItems.add(nextItem);
					}
					if(nextAllItem != null) {
						nextItems.add(nextAllItem);
					}
				}
				
				for(ConsumerTreeItem item : nextItems) {
					candidates.add(item);
				}
			}
		}
		
		for(ConsumerTreeItem item : candidates) {
			consumers.addAll(item.comsumers);
		}
		
		return consumers;
	}
}
