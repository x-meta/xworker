package xworker.javafx.control;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import xworker.javafx.util.converter.ThingStringConverter;

public class ComboBoxActions {
	public static void init(ComboBox<?> box, Thing thing, ActionContext actionContext) {
		ComboBoxBaseActions.init(box, thing, actionContext);
		
		if(thing.valueExists("visibleRowCount")) {
			box.setVisibleRowCount(thing.getInt("visibleRowCount"));
		}
	}
	
	public static ComboBox<?> create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ComboBox<?> box = new ComboBox<Object>();
		init(box, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), box);
		
		actionContext.peek().put("parent", box);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		Node placeholder = self.doAction("getPlaceholder", actionContext);
		if(placeholder != null) {
			box.setPlaceholder(placeholder);
		}
		
		return box;
	}
	
	public static void createThingItems(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ComboBox<Object> parent = actionContext.getObject("parent");
		
		List<Thing> items = self.getChilds("Item");
		parent.setConverter(new ThingStringConverter(items));
		
		parent.getItems().addAll(items);
	}
}
