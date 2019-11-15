package xworker.app.view.swt.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.DataObjectListListener;
import xworker.dataObject.DataObjectListener;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.UtilCondition;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.task.DelayTask;
import xworker.util.UtilData;

public class DataStore implements DataObjectListener, DataObjectListListener, DisposeListener, DataStoreSelectionListener{
	//private static Logger log = LoggerFactory.getLogger(DataStore.class);
	private static final String TAG = "DataStore";
	
	public static final String LISTENER = "storeListener";
	public static final String STORE = "store";
	private static final String RECORDS = "records";
	public static final String STORE_RECORDS = "_store_records";
	
	Thing store;
	ActionContext actionContext;
	
	DataObjectList datas = new DataObjectList();
	DataObjectList sourceDatas = null; 
	SourceDataObjectListListener sourceListener = null;
	
	//List<DataObject> removedRecords = new ArrayList<DataObject>();	
	boolean eventEanbeld = true;
	
	//绑定的控件
	Map<Widget, Widget> widgets = new HashMap<Widget, Widget>();
	
	/** 监听数据仓库选中数据对象的监听器列表 */
	List<DataStoreSelectionListener> selectionListeners = new ArrayList<DataStoreSelectionListener>();
	List<DataObject> changedDataObjects = new ArrayList<DataObject>();
	
