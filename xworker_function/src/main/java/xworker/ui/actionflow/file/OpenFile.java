package xworker.ui.actionflow.file;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.actionflow.ActionFlowRunner;

public class OpenFile {
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionFlowRunner runner = ActionFlowRunner.getActionFlowRunner(actionContext);
		FileDialog dialog = new FileDialog(runner.getRequestUI().getShell(), SWT.OPEN);
		String fileName = dialog.open();
		if(fileName != null){
			runner.actionFinished(self, new File(fileName));
		}
	}
}
