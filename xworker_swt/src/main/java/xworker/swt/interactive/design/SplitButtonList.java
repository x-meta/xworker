package xworker.swt.interactive.design;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.DesignTools;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.swt.xwidgets.SplitButton;

public class SplitButtonList {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator creator = SwtUtils.createCompositeCreator(self, actionContext);
		creator.setCompositeThing(World.getInstance().getThing("xworker.swt.interactive.design.prototypes.ReplaceListShell/@splitButton"));
		
		SplitButton button = creator.create();
		
		List<Thing> widgets = self.doAction("getWidgets", actionContext);
		if(widgets != null) {
			Menu menu = new Menu(button);
			menu.setData(button);
						
			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					MenuItem item = (MenuItem) event.widget;
					Control control = (Control) item.getParent().getData();
					Thing thing = (Thing) item.getData();
					DesignTools.insert(control, thing, DesignTools.REPLACE);
				}
			};
			
			for(Thing widget : widgets) {
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(widget.getMetadata().getLabel());
				item.setData(widget);
				item.addListener(SWT.Selection, listener);
			}
			button.setMenu(menu);
		}
		
		actionContext.g().put(self.getMetadata().getName(), button);
		return button;
	}
}
