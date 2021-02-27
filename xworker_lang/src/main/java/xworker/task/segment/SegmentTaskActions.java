package xworker.task.segment;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class SegmentTaskActions {
	//private static Logger logger = LoggerFactory.getLogger(SegmentTaskActions.class);
	private static final String TAG = SegmentTaskActions.class.getName();
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SegmentTask task = SegmentTaskManager.getTask(self, actionContext);
		task.start();
	}
	
	public static void stop(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SegmentTask task = SegmentTaskManager.getTask(self, actionContext);
		if(task != null){
			task.stop();
		}
	}
	
	public static void remove(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		SegmentTaskManager.removeTask(self);
	}
	
	public static void onRangeSuccess(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		
		Executor.info(TAG, self.getMetadata().getName() + ": Range[" + range.start + "-" + range.end + "] successed");
	}
	
	public static void onRangeException(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		Exception e = (Exception) actionContext.get("exception");
		
		Executor.info(TAG, self.getMetadata().getName() + ": Range[" + range.start + "-" + range.end + "]", e);
	}
	
	public static void onRangeFailure(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		
		Executor.info(TAG, self.getMetadata().getName() + ": Range[" + range.start + "-" + range.end + "] failed");
	}
	
	public static void onRanageTimeout(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		
		Executor.info(TAG, self.getMetadata().getName() + ": Range[" + range.start + "-" + range.end + "] timeouted");
	}
	
	public static void onException(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		Exception e = (Exception) actionContext.get("exception");
		
		Executor.info(TAG, self.getMetadata().getName() + ": exception, " + e.getLocalizedMessage());
	}
	
	public static void onFinished(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//Range range = (Range) actionContext.get("range");
		//Callable<Boolean> callable = (Callable<Boolean>) actionContext.get("callable");
		//SegmentTask task = (SegmentTask) actionContext.get("task");
		
		Executor.info(TAG, self.getMetadata().getName() + ": is finished");
	}
	
	public static Thing getRangeTaskThing(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return self;
	}
}
