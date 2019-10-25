package xworker.dataObject.swt.reacts.widgets.actions;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.reacts.widgets.WidgetDataObjectReactor;

public class FireSelected extends WidgetDataObjectReactor implements Listener{
	List<DataObject> dataObjects;
	
	public FireSelected(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);		
		
		widget.addListener(SWT.Selection, this);
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		this.dataObjects = dataObjects;
		if(this.dataObjects != null && this.dataObjects.size() > 0) {
			this.setWidgetEnabled(true);
		}else {
			this.setWidgetEnabled(false);
		}
		
	}

	@Override
	public void doWidgetOnUnselected() {
		this.dataObjects = null;
	}

	@Override
	public void doWidgetOnAdded(int index, List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnRemoved(List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnUpdated(List<DataObject> dataObjects) {
		doWidgetOnSelected(dataObjects);
	}

	@Override
	public void doWidgetOnLoaded(List<DataObject> dataObjects) {
	}

	@Override
	public void handleEvent(Event event) {
		if(this.dataObjects == null || this.dataObjects.size() == 0) {
			this.fireUnselected();
		}else {
			this.fireSelected(dataObjects);
		}
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Widget widget = self.doAction("getWidget", actionContext);
		if(widget == null) {
			widget = actionContext.getObject("parent");
		}
		
		if(widget != null) {
			FireSelected reactor = new FireSelected(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}
}
