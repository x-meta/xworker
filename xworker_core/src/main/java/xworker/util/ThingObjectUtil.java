package xworker.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 一般是通过事物生成对象，且这个对象是被缓存的工具。
 * 
 * @author zyx
 *
 */
public class ThingObjectUtil {
	Thing thing;
	String createAction;
	Object object;
	long lastmodified = 0;
	String closeAction;
	
	public ThingObjectUtil(Thing thing, String createAction, String closeAction){
		this.thing = thing;
		this.createAction = createAction;
		this.closeAction = closeAction;
	}
	
	public Object getObject(ActionContext actionContext){
		if(lastmodified != thing.getMetadata().getLastModified() || object == null){			
			close(actionContext);
			
			object = thing.doAction(createAction, actionContext);
			lastmodified = thing.getMetadata().getLastModified();
		}
		
		return object;
	}
	
	public void close(ActionContext actionContext){
		if(object != null && closeAction != null){
			thing.doAction(closeAction, actionContext, "object", object);
		}
	}
	
	/**
	 * 通过事物的动作获取缓存的对象，该对象缓存到world上。如果事物发生了变化将重新创建对象。
	 * 
	 * @param thing 获取缓存的事物，也通过该事物创建对象
	 * @param createAction 创建的动作
	 * @param closeAction 关闭的动作
	 * @param actionContext 变量上下文
	 * @return 缓存的对象
	 */
	public static Object getObject(Thing thing, String createAction, String closeAction, ActionContext actionContext){
		String key = thing.getMetadata().getPath() + "__ThingObjectUtil__" + createAction;
		ThingObjectUtil util =	(ThingObjectUtil) World.getInstance().getData(key);
		
		if(util == null){
			util = new ThingObjectUtil(thing, createAction, closeAction);
			World.getInstance().setData(key, util);
		}
		
		return util.getObject(actionContext);
	}
	
	public static void removeCache(Thing thing, String actionName, ActionContext actionContext){
		String key = thing.getMetadata().getPath() + "__ThingObjectUtil__" + actionName;
		ThingObjectUtil util =	(ThingObjectUtil) World.getInstance().getData(key);
		if(util != null){
			util.close(actionContext);
		}
		
		World.getInstance().removeData(key);
	}
}
