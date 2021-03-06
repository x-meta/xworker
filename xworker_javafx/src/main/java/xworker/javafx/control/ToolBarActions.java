package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ToolBarActions {
    public static void init(ToolBar node, Thing thing, ActionContext actionContext) {
        ControlActions.init(node, thing, actionContext);
    }

    public static ToolBar create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ToolBar node = new ToolBar();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                node.getItems().add((Node) obj);
            }
        }

        return node;
    }
}
