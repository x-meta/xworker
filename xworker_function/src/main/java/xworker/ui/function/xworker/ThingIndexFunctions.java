package xworker.ui.function.xworker;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

/**
 * 事物索引相关的函数。
 * 
 * @author Administrator
 *
 */
public class ThingIndexFunctions {
	/**
	 * 创建事物注册信息。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String createRegistThingInfo(ActionContext actionContext){
		String type = (String) actionContext.get("type");
		if(type == null || "".equals(type)){
			type = "child";
		}
		
		Object thing = actionContext.get("thing");
		if(thing instanceof Thing){
			return type + "|" + ((Thing) thing).getMetadata().getPath();
		}else if(thing == null){
			throw new ActionException("thing is null");
		}else{
			return type + "|" + thing;
		}
	}
}
