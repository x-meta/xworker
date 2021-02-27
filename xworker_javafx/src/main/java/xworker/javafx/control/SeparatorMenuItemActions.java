package xworker.javafx.control;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.SeparatorMenuItem;
import ognl.OgnlException;

public class SeparatorMenuItemActions {
	public static void init(SeparatorMenuItem item, Thing thing, ActionContext actionContext) throws OgnlException, IOException {
		CustomMenuItemActions.init(item, thing, actionContext);		
	}
	
	public static SeparatorMenuItem create(ActionContext actionContext) throws OgnlException, IOException {
		Thing self = actionContext.getObject("self");
		
		SeparatorMenuItem item = new SeparatorMenuItem();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
