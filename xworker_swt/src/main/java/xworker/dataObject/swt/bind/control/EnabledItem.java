package xworker.dataObject.swt.bind.control;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;

public class EnabledItem  extends WidgetBinderItem{
	Boolean defaultEnabled;
	
	public EnabledItem(Thing thing, Widget widget, ActionContext actionContext) {
		super(thing, widget, actionContext);
		
		Control control = (Control) widget;
		defaultEnabled = control.getEnabled();
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		Control control = (Control) widget;
		
		//Boolean newBoolean = null;
		if(value instanceof Boolean) {
			control.setEnabled((Boolean) value);		
		}else {
			Boolean enabled = UtilData.getBoolean(value, defaultEnabled);
			control.setEnabled(enabled);					
		}

	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
	}
	
	@Override
	public Object getValue() {
		Control control = (Control) widget;
		return control.getEnabled();
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new EnabledItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}
	}

}
