package xworker.lang.system.message;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 系统消息的消费者。
 * 
 * @author zyx
 *
 */
public class MessageConsumer {
    Thing thing;
    ActionContext actionContext;
    List<String> topics;	
    
    public MessageConsumer(Thing thing,  List<String> topics, ActionContext actionContext) {
    	this.thing = thing;
    	this.actionContext = actionContext;
    	this.topics = topics;
    }

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public List<String> getTopics() {
		return topics;
	}
	
	public void handleMessage(Message message) {
		thing.doAction("handleMessage", actionContext, "message", message);
	}
}
