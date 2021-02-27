package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.FocusModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;

public class ListViewActions {
	public static void init(ListView<Object> list, Thing thing, ActionContext actionContext) {
		ControlActions.init(list, thing, actionContext);
		
        if(thing.valueExists("cellFactory")){
        	@SuppressWarnings("unchecked")
			Callback<ListView<Object>,ListCell<Object>> cellFactory = (Callback<ListView<Object>,ListCell<Object>>) UtilData.getData(thing.getString("cellFactory"), actionContext);
        	if(cellFactory != null) {
        		list.setCellFactory(cellFactory);
        	}
        }
        if(thing.valueExists("editable")){
            list.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("fixedCellSize")){
            list.setFixedCellSize(thing.getDouble("fixedCellSize"));
        }
        if(thing.valueExists("focusModel")){
        	@SuppressWarnings("unchecked")
			FocusModel<Object> focusModel = (FocusModel<Object>) UtilData.getData(thing.getString("focusModel"), actionContext);
        	if(focusModel != null) {
        		list.setFocusModel(focusModel);
        	}
        }
        if(thing.valueExists("orientation")){
            list.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("placeholder")){
        	Node placeholder = (Node) UtilData.getData(thing.getString("placeholder"), actionContext);
        	if(placeholder != null) {
        		list.setPlaceholder(placeholder);
        	}
        }
        if(thing.valueExists("selectionModel")){
        	String selectionModel = thing.getString("selectionModel");
        	if("MULTIPLE".equals(selectionModel)) {
        		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        	}else if("SINGLE".equals(selectionModel)) {
        		list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        	}
        }
	}
	
	public static ListView<Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ListView<Object> item = new ListView<Object>();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
