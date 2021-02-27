package xworker.lang.context;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

public class ContextUtil {
	/**
	 * 通过事物路径获取事物，一般在动作上下文中使用。
	 * <ul>
	 * <li><strong>self.xxxx</strong>
	 * 从self变量中取xxxx属性。</li>
	 * <li><strong>var:xxxx</strong>
	 * 从ActionContext中去xxxx变量。</li>
	 * <li><strong>ognl:xxxx</strong>
	 * 使用Ognl表达式xxxx获取变量。</li>
     * </ul>
	 * 
	 * @param thingPath
	 * @param acContext
	 * @return
	 */
	public static Thing getThing(String thingPath, ActionContext acContext){
		World world = World.getInstance();
		Object dataSourceThing = null;
		if(thingPath.startsWith("self.")){
		    thingPath = thingPath.substring(5, thingPath.length());
		    thingPath = ((Thing) acContext.get("self")).getString(thingPath);
		}

		if(thingPath.startsWith("var:")){
		    thingPath = thingPath.substring(4, thingPath.length());
		    dataSourceThing = acContext.get(thingPath);
		    if(dataSourceThing instanceof String){
		        dataSourceThing = world.getThing((String) dataSourceThing);
		    }
		}
		
		if(dataSourceThing == null && thingPath.startsWith("ognl:")){
			thingPath = thingPath.substring(5, thingPath.length());
			dataSourceThing = OgnlUtil.getValue(thingPath, acContext);
		    if(dataSourceThing instanceof String){
		        dataSourceThing = world.getThing((String) dataSourceThing);
		    }
		}

		if(dataSourceThing == null){
		    dataSourceThing = world.getThing(thingPath);
		}
		
				
		return (Thing) dataSourceThing;
	}
	
	/**
	 * 当前变量上下文是上下文的变量上下文，获取主变量上下文。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static ActionContext getActionContext(ActionContext actionContext){
		return (ActionContext) actionContext.get(Action.str_acContext);
	}
	
	/**
	 * 返回正在执行的动作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Action getAction(ActionContext actionContext){
		return (Action) actionContext.get(Action.str_action);
	}
	
	/**
	 * 返回正在执行的事物。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing getActionThing(ActionContext actionContext){
		return (Thing) actionContext.get(Action.str_actionThing);
	}
}
