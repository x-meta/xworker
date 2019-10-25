package xworker.app.monitor;

import java.util.Date;
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

import xworker.dataObject.DataObject;

public class DataGraberScheduler implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(DataGraberScheduler.class);
	
	Thing thing = null;
	ActionContext actionContext = null;
	Future<DataGraberScheduler> future = null;
	long lastModified;
	long delay;
	long interval;
	
	private DataGraberScheduler(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		this.lastModified = thing.getMetadata().getLastModified();
		this.delay = thing.getLong("delay");
		this.interval = thing.getLong("interval");
	}
	
	private void setFuture(Future<DataGraberScheduler> future){
		this.future = future;
	}
	
	public void stop(){
		MonitorUtils.removeDataGraber(thing);
		
		future.cancel(false);
	}
	
	public void start(){
		thing.doAction("run", actionContext);
	}
	
	public void run(){
		if(thing.getMetadata().isRemoved()){
			cancel();
		}else{
			try{
				Date time = MonitorUtils.getCurrentTime(delay, interval);
				Bindings bindings = actionContext.push();
				bindings.put("dataCreateTime", time);
				try{
					for(Thing child : thing.getChilds("DataGrabers")){
						for(Thing dataGraber : child.getChilds()){
							dataGraber.doAction("run", actionContext);
						}
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
		DataGraberScheduler scheduler = new DataGraberScheduler(self, actionContext);
		Future future = MonitorUtils.getScheduledExecutorService().scheduleAtFixedRate(scheduler, MonitorUtils.getDelay(delay, interval), interval, TimeUnit.MILLISECONDS);
		scheduler.setFuture(future);
		MonitorUtils.putDataGraber(self, scheduler);
	}
	
	/**
	 * DataGraberScheduler事物的run方法的实现。
	 * 
	 * @param actionContext
	 */
	public static void runDataGraberScheduler(ActionContext actionContext){
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
	@SuppressWarnings("unchecked")
	public static void runDataGraber(ActionContext actionContext){
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
		
		//抓取数据
		Map<String, Object> data = (Map<String, Object>) self.doAction("doGrab", actionContext);
		DataObject dataObjectData = new DataObject(dataObject);
		dataObjectData.putAll(data);
		dataObjectData.createOrUpdate(actionContext);
	}
}
