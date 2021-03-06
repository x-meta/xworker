package xworker.javafx.control;

import javafx.geometry.Side;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TabPaneActions {
    public static void init(TabPane node, Thing thing, ActionContext actionContext){
        ControlActions.init(node, thing, actionContext);

        if(thing.valueExists("rotateGraphic")){
            node.setRotateGraphic(thing.getBoolean("rotateGraphic"));
        }
        if(thing.valueExists("selectionModel")){
            SingleSelectionModel<Tab> model = JavaFXUtils.getObject(thing, "selectionModel", actionContext);
            if(model != null) {
                node.setSelectionModel(model);
            }
        }
        if(thing.valueExists("side")){
            node.setSide(Side.valueOf(thing.getString("side")));
        }
        if(thing.valueExists("tabClosingPolicy")){
            node.setTabClosingPolicy(TabPane.TabClosingPolicy.valueOf(thing.getString("tabClosingPolicy")));
        }
        if(thing.valueExists("tabMaxHeight")){
            node.setTabMaxHeight(thing.getDouble("tabMaxHeight"));
        }
        if(thing.valueExists("tabMaxWidth")){
            node.setTabMaxWidth(thing.getDouble("tabMaxWidth"));
        }
        if(thing.valueExists("tabMinHeight")){
            node.setTabMinHeight(thing.getDouble("tabMinHeight"));
        }
        if(thing.valueExists("tabMinWidth")){
            node.setTabMinWidth(thing.getDouble("tabMinWidth"));
        }
    }

    public static TabPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TabPane node = new TabPane();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Tab){
                node.getTabs().add((Tab) obj);
            }
        }

        return node;
    }
}
