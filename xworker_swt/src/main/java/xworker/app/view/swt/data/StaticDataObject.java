package xworker.app.view.swt.data;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;

public class StaticDataObject {
	//private static final String TAG = "StaticDataObject";
	
	public static DataObject createDataObject(Thing self, Thing dataObjectDesc, ActionContext actionContext) {
		DataObject data = new DataObject(dataObjectDesc);
		
		//初始化的值
		for(Thing child : self.getChilds()) {
			if(child.isThing("xworker.app.view.swt.data.StaticDataObject/@InitialValues")) {
				data.putAll(child.getAttributes());
			}
		}
		
		//执行数据对象自己的初始化方法
		data.init(actionContext);
		
		//使用文本设置的值
		String attributes = self.doAction("getAttributes", actionContext);
		if(attributes != null) {
			data.setAttributes(attributes, actionContext);
		}
				
		return data;
	}
	
	public static DataObject getStaticDataObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataObject dataObject = null;		
		String scope = self.doAction("getScope", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		String key = "StaticDataObject:" + dataDesc.getMetadata().getPath();
		
		World world = World.getInstance();
		
		if("world".equals(scope)) {
			//系统全局的
			dataObject = world.getData(key);
			if(dataObject == null) {
				dataObject = createDataObject(self, dataDesc, actionContext);
				world.setData(key, dataObject);
			}
		}else if("thing".equals(scope)) {
			//事物全局的
			Thing staticThing = self.doAction("getStaticThing", actionContext);
			if(staticThing == null) {
				staticThing = dataDesc;
			}
			
			dataObject = staticThing.getStaticData(key);
			if(dataObject == null) {
				dataObject = createDataObject(self, dataDesc, actionContext);
				staticThing.setStaticData(key, dataObject);
			}
		}else {
			//保存变量上下文，总是创建
			dataObject = createDataObject(self, dataDesc, actionContext);
		}		
		
		return dataObject;
	}
	
	public static void removeStaticDataObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String scope = self.doAction("getScope", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		String key = "StaticDataObject:" + dataDesc.getMetadata().getPath();
		
		World world = World.getInstance();
		
		if("world".equals(scope)) {
			//系统全局的
			world.setData(key, null);
		}else if("thing".equals(scope)) {
			//事物全局的
			Thing staticThing = self.doAction("getStaticThing", actionContext);
			if(staticThing == null) {
				staticThing = dataDesc;
			}
			
			staticThing.setStaticData(key, null);
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObject dataObject = self.doAction("getStaticDataObject", actionContext);
		
		actionContext.g().put(self.getMetadata().getName(), dataObject);
		/*
		 * 2019-11-20 取消了绑定的功能，要实现绑定可以通过DataObjectBinder或Reactors框架
		DataObjectBinder binder = new DataObjectBinder(dataObject, (Control) actionContext.get("parent"), actionContext);
		if(self.getBoolean("bindToParent")) {
			Object parent = actionContext.get("parent");
			if(parent != null) {
				self.doAction("bind" + parent.getClass().getSimpleName(), actionContext, 
						"parent", parent, "dataObject", dataObject, "binder", binder);
			}
		}
		List<String> bindTo = self.doAction("getBindTo", actionContext);
		for(String var : bindTo) {
			Object parent = actionContext.get(var);
			if(parent != null) {
				self.doAction("bind" + parent.getClass().getSimpleName(), actionContext, 
						"parent", parent, "dataObject", dataObject, "binder", binder, "parentName", var);
			}
		}
		
		for(Thing binderItems : self.getChilds("BinderItems")) {
			for(Thing child : binderItems.getChilds()) {
				try {
					BinderItem function = child.doAction("create", actionContext, "binder", binder);
					if(function != null) {
						binder.addBinderItem(function);
					}
				}catch(Exception e) {
					Executor.warn(TAG, "Create BinderItem error, path=" + child.getMetadata().getPath(), e);					
				}
			}
		}*/
				
		actionContext.g().put(self.getMetadata().getName(), dataObject);		
		//actionContext.g().put(self.getMetadata().getName() + "Binder", binder);
		return dataObject;
	}
	
	public static void bindThing(ActionContext actionContext) {
		Thing thing = actionContext.getObject("parent");
		//DataObject dataObject = actionContext.getObject("dataObject");
		
		thing.doAction("setDataObject", actionContext);
	}
	
	/*
	public static void bindText(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		bindId(actionContext);
	}
	
	public static void bindStyledText(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		bindId(actionContext);
	}
	
	public static void bindButton(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		bindDisplayText(actionContext);
	}
		
	public static void bindLabel(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		bindDisplayText(actionContext);
	}
	
	public static void bindDisplayText(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DataObjectBinder binder = actionContext.getObject("binder");
		DataObject dataObject = actionContext.getObject("dataObject");		
		String parentName = actionContext.getObject("parentName");
		if(parentName == null) {
			return;
		}
		
		String idName = getDisplayName(dataObject);
		if(idName != null) {
			Thing itemThing = new Thing("xworker.dataObject.swt.bind.reflect.Text");
			itemThing.put("valuePath", idName);
			itemThing.put("widget", parentName);
			
			binder.addBinderItem(new TextItem(itemThing, actionContext));
		}
	}
	
	public static void bindId(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DataObjectBinder binder = actionContext.getObject("binder");
		DataObject dataObject = actionContext.getObject("dataObject");		
		String parentName = actionContext.getObject("parentName");
		if(parentName == null) {
			return;
		}
		
		String idName = getIdName(dataObject);
		if(idName != null) {
			Thing itemThing = new Thing("xworker.dataObject.swt.bind.reflect.Text");
			itemThing.put("valuePath", idName);
			itemThing.put("widget", parentName);
			
			binder.addBinderItem(new TextItem(itemThing, actionContext));
		}
	}
	
	public static String getIdName(DataObject dataObject) {
		if(dataObject == null || dataObject.getMetadata().getDescriptor() == null) {
			return null;
		}
		
		Thing desc = dataObject.getMetadata().getDescriptor();
		String valueName= desc.getStringBlankAsNull("valueName");
		if(valueName != null) {
			return valueName;
		}
		
		Thing[] keys = dataObject.getMetadata().getKeys();
		if(keys != null && keys.length > 0) {
			valueName = keys[0].getMetadata().getName();
		}else {
			List<Thing> attrs = dataObject.getMetadata().getAttributes();
			if(attrs.size() > 0) {
				valueName = attrs.get(0).getMetadata().getName();
			}
		}
		
		return valueName;
	}
	
	public static String getDisplayName(DataObject dataObject) {
		if(dataObject == null || dataObject.getMetadata().getDescriptor() == null) {
			return null;
		}
		
		Thing desc = dataObject.getMetadata().getDescriptor();
		String displayName= desc.getStringBlankAsNull("displayName");
		if(displayName != null) {
			return displayName;
		}
		
		Thing[] keys = dataObject.getMetadata().getKeys();
		if(keys != null && keys.length > 0) {
			displayName = keys[0].getMetadata().getName();
		}else {
			List<Thing> attrs = dataObject.getMetadata().getAttributes();
			if(attrs.size() > 0) {
				displayName = attrs.get(0).getMetadata().getName();
			}
		}
		
		return displayName;
	}*/
}
