package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ProgressBar;

public class ProgressBarActions {
	public static void init(ProgressBar p, Thing thing, ActionContext actionContext) {
		ProgressIndicatorActions.init(p, thing, actionContext);
	}
	
	public static ProgressBar create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ProgressBar item = new ProgressBar();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);			
		}
		
		return item;
	}
}
