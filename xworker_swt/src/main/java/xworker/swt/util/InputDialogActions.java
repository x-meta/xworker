package xworker.swt.util;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

public class InputDialogActions {
	public static void inputInit(ActionContext actionContext){
		Shell shell = actionContext.getObject("shell");
		Label messageLabel = actionContext.getObject("messageLabel");
		Text text = actionContext.getObject("text");
		
		String title = actionContext.getString("title");
		String message = actionContext.getString("message");
		if(title != null){
			shell.setText(title);
		}
		
		if(message != null){
			messageLabel.setText(message);
		}else{
			messageLabel.dispose();
		}
		
		String value = actionContext.getString("value");
		if(value != null){
			text.setText(value);
		}
	}
	
	public static void inputOkButton(ActionContext actionContext){
		Shell shell = actionContext.getObject("shell");
		Text text = actionContext.getObject("text");
		
		actionContext.getScope(0).put("result", text.getText());
		shell.dispose();		
	}
}
