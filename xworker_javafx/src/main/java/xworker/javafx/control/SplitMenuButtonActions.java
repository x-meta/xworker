package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

public class SplitMenuButtonActions {
	public static void init(SplitMenuButton button, Thing thing, ActionContext actionContext) {
		MenuButtonActions.init(button, thing, actionContext);
	}
	
	public static SplitMenuButton create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		SplitMenuButton button = new SplitMenuButton();
		init(button, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), button);
		
		actionContext.peek().put("parent", button);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof MenuItem) {
				button.getItems().add((MenuItem) obj);
			}
		}
		
		return button;
	}
}
