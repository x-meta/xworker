package xworker.app.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class DataMonitorScheduler implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(DataMonitorScheduler.class);
	
	Thing thing = null;
	ActionContext actionContext = null;
	Future<DataMonitorScheduler> future = null;
	long lastModified;
	long delay;
	long interval;
	
	private DataMonitorScheduler(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		this.lastModified = thing.getMetadata().getLastModified();
		this.delay = thing.getLong("delay");
		this.interval = thing.getLong("interval");
	}
	
	private void setFuture(Future<DataMonitorScheduler> future){
		this.future = future;
	}
	
	public void stop(){
		MonitorUtils.removeDataGraber(thing);
		
		future.cancel(false);
	}
	
	public void start(){
		thing.doAction("run", actionContext);
	}
	
	@SuppressWarnings({ "unchecked" })
	public void run(){
		if(thing.getMetadata().isRemoved()){
			cancel();
		}else{
			try{
				Date time = MonitorUtils.getCurrentTime(delay, interval);
				Bindings bindings = actionContext.push();
				bindings.put("dataCreateTime", time);
				
				Thing condition = World.getInstance().getThing("xworker.app.monitor.DataMonitorQueryCondition");
				Map<String, Object> conditionData = new HashMap<String, Object>();
				conditionData.put("endTime", time);
				conditionData.put("startTime", new Date(time.getTime() - thing.getLong("lasttime")));
				bindings.put("conditionData", conditionData);
				bindings.put("conditionConfig", condition);
				bindings.put("messages", new HashMap<String, Object>());
				Map<String, Object> allResults = new HashMap<String, Object>();
				bindings.put("results", allResults);
				try{
					//执行数据检查
					for(Thing child : thing.getChilds("DataMonitors")){
						for(Thing dataGraber : child.getChilds()){
							Map<String, Object> results = (Map<String, Object>) dataGraber.doAction("run", actionContext);
							allResults.put(dataGraber.getMetadata().getName(), results);
						}
					}	
					
					//执行总的Result
					for(Thing result : thing.getChilds("Result")){
						result.doAction("run", actionContext);
					}
				}finally{
					actionContext.pop();
				}
			}catch(Exception e){
				logger.error("Monitor datagraber error, path=" + thing.getMetadata().getPath(), e);
			}
		}
	}
	
	public void cancel(){
		future.cancel(false);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void schedule(Thing self, ActionContext actionContext){
		//首次启动延迟时间，相对于当前凌晨零点整点
		long delay = self.getLong("delay");
		
		//时间间隔
		long interval = self.getLong("interval");
		if(interval <= 0){
			throw new ActionException("interval can not less 0, thing=" + self.getMetadata().getPath());
		}

		//启动定时器
		DataMonitorScheduler scheduler = new DataMonitorScheduler(self, actionContext);
		Future future = MonitorUtils.getScheduledExecutorService().scheduleAtFixedRate(scheduler, MonitorUtils.getDelay(delay, interval), interval, TimeUnit.MILLISECONDS);
		scheduler.setFuture(future);
		MonitorUtils.putDataMonitor(self, scheduler);
	}
	
	/**
	 * DataGraberScheduler事物的run方法的实现。
	 * 
	 * @param actionContext
	 */
	public static void runDataMonitorScheduler(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//如果已经启动则无需重新执行，如有变动抓取器下次执行后会重新定时
		if(MonitorUtils.getDataGraber(self) != null){
			return;
		}else{
			schedule(self, actionContext);
		}
	}
	
	/**
	 * 数据抓取器的run方法。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings({"rawtypes" })
	public static Object runDataMonitor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//确定数据对象
		Thing dataObject = null;
		String dpath = self.getStringBlankAsNull("dataObject");
		if(dpath != null){
			dataObject = World.getInstance().getThing(dpath);
		}
		
		if(dataObject == null){
			Thing dataObjects = self.getThing("DataObjects@0");
			if(dataObjects != null && dataObjects.getChilds().size() > 0){
				dataObject = dataObjects.getChilds().get(0);
			}
		}
		if(dataObject == null){
			throw new ActionException("DataGraber: dataobject is null, thing=" + self.getMetadata().getPath());
		}
		
		//读取数据
		List datas = (List) dataObject.doAction("query", actionContext);
		
		//校验数据
		Object resultObj = self.doAction("doCheck", actionContext, UtilMap.toMap(new Object[]{"datas", datas}));
		String result = String.valueOf(resultObj);
		
		//执行Result，如果符合条件
		for(Thing resultThing : self.getChilds("Result")){
			if(resultThing.getMetadata().getName().equals(result)){
				resultThing.doAction("run", actionContext, UtilMap.toMap(new Object[]{"datas", datas, "result", result}));
			}
		}
		
		//总的返回结果
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("result", result);
		results.put("datas", datas);
		return results;
	}
}
