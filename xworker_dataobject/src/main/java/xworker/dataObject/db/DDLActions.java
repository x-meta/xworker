package xworker.dataObject.db;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

public class DDLActions {
	//private static Logger logger = LoggerFactory.getLogger(DDLActions.class);
	private static final String TAG = DDLActions.class.getName();
	
	/**
	 * 动作xworker.dataObject.db.DBDataObjectDDL的执行方法。
	 */
	public static void runDBDataObjectDDL(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Executor.info(TAG, "DDL: start " + self.getMetadata().getLabel() + ", path=" + self.getMetadata().getPath());
		//执行DDL
		String dataObjects = self.getStringBlankAsNull("dataObjects");
		Thing datasoruce = (Thing) self.doAction("getDatasource", actionContext);
		if(dataObjects != null){
			for(String str : dataObjects.split("[\n]")) {
				for(String dataObject : str.split("[,]")){
					dataObject = dataObject.trim();
					if("".equals(dataObject)){
						continue;
					}
					
					Thing dbThing = World.getInstance().getThing(dataObject);
					if(dbThing != null){
						Executor.info(TAG, "DDL: " + dbThing.getMetadata().getPath());
						if(datasoruce != null){
							DbDataObject.mapping2ddl(dbThing, datasoruce, actionContext);					
						}else{
							dbThing.doAction("mapping2ddl", actionContext);
						}
					}else{
						Executor.info(TAG, "DDL: DBDataObject not exists, path=" + dataObject);
					}
				}
			}
		}
		
		//执行参考事物的DDL
		String refDBDataObjectDDL = self.getStringBlankAsNull("refDBDataObjectDDL");
		if(refDBDataObjectDDL != null){
			Thing ddlThing = World.getInstance().getThing(refDBDataObjectDDL);
			if(ddlThing != null){
				ddlThing.doAction("run", actionContext);
			}
		}
		
		//执行子事物的 DDL
		for(Thing child : self.getChilds()){
			child.doAction("run", actionContext);
		}
	}
}
