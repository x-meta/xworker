package xworker.swt.rwt;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XRWT {
	public static void setAttribute(String key, Object value, ActionContext actionContext) {
		Thing swt = World.getInstance().getThing("xworker.swt.SWT");
		swt.doAction("rwtSetData", actionContext, "key", key, "value", value);
	}
	
	public static Object getAttribute(String key, ActionContext actionContext) {
		Thing swt = World.getInstance().getThing("xworker.swt.SWT");
		return swt.doAction("rwtGetData", actionContext, "key", key);
	}
}
