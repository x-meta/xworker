package xworker.swt.interactive.design;

import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.DesignTools;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ReplaceList {
	private static final String DATA = "__ReplaceList_Datas__";
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator creator = SwtUtils.createCompositeCreator(self, actionContext);
		creator.setCompositeThing(World.getInstance().getThing("xworker.swt.interactive.design.prototypes.ReplaceListShell/@list"));
		
		org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) creator.create();
		
		List<Thing> widgets = self.doAction("getWidgets", actionContext);
		if(widgets != null) {
			for(Thing widget : widgets) {
				list.add(widget.getMetadata().getLabel());
			}
			
			list.setData(DATA, widgets);
		}
		
		actionContext.g().put(self.getMetadata().getName(), list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static void listSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		org.eclipse.swt.widgets.List control = (org.eclipse.swt.widgets.List) event.widget;
		int index = control.getSelectionIndex();
		
		List<Thing> widgets = (List<Thing>) control.getData(DATA);
		Thing replaceThing = widgets.get(index);
		
		if(replaceThing != null) {
			DesignTools.insert(control, replaceThing, DesignTools.REPLACE);
		}
	}
}
