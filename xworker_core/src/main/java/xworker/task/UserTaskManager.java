package xworker.task;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionException;
import org.xmeta.Thing;

/**
 * 任务监控模块。
 * 
 * @author Administrator
 *
 */
public class UserTaskManager {
	private static List<UserTask> tasks = new ArrayList<UserTask>();
	private static List<UserTaskListener> userTaskListeners = new ArrayList<UserTaskListener>();
	
	private static UserTaskListener listener = new UserTaskListener(){

		@Override
		public void started(UserTask task) {
			for(UserTaskListener lis : userTaskListeners){
				lis.started(task);
			}
		}

		@Override
		public void finished(UserTask task) {
			synchronized(tasks){
				tasks.remove(task);
			}
			
			for(UserTaskListener lis : userTaskListeners){
				lis.finished(task);
			}
		}

		@Override
		public void progressSetted(UserTask task, int progress) {
			for(UserTaskListener lis : userTaskListeners){
				lis.progressSetted(task, progress);
			}
		}

		@Override
		public void currentLabelUpdated(UserTask task, String label) {
			for(UserTaskListener lis : userTaskListeners){
				lis.currentLabelUpdated(task, label);
			}
		}
		
	};
	
	/** 事件监听列表 */
	private static List<UserTaskManagerListener> listeners = new ArrayList<UserTaskManagerListener>();
	
	/**
	 * 获取所有监控任务列表。
	 * 
	 * @return
	 */
	public static List<UserTask> getTasks(){
		return tasks;
	}
	
	public static void addListener(UserTaskManagerListener listener){
		synchronized(listeners){
			listeners.add(listener);
		}
	}
	
	public static void removeListener(UserTaskManagerListener listener){
		synchronized(listeners){
			listeners.remove(listener);
		}
	}
	
	public static void addUserTaskListener(UserTaskListener listener){
		synchronized(userTaskListeners){
			userTaskListeners.add(listener);
		}
	}
	
	public static void removeUserTaskListener(UserTaskListener listener){
		synchronized(userTaskListeners){
			userTaskListeners.remove(listener);
		}
	}
	
	/**
	 * 创建一个监控任务。
	 * 
	 * @param taskThing
	 * @param progressAble
	 * @return
	 */
	public static UserTask createTask(Thing taskThing, boolean progressAble){
		if(taskThing == null){
			throw new ActionException("taskThing can not be null");
		}
		
		UserTask task = new UserTask(taskThing, progressAble);
		task.addListener(listener);
		synchronized(tasks){
			tasks.add(task);
			
			synchronized(listeners){
				for(UserTaskManagerListener lis : listeners){
					lis.taskAdded(task);
				}
			}
		}
		
		return task;
	}
	
	/**
	 * 强制删除一个监控任务。
	 * 
	 * @param task
	 */
	public static void removeTask(UserTask task){
		synchronized(tasks){			
			tasks.remove(task);
		}
	}
	
	public static void setUserTaskLabelDetail(UserTask userTask, String label, String detail){
		if(userTask != null){
			userTask.setLabel(label);
			userTask.setDetail(detail);
		}
	}
	
	public static void setUserTaskData(UserTask userTask, String key, Object value){
		if(userTask != null){
			userTask.setData(key, value);
		}
	}
}
