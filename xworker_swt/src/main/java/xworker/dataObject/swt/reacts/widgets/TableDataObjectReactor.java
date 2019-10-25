package xworker.dataObject.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class TableDataObjectReactor extends WidgetDataObjectReactor{
	Table table;
	
	public TableDataObjectReactor(Table table, Thing self, ActionContext actionContext) {
		super(table, self, actionContext);
		
		this.table = table;
		this.table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				DataObject dataObject = (DataObject) event.item.getData();
				if(dataObject != null) {
					List<DataObject> dataObjects = new ArrayList<DataObject>();
					dataObjects.add(dataObject);
					
					TableDataObjectReactor.this.fireSelected(dataObjects);
				}
			}
			
		});
	}

	@Override
	public void doWidgetOnSelected(List<DataObject> dataObjects) {
		for(TableItem item : table.getItems()) {
			DataObject data = (DataObject) item.getData();
			if(data != null) {				
				for(DataObject dataObject : dataObjects) {
					if(data.equals(dataObject)) {
						item.setChecked(true);
						break;
					}
				}
			}			
		}		
	}

	@Override
	public void doWidgetOnUnselected() {
		for(TableItem item : table.getItems()) {
			if(item.getChecked()) {
				item.setChecked(false);
			}
		}
	}
		
	@Override
	public void doWidgetOnAdded(int index, List<DataObject> dataObjects) {
	}

	@Override
	public void doWidgetOnRemoved(List<DataObject> dataObjects) {
		this.fireUnselected();
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
		
		Table table = self.doAction("getTable", actionContext);
		if(table == null) {
			Object parent = actionContext.getObject("parent");
			if(parent instanceof Table) {
				table = (Table) parent;
			}			
		}
		
		if(table != null) {
			TableDataObjectReactor reactor = new TableDataObjectReactor(table, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}
}
