package xworker.javafx.design;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AttachInfo {
    Thing thing;
    Node node;
    ActionContext actionContext;

    public AttachInfo(Thing thing, Node node, ActionContext actionContext) {
        this.thing = thing;
        this.node = node;
        this.actionContext = actionContext;
    }

    public Thing getThing() {
        return thing;
    }

    public Node getNode() {
        return node;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }
}
