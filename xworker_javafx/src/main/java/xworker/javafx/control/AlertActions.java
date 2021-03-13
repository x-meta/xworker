package xworker.javafx.control;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AlertActions {
    public static Alert create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Alert node = new Alert(Alert.AlertType.valueOf(self.getString("alertType")));
        DialogActions.init(node, self, actionContext);

        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        if(node.getButtonTypes().size() == 0){
            node.getButtonTypes().add(ButtonType.OK);
        }
        return node;
    }
}
