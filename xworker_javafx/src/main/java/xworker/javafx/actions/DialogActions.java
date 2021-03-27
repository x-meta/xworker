package xworker.javafx.actions;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.Optional;

public class DialogActions {
    public static Object showDialog(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ActionContext ac = new ActionContext();
        ac.put("parentContext", actionContext);
        Dialog<Object> dialog = self.doAction("getDialog", actionContext);
        if(dialog == null){
            Thing thing = self.doAction("getDialogThing", actionContext);
            if(thing != null){
                dialog = thing.doAction("create", ac);
            }
        }

        if(dialog == null){
            return null;
        }

        Optional<Object> result = dialog.showAndWait();
        if(result.isPresent()){
            actionContext.peek().put("result", result.get());
            if(result.get() == ButtonType.APPLY){
                self.doAction("apply", actionContext);
            }else if(result.get() == ButtonType.CANCEL){
                self.doAction("cancel", actionContext);
            }else if(result.get() == ButtonType.CLOSE){
                self.doAction("close", actionContext);
            }else if(result.get() == ButtonType.FINISH){
                self.doAction("finish", actionContext);
            }else if(result.get() == ButtonType.NEXT){
                self.doAction("next", actionContext);
            }else if(result.get() == ButtonType.NO){
                self.doAction("no", actionContext);
            }else if(result.get() == ButtonType.	OK){
                self.doAction("ok", actionContext);
            }else if(result.get() == ButtonType.PREVIOUS){
                self.doAction("previous", actionContext);
            }else if(result.get() == ButtonType.YES){
                self.doAction("yes", actionContext);
            }

            return self.doAction("onResult", actionContext);
        }else{
            return null;
        }
    }
}
