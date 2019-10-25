package xworker.app.monitor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.monitor.res.MonitorContext;
import xworker.app.monitor.res.ResourceTask;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.task.NameThreadFactory;

public class MonitorUtils {
	/** 定时任务的执行器 */
	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(15, new NameThreadFactory("xworker-app-monitor"));
	/** 定期任务缓存 */
	private static Map<String, DataGraberScheduler> dataGrabers = new HashMap<String, DataGraberScheduler>();
	private static Map<String, DataMonitorScheduler> monitorGrabers = new HashMap<String, DataMonitorScheduler>();
	
	/** 放到资源里的一个键值，表示资源对应的任务数据对象 */
	public static final String resourceTaskKey = "__resource__task__key__";
	
	/**
	 * 执行一个监控命令。
	 * 
	 * @param command
	 */
	public static void execute(Runnable command){
		scheduledExecutorService.execute(command);
	}
	
	public static void removeDataGraber(Thing thing){
		dataGrabers.remove(thing.getMetadata().getPath());
	}
	
	public static List<Thing> getDataGrabers(){
		List<Thing> list = new ArrayList<Thing>();
		for(String key : dataGrabers.keySet()){
			list.add(dataGrabers.get(key).thing);
		}
		
		return list;
	}
	
	public static List<Thing> getDataMonitors(){
		List<Thing> list = new ArrayList<Thing>();
		for(String key : monitorGrabers.keySet()){
			list.add(monitorGrabers.get(key).thing);
		}
		
		return list;
	}
	
	public static ScheduledExecutorService getScheduledExecutorService(){
		return scheduledExecutorService; 
	}
	
	public static void putDataGraber(Thing thing, DataGraberScheduler scheduler){
		dataGrabers.put(thing.getMetadata().getPath(), scheduler);
	}
	
	public static DataGraberScheduler getDataGraber(Thing thing){
		return dataGrabers.get(thing.getMetadata().getPath());
	}
	
	public static void removeDataMonitor(Thing thing){
		monitorGrabers.remove(thing.getMetadata().getPath());
	}
		
	public static void putDataMonitor(Thing thing, DataMonitorScheduler scheduler){
		monitorGrabers.put(thing.getMetadata().getPath(), scheduler);
	}
	
	public static DataMonitorScheduler getDataMonitor(Thing thing){
		return monitorGrabers.get(thing.getMetadata().getPath());
	}
	
	/**
	 * 获取从当前起执行任务时应该延迟的时间。
	 * 
	 * @param interval
	 * @param delay
	 * @return
	 */
	public static long getDelay(long delay, long interval){
		long current = System.currentTimeMillis();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			current = System.currentTimeMillis() - sf.parse(sf.format(new Date())).getTime();
		} catch (ParseException e) {
		}
		
