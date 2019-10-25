package xworker.app.view.swt.widgets.prototype;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataObjectEditFormShell {
	public static void saveButtonGroovy(ActionContext actionContext) {
		Thing form = actionContext.getObject("form");
		form.doAction("updateDataObject", actionContext);
	}
}	
