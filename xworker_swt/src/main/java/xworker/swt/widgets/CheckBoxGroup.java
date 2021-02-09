package xworker.swt.widgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class CheckBoxGroup {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.addChildFilter("Composite");
		
		return null;
	}
}
