package xworker.task.segment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import org.xmeta.util.UtilString;
import xworker.lang.executor.Executor;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

/**
 * 可以分为n个片段的任务。
 * 
 * @author Administrator
 *
 */
public class SegmentTask implements Runnable{
	//private static Logger logger = LoggerFactory.getLogger(SegmentTask.class);
	private static final String TAG = SegmentTask.class.getName();
	
	/** 分段任务事物 */
	Thing thing;
	
	/** 变量上下文 */
	ActionContext actionContext;
	
	/** 使用独立的ExecutorService */
	ExecutorService service;
	
	/** 用于监控任务的独立线程 */
	Thread taskThread;
	
	boolean running = false;
	
	/** 当前正在执行的任务 */
	final private List<RangeTask> tasks = new CopyOnWriteArrayList<>();
	
	/** 让任务保存自己的数据的地方 */
	final Map<String, Object> datas = new HashMap<>();

	/** 初始化时带入的变量  */
	Map<String, Object> variables = null;

	//RangeManager rangeManager = null;
	/** 范围生成器 */
	RangeGenerator rangeGenerator;

	/** 范围存储器 */
	RangeStore rangeStore;

	int maxCountOfRetry;
	boolean removeRangeOnSuccess;
	boolean removeRangeOnFinalFail;
	boolean retryOnRangeFail;
	long intervalOfRetry;

	UserTask userTask;
	List<Range> unfinishedTasks;
	
	public SegmentTask(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		Thing rangeStoreThing = thing.doAction("getRangeStore", actionContext);
		if(rangeStoreThing != null){
			this.rangeStore = rangeStoreThing.doAction("create", actionContext);
		}
		if(rangeStore == null){
			throw new ActionException("RangeStore can not be null, path=" + thing.getMetadata().getPath());
		}

		Thing rangeGeneratorThing = thing.doAction("getRangeGenerator", actionContext);
		if(rangeGeneratorThing != null){
			this.rangeGenerator = rangeGeneratorThing.doAction("create", actionContext);
		}
		if(rangeGenerator == null){
			throw new ActionException("RangeGenerator can not be null, path=" + thing.getMetadata().getPath());
		}

		variables = thing.doAction("getVariables", actionContext);
		if(variables == null){
			variables = new HashMap<>();
		}

		maxCountOfRetry = thing.getInt("maxTryCount");
		removeRangeOnSuccess = thing.getBoolean("removeRangeOnSuccess");
		removeRangeOnFinalFail = thing.getBoolean("removeRangeOnFinalFail");
		retryOnRangeFail = thing.getBoolean("retryOnRangeFail");
		intervalOfRetry = thing.getLong("intervalOfRetry");

		rangeGenerator.init(this);
		rangeStore.init(this);
	}
		
	public Thing getThing() {
		return thing;
	}

	public void run(){
		try{
			//通过UserTask显示进度
			userTask = UserTaskManager.createTask(thing, false);
			userTask.start();

			unfinishedTasks = rangeStore.getUnfinishedRanges(this);
			service = Executors.newFixedThreadPool(getThreadCount());
			ThreadPoolExecutor thExecutor = (ThreadPoolExecutor) this.service;
			while(true){
				if(!running || userTask.isStoped()){
					//已经被外部终止了
					break;
				}
				
				boolean changed = false;
				//如果任务数没有达到线程数，启动一个新的
				while(thExecutor.getActiveCount() < getThreadCount()){
					if(!startNewRange()){
						//没有新的任务了，终止
						break;
					}

					changed = true;
				}
				
				//没有新的任务执行了，完成
				if(tasks.size() == 0 && !this.isContinueTask()){
					break;
				}
				
				long timeOut = this.getTimeout();
				//检查任务的状态
				for(int i=0; i<tasks.size(); i++){
					RangeTask task = tasks.get(i);					
					if(task == null){
						tasks.remove(i);
						i--;
						continue;
					}
					
					if(task.isFinished()){
						tasks.remove(i);
						i--;
						continue;
					}
					
					if(timeOut >= 0 && task.getExecuteTime() > this.getTimeout()){
						//已经超时
						tasks.remove(i);
						changed = true;
						i--;
						
						task.future.cancel(true);
						task.range.setStatus(Range.TIMEOUT);
						this.onRangeTimeout(task.range, task.rangeTask);
					}
				}
			
				//睡眠
				if(!changed){
				    Thread.sleep(300);
				}
			}
						
			this.onFinished();
		}catch(Exception e){
			Executor.error(TAG, "Execute SegmentTask error", e);
			
			this.onException(e);
		}finally{
			if(service != null) {
				service.shutdownNow();
			}
			running = false;

			if(userTask != null){
				userTask.finished();
			}

			this.onFinished();
		}
	
	}