	DelayTask dataObjectChangedTask = new DelayTask(500) {
		@Override
		public void run() {
			try {
				store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onUpdate", 
		        		"records", changedDataObjects));
			}catch(Exception e ) {
				Executor.error(TAG, "Fire onUpdate error", e);
			}finally {
				changedDataObjects.clear();
			}
		}
		
	};
	
	public DataStore(Thing store, ActionContext actionContext) {
		this.store = store;
		this.actionContext = actionContext;
		//datas.addListener(this);
		sourceListener = new SourceDataObjectListListener(this);
		datas.addListener(this);
	}
	
	/**
	 * 返回数据仓库的事物模型。
	 * 
	 * @return
	 */
	public Thing getStore() {
		return this.store;
	}
	/**
	 * 返回设置在Store上的数据对象的描述者。
	 * 
	 * @return
	 */
	public Thing getDataObject() {
		return (Thing) store.get("dataObject");
	}
	
	/**
	 * 返回Store当前的查询条件配置。
	 * 
	 * @return
	 */
	public Thing getQueryConfig() {
		return (Thing) store.get("queryConfig");
	}
	
	/**
	 * 返回Store当前的查询条件参数。
	 * 
	 * @return
	 */
	public Object getQueryParams() {		
		return store.get("params");
	}
	
	public void attach(Widget widget) {
		widget.addDisposeListener(this);
		widgets.put(widget, widget);
	}
	
	public void setEventEnabled(boolean enabled) {
		this.eventEanbeld = enabled;
	}
	
	/**
	 * 向Store设置新的记录，同时为每一个数据对象增加监听器，监听器是this。
	 * 
	 * @param records
	 */
	public void setDataObjects(List<DataObject> records) {
		for(DataObject dataObject: datas) {
			dataObject.removeListener(this);
		}
		
		datas.setDataObjects(records);
	}
	
	public void setDataObjectList(DataObjectList datas){
		if(this.datas != null) {
			this.datas.removeListener(this);
		}
		
		this.datas = datas;
		datas.addListener(this);		
		
		//触发已加载的事件
		store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onLoaded"));
	}
	
	/**
	 * 设置源数据对象列表。
	 * 
	 * @param sourceDatas
	 */
	public void setSourceDatas(DataObjectList sourceDatas) {
		if(this.sourceDatas != null) {
			this.sourceDatas.removeListener(this);
		}
		
		this.sourceDatas  = sourceDatas;
		this.sourceDatas.addListener(sourceListener);
		
		this.reload();
	}
	
	/**
	 * 向DataStore的制定索引位置插入一条记录。如果noSaveDataObject=false并且数据仓库设置了autoSave=true，
	 * 那么则会调用数据对象的create方法。
	 * 
	 * @param record 要插入的记录
	 * @param index 索引位置
	 * @param noSave 是否不保存
	 * 
	 * @return 返回新插入的数据对象
	 */
	public DataObject insert(DataObject record, int index, boolean noSave) {
		if(record == null) {
			return null;
		}
		
		//自动保存
		if(!noSave && isAutoSave()) {
			//record.setFlag(DataObject.FLAG_NEW);
			record = (DataObject) record.doAction("create", actionContext);			
		}
        
		if(index < 0) {
			index = 0;
		}
		
		synchronized(datas) {
			if(index > datas.size()) {
				index = datas.size();
			}
			
			datas.add(index, record);
		}
        return record;
	}
	
	/**
	 * 插入一组数据对象。如果noSave=true并且autoSave=true那么会执行数据对象的create方法。
	 * 
	 * @param records 
	 * @param index
	 * @param noSave 
	 * @return
	 */
	public List<DataObject> insert(List<DataObject> records, int index, boolean noSave) {
		if(records == null) {
			return null;
		}
		
		//自动保存
		if(!noSave && isAutoSave()) {
			//record.setFlag(DataObject.FLAG_NEW);
			for(DataObject record : records) {
				record.doAction("create", actionContext);
			}
		}
        
		if(index < 0) {
			index = 0;
		}
		
		synchronized(datas) {
			if(index > datas.size()) {
				index = datas.size();
			}
			
			datas.addAll(index, records);
		}
        return records;
	}
	
	public void update(DataObject record) {
		if(record != null && isAutoSave()) {
			record.update(actionContext);
		}
	}
	
	public void update(List<DataObject> records) {
		if(records != null) {
			for(DataObject record : records) {
				if(isAutoSave()) {
					record.update(actionContext);
				}
			}
		}
	}
	
	public void remove(DataObject record) {
		if(record != null) {
			if(isAutoSave()) {
				record.delete(actionContext);
			}
			
			datas.remove(record);
			
			if(sourceDatas != null) {
				sourceDatas.remove(record);
			}
		}
	}
	
	public void remove(List<DataObject> records) {
		if(records != null) {
			for(DataObject record : records) {
				if(isAutoSave()) {
					record.delete(actionContext);
				}
				
				datas.remove(record);
				
				if(sourceDatas != null) {
					sourceDatas.remove(record);
				}
			}
		}
	}
	
	/**
	 * 重新加载数据。
	 */
	public void reload() {
		store.doAction("reload", actionContext);
	}
	
	/**
	 * 加载数据。
	 * 
	 * @param params
	 */
	public void load(Map<String, Object> params) {		
		store.doAction("load", actionContext, "params", params);
	}
	
	public void setDataObject(Thing dataObject) {
		Thing queryConfig = this.getQueryConfig();
		store.doAction("setDataObject", actionContext, "dataObject", dataObject, "queryConfig", queryConfig);
	}
	
	/**
	 * 设置新的数据对象。
	 * 
	 * @param dataObject
	 * @param queryConfig
	 * @param params
	 */
	public void setDataObject(Thing dataObject, Thing queryConfig, Map<String, Object> params) {
		store.set("params", params);
		store.doAction("setDataObject", actionContext, "dataObject", dataObject, "queryConfig", queryConfig);
	}
	
	public void commit() {
		store.doAction("commit", actionContext);
	}
	
	public boolean isAutoSave() {
		return store.getBoolean("autoSave");
	}
	
	/**
	 * 添加一个DataStoreListener。
	 * @param listener
	 */
	public void addListener(DataStoreListener listener) {
		ThingDataStoreListener.attach(store, listener, actionContext);
	}
	
	public void addSelectionListener(DataStoreSelectionListener listener) {
		if(listener != null && selectionListeners.contains(listener) == false) {
			selectionListeners.add(listener);
		}
	}
	
	public void removeSelectionListener(DataStoreSelectionListener listener) {
		if(listener != null) {
			selectionListeners.remove(listener);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		//数据仓库实例
		Thing store = null;
		boolean isRef = false;
		String storeRef = self.getStringBlankAsNull("storeRef");
		if(storeRef != null){
			//先查看是否是注册的store
		    store = DataStoreManager.get(storeRef);//OgnlUtil.getValue(self.storeRef, actionContext);
		    
		    //其次路径查看
		    if(store == null){
		    	store = world.getThing(storeRef);
		    }
		    
		    //查看是否是在变量中
		    if(store == null){
		        try{
		            store = (Thing) OgnlUtil.getValue(storeRef, actionContext);
		        }catch(Exception e){
		        }
		    }
		    if(store == null){
		        Executor.warn(TAG, "DataStore ref is null, storeRef=" + storeRef + ", thing=" + self.getMetadata().getPath());
		    }
		    isRef = true;
		}else{
		    isRef = false;
		    store = new Thing("xworker.app.view.swt.data.DataStore");
		    store.getAttributes().putAll(self.getAttributes());
		    store.set("extends", self.getMetadata().getPath());
		    store.put("listeners", new ArrayList<Object>());
		    store.put("config", self);
		    store.put("dataLoaded", false);
		    store.put("name", self.get("name"));
		    store.put("actionContext", actionContext);
		    store.setData("actionContext", actionContext);
		    store.put("display", Display.getCurrent());
		    DataStore dataStore = new DataStore(store, actionContext);
		    store.put("dataStore", dataStore);
		    store.put("records", dataStore.datas);
		    //store.put("removedRecords", dataStore.removedRecords);
		    
		    //数据对象
		    Thing dataObject = world.getThing(self.getString("dataObject"));
		    if(dataObject == null){
		        Thing dos = self.getThing("dataObjects@0");
		        if(dos != null && dos.getChilds().size() > 0){
		            dataObject = dos.getChilds().get(0);
		        }
		    }
		    store.put("dataObject", dataObject);
		    
		    //查询配置
		    Thing queryConfig = world.getThing(self.getString("queryConfig"));
		    if(queryConfig == null){
		        queryConfig = self.getThing("queryConfig@0");
		    }
		    store.put("queryConfig", queryConfig);
		    
		    //分页
		    Map<String, Object> pageInfo = new HashMap<String, Object>();
		    pageInfo.put("limit", 0);
		    pageInfo.put("start", 0);
		    if("extend".equals(self.getString("paging"))){
		        if(dataObject != null && dataObject.getBoolean("paging")){
		            pageInfo.put("limit", dataObject.getInt("pageSize", 50));
		            pageInfo.put("start", 0);
		        }
		    }else if("yes".equals(self.getString("paging"))){
		        pageInfo.put("limit", self.getInt("pageSize", 50));
		        pageInfo.put("start", 0);
		    }
		    if("_extend".equals(self.getString("storeSortField"))){
		        if(dataObject != null){
		            pageInfo.put("sort", dataObject.getString("storeSortField"));
		            pageInfo.put("dir", dataObject.getString("storeSortDir"));
		        }
		    }else{
		        pageInfo.put("sort", self.getString("storeSortField"));
		        pageInfo.put("dir", self.getString("storeSortDir"));
		    }
		    store.put("pageInfo", pageInfo);
		       		   		    
		    //监听器
		    for(Thing listeners : (List<Thing>) self.get("Listeners@")){
		        for(Thing listener : (List<Thing>) listeners.get("Listener@")){
		            if(listener.getBoolean("afterAttach") == false){
		                 ((List<Thing>) store.get("listeners")).add(listener);
		            }
		        }
		    }		    
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), store);

		if (self.getBoolean("attachToParent") && store != null) {
			store.doAction("attach", actionContext, UtilMap.toMap("object", actionContext.get("parent"), "selectType",
					self.getString("radioAndCheckBox")));
		}
		String parentControls = self.getStringBlankAsNull("parentControls");
		if (parentControls != null) {
			for (String p : parentControls.split("[,]")) {
				Object control = actionContext.get(p);
				store.doAction("attach", actionContext,
						UtilMap.toMap("object", control, "selectType", self.getString("radioAndCheckBox")));
			}
		}

		//选择事件
		String selectionListeners = self.getStringBlankAsNull("selectionListeners");
		if(selectionListeners != null) {
			DataStore dataStore = (DataStore) store.get("dataStore");
			for(String s : selectionListeners.split("[,")) {
				Object listener = actionContext.get(s);
				if(listener instanceof Thing) {
					DataStoreSelectionListener lis = ((Thing) listener).doAction("createDataStoreSelectionListener", actionContext);
					if(lis != null) {
						dataStore.addSelectionListener(lis);
					}
				}else if(listener instanceof DataStoreSelectionListener){
					dataStore.addSelectionListener((DataStoreSelectionListener) listener);
				}
			}
		}
		
		if(!isRef){
		    //监听器
			for(Thing listeners : (List<Thing>) self.get("Listeners@")){
		        for(Thing listener : (List<Thing>) listeners.get("Listener@")){
		            if(listener.getBoolean("afterAttach") == true){
		            	((List<Thing>) store.get("listeners")).add(listener);
		            }
		        }
		    }
		}
		
		//自动加载放在最后，可以避免onLoaded事件激活两次（如果加载过快，config一次，添加监听一次）
		if(!isRef && store.get("dataObject") != null){
		    if(store.getBoolean("autoLoad")){
	            store.doAction("load", actionContext);
	        }
	    }

		//查看是否存在Reactor
		Thing reactor = self.getThing("DataStoreReactor@0");
		if(reactor != null) {
			reactor.doAction("create", actionContext, "dataStore", store);
		}
		return store;
	}
	
	public static Object createDataStoreSelectionListener(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return (DataStore) self.get("dataStore");
	}
	
	/**
	 * 判断一个事物是否是Stroe的实例。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static boolean isInstance(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//实例必定有display变量
		return self.get("display") instanceof Display;
	}

	public static void setSourceDatas(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		DataStore dataStore = (DataStore) self.get("dataStore");
		
		DataObjectList sourceDatas = actionContext.getObject("sourceDatas");
		dataStore.setSourceDatas(sourceDatas);
	}
	
	/**
	 * 从Widget上获取缓存的DataStore的数据对象列表的规范方法。
	 * 
	 * @param control
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> getWidgetRecords(Widget widget){
		return (List<DataObject>) widget.getData(STORE_RECORDS);
	}
	
	/**
	 * 设置数据对象列表到控件的缓存数据上的规范方法。
	 * 
	 * @param widget
	 * @param records
	 */
	public static void setWidgetRecords(Widget widget, List<DataObject> records) {
		widget.setData(STORE_RECORDS, records);
	}
	
	public static void setDataObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing dataObject = (Thing) actionContext.get("dataObject");
		Thing queryConfig = (Thing) actionContext.get("queryConfig");
		
		//数据对象
		if(dataObject == null){
		    return;
		}

		Thing store = self;
		store.put("dataObject", dataObject);

		//查询配置
		if(actionContext.get("queryConfig") != null){
		    store.put("queryConfig", queryConfig);
		}

		//分页
		Map<String, Object> pageInfo = new HashMap<String, Object>();
		pageInfo.put("limit", 0);
		pageInfo.put("start", 0);
		if("extend".equals(self.getString("paging"))){
		    if(dataObject != null && dataObject.getBoolean("paging")){
		        pageInfo.put("limit", dataObject.getInt("pageSize", 50));
		        pageInfo.put("start", 0);
		    }
		}else if("yes".equals(self.getString("paging"))){
		    pageInfo.put("limit", self.getInt("pageSize", 50));
		    pageInfo.put("start", 0);
		}
		if("_extend".equals(self.getString("storeSortField"))){
		    if(dataObject != null){
		        pageInfo.put("sort", dataObject.getString("storeSortField"));
		        pageInfo.put("dir", dataObject.getString("storeSortDir"));
		    }
		}else{
		    pageInfo.put("sort", self.getString("storeSortField"));
		    pageInfo.put("dir", self.getString("storeSortDir"));
		}
		store.put("pageInfo", pageInfo);

		//通知已更新
		self.doAction("reconfig", actionContext);

		if(store.get("dataObject") != null){
		    if(store.getBoolean("autoLoad")){
		        store.doAction("load", actionContext);
		    }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void load(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//数据对象
		Thing dataObject = (Thing) self.get("dataObject");
		if(dataObject == null){
			Executor.warn(TAG, "not dataobject");
		    return;
		}

		//查询配置
		//Thing queryConfig = (Thing) self.get("queryConfig");
		//分页信息
		PageInfo pageInfo = PageInfo.getPageInfo(self.get("pageInfo"));
		pageInfo.setStart(0);

		//查询参数
		Map<String, Object> params = (Map<String, Object>) actionContext.get("params");
		//log.info("params=" + params);
		if(params == null){
		    params = new HashMap<String, Object>();
		}

		/*
		if(queryConfig != null){  //初始化主动抓取的参数值
		    queryConfig.doAction("initParamsSwt", actionContext);
		}
		*/

		self.set("pageInfo", pageInfo.getPageInfoData());
		self.set("params", params);

		//执行查询
		self.doAction("doLoad", actionContext);
	}
	
	public static void reload(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//数据对象
		Object dataObject = self.get("dataObject");
		if(dataObject == null){
			Executor.warn(TAG, "dataobject is null, can not reload");
		    return;
		}

		//执行查询
		self.doAction("doLoad", actionContext);
	}
	
	/**
	 * 数据对象查询的结果是List<DataObject>，把它转化为DataObjectList，这样DataStore可以监听插入和移除等事件。
	 * 
	 * @param store
	 * @param records
	 * @return
	 */
	private static DataObjectList setDataObjectList(Thing store, List<DataObject> records) {
		DataStore ds = (DataStore) store.get("dataStore");
		try {
			ds.setEventEnabled(false);
			ds.setDataObjects(records);
		}finally {
			ds.setEventEnabled(true);
		}
		
		return ds.datas;
	}
	
	public static void doLoad(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		final Thing store = self;
		final ActionContext acContext = actionContext;
		Map<String, Object> event = UtilMap.toMap("doit", true);

		store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "beforeLoad", "event", event));
		if(UtilData.isTrue(event.get("doit")) == false){
		    return;
		}
		           
		if(self.getBoolean("loadBackground") && !SwtUtils.isRWT()){			
		    Runnable runnable = new Runnable(){
		        public void run() {
		            DataObject.beginThreadCache();
		            Thing sourceDataObject = (Thing) ((Thing) store.get("dataObject")).getData("sourceDataObject");
		            DataStore dataStore = (DataStore) store.get("dataStore");
		            try{
		                //源数据对象		                
		                if(sourceDataObject == null){
		                	PageInfo pageInfo  = PageInfo.getPageInfo(store.get("pageInfo"));
		                	sourceDataObject = (Thing) pageInfo.get("sourceDataObject");
		                }
		                if(sourceDataObject == null){
		                    sourceDataObject = (Thing) store.get("dataObject");
		                }
		                store.put("userTask", DataObject.getUserTask(sourceDataObject, acContext));
		                List<DataObject> records = null;
		                acContext.peek().putAll(UtilMap.toMap("store", store, "conditionData", store.get("params"), 
                				"conditionConfig", store.get("queryConfig"), "pageInfo", store.get("pageInfo")));
		                if(dataStore.sourceDatas != null) {
		                	records = DataObjectUtil.query(dataStore.sourceDatas, acContext);
		                }else {
		                	records = sourceDataObject.doAction("query", acContext);
		            	}
		                DataObjectList datas = setDataObjectList(store, records);
		                store.put("records", datas);            
		            }catch(Exception e) {
		            	Executor.error(TAG, "Load dataobject error, dataObject=" + sourceDataObject, e);
		            }finally{
		                DataObject.finishThreadCache();
		                store.put("userTask", null);
		            }
		            //log.info("records=" + store.records);
		            store.put("dataLoaded", true);
		            
		            //如果是动态查询，重新初始化数据对象
		            initDataObject(store, acContext);
		            store.doAction("fireEvent", acContext, UtilMap.toMap("eventName", "onLoaded"));
		            //store.doAction("fireEvent", acContext, ["eventName":"afterLoaded"]);
		        }
		    };
		    		    
		    new Thread(runnable).start();
		}else{
		    //执行查询
		    DataObject.beginThreadCache();
		    try{
		    	store.put("userTask", DataObject.getUserTask((Thing) store.get("dataObject"), acContext));
		    	
		    	//List<DataObject> records = ((Thing) store.get("dataObject")).doAction("query", actionContext, 
		        //		UtilMap.toMap("store", store, "conditionData", store.get("params"), "conditionConfig", store.get("queryConfig"),
		        //				"pageInfo", store.get("pageInfo"))); 
		    	
		    	DataStore dataStore = (DataStore) store.get("dataStore");
		    	List<DataObject> records = null;
                acContext.peek().putAll(UtilMap.toMap("store", store, "conditionData", store.get("params"), 
        				"conditionConfig", store.get("queryConfig"), "pageInfo", store.get("pageInfo")));
                if(dataStore.sourceDatas != null) {
                	records = DataObjectUtil.query(dataStore.sourceDatas, acContext);
                }else {
                	records = ((Thing) store.get("dataObject")).doAction("query", acContext);
            	}
		    	DataObjectList datas = setDataObjectList(store, records);
		        self.put("records", datas);
		    }finally{
		        DataObject.finishThreadCache();
		        store.put("userTask", null);
		    }
		    self.put("dataLoaded", true);
		    
		    //如果是动态查询，重新初始化数据对象
		    initDataObject(store, actionContext);
		                         
		    //触发装载事件
		    self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onLoaded"));
		    //self.doAction("fireEvent", actionContext, ["eventName": "afterLoaded"]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void initDataObject(Thing store, ActionContext acContext){         
	     Thing oldDataObject = (Thing) store.get("dataObject");
	     Map<String, Object> pageInfo = (Map<String, Object>) store.get("pageInfo"); 
	     //if(pageInfo.get("getdynamicDataObject") != null && pageInfo.get("dynamicDataObject") != store.get("dynamicDataObject")){         
	     if(pageInfo.get("dynamicDataObject") != store.get("dynamicDataObject")){
	         //可变数据对象且数据对象已变化
	         store.put("dataObject", pageInfo.get("dynamicDataObject"));
	         store.doAction("reconfig", acContext);
	     }else if(pageInfo.get("dynamicDataObject") == null){
	         //不可变数据对象或可变数据对象已恢复
	         Object sourceDataObject = oldDataObject.getData("sourceDataObject");
	         if(sourceDataObject != null && sourceDataObject != oldDataObject){
	             store.put("dataObject", sourceDataObject);
	             store.doAction("reconfig", acContext);
	         }
	     }
	}
	
	public static void fireSelectionEvent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataStore dataStore = (DataStore) self.get("dataStore");
		List<DataObject> dataObjects = actionContext.getObject("dataObjects");
		dataStore.fireSelectionEvent(dataObjects);
		
		//触发dataStore自身的onSelected事件
		self.doAction("onSelected", actionContext);
	}
	
	public static void addSelectionListener(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		DataStore dataStore = (DataStore) self.get("dataStore");
		Object listener = actionContext.get("listener");
		if(listener instanceof Thing) {
			DataStoreSelectionListener lis = ((Thing) listener).doAction("createDataStoreSelectionListener", actionContext);
			if(lis != null) {
				dataStore.addSelectionListener(lis);
			}
		}else if(listener instanceof DataStoreSelectionListener){
			dataStore.addSelectionListener((DataStoreSelectionListener) listener);
		}
			
	}
	
	public static void addListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing listener = (Thing) actionContext.get("listener");
		
		if(UtilData.isTrue(self.get("dataLoaded"))){
		    listener.doAction("onLoaded", actionContext, UtilMap.toMap("store", self));
		}

		//避免重复加入
		List<Thing> listeners = self.getObject("listeners");
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}		
	}
	
	public static void addListenerToFirst(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing listener = (Thing) actionContext.get("listener");
		
		if(UtilData.isTrue(self.get("dataLoaded"))){
		    listener.doAction("onLoaded", actionContext, UtilMap.toMap("store", self));
		}

		//避免重复加入
		List<Thing> listeners = self.getObject("listeners");
		if(!listeners.contains(listener)) {
			listeners.add(0, listener);
		}	
	}
	
	@SuppressWarnings("unchecked")
	public static void removeListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing listener = (Thing) actionContext.get("listener");
		
		((List<Thing>) self.get("listeners")).remove(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void fireEvent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//Bindings bindings = actionContext.peek();
		String eventName = (String) actionContext.get("eventName");
		
		List<Thing> listeners = ((List<Thing>) self.get("listeners"));
		for(int i=0; i<listeners.size(); i++){
		    Thing listener = listeners.get(i);
		    try {
		    	listener.doAction(eventName, actionContext, UtilMap.toMap("store", self));
		    }catch(Exception e) {
		    	Executor.warn(TAG, "Invoke datastore event '" + eventName + "' error", e);
		    }
		}
	}
	
	public static void reconfig(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onReconfig", "store",  self));
	}
	
	public static void attach(ActionContext actionContext){
		Object object = actionContext.get("object");
		if(object == null) {
			return;
			
		}
		
		Thing self = (Thing) actionContext.get("self");
		Thing attribute = (Thing) actionContext.get("attribute");
		DataStore ds = self.getObject("dataStore");
		if(object instanceof Widget) {
			Widget widget = (Widget) object;
			if(widget.isDisposed()) {
				return;
			}
			//绑定控件的dispose等事件
			ds.attach((Widget) object);
		}
		
		if(object instanceof Control) {
			//支持第三方的情形，如果创建控件的事物有attachDataStore方法则绑定
			Thing parentThing = Designer.getThing((Control) object);
			if(parentThing != null && parentThing.getActionThing("attachDataStore") != null) {
				parentThing.doAction("attachDataStore", actionContext, "dataStore", self, "control", object);
				return;
			}
		}		 
		
		if(object instanceof DataStoreListener) {
			//绑定数据仓库监听器
			ThingDataStoreListener.attach(self, (DataStoreListener) object, actionContext) ;
		}		
		
		if(object instanceof ActionContainer) {
			//通过ActionContainer绑定
			ActionContainer actions = (ActionContainer) object;
			if(actions.getActionThing("attachDataStore") != null) {
				if(actions.getActionThing("attachDataStore") != null) {
					actions.doAction("attachDataStore", actionContext, "dataStore", self, "control", object);
					return;
				}
			}
		}
		
		if(object instanceof Table){   
		    self.doAction("attachToTable", actionContext);
		}else if(object instanceof Tree){   
		    self.doAction("attachToTree", actionContext);
		}else if(object instanceof CCombo){
		    self.doAction("attachToCCombo", actionContext);
		}else if(object instanceof Combo){
		    self.doAction("attachToCombo", actionContext);
		}else if(object instanceof org.eclipse.swt.widgets.List){
		    self.doAction("attachToList", actionContext);
		}else if(object instanceof ActionContainer){
		    //有可能是绑定到MultCombo
			ActionContainer acc = (ActionContainer) object;
			ActionContext ac = acc.getActionContext();
		    if(ac.get("comboThing") != null){
		    	Thing comboThing = (Thing) ac.get("comboThing");
		    	comboThing.put("dataSource", "selfDataStore");
		        comboThing.put("dataName", "store");
		        ac.getScope(0).put("store", self);
		        if(actionContext.get("attribute") != null){
		            //shi dd
		            //log.info("attribute=" + attribute);
		            comboThing.put("idName", attribute.get("relationValueField"));
		            comboThing.put("labelName", attribute.get("relationLabelField"));
		        }
		        //log.info("store name=" + self.name);
		    }
		}else if(object instanceof Composite){
		    Thing listener = null;
		    String selectType = (String) actionContext.get("selectType");
		    if(selectType == null){
		        selectType = "checkBox";
		    }
		    if("radio".equals(selectType)){
		        listener = new Thing("xworker.app.view.swt.data.events.RadioDataStoreListener");
		    }else if("checkBox".equals(selectType)){
		        listener = new Thing("xworker.app.view.swt.data.events.CheckBoxDataStoreListener");
		    }
		    if(listener != null){
		        listener.put("composite", object);
		        listener.put("store",  self);
		        listener.put("modifyListener", actionContext.get("modifyListener"));
		        listener.put("selectType", selectType);
		        
		        //log.info("call onReconfig," + listener);
		        //先调用监听初始化
		        listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));
		        
		        //加入到监听器中
		        self.doAction("addListener", actionContext, UtilMap.toMap("listener", listener));
		        
		        //把监听和自身添加到table中以备后用
		        ((Composite) object).setData("storeListener", listener);
		        ((Composite) object).setData("store", self);
		    }
		}else if(object instanceof Thing) {
			Thing thing = (Thing) object;
			String thingName = thing.getThingName();
			if("PagingToolbar".equals(thingName)) {
				//绑定到分页组件
				thing.doAction("setStore", actionContext, "store", self);
			}
		}
	}
	
	public static void attachToTable(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object object = actionContext.get("object");
		
		//添加一个表格数据仓库监听
		Table table = (Table) object;
		Thing listener = new Thing("xworker.app.view.swt.data.events.TableDataStoreListener");
		listener.put("table", object);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));

		//加入到监听器中
		self.doAction("addListenerToFirst", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		table.setData("storeListener", listener);
		table.setData("store", self);

		DataStoreDisposeListener.attach(table);
	}
	
	public static void attachToTree(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object object = actionContext.get("object");
		
		//添加一个表格数据仓库监听
		Tree tree = (Tree) object;
		Thing listener = new Thing("xworker.app.view.swt.data.events.TreeDataStoreListener");
		listener.put("tree", object);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));

		//加入到监听器中
		self.doAction("addListenerToFirst", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		tree.setData("storeListener", listener);
		tree.setData("store", self);

		DataStoreDisposeListener.attach(tree);
	}
	
	public static void attachToCCombo(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Control object = (Control) actionContext.get("object");
		
		//添加一个表格数据仓库监听
		Thing listener = new Thing("xworker.app.view.swt.data.events.CComboDataStoreListener");
		listener.put("combo", object);
		listener.put("store", self);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));

		//加入到监听器中
		self.doAction("addListenerToFirst", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		object.setData("storeListener", listener);
		object.setData("store", self);

		if(object instanceof CCombo){
			DataStoreDisposeListener.attach((CCombo) object);
		}else if(object instanceof Combo){
			DataStoreDisposeListener.attach((Combo) object);
		}
	}
	
	public static void attachToList(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		org.eclipse.swt.widgets.List object = (org.eclipse.swt.widgets.List) actionContext.get("object");
		
		//添加一个表格数据仓库监听
		Thing listener = new Thing("xworker.app.view.swt.data.events.ListDataStoreListener");
		listener.put("combo", object);
		listener.put("store", self);

		//先调用监听初始化
		listener.doAction("onReconfig", actionContext, UtilMap.toMap("store", self));

		//加入到监听器中
		self.doAction("addListenerToFirst", actionContext, UtilMap.toMap("listener", listener));

		//把监听和自身添加到table中以备后用
		object.setData("storeListener", listener);
		object.setData("store", self);

		DataStoreDisposeListener.attach(object);
	}
	
	@SuppressWarnings("unchecked")
	public static Object insert(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		if(self.get("dataObject") == null){
		    return null;   //未绑定DataObject
		}

		if(self.get("records") == null){
		    self.put("records", new ArrayList<Object>());
		}
		
		List<Object> records = (List<Object>) self.get("records"); 
		Integer index = (Integer) actionContext.get("index");
		if(index == null || index < 0){
		    index = records.size();
		}
		boolean save = true;
		if(UtilData.isTrue(actionContext.get("noSaveDataObject"))){
		    save = false;
		}
		
		if(actionContext.get("record") != null){
			Object record = actionContext.get("record");
		    if(record instanceof DataObject){
		    	DataObject drecord = (DataObject) record;
		        drecord.setFlag(DataObject.FLAG_NEW);
		        if(save && self.getBoolean("autoSave")){
		            drecord.doAction("create", actionContext);
		        }
		        records.add(index, record);
		        index++;        
		        //self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
		        //		"records", UtilData.toList(record), "index", index));
		        
		        return drecord;
		    }else if(record instanceof Map){
		        DataObject drecord = new DataObject((Thing) self.get("dataObject"));
		        drecord.setFlag(DataObject.FLAG_NEW);
		        drecord.putAll((Map<String, Object>) record);
		        if(save && self.getBoolean("autoSave")){
		            drecord.doAction("create", actionContext);
		        }
		        records.add(index, drecord);
		        index++;
		        self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
		        		"records", UtilData.toList(record), "index", index));
		        
		        return drecord;
		    }else{
		    	Thing dataObject = (Thing) self.get("dataObject");
		        DataObject r = (DataObject) dataObject.doAction("createDataObjectFromObject", actionContext,
		        		UtilMap.toMap("data", record, "descriptor", dataObject));
		        if(r != null){
		            r.setFlag(DataObject.FLAG_NEW);
		            if(save && self.getBoolean("autoSave")){
		                r.doAction("create", actionContext);
		            }
		            records.add(index, r);
		            index++;        
		            self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
			        		"records", UtilData.toList(r), "index", index));
		            
		            return r;
		        }else{
		            return null;
		        }
		    }
		}

		if(actionContext.get("records") != null){
		    List<Object> rs = new ArrayList<Object>();
		    List<Object> recordsForDelte  = (List<Object>) actionContext.get("records");
		    for(Object record : recordsForDelte){
		        if(record instanceof DataObject){
		        	DataObject drecord = (DataObject) record;
		            drecord.setFlag(DataObject.FLAG_NEW);
		            if(save && self.getBoolean("autoSave")){
		                drecord.doAction("create", actionContext);
		            }
		            records.add(index,record);
		            index++;
		            rs.add(record);
		        }else if(record instanceof Map){
		            DataObject drecord = new DataObject((Thing) self.get("dataObject"));
		            drecord.setFlag(DataObject.FLAG_NEW);
		            drecord.putAll((Map<String, Object>) record);
		            if(save && self.getBoolean("autoSave")){
		                drecord.doAction("create", actionContext);
		            }
		            records.add(index,drecord);
		            index++;
		            rs.add(drecord);            
		        }else{
		            DataObject r = (DataObject) ((Thing) self.get("dataObject")).doAction("createDataObjectFromObject", actionContext,
		            		UtilMap.toMap("data", record, "descriptor", self.get("dataObject")));
		            if(r != null){
		                r.setFlag(DataObject.FLAG_NEW);
		                if(save && self.getBoolean("autoSave")){
		                    r.doAction("create", actionContext);
		                }
		                records.add(index, r);
		                index++;        
		                rs.add(r);
		            }
		        }
		    }
		    
		    self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", "records" ,rs, "index", index));
		    
		    return rs;
		}else{
		    return null;
		}
	}
	
	public static void insertNoSave(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		self.doAction("insert", actionContext, UtilMap.toMap("noSaveDataObject", true));
	}
	
	@SuppressWarnings("unchecked")
	public static Object update(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing dataObject = (Thing) self.get("dataObject");
		List<Object> records = (List<Object>) self.get("records");
		
		if(dataObject == null){
		    return null;   //未绑定DataObject
		}

		if(records == null){
		    return null;
		}

		if(actionContext.get("record") != null){
			Object record = actionContext.get("record");
		    if(record instanceof DataObject){
		        if(self.getBoolean("autoSave")){
		            ((DataObject) record).doAction("update", actionContext);
		        }
		        
		        //重新打开，以前是关闭的，但是关闭导致了Table编辑时展示数据的问题，即没有格式化数据
		        List<Object> list = new ArrayList<Object>();
		        list.add(record);
		        self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onUpdate", 
		        		"records", list));		        
		        return record;
		    }else{
		        return null;
		    }
		}

		if(actionContext.get("records") != null){
		    List<Object> rs = new ArrayList<Object>();
		    List<Object> recordsForDelte  = (List<Object>) actionContext.get("records");
		    for(Object record : recordsForDelte){
		        if(record instanceof DataObject){
		            if(self.getBoolean("autoSave")){
		                ((DataObject) record).doAction("update", actionContext);
		            }
		            rs.add(record);
		        }
		    }
		    
		    self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onUpdate", "records",rs));		    
		    return rs;
		}else{
		    return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object remove(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing dataObject = (Thing) self.get("dataObject");
		DataStore dataStore = (DataStore) self.get("dataStore");
		List<Object> records = (List<Object>) self.get("records");
		
		if(dataObject == null){
		    return null;   //未绑定DataObject
		}

		if(records == null){
		    return null;
		}

		
		if(self.get("removedRecords") == null){
		    self.put("removedRecords", new ArrayList<Object>());
		}
		List<Object> removedRecords = (List<Object>) self.get("removedRecords");

		if(actionContext.get("record") != null){
			Object record = actionContext.get("record");
		    if(record instanceof DataObject){
		        if(self.getBoolean("autoSave")){
		            ((DataObject) record).doAction("delete", actionContext);		            
		        }else{
		        	//removedRecords.add(record);
		        }
		        
		        //records.remove(record);
		        dataStore.remove((DataObject) record);
		        //self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onRemove", 
		        //		"records", UtilData.toList(record)));
		        
		        return record;
		    }else{
		        return null;
		    }
		}

		if(actionContext.get("records") != null){
			List<Object> recordsForRemoved = (List<Object>) actionContext.get("records");
		    List<Object> rs = new ArrayList<Object>();
		    for(int i=0; i<recordsForRemoved.size(); i++){
		    	Object record = recordsForRemoved.get(i);
		        if(record instanceof DataObject){
		            if(self.getBoolean("autoSave")){
		                ((DataObject) record).doAction("delete", actionContext);
		                //recordsForRemoved.remove(i);
		            }else{
		                removedRecords.add(record);
		            }
		            
		            rs.add(record);
		            //records.remove(record);
		            dataStore.remove((DataObject) record);
		        }
		    }
		    
		    /*
		    if(actionContext.get("index") != null){
		        self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onRemove", "records", rs));
		    }else{
		        self.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onRemove", "records", rs));
		    }*/
		    
		    return rs;
		}else{
		    return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object findRecordById(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//Thing dataObject = (Thing) self.get("dataObject");
		List<Object> records = (List<Object>) self.get("records");
		Object id = actionContext.get("id");
		
		if(records == null){
		    return null;
		}

		for(Object record : records){
		    Object[][] keys = ((DataObject) record).getKeyAndDatas();
		    for(Object[] key : keys){
		        if(UtilCondition.eq(key[1] , id, null, null, false, actionContext)){
		            return record;
		        }
		    }
		}

		return false;
	}
	
	public static void openCreateForm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		//SWT的窗体的父
		Display display = Display.getCurrent();
		if(display == null){
		    return;
		}
		Object parent = display.getActiveShell();
		if(parent == null){
		    parent = display;
		}

		//创建编辑窗体
		ActionContext ac = new ActionContext();
		ac.put("store", self);
		ac.put("parent", parent);
		ac.put("formType", "create");
		ac.put("parentContext", actionContext);
		ac.put("values", actionContext.get("values"));
		Thing editorThing = world.getThing("xworker.app.view.swt.widgets.table.DataObjectGridRowEditor/@shell");
		editorThing.doAction("create", ac);
		((Thing) ac.get("form")).doAction("setDataObject", ac, UtilMap.toMap("dataObject", self.get("dataObject")));
		if(actionContext.get("values") != null){
			((Thing) ac.get("form")).doAction("setPartialValues", ac, UtilMap.toMap("values", actionContext.get("values")));
		}
		
		Shell shell = (Shell) ac.get("shell");
		shell.pack();
		SwtUtils.centerShell(shell);
		shell.open();
	}
	
	public static void openEditForm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		//SWT的窗体的父
		Display display = Display.getCurrent();
		if(display == null){
		    return;
		}
		Object parent = display.getActiveShell();
		if(parent == null){
		    parent = display;
		}

		//创建编辑窗体
		ActionContext ac = new ActionContext();
		ac.put("store", self);
		ac.put("parent", parent);
		ac.put("formType", "edit");
		ac.put("parentContext", actionContext);
		Thing editorThing = world.getThing("xworker.app.view.swt.widgets.table.DataObjectGridRowEditor/@shell");
		editorThing.doAction("create", ac);
		((Thing) ac.get("form")).doAction("setDataObject", ac, UtilMap.toMap("dataObject", actionContext.get("record")));
		Shell shell = (Shell) ac.get("shell");
		shell.pack();
		SwtUtils.centerShell(shell);
		shell.open();
	}
	
	@SuppressWarnings("unchecked")
	public static void commit(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		for(DataObject record : (List<DataObject>) self.get("records")){
	        record.commit(actionContext);
		}

		/*
		List<DataObject> removedRecords = (List<DataObject>) self.get("removedRecords"); 
		for(DataObject record : removedRecords){
		    record.commit(actionContext);
		}
		removedRecords.clear();*/
	}
	
	public static void removeBatch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//数据对象
		Thing dataObject = (Thing) self.get("dataObject");
		if(dataObject == null){
			Executor.warn(TAG, "not dataobject");
		    return;
		}

		//查询配置
		Thing queryConfig = (Thing) self.get("queryConfig");

		//查询参数
		Object params = actionContext.get("params");
		if(params == null){
		    params = new HashMap<String, Object>();
		}
		if(queryConfig != null){  //初始化主动抓取的参数值
		    queryConfig.doAction("initParamsSwt", actionContext);
		}

		//执行批量删除
		dataObject.doAction("deleteBatch", actionContext, UtilMap.toMap("conditionData", params, "conditionConfig", queryConfig));            

		//执行完毕后重新查询
		self.doAction("reload", actionContext);
	}
	
	/**
	 * 从创建后的数据仓库事物上获取DataStore对象。
	 * 
	 * @param dataStore
	 * @return
	 */
	public static DataStore getDataStore(Thing dataStore) {
		return (DataStore) dataStore.get("dataStore");
	}
	
	public static void updateBatch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//数据对象
		Thing dataObject = (Thing) self.get("dataObject");
		if(dataObject == null){
			Executor.warn(TAG, "not dataobject");
		    return;
		}

		//查询配置
		Thing queryConfig = (Thing) self.get("queryConfig");

		//查询参数
		Object params = actionContext.get("params");
		if(params == null){
		    params = new HashMap<String, Object>();
		}
		if(queryConfig != null){  //初始化主动抓取的参数值
		    queryConfig.doAction("initParamsSwt", actionContext);
		}

		//执行批量删除
		dataObject.doAction("updateBatch", actionContext, 
				UtilMap.toMap("conditionData", params, "conditionConfig", queryConfig, "theData", actionContext.get("record")));            

		//执行完毕后重新查询
		self.doAction("reload", actionContext);
	}

	/**
	 * 触发选择事件。
	 * 
	 * @param dataObjects
	 */
	public void fireSelectionEvent(List<DataObject> dataObjects) {
		for(DataStoreSelectionListener listener : selectionListeners) {
			try {
				listener.selected(dataObjects);
			}catch(Exception e) {
				Executor.error(TAG, "Fire event listener error", e);
			}
		}
	}
	
	@Override
	public void onLoaded(DataObjectList list) {
		for(DataObject dataObject : list) {
			dataObject.addListener(this);
		}
		
		if(this.eventEanbeld) {
			store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onLoaded"));
		}
	}

	@Override
	public void onAdded(DataObjectList list, DataObject dataObject) {		
		dataObject.addListener(this);
		
		int index = list.indexOf(dataObject);
		if(index <= 0) {
			index = list.size() - 1;
		}
		if(this.eventEanbeld) {
			store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
	        		"records", UtilData.toList(dataObject), "index", index));
		}
		
	}

	@Override
	public void onAdded(DataObjectList list, int index, DataObject dataObject) {
		dataObject.addListener(this);
		
		if(this.eventEanbeld) {
			store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
	        		"records", UtilData.toList(dataObject), "index", index));
		}
	}

	@Override
	public void onAddedAll(DataObjectList list, int index, Collection<? extends DataObject> c) {
		List<DataObject> dataObjects = new ArrayList<DataObject>();
		dataObjects.addAll(c);
		for(DataObject dataObject : dataObjects) {
			dataObject.addListener(this);
		}
		
		if(this.eventEanbeld) {
			store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onInsert", 
	        		"records", c, "index", index));
		}
	}

	@Override
	public void onRemoved(DataObjectList list, int index, DataObject dataObject) {
		dataObject.removeListener(this);
		
		if(this.eventEanbeld) {
		    store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onRemove", 
        	    	"records", UtilData.toList(dataObject)));
		}
	}

	@Override
	public void onSeted(DataObjectList list, int index, DataObject newDataObject, DataObject oldDataObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changed(DataObject dataObject) {
		changedDataObjects.add(dataObject);
		dataObjectChangedTask.doTask();				
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		widgets.remove(e.widget);
		
		if(widgets.size() == 0) {
			//这个时候应该被销毁了
			this.datas.removeListener(this);
			
			if(this.sourceDatas != null) {
				this.sourceDatas.removeListener(sourceListener);
			}
		}
	}
	
	public static class SourceDataObjectListListener implements DataObjectListListener{
		DataStore dataStore;
		String sourceAddType = "end";
		
		public SourceDataObjectListListener(DataStore dataStore) {
			this.dataStore = dataStore;
			if(dataStore != null && dataStore.store != null) {
				sourceAddType = this.dataStore.store.getStringBlankAsNull("sourceDataAddType");
			}
		}

		@Override
		public void onLoaded(DataObjectList list) {
			dataStore.reload();
		}

		public void addDataObject(DataObject dataObject) {
			Thing conditionConfig = dataStore.getQueryConfig();
			Object params = dataStore.getQueryParams();
			boolean noFilter = true;
			if(dataStore.store != null) {
				noFilter = dataStore.store.getBoolean("sourceDataNoFilter");
			}
			if(!noFilter && conditionConfig != null) {
				Object result = conditionConfig.doAction("isMatch", dataStore.actionContext, "condition",params, "data", dataObject);
				if(UtilData.isTrue(result) == false) {
					return;
				}
			}
			
			if("first".equals(sourceAddType)) {
				dataStore.datas.add(0, dataObject);
			}else {
				dataStore.datas.add(dataObject);
			}
		}
				
		@Override
		public void onAdded(DataObjectList list, DataObject dataObject) {
			addDataObject(dataObject);
		}

		@Override
		public void onAdded(DataObjectList list, int index, DataObject dataObject) {
			addDataObject(dataObject);
		}

		@Override
		public void onAddedAll(DataObjectList list, int index, Collection<? extends DataObject> c) {
			for(DataObject dataObject : c) {
				addDataObject(dataObject);
			}
		}

		@Override
		public void onRemoved(DataObjectList list, int index, DataObject dataObject) {
			dataStore.datas.remove(dataObject);
		}

		@Override
		public void onSeted(DataObjectList list, int index, DataObject newDataObject, DataObject oldDataObject) {
			int index_ = dataStore.datas.indexOf(oldDataObject);
			if(index_ != -1) {
				dataStore.datas.set(index_, newDataObject);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void selected(List<DataObject> dataObjects) {
		//当需要监听其它DataStore时
		if(dataObjects == null || dataObjects.size() == 0) {
			this.load(Collections.EMPTY_MAP);
		}else {
			//把选择数据对象作为查询条件
			this.load(dataObjects.get(0));
		}
	}
}
