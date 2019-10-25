package xworker.app.monitor.liunx;

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

public class LinuxCheckTaskActions {
	private static Logger logger = LoggerFactory.getLogger(LinuxCheckTaskActions.class);
	
	@SuppressWarnings("unchecked")
	public static void checkTaskEatch(ActionContext actionContext){
		try{
			DataObject monitor = (DataObject) actionContext.get("monitor");
			DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
			DataObject monitorTaskTask = (DataObject) actionContext.get("monitorTaskTask");
			List<DataObject> resources = (List<DataObject>) actionContext.get("resources");
			Thing taskThing = (Thing) actionContext.get("taskThing");
			MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
			
			for(DataObject monitorTaskResource : resources){
				Object rs = MonitorUtils.loadResource(monitorTaskResource, actionContext);
				Object server = null;
				if(rs instanceof DataObject){
					server = ((DataObject) rs).doAction("getServer", actionContext);
				}else{
					server = rs;
				}
				
				/*
				if(server == null){
					server = new DataObject("xworker.app.server.dataobjects.Server");
					((DataObject) server).put("id", monitorTaskResource.getString("resId"));
					server = ((DataObject) server).load(actionContext);
				}*/
				
				if(server != null){
					LinuxTask dbTask = new LinuxTask(monitorContext, actionContext, monitor, monitorTask, monitorTaskTask, monitorTaskResource, server, taskThing);
					dbTask.paramBindings = (Bindings) actionContext.get("paramBindings");
					MonitorUtils.execute(dbTask);
				}else{
					monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("resName"), "Server db data not exists");
				}
			}			
		}catch(Exception e){
			logger.error("linux task error", e);
		}
	}
	
	public static String getCommand(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		String command = self.getStringBlankAsNull("command");
		if(command != null){
			command = UtilTemplate.processString(actionContext, command);
		}
		
		return command;
	}
	
	public static String TaskGetCommand(ActionContext actionContext) throws IOException, TemplateException{
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		String command = monitorTask.getString("param1");
		if(command != null){
			command = UtilTemplate.processString(actionContext, command);
		}
		return command;
	}
	
	public static String doCheck(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		//Thing self = (Thing) actionContext.get("self");
		//DataObject server = (DataObject) actionContext.get("server");
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		//MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		String result = (String) actionContext.get("result");
		
		String message = monitorTask.getString("param2");
		String splitStr = monitorTask.getString("param3");
		String lineSplitStr = monitorTask.getString("param4");
		String ognlExp = monitorTask.getString("param5");
		String ognlMessage = monitorTask.getString("param6");
		String dataNames = monitorTask.getString("param7");
		boolean saveMontiorData = monitorTask.getBoolean("param8");
		boolean multiLine = monitorTask.getBoolean("param9");
		int dataIdIndex = monitorTask.getInt("param10");
		
		//处理信息以及子任务等
		MonitorUtils.handleStringResult(result, splitStr, lineSplitStr, dataNames, saveMontiorData, 
				multiLine, dataIdIndex, monitorTaskResource.getString("resName"), actionContext);
		
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
	
	/**
	 * LinuxTask的doCheck方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws OgnlException
	 */
	public static Object linuxTask(ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		//DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		//MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		String result = (String) actionContext.get("result");
		
		String message = monitorTask.getString("param2");
		String splitStr = monitorTask.getString("param3");
		String lineSplitStr = monitorTask.getString("param4");
		String ognlExp = monitorTask.getString("param5");
		String ognlMessage = monitorTask.getString("param6");
		String dataNames = monitorTask.getString("param7");
		boolean saveMontiorData = monitorTask.getBoolean("param8");
		boolean multiLine = monitorTask.getBoolean("param9");
		int dataIdIndex = monitorTask.getInt("param10");
		
		//处理信息以及子任务等
		MonitorUtils.handleStringResult(result, splitStr, lineSplitStr, dataNames, saveMontiorData, 
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
	
	public static Object loadResource(ActionContext actionContext){
		DataObject resource = (DataObject) actionContext.get("resource");
		if(resource != null){
			DataObject server = new DataObject("xworker.app.server.dataobjects.Server");
			server.put("id", resource.getString("resId"));
			server = server.load(actionContext);
			return server;
		}
		
		return null;
	}
}
