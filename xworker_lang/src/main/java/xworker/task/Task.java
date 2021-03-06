package xworker.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.util.UtilAction;

/**
 * 任务。
 * 
 * @author Administrator
 *
 */
public class Task implements Runnable{
	//private static Logger logger = LoggerFactory.getLogger(Task.class);
	private static final String TAG = Task.class.getName();
	
	static int uid = 0; 
	/** 任务状态，等待运行 */
	public static final byte STATUS_WAIT = 0;
	/** 任务状态，正在运行 */
	public static final byte STATUS_RUNNING = 1;
	/** 任务状态，已取消 */
	public static final byte STATUS_CANCELED = 2;
	/** 任务状态，暂停 */
	public static final byte STATUS_PAUSED = 3;
	/** 任务状态，已停止 */
	public static final byte STATUS_FINISHED = 4;
	
	/** 任务事物，任务执行时执行任务事物的run方法。*/
	ThingEntry thingEntry;
	/** 执行任务时所使用的动作上下文 */
	ActionContext actionContext;
	/** 任务的状态 */
	byte status;	
	/** 任务执行后情况  */
	Future<?> future;
	/** 是否在执行完时调用callback，如果调用callbak，则会把返回值以result变量名传入，且调用事物的callbak方法。 */
	boolean callback;
	/** 是否在取消时调用callbackCacnel。 */
	boolean callbackCancel;
	/** 是否是计划任务 */
	boolean schedule;
	private Semaphore semaphore = new Semaphore(1);
	private int maxCount;
	private int count = 0;
	private int id;
	/** 执行时间 */
	private long executeTime;
	/** 执行时的参数 */
	private Map<String, Object> parameters;
	/** Thread方式执行时创建的Thread */
	private Thread thread;
	private Map<String, Object> data;
	
	public Task(Thing thing, ActionContext actionContext, boolean schedule){
		this(thing, actionContext, true, true, schedule);
	}
	
	public Task(Thing thing, ActionContext actionContext, boolean callback, boolean callbackCancel, boolean schedule){
		synchronized(Task.class){
			id = uid;
			uid++;
		}
		this.thingEntry = new ThingEntry(thing); 
		this.actionContext = actionContext;
		this.status = STATUS_WAIT;
		this.callback = callback;
		this.callbackCancel = callbackCancel;
		this.schedule = schedule;
		
		parameters = thing.doAction("getParams"	, actionContext);
		if(parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		Map<String, Object> taskParams = UtilAction.getChildChildsResultMap(thing, "TaskVariables", actionContext);
		if(taskParams != null) {
			parameters.putAll(taskParams);
		}
		parameters.put("parentContext", actionContext);
		/*
		 * 
		String params = thing.getStringBlankAsNull("params");
		if(params != null){
			parameters = UtilData.getObjectByType(thing, "params", Map.class, actionContext);
		}
		
		if(parameters == null){
			parameters = UtilAction.getChildChildsResultMap(thing, "Variables", actionContext);
		}*/
	}
	
	public void set(String key, Object value) {
		if(data == null) {
			data = new HashMap<String, Object>();
		}
		
		data.put(key, value);
	}
	
	public Object get(String key) {
		if(data == null) {
			return null;
		}
		
		return data.get(key);
	}
	
	public Map<String, Object> getData(){
		return data;
	}

	public int getId(){
		return id;
	}
	
	private void use(){
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
		}
	}
	
	private void finish(){
		semaphore.release();
	}
	
	protected void setThread(Thread thread) {
		this.thread = thread;
	}
	
	public Thread getThread() {
		return this.thread;
	}
	
