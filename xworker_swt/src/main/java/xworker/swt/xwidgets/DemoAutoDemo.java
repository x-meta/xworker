package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;

public class DemoAutoDemo {
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//要演示的事物
		Object com = UtilData.getData(self, "autoDemoPath", actionContext);
		Thing comThing = null;
		if(com instanceof Thing){
			comThing = (Thing) com;
		}else if(com instanceof String){
			comThing = World.getInstance().getThing((String) com);
		}
		if(comThing == null){
			comThing = self.getThing("AtuoDemo@0");
		}		
		
		//创建演示的TabFolder
		Thing tabThing = World.getInstance().getThing("xworker.swt.xwidgets.DemoAutoDemo/@demoTabFolder");
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		Control tabFolder = null;
		Designer.pushCreator(self);
		try {
			tabFolder = tabThing.doAction("create", ac);
			Designer.attachCreator(tabFolder, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", tabFolder);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//演示的动作集
		ActionContainer actions = (ActionContainer) ac.get("actions");
		if(comThing != null){
			actions.doAction("setAutoDemo", ac, UtilMap.toMap("autoDemo", comThing));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), actions);
		return tabFolder;
	}
}
