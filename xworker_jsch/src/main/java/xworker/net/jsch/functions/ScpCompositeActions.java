package xworker.net.jsch.functions;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.xmeta.ActionContext;

import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;

public class ScpCompositeActions {
	public static void init(ActionContext actionContext){
		final UIRequest request = (UIRequest) actionContext.get("request");
		UserTask task = (UserTask) request.getData("task");
		final ProgressBar progressBar = (ProgressBar) actionContext.get("progressBar");
		final Label info = (Label) actionContext.get("info");
		task.addListener(new Listener(request, progressBar, info));
	}

	static class Listener implements UserTaskListener{
		UIRequest request;
		ProgressBar progressBar;
		Label info;
		
		public Listener(UIRequest request, ProgressBar progressBar, Label info){
			this.request = request;
			this.progressBar = progressBar;
			this.info = info;
		}
		
		@Override
		public void started(UserTask task) {
		}

		@Override
		public void finished(UserTask task) {
			final FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
			if(progressBar.isDisposed()){
				task.cancel();
				FunctionRequestUtil.callbakMyselfCancel(fnRequest, null, fnRequest.getActionContext());
			}else{
				if(task.isTerminated()){
					FunctionRequestUtil.callbakMyselfOk(fnRequest, false, fnRequest.getActionContext());
				}else{
					FunctionRequestUtil.callbakMyselfOk(fnRequest, true, fnRequest.getActionContext());
				}
			}
		}

		@Override
		public void progressSetted(final UserTask task, final int progress) {
			if(progressBar.isDisposed()){
				return;
			}
			
			progressBar.getDisplay().asyncExec(new Runnable(){
				public void run(){
					progressBar.setSelection(progress);
				}
			});
		}
		
		@Override
		public void currentLabelUpdated(final UserTask task, final String label) {
			if(info.isDisposed()){
				return;
			}
			
			info.getDisplay().asyncExec(new Runnable(){
				public void run(){
					info.setText(label);
				}
			});
		}
	}
}
