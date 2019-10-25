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

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

public class TaskMonitor implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(TaskMonitor.class);
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
					logger.error("Runn tasks error, task=" + task, t);
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
	public static Thing create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//任务列表
		List<Thing> tasks = new ArrayList<Thing>();
		Thing tasksThing = self.getThing("Tasks@0");
		if(tasksThing != null){
			tasks.addAll(tasksThing.getChilds());
		}
		
		//进度条和标签
		String progressBarVarName = self.getStringBlankAsNull("progressBarVarName");
		ProgressBar progressBar = null;
		if(progressBarVarName != null){
			progressBar = (ProgressBar) actionContext.get(progressBarVarName);
		}
		String labelVarName = self.getStringBlankAsNull("labelVarName");
		Label label = null;
		if(labelVarName != null){
			label = (Label) actionContext.get(labelVarName);
		}
		
		//创建新的事物
		Thing thing = new Thing();
		thing.put("descriptors", self.getMetadata().getPath()); //设置原来的事物为描述者，用于继承行为
		
		//创建实例
		TaskMonitor monitor = new TaskMonitor(self, tasks, progressBar, label, actionContext);		
		thing.setData(TaskMonitor.monitor_name, monitor);
		
		//保存变量
		actionContext.getScope(0).put(self.getMetadata().getName(), thing);
		
		return thing;
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