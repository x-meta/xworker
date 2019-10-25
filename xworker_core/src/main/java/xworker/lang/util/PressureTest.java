package xworker.lang.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

/**
 * 压力测试。
 * 
 * @author Administrator
 *
 */
public class PressureTest {
	private static final String TAG = "PressureTest";
	//private static Logger logger = LoggerFactory.getLogger(PressureTest.class);
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String cacheKey = "__PressureTest__";
		PressureTest es = (PressureTest) self.getData(cacheKey);
		if(es != null && !es.isShutdown()){
			Executor.warn(TAG, "Pressure test not finished, path=" + self.getMetadata().getPath());
		}else{
			PressureTest test = new PressureTest(self, actionContext);
			self.setData(cacheKey , test);
			test.run();
		}
	}
	
	public static void shutdown(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String cacheKey = "__PressureTest__";
		PressureTest es = (PressureTest) self.getData(cacheKey);
		if(es != null && !es.isShutdown()){
			es.shutDown();
		}
	}
	
	Thing thing;
	ActionContext actionContext;
	ExecutorService es;
	
	/** 已经执行次数 */
	int executedCount;
	/** 当前正在执行的次数 */
	int currentCount;
	/** 异常数量 */
	int exceptionCount;
	/** 剩余次数 */
	int leftCount;
	/** 所有已经执行的事件 */
	long executedTime;
	/** 最短的一次执行时间 */
	long minTime;
	/** 最长的一次执行时间 */
	long maxTime;
	long start = System.currentTimeMillis();
	long currentTime = System.currentTimeMillis();
	
	public PressureTest(Thing thing , ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		leftCount = thing.getInt("taskCount");
		es = Executors.newFixedThreadPool(thing.getInt("threadSize"));		
	}
	
	public boolean isShutdown(){
		return es.isShutdown();
	}
	
	public void shutDown(){
		es.shutdownNow();
		
		thing.doAction("onFinish", actionContext);
	}
	
	public void run(){		
		thing.doAction("init", actionContext);
		
		for(int i=0; i<thing.getInt("taskCount"); i++){
			es.execute(new PressureTestTask(thing, actionContext, this));
		}
	}
	
	public synchronized  void started(){
		currentCount++;
		leftCount--;
	}
	
	public synchronized void successed(long time){
		currentCount--;
		executedCount++;

		checkStatus(time);
	}
	
	public synchronized void exception(long time){
		currentCount--;
		exceptionCount++;
		
		checkStatus(time);
	}
	
	public void checkStatus(long time){
		executedTime += time;
		if(minTime > time){
			minTime = time;
		}
		
		if(maxTime < time){
			maxTime = time;
		}
				
		if(currentCount == 0 && leftCount == 0){
			printStatus();
			Executor.info(TAG, "Pressure Test finished, path=" + thing.getMetadata().getPath());
			shutDown();
		}else{
			//每隔两秒打印一次信息
			if(System.currentTimeMillis() - currentTime > 2000){
				currentTime = System.currentTimeMillis();
				printStatus();
			}
		}
	}
	
	public void printStatus(){
		double perSecond = ((1000 * (executedCount + exceptionCount)) / ((System.currentTimeMillis() - start)) * 1000) / 1000d; 
		
		Executor.info(TAG, thing.getMetadata().getLabel() + ": success=" + executedCount 
				+ ", failure: " + exceptionCount + ", left: " + leftCount + ", " + perSecond + "(Count/Sec)" 
				+ ", minTime: " + minTime + "(Mil), maxTime:" + maxTime + "(Mil), lastTime: " + ((System.currentTimeMillis() - start) / 1000) + "(Sec)");
	}
	
	static class PressureTestTask implements Runnable{
		Thing thing;
		ActionContext actionContext;
		PressureTest test;
		
		public PressureTestTask(Thing thing, ActionContext actionContext, PressureTest test){
			this.thing = thing;
			this.actionContext = actionContext;
			this.test = test;
		}
		
		public void run(){
			//任务开始
			test.started();
			
			long start = System.currentTimeMillis();
			try{
				thing.doAction("doTask", actionContext);
				test.successed(System.currentTimeMillis() - start);
			}catch(Throwable e){
				test.exception(System.currentTimeMillis() - start);
				e.printStackTrace();
			}
		}
	}
	
}
