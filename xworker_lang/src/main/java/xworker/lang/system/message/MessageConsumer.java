package xworker.lang.system.message;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

/**
 * 系统消息的消费者。
 * 
 * @author zyx
 *
 */
public class MessageConsumer {
	ActionContainer actions = null;	
	String actionName = null;
	Action action = null;
    Thing thing = null;
    ActionContext actionContext;
    List<String> topics;	
    
    public MessageConsumer(ActionContainer actions, String actionName) {
    	this.actions = actions;
    	this.actionName = actionName;
    }
    
    public MessageConsumer(Action action, ActionContext actionContext) {
    	this.action = action;
    	this.actionContext = actionContext;
    }
    
    public MessageConsumer(Thing thing,  ActionContext actionContext) {
    	this.thing = thing;
    	this.actionContext = actionContext;
    	this.topics = thing.doAction("getTopics", actionContext);
    }
    
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

	/**
	 * 订阅默认设置（模型初始设置）的所有主题。
	 */
	public void subscribeAll() {
		if(topics != null) {
			for(String topic : topics) {
				MessageCenter.subscribe(this, topic);
			}
		}
	}
	
	/**
	 * 取消订阅所有主题。
	 */
	public void unsubscribeAll() {
		if(topics != null) {
			for(String topic : topics) {
				MessageCenter.unsubscribe(this, topic);
			}
		}
	}
	
	/**
	 * 订阅指定的主题，并把主题添加到主题列表。
	 * 
	 * @param topic
	 */
	public void subscribe(String topic) {
		MessageCenter.subscribe(this, topic);
		
		if(topics == null) {
			topics = new ArrayList<String>();
		}
		
		if(topics.contains(topic) == false) {
			topics.add(topic);
		}
	}
	
	/**
	 *  取消订阅指定的主题，并从主题列表中移除。
	 *  
	 * @param topic
	 */
	public void unsubscribe(String topic) {
		MessageCenter.unsubscribe(this, topic);
		
		if(topics != null) {
			topics.remove(topic);
		}
	}
	
	public void handleMessage(Message message) {
		if(actions != null && actionName != null) {
			actions.doAction(actionName, actions.getActionContext(), "message", message);
		}
		
		if(action != null) {
			action.run(actionContext, "message", message);
		}
		
		if(thing != null) {
			thing.doAction("handleMessage", actionContext, "message", message);
		}
	}

	public ActionContainer getActions() {
		return actions;
	}

	public void setActions(ActionContainer actions) {
		this.actions = actions;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getActionName() {
		return actionName;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
}