	public void testUseFinish(){
		use();
		System.out.println(Thread.currentThread().getName() + ": used");
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			
		}
		System.out.println(Thread.currentThread().getName() + ": finished");
		finish();
		
	}
	
	/**
	 * 创建界面。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Object createUI(ActionContext actionContext) {
		Thing thing = getThing();
		return thing.doAction("createUI", actionContext, "task", this);
	}
	
	public static void main(String args[]){
		try{
			final Task t = new Task(null, null , false, false, false);
			for(int i=0; i<100;i++){
				new Thread(new Runnable(){
					public void run(){						
						while(true){
							t.testUseFinish();
						}
					}
				}).start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消任务，如果任务已经完成或已经取消则返回false。如果任务已经启动，则 mayInterruptIfRunning 参数确定是否应该以试图停止任务的方式来中断执行此任务的线程。 
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public boolean cancel(boolean mayInterruptIfRunning){
		try{
			Thing thing = thingEntry.getThing();
			use();
			
			boolean canceled = false;
			if(schedule){
				if(this.status == STATUS_WAIT || status == STATUS_RUNNING || status == STATUS_PAUSED){
					if(future != null && !future.isDone()){
						if(future.cancel(mayInterruptIfRunning)){
							status = STATUS_FINISHED;
							end();
							canceled = true;
						}else{
							status = STATUS_CANCELED;
							canceled = false;
						}
					}
				}
			}else{
				canceled = normalTaskCancel(mayInterruptIfRunning);			
			}
			
			//如果有取消回调的话
			if(callbackCancel && thing != null){
				ActionContext ac = actionContext;
				if(ac == null){
					ac = new ActionContext();
				}
				Bindings bindings = ac.push();
				bindings.put("task", this);
				bindings.put("stauts", canceled);
				try{
					thing.doAction("callbackCancel", ac, parameters);				
				}finally{
					ac.pop();
				}
			}
			return canceled;
		}finally{
			finish();
		}
	}
	
	private boolean normalTaskCancel(boolean mayInterruptIfRunning){
		if(this.status == STATUS_WAIT || status == STATUS_RUNNING){
			if(future.isDone()){
				return false;
			}
				
			
			if(future != null && future.cancel(mayInterruptIfRunning)){
				status = STATUS_FINISHED;
				end();
				return true;
			}if(thread != null){
				try {
					thread.interrupt();
					status = STATUS_CANCELED;
					return true;
				}catch(Exception e) {
					return false;
				}
			}else{
				status = STATUS_CANCELED;
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * 暂停定时任务。
	 * 
	 * @return
	 */
	public boolean pause(){
		try{
			use();
			if(schedule && status == STATUS_WAIT){
				status = STATUS_PAUSED;
				return true;
			}else{
				return false;
			}
		}finally{
			finish();
		}
	}
	
	/**
	 * 继续定时任务。
	 * 
	 * @return
	 */
	public boolean resume(){
		try{
			use();
			if(schedule && status == STATUS_PAUSED){
				status = STATUS_WAIT;
				return true;
			}else{
				return false;
			}
		}finally{
			finish();
		}
	}
	
	/**
	 * 获取状态的标签。
	 * 
	 * @return
	 */
	public String getStatusLabel(){
		switch(status){
		case 0:
			return UtilString.getString("lang:d=等待&en=Waiting", actionContext);
		case 1:
			return UtilString.getString("lang:d=运行中&en=Running", actionContext);
		case 2:
			return UtilString.getString("lang:d=已取消&en=Canceled", actionContext);
		case 3:
			return UtilString.getString("lang:d=已暂停&en=Paused", actionContext);
		case 4:
			return UtilString.getString("lang:d=已结束&en=Stopped", actionContext);
		}
		
		return UtilString.getString("lang:d=未知状态&en=Unknown", actionContext);
	}
	
	private void end(){
		status = STATUS_FINISHED;
		if(schedule){
			if(!(future.isDone() || future.isCancelled())){
				future.cancel(false);
			}
			
			TaskManager.removeScheduleTask(this);
		}else{
			TaskManager.removeTask(this);
		}
	}
	
	public void run(){
		Thing thing = thingEntry.getThing();
		try{
			use();
			if(status == STATUS_CANCELED || status == STATUS_FINISHED){
				//取消和已经结束的都不应该再执行
				end();
				return;
			}
			
			if(status == STATUS_WAIT){
				//只有等待执行的状态才能进入执行
				status = STATUS_RUNNING;
			}
		}finally{
			finish();
		}	
			
		if(status == STATUS_RUNNING){
			long start = System.currentTimeMillis();
			try{
				count++;
				
				if(!schedule){
					//普通任务的执行
					normalTaskRun();
				}else{
					scheduleTaskRun();
				}								
			}catch(Exception e){
				Executor.error(TAG, "Task excute error, path=" + thing.getMetadata().getPath(), e);
			}finally{
				executeTime = executeTime + (System.currentTimeMillis() - start);
				
				if(maxCount > 0 && count >= maxCount){
					end();
				}
			}
		}
	}
	
	/**
	 * 定时任务的执行。
	 */
	private void scheduleTaskRun(){
		ActionContext ac = actionContext;
		Thing thing = thingEntry.getThing();
		if(ac == null){
			ac = new ActionContext();
		}
		Bindings bindings = ac.push();
		bindings.put("task", this);
		try{
			if(thing == null){
				this.cancel(true);
				return;
			}
			
			Object result = thing.doAction("doTask", ac, parameters);
			
			if(callback){
				bindings.put("result", result);
				thing.doAction("callback", ac);
			}
		}finally{
			ac.pop();
			
			if(status == STATUS_CANCELED){
				end();
			}else{
				//定时任务执行完毕后回到等待状态
				status = STATUS_WAIT;
			}
		}
	}
	
	/**
	 * 普通任务的执行。
	 */
	private void normalTaskRun(){
		ActionContext ac = actionContext;
		Thing thing = thingEntry.getThing();
		if(ac == null){
			ac = new ActionContext();
		}
		Bindings bindings = ac.push();
		bindings.put("task", this);
		try{
			Object result = thing.doAction("doTask", ac, parameters);
			
			if(callback){
				bindings.put("result", result);
				thing.doAction("callback", ac);
			}
		}finally{
			ac.pop();
			
			//普通任务执行完毕就结束了
			end();
		}
	}
	
	public Thing getThing() {
		return thingEntry.getThing();
	}


	public String getPath(){
		Thing thing = thingEntry.getThing();
		return thing.getMetadata().getPath();
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}

	public byte getStatus() {
		return status;
	}

	public Future<?> getFuture() {
		return future;
	}

	public void setFuture(Future<?> future) {
		this.future = future;
	}

	public int getExecuteMaxCount() {
		return maxCount;
	}

	public void setExecuteMaxCount(int executeCount) {
		this.maxCount = executeCount;
	}

	public int getCount() {
		return count;
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public String getExecuteTimeLabel(){
		long days = executeTime / (24 * 3600000);
		long  time = executeTime % (24 * 3600000);
		long hours = time / 3600000;
		time = time % 3600000;
		long minute = time / 60000;
		time = time % 60000;
		long second = time / 1000;
		time = time % 1000;
		
		String str = "";
		if(days > 0){
			str = days + UtilString.getString("lang:d=天&en= days", actionContext);
		}
		if(hours > 0){
			str = str + hours + UtilString.getString("lang:d=小时&en= hours", actionContext);
		}
		if(minute > 0){
			str = str + minute + UtilString.getString("lang:d=分钟&en= minutes", actionContext);
		}
		if(second > 0){
			str = str + second + UtilString.getString("lang:d=秒&en= seconds", actionContext);
		}
		if(time > 0){
			str = str + time + UtilString.getString("lang:d=毫秒&en= milliseconds", actionContext);
		}
		if(str.equals("")){
			str = UtilString.getString("lang:d=小于1毫秒&en less 1 millisencond", actionContext);
		}
		return str;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public void putParameter(String key , Object value) {
		this.parameters.put(key, value);
	}
	
	public void putParameterAll(Map<String, Object> values) {
		this.parameters.putAll(values);
	}
}
