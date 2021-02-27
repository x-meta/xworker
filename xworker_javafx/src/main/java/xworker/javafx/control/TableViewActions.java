package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.util.Callback;
import xworker.javafx.util.JavaFXUtils;

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
        if(thing.valueExists("items")){
        	ObservableList<Object> items = (ObservableList<Object>) JavaFXUtils.getObject(thing, "items", actionContext);
        	if(items != null) {
        		node.getItems().clear();
        		node.getItems().addAll(items);
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
        	TableViewSelectionModel<Object> model = (TableViewSelectionModel<Object>) JavaFXUtils.getObject(thing, "selectionModel", actionContext);
        	if(model != null) {
        		node.setSelectionModel(model);
        	}
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
}
