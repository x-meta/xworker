package xworker.dataObject.swt.bind.reflect;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;

public class AppendItem extends ReflectItem {
	StringBuilder sb = new StringBuilder();
	
	public AppendItem(Thing thing,  Widget widget, ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		super(thing, widget, actionContext, "getText", "append", String.class);
	}
	
	@Override
	public void changed(DataObjectBinder binder, DataObject dataObject) {
		Object value = super.getValue(binder, dataObject);
		
		if(value != null) {
			synchronized(sb) {
				sb.append(String.valueOf(value));
			}
		}
	}

	@Override
	public Object getValue(DataObjectBinder binder, DataObject dataObject) {
		synchronized(sb) {
			String str = sb.toString();
			sb.delete(0, sb.length());
			return str;
		}
	}

	public static void create(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new AppendItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}		
	}

}
