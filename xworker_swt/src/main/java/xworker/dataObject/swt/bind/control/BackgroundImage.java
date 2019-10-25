package xworker.dataObject.swt.bind.control;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.dataObject.swt.bind.WidgetBinderItem;
import xworker.swt.util.ResourceManager;

public class BackgroundImage extends WidgetBinderItem{
	Image image;
	Image defaultImage;
	
	public BackgroundImage(Thing thing, Widget widget, ActionContext actionContext) {
		super(thing, widget, actionContext);
		
		Control control = (Control) widget;
		defaultImage = control.getBackgroundImage();
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		Control control = (Control) widget;
		Image oldImage = image;		
		
		try {
			Image newImage = null;
			if(value instanceof Image) {
				control.setBackgroundImage(newImage);		
				image = null;
			}else if(value instanceof Integer) {
				Image sysImage = control.getDisplay().getSystemImage((Integer) value);
				if(sysImage != null) {
					control.setBackgroundImage(sysImage);
					image = null;
				}else {
					control.setBackgroundImage(defaultImage);
					image = null;
				}
			}else if(value instanceof String) {
				Bindings bindings = actionContext.push();
				bindings.put("parent", null);
				try {
					image = (Image) ResourceManager.createIamge((String) value, actionContext);
					if(image != null) {
						control.setBackgroundImage(image);					
					}else {
						control.setBackgroundImage(defaultImage);
					}
				}finally {
					actionContext.pop();
				}
			}else {
				control.setBackgroundImage(defaultImage);
			}
		}finally {
			if(oldImage != null && oldImage != image) {
				oldImage.dispose();
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(image != null && image.isDisposed() == false) {
			image.dispose();
		}
	}
	
	@Override
	public Object getValue() {
		Control control = (Control) widget;
		return control.getBackgroundImage();
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");		
		DataObjectBinder binder = actionContext.getObject("binder");
		
		String widgets = self.getStringBlankAsNull("widget");
		if(widgets != null) {
			for(String name : widgets.split("[,]")) {
				Widget widget = actionContext.getObject(name);
				if(widget != null) {
					BinderItem item = new BackgroundImage(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}	
	}

}
