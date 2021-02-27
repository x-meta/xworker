package xworker.swt.reacts.datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.app.view.swt.data.DataStore;
import xworker.app.view.swt.data.DataStoreListener;
import xworker.app.view.swt.data.ThingDataStoreListener;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;

public class DataStoreDataReactor extends DataReactor implements DataStoreListener {
	private static final String TAG = DataStoreDataReactor.class.getName();
	//private static Logger logger = LoggerFactory.getLogger(DataStoreDataReactor.class);
	
	DataStore dataStore = null;
	Control control;
	ThreadLocal<DataReactorContext> contextLocal = new ThreadLocal<DataReactorContext>();
	
	public DataStoreDataReactor(Thing dataStore, Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		this.dataStore = DataStore.getDataStore(dataStore);
		this.control = self.doAction("getControl", actionContext);
		if(control == null && dataStore.get("parent") instanceof Control) {
			//使用dataStore创建时的parnet的控件
			this.control = dataStore.getObject("parent");
		}
		if(control == null) {
			Executor.warn(TAG, "Should set the control of this DataStoreDataReactor, path=" + self.getMetadata().getPath());
		}else {
			ThingDataStoreListener.attach(this.dataStore.getStore(), this, actionContext);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {
		contextLocal.set(context);
		Map<String, Object> params = null;
		for(Object data : datas) {
			if(data instanceof Map) {
				params = (Map<String, Object>) data;
				break;
			}
		}
		
		if(params != null) {
			dataStore.load(params);
		}else {
			dataStore.load(new HashMap<String, Object>());
		}
	}

	@Override
	protected void doOnUnselected(DataReactorContext context) {
		contextLocal.set(context);
		dataStore.load(new HashMap<String, Object>());
	}

	@Override
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		contextLocal.set(context);
		try {
			//打开新建数据对象的对话框
			String title = UtilString.getString("lang:d=新建&en=New ", actionContext);
			Thing dataObject = dataStore.getDataObject();
			if(dataObject != null) {
				title = title + dataObject.getMetadata().getLabel();
			}
			
			Object initValues = null;
			if(datas != null && datas.size() > 0) {
				initValues = datas.get(0);
			}
			
			//打开对话框
			self.doAction("openNewDialog", actionContext, "dataStore", dataStore, "title", title, "initValues", initValues);
		}catch(Exception e) {
			Executor.warn(TAG, "Open dialog error, thing=" + self.getMetadata().getPath(), e);
		}
	}

	@Override
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		if(datas == null || datas.size() == 0) {
			Executor.info(TAG, "Datas is null or blank, does not open delete dialog");
			return;
		}
		
		try {
			//打开新建数据对象的对话框
			String title = UtilString.getString("lang:d=删除&en=Delete ", actionContext);
			Thing dataObject = dataStore.getDataObject();
			if(dataObject != null) {
				title = title + dataObject.getMetadata().getLabel();
			}
			
			String confirmMessage = UtilString.getString("lang:d=确认要删除&en=Are you sure to delete ", actionContext);
			Thing dataObjectDesc = dataStore.getDataObject();
			if(dataObjectDesc != null) {
				confirmMessage = confirmMessage + dataObjectDesc.getMetadata().getLabel();
			}
			confirmMessage = confirmMessage + 
					UtilString.getString("lang:d=吗?&en=?", actionContext);
			
			//打开对话框
			self.doAction("openDeleteDialog", actionContext, "dataStore", dataStore, "title", title,
					"dataObjects", DataReactorUtils.toDataObjectList(datas),
					"confirmMessage", confirmMessage);
		}catch(Exception e) {
			Executor.warn(TAG, "Open dialog error, thing=" + self.getMetadata().getPath(), e);
		}
	}

	@Override
	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
		try {
			List<DataObject> dataObjects = DataReactorUtils.toDataObjectList(datas);
			if(dataObjects.size() == 0) {
				Executor.info(TAG, "Datas is null or blank, does not open edit dialog");
				return;
			}
			
			DataObject dataObject = dataObjects.get(0);
			
			//打开新建数据对象的对话框
			String title = UtilString.getString("lang:d=编辑&en=Edit ", actionContext);
			Thing desc = dataStore.getDataObject();
			if(desc != null) {
				title = title + desc.getMetadata().getLabel();
			}
			
			//打开对话框
			self.doAction("openEditDialog", actionContext, "dataStore", dataStore, "title", title, "dataObject", dataObject);
		}catch(Exception e) {
			Executor.warn(TAG, "Open dialog error, thing=" + self.getMetadata().getPath(), e);
		}
	}

	public DataReactorContext getContext() {
		return contextLocal.get();
	}
	
	@Override
	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
	}

	private List<Object> toObjectList(List<DataObject> records){
		List<Object> list = new ArrayList<Object>();
		for(DataObject dataObject : records) {
			list.add(dataObject);
		}
		
		return list;
	}
	@Override
	public void onInsert(Thing store, int index, List<DataObject> records) {
		this.fireAdded(index, toObjectList(records), getContext()); 
	}

	@Override
	public void onLoaded(Thing store, List<DataObject> records) {
		this.fireLoaded(toObjectList(records), getContext());
	}

	@Override
	public void onReconfig(Thing store) {
		this.fireUnselected(getContext());
	}

	@Override
	public void onRemove(Thing store, List<DataObject> records) {
		this.fireRemoved(toObjectList(records), getContext());
	}

	@Override
	public void onUpdate(Thing store, List<DataObject> records) {
		this.fireUpdated(toObjectList(records), getContext());
	}

	@Override
	public void beforeLoad(Thing store) {
		this.fireUnselected(getContext());
	}

	@Override
	public Control getControl() {
		return control;
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing dataStore = self.doAction("getDataStore", actionContext);
		if(dataStore == null) {
			dataStore = actionContext.getObject("dataStore");
		}
		
		if(dataStore != null) {
			DataStoreDataReactor reactor = new DataStoreDataReactor(dataStore, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
		}
				
	}
}
