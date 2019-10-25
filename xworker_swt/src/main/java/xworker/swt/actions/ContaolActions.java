package xworker.swt.actions;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class ContaolActions {
	public static void setVisiable(ActionContext actionContext)
			throws OgnlException {
		Thing self = (Thing) actionContext.get("self");

		String method = self.getString("method");
		if (method == null || "".equals(method)) {
			method = "enable";
		}

		String controlList = self.getString("controlList");
		if (controlList == null || "".equals(controlList)) {
			return;
		}

		for (String controlName : controlList.split("[,]")) {
			Object c = OgnlUtil.getValue(controlName, actionContext);
			if(c instanceof Control){
				final Control control = (Control) c;
				if (control != null) {
					final String m = method;
					control.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if ("true".equals(m)) {
								control.setVisible(true);
							} else if ("false".equals(m)) {
								control.setVisible(false);
							} else if ("reverse".equals(m)) {
								control.setVisible(!control.getEnabled());
							}
						}
					});
				}
			}else if(c instanceof Menu){
				final Menu item = (Menu) c;
				final String m = method;
				item.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if ("true".equals(m)) {
							item.setVisible(true);
						} else if ("false".equals(m)) {
							item.setVisible(false);
						} else if ("reverse".equals(m)) {
							item.setVisible(!item.getEnabled());
						}
					}
				});
			}
		}
	}

}
