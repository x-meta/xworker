package xworker.swt.xwidgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class ObjectTable {
private static Logger logger = LoggerFactory.getLogger(ObjectTable.class);
	
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//创建表格
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Thing tableThing = World.getInstance().getThing("xworker.swt.xwidgets.ObjectTable/@table");
		tableThing = tableThing.detach();
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.putAll(self.getAttributes());
		attrs.remove("descriptors");
		attrs.remove("extends");
		
		tableThing.putAll(attrs);
		Table table = (Table) tableThing.doAction("create", ac);
		ac.put("parent", table);
		for(Thing child : self.getChilds()){
			String thingName = child.getThingName();
			if(thingName.equals("ObjectMapping") || thingName.equals("DefaultMapping")){
				continue;
			}
			
			child.doAction("create", ac);
		}
		
		//创建事物定义，使用事物编辑器
		Thing form = new Thing("xworker.swt.xwidgets.ObjectTable");
		form.putAll(self.getAttributes());
		form.setData("thing", self);
		form.setData("actionContext", ac);
		form.set("extends", self.getMetadata().getPath());
		
		actionContext.getScope(0).put(self.getMetadata().getName(), form);
		
		Object obj = UtilData.getObject(self, "listData", actionContext);
		if(obj != null){
			form.doAction("setListData", actionContext, UtilMap.toMap("datas", obj, "type", null));
		}
		
		return table;
	}
	
	public static void setListData(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Thing formThing = (Thing) self.getData("thing");
		//表格所在的上下文
		ActionContext ac = (ActionContext) self.getData("actionContext");
		//数据仓库
		Thing dataStore = (Thing) ac.get("dataStore");
		//数据对象要使用的表格
		ac.put("datas", actionContext.get("datas"));

		Thing defaultMapping = formThing.getThing("DefaultMapping@0");
		Thing dataObject = null;
		String type = (String) actionContext.get("type");
		if(type != null){
			for(Thing child : formThing.getChilds("ObjectMapping")){
				if(type.equals(child.getString("type"))){
					dataObject = child;
					break;
				}
			}
		}
		
		if(dataObject == null){
			dataObject = defaultMapping;
		}
		
		if(dataObject == null){
			logger.info("Can not find Mapping by type='" + type + "', and DefaultMapping not setted, do nothing, path=" + formThing.getMetadata().getPath());
			return;
		}
		
		//数据仓库设置数据对象
		dataStore.doAction("setDataObject", ac, UtilMap.toMap("dataObject", dataObject));
		
		//数据仓库装载数据
		dataStore.doAction("load", ac);
	}
}
