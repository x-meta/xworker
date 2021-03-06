package xworker.javafx.scene.text;

import javafx.scene.Node;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.layout.PaneActions;

public class TextFlowActions {
    public static void init(TextFlow node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("lineSpacing")){
            node.setLineSpacing(thing.getDouble("lineSpacing"));
        }

        if(thing.valueExists("textAlignment")){
            node.setTextAlignment(TextAlignment.valueOf(thing.getString("textAlignment")));
        }
    }

    public static TextFlow create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TextFlow node = new TextFlow();
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
