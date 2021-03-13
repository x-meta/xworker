package xworker.javafx.control;

import java.util.Iterator;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ChoiceBox;
import xworker.javafx.util.JavaFXUtils;
import xworker.javafx.util.converter.ThingValueConverter;

public class ChoiceBoxActions {
	public static void init(ChoiceBox<Object> box, Thing thing, ActionContext actionContext) {
		ControlActions.init(box, thing, actionContext);

		if(thing.valueExists("converter")){
			StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
			if(converter != null){
				box.setConverter(converter);
			}
		}
		if(thing.valueExists("items")){
			Object obj = JavaFXUtils.getObject(thing, "items", actionContext);
			if (obj instanceof ObservableList) {
				box.setItems((ObservableList<Object>) obj);
			} else if(obj instanceof Iterable){
				Iterable<Object> items = (Iterable<Object>) obj;
				Iterator<Object> iter = items.iterator();
				while (iter.hasNext()) {
					box.getItems().add(iter.next());
				}
			}
		}
		if(thing.valueExists("value")){
			Object value = JavaFXUtils.getObject(thing, "value", actionContext);
			if(value != null){
				box.setValue(value);
			}
		}
		if(thing.valueExists("stringValues")){
			List<String> list = thing.doAction("getStringValues", actionContext);
			if(list != null){
				box.getItems().addAll(list);
			}
		}

	}

	String name = "abc";
	public static ChoiceBox<?> create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ChoiceBox<Object> box = new ChoiceBox<Object>();
		init(box, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), box);
		
		actionContext.peek().put("parent", box);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StringConverter){
				box.setConverter((StringConverter<Object>) obj);
			}
		}

		return box;
	}
	
	public static void createThingItems(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ChoiceBox<Object> parent = actionContext.getObject("parent");
		
		List<Thing> items = self.getChilds("Item");
		parent.setConverter(new ThingValueConverter(items));
		
		parent.getItems().addAll(items);
	}
}
