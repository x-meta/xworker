package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.CheckMenuItem;

public class CheckMenuItemActions {
	public static void init(CheckMenuItem item, Thing thing, ActionContext actionContext) {
		MenuItemActions.init(item, thing, actionContext);
		
		if(thing.valueExists("selected")) {
			item.setSelected(thing.getBoolean("selected"));
		}
	}
	
	public static CheckMenuItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		CheckMenuItem item = new CheckMenuItem();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
