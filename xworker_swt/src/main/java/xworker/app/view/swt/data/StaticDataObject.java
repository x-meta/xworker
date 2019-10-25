package xworker.app.view.swt.data;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.swt.bind.BinderItem;
import xworker.dataObject.swt.bind.DataObjectBinder;
import xworker.lang.executor.Executor;

public class StaticDataObject {
	private static final String TAG = "StaticDataObject";
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataObject dataObject = null;
		String key = "StaticDataObject";
		Thing staticThing = self.doAction("getStaticThing", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		if(staticThing == null) {
			staticThing = dataDesc;
		}
		if(staticThing != null) {
			if(staticThing.isThing("xworker.dataObject.staticdata.StaticDataObject")) {
				dataObject = staticThing.doAction("getData", actionContext);
			}else {
				dataObject = staticThing.getStaticData(key);
			}
		}
		
		if(dataObject == null) {
			if(dataDesc != null) {
				String scope = self.doAction("getScope", actionContext);
				if("world".equals(scope)) {
					if(dataObject == null) {
						dataObject = new DataObject(dataDesc);
						dataObject.init(actionContext);
						staticThing.setStaticData(key, dataObject);
					}
				}else {
					dataObject = actionContext.getObject(dataDesc.getMetadata().getPath());
					if(dataObject == null) {
						dataObject = new DataObject(dataDesc);
						dataObject.init(actionContext);
						actionContext.g().put(dataDesc.getMetadata().getPath(), dataObject);
					}
				}
			}else {
				dataObject = new DataObject();
			}
		}
		
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
		}
				
		actionContext.g().put(self.getMetadata().getName(), dataObject);		
		actionContext.g().put(self.getMetadata().getName() + "Binder", binder);
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
