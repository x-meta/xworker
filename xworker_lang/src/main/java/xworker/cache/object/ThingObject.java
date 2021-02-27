package xworker.cache.object;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 和模型相关的对象。
 * 
 * @author Administrator
 *
 */
public class ThingObject<T> {
	/** 对象实例 */
	T object;
	
	/** 生成对象的模型 */
	Thing thing;
	
	/** 变量上下文 */
	ActionContext actionContext;
	
	public ThingObject(Thing thing, T object, ActionContext actionContext){
		this.thing = thing;
		this.object = object;
		this.actionContext = actionContext;
	}

	/**
	 * 执行动作，其中object变量是对象实例。
	 * 
	 * @param name
	 * @return
	 */
	public <V> V doAction(String name){
		return thing.doAction(name, actionContext, UtilMap.toMap(new Object[]{"object", object}));
	}

	/**
	 * 执行动作，使用传入的参数，其中对象实例会以object变量传入。
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	public <V> V doAction(String name, Map<String, Object> params){
		Map<String, Object> p = new HashMap<String, Object>();
		if(params != null){
			p.putAll(params);
		}
		p.put("object", object);
		
		return thing.doAction(name, actionContext, p);
	}	
		
	public <V> V doAction(String name, Object ... params) {
		return thing.doExec(name, actionContext, params);
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}
	
	public <V> V get(String  key) {
		if(actionContext != null) {
			return actionContext.getObject(key);
		}
		
		return null;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
}
