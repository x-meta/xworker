package xworker.dataObject.swt.bind.reflect;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.swt.util.ResourceManager;

public class ImageItem extends ReflectItem {
	Image image;
	public ImageItem(Thing thing, Widget widget,  ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		super(thing, widget, actionContext, "getImage", "setImage", Image.class);
	}
	
	
	@Override
	public Object getValue(DataObjectBinder binder, DataObject dataObject) {
		Object value = super.getValue(binder, dataObject);
		Image oldImage = image;
		try {
			image = null;
			if(value instanceof Integer) {
				return widget.getDisplay().getSystemImage((Integer) value);
			}else if(value instanceof String) {
				Bindings bindings = actionContext.push();
				bindings.put("parent", widget);
				try {
					return (Image) ResourceManager.createIamge((String) value, actionContext);
				}finally {
					actionContext.pop();
				}
			}
		}finally {
			if(oldImage != null && oldImage != image) {
				oldImage.dispose();
			}
		}
		
		return image;
	}


	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(image != null) {
			image.dispose();
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
					BinderItem item = new ImageItem(self, widget, actionContext);
					binder.addBinderItem(item);
				}
			}
		}
	}

}
