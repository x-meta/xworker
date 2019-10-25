package xworker.app.task;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class TaskActions {
	public static void init(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataObject db = new DataObject("xworker.app.task.dataobjects.ScheduledTask");
		db.put("name", self.getMetadata().getLabel());
		db.put("actionPath", self.getMetadata().getPath());
		db.put("sgroup", self.getString("group"));
		db.put("status", self.get("status"));
		db.put("delay", self.get("delay"));
		db.put("period", self.get("period"));
		db.put("timeUnit", self.get("timeUnit"));
		db.put("fixedRate", self.get("fixedRate"));
		db.put("fixTime", self.get("fixTime"));
		db.put("fixTimeStr", self.get("fixTimeStr"));
		db.put("fixTimeStr", self.get("fixTimeStr"));
		db.put("executeCount", self.get("executeCount"));
		
		DataObjectUtil.createIfNotExists(db, new String[]{"actionPath"}, actionContext);
	}
}
