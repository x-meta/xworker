package xworker.dataObject.swt.reacts.datas;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.view.swt.data.DataStore;
import xworker.app.view.swt.data.DataStoreListener;
import xworker.app.view.swt.data.ThingDataStoreListener;
import xworker.dataObject.DataObject;
import xworker.dataObject.swt.reacts.ThingDataObjectReactor;

public class DataStoreReactor extends ThingDataObjectReactor implements DataStoreListener {
	DataStore dataStore = null;
	Control control;
	public DataStoreReactor(DataStore dataStore, Control control, Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		this.dataStore = dataStore;
		this.control = control;
		ThingDataStoreListener.attach(dataStore.getStore(), this, actionContext);
	}
	
	@Override
	public void doOnSelected(List<DataObject> dataObjects) {
		DataObject params = null;
		if(dataObjects != null && dataObjects.size() > 0) {
			params = dataObjects.get(0);
		}
		
		if(params != null) {
			dataStore.load(params);
		}else {
			dataStore.load(new HashMap<String, Object>());
		}
	}

	@Override
	public void doOnUnselected() {
		dataStore.load(new HashMap<String, Object>());
	}

	@Override
	public void doOnAdded(int index, List<DataObject> dataObjects) {
		dataStore.insert(dataObjects, index, true);		
	}

	@Override
	public void doOnRemoved(List<DataObject> dataObjects) {
		dataStore.remove(dataObjects);
	}

	@Override
	public void doOnUpdated(List<DataObject> dataObjects) {
		dataStore.update(dataObjects);
	}

	@Override
	public void doOnLoaded(List<DataObject> dataObjects) {
		
	}

	@Override
	public void onInsert(Thing store, int index, List<DataObject> records) {
		this.fireAdded(index, records);
	}

	@Override
	public void onLoaded(Thing store, List<DataObject> records) {
		this.fireLoaded(records);
	}

	@Override
	public void onReconfig(Thing store) {
	}

	@Override
	public void onRemove(Thing store, List<DataObject> records) {
		this.fireRemoved(records);
	}

	@Override
	public void onUpdate(Thing store, List<DataObject> records) {
		this.fireUpdated(records);
	}

	@Override
	public void beforeLoad(Thing store) {
	}

	@Override
	public Control getControl() {
		return control;
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing dataStore = self.doAction("getDataStore", actionContext);
		Control control = self.doAction("getControl", actionContext);
		if(dataStore == null) {
			dataStore = actionContext.getObject("dataStore");
		}
		
		if(dataStore != null) {
			DataStoreReactor reactor = new DataStoreReactor(DataStore.getDataStore(dataStore), control, 
					self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
	}
}
