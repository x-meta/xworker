package xworker.javafx.control;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;
import xworker.javafx.util.ThingCallback;

import java.util.Iterator;

public class TableViewActions {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void init(TableView<Object> node, Thing thing, ActionContext actionContext) {
		ControlActions.init(node, thing, actionContext);
		        
        if(thing.valueExists("columnResizePolicy")){
        	Callback<TableView.ResizeFeatures,Boolean> cp = (Callback<TableView.ResizeFeatures,Boolean>) JavaFXUtils.getObject(thing, "columnResizePolicy", actionContext);
        	if(cp != null) {
        		node.setColumnResizePolicy(cp);
        	}
        }
        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("fixedCellSize")){
            node.setFixedCellSize(thing.getDouble("fixedCellSize"));
        }
        if(thing.valueExists("focusModel")){
        	TableViewFocusModel<Object> model = (TableViewFocusModel<Object>) JavaFXUtils.getObject(thing, "focusModel", actionContext);
        	if(model != null) {
        		node.setFocusModel(model);
        	}
        }
        if(thing.valueExists("items")) {
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
        	Node placeholder = (Node) JavaFXUtils.getObject(thing, "placeholder", actionContext);
        	if(placeholder != null) {
        		node.setPlaceholder(placeholder);
        	}
        }
        if(thing.valueExists("rowFactory")){
        	Callback<TableView<Object>,TableRow<Object>> rf = (Callback<TableView<Object>,TableRow<Object>>) JavaFXUtils.getObject(thing, "", actionContext);
        	if(rf != null) {
        		node.setRowFactory(rf);
        	}
        }
        if(thing.valueExists("selectionModel")){
        	if("MULTIPLE".equals(thing.getString("selectionModel"))){
        		node.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			}else{
				node.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			}
        }
		if(thing.valueExists("cellSelectionEnabled")){
			node.getSelectionModel().setCellSelectionEnabled(thing.getBoolean("cellSelectionEnabled"));
		}
        if(thing.valueExists("sortPolicy")){
        	Callback<TableView<Object>,Boolean> sp = (Callback<TableView<Object>,Boolean>) JavaFXUtils.getObject(thing, "sortPolicy", actionContext);
        	if(sp != null) {
        		node.setSortPolicy(sp);
        	}
        }
        if(thing.valueExists("tableMenuButtonVisible")){
            node.setTableMenuButtonVisible(thing.getBoolean("tableMenuButtonVisible"));
        }
	}
	
	@SuppressWarnings("unchecked")
	public static TableView<Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		TableView<Object> item = new TableView<Object>();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof TableColumn) {
				item.getColumns().add((TableColumn<Object, Object>) obj);
			}else if(obj instanceof Node) {
				item.getItems().add((Node) obj);
			}
		}
		
		return item;
	}

	public static void createColumnResizePolicy(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		TableView<?> parent = actionContext.getObject("parent");

		parent.setColumnResizePolicy(new ThingCallback<>(self, actionContext));
	}

	public static void createRowFactory(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		TableView<?> parent = actionContext.getObject("parent");

		parent.setRowFactory(new ThingCallback<>(self, actionContext));
	}

	public static void createSortPolicy(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		TableView<?> parent = actionContext.getObject("parent");

		parent.setSortPolicy(new ThingCallback<>(self, actionContext));
	}

	public static void createPlaceholder(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		TableView<?> parent = actionContext.getObject("parent");


		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node){
				parent.setPlaceholder((Node) obj);
			}
		}
	}
}
