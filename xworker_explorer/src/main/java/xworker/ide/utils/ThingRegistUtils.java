package xworker.ide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;

public class ThingRegistUtils {
	/**
	 * 获取指定parent的所有注册，包括ThingIndex下的子事物，但不包括ThingIndex。
	 * 
	 * @param parentThing
	 * @param registType
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getAllRegists(String parentThing, String registType, ActionContext actionContext){
		List<Thing> childs = new ArrayList<Thing>();
		Map<String, String> context = new HashMap<String, String>();
		
		initAllRegists(childs, context, parentThing, registType, actionContext);
		return childs;
	}
	
	@SuppressWarnings("unchecked")
	private static void initAllRegists(List<Thing> childs, Map<String, String> context, String parentThing, String registType, ActionContext actionContext){
		//查询注册的子事物
		World world = World.getInstance();
		Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		List<DataObject> rchilds = (List<DataObject>) registThing.doAction(
				"query",
				actionContext,
				UtilMap.toMap(new Object[] { "thing", parentThing,
						"noDescriptor", true, "registType", registType }));
		for (DataObject rc : rchilds) {
			Thing child = world.getThing((String) rc.get("path"));
			if (child != null) {
				if(child.getBoolean("registMyChilds")){
					for(Thing cc : child.getChilds()){
						addToRegistList(childs, context, cc, registType, actionContext);
					}
				}else{
					addToRegistList(childs, context, child, registType, actionContext);
				}
			}
		}
	}
	
	private static void addToRegistList(List<Thing> childs, Map<String, String> context, Thing child, String registType, ActionContext actionContext ){
		String path = child.getMetadata().getPath();
		if(context.get(path) != null){
			return;
		}
		
		if(child.getThingName().equals("ThingIndex")){
			initAllRegists(childs, context, path, registType, actionContext);
		}else{
			childs.add(child);
			context.put(path, path);
		}
	}
}
