package xworker.util;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 事物注册的工具类。
 * 
 * @author zyx
 *
 */
public class ThingRegistUtils {
    public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext){
    	return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, actionContext);
    }
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  ActionContext actionContext){
		return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, parent, actionContext);
	}
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  boolean noDescriptor, ActionContext actionContext){
		return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, parent, noDescriptor, actionContext);
	}
}
