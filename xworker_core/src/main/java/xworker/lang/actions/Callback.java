package xworker.lang.actions;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 动作回调。
 * 
 * @author zyx
 *
 */
public class Callback {
	Thing thing;
	ActionContext actionContext;
	
	public Callback(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public Callback(ActionContainer ac, String name){
		this.thing = ac.getActionThing(name);
		this.actionContext = ac.getActionContext();
	}
	
	public Object call(Map<String, Object> parameters){
		return thing.getAction().run(actionContext, parameters);
	}
	
	public Object call(Object ... objects){
		return call(UtilMap.toMap(objects));
	}
	
	public Object call(){
		return thing.getAction().run(actionContext);
	}
}
