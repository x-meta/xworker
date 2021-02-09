package xworker.swt.data;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface InputCreator {
	public Input create(Object control, Thing thing, ActionContext actionContext);
}
