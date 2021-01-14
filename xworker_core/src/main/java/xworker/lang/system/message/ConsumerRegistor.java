package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import xworker.util.ThingRegistUtils;

/**
 * 消费者注册器，定时更新注册消费者。
 * 
 * @author zyx
 *
 */
public class ConsumerRegistor {
	/** 已注册的消费者缓存 */
	Map<String, ThingEntry> consumers = new HashMap<String, ThingEntry>();
	
	/**
	 * 更新消费者信息。
	 */
	public void update(ActionContext actionContext) {
		Thing index = World.getInstance().getThing("xworker.lang.system.message.MessageConsumerIndex");
		
		List<Thing> things = ThingRegistUtils.searchRegistThings(index, "child", null, actionContext);
		Map<String, String> context = new HashMap<String, String>(); //记录当前的
		for(Thing thing : things) {
			String path  = thing.getMetadata().getPath();
			context.put(path, path);
			ThingEntry thingEntry = consumers.get(path);
			if(thingEntry != null && thingEntry.isChanged() == false) {
				//已存在，且没有变更
				continue;
			}
			
			if(thingEntry == null) {
				//不存在，新加入的
				MessageCenter.registConsumer(thing, actionContext);
				thingEntry = new ThingEntry(thing);
				consumers.put(path, thingEntry);
			}else {
				//已变更
				MessageCenter.updateConsumer(thing, actionContext);
				thingEntry.getThing(); //刷新缓存时间
			}
		}
		
		//移除已取消注册的消息消费者
		List<String> removed = new ArrayList<String>();
		for(String key : consumers.keySet()) {
			if(context.get(key) == null) {
				removed.add(key);
			}
		}
		
		for(String path : removed) {
			consumers.remove(path);
			MessageCenter.unregistConsumer(path);			
		}
	}
	
	public static void doTask(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String key = "_ConsumerRegistor__";
		ConsumerRegistor cr = self.getStaticData(key);
		if(cr == null) {
			cr = new ConsumerRegistor();
			self.setStaticData(key, cr);
		}
		
		cr.update(actionContext);
	}
}
