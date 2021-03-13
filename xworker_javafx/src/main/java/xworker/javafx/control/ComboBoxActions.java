package xworker.javafx.control;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import xworker.javafx.util.JavaFXUtils;
import xworker.javafx.util.converter.ThingValueConverter;

public class ComboBoxActions {
	public static void init(ComboBox<Object> node, Thing thing, ActionContext actionContext) {
		ComboBoxBaseActions.init(node, thing, actionContext);

		if(thing.valueExists("value")){
			Object value = JavaFXUtils.getObject(thing, "value", actionContext);
			if(value != null){
				node.setValue(value);
			}
		}
		if(thing.valueExists("converter")){
			StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
			if(converter != null){
				node.setConverter(converter);
			}
		}
		if(thing.valueExists("visibleRowCount")) {
			node.setVisibleRowCount(thing.getInt("visibleRowCount"));
		}
		if(thing.valueExists("buttonCell")){
			ListCell<Object> buttonCell = JavaFXUtils.getObject(thing, "buttonCell", actionContext);
			if(buttonCell != null) {
				node.setButtonCell(buttonCell);
			}
		}
		if(thing.valueExists("cellFactory")){
			Callback<ListView<Object>,ListCell<Object>> cellFactory = JavaFXUtils.getObject(thing, "cellFactory", actionContext);
			if(cellFactory != null) {
				node.setCellFactory(cellFactory);
			}
		}
		if(thing.valueExists("items")){
			Object obj = JavaFXUtils.getObject(thing, "items", actionContext);
			if (obj instanceof ObservableList) {
				node.setItems((ObservableList<Object>) obj);
			} else if(obj instanceof Iterable){
				Iterable<Object> items = (Iterable<Object>) obj;
				Iterator<Object> iter = items.iterator();
				while (iter.hasNext()) {
					node.getItems().add(iter.next());
				}
			}
		}
		if(thing.valueExists("placeholder")){
			Node placeholder = JavaFXUtils.getObject(thing, "placeholder", actionContext);
			if(placeholder != null) {
				node.setPlaceholder(placeholder);
			}
		}
	}
	
	public static ComboBox<Object> create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ComboBox<Object> box = new ComboBox<Object>();
		init(box, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), box);
		
		actionContext.peek().put("parent", box);
		for(Thing child : self.getChilds()) {
			Object obj  = child.doAction("create", actionContext);
			if(obj instanceof StringConverter){
				box.setConverter((StringConverter<Object>) obj);
			}
		}

		return box;
	}
	
	public static void createThingItems(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ComboBox<Object> parent = actionContext.getObject("parent");
		
		List<Thing> items = self.getChilds("Item");
		parent.setConverter(new ThingValueConverter(items));
		
		parent.getItems().addAll(items);
	}

	public static void createButtonCell(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		ComboBox<Object> parent = actionContext.getObject("parent");

		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof ListCell){
				parent.setButtonCell((ListCell<Object>) obj);
				return;
			}
		}
	}

	public static void createCellFactory(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		ComboBox<Object> parent = actionContext.getObject("parent");
		parent.setCellFactory(new ListCellFactory(self, actionContext));
	}

	public static ListCell<Object> createCell(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if (obj instanceof ListCell) {
				return (ListCell<Object>) obj;
			}
		}

		return null;
	}

	public static class ListCellFactory implements Callback<ListView<Object>, ListCell<Object>> {
		Thing thing;
		ActionContext actionContext;

		public ListCellFactory(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}

		@Override
		public ListCell<Object> call(ListView<Object> param) {
			return thing.doAction("createCell", actionContext, "param", param);
		}
	}


}
