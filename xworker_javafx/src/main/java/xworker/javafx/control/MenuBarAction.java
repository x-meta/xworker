package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MenuBarAction {
	public static void init(MenuBar list, Thing thing, ActionContext actionContext) {
		ControlActions.init(list, thing, actionContext);
        if(thing.valueExists("useSystemMenuBar")){
            list.setUseSystemMenuBar(thing.getBoolean("useSystemMenuBar"));
        }
	}
	
	public static MenuBar create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		MenuBar item = new MenuBar();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Menu) {
				item.getMenus().add((Menu) obj);
			}
		}
		
		return item;
	}
}