	private boolean startNewRange(){
		if(!running){
			return false;
		}

		//尝试运行以前的未执行完毕的分段
		if(unfinishedTasks != null) {
			for(int i=0; i<unfinishedTasks.size(); i++){
				Range range = unfinishedTasks.get(i);
				if(!(intervalOfRetry > 0 && (System.currentTimeMillis() - range.lastExecutedTime < intervalOfRetry * 1000))){
					unfinishedTasks.remove(i);
					startRangeTask(range);

					return true;
				}
			}
		}

		//运行新的分段
		Range range = getNextRange();
		if(range == null){
			return false;
		}else{
			rangeStore.saveRange(this, range);
			rangeStore.updateRangeOffset(range.getEnd());
			startRangeTask(range);
			return true;
		}
	}

	private void startRangeTask(Range range){
		RangeTask task = new RangeTask(this, range, thing, actionContext);
		tasks.add(task);
		Future<Boolean> future = service.submit(task);
		task.setFuture(future);
	}
	
	/**
	 * 是否是一直持续不断执行的任务。
	 * 
	 * @return
	 */
	public boolean isContinueTask(){
		return rangeGenerator.hasNext();
	}
	
	public synchronized void start(){
		if(running){
			return;
		}
		
		running = true;
		taskThread = new Thread(this, "SegmentTask-" + thing.getMetadata().getLabel());
		taskThread.start();
	}
	
	public synchronized void stop(){
		if(running){
			running = false;
		}
	}
	
	public ExecutorService getExecuteService(){
		return service;
	}
	
