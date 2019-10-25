package xworker.app.view.swt.data.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.util.SwtUtils;

public class AddChildDataObjectDialog {
	public static void okButtonAction(ActionContext actionContext){
		Thing form = (Thing) actionContext.get("form");
		Shell shell = (Shell) actionContext.get("shell");
		
		Object dataObject = form.doAction("getDataObject", actionContext);
		actionContext.getScope(0).put("result", dataObject);
		shell.dispose();
	}
	
	public static void setDataObject(ActionContext actionContext){
		Thing form = (Thing) actionContext.get("form");
		Shell shell = (Shell) actionContext.get("shell");
		
		form.doAction("setDataObject", actionContext);

		if(actionContext.get("initValues") != null){
		    form.doAction("setValues", actionContext, UtilMap.toMap("values", actionContext.get("initValues")));
		}
		shell.pack();
		SwtUtils.centerShell(shell);
	}
	
	public static void setValues(ActionContext actionContext){
		Thing form = (Thing) actionContext.get("form");
		//取数据
		form.doAction("setValues", actionContext);
	}
}