		if(current >= delay){
			return current - delay;
		}else{
			long pastTime = delay - current;
			return interval - pastTime % interval;
		}
	}
	
	/**
	 * 返回当前的抓取时间，统一格式化为整点时间。
	 * 
	 * @param delay
	 * @param interval
	 * @return
	 */
	public static Date getCurrentTime(long delay, long interval){
		long current = System.currentTimeMillis();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		long date = 0;
		try {
			date = sf.parse(sf.format(new Date())).getTime();
			current = System.currentTimeMillis() - date - delay;
		} catch (ParseException e) {
		}
		
		return new Date(date + delay + (current / interval) * interval);
	}
	
	/**
	 * 执行下级资源监控任务。
	 * 
	 * @param actionContext
	 * @throws OgnlException 
	 */
	@SuppressWarnings("unchecked")
	public static void executeSubResTasks(DataObject resuourceItem, ActionContext actionContext) throws OgnlException{
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		
		List<DataObject> subTasks = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitorTaskItems",
				UtilMap.toMap("parentId", monitorTask.get("id")), actionContext);
		for(DataObject subTask : subTasks){
			List<DataObject> resources = null;
			if(resuourceItem != null){
				resources = new ArrayList<DataObject>();
				resources.add(resuourceItem);
			}else{
				resources = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitorResItems",
						 UtilMap.toMap("taskId", subTask.get("id"), "status", 1), actionContext);
			}
			 
			DataObject task = new DataObject("xworker.app.monitor.dataobjects.ResMonitorTasks");
			task.put("id", subTask.get("taskId"));
			task = task.load(actionContext);
			if(task != null){
				Bindings parentBindings = actionContext.peek();
				String preCondition = subTask.getString("preCondition");
				if(subTask.getBoolean("perRowExecute")){
					List<Map<String, Object>> vlist = (List<Map<String, Object>>) parentBindings.get("vlist");
					if(vlist != null){
						for(Map<String, Object> values : vlist){
							Bindings bindings = new Bindings();
							bindings.putAll(parentBindings);
							bindings.putAll(values);
							bindings.put("v", values);
									
							try{
								actionContext.push(bindings);
								if(preCondition == null || "".equals(preCondition) 
										|| UtilData.getBoolean(OgnlUtil.getValue(preCondition, actionContext), false)){
									ResourceTask dbTask = new ResourceTask(monitorContext, actionContext, monitorContext.monitor, subTask, task, resources);
								    dbTask.paramBindings = bindings;
								    MonitorUtils.execute(dbTask);
								}
							}finally{
								actionContext.pop();
							}
						}
					}
				}else{
					if(preCondition == null || "".equals(preCondition) 
							|| UtilData.getBoolean(OgnlUtil.getValue(preCondition, actionContext), false)){
						ResourceTask dbTask = new ResourceTask(monitorContext, actionContext, monitorContext.monitor, subTask, task, resources);
					    dbTask.paramBindings = parentBindings;
					    MonitorUtils.execute(dbTask);
					}				    
				}
				
			}
		}
	}

	/**
	 * 保存监控数据的一个公共方法。
	 * 
	 * @param monitorContext 监控上下文，用于获取监控的定义和监控的起始时间等
	 * @param monitorTask 监控任务定义
	 * @param monitorTaskResource 监控任务对应的资源定义
	 * @param taskThing 监控任务事物
	 * @param resourceId 监控资源标识
	 * @param columnMappings 数据列的映射
	 * @param rowIdIndex 每行数据的标识列索引，在返回的二维表格数据中可以作为标识的列索引
	 * @param listDatas 要保存的数据，是二维表格数据
	 */
	public static void saveMonitorData(MonitorContext monitorContext, DataObject monitorTask,
			DataObject monitorTaskResource, Thing taskThing, Object resourceId, 
			String[] columnMappings, int rowIdIndex, List<Map<String, Object>> listDatas){
		if(columnMappings != null){
			String[] dts = columnMappings;
			
			for(Map<String, Object> values : listDatas){
				for(int i=0; i<dts.length; i++){
					if(!"".equals(dts[i])){
						DataObject data = new DataObject("xworker.app.monitor.dataobjects.MonitorData");
						data.put("taskThingPath", taskThing.getMetadata().getPath());
						data.put("resourceId", resourceId);
						data.put("dataName", dts[i]);
						data.put("value", values.get("v" + (i + 1)));
						data.put("grabStartTime", monitorContext.getStartTime());
						data.put("grabEndTime", new Date());
						data.put("monitorId", monitorContext.monitor.get("id"));
						data.put("monitorTaskId", monitorTask.get("id"));
						data.put("monitorTaskResId", monitorTaskResource.get("id"));
						if(rowIdIndex > 0){
							data.put("dataId", values.get("v" + rowIdIndex));
						}
						
						MonitorDataSaveTask.addCreateData(data);
					}
				}
			}
		}
	}
	
	/**
	 * 处理字符串类型的数据方法，包含了分析数据为vlist和vmap，保存数据，执行子任务等功能。
	 * 
	 * @param result 字符串表示的值
	 * @param dataNames 数据列的名字映射，要保存的数据在这里
	 * @param saveMontiorData 是否保存监控数据
	 * @param multiLine 是否是多行数据
	 * @param dataIdIndex 数据标识列的索引
	 * 
	 * @param actionContext 动作上下文
	 * @throws OgnlException 
	 */
	public static void handleStringResult(String result, String splitStr, String lineSplitStr, String dataNames, boolean saveMontiorData, boolean multiLine, int dataIdIndex, String resourceId, ActionContext actionContext) throws OgnlException{
		DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
		DataObject monitorTaskResource = (DataObject) actionContext.get("monitorTaskResource");
		MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
		Thing taskThing = (Thing) actionContext.get("taskThing");
		
		Map<String, Map<String, Object>> mapDatas = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
		actionContext.peek().put("vmap", mapDatas);
		actionContext.peek().put("vlist", listDatas);
		
		String[] columnMappings = null;
		if(dataNames != null && !"".equals(dataNames)){
			columnMappings = dataNames.split("[,]");
		}
		
		//转化数据到List和Map
		if(lineSplitStr == null || "".equals(lineSplitStr)){
			lineSplitStr = "\n";
		}
		
		if(result != null){
			String[] rows = null;
			if(multiLine){
				rows = result.split("[" + lineSplitStr + "]");
			}else{
				rows = new String[]{result};
			}
			
			int rowIndex = 1;
			for(String row : rows){
				String rs[] = row.split("[" + splitStr + "]");
				
				Map<String, Object> values = new HashMap<String, Object>();
				for(int i=0; i<rs.length; i++){
					Object value = rs[i].trim();
					try{
						//尝试转化为数字
						value = NumberFormat.getNumberInstance().parse(rs[i].trim());
					}catch(Exception e){						
					}
					
					values.put("v" + (i + 1), value);
					if(columnMappings != null && columnMappings.length > i){
						String name = columnMappings[i];
						if(name != null && !"".equals(name)){
							values.put(name, value);
						}
					}
					
					actionContext.peek().put("v" + (i + 1), value);
				}
				
				listDatas.add(values);
				mapDatas.put("r" + rowIndex, values);
				if(dataIdIndex > 0 && dataIdIndex < rs.length){
					mapDatas.put(rs[dataIdIndex], values);
				}
				
				rowIndex++;
			}
		}
		
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
	}
	
	/**
	 * 通过资源数据对象装载资源。
	 * 
	 * @param monitorTaskResource
	 * @param actionContext
	 * @return
	 */
	public static Object loadResource(DataObject monitorTaskResource, ActionContext actionContext){
		DataObject task = (DataObject) monitorTaskResource.get(MonitorUtils.resourceTaskKey);
		if(task != null){
			Thing taskThing = World.getInstance().getThing(task.getString("taskPath"));
			if(taskThing != null){
				return taskThing.doAction("loadResource", actionContext, UtilMap.toMap("resource", monitorTaskResource));
			}
		}
		
		return null;
	}
}