	public int getThreadCount(){
		int count = thing.getInt("threadCount");
		if(count <= 0){
			count = 1;
		}
		
		return count;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public int getTaskCount(){
		return tasks.size();
	}

	public String getTaskId(){
		String taskId = thing.doAction("getTaskId", actionContext);
		if(taskId == null || taskId.isEmpty()){
			taskId = thing.getMetadata().getPath();
		}

		return taskId;
	}

	public String getStatus(){
		if(service == null){
			return UtilString.getString("lang:d=未启动&en=Not Running", actionContext);
		}else if(service.isShutdown()){
			return UtilString.getString("lang:d=已停止&en=Stopped", actionContext);
		}else if(service.isTerminated()){
			return UtilString.getString("lang:d=已中断&en=Terminated", actionContext);
		}else{
			return UtilString.getString("lang:d=正在运行&en=Running", actionContext);
		}		
	}

	public String getTasksDetail(){
		String str = "<pre>";
		for(RangeTask task : tasks){
			if(str == null){
				str = task.toString();
			}else{
				str = str + "\n" + task.toString();
			}
		}

		return str + "</pre>";
	}
	
	/**
	 * 获取超时的时间。
	 * 
	 * @return
	 */
	public long getTimeout(){
		return thing.getLong("timeout");
	}
	
	/**
	 * 获取下一个片段。
	 * 
	 * @return
	 */
	public Range getNextRange(){
		return rangeGenerator.getNextRange();
	}

	public RangeStore getRangeStore() {
		return rangeStore;
	}

	public RangeGenerator getRangeGenerator() {
		return rangeGenerator;
	}

	/**
	 * 片段执行成功了得事件。
	 * 
	 * @param range
	 */
	public void onRangeSuccess(Range range, Thing rangeTask){
		range.setStatus(Range.FINISHED);
		if(removeRangeOnSuccess){
			rangeStore.removeRange(this, range);
		}else {
			rangeStore.updateRange(this, range);
		}
		
		thing.doAction("onRangeSuccess", actionContext, UtilMap.toMap("range", range, "taskThing", rangeTask, "task", this));

		updateUserTask();
	}

	private void checkRangeStatus(Range range){
		boolean finalFail = false;
		range.runCount ++;
		if(retryOnRangeFail){
			if(range.getRunCount() >= maxCountOfRetry){
				finalFail = true;
			}else {
				if (intervalOfRetry < 0) {
					//重启再试
					return;
				}else{
					if(unfinishedTasks != null){
						unfinishedTasks.add(range);
					}
				}
			}
		}else{
			finalFail = true;
		}

		if(finalFail){
			range.setStatus(Range.FINALFAIL);
			if(removeRangeOnFinalFail){
				rangeStore.removeRange(this, range);
			}else{
				rangeStore.updateRange(this, range);
			}
		}else{
			rangeStore.updateRange(this, range);
		}
	}
	
	/**
	 * 片段执行时发生异常的事件。
	 * 
	 * @param range 分段
	 * @param taskThing 任务事物
	 * @param e 异常
	 */
	public void onRangeException(Range range, Thing taskThing, Exception e){
		range.setStatus(Range.EXCEPTION);
		checkRangeStatus(range);
		
		thing.doAction("onRangeException", actionContext, UtilMap.toMap("range", range, "taskThing", taskThing, "exception", e, "task", this));
		updateUserTask();
	}
	
	/**
	 * 片段执行返回失败的事件。
	 * 
	 * @param range 段
	 * @param taskThing 任务事物
	 */
	public void onRangeFailed(Range range, Thing taskThing){
		range.setStatus(Range.FAILED);
		checkRangeStatus(range);
		
		thing.doAction("onRangeFailure", actionContext, UtilMap.toMap("range", range, "taskThing", taskThing, "task", this));
		updateUserTask();
	}
	
	/**
	 * 执行时发生了异常，停止了全部执行。
	 * 
	 * @param e
	 */
	public void onException(Exception e){		
		thing.doAction("onException", actionContext, UtilMap.toMap("exception", e, "task", this));
		updateUserTask();
	}

	/**
	 * 全部已经执行完毕了，或者被中断取消了。
	 * 
	 */
	public void onFinished(){
		thing.doAction("onFinished", actionContext, UtilMap.toMap("task", this));
		updateUserTask();
	}
	
	/**
	 * 片段执行超时的事件。
	 * 
	 * @param range
	 */
	public void onRangeTimeout(Range range, Thing rangeTask){
		range.setStatus(Range.TIMEOUT);
		checkRangeStatus(range);
		
		thing.doAction("onRangeTimeout", actionContext, UtilMap.toMap("range", range, "taskThing", rangeTask, "task", this));
		updateUserTask();
	}

	private void updateUserTask(){
		if(userTask != null){
			userTask.set(getStatus(), getTasksDetail());
		}
	}

	static class RangeTask implements Callable<Boolean>{
		Range range;
		Thing rangeTask;
		Future<Boolean> future;
		long start = System.currentTimeMillis();
		SegmentTask task;
		ActionContext actionContext;
		boolean failed = false;

		public RangeTask(SegmentTask task, Range range, Thing rangeTask, ActionContext actionContext){
			this.task = task;
			this.range = range;
			this.rangeTask = rangeTask;
			this.actionContext = actionContext;
		}
		
		public void setFailed(boolean failed){
			this.failed = failed;
		}
		
		public void setFuture(Future<Boolean> future){
			this.future = future;
		}
		
		public boolean isFinished(){
			return future.isDone();
		}
		
		public boolean isCancelled(){
			return future.isCancelled();
		}
		
		public boolean isSuccess() throws InterruptedException, ExecutionException{
			return future.get();
		}
		
		/**
		 * 获取开始执行到现在的时间。
		 * 
		 * @return
		 */
		public long getExecuteTime(){
			return System.currentTimeMillis() - start;
		}

		@Override
		public Boolean call() throws Exception {
			try{
				Bindings bindings  = actionContext.push();
				if(task.variables != null){
					bindings.putAll(task.variables);
				}

				bindings.put("rangeTask", this);
				bindings.put("task", task);
				bindings.put("range", range);
				bindings.put("start", task.getRangeGenerator().toObject(range.start));
				bindings.put("end", task.getRangeGenerator().toObject(range.end));
				Object data = rangeTask.doAction("doRange", actionContext);
				boolean result = UtilData.isTrue(data);
				
				if(this.isCancelled()){
					return false;
				}
				
				if(result && !this.failed){
					task.onRangeSuccess(range, rangeTask);
				}else{
					task.onRangeFailed(range, rangeTask);
				}
				return result;
			}catch(Exception e){
				range.setStatus(Range.EXCEPTION);
				task.onRangeException(range, rangeTask, e);
				return false;
			}finally{
				actionContext.pop();

				range.lastExecutedTime = System.currentTimeMillis();
			}
		}

		@Override
		public String toString(){
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = UtilString.getString("lang:d=启动时间：&en=Start Time: " + sf.format(new Date(start)), actionContext);
			return str + " " + range.toString();
		}
	}
	
	public void setData(String key , Object value){
		datas.put(key, value);
	}
	
	public Object getData(String key){
		return datas.get(key);
	}


}
