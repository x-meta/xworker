package xworker.task.segment;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SegmentTaskManager {
	private static Map<String, SegmentTask> tasks = new HashMap<String, SegmentTask>();
	
	/**
	 * 返回任务列表中是否已经有了该事物的任务。
	 * 
	 * @param thing
	 * @return
	 */
	public static SegmentTask getTask(Thing thing){
		String path = thing.getMetadata().getPath();
		
		SegmentTask task = tasks.get(path);
		return task;
	}
	
	/**
	 * 把分段任务放入到管理器中，只有分段任务为空时才能放入。
	 * 
	 * @param thing
	 * @param task
	 */
	public static void putTask(Thing thing, SegmentTask task){
		String path = thing.getMetadata().getPath();
		
		if(tasks.get(path) == null){
			tasks.put(path, task);
		}
	}
	
	public synchronized static SegmentTask getTask(Thing thing, ActionContext actionContext){
		String path = thing.getMetadata().getPath();
		
		SegmentTask task = tasks.get(path);
		if (task == null){
			task = new SegmentTask(thing, actionContext);
			tasks.put(path, task);
		}
		
		return task;
	}
	
	public static void removeTask(Thing thing){		
		SegmentTask task = tasks.remove(thing.getMetadata().getPath());
		if(task != null){
			task.stop();
		}
	}
}
