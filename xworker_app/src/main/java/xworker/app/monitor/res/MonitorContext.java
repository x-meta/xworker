package xworker.app.monitor.res;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;

import xworker.app.monitor.MonitorDataSaveTask;
import xworker.app.monitor.MonitorUtils;
import xworker.dataObject.DataObject;

/**
 * 监控的上下文，主要用来检查整个监控任务是否结束，以及检测的结果等。
 * 
 * @author Administrator
 *
 */
public class MonitorContext {
	/** 邮件主题 */
	public String subject;
	/** 任务列表 */
	public List<MonitableTask> tasks = new ArrayList<MonitableTask>();
	/** 监控任务数据对象 */
	public DataObject monitor;
	/** 邮件是否已经发送 */
	public boolean mailSended = false;
	/** 开始时间 */
	private Date startTime = new Date();
	/** 日志数据 */
	private DataObject logData;
	/** 是否已经结束 */
	private boolean finished = false;
	/** 创建Log时使用的变量上下文 */
	private ActionContext createContext = new ActionContext(); 
	
	List<Content> contents = new ArrayList<Content>();
	Map<String, Content> contentCache = new HashMap<String, Content>();
	
	public MonitorContext(DataObject monitor, String subject){
		this.monitor = monitor;
		this.subject = subject;
		
		logData = new DataObject("xworker.app.monitor.dataobjects.ResMonitorLog");
		logData.put("monitorId", monitor.get("id"));
		logData.put("startTime", startTime);		
		logData.create(createContext);
	}
	
	public Date getStartTime(){
		return startTime;
	}
	
	public String getKey(DataObject task, DataObject resource, String resourceName){
		String key = "";
		if(task != null){
			key = key + task.getString("id");
		}else{
			key = key + "null";
		}
		
		if(resource != null){
			key = key + "__" + resource.getString("id");
		}else{
			key = key + "__null";
		}
		
		return key + "__" + resourceName;
	}
	
	/**
	 * 增加报警信息。
	 * 
	 * @param monitorTask
	 * @param resource
	 * @param resourceName
	 * @param text
	 */
	public void appendContent(DataObject monitorTask, DataObject resource, String resourceName, String text){
		if(text == null){
			return;
		}
		
		text = text.trim();
		if("".equals(text)){
			return;
		}
		
		String key = getKey(monitorTask, resource, resourceName);
		Content content = contentCache.get(key);
		if(content == null){
			content = new Content(monitorTask, resource, resourceName);
			contentCache.put(key, content);
			contents.add(content);
		}
		
		content.appendText(text);
	}
	
	public String getContent(){
		String str = "<table border=\"1\"><tr><td>任务标识</td><td>任务名称</td><td>资源标识</td><td>资源名称</td><td>资源信息</td></tr>";
		for(Content content : contents){
			if(!"".equals(str)){
				str = str  + content.toString();
			}else{
				str = content.toString();
			}
		}
		
		str = str + "</table>";
		return str;
	}
	
	public synchronized void sendMail(){
		if(!mailSended && contents.size() > 0){
			MonitorUtils.getScheduledExecutorService().execute(new Runnable(){
				public void run(){
					try{
						MessageSender.sendMessage(monitor, subject, getContent());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
		mailSended = true;
	}
	
	public boolean isFinished(){
		if(!finished){
			long endTime = 0;
			for(MonitableTask task : tasks){
				if(!task.isFinished()){
					return false;
				}
				
				if(endTime < task.endTime){
					endTime = task.endTime;
				}
			}
			
			//保存结束的日志
			logData.put("endTime", new Date(endTime));
			String content = getContent();
			if(content.length() > 500){
				content = content.substring(0, 400);
			}		
			logData.put("content", content);
			MonitorDataSaveTask.addUpdateData(logData);
		}		
		
		finished = true;
		return true;
	}
	
	public boolean isTimeout(){
		long timeout = monitor.getLong("timeout");
		if(timeout <= 0){
			return false;
		}else{
			for(MonitableTask task : tasks){
				if(task.getExecuteTime() > timeout){
					this.appendContent(task.monitorTask, null, null, "Execute time out");
					return true;
				}
			}
		}
		
		return false;
	}
	
	static class Content{
		DataObject task = null;
		DataObject resource = null;
		String resourceName = null;
		List<String> contents = new ArrayList<String>();
		
		public Content(DataObject task, DataObject resource, String resourceName){
			this.task = task;
			this.resource = resource;
			this.resourceName = resourceName;
			
		}
		
		public void appendText(String text){
			contents.add(text);
		}
		
		public String toString(){
			String str = "";
			str = str + "<tr>";
			if(task != null){
				str = str + "<td>" + task.getString("id") + "</td><td>" + task.getString("taskName") + "</td>";
			}else{
				str = str + "<td>&nbsp;</td><td>&nbsp;</td>";
			}
			if(resource != null){
				str = str + "<td>" + resource.getString("resId") + "</td><td>" + resource.getString("resName") + "</td>";
			}else{
				str = str + "<td>&nbsp;</td><td>&nbsp;</td>";
			}
			if(resourceName != null){
				str = str + "<td>" + resourceName + "</td>";
			}else{
				str = str + "<td>&nbsp;</td>";
			}
			str = str + "</tr>";
			
			str = str + "<tr><td colspan=\"5\">";
			for(String content : contents){
				str = str + content + "<br/>";
			}
			
			str = str + "</tr>";
			return str;
		}
	}
}
