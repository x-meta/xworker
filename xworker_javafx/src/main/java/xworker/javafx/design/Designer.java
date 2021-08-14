package xworker.javafx.design;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.xmeta.ActionContext;
import org.xmeta.Thing;


public class Designer {
    public static final String THING = "__designer_info__";

    private static final DesignerListener designerListener = new DesignerListener();

    public static void attach(Thing thing, Node node, ActionContext actionContext){
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, designerListener);

        AttachInfo attachInfo = new AttachInfo(thing, node, actionContext);
        node.getProperties().put(THING, attachInfo);
    }

    public static AttachInfo getAttachInfo(Node node){
        return (AttachInfo) node.getProperties().get(THING);
    }

}
