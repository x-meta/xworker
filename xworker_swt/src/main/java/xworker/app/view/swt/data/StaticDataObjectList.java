package xworker.app.view.swt.data;

import org.eclipse.swt.widgets.Table;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObjectList;
import xworker.lang.executor.Executor;

public class StaticDataObjectList {
	private static DataObjectList createDataObjectList(Thing self, Thing dataDesc, ActionContext actionContext) {
		DataObjectList dataObjectList = new DataObjectList(dataDesc);
		Thing initAction = self.doAction("getInitAction", actionContext);
		if(initAction != null) {
		    initAction.getAction().run(actionContext, "self", self, "dataObjectList", dataObjectList);	
		}
		
		//执行初始化操作
		self.doAction("init", actionContext, "dataObjectList", dataObjectList);
		
		return dataObjectList;
	}
	
	public static void removeDataObjectList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing staticThing = self.doAction("getStaticThing", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		if(dataDesc == null) {
			return;
		}
		if(staticThing == null) {
			staticThing = self;
		}else if(staticThing.isThing("xworker.dataObject.staticdata.StaticDataObjectList")) {
			return;
		}
		
		//创建或返回数据对象列表		
		String staticKey = "StaticDataObjectList：" + dataDesc.getMetadata().getPath();
		String scope = self.doAction("getScope", actionContext);
		if("world".equals(scope)){
			World.getInstance().setData(staticKey, null);
		}else if("thing".equals(scope)) {
			staticThing.setStaticData(staticKey, null);
		}
	}
	
	public static DataObjectList getDataObjectList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataObjectList dataObjectList = null;
		Thing staticThing = self.doAction("getStaticThing", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		if(dataDesc == null) {
			throw new ActionException("DataObject can not be null, path=" + self.getMetadata().getPath());
		}
		if(staticThing == null) {
			staticThing = self;
		}else if(staticThing.isThing("xworker.dataObject.staticdata.StaticDataObjectList")) {
			return staticThing.doAction("getData", actionContext);
		}
		
		//创建或返回数据对象列表		
		String staticKey = "StaticDataObjectList：" + staticThing.getMetadata().getPath();
		String scope = self.doAction("getScope", actionContext);
		if("world".equals(scope)){
			dataObjectList = World.getInstance().getData(staticKey);
			if(dataObjectList ==null) {
				dataObjectList = createDataObjectList(self, dataDesc, actionContext);
				World.getInstance().setData(staticKey, dataObjectList);
			}
		}else if("thing".equals(scope)) {
			dataObjectList = staticThing.getStaticData(staticKey);
			if(dataObjectList ==null) {
				dataObjectList = createDataObjectList(self, dataDesc, actionContext);
				staticThing.setStaticData(staticKey, dataObjectList);
			}
		}else {
			dataObjectList = createDataObjectList(self, dataDesc, actionContext);
		}
		
		return dataObjectList;
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataObjectList dataObjectList = self.doAction("getDataObjectList", actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), dataObjectList);
		
		//绑定控件
		Thing dataObject = dataObjectList.getDescriptor();
		if(dataObject == null) {
			Executor.warn("StaticDataObjectList_SWT","DataObject is null, can not create dataStore, path=" 
						+ self.getMetadata().getPath());
		}else {
			Thing dataStore = createDataStore(self, dataObject, actionContext);
			DataStore ds = (DataStore) dataStore.get("dataStore");
			dataStore.set("records", dataObjectList);
			ds.setSourceDatas(dataObjectList);		
			dataStore.set("dataLoaded", true);
		}
		
		/*
		if(self.getBoolean("bindToParent")) {
			Object parent = actionContext.get("parent");
			if(parent != null) {
				self.doAction("bind", actionContext, 
						"parent", parent, "dataObjectList", dataObjectList, "dataObject", dataObjectList.getDescriptor());
			}
		}
		List<String> bindTo = self.doAction("getBindTo", actionContext);
		for(String var : bindTo) {
			Object parent = actionContext.get(var);
			if(parent != null) {
				self.doAction("bind", actionContext, 
						"parent", parent, "dataObjectList", dataObjectList, "dataObject", dataObjectList.getDescriptor());
			}
		}
		*/
		return dataObjectList;
	}
	
	public static void bind(ActionContext actionContext) {
		Table table = actionContext.getObject("parent");
		Thing self = actionContext.getObject("self");
		Thing dataObject = actionContext.getObject("dataObject");
		DataObjectList dataObjectList = actionContext.getObject("dataObjectList");
		
		Thing dataStore = createDataStore(self, dataObject, actionContext);
		DataStore ds = (DataStore) dataStore.get("dataStore");
		ds.setDataObjectList(dataObjectList);
		dataStore.set("records", dataObjectList);
		dataStore.set("dataLoaded", true);
		
		dataStore.doAction("attach", actionContext, UtilMap.toMap("object", table, "selectType", self.getString("radioAndCheckBox")));
	}
	
	public static Thing createDataStore(Thing self, Thing dataObject, ActionContext actionContext) {
		Thing dataStore = new Thing("xworker.swt.widgets.Table/@DataStore");
		dataStore.put("name", self.getMetadata().getName() + "DataStore");
		//dataStore.put("attachToParent", "false");
		dataStore.put("loadBackground", "false");
		dataStore.put("autoLoad", "false");
		dataStore.put("dataObject", dataObject.getMetadata().getPath());
		//System.out.println(dataObject.getMetadata().getPath());
		dataStore.put("parentControls", self.getString("bindTo"));		
		dataStore.put("attachToParent", self.getString("bindToParent"));
		
		return dataStore.doAction("create", actionContext);
	}
}
