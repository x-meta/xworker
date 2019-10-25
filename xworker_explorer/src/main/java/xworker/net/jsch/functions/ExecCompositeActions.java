package xworker.net.jsch.functions;

import org.eclipse.swt.custom.StyledText;
import org.xmeta.ActionContext;

import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;

public class ExecCompositeActions {
	public static void init(ActionContext actionContext){
		final UIRequest request = (UIRequest) actionContext.get("request");
		UserTask task = (UserTask) request.getData("task");
		final StyledText text = (StyledText) actionContext.get("text");
		task.addListener(new Listener(request, text));
	}
	
	static class Listener implements UserTaskListener{
		UIRequest request;
		StyledText text;
		
		public Listener(UIRequest request, StyledText text){
			this.request = request;
			this.text = text;
		}
		
		@Override
		public void started(UserTask task) {
		}

		@Override
		public void finished(UserTask task) {
			final FunctionRequest fnRequest = (FunctionRequest) request.getRequestMessage();
			if(text.isDisposed()){
				task.cancel();
				FunctionRequestUtil.callbakMyselfCancel(fnRequest, null, fnRequest.getActionContext());
			}else{
				text.getDisplay().asyncExec(new Runnable(){
					public void run(){
						String result = text.getText();
						
						FunctionRequestUtil.callbakMyselfOk(fnRequest, result, fnRequest.getActionContext());
					}
				});		
			}
		}

		@Override
		public void progressSetted(UserTask task, int progress) {
		}

		@Override
		public void currentLabelUpdated(UserTask task, String label) {
		}
	}
}
