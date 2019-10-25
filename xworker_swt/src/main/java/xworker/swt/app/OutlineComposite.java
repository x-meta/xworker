package xworker.swt.app;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;

public class OutlineComposite {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", actionContext.get("parent"));
		
		Thing thing = World.getInstance().getThing("xworker.swt.app.prototypes.OutlineShell/@outlineComposite");
		Designer.pushCreator(self);
		Composite composite = null;
		try{
			composite =	thing.doAction("create", ac);
			
			OutlineContainer container = new OutlineContainer((ActionContainer) ac.get("actions"), ac);
			actionContext.g().put(self.getMetadata().getName(), container);
			
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;
	}
}
