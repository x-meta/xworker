package xworker.command.interactive;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;

/**
 * 交互监听器，相当于交互的服务器，交互者可以通过监听器向监听器发送信息。
 * 
 * @author zyx
 *
 */
public class InteractiveListener {
	Thing thing;
	ActionContext actionContext;
	
	public InteractiveListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		String listenerName = thing.getStringBlankAsNull("listenerName");
		if(listenerName == null){
			listenerName = "";
		}
		
		Designer.registInteractiveListener(listenerName, this);
	}
	
	/**
	 * 接受交互者发送来的信息。
	 * 
	 * @param ui UI
	 * @param message 消息
	 */
	public void accept(InteractiveUI ui, Object message){
		thing.doAction("accept", actionContext, "ui", ui, "message", message);
	}
	
	/**
	 * 当连接到UI时被UI触发的事件。
	 * 
	 * @param ui
	 */
	public void connected(InteractiveUI ui){
		thing.doAction("connected", actionContext, "ui", ui);
	}
	
	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		InteractiveListener listener = new InteractiveListener(self, actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), listener);		
	}
}
