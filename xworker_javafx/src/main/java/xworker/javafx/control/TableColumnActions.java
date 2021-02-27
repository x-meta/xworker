package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.util.Callback;
import xworker.javafx.util.JavaFXUtils;

public class TableColumnActions {
	public static void init(TableColumn<Object, Object> node, Thing thing, ActionContext actionContext) {
		TableColumnBaseActions.init(node, thing, actionContext);
		
		if(thing.valueExists("cellFactory")) {
			Callback<TableColumn<Object,Object>,TableCell<Object,Object>> cellFactory = JavaFXUtils.getObject(thing, "cellFactory", actionContext);
			if(cellFactory != null) {
				node.setCellFactory(cellFactory);
			}
		}
		if(thing.valueExists("cellValueFactory")) {
			Callback<TableColumn.CellDataFeatures<Object,Object>,ObservableValue<Object>> cellValueFactory = JavaFXUtils.getObject(thing, "cellValueFactory", actionContext);
			if(cellValueFactory != null) {
				node.setCellValueFactory(cellValueFactory);
			}
		}
		if(thing.valueExists("sortType")) {
			node.setSortType(SortType.valueOf(thing.getString("sortType")));
		}
	}
	
	public static TableColumn<Object, Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		TableColumn<Object, Object> item = new TableColumn<Object, Object>();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);					
		}
		
		return item;
	}
}
