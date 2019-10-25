package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.actions.ActionContainer;
import xworker.swt.design.Designer;

public class DemoDataObject {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//创建TabFolder
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Thing thing = World.getInstance().getThing("xworker.swt.xwidgets.DemoDataObject/@tabFolder");
		Control tabFolder = null;
		Designer.pushCreator(self);
		try {
			tabFolder = thing.doAction("create", ac);
			Designer.attachCreator(tabFolder, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", tabFolder);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}

		//设置数据对象
		ActionContainer actionContainer = ac.getObject("actions");
		Object dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject != null){			
			actionContainer.doAction("setDataObject", ac, UtilMap.toMap("dataObject", dataObject));
		}
		
		actionContext.g().put(self.getMetadata().getName(), actionContainer);
		return tabFolder;
	}
	
	public static void setDataObject(ActionContext actionContext){
		Object obj = actionContext.get("dataObject");
		Thing dataObject = null;
		if(obj instanceof String){
			dataObject = World.getInstance().getThing(String.valueOf(obj));
		}else{
			dataObject = (Thing) obj;
		}
		
		if(dataObject != null){
			//设置数据对象
			Thing dataObjectEditor = actionContext.getObject("dataObjectEditor");
			dataObjectEditor.doAction("setDataObject", actionContext, "dataObject", dataObject);
			
			//设置事物编辑器
			ActionContext thingEditor = actionContext.getObject("thingEditor");
			ActionContainer ac = thingEditor.getObject("editorActions");
			ac.doAction("setThing", actionContext, "thing", dataObject);
			
			//执行查询操作
			dataObjectEditor.doAction("doQuery", actionContext);
		}
		
	}
}
