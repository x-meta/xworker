package xworker.app.view.swt.data.events;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;
import xworker.task.DelayTask;

public class TableDataStoreDataObjectListener extends DelayTask implements DataObjectListener, DisposeListener{
	Thing dataStore;
	DataObject dataObject;
	TableItem tableItem;
	ActionContext actionContext;

	public TableDataStoreDataObjectListener(Thing dataStore, DataObject dataObject, TableItem tableItem, ActionContext actionContext) {
		super(300);
		
		this.dataObject = dataObject;
		this.dataStore = dataStore;
		this.tableItem = tableItem;
		this.actionContext = actionContext;
		
		dataObject.addListener(this);
	}

	@Override
	public void run() {
		if(tableItem.isDisposed() == false) {
			tableItem.getParent().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						TableDataStoreListener.updateItem(dataStore, tableItem, dataObject, actionContext);
					}catch(Exception e) {						
					}
				}
			});
		}
	}

	@Override
	public void changed(DataObject dataObject) {
		this.doTask();
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		dataObject.removeListener(this);
	}
	
}
