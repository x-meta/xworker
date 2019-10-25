package xworker.dataObject.swt.reacts.xworker;

import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.view.swt.widgets.form.DataObjectForm;
import xworker.app.view.swt.widgets.form.DataObjectFormListener;
import xworker.dataObject.DataObject;
import xworker.dataObject.swt.reacts.widgets.WidgetDataObjectReactor;

public class DataObjectFormReactor extends WidgetDataObjectReactor implements DataObjectFormListener {
	DataObjectForm form;
	DataObject dataObject;
	
	public DataObjectFormReactor(Widget widget, DataObjectForm form, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);
		this.form = form;
		this.form.addListener(this);
		this.dataObject = form.getDataObject();
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		if(dataObjects.size() > 0) {
			form.setDataObject(dataObjects.get(0));
		}
	}

	@Override
	public void doWidgetOnUnselected() {
		form.setDataObject((DataObject) null); 
	}

	@Override
	public void doWidgetOnAdded(int index, List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnRemoved(List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnUpdated(List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnLoaded(List<DataObject> dataObjects) {
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing form = self.doAction("getDataObjectForm", actionContext);
		if(form == null) {
			form = actionContext.getObject("dataObjectForm");
		}
		
		if(form != null) {
			DataObjectForm dataObjectForm = DataObjectForm.getDataObjectForm(form);
			if(dataObjectForm != null) {
				DataObjectFormReactor reactor = new DataObjectFormReactor(dataObjectForm.getControl(), dataObjectForm, self, actionContext);
				actionContext.g().put(self.getMetadata().getName(), reactor);
			}
		}
	}

	@Override
	public void onSetDataObject(DataObjectForm dataObjectForm, DataObject dataObject) {
		this.dataObject = dataObject;
		this.fireSelected(toDataObjectList(dataObject));
	}

	@Override
	public void onMidified(DataObjectForm dataObjectForm) {
		DataObject data = form.getDataObject();
		this.fireUpdated(toDataObjectList(data));
	}
}
