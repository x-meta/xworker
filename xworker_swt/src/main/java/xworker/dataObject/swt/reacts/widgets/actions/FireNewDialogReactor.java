package xworker.dataObject.swt.reacts.widgets.actions;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.app.view.swt.data.DataStore;
import xworker.dataObject.DataObject;
import xworker.dataObject.swt.reacts.widgets.WidgetDataObjectReactor;

public class FireNewDialogReactor  extends WidgetDataObjectReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(FireNewDialogReactor.class);
			
	public FireNewDialogReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);		
		
		widget.addListener(SWT.Selection, this);
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnUnselected() {
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
		try {
			//打开新建数据对象的对话框
			Thing dataStore = self.doAction("getDataStore", actionContext);
			String title = self.doAction("getTitle", actionContext);
			DataStore ds = DataStore.getDataStore(dataStore);
			if(ds == null) {
				logger.warn("DataStore is null, thing=" + self.getMetadata().getPath());
				return;
			}
			
			if(title == null || "".equals(title)) {
				title = UtilString.getString("lang:d=新建&en=New ", actionContext);
				Thing dataObject = ds.getDataObject();
				if(dataObject != null) {
					title = title + dataObject.getMetadata().getLabel();
				}
			}
			
			Object initValues = self.doAction("getInitValues", actionContext);
			
			//打开对话框
			self.doAction("openDialog", actionContext, "dataStore", dataStore, "title", title, "initValues", initValues);
		}catch(Exception e) {
			logger.warn("Open dialog error, thing=" + self.getMetadata().getPath(), e);
		}
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Widget widget = self.doAction("getWidget", actionContext);
		if(widget == null) {
			widget = actionContext.getObject("parent");
		}
		
		if(widget != null) {
			FireNewDialogReactor reactor = new FireNewDialogReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}

}
