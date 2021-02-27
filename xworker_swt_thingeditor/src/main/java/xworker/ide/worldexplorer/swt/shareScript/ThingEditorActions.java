package xworker.ide.worldexplorer.swt.shareScript;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtTextUtils;

public class ThingEditorActions {
	public static void openDataListener(ActionContext actionContext) {
		World world = World.getInstance();
		
		Thing dialogObject = world.getThing("xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
		Shell newShell = dialogObject.doAction("create", actionContext);
		Shell shell = actionContext.getObject("shell");
		
		final Object text = actionContext.getObject("text");
		
		SwtDialog dialog = new SwtDialog(shell, newShell, actionContext);
		dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null) {
					SwtTextUtils.setText(text, String.valueOf(result));
				}
			}
			
		});		
	}
}
