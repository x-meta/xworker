package xworker.command.interactive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;

/**
 * 交互接口，是交互的客户端，向监听器发送信息。
 * 
 * @author zyx
 *
 */
public class InteractiveUI {
	public static final String DATA_KEY = "__interactiveUI__";
	Thing thing;
	
	ActionContext actionContext;
	
	InteractiveListener listener;
	
	/** 初始化的一些属性 */
	Map<String, Object> attribues = null;
	
	Shell shell;
	
	Control control;
	
	boolean enabled = true;
	
	public InteractiveUI(Thing thing, Map<String, Object> attributes, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		this.attribues = attributes;
		
		control = actionContext.getObject("parent");
		shell = control.getShell();		
	}
	
	public Control getControl(){
		return control;
	}
	
	/**
	 * 按照控件的父子关系获取父UI列表。
	 * 
	 * @return
	 */
	public List<InteractiveUI> getParentUis(){
		List<InteractiveUI> list = new ArrayList<InteractiveUI>();
		Control parent = control.getParent();
		while(parent != null){
			InteractiveUI ui = getUI(parent);
			if(ui != null){
				list.add(0, ui);				
			}
			
			parent = parent.getParent();
		}
		
		return list;
	}
	
	public static InteractiveUI getUI(Control control){
		return (InteractiveUI) control.getData(InteractiveUI.DATA_KEY);
	}
	
	public String getListenerName(){
		return thing.getStringBlankAsNull("listenerName");
	}
	
	public ActionContext getActionContext(){
		return actionContext;
	}
	
	public Thing getThing(){
		return thing;
	}
	
	public void setThing(Thing thing){
		this.thing = thing;
	}
	
	/**
	 * 连接交互服务端，如果存在的话。
	 */
	public void connect(){
		InteractiveListener interactiveListener = Designer.getInteractiveListener(getListenerName());
		if(interactiveListener != null){
			interactiveListener.connected(this);
			this.setInteractiveListener(interactiveListener);						//ui.addListener();
		}
	}
	
	/**
	 * 返回UI所在的Shell的事物路径，为避免监听器监听自己。
	 * 
	 * @return
	 */
	public String getShellPath(){
		Composite parent = shell;
		while(parent.getParent() != null){
			parent = parent.getParent();
		}
		return  Designer.getControlThingPath(parent);
	}
	
	/**
	 * UI的Shell和指定的Shell是否是同一个。有的时候不能和自己交互，有时候Listener使用该方法判断是不是自己。
	 * 
	 * @param ashell
	 * @return
	 */
	public boolean isSameShell(Shell ashell){
		if(ashell == shell){
			return true;
		}
		
		Composite parent = shell;
		while(parent.getParent() != null){
			parent = parent.getParent();
			
			if(parent == ashell){
				return true;
			}
		}
		
		return false;
	}
	
	public String getCommandIndex(){
		return (String) thing.doAction("getCommandIndex", actionContext);
	}
	
	public Thing getRegistThing(){
		return thing.doAction("getRegistThing", actionContext);
	}
	
	public String getRegistType(){
		return thing.doAction("getRegistType", actionContext);
	}
	
	public Object doAction(String name, Map<String, Object> parameters){
		return thing.doAction(name, actionContext, parameters);
	}
	
	public Object doAction(String name){
		return thing.doAction(name, actionContext);
	}
	
	public void setInteractiveListener(InteractiveListener listener){
		this.listener = listener;
	}
	
	/**
	 * 从属性或变量上下文中获取指定的对象。
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key){
		if(attribues != null){
			Object obj = attribues.get(key);
			if(obj != null){
				return obj;
			}
		}
		
		return actionContext.get(key);
	}

	/**
	 * 交互者接受传入的信息。
	 * 
	 * @param message
	 */
	public void accept(Object message){		
		thing.doAction("accept", actionContext, "message", message);
	}
	
	/**
	 * 交互者向监听器发送信息。
	 * 
	 * @param message
	 */
	public void fire(Object message){
		if(listener != null){
			listener.accept(this, message);
		}
	}
	
	public static void create(ActionContext actionContext){
		Control parent = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		
		//保留的属性
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		String attributesStr = self.getStringBlankAsNull("attributes");
		if(attributesStr != null){
			for(String attr : attributesStr.split("[,]")){
				attr = attr.trim();
				if(!"".equals(attr)){
					attributes.put(attr, actionContext.get(attr));
				}
			}
		}
		
		//交互者
		InteractiveUI ui = new  InteractiveUI(self, attributes, actionContext);
		if(parent != null){
			parent.setData(DATA_KEY, ui);
		}
		
		//保存变量
		actionContext.getScope(0).put(self.getMetadata().getName(), ui);		
	}
	
	public static String getCommandIndex(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return self.getStringBlankAsNull("commandIndex");
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
