package xworker.javafx.control;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TextInputDialogActions {
    public static TextInputDialog create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String defaultValue = JavaFXUtils.getString(self, "defaultValue", actionContext);
        TextInputDialog node = null;
        if(defaultValue == null){
            node = new TextInputDialog();
        }else{
            node = new TextInputDialog(defaultValue);
        }
        DialogActions.init(node, self, actionContext);

        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof ButtonType){
                node.getDialogPane().getButtonTypes().add((ButtonType) obj);
            }
        }

        if(node.getDialogPane().getButtonTypes().size() == 0){
            node.getDialogPane().getButtonTypes().add(ButtonType.OK);
        }
        return node;
    }
}
