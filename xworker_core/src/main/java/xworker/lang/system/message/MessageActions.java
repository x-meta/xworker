package xworker.lang.system.message;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MessageActions {
	public static void sendMessage(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing messageThing = self.doAction("getMessage", actionContext);
		if(messageThing == null) {
			return;
		}

		Message message = new Message(messageThing, actionContext);
		MessageCenter.publish(message);
		
	}
	
	public static Object registConsumer(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing consumer = self.doAction("getConsumer", actionContext);
		ActionContext ac = self.doAction("getActionContext", actionContext);
		if(ac == null) {
			ac = new ActionContext();
		}
		MessageCenter.registConsumer(consumer, ac);
		
		return null;
		
	}
	
	public static Object unregistConsumer(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing consumer = self.doAction("getConsumer", actionContext);
		MessageCenter.unregistConsumer(consumer);
		
		return null;
		
	}
	
	/**
	 * 消费者模型自行注册。
	 * 
	 * @param actionContext
	 */
	public static void regist(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		MessageCenter.registConsumer(self, new ActionContext());
	}
	
	/**
	 * 消费者自行取消注册。
	 * 
	 * @param actionContext
	 */
	public static void unregist(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		MessageCenter.unregistConsumer(self);
	}
	
	public static String getLabel(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return self.getMetadata().getLabel();
	}
}
