package xworker.cache.object;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 对象的上下文。
 * 
 * @author Administrator
 *
 */
public class ObjectContext {
	/** 对象实例 */
	Object object;
	
	/** 生成对象的事物 */
	Thing thing;
	
	/** 事物在生成对象时的变量上下文 */
	ActionContext actionContext;
	
	public ObjectContext(Thing thing, Object object, ActionContext actionContext){
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
	public Object doAction(String name){
		return thing.doAction(name, actionContext, UtilMap.toMap(new Object[]{"object", object}));
	}

	/**
	 * 执行动作，使用传入的参数，其中对象实例会以object变量传入。
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	public Object doAction(String name, Map<String, Object> params){
		Map<String, Object> p = new HashMap<String, Object>();
		if(params != null){
			p.putAll(params);
		}
		p.put("object", object);
		
		return thing.doAction(name, actionContext, p);
	}	
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
}
