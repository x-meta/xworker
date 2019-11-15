package xworker.swt.xwidgets;

import java.util.HashMap;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.view.swt.widgets.form.DataObjectForm;
import xworker.app.view.swt.widgets.form.DataObjectForm.ThingFormModifyListener;
import xworker.dataObject.DataObject;
import xworker.swt.design.Designer;

public class ObjectForm {
	private static Logger logger = LoggerFactory.getLogger(ObjectForm.class);
	
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//创建面板
		int style = 0;
		Composite parent = (Composite) actionContext.get("parent");
		Composite composite = new Composite(parent, style); 
		FillLayout fillLayout = new FillLayout();
		composite.setLayout(fillLayout);
		actionContext.peek().put("parent", composite);
		Designer.pushCreator(self);
		try {
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
			Designer.attach(composite, self.getMetadata().getPath(), actionContext);
			for(Thing child : self.getChilds()){
				String thingName = child.getThingName();
				if(thingName.equals("ObjectMapping") || thingName.equals("DefaultMapping")){
					continue;
				}
				
				child.doAction("create", actionContext);
			}
			
			//创建事物定义，使用事物编辑器
			Thing form = new Thing("xworker.app.view.swt.widgets.form.DataObjectForm");
			form.putAll(self.getAttributes());
			form.set("descriptors", "xworker.app.view.swt.widgets.form.DataObjectForm");
			form.setData("formThing", self);
			form.setData("parent", composite);
			form.set("extends", self.getMetadata().getPath());
			
			actionContext.getScope(0).put(self.getMetadata().getName(), form);
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
			
			Object defaultModifyObj = null;//defaultModify == null ? null : actionContext.get(defaultModify);
			ThingFormModifyListener modifyListener = new ThingFormModifyListener(composite, self, form, defaultModifyObj, actionContext);
			form.setData("defaultModify", modifyListener);
			
			//表单对象和设置监听等
			DataObjectForm dataObjectForm = new DataObjectForm(self, form, modifyListener, actionContext);
			composite.addDisposeListener(dataObjectForm);
			form.setData(DataObjectForm.KEY_DATAOBJECTFORM, dataObjectForm);
			
			Object obj = self.doAction("getEditObject", actionContext);
			if(obj != null){
				form.doAction("setObject", actionContext, UtilMap.toMap("object", obj));
			}else {
				form.doAction("setObject", actionContext, "object", new HashMap<String, Object>());
			}
			
			return composite;
		}finally {
			Designer.popCreator();
		}
	}
	
	public static void setObject(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing formThing = (Thing) self.getData("formThing");
		
		Thing defaultMapping = formThing.doAction("getDefaultMapping", actionContext); 
		Object object = actionContext.get("object");
		if(object == null) {
			object = new HashMap<String, Object>();
		}

		String type = object.getClass().getName();
		
		Thing dataObject = null;
		for(Thing child : formThing.getChilds("ObjectMapping")){
			if(type.equals(child.getString("dataClassName"))){
				dataObject = child;
				break;
			}
		}
		
		if(dataObject == null){
			dataObject = defaultMapping;
		}
		
		if(dataObject == null){
			logger.info("Can not find Mapping by type='" + type + "', and DefaultMapping not setted, do nothing, path=" + formThing.getMetadata().getPath());
			return;
		}
		
		//创建数据对象
		Action action = World.getInstance().getAction("xworker.dataObject.java.ListDataObject/@actions1/@createDataObjectFromObject");
		Object dataObj = action.run(actionContext, UtilMap.toMap("data", object, "descriptor", dataObject));
		
		self.doAction("setDataObject", actionContext, UtilMap.toMap("dataObject", dataObj));
	}
	
	public static Object getObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		DataObject dataObject = (DataObject) self.getData("values");
		return dataObject.doAction("getData", actionContext);
		
	}
	
	public static Object saveObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataObject dataObject = (DataObject) self.doAction("getDataObject", actionContext);
		if(dataObject != null){
			dataObject.doAction("updateData", actionContext);
			return dataObject.doAction("getData", actionContext);
		}else {
			return null;
		}
	}
}
