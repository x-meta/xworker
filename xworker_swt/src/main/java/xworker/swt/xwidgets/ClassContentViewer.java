package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class ClassContentViewer {
	public Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(world.getThing("xworker.swt.xwidgets.prototypes.ClassViewer/@mainComposite"));
		Composite composite = cc.create();
	
		//设置类
		String cls = self.doAction("getClass", actionContext);
		ActionContainer actions = cc.getNewActionContext().getObject("actions");
		if(cls != null && !"".equals(cls)){
		    actions.doAction("setClass", cc.getNewActionContext(), "cls", cls);
		}

		//保存变量和返回值
		actionContext.g().put(self.getMetadata().getName(), actions);
		return composite;
	}
}
