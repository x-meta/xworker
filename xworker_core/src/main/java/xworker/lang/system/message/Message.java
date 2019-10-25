package xworker.lang.system.message;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.UtilData;

/**
 * 系统里各功能模块之间传递的消息，传递的是事物模型。
 * 
 * @author zyx
 *
 */
public class Message {
	/** 消息事物，也是消息的描述 */
	Thing thing;
	
	/** 创建消息时的变量上下文 */
	ActionContext messageContext = null;
	
	/** 一般是SWT下Message自己的变量上下文 */
	ActionContext actionContext = null;
	
	/** 消息的创建时间 */
	long time = System.currentTimeMillis();
	
	/** 消息所携带的内容 */
	Object content;
	
	/** 消息的标识 */
	String id;
	
	/** 消息主题 */
	String topic;
	
	/** 和消息一起转移的变量 */
	Map<String, Object> variables = null;
	
	Thing quickFunction;
	
	/** 消息的级别，一般是紧急程度 */
	String level;
	
	public Message(Thing thing, ActionContext actionContext) {
		this.thing = thing;
				
		//变量上下文
		if(UtilData.isTrue(thing.doAction("isIncludeActionContext", actionContext))) {
			this.messageContext = actionContext;
		}
		
		//类型和内容
		content = thing.doAction("getContent", actionContext);
		topic = thing.doAction("getTopic", actionContext);
		
		id = thing.doAction("getMessageId", actionContext);
		variables = thing.doAction("getVariables", actionContext);
		quickFunction = thing.doAction("getQuickFunction", actionContext);
		level = thing.doAction("getLevel", actionContext);
		
		if(variables == null) {
			variables = new HashMap<String, Object>();
		}
	}

	/**
	 * 获取消息的定义。
	 * 
	 * @return
	 */
	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return messageContext;
	}

	public long getTime() {
		return time;
	}
	
	public void putVariable(String key , Object value) {
		variables.put(key, value);
	}
	
	public Map<String, Object> getVariables(){
		return variables;
	}
	
	/**
	 * 在SWT下执行创建展示界面。
	 * 
	 * @param parentContext
	 */
	public Object create(ActionContext parentContext) {
		if(actionContext == null) {
			actionContext = new ActionContext();
			actionContext.putAll(variables);
			actionContext.put("message", this);
			actionContext.put("messageContext", messageContext);			
		}
		
		if(quickFunction == null){
			//如果没有内容显示消息事物的文档
		    Thing thing = World.getInstance().getThing("xworker.swt.xworker.ThingRegistThing/@composite/@simpleBrowser");
		    
		    actionContext.peek().put("parent", parentContext.get("parent"));
		    Object browser = thing.doAction("create", actionContext);
		    
		    this.thing.doAction("setUrl", actionContext, "browser", browser, "thing", this.thing);
		    return browser;
		}else{
			Bindings bindings = actionContext.push();
			try {
				bindings.put("parent", parentContext.get("parent"));
				bindings.put("parentContext", parentContext);	
			    return quickFunction.doAction("create", actionContext);
			}finally {
				actionContext.pop();
			}
		}
	}
	
	/**
	 * 当作动作来执行。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Object execute(ActionContext actionContext) {
		if(actionContext == null) {
			actionContext = new ActionContext();				
		}
		
		//如果没有内容显示一个空白
		if(quickFunction == null){
			return null;
		}else{
			Bindings bindings = actionContext.peek();
			try {
				bindings.putAll(variables);
				bindings.put("message", this);
				bindings.put("messageContext", messageContext);
			    return quickFunction.doAction("execute", actionContext);
			}finally {
				actionContext.pop();
			}
		}	
	}
		
	public ActionContext getMessageContext() {
		return messageContext;
	}

	public Object getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public String getTopic() {
		return topic;
	}

	public static Message createMessage(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new Message(self, actionContext);
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}	 
}
