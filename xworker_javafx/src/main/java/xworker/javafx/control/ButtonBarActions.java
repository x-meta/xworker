package xworker.javafx.control;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

public class ButtonBarActions {
	public static void init(ButtonBar bar, Thing thing, ActionContext actionContext) {
		ControlActions.init(bar, thing, actionContext);

		if (thing.valueExists("buttonMinWidth")) {
			bar.setButtonMinWidth(thing.getDouble("buttonMinWidth"));
		}
		if (thing.valueExists("buttonOrder")) {
			bar.setButtonOrder(thing.getString("buttonOrder"));
		}
	}
	
	public static ButtonBar create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ButtonBar bar = new ButtonBar();
		init(bar, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), bar);
		
		actionContext.peek().put("parent", bar);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Button) {
				bar.getButtons().add((Button) obj);
			}
		}
		
		return bar;
	}

	public static void createButtonData(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.getObject("parent");
		if(parent instanceof Node){
			Node node = (Node) parent;

			if(self.valueExists("buttonData")) {
				ButtonBar.ButtonData data = ButtonBar.ButtonData.valueOf(self.getString("buttonData"));
				if(data != null){
					ButtonBar.setButtonData(node, data);
				}
			}
		}
	}
}
