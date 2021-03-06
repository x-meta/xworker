package xworker.javafx.scene.layout;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.layout.RegionActions;

public class PaneActions {
    public static void init(Pane node, Thing thing, ActionContext actionContext){
        RegionActions.init(node, thing, actionContext);
    }

    public static Pane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Pane node = new Pane();
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
}
