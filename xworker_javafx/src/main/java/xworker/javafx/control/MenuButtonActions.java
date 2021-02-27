package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.Side;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class MenuButtonActions {
	public static void init(MenuButton button, Thing thing, ActionContext actionContext) {
		ButtonBaseActions.init(button, thing, actionContext);
		 
		 if(thing.valueExists("popupSide")) {
			 button.setPopupSide(Side.valueOf(thing.getString("popupSide")));
		 }
		 
	}
	
	public static MenuButton create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		MenuButton button = new MenuButton();
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
