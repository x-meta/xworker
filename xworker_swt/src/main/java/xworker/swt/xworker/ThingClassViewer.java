package xworker.swt.xworker;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class ThingClassViewer {
	public static Composite create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		
		Thing compositeThing = World.getInstance().getThing("xworker.ide.worldexplorer.swt.dialogs.DesAndExtendsDialog/@shell/@mainComposite");				
		Composite composite = compositeThing.doAction("create", ac);
		ActionContainer actions = ac.getObject("actions");
		Thing thing = self.doAction("getThing", actionContext);
		if(thing != null) {
			actions.doAction("setThing", ac, "thing", thing);
		}
		
		actionContext.g().put(self.getMetadata().getName(), ac.get("actions"));		
		
		return composite;
	}
}
