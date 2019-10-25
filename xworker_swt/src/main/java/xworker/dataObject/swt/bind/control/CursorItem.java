package xworker.dataObject.swt.bind.control;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;
import xworker.swt.util.UtilSwt;

public class CursorItem extends WidgetBinderItem{
	Cursor cursor;
	Cursor defaultCursor;
	
	public CursorItem(Thing thing, Widget widget,  ActionContext actionContext) {
		super(thing, widget, actionContext);
		
		Control control = (Control) widget;
		defaultCursor = control.getCursor();
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		Control control = (Control) widget;
		Cursor oldCursor = cursor;		
		
		try {
			Cursor newCursor = null;
			if(value instanceof Cursor) {
				control.setCursor(newCursor);		
				cursor = null;
			}else if(value instanceof Integer) {
				Cursor sysCursor = control.getDisplay().getSystemCursor((Integer) value);
				if(sysCursor != null) {
					control.setCursor(sysCursor);
					cursor = null;
				}else {
					control.setCursor(defaultCursor);
					cursor = null;
				}
			}else if(value instanceof String) {
				cursor = UtilSwt.createCursor(null, (String) value, actionContext);
				if(cursor != null) {
					control.setCursor(cursor);					
				}else {
					control.setCursor(defaultCursor);
				}
			}
		}finally {
			if(oldCursor != null && oldCursor != cursor) {
				oldCursor.dispose();
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(cursor != null && cursor.isDisposed() == false) {
			cursor.dispose();
		}
	}

	@Override
	public Object getValue() {
		Control control = (Control) widget;
		return control.getCursor();
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new CursorItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}
	}

}
