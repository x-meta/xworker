package xworker.ui.function.xworker.explorer;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.util.XWorkerUtils;

public class ExplorerActions {
	public static Shell getExplorerShell(ActionContext actionContext){
		return (Shell) XWorkerUtils.getIDEShell();
	}
}
