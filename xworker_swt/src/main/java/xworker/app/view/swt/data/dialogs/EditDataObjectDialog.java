package xworker.app.view.swt.data.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.util.UtilData;

public class EditDataObjectDialog {
	public static void okButtonAction(ActionContext actionContext){
		Thing form = (Thing) actionContext.get("form");
		Shell shell = (Shell) actionContext.get("shell");
		Thing dataStore = (Thing) actionContext.get("dataStore");
		ActionContext parentContext = (ActionContext) actionContext.get("parentContext");

		Object result = form.doAction("getDataObject", actionContext);
		Object confirm = actionContext.get("confirm");
		String message = (String) actionContext.get("message");
		
		if(UtilData.isTrue(confirm) && message != null){
		    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK |SWT.CANCEL);
		    box.setText(shell.getText());
		    box.setMessage(message);
		    if(box.open() != SWT.OK){
		        return;
		    }
		}

		try{
		    result = dataStore.doAction("update", parentContext, UtilMap.toMap("record", result));
		}catch(Exception e){
		    MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		    box.setText(shell.getText());
		    box.setMessage(e.getMessage());
		    box.open();
		    return;
		}

		actionContext.getScope(0).put("result", result);
		shell.dispose();
	}
}
