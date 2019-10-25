package xworker.dataObject.swt.bind;

import org.eclipse.swt.events.DisposeEvent;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.util.UtilData;

public class ThingBinderItem extends BinderItem{

	public ThingBinderItem(Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
	}

	@Override
	public boolean isDisposed() {
		return UtilData.isTrue(thing.doAction("isDisposed", actionContext));
	}

	@Override
	public Object getValue() {
		return thing.doAction("getValue", actionContext);
	}

	@Override
	public void doUpdate(DataObjectBinder binder, DataObject dataObject, Object value) {
		thing.doAction("update", actionContext, 
				"binder", binder, "dataObject", dataObject, "value", value);
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = actionContext.getObject("binder");
		
		ThingBinderItem item = new ThingBinderItem(self, actionContext);
		binder.addBinderItem(item);
	}
}
