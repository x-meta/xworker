package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class RadioMenuItemActions {
	public static void init(RadioMenuItem item, Thing thing, ActionContext actionContext) {
		MenuItemActions.init(item, thing, actionContext);
		
		if(thing.valueExists("selected")) {
			item.setSelected(thing.getBoolean("selected"));
		}
		
		if(thing.valueExists("toggleGroup")) {
			ToggleGroup toggleGroup = (ToggleGroup) UtilData.getData(thing.getString("toggleGroup"), actionContext);
			if(toggleGroup != null) {
				item.setToggleGroup(toggleGroup);
			}
		}
	}
	
	public static RadioMenuItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		RadioMenuItem item = new RadioMenuItem();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
