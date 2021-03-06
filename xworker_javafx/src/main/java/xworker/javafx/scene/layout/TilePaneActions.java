package xworker.javafx.scene.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TilePaneActions {
    public static void init(TilePane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
        if(thing.valueExists("hgap")){
            node.setHgap(thing.getInt("hgap"));
        }
        if(thing.valueExists("orientation")){
            node.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("prefColumns")){
            node.setPrefColumns(thing.getInt("prefColumns"));
        }
        if(thing.valueExists("prefRows")){
            node.setPrefRows(thing.getInt("prefRows"));
        }
        if(thing.valueExists("prefTileHeight")){
            node.setPrefTileHeight(thing.getDouble("prefTileHeight"));
        }
        if(thing.valueExists("prefTileWidth")){
            node.setPrefTileWidth(thing.getDouble("prefTileWidth"));
        }
        if(thing.valueExists("tileAlignment")){
            node.setTileAlignment(Pos.valueOf(thing.getString("tileAlignment")));
        }
        if(thing.valueExists("vgap")){
            node.setVgap(thing.getDouble("vgap"));
        }
    }

    public static TilePane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TilePane node = new TilePane();
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

    public static void createConstraints(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        Node node = actionContext.getObject("parent");

        if(thing.valueExists("alignment")){
            TilePane.setAlignment(node, Pos.valueOf(thing.getString("alignment")));
        }

        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing,"margin", actionContext);
            if(margin != null){
                TilePane.setMargin(node, margin);
            }
        }
    }
}
