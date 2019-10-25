package xworker.ide.task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.task.UserTaskManager;
import xworker.task.UserTaskManagerListener;

public class IDEUserTaskManager {
	
	/**
	 * 初始化。
	 * 
	 * @param actionContext
	 */
	public static void init(ActionContext actionContext){
		final Table table = (Table) actionContext.get("taskTable");
		final UserTaskListener taskListener = new UserTaskListener(){
			@Override
			public void started(UserTask task) {
				doUpdateStatus(task, table);
			}

			@Override
			public void finished(UserTask task) {
				doFinish(task, table);
			}

			@Override
			public void progressSetted(UserTask task, int progress) {
				doUpdateStatus(task, table);
			}

			@Override
			public void currentLabelUpdated(UserTask task, String label) {
				doUpdateStatus(task, table);
			}
			
			
		};
		
		UserTaskManagerListener listener = new UserTaskManagerListener(){
			@Override
			public void taskAdded(final UserTask task) {
				if(!table.isDisposed()){
					table.getDisplay().asyncExec(new Runnable(){
						public void run(){
							if(!isAccept(task, table)){
								return;
							}
							
							createTableItem(task, table);
							
							task.addListener(taskListener);
						}
					});
				}
			}			
		};
		
		table.setData("managerListener", listener);
		table.setData("taskListener", taskListener);
		
		for(UserTask task : UserTaskManager.getTasks()){
			createTableItem(task, table);
			
			//task.addListener(taskListener);
		}
		
		UserTaskManager.addUserTaskListener(taskListener);
		UserTaskManager.addListener(listener);
		
	}
	
	public static boolean isAccept(UserTask task, Table table){
		Thing thing = (Thing) table.getData("tableThing");
		if(thing == null){
			return true;
		}
		
		ActionContext ac = (ActionContext) table.getData("actionContext");
		return UtilData.isTrue(thing.doAction("accept", ac, "userTask", task));
	}
	
	public static void doFinish(final UserTask task, final Table table){
		if(!table.isDisposed()){
			table.getDisplay().asyncExec(new Runnable(){
				public void run(){
					if(!isAccept(task, table)){
						return;
					}
					for(TableItem item : table.getItems()){
						if(task == item.getData()){
							TableEditor editor = (TableEditor) item.getData("progressEditor");
							ProgressBar progressBar = (ProgressBar) item.getData("progressBar");
							
							if(progressBar != null && !progressBar.isDisposed()){
								progressBar.dispose();
								editor.dispose();
							}
							
							item.dispose();
							break;
						}
					}
					
					//处理一个条目删除后，下面的条目的进度条不前移的问题
					table.pack();
					table.getParent().layout();
				}
			});
		}
	}
	
	public static void doUpdateStatus(final UserTask task, final Table table){
		if(!table.isDisposed()){
			table.getDisplay().asyncExec(new Runnable(){
				public void run(){
					if(!isAccept(task, table)){
						return;
					}
					
					for(TableItem item : table.getItems()){
						if(task == item.getData()){
							updateTableItem(task, item);
							break;
						}
					}
				}
			});
		}
	}
	
	public static void dispose(ActionContext actionContext){
		final Table table = (Table) actionContext.get("taskTable");
		UserTaskManagerListener managerListener = (UserTaskManagerListener) table.getData("managerListener");
		if(managerListener != null){
			UserTaskManager.removeListener(managerListener);
		}
		
		UserTaskListener taskListener = (UserTaskListener) table.getData("taskListener");
		if(taskListener != null){
			for(UserTask task : UserTaskManager.getTasks()){
				task.removeListener(taskListener);
			}
			
			UserTaskManager.removeUserTaskListener(taskListener);
		}
	}
	
	public static void createTableItem(UserTask task, Table table){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setData(task);
		
		updateTableItem(task, item);
	}
	
	public static void updateTableItem(UserTask task, TableItem item){
		//更新提示
		String[] texts = new String[4];
		texts[0] = task.getTaskThing().getMetadata().getLabel();
		switch(task.getStatus()){
		case UserTask.CANCEL:
			texts[1] = "取消中";
			break;
		case UserTask.FINISHED:
			texts[1] = "已结束";
			break;
		case UserTask.RUNING:
			texts[1] = "执行中";
			break;
		case UserTask.TERMINATED:
			texts[1] = "已终止";
			break;
		case UserTask.WAIT:
			texts[1] = "等待中";
			break;
			default:
				texts[1] = "未知";
		}
		texts[2] = task.getCurrentLabel();
		
		//更新进度条
		updateProgress(task, item);
		
		texts[3] = "";
		item.setText(texts);
	}
	
	public static void updateProgress(UserTask task, TableItem item){
		if(task.getStatus() == UserTask.CANCEL || task.getStatus() == UserTask.RUNING){
			//如果正在执行，那么显示进度条
			TableEditor editor = (TableEditor) item.getData("progressEditor");
			ProgressBar progressBar = (ProgressBar) item.getData("progressBar");
			if(progressBar == null || progressBar.isDisposed()){
				if(editor != null){
					editor.dispose();
				}
				
				int style = SWT.HORIZONTAL | SWT.SMOOTH;
				if(!task.isProgressAble()){
					style = style | SWT.INDETERMINATE;
				}
				progressBar = new ProgressBar(item.getParent(), style);
				progressBar.setMaximum(100);
				progressBar.setSelection(task.getProgress());
				
				editor = new TableEditor(item.getParent());
				editor.grabHorizontal = editor.grabVertical = true;
				editor.setEditor(progressBar, item, 3);
				
				item.setData("progressEditor", editor);
				item.setData("progressBar", progressBar);
			}
			
			progressBar.setSelection(task.getProgress());
		}else{
			TableEditor editor = (TableEditor) item.getData("progressEditor");
			ProgressBar progressBar = (ProgressBar) item.getData("progressBar");
			
			if(progressBar != null && !progressBar.isDisposed()){
				progressBar.dispose();
				editor.dispose();
			}
		}
	}
}
