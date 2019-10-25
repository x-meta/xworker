package xworker.dataObject.swt.bind.reflect;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;

public class BackgroundModeItem extends ReflectItem {

	public BackgroundModeItem(Thing thing,  Widget widget, ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		super(thing, widget, actionContext, "getBackgroundMode", "setBackgroundMode", int.class);
	}
	
	public static void create(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new BackgroundModeItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}
	}

}
