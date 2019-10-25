package xworker.dataObject.swt.bind.control;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;
import xworker.swt.util.SwtUtils;

public class FontItem extends WidgetBinderItem{
	Font font;
	Font defaultFont;
	
	public FontItem(Thing thing, Widget widget, ActionContext actionContext) {
		super(thing, widget, actionContext);
		
		Control control = (Control) widget;
		defaultFont = control.getFont();
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		Control control = (Control) widget;
		Font oldFont = font;		
		
		try {
			Font newFont = null;
			if(value instanceof Font) {
				control.setFont(newFont);		
				font = null;
			}else if(value instanceof String) {
				Bindings bindings = actionContext.push();
				bindings.put("parent", null);
				try {
					font = SwtUtils.createFont(control, (String) value, actionContext);
					if(font != null) {
						control.setFont(font);					
					}else {
						control.setFont(defaultFont);
					}
				}finally {
					actionContext.pop();
				}
			}else {
				control.setFont(defaultFont);
			}
		}finally {
			if(oldFont != null && oldFont != font) {
				oldFont.dispose();
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(font != null && font.isDisposed() == false) {
			font.dispose();
		}
	}
	
	@Override
	public Object getValue() {
		Control control = (Control) widget;
		return control.getFont();
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new FontItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}
	}

}
