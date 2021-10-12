package xworker.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class ThingUtilsImpl implements IThingUtils{
	private static final String TAG = ThingUtilsImpl.class.getName();

	public void searchRegistThings(List<Thing> thingList, Map<String, Thing> context, Thing registorThing, String keyStr, String registType, boolean noDescriptor, boolean parent, ActionContext actionContext){
		World world = World.getInstance();
		Thing registByThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		List<DataObject> results = null;
		if(registByThing != null) {
			try {
				results = registByThing.doAction("query", actionContext, UtilMap.toMap(
						"thing", registorThing, "keywords", keyStr, "registType", registType,
						"noDescriptor", noDescriptor, "parent", parent));
			}catch(Exception e) {
				Executor.warn(TAG, "query index form db exception", e);
				//return thingList;
				results = Collections.emptyList();
			}
		}else {
			results = Collections.emptyList();
		}

		//过滤重复事物的上下文

		for(DataObject obj : results){
			String path = (String) obj.get("path");
			Thing thing = world.getThing(path);
			if(thing != null){
				if(thing.getBoolean("th_registDisabled")){
					continue;
				}

				//用户自定义分组
				thing.getMetadata().setUserGroup(obj.getString("userGroup"));
				if(context.get(path) != null){
					continue;
				}else{
					context.put(path, thing);
				}

				//是否注册是子事物，即把自己注册到某一个事物，其实是要注册自己的所有子事物到被注册的事物下
				if(!ThingRegistUtils.isDisableRegistMyChilds() && thing.getBoolean("th_registMyChilds")){
					ThingRegistUtils.addThingsToRegistList(thing.getChilds(), context, thingList);
				}else{
					String th_registActionChilds = thing.getStringBlankAsNull("th_registActionChilds");
					if(th_registActionChilds == null) {
						thingList.add(thing);
					}else {
						try {
							List<Thing> childs = thing.doAction(th_registActionChilds, actionContext);
							ThingRegistUtils.addThingsToRegistList(childs, context, thingList);
						}catch(Exception e) {
							Executor.warn(TAG, "Add action childs error, thing=" + thing.getMetadata().getPath(), e);
						}
					}
				}
			}
		}
	}
}
