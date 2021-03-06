package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TabActions {
    public static void init(Tab tab, Thing thing, ActionContext actionContext){
        if(thing.valueExists("closable")){
            tab.setClosable(thing.getBoolean("closable"));
        }
        if(thing.valueExists("content")){
            Node content = JavaFXUtils.getObject(thing, "content", actionContext);
            if(content != null) {
                tab.setContent(content);
            }
        }
        if(thing.valueExists("contextMenu")){
            ContextMenu contextMenu = JavaFXUtils.getObject(thing, "contextMenu", actionContext);
            if(contextMenu != null) {
                tab.setContextMenu(contextMenu);
            }
        }
        if(thing.valueExists("disable")){
            tab.setDisable(thing.getBoolean("disable"));
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getObject(thing, "graphic", actionContext);
            if(graphic != null) {
                tab.setGraphic(graphic);
            }
        }
        if(thing.valueExists("id")){
            tab.setId(thing.getString("id"));
        }
        if(thing.valueExists("style")){
            tab.setStyle(thing.getString("style"));
        }
        if(thing.valueExists("text")){
            tab.setText(thing.getString("text"));
        }
        if(thing.valueExists("tooltip")){
            Tooltip tooltip = JavaFXUtils.getObject(thing, "tooltip", actionContext);
            if(tooltip != null) {
                tab.setTooltip(tooltip);
            }
        }
    }

    public static Tab create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Tab node = new Tab();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Tooltip){
                node.setTooltip((Tooltip) obj);
            }
        }

        return node;
    }
}
