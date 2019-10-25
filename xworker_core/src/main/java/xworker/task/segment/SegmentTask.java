package xworker.task.segment;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

/**
 * 可以分为n个片段的任务。
 * 
 * @author Administrator
 *
 */
public class SegmentTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(SegmentTask.class);
	
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
	private List<RangeTask> tasks = new CopyOnWriteArrayList<RangeTask>();
	
	/** 让任务保存自己的数据的地方 */
	Map<String, Object> datas = new HashMap<String, Object>();
	
	RangeManager rangeManager = null;
	
	public SegmentTask(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		initRangeManager();
	}
		
	public Thing getThing() {
		return thing;
	}

	/**
	 * 初始化RangeManager
	 */
	public void initRangeManager(){
		rangeManager = (RangeManager) thing.doAction("getRanageManager", actionContext, "task", this);
		
		if(rangeManager == null){
			throw new ActionException("RanageManager is null, thing=" + thing.getMetadata().getPath());
		}
	}
	
	public void run(){
		try{
			ThreadPoolExecutor thExecutor = (ThreadPoolExecutor) this.service;
			while(true){
				if(!running){
					break;
				}
				
				boolean changed = false;
				//如果任务数没有达到线程数，启动
				while(thExecutor.getActiveCount() < getThreadCount()){
					if(startNewRange() == false){
						break;
					}
					
					changed = true;
				}
				
				//没有新的任务执行了，完成
				if(tasks.size() == 0 && !this.isContinueTask()){
					break;
				}
				
				long timeOut = this.getTimeOut();
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
					
					if(timeOut >= 0 && task.getExecuteTime() > this.getTimeOut()){
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
			logger.error("Execute SegmentTask error", e);
			
			this.onException(e);
		}finally{
			service.shutdownNow();
			running = false;
		}
	
	}
	
	private void rangeFinished(RangeTask rangeTask){
	}
	
	private boolean startNewRange(){
		if(!running){
			return false;
		}
		
		Range range = getNextRange();
		if(range == null){
			return false;
		}
		
		Thing rangeTask = getRangeTaskThing(range);
		if(rangeTask == null){
			//這里应该是一个异常，不应该返回null
			throw new ActionException("Must reutn range task, path=" + thing.getMetadata().getPath());
		}
		
		RangeTask task = new RangeTask(this, range, rangeTask, actionContext);
		tasks.add(task);
		Future<Boolean> fututre = service.submit(task);
		task.setFuture(fututre);
		
		return true;
	}
	
	/**
	 * 是否是一直持续不断执行的任务。
	 * 
	 * @return
	 */
	public boolean isContinueTask(){
		return thing.getBoolean("continueTask");
	}
	
	public synchronized void start(){
		if(running){
			return;
		}
		
		running = true;
		service = Executors.newFixedThreadPool(getThreadCount());
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
	
	public boolean isRunnint(){
		return running;
	}
	
	public int getTaskCount(){
		return tasks.size();
	}
	
	public String getStatus(){
		if(service == null){
			return "未启动";
		}else if(service.isShutdown()){
			return "已关闭";
		}else if(service.isTerminated()){
			return "已终端";
		}else{
			return "正在运行";
		}		
	}
	
	/**
	 * 获取超时的时间。
	 * 
	 * @return
	 */
	public long getTimeOut(){
		return thing.getLong("timeOut");
	}
	
	/**
	 * 获取下一个片段。
	 * 
	 * @return
	 */
	public Range getNextRange(){
		return rangeManager.getNextRange();
	}
	
	/**
	 * 返回段管理器。
	 * 
	 * @return
	 */
	public RangeManager getRangeManger(){
		return rangeManager;
	}
	
	/**
	 * 根据片段获取针对片段的执行器。
	 * 
	 * @param range
	 * @return
	 */
	public Thing getRangeTaskThing(Range range){
		return (Thing) thing.doAction("getRangeTaskThing", actionContext, UtilMap.toMap("task", this, "range", range));
	}
	
	/**
	 * 片段执行成功了得事件。
	 * 
	 * @param range
	 */
	public void onRangeSuccess(Range range, Thing rangeTask){
		rangeManager.rangeFinished(range);
		
		thing.doAction("onRangeSuccess", actionContext, UtilMap.toMap("range", range, "taskThing", rangeTask, "task", this));
	}
	
	/**
	 * 片段执行时发生异常的事件。
	 * 
	 * @param range 分段
	 * @param taskThing 任务事物
	 * @param e 异常
	 */
	public void onRangeException(Range range, Thing taskThing, Exception e){
		rangeManager.rangeFailed(range);
		
		thing.doAction("onRangeException", actionContext, UtilMap.toMap("range", range, "taskThing", taskThing, "exception", e, "task", this));
	}
	
	/**
	 * 片段执行返回失败的事件。
	 * 
	 * @param range 段
	 * @param taskThing 任务事物
	 */
	public void onRangeFailed(Range range, Thing taskThing){
		rangeManager.rangeFailed(range);
		
		thing.doAction("onRangeFailure", actionContext, UtilMap.toMap("range", range, "taskThing", taskThing, "task", this));
	}
	
	/**
	 * 执行时发生了异常，停止了全部执行。
	 * 
	 * @param e
	 */
	public void onException(Exception e){		
		thing.doAction("onException", actionContext, UtilMap.toMap("exception", e, "task", this));
	}
	
	
	/**
	 * 全部已经执行完毕了。
	 * 
	 */
	public void onFinished(){
		thing.doAction("onFinished", actionContext, UtilMap.toMap("task", this));
	}
	
	/**
	 * 片段执行超时的事件。
	 * 
	 * @param range
	 */
	public void onRangeTimeout(Range range, Thing rangeTask){
		rangeManager.rangeFailed(range);
		
		thing.doAction("onRangeTimeout", actionContext, UtilMap.toMap("range", range, "taskThing", rangeTask, "task", this));
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
				Object data = rangeTask.doAction("doRange", actionContext, "rangeTask", this, "task", task, "range", range);
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
				task.rangeFinished(this);
			}
		}
	}
	
	public void setData(String key , Object value){
		datas.put(key, value);
	}
	
	public Object getData(String key){
		return datas.get(key);
	}
}
