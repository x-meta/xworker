package xworker.swt.xworker.attributeEditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class TwoInputs {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String type = self.getString("type");
		Thing prototype = null;
		if("date".equals(type)) {
			prototype = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.prototypes.TwoInputsShell/@Composite1");
		}else {
			prototype = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.prototypes.TwoInputsShell/@Composite");
		}
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(prototype);		
		
		Object obj = cc.create();
		
		actionContext.g().put(self.getString("input1Name"), cc.getNewActionContext().get("input1"));
		actionContext.g().put(self.getString("input2Name"), cc.getNewActionContext().get("input2"));
		
		return obj;
	}
}
