package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Button;

public class ButtonActions {
	public static void init(Button button, Thing thing, ActionContext actionContext) {
		ButtonBaseActions.init(button, thing, actionContext);
		 
		 if(thing.valueExists("cancelButton")) {
			 button.setCancelButton(thing.getBoolean("cancelButton"));
		 }
		 
		 if(thing.valueExists("defaultButton")) {
			 button.setDefaultButton(thing.getBoolean("defaultButton"));
		 }
	}
	
	public static Button create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Button button = new Button();
		init(button, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), button);
		
		actionContext.peek().put("parent", button);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return button;
	}
}
