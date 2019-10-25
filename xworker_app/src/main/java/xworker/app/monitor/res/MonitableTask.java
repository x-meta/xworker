package xworker.app.monitor.res;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import freemarker.template.TemplateException;
import ognl.OgnlException;
import xworker.app.monitor.MonitorDataSaveTask;
import xworker.app.monitor.MonitorUtils;
import xworker.dataObject.DataObject;
import xworker.util.UtilTemplate;

public abstract class MonitableTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(MonitableTask.class);
	
	/** 总监控定时任务 */
	public DataObject monitor;
	/** 执行开始时间 */	
	public long startTime;
	/** 执行结束时间 */
	public long endTime;
	/** 是否已经结束 */
	public boolean finished = false;
	/** 变量上下文 */
	public ActionContext actionContext;
	/** 监控上下文 */
	public MonitorContext monitorContext;
	/** 监控任务的任务定义 */
	public DataObject monitorTaskTask;
	/** 监控任务 */
	public DataObject monitorTask;
	/** 参数绑定 */
	public Bindings paramBindings;
	/** 监控任务资源，单一时 */
	public DataObject monitorTaskResource;
	/** 监控任务资源，多个时 */
	public List<DataObject> monitorTaskResources;
	/** 任务事物 */
	public Thing taskThing;
	private DataObject logData = null;
	
	public MonitableTask(MonitorContext monitorContext, DataObject monitorTask, DataObject monitorTaskTask, DataObject monitorTaskResource, ActionContext actionContext){
		this(monitorContext, monitorTask, monitorTaskTask, null, monitorTaskResource, actionContext);
	}
	
	public MonitableTask(MonitorContext monitorContext, DataObject monitorTask, DataObject monitorTaskTask, List<DataObject> monitorTaskResources, ActionContext actionContext){
		this(monitorContext, monitorTask, monitorTaskTask, monitorTaskResources, null, actionContext);
	}
	
	public MonitableTask(MonitorContext monitorContext, DataObject monitorTask, DataObject monitorTaskTask, List<DataObject> monitorTaskResources, DataObject monitorTaskResource, ActionContext actionContext){
		this.monitorContext = monitorContext;
		this.actionContext = actionContext;
		this.monitorTask = monitorTask;
		this.monitorTaskTask = monitorTaskTask;
		this.monitor = this.monitorContext.monitor;
		this.monitorTaskResource = monitorTaskResource;
		this.monitorTaskResources = monitorTaskResources;		
		this.taskThing = World.getInstance().getThing(monitorTaskTask.getString("taskPath"));
		
		this.monitorContext.tasks.add(this);		
	}
	
	public boolean isFinished(){
		return finished;
	}
	
	public long getExecuteTime(){
		if(finished){
			return endTime - startTime;
		}else{
			return System.currentTimeMillis() - startTime;
		}
	}
	
	/**
	 * 执行任务。
	 */
	public abstract void doTask();
	
	public void run(){
		boolean success = false;
		try{
			//首先创建日志
			startTime = System.currentTimeMillis();
			logData = new DataObject("xworker.app.monitor.dataobjects.ResMonitorTaskLog");
			logData.put("monitorId", monitor.get("id"));
			logData.put("monitorTaskId", monitorTask.get("id"));
			if(monitorTaskResource != null){
				logData.put("taskResourceId", monitorTaskResource.get("id"));
				logData.put("resourceId", monitorTaskResource.get("resId"));
			}
			logData.put("monitorStartTime", monitorContext.getStartTime());
			logData.put("startTime", new Date(startTime));			
			logData.put("status", 0);
			logData.create(actionContext);
			
			if(paramBindings != null){
				actionContext.push(paramBindings);
				paramBindings.put("paramBindings", paramBindings);
			}
												
			doTask();			
			
			success = true;
		}catch(Exception e){			
			logger.error("Execute monitorable task error," +
					"monitorid=" + monitor.getString("id") + ", monitorname=" + monitor.getString("monitor") +
					",taskid=" + monitorTask.get("id") + ",taskname=" + monitorTask.getString("name"), e);
			monitorContext.appendContent(monitorTask, null, null, ExceptionUtil.getRootMessage(e));
		}finally{
			if(paramBindings != null){
				actionContext.pop();
			}
			
			endTime = System.currentTimeMillis();
			finished = true;
			
			try{
			   //检查是否都已经结束了			
				monitorContext.isFinished();
			
				logData.put("endTime", new Date(endTime));
				if(success){
					logData.put("status", 1);
				}else{
					logData.put("status", 2);
				}
				MonitorDataSaveTask.addUpdateData(logData);
			}catch(Exception e){
				logger.error("monitable task finally check finish error", e);
			}
		}
	}
	
	/**
	 * 添加要通过邮件发送的信息。
	 * 
	 * @param resourceName
	 * @param message
	 */
	public void appendMessage(String resourceName, String message){
		if(monitorContext != null){
			monitorContext.appendContent(monitorTask, monitorTaskResource, resourceName, message);
		}
	}
	
	/**
	 * 如果使用了通用的参数配置，那么可以使用此方法处理子事物。
	 * 
	 * @param paramsThing
	 * @param result
	 * @param resourceId
	 * @param actionContext
	 * @throws OgnlException
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void handleStringResult(Thing paramsThing, String result, String resourceId, String resourcenName, ActionContext actionContext) throws OgnlException, IOException, TemplateException{
		String splitStr = paramsThing.getString("param3");
		String lineSplitStr = paramsThing.getString("param5");
		String dataNames = paramsThing.getString("param6");
		boolean saveMontiorData = paramsThing.getBoolean("param7");
		boolean multiLine = paramsThing.getBoolean("param4");
		int dataIdIndex = paramsThing.getInt("param8");
		
		//分析消息和处理消息等
		MonitorUtils.handleStringResult(result, splitStr, lineSplitStr, dataNames, saveMontiorData, multiLine, dataIdIndex, resourceId, actionContext);
		
		//处理消息
		String message = getMessage(paramsThing.getString("param1"), paramsThing.getString("param2"), actionContext);
		if(message != null && !"".equals(message)){
			this.appendMessage(resourcenName, message);
		}
	}
	
	/**
	 * 处理非字符串的消息。
	 * 
	 * @param paramsThing
	 * @param resourceId
	 * @param resourceName
	 * @param listDatas
	 * @param actionContext
	 * @throws OgnlException
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public void handleListResult(Thing paramsThing, String resourceId, String resourceName, List<Map<String, Object>> listDatas, ActionContext actionContext) throws OgnlException, IOException, TemplateException{
		boolean saveMontiorData = paramsThing.getBoolean("param4");
		String[] columnMappings = null;
		String param3 = paramsThing.getString("param3");
		if(param3 != null){
			columnMappings = param3.split("[,]");
		}
		int dataIdIndex = paramsThing.getInt("param5");
		//是否保存监控数据
		if(saveMontiorData){
			MonitorUtils.saveMonitorData(monitorContext, monitorTask, monitorTaskResource, taskThing, 
					resourceId, columnMappings, dataIdIndex, listDatas);
		}
		
		//执行子任务
		if(monitorTask.getBoolean("childSameRes")){
			MonitorUtils.executeSubResTasks(monitorTaskResource, actionContext);
		}else{
			MonitorUtils.executeSubResTasks(null, actionContext);
		}
		
		//处理消息
		String message = getMessage(paramsThing.getString("param1"), paramsThing.getString("param2"), actionContext);
		if(message != null && !"".equals(message)){
			this.appendMessage(resourceName, message);
		}
	}
	
	/**
	 * 返回消息。
	 * 
	 * @param ognlExp
	 * @param message
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws OgnlException
	 */
	public String getMessage(String ognlExp, String message, ActionContext actionContext) throws IOException, TemplateException, OgnlException{
		boolean haveMessage = false;
		if(ognlExp == null || "".equals(ognlExp)){
			haveMessage = true;			
		}else if(UtilData.getBoolean(OgnlUtil.getValue(ognlExp, actionContext), false)){
			haveMessage = true;
		}
		
		if(haveMessage && message != null && !"".equals(message)){
		    return UtilTemplate.processString(actionContext, message);
		}
		
		return null;
	}
	
	/**
	 * 保存监控数据的快捷接口。
	 * 
	 * @param resourceId
	 * @param columnMappings
	 * @param rowIdIndex
	 * @param listDatas
	 */
	public void saveMonitorData(Object resourceId, String[] columnMappings, int rowIdIndex, List<Map<String, Object>> listDatas){
		MonitorUtils.saveMonitorData(monitorContext, monitorTask, monitorTaskResource, taskThing, resourceId, columnMappings, rowIdIndex, listDatas);
	}
	
	/**
	 * 处理子任务的快捷方式。
	 * 
	 * @throws OgnlException
	 */
	public void handleChildTask() throws OgnlException{
		//执行子任务
		if(monitorTask.getBoolean("childSameRes")){
			MonitorUtils.executeSubResTasks(monitorTaskResource, actionContext);
		}else{
			MonitorUtils.executeSubResTasks(null, actionContext);
		}
	}
}