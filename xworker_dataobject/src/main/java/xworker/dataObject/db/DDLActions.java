package xworker.dataObject.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class DDLActions {
	private static Logger logger = LoggerFactory.getLogger(DDLActions.class);
	
	/**
	 * 动作xworker.dataObject.db.DBDataObjectDDL的执行方法。
	 */
	public static void runDBDataObjectDDL(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		logger.info("DDL: start " + self.getMetadata().getLabel() + ", path=" + self.getMetadata().getPath());
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
						logger.info("DDL: " + dbThing.getMetadata().getPath());
						if(datasoruce != null){
							DbDataObject.mapping2ddl(dbThing, datasoruce, actionContext);					
						}else{
							dbThing.doAction("mapping2ddl", actionContext);
						}
					}else{
						logger.warn("DDL: DBDataObject not exists, path=" + dataObject);
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
