package xworker.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 任务管理器。
 * 
 * @author Administrator
 *
 */
public class TaskManager {
	/** 正在执行的任务 */
	private static Map<String, List<Task>> tasks = new HashMap<String, List<Task>>();
	
	/** 正在执行的TimerTask */
	private static Map<String, Task> scheduledTasks = new HashMap<String, Task>();
	
	/** 非定时任务的执行器 */
	private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 500, 15, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(10), new NameThreadFactory("xworker-task-thread"), new ThreadPoolExecutor.AbortPolicy());
	
	/** 定时任务的执行器 */
	private static ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(10, new NameThreadFactory("xworker-scheduled-task-thead"));
	
	public static ScheduledThreadPoolExecutor getScheduledExecutorService() {
		return scheduledExecutorService;
	}
	
	public static ThreadPoolExecutor getExecutorService(){
		return executorService;
	}

	/**
	 * 通过事物的路径获取定时的任务。
	 * 
	 * @param path
	 * @return
	 */
	public static Task getScheduledTask(String path){
		return scheduledTasks.get(path);
	}
	
	/**
	 * 通过事物获取相对应的定时任务。
	 * 
	 * @param taskThing
	 * @return
	 */
	public static Task getScheduledTask(Thing taskThing){
		return scheduledTasks.get(taskThing.getMetadata().getPath());
	}
	
	/**
	 * 移除指定的计划任务，如果存在那么还将取消任务。
	 * 
	 * @param taskThing
	 */
	public static void remvoeScheduledTask(Thing taskThing){
		Task task = getScheduledTask(taskThing);
		if(task != null){
			task.cancel(false);
		}
	}
	
	/**
	 * 通过路径获取已执行的任务列表。
	 * 
	 * @param path
	 * @return
	 */
	public static List<Task>  getTasks(String path){
		return tasks.get(path);
	}
	
	/**
	 * 通过任务事物获取已经执行的任务列表。
	 * 
	 * @param taskThing
	 * @return
	 */
	public static List<Task>  getTasks(Thing taskThing){
		return tasks.get(taskThing.getMetadata().getPath());
	}
	
	/**
	 * 获取所有的任务事物的列表。
	 * 
	 * @return
	 */
	public synchronized static List<Thing> getTaskThings(){
		World world = World.getInstance();
		List<Thing> list = new ArrayList<Thing>();
		for(String key : tasks.keySet()){
			Thing thing = world.getThing(key);
			if(thing != null){
				list.add(thing);
			}
		}
		
		return list;
	}
	
	/**
	 * 获取所有的定时任务事物的列表。
	 * 
	 * @return
	 */
	public synchronized static List<Thing> getScheduledTaskThings(){
		World world = World.getInstance();
		List<Thing> list = new ArrayList<Thing>();
		for(String key : scheduledTasks.keySet()){
			Thing thing = world.getThing(key);
			if(thing != null){
				list.add(thing);
			}
		}
		
		return list;
	}
	
	public static Map<String, Task> getScheduledTasks(){
		return scheduledTasks;
	}
	
	public static TimeUnit getTimeUnit(String timeUnit){
		TimeUnit unit = TimeUnit.MILLISECONDS;		
		if("DAYS".equals(timeUnit)){
			unit = TimeUnit.DAYS;
		}else if("HOURS".equals(timeUnit)){
			unit = TimeUnit.HOURS;
		}else if("MICROSECONDS".equals(timeUnit)){
			unit = TimeUnit.MICROSECONDS;
		}else if("MILLISECONDS".equals(timeUnit)){
			unit = TimeUnit.MILLISECONDS;
		}else if("MINUTES".equals(timeUnit)){
			unit = TimeUnit.MINUTES;
		}else if("NANOSECONDS".equals(timeUnit)){
			unit = TimeUnit.NANOSECONDS;
		}else if("SECONDS".equals(timeUnit)){
			unit = TimeUnit.SECONDS;
		} 
		return unit;
	}
	
	/**
	 * 执行事物的任务。
	 * @param actionContext
	 * @throws ParseException 
	 */
	public static synchronized Object run(ActionContext actionContext) throws ParseException{
		Thing self = (Thing) actionContext.get("self");
		if(!self.getBoolean("enable")){
			return null;
		}
		
		boolean callback = self.getBoolean("callback");
		boolean callbackCancel= self.getBoolean("callbackCancel");
		boolean singleInstance = self.getBoolean("singleInstance");
		boolean schedule = self.getBoolean("schedule");
		long delay = self.getLong("delay");
		long period = self.getLong("period");
		boolean fixedRate = self.getBoolean("fixedRate");
		String timeUnit = self.getString("timeUnit");
		TimeUnit unit = getTimeUnit(timeUnit);
		boolean fixTime = self.getBoolean("fixTime");
		String fixTimeStr = self.getStringBlankAsNull("fixTimeStr");
		int executeCount = self.getInt("executeCount", 0);
		delay = TaskManager.tooMillis(delay, unit);		
		period = TaskManager.tooMillis(period, unit);
		unit = TimeUnit.MILLISECONDS; //统一都转化为毫秒
		
		Task task = null;
		if(schedule){
			task = scheduledTasks.get(self.getMetadata().getPath());
			if(task != null){
				task.cancel(false);				
			}
			
			//定时任务
			task = new Task(self, actionContext, callback, callbackCancel, true);
			if(fixTime && fixTimeStr != null){
				DelayInfo delayInfo = new DelayInfo(fixTimeStr, period, unit);
				if(period > 0){					
					if(self.getBoolean("calculatePassCount")){
						if(executeCount > 0 && delayInfo.executedCount > executeCount){
							return null;
						}
						task.setCount(delayInfo.executedCount);
					}
					
					Future<?> future  = null;
					if(fixedRate){
						future = scheduledExecutorService.scheduleAtFixedRate(task, delayInfo.delay, period, unit);
					}else{
						future = scheduledExecutorService.scheduleWithFixedDelay(task, delayInfo.delay, period, unit);
					}
					task.setFuture(future);
					scheduledTasks.put(self.getMetadata().getPath(), task);
				}else{
					//定时但是非定期任务
					if(delayInfo.executedCount == 0){ //executeCount==0表示还未执行
						Future<?> future  = scheduledExecutorService.schedule(task, delayInfo.delay, unit);
						task.setFuture(future);
						scheduledTasks.put(self.getMetadata().getPath(), task);
					}
				}
			}else{
				if(period > 0){
					task.setExecuteMaxCount(executeCount);
					//非定期执行的任务
					Future<?> future  = null;
					if(fixedRate){
						future = scheduledExecutorService.scheduleAtFixedRate(task, delay, period, unit);
					}else{
						future = scheduledExecutorService.scheduleWithFixedDelay(task, delay, period, unit);
					}
					task.setFuture(future);
					scheduledTasks.put(self.getMetadata().getPath(), task);
				}else{
					//非定期执行的任务
					Future<?> future  = scheduledExecutorService.schedule(task, delay, unit);
					task.setFuture(future);
					scheduledTasks.put(self.getMetadata().getPath(), task);
				}
			}		
		}else{
			//非定时任务
			task = (Task) tasks.get(self.getMetadata().getPath());
			if(singleInstance && tasks.get(self.getMetadata().getPath()) != null){
				return task;
			}
					
			task = new Task(self, actionContext, callback, callbackCancel, false);
			putTask(task);
			if(self.getBoolean("thread")) {
				new Thread(task, self.getMetadata().getLabel()).start();
			}else {
				Future<?> future = executorService.submit(task);
				task.setFuture(future);
			}			
		}
		
		if(self.getBoolean("saveAsGlobalVar")){
			actionContext.getScope(0).put(self.getMetadata().getName(), task);
		}
		
		return task;
	}
	
	/**
	 * 启动一个普通的任务。
	 * 
	 * @param taskThing
	 * @param actionContext
	 */
	public static Task startTask(Thing taskThing, ActionContext actionContext, Map<String, Object> params){
		Task task = new Task(taskThing, actionContext, false, false, false);
		task.setParameters(params);
		Future<?> future = executorService.submit(task);
		task.setFuture(future);
		putTask(task);
		
		return task;
	}
	
	/**
	 * 放入定期任务。
	 * 
	 * @param task
	 */
	protected synchronized static void putScheduleTask(Task task){
		scheduledTasks.put(task.getPath(), task);
	}
	
	/**
	 * 移除定时任务。
	 * 
	 * @param task
	 */
	protected synchronized static void removeScheduleTask(Task task){
		Task current = scheduledTasks.get(task.getPath());
		if(current == task){
			scheduledTasks.remove(task.getPath());
		}
	}
	
	
	/**
	 * 放入常规任务到缓存。
	 * 
	 * @param task
	 */
	protected synchronized static void putTask(Task task){
		List<Task> taskList = tasks.get(task.getPath());
		if(taskList == null){
			taskList = new ArrayList<Task>();
			tasks.put(task.getPath(), taskList);
		}
		taskList.add(task);
	}
	
	/**
	 * 从任务管理器中移除任务。
	 * 
	 * @param task
	 */
	protected synchronized static void removeTask(Task task){
		List<Task> taskList = tasks.get(task.getPath());
		if(taskList != null){
			taskList.remove(task);
			if(taskList.size() == 0){
				tasks.remove(task.getPath());
			}
		}
	}
	
	/**
	 * 返回固定频率定点运行的从当前为开始的延迟时间。
	 * 
	 * @param fixTimeStr
	 * @param period
	 * @param unit
	 * @return
	 * @throws ParseException
	 */
	public static long getFixedDelay(String fixTimeStr, long period, TimeUnit unit) throws ParseException{
		//先把时间转化为毫秒
		period = tooMillis(period, unit);
		
		Date fixTime = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sf1 = null;
		switch(fixTimeStr.length()){
		case 19: //yyyy-MM-dd HH:mm:ss				
			fixTime = sf.parse(fixTimeStr);
			break;
		case 14: //MM-dd HH:mm:ss
			sf1 = new SimpleDateFormat("yyyy");
			fixTime = sf.parse(sf1.format(new Date()) + "-" + fixTimeStr);
			break;
		case 11://dd HH:mm:ss
			sf1 = new SimpleDateFormat("yyyy-MM");
			fixTime = sf.parse(sf1.format(new Date()) + "-" + fixTimeStr);
			break;
		case 8://HH:mm:ss
			sf1 = new SimpleDateFormat("yyyy-MM-dd");
			fixTime = sf.parse(sf1.format(new Date()) + " " + fixTimeStr);
			break;
		case 5://mm:ss
			sf1 = new SimpleDateFormat("yyyy-MM-dd HH:");
			fixTime = sf.parse(sf1.format(new Date()) + ":" + fixTimeStr);
			break;
		case 2://ss
			sf1 = new SimpleDateFormat("yyyy-MM-dd HH:MM");
			fixTime = sf.parse(sf1.format(new Date()) + ":" + fixTimeStr);
			break;
		}
		
		if(fixTime.getTime() >= System.currentTimeMillis()){
			//定时的时间还未到
			return fixTime.getTime() - System.currentTimeMillis(); 
		}else{
			//定时的时间已经过去
			long past = System.currentTimeMillis() - fixTime.getTime();			
			long delay =  period - (past % period);
			if(delay < 0){
				return 0;
			}else{
				return delay;
			}
		}
	}
	
	/**
	 * 把时间转为毫秒。
	 * 
	 * @param duration 周期
	 * @param unit 事件
	 * @return 毫秒值
	 */
	public static long tooMillis(long duration, TimeUnit unit){
		return unit.toMillis(duration);		
	}
	
	static class DelayInfo{
		long delay;
		long period; 
		int executedCount;
		
		public DelayInfo(String fixTimeStr, long period, TimeUnit unit) throws ParseException{
			//先把时间转化为毫秒
			this.period = unit.toMillis(period);
			
			Date fixTime = null;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sf1 = null;
			switch(fixTimeStr.length()){
			case 19: //yyyy-MM-dd HH:mm:ss				
				fixTime = sf.parse(fixTimeStr);
				break;
			case 14: //MM-dd HH:mm:ss
				sf1 = new SimpleDateFormat("yyyy");
				fixTime = sf.parse(sf1.format(new Date()) + "-" + fixTimeStr);
				break;
			case 11://dd HH:mm:ss
				sf1 = new SimpleDateFormat("yyyy-MM");
				fixTime = sf.parse(sf1.format(new Date()) + "-" + fixTimeStr);
				break;
			case 8://HH:mm:ss
				sf1 = new SimpleDateFormat("yyyy-MM-dd");
				fixTime = sf.parse(sf1.format(new Date()) + " " + fixTimeStr);
				break;
			case 5://mm:ss
				sf1 = new SimpleDateFormat("yyyy-MM-dd HH:");
				fixTime = sf.parse(sf1.format(new Date()) + ":" + fixTimeStr);
				break;
			case 2://ss
				sf1 = new SimpleDateFormat("yyyy-MM-dd HH:MM");
				fixTime = sf.parse(sf1.format(new Date()) + ":" + fixTimeStr);
				break;
			}
			
			if(fixTime.getTime() >= System.currentTimeMillis()){
				//定时的时间还未到
				delay = fixTime.getTime() - System.currentTimeMillis(); 
			}else{
				//定时的时间已经过去
				long past = System.currentTimeMillis() - fixTime.getTime();
				executedCount = (int) (past / this.period) + 1;
				delay = this.period - (past % this.period);
				if(delay < 0){
					delay = 0;
				}
			}
		}
	}
	
	/**
	 * 任务事物自身的停止动作。
	 * 
	 * @param actionContext
	 */
	public static void stop(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TaskManager.remvoeScheduledTask(self);
	}
}
