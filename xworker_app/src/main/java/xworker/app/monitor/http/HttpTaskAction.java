package xworker.app.monitor.http;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.app.monitor.MonitorUtils;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.util.UtilTemplate;

public class HttpTaskAction {
	private static Logger logger = LoggerFactory.getLogger(HttpTaskAction.class);
	
	@SuppressWarnings("unchecked")
	public static void run(ActionContext actionContext) throws IOException, TemplateException{
		try{
			DataObject monitor = (DataObject) actionContext.get("monitor");
			DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
			DataObject monitorTaskTask = (DataObject) actionContext.get("monitorTaskTask");
			List<DataObject> resources = (List<DataObject>) actionContext.get("resources");
			Thing taskThing = (Thing) actionContext.get("taskThing");
			MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
			
			for(DataObject monitorTaskResource : resources){
     			HttpTask httpTask = new HttpTask(monitorContext, actionContext, monitor, monitorTask, monitorTaskTask, monitorTaskResource, taskThing);
     			httpTask.paramBindings = (Bindings) actionContext.get("paramBindings");
				MonitorUtils.execute(httpTask);
			}			
		}catch(Exception e){
			logger.error("http task error", e);
		}
	}
	
	public static Object doCheck(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		//Thing self = (Thing) actionContext.get("self");
		
		//DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		//String url = monitorTaskResource.getString("param1");
		String message = monitorTaskResource.getString("param2");
		String splitStr = monitorTaskResource.getString("param3");
		String lineSplitStr = monitorTaskResource.getString("param4");
		String ognlExp = monitorTaskResource.getString("param5");
		String ognlMessage = monitorTaskResource.getString("param6");
		String dataNames = monitorTaskResource.getString("param7");
		boolean saveMontiorData = monitorTaskResource.getBoolean("param8");
		boolean multiLine = monitorTaskResource.getBoolean("param9");
		int dataIdIndex = monitorTaskResource.getInt("param10");
		
		String content = (String) actionContext.get("content");
		
		//处理信息以及子任务等
		MonitorUtils.handleStringResult(content, splitStr, lineSplitStr, dataNames, saveMontiorData, 
				multiLine, dataIdIndex, null, actionContext);
		
		//消息模板
		if(message != null && !"".equals(message)){
			message = UtilTemplate.processString(actionContext, message);
		}		
		
		//ognl表达式
		if((message == null || "".equals(message)) && ognlExp != null && !"".equals(ognlExp) && ognlMessage != null && !"".equals(ognlMessage)){
			if(UtilData.getBoolean(OgnlUtil.getValue(ognlExp, actionContext), false)){
				message = UtilTemplate.processString(actionContext, ognlMessage);
			}
		}
		
		if("".equals(message)){
			message = null;
		}
		
		return message;
	}
	
	public static String getUrl(ActionContext actionContext){
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		return monitorTaskResource.getString("param1");
	}
	
	public static void main(String args[]){
		try{
			System.out.println("DOK|OK|OK|2875|2014-12-09 19:10:18".matches("[^OK\\|OK\\|OK.*]"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
