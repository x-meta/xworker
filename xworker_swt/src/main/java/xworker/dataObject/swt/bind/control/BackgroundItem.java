package xworker.dataObject.swt.bind.control;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;
import xworker.swt.util.UtilSwt;

public class BackgroundItem extends WidgetBinderItem{
	/** color是由本对象创建出来的，所以有本对象负责销毁 */
	Color color;
	/** 是Control默认的颜色，但value=null时重新赋值回去 */
	Color defaultColor;
	
	public BackgroundItem(Thing thing, Widget widget, ActionContext actionContext) {
		super(thing, widget, actionContext);
		
		Control control = (Control) widget;
		defaultColor = control.getBackground();
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		Control control = (Control) widget;
		Color oldColor = color;		
		
		try {
			Color newColor = null;
			if(value instanceof Color) {
				control.setBackground(newColor);		
				color = null;
			}else if(value instanceof Integer) {
				Color sysColor = control.getDisplay().getSystemColor((Integer) value);
				if(sysColor != null) {
					control.setBackground(sysColor);
					color = null;
				}else {
					//设置回默认颜色
					control.setBackground(defaultColor);
					color = null;
				}
			}else if(value instanceof String) {
				color = UtilSwt.createColor(null, (String) value, actionContext);
				if(color != null) {
					control.setBackground(color);					
				}else {
					control.setBackground(defaultColor);
				}
			}else {
				control.setBackground(defaultColor);
			}
		}finally {
			if(oldColor != null && oldColor != color) {
				oldColor.dispose();
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {		
		if(color != null && color.isDisposed() == false) {
			color.dispose();
		}
	}
	
	@Override
	public Object getValue() {
		Control control = (Control) widget;
		return control.getBackground();
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new BackgroundItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}		
	}
}
