package xworker.swt.interactive.design;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ReplaceButton {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator creator = SwtUtils.createCompositeCreator(self, actionContext);
		creator.setCompositeThing(World.getInstance().getThing("xworker.swt.interactive.design.prototypes.ReplaceButton/@replaceButton"));
		
		Button button = (Button) creator.create();
		button.setText(self.getMetadata().getLabel());
		actionContext.put(self.getMetadata().getName(), button);
		
		return button;
	}
	
	public static void buttonSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Control control = (Control) event.widget;
		Thing thing = Designer.getThing(control);
		ActionContext ac = Designer.getActionContext(control);
		
		if(thing != null) {
			Thing replaceThing = thing.doAction("getWidget", ac);
			if(replaceThing != null) {
				DesignTools.insert(control, replaceThing, DesignTools.REPLACE);
			}
		}
	}
}
