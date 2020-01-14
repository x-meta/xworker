package xworker.command.interactive;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;

/**
 * 交互监听器，相当于交互的服务器，交互者可以通过监听器向监听器发送信息。
 * 
 * @author zyx
 *
 */
public class InteractiveListener implements DisposeListener{
	private static final String TAG = InteractiveListener.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	/** 正在监听的UI列表 */
	List<InteractiveUI> uis = new ArrayList<InteractiveUI>();
	boolean disposed = false;
		
	public InteractiveListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		//添加DisposeListener，及时销毁自己，InteractiveListener通常是绑定到控件上的
		Control parent = actionContext.getObject("parent");
		if(parent != null) {
			parent.addDisposeListener(this);
		}
		
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
	 * @deprecated
	 */
	public void accept(InteractiveUI ui, Object message){
		thing.doAction("accept", actionContext, "ui", ui, "message", message);
	}
	
	/**
	 * 向所有已监听的UI发送一个消息。
	 * @param message
	 */
	public void sendMessage(Object message) {
		for(InteractiveUI ui : uis) {
			ui.receiveMessage(this, message);
		}
	}
	
	/**
	 * 向指定的InteractiveUI发送消息。
	 * 
	 * @param ui
	 * @param message
	 */
	public void sendMessage(InteractiveUI ui, Object message) {
		if(ui != null) {
			ui.receiveMessage(this, message);
		}
	}
	
	/**
	 * 接收从InteractiveUI发送过来的消息。
	 * 
	 * @param ui
	 * @param message
	 */
	public void receiveMessage(InteractiveUI ui, Object message) {
		thing.doAction("receiveMessage", actionContext, "ui", ui, "message", message);
	}
	
	/**
	 * 当连接到UI时被UI触发的事件。
	 * 
	 * @param ui
	 */
	public void connected(InteractiveUI ui){
		if(uis.contains(ui) == false) {
			//把自己添加到监听中
			ui.addListener(this);
			
			thing.doAction("connected", actionContext, "ui", ui);
		}
	}
	
	/**
	 * 断开指定的InteractiveUI。
	 * 
	 * @param ui
	 */
	public void disconnect(InteractiveUI ui) {
		if(ui != null) {
			uis.remove(ui);
			
			ui.removeListener(this);
		}
	}
	
	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		InteractiveListener listener = new InteractiveListener(self, actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), listener);		
	}

	public boolean isDisposed() {
		return disposed;
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		disposed = true;
		try {
			for(InteractiveUI ui : uis) {
				try {
					ui.removeListener(this);
				}catch(Exception ee) {				
				}
			}
		}catch(Exception t) {
			Executor.warn(TAG, "remove uis error", t);
		}
	}

	/**
	 * 返回已监听的所有InteractiveUI列表。
	 * 
	 * @return
	 */
	public List<InteractiveUI> getUis() {
		return uis;
	}
	
	
}
