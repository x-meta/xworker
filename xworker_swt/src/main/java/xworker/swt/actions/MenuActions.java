package xworker.swt.actions;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class MenuActions {
	public static void runShowMenuByEventWidgets(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Event event = (Event) actionContext.get("event");
		Widget item = (Widget) event.widget;
		
		Rectangle rect = getBounds(item);
		
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = getParent(item).toDisplay(pt);

		String menuVar = self.getStringBlankAsNull("menuVar");
		Menu menu = null;
		if(menuVar != null) {
			menu = (Menu) actionContext.get(menuVar);			
		}else {
			Thing menuThing = self.doAction("getMenuThing", actionContext);
			String key = "__runShowMenuByEventWidgets__";
			menu = (Menu) item.getData(key);
			if(menuThing != null) {
				boolean dynamic = self.doAction("isDynamic", actionContext);
				if(dynamic || menu == null || menu.isDisposed()) {
					if(menu != null && menu.isDisposed() == false) {
						menu.dispose();
					}
					
					ActionContext ac = actionContext;
					if(UtilData.isTrue(self.doAction("isNewActionContext", actionContext))) {
						ac = new ActionContext();
						ac.put("parentContext", actionContext);
					}
					ac.peek().put("parent", item);
					menu = menuThing.doAction("create", ac);
					item.setData(key, menu);
				}
			}
		}
		
		if(menu != null){
		    menu.setLocation(pt.x, pt.y);
		    //menu.update();
		    menu.setVisible(true);
		}
	}
	
	public static Control getParent(Widget item){
		if(item instanceof Control){
			return ((Control) item).getParent();
		}else if(item instanceof ToolItem){
			return ((ToolItem) item).getParent();
		}else if(item instanceof CoolItem){
			return ((CoolItem) item).getParent();
		}else{
			throw new ActionException("Can not get parent from type " + item.getClass());
		}
	}
	
	public static Rectangle getBounds(Widget item){
		if(item instanceof Control){
			return ((Control) item).getBounds();
		}else if(item instanceof ToolItem){
			return ((ToolItem) item).getBounds();
		}else if(item instanceof CoolItem){
			return ((CoolItem) item).getBounds();
		}else{
			throw new ActionException("Can not get bounds from type " + item.getClass());
		}
	}
}
