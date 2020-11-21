package xworker.swt.browser;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class WebBrowser {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator sc = SwtUtils.createCompositeCreator(self, actionContext);
		sc.setCompositeThing(World.getInstance().getThing("xworker.ide.worldexplorer.swt.util.WebBrowser/@shell/@mainComposite"));
		
		Object result = sc.create();
		
		actionContext.g().put(self.getMetadata().getName(), sc.getNewActionContext().get("scripts"));
		return result;
	}
}
