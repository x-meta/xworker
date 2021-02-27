/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;

public class TaskMonitor implements Runnable{
	private static final String TAG = TaskMonitor.class.getName();
	private static final String monitor_name = "___monitor___";
	
	/** 进度条，显示进度 */
	ProgressBar progressBar;
	
	/** 标签，显示文本 */
	Label label;

	/** 任务列表 */
	List<Thing> tasks = new ArrayList<Thing>();
	
	/** 任务的总数量 */
	int taskTotalCount = 0;
	
	/** 任务已经执行的数量 */
	int taskRunnedCount = 0;
	
	/** 是否已经结束 */
	boolean finished = false;
	
	/** 任务监控的事物定义 */
	Thing thing;
	
	/** 创建监控时的变量上下文 */
	ActionContext actionContext;
	
	/** 是否是动态任务 */
	boolean dynamicTask;
	
	public TaskMonitor(Thing thing, List<Thing> tasks, ProgressBar progressBar, Label label, ActionContext actionContext){
		this.thing = thing;
		this.tasks = tasks;
		this.progressBar = progressBar;
		this.label = label;
		this.actionContext = actionContext;
		
		taskTotalCount = tasks.size();
		dynamicTask = thing.getBoolean("dynamicTask");
		if(!dynamicTask){
			setProgressBarMaximun(tasks.size());
		}
		
		Thread th = new Thread(this, thing.getMetadata().getName());
		th.setDaemon(true);
		th.start();
	}
	
	public synchronized void addRunnedTaskCount(){
		taskRunnedCount = taskRunnedCount + 1;
	}
	
	public synchronized void setProgressBarSelection(boolean last){
		if(!dynamicTask && progressBar != null && !progressBar.isDisposed()){
			progressBar.getDisplay().asyncExec(new Runnable(){
				public void run(){
					progressBar.setSelection(taskRunnedCount);
					
				}
			});
		}
	}
	
	public void setProgressBarMaximun(final int count){
		if(!dynamicTask && progressBar != null && !progressBar.isDisposed()){
			progressBar.getDisplay().asyncExec(new Runnable(){
				public void run(){
					progressBar.setMaximum(count);					
				}
			});
		}
	}
	
	public boolean addTask(Thing task){
		synchronized(tasks){
			if(finished){
				return false;
			}
			
			tasks.add(task);
			taskTotalCount++;
			setProgressBarMaximun(taskTotalCount);
			
			return true;
		}
	}
	
	public boolean addTasks(List<Thing> tasks){
		synchronized(tasks){
			if(finished){
				return false;
			}
			
			tasks.addAll(tasks);
			taskTotalCount += tasks.size();
			setProgressBarMaximun(taskTotalCount);
			
			return true;
		}
	}

	public void removeTask(Thing task){
		synchronized(tasks){
			if(finished){
				return;
			}
			
			boolean removed = tasks.remove(task);
			if(removed){
				taskTotalCount--;
				setProgressBarMaximun(taskTotalCount);
			}
		}
	}
	
	public void removeTasks(List<Thing> tasks){
		synchronized(tasks){
			if(finished){
				return;
			}
			
			for(Thing task : tasks){
				boolean removed = tasks.remove(task);
				if(removed){
					taskTotalCount--;					
				}
			}
			
			setProgressBarMaximun(taskTotalCount);
		}
	}
	
	public void setLabel(final String text){
		if(text == null || label == null || label.isDisposed()){
			return;
		}
		
		label.getDisplay().asyncExec(new Runnable(){
			public void run(){
				label.setText(text);
			}
		});		
	}
	
	@Override
	public void run() {
		for(int i=0; i<tasks.size(); i++){			
			synchronized(tasks){
				if(i >= tasks.size()){
					break;
				}
			
				Thing task = tasks.get(i);
				try{
					//显示标签
					if(label != null && !label.isDisposed()){
						String text = (String) task.doAction("getLabel", actionContext);
						if(text == null){
							text = "";
						}
						setLabel(text);		
					}
					
					//先从列表中移除任务
					tasks.remove(i);
					i--;
					
					//执行任务
					task.doAction("run", actionContext);									
				}catch(Throwable t){
					Executor.error(TAG, "Runn tasks error, task=" + task, t);
				}finally{
					if(label != null && !label.isDisposed()){
						String text = (String) task.doAction("getLabel", actionContext);
						if(text == null){
							text = "";
						}
						setLabel(text + " task finished");		
					}
					
					addRunnedTaskCount();
					
					//设置进度条的值
					setProgressBarSelection(i == tasks.size() - 1);
				}
			}
		}
		
		finished = true;
		String finishLabel = (String) thing.doAction("getFinishLabel", actionContext);
		if(finishLabel != null){
			setLabel(finishLabel);
		}
		
		thing.doAction("onFinished", actionContext);
	}
	
	/**
	 * 创建。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//任务列表
		List<Thing> tasks = new ArrayList<Thing>();
		Thing tasksThing = self.getThing("Tasks@0");
		if(tasksThing != null){
			tasks.addAll(tasksThing.getChilds());
		}
		
		ThingCompositeCreator sc = SwtUtils.createCompositeCreator(self, actionContext);
		sc.setCompositeThing(World.getInstance().getThing("xworker.swt.util.prototypes.TaskMonitor/@mainComposite"));
		Composite composite = sc.create();

		ActionContext ac = sc.getNewActionContext();
		
		//进度条和标签
		ProgressBar progressBar = ac.getObject("progressBar");
		Label label = ac.getObject("label");
		
		//创建实例
		TaskMonitor monitor = new TaskMonitor(self, tasks, progressBar, label, actionContext);		
		//保存变量
		actionContext.getScope(0).put(self.getMetadata().getName(), monitor);
		
		return composite;
	}
	
	public static Object addTask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing task = (Thing) actionContext.get("task");
		
		TaskMonitor monitor = (TaskMonitor) self.getData(TaskMonitor.monitor_name);
		if(task != null){
			return monitor.addTask(task);
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object addTasks(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<Thing> tasks = (List<Thing>) actionContext.get("tasks");
		
		TaskMonitor monitor = (TaskMonitor) self.getData(TaskMonitor.monitor_name);
		if(tasks != null){
			return monitor.addTasks(tasks);
		}else{
			return false;
		}
	}
	
	public static void removeTask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing task = (Thing) actionContext.get("task");
		
		TaskMonitor monitor = (TaskMonitor) self.getData(TaskMonitor.monitor_name);
		if(task != null){
			monitor.removeTask(task);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void removeTasks(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<Thing> tasks = (List<Thing>) actionContext.get("tasks");
		
		TaskMonitor monitor = (TaskMonitor) self.getData(TaskMonitor.monitor_name);
		if(tasks != null){
			monitor.removeTasks(tasks);			
		}
	}
	
	public static Object getFinishLabel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return UtilString.getString(self, "finishLabel", actionContext);
	}
}