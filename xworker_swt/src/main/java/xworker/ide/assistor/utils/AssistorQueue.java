package xworker.ide.assistor.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助者的请求队列，辅助者可能会收到很多请求，但辅助者只处理最后一个请求。
 * 
 * @author Administrator
 *
 */
public class AssistorQueue implements Runnable{
	Object lockObj = new Object();
	
	List<AssistorQueueTask> tasks = new ArrayList<AssistorQueueTask>();
	
	public void addTask(AssistorQueueTask task){
		synchronized(lockObj){
			tasks.add(task);
		}
	}
	
	public void run(){
		AssistorQueueTask task = getLastTask();
		while(task != null){			
			task.doTask();
			task.setExecuted();
			
			AssistorQueueTask lastTask = getLastTask();
			if(lastTask != null){
				task = lastTask;
			}else{
				task.doNotify();
				return;
			}
		}
	}
	
	/**
	 * 返回追后一个请求任务。
	 * 
	 * @return
	 */
	private AssistorQueueTask getLastTask(){
		synchronized(lockObj){
			if(tasks.size() > 0){
				for(int i=0; i<tasks.size() - 2; i++){
					tasks.get(i).setExecuted();
				}
				
				AssistorQueueTask task = tasks.get(tasks.size() - 1);
				tasks.clear();
				return task;
			}else{
				return null;
			}
		}
	}
}
