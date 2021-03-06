package xworker.javafx.stage;

import javafx.scene.Node;
import javafx.stage.Popup;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PopupActions {
    public static void init(Popup node, Thing thing, ActionContext actionContext){
        PopupWindowActions.init(node, thing, actionContext);
    }

    public static Popup create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Popup node = new Popup();
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                node.getContent().add((Node) obj);
            }
        }

        return node;
    }
}
