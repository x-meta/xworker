package xworker.swt.xworker;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ide.utils.ThingGuide;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ThingEditorGuide {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//创建控件
		ThingCompositeCreator creator = SwtUtils.createCompositeCreator(self, actionContext);
		creator.setCompositeThing(World.getInstance().getThing("xworker.swt.xworker.prototype.ThingEditorGuide/@guideComposite"));
		Composite composite = creator.create();
		
		//设置要编辑的事物
		ActionContext ac = creator.getNewActionContext();
		Thing thing = self.doAction("getThing", actionContext);
		
		if(thing == null) {
			String descriptor = self.doAction("getDescriptor", actionContext);
			thing = new Thing(descriptor);
		}
		
		ThingGuide thingGuide = new ThingGuide(self, thing, World.getInstance().getThing("xworker.lang.util.ThingGuideSelector"), ac);
		actionContext.g().put(self.getMetadata().getName(), thingGuide);
		
		//guideCancelButton应该是不需要的
		((Button) ac.get("guideCancelButton")).dispose();
		composite.layout();
		return composite;
	}
}
