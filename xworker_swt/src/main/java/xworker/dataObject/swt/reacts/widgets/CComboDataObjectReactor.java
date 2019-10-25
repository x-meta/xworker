package xworker.dataObject.swt.reacts.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.view.swt.data.DataStore;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class CComboDataObjectReactor extends WidgetDataObjectReactor{
	CCombo widget;
	
	public CComboDataObjectReactor(CCombo widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);
		
		this.widget = widget;
		this.widget.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List<DataObject> dataObjects = DataStore.getWidgetRecords(CComboDataObjectReactor.this.widget);
				if(dataObjects != null) {
					if(event.index < dataObjects.size()) {
						DataObject dataObject = dataObjects.get(event.index);
						List<DataObject> datas = CComboDataObjectReactor.this.toDataObjectList(dataObject);
						CComboDataObjectReactor.this.fireSelected(datas);
					}
				}				
			}			
		});
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		if(dataObjects != null && dataObjects.size() > 0) {
			DataObject dataObject = dataObjects.get(0);
			widget.setText(DataObjectUtil.getDisplayString(dataObject));
		}
	}

	@Override
	public void doWidgetOnUnselected() {
		widget.setText("");
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
		this.fireUnselected();
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		CCombo widget = self.doAction("getCombo", actionContext);
		if(widget == null) {
			Object parent = actionContext.getObject("parent");
			if(parent instanceof CCombo) {
				widget = (CCombo) parent;
			}			
		}
		
		if(widget != null) {
			CComboDataObjectReactor reactor = new CComboDataObjectReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}

}
