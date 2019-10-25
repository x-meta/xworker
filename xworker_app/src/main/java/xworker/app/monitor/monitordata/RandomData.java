package xworker.app.monitor.monitordata;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.monitor.MonitorDataSaveTask;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;

public class RandomData {
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		//DataObject monitorTaskTask = (DataObject) actionContext.get("monitorTaskTask");
		List<DataObject> resources = (List<DataObject>) actionContext.get("resources");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		
		Random random = new Random();
		for(DataObject resource : resources){
			String dataNames = resource.getString("param1");
			double minValue = resource.getDouble("param2");
			double maxValue = resource.getDouble("param3");
			boolean toInt = resource.getBoolean("param4");
			
			for(String dataName : dataNames.split("[,]")){
				double v = random.nextDouble();
				v = (maxValue - minValue) * v + minValue;
				
				DataObject data = new DataObject("xworker.app.monitor.dataobjects.MonitorData");
				data.put("taskThingPath", self.getMetadata().getPath());
				data.put("resourceId", resource.getString("resId"));
				data.put("dataName", dataName);
				if(toInt){
					long lv = (long) v;
					data.put("value", String.valueOf(lv));
				}else{
					data.put("value", String.valueOf(v));
				}
				data.put("grabStartTime", monitorContext.getStartTime());
				data.put("grabEndTime", new Date());
				data.put("monitorId", monitorContext.monitor.get("id"));
				data.put("monitorTaskId", monitorTask.get("id"));
				data.put("monitorTaskResId", resource.get("id"));
				
				MonitorDataSaveTask.addCreateData(data);
			}
		}
	}
}
