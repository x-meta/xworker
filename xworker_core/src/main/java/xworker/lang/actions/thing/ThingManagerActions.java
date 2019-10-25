package xworker.lang.actions.thing;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingManagerActions {
	private static Logger logger = LoggerFactory.getLogger(ThingManagerActions.class);
	
	public static void initThingManager(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		File path = null;
		Object pathObj = self.doAction("getFilePath", actionContext);
		if(pathObj instanceof String){
			path = new File((String) pathObj);
		}else if(pathObj instanceof File){
			path = (File) pathObj;
		}else{
			logger.warn("InitThingManager filePath is null, filePath=" + pathObj + ", path=" + self.getMetadata().getPath());
			return;
		}
		
		World.getInstance().initThingManager(path);
	}
	
	public static Object getFilePath(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getString("filePath");
	}
}
