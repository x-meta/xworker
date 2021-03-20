package xworker.javafx.scene.layout;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class GridPaneActions {
    public static void init(GridPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
        if(thing.valueExists("gridLinesVisible")){
            node.setGridLinesVisible(thing.getBoolean("gridLinesVisible"));
        }
        if(thing.valueExists("hgap")){
            node.setHgap(thing.getDouble("hgap"));
        }
        if(thing.valueExists("vgap")){
            node.setVgap(thing.getDouble("vgap"));
        }
    }

    public static GridPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        GridPane node = new GridPane();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                node.getChildren().add((Node) obj);
            }
        }

        return node;
    }

    public static void createGridePaneConstraints(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        Node node = actionContext.getObject("parent");

        if(thing.valueExists("columnIndex")){
            GridPane.setColumnIndex(node, thing.getInt("columnIndex"));
        }
        if(thing.valueExists("rowIndex")){
            GridPane.setRowIndex(node, thing.getInt("rowIndex"));
        }
        if(thing.valueExists("columnspan")){
            GridPane.setColumnSpan(node, thing.getInt("columnspan"));
        }
        if(thing.valueExists("rowspan")){
            GridPane.setRowSpan(node, thing.getInt("rowspan"));
        }
        if(thing.valueExists("halignment")){
            GridPane.setHalignment(node, HPos.valueOf(thing.getString("halignment")));
        }
        if(thing.valueExists("valignment")){
            GridPane.setValignment(node, VPos.valueOf(thing.getString("valignment")));
        }
        if(thing.valueExists("hgrow")){
            GridPane.setHgrow(node, Priority.valueOf(thing.getString("hgrow")));
        }
        if(thing.valueExists("vgrow")){
            GridPane.setVgrow(node, Priority.valueOf(thing.getString("vgrow")));
        }
        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing, "margin", actionContext);
            if(margin != null) {
                GridPane.setMargin(node, margin);
            }
        }
    }
}
