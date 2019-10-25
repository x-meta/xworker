package xworker.swt.xworker.dialogs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.xmeta.ActionContext;

import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.task.UserTaskManager;
import xworker.task.UserTaskManagerListener;

public class UserTaskDialog {
	public static void initTextDialog(ActionContext actionContext){
		StyledText text = actionContext.getObject("text");
		UserTask userTask = actionContext.getObject("userTask");
		
		TextUserTaskListener listener = new TextUserTaskListener(text, actionContext);
		if(userTask != null){
			userTask.addListener(listener);
		}else{
			UserTaskManager.addUserTaskListener(listener);
			text.addDisposeListener(new TaskDisposeListener(null, listener));
		}
	}
	
	public static class TaskDisposeListener implements DisposeListener{
		TextUserTaskListener taskListener;
		UserTaskManagerListener listener;
		
		public TaskDisposeListener(UserTaskManagerListener listener, TextUserTaskListener taskListener){
			this.listener = listener;
			this.taskListener = taskListener;
		}
		
		@Override
		public void widgetDisposed(DisposeEvent arg0) {
			if(listener != null){
				UserTaskManager.removeListener(listener);
			}
			
			if(taskListener != null){
				UserTaskManager.removeUserTaskListener(taskListener);
			}
		}
	}
	
	public static class TextUserTaskListener implements UserTaskListener{
		StyledText text;
		ActionContext actionContext;
		
		public TextUserTaskListener(StyledText text, ActionContext actionContext){
			this.text = text;
			this.actionContext = actionContext;
		}
		
		@Override
		public void started(UserTask task) {
			this.append(task.getLabel() + " started");
		}

		@Override
		public void finished(UserTask task) {
			this.append(task.getLabel() + " finished");
			
			final Button closeButton = (Button) actionContext.getObject("closeButton");
			if(closeButton != null && !closeButton.isDisposed()){
				closeButton.getDisplay().asyncExec(new Runnable(){
					public void run(){
						closeButton.setEnabled(true);
					}
				});
				
			}
		}

		@Override
		public void progressSetted(UserTask task, int progress) {
			this.append(task.getLabel() + " " + progress + "%");
		}

		@Override
		public void currentLabelUpdated(UserTask task, String label) {
			this.append(task.getLabel() + " " + label);
		}
		
		public void append(final String str){
			text.getDisplay().asyncExec(new Runnable(){
				public void run(){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					text.append(sf.format(new Date()));
					text.append(" ");
					text.append(str);
					text.append("\n");
					
					//滚动到底部
					//text.setCaretOffset(text.getText().length());
					text.setSelection(text.getText().length());
				}
			});
			
		}
	}
}
