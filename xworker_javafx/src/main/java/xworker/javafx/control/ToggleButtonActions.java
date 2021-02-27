package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class ToggleButtonActions {
	public static void init(ToggleButton item, Thing thing, ActionContext actionContext) {
		ButtonBaseActions.init(item, thing, actionContext);
		
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
	
	public static ToggleButton create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ToggleButton item = new ToggleButton();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
