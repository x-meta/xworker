package xworker.javafx.control;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TextFieldActions {
    public static void init(TextField node, Thing thing, ActionContext actionContext){
        TextInputControlActions.init(node, thing, actionContext);

        if(thing.valueExists("prefColumnCount")){
            node.setPrefColumnCount(thing.getInt("prefColumnCount"));
        }
        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
    }

    public static TextField create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TextField node = new TextField();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Font){
                node.setFont((Font) obj);
            }
        }

        return node;
    }

}
