package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ProgressIndicator;

public class ProgressIndicatorActions {
	public static void init(ProgressIndicator p, Thing thing, ActionContext actionContext) {
		ControlActions.init(p, thing, actionContext);

		if (thing.valueExists("progress")) {
			p.setProgress(thing.getDouble("progress"));
		}		
	}
	
	public static ProgressIndicator create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ProgressIndicator item = new ProgressIndicator();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);			
		}
		
		return item;
	}
}
