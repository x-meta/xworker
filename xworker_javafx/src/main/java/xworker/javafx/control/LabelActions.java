package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Label;

public class LabelActions {
	public static void init(Label label, Thing thing, ActionContext actionContext) {
		LabeledActions.init(label, thing, actionContext);
	}
	
	
	public static Label create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Label item = new Label();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
