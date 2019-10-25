package xworker.app.monitor.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.liunx.LinuxTask;
import xworker.app.monitor.res.MonitableTask;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.util.monitor.http.HttpRequest;
import xworker.util.monitor.http.HttpResult;

public class HttpTask extends MonitableTask implements Runnable{		
	private static Logger logger = LoggerFactory.getLogger(LinuxTask.class);
	
	DataObject monitorTaskResource;
	Thing taskThing;
	
	public HttpTask(MonitorContext message, ActionContext actionContext, DataObject monitor, DataObject monitorTask, DataObject monitorTaskTask, DataObject monitorTaskResource, Thing taskThing){
		super(message, monitorTask, monitorTaskTask, monitorTaskResource, actionContext);
		
		this.monitorTaskResource = monitorTaskResource;
		this.taskThing = taskThing;
	}
		
	public void doTask(){
		Thing doCheckThing = taskThing;
		String param1 = monitorTask.getString("param1");
		if(param1 != null){
			Thing tmp = World.getInstance().getThing(param1);
			if(tmp != null){
				doCheckThing = tmp;
			}
		}
		
		try{
			long start = System.currentTimeMillis();
			String url = (String) taskThing.doAction("getUrl", actionContext, UtilMap.toMap("monitorTaskResource", monitorTaskResource));
			HttpResult result = HttpRequest.request(url);
			String message = null;
			long time = System.currentTimeMillis() - start;
			
			if(result.statusCode != 200){				
			//	monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("resName"), "Http status code " + result.statusCode + ", Time: " + time + ", content=" + result.content);  
			//}else{
				actionContext.peek().put("statusCode", result.statusCode);
				actionContext.peek().put("exception", null);
				
				message = (String) doCheckThing.doAction("doCheck", actionContext, 
						UtilMap.toMap("content", result.content,
								"result", result,
								"monitorTaskResource", monitorTaskResource,
								"monitorTask", monitorTask, 
								"taskThing", taskThing,
								"monitorContext", monitorContext,
								"monitor", monitor,
								"time", time));
			}

			if(message != null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("param1"), message);
			}
		}catch(Exception e){			
			logger.error("Execute http task error," +
					"monitorid=" + monitor.getString("id") + ", monitorname=" + monitor.getString("monitor") +
					",taskid=" + monitorTask.get("id") + ",taskname=" + monitorTask.getString("name"), e);
			monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("param1"), ExceptionUtil.getRootMessage(e));
			
			actionContext.peek().put("exception", e);
			String message = (String) doCheckThing.doAction("doCheck", actionContext, 
					UtilMap.toMap("content", null,
							"result", null,
							"monitorTaskResource", monitorTaskResource,
							"monitorTask", monitorTask, 
							"taskThing", taskThing,
							"monitorContext", monitorContext,
							"monitor", monitor,
							"time", 0));
			
			if(message != null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("param1"), message);
			}			
		}
	}
}
