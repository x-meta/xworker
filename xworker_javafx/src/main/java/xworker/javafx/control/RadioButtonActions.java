package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.RadioButton;

public class RadioButtonActions {
	public static void init(RadioButton item, Thing thing, ActionContext actionContext) {
		ToggleButtonActions.init(item, thing, actionContext);		
	}
	
	public static RadioButton create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		RadioButton item = new RadioButton();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
