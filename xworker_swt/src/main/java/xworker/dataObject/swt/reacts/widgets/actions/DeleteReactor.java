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

public class DeleteReactor extends WidgetDataObjectReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(EditDialogReactor.class);
	List<DataObject> dataObjects;
			
	public DeleteReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);		
		
		widget.addListener(SWT.Selection, this);
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		if(dataObjects != null && dataObjects.size() > 0) {
			this.dataObjects = dataObjects;
			this.setWidgetEnabled(true);
		}else {
			this.dataObjects = null;
			this.setWidgetEnabled(false);
		}
	}

	@Override
	public void doWidgetOnUnselected() {
		dataObjects = null;
		this.setWidgetEnabled(false);
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
		dataObjects = null;
		this.setWidgetEnabled(false);
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
				title = UtilString.getString("lang:d=删除&en=Delete ", actionContext);
				Thing dataObject = ds.getDataObject();
				if(dataObject != null) {
					title = title + dataObject.getMetadata().getLabel();
				}
			}
			
			String confirmMessage = self.doAction("getConfirmMessage", actionContext);
			if(confirmMessage == null || "".equals(confirmMessage)){
				confirmMessage = UtilString.getString("lang:d=确认要删除&en=Are you sure to delete ", actionContext);
				Thing dataObject = ds.getDataObject();
				if(dataObject != null) {
					confirmMessage = confirmMessage + dataObject.getMetadata().getLabel();
				}
				confirmMessage = confirmMessage + 
						UtilString.getString("lang:d=吗?&en=?", actionContext);
			}
			
			//打开对话框
			self.doAction("openDialog", actionContext, "dataStore", dataStore, "title", title,
					"dataObjects", dataObjects, "confirmMessage", confirmMessage);
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
			DeleteReactor reactor = new DeleteReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}


}
