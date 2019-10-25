package xworker.app.xworker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.util.ThingUtils;

public class ThingActions {
	private static Logger logger = LoggerFactory.getLogger(ThingActions.class);	
	
	/**
	 * 返回事物描述者定义的所有子节点，以及注册到描述者的所有子节点。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getDefinedChilds(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = actionContext.getObject("self");
		
		//返回描述者
		Thing descriptor = self.doAction("getDescriptor", actionContext);
		Thing currentThing = self.doAction("getThing", actionContext);
		String keyword = self.doAction("getKeyword", actionContext);
		
		List<Thing> desChilds = descriptor.getAllChilds("thing");
		//查找并添加注册的子事物
		try{
		    Thing registThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		    List<DataObject> rchilds = registThing.doAction("query", actionContext, "thing", descriptor, "noDescriptor", true, "registType", "child");
		    Map<String, Thing> context = new HashMap<String, Thing>();
		    for(DataObject rc : rchilds){
		        //log.info("child=" + rchilds);
		        Thing child = world.getThing(rc.getString("path"));        
		        if(child != null){
		            if(child.getBoolean("th_registMyChilds")){
		                for(Thing cchild : child.getChilds()){
		                    addThing(desChilds, cchild, context);
		                }
		            }else{
		                addThing(desChilds, child, context);
		            }
		        }
		    }
		    
		    ThingUtils.initFromRegistCache(desChilds, context, descriptor, "child");
		}catch(Exception e){
			logger.warn("search regist child error", e);
		}
		//log.info("Select DB Time: " + (System.currentTimeMillis() - start));
		if(keyword == null) {
			keyword = "";
		}
		keyword = keyword.toLowerCase();
		List<Thing> dess = new ArrayList<Thing>();
		for(Thing des : desChilds){
		    if(currentThing != null && des.getString("many") == "false"){
		        //只找首要描述者相同的子节点，其他情况不再过滤了
		        boolean have = false;		        
		        for(Thing child : currentThing.getChilds()){
		            if(child.getDescriptor().getMetadata().getPath().equals(des.getMetadata().getPath())){
		                have = true;
		                break;
		            }
		        }
		        if(have){
		            continue;
		        }
		    }
		    
		    //过滤关键字
		    if(keyword != ""){
		        String desKeys = des.getString("th_keywords");
		        boolean ok = false;
		        if(desKeys != null && desKeys.toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok && des.getMetadata().getPath().toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok){
		            String group = des.getString("group");
		            if(group != null && group.toLowerCase().indexOf(keyword) != -1){
		                ok = true;
		            }
		        }
		        if(!ok && des.getMetadata().getLabel().toLowerCase().indexOf(keyword) != -1){
		            ok = true;
		        }
		        if(!ok){
		            continue;
		        }
		    }
		
		    dess.add(des);
		}
		//log.info("Filter Many Time: " + (System.currentTimeMillis() - start));
		
		return dess;
	}
	
	private static void addThing(List<Thing> list, Thing thing, Map<String, Thing >context){
	    String path = thing.getMetadata().getPath();
	    if(context.get(path) != null){
	        return;
	    }
	    
	    context.put(path, thing);
	    list.add(thing);
	}
}
