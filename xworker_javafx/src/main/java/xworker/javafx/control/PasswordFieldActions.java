package xworker.javafx.control;

import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PasswordFieldActions {
    public static void init(PasswordField node, Thing thing, ActionContext actionContext){
        TextFieldActions.init(node, thing, actionContext);
    }

    public static PasswordField create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        PasswordField node = new PasswordField();
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
