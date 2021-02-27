package xworker.javafx.control;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ChoiceBox;
import xworker.javafx.util.converter.ThingStringConverter;

public class ChoiceBoxActions {
	public static void init(ChoiceBox<?> box, Thing thing, ActionContext actionContext) {
		ControlActions.init(box, thing, actionContext);
		
	}
	
	public static ChoiceBox<?> create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ChoiceBox<?> box = new ChoiceBox<Object>();
		init(box, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), box);
		
		actionContext.peek().put("parent", box);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return box;
	}
	
	public static void createThingItems(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ChoiceBox<Object> parent = actionContext.getObject("parent");
		
		List<Thing> items = self.getChilds("Item");
		parent.setConverter(new ThingStringConverter(items));
		
		parent.getItems().addAll(items);
	}
}
