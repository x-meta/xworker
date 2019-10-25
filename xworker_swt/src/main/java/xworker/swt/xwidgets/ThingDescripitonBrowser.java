package xworker.swt.xwidgets;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;
import xworker.util.XWorkerUtils;

public class ThingDescripitonBrowser {
	public static Composite create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//创建浏览器
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Thing compositeThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ThingDescripitonBrowserShell/@composite");
		Composite composite = null;
		Designer.pushCreator(self);
		try {
			composite = compositeThing.doAction("create", ac);
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
		
		//子事物节点的create方法
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//设置事物
		Thing thing = self.doAction("getThing", actionContext);
		ActionContainer actions = ac.getObject("actions");
		actions.doAction("setThing", ac, "thing", thing);
		
		actionContext.g().put(self.getMetadata().getName(), actions);
		return composite;
	}
	
	public static void setThing(ActionContext actionContext){
		Thing thing = actionContext.getObject("thing");
		Browser browser = actionContext.getObject("browser");
		
		if(thing != null){
			browser.setUrl(XWorkerUtils.getThingDescUrl(thing));
		}else{
			browser.setText("Thing is null");
		}
	}
}
