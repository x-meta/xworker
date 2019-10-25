package xworker.app.monitor.res;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.MonitorUtils;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.task.TaskManager;

public class ResMonitor {
	private static Map<Long, DataObject> monitors = new HashMap<Long, DataObject>();
	private static CheckResMonitorTask monitorJobTask = new CheckResMonitorTask();
	static{
		MonitorUtils.getScheduledExecutorService().scheduleWithFixedDelay(monitorJobTask, 60000, 60000, TimeUnit.MILLISECONDS);
	}
	
	public static void addMonitorContext(MonitorContext context){
		monitorJobTask.contexts.add(context);
	}
	
	/**
	 * 通过数据库的配置刷新定时任务。
	 * 
	 * @param actionContext
	 * @throws ParseException 
	 */
	public static synchronized void run(ActionContext actionContext) throws ParseException{
		//所有生效的数据库任务
		List<DataObject> allMonitors = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitor", UtilMap.toMap("status", 1), actionContext);
		
		//查看定时任务的变更和更新任务
		for(DataObject monitor : allMonitors){
			Long id = monitor.getLong("id");
			DataObject cacheMonitor = monitors.get(id);
			if(cacheMonitor != null){
				long lastModified = monitor.getDate("updateTime").getTime();
				long oldLastModified = cacheMonitor.getDate("updateTime").getTime();
				if(lastModified != oldLastModified){
					Future<?> future = (Future<?>) cacheMonitor.get("___future___");
					if(future != null){
						future.cancel(true);
					}
				}else{
					continue;
				}
				
			}
			
			//任务已改变或者是一个新任务，那么重新定时
			ResMonitorTask task = new ResMonitorTask(monitor);			
			boolean fixTime = monitor.getBoolean("fixTime");
			String fixTimeStr = monitor.getString("fixTimeStr");
			TimeUnit unit = TaskManager.getTimeUnit(monitor.getString("timeUnit"));
			long period = unit.toMillis(monitor.getLong("period"));
			unit = TimeUnit.MILLISECONDS;
			long delay = 0;
			if(fixTime){
				delay = TaskManager.getFixedDelay(fixTimeStr, period, unit);
			}
			boolean fixedDelay = monitor.getBoolean("fixedDelay");
			
			Future<?> future = null;
			if(fixedDelay){
				future = MonitorUtils.getScheduledExecutorService().scheduleWithFixedDelay(task, delay, period, unit);
			}else{
				future = MonitorUtils.getScheduledExecutorService().scheduleAtFixedRate(task, delay, period, unit);
			}
			monitor.put("___future___", future);	
			monitor.put("__monitor__task__", task);
			monitors.put(id, monitor);
		}
		
		//移除已经取消的任务
		List<Long> removedList = new ArrayList<Long>();
		for(long id : monitors.keySet()){
			boolean have = false;
			for(DataObject monitor : allMonitors){
				if(monitor.getLong("id") == id){
					have = true;
					break;
				}
			}
			
			if(!have){
				removedList.add(id);
			}
		}
		
		for(long id : removedList){
			DataObject cacheMonitor = monitors.get(id);
			if(cacheMonitor != null){
				Future<?> future = (Future<?>) cacheMonitor.get("___future___");
				if(future != null){
					future.cancel(true);
				}
			}
			
			monitors.remove(id);
		}
	}
	
}
