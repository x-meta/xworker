package xworker.javafx.thing;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingRegistView {
    Node rootNode;

    Thing thing;
    Thing registThing;
    ActionContext actionContext;
    String registType = "child";

    public ThingRegistView(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;

    }
}
