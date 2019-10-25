package xworker.app.view.swt.data.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class DSSelectionListener  implements Listener{
	private static final String TAG = "DataStore.DSSelectionListener";
	List<DataObject> dataObjects = new ArrayList<DataObject>();
	Thing store;
	ActionContext actionContext;
	boolean enabled = true;
	
	public DSSelectionListener(Thing store, ActionContext actionContext) {
		this.store = store;
		this.actionContext = actionContext;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event.widget instanceof Button) {
			handleButton(event);
		}else if(event.widget instanceof Combo) {
			handleCombo(event);
		}else if(event.widget instanceof CCombo) {
			handleCCombo(event);
		}else if(event.widget instanceof Table) {
			handleTable(event);
		}else if(event.widget instanceof Tree) {
			handleTree(event);
		}else if(event.widget instanceof Grid) {
			handleTree(event);
		}else {
			Executor.warn(TAG, "Unsupport type " + event.widget.getClass().getName());
		}
		
		if(enabled) {
			store.doAction("fireSelectionEvent", actionContext, "dataObjects", dataObjects);
		}
	}
	
	public void clear() {
		dataObjects.clear();
	}
	
	public void handleTree(Event event) {
		dataObjects.clear();
		DataObject record = (DataObject) event.item.getData();
		if(record != null) {
			dataObjects.add(record);
		}
	}
	
	public void handleTable(Event event) {
		dataObjects.clear();
		
		Object data = event.item.getData();
		if(!(data instanceof DataObject)) {
			return;
		}
		
		DataObject record = (DataObject) event.item.getData();
		if(record != null) {
			dataObjects.add(record);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleCCombo(Event event) {
		CCombo combo = (CCombo) event.widget;
		int index = combo.getSelectionIndex();
		List<DataObject> records = (List<DataObject>) combo.getData("_store_records");
		if(index != -1 && index < records.size()) {
			dataObjects.clear();
			dataObjects.add(records.get(index));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleCombo(Event event) {
		Combo combo = (Combo) event.widget;
		int index = combo.getSelectionIndex();
		List<DataObject> records = (List<DataObject>) combo.getData("_store_records");
		if(index != -1 && index < records.size()) {
			dataObjects.clear();
			dataObjects.add(records.get(index));
		}
	}
	
	public void handleButton(Event event) {
		Button button = (Button) event.widget;
		DataObject record = (DataObject) button.getData("record");
		if(record == null) {
			return;
		}
		
		if(button.getSelection() == true) {
			dataObjects.add(record);
		}else {
			dataObjects.remove(record);
		}
	}
	
	public void remove(List<DataObject> list) {
		dataObjects.removeAll(list);		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static void bind(Control parent, Thing store, ActionContext actionContext) {
		String key = "__DSSelectionListener__";
		DSSelectionListener listener = (DSSelectionListener) parent.getData(key);
		if(listener == null) {
			listener = new DSSelectionListener(store, actionContext);
			parent.setData(key, listener);
			
			parent.addListener(SWT.Selection, listener);
		}else {
			listener.clear();
		}
	}
	
	public static DSSelectionListener getListener(Control parent) {
		String key = "__DSSelectionListener__";
		return (DSSelectionListener) parent.getData(key); 
	}
	
	public static void removeRecords(Control parent, List<DataObject> dataObjects) {
		DSSelectionListener listener = getListener(parent);
		if(listener != null) {
			listener.remove(dataObjects);
		}
	}
	
	public static void clear(Control parent) {
		DSSelectionListener listener = getListener(parent);
		if(listener != null) {
			listener.clear();
		}
	}
}
