package xworker.lang.context;

import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class ActionListener {
	private static final String KEY = "__default_context__";
	Thing thing;
	ActionContext actionContext;
	ActionContext acContext;

	public ActionListener(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.actionContext.g().put(KEY, this);
		acContext = ContextUtil.getActionContext(actionContext);
	}

	/**
	 * 查看监听器是否已经存在。比如数据库的动作监听器是为了设置Connection，对于同一个数据源应该设置同一个Connection。
	 * 
	 * @param key 如果是数据库数据源，那么key一般是数据源模型
	 * @return
	 */
	public ActionListener getActionListener(Thing key) {
		List<Bindings> scopes = acContext.getScopes();
		for (int i = scopes.size() - 1; i >= 0; i--) {
			Bindings scope = scopes.get(i);
			ActionContext context = scope.getContexts().get(key);
			if(context != null) {
				Object obj = context.get(KEY);
				if(obj instanceof ActionListener) {
					return (ActionListener) obj;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 保存动作监听器，key一般是资源事物。
	 * 
	 * @param key
	 */
	public void save(Thing key) {
		ActionContext acContext = ContextUtil.getActionContext(actionContext);
		acContext.peek().getContexts().put(key, actionContext);
	}
	
	/**
	 * 保存一个变量到动作所在的变量上下文中。
	 * 
	 * @param key
	 * @param value
	 */
	public void putToAc(String key, Object value) {
		acContext.peek().put(key, value);
	}
	
	/**
	 * 保存一个变量到动作监听器中。
	 * 
	 * @param key
	 * @param value
	 */
	public void putToMe(String key, Object value) {
		actionContext.g().put(key, value);
	}
	
	public void put(String key, Object value) {
		actionContext.g().put(key, value);
	}
	
	public Object get(String key) {
		return actionContext.get(key);
	}
	
	public void putActionContext(String key, Object value) {
		acContext.peek().put(key, value);
	}
	
	public Object getActionContext(String key) {
		return acContext.get(key);
	}
	
	/**
	 * 从动作所在的变量上下文中获取一个变量。
	 * 
	 * @param key
	 * @return
	 */
	public Object getFromAc(String key) {
		return acContext.peek().get(key);
	}
	
	public Thing getActionThing() {
		return ContextUtil.getActionThing(actionContext);
	}
	
	public Action getAction() {
		return ContextUtil.getAction(actionContext);
	}
	
	/**
	 * 从动作监听器获取一个变量。
	 * 
	 * @param key
	 * @return
	 */
	public Object getFromMe(String key) {
		return actionContext.get(key);
	}
	
	public void doAction(String name) {
		thing.doAction(name, actionContext, "listener", this);
	}
	
	public static void init(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionListener context = new ActionListener(self, actionContext);
		context.doAction("doInit");
	}
	
	public static void success(ActionContext actionContext) {
		ActionListener context = actionContext.getObject(KEY);
		context.doAction("doSuccess");
	}
	
	public static void exception(ActionContext actionContext) {
		ActionListener context = actionContext.getObject(KEY);
		context.doAction("doException");
	}
}
