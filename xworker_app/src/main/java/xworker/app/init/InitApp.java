package xworker.app.init;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.util.ThingUtils;

/**
 * 初始化APP的信息等。
 * 
 * @author bookroom
 *
 */
public class InitApp {
	private static final Logger logger = LoggerFactory.getLogger(InitApp.class);
	private static boolean isIniting = false;
	
	public static boolean isIniting(){
		return isIniting;
	}
	
    public static byte run(ActionContext actionContext){
    	synchronized(logger){
	    	if(isIniting){
	    		return 0;
	    	}
	    	
	    	isIniting = true;
    	}
    	
    	try{
	    	//获取数据库中已经初始化
	    	Thing indexThing = World.getInstance().getThing("xworker.app.init.InitIndex");
	    	initIndex(indexThing, actionContext);
	    	
	    	return 1;
    	}finally{
    		synchronized(logger){
    	    	isIniting = false;
        	}
    	}
    }
    
    public static void initIndex(Thing indexThing, ActionContext actionContext){
    	@SuppressWarnings("unchecked")
		List<Thing> things = ThingUtils.searchRegistThings(indexThing, "child", Collections.EMPTY_LIST, actionContext);
    	for(Thing thing : things){
    		if("ThingIndex".equals(thing.getThingName())){
    			//还是索引，初始化子事物
    			initIndex(thing, actionContext);
    		}else{
    			try{
    				if(thing.getBoolean("th_registMyChilds")){
    					for(Thing child : thing.getChilds()){
    						init(child, actionContext);
    					}
    				}else{
    					init(thing, actionContext);
    				}
    			}catch(Exception e){
    				logger.error("init app error, thing=" + thing.getMetadata().getPath(), e);
    			}
    		}
    	}
    }
    
    /**
     * 执行初始化操作。
     */
    public static void init(Thing initThing, ActionContext actionContext){
    	List<DataObject> initLogs = DataObjectUtil.query("xworker.app.init.dataobjects.InitLog",
    			UtilMap.toMap("initThingPath", initThing.getMetadata().getPath()), actionContext);
    	if(initLogs.size() > 0){
    		DataObject initLog = initLogs.get(0);
    		long thingLastModified = initLog.getLong("thingLastModified");
    		if(initThing.getMetadata().getLastModified() != thingLastModified){
    			doInit(initThing, actionContext, initLog);
    		}
    	}else{
    		doInit(initThing, actionContext, null);
    	}
    }
    
    public static void doInit(Thing initThing, ActionContext actionContext, DataObject initLog){
    	String result = "success";
    	try{
    		initThing.doAction("init", actionContext);    		
    	}catch(Exception e){
    		result = "failue";
    		logger.error("init app error, thing=" + initThing.getMetadata().getPath(), e);
    	}
    	
    	if(initLog != null){
    		initLog.set("thingLastModified", initThing.getMetadata().getLastModified());
    		initLog.set("status", result);
    		initLog.set("category", initThing.getString("group"));
    		initLog.set("updateTime", new Date());
    		initLog.update(actionContext);
    	}else{
    		initLog = new DataObject("xworker.app.init.dataobjects.InitLog");
    		initLog.set("thingLastModified", initThing.getMetadata().getLastModified());
    		initLog.set("status", result);
    		initLog.set("name", initThing.getMetadata().getLabel());
    		initLog.set("initThingPath", initThing.getMetadata().getPath());
    		initLog.set("updateTime", new Date());
    		initLog.set("category", initThing.getString("group"));
    		initLog.create(actionContext);
    	}
    }
}
