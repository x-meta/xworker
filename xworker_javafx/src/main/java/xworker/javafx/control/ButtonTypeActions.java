package xworker.javafx.control;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ButtonTypeActions {
    public static ButtonType create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String type = self.getStringBlankAsNull("type");
        if(type != null){
            if("APPLY".equals(type)){
                return ButtonType.APPLY;
            }else if("CANCEL".equals(type)){
                return ButtonType.CANCEL;
            }else if("CLOSE".equals(type)){
                return ButtonType.CLOSE;
            }else if("FINISH".equals(type)){
                return ButtonType.FINISH;
            }else if("NEXT".equals(type)){
                return ButtonType.NEXT;
            }else if("NO".equals(type)){
                return ButtonType.NO;
            }else if("OK".equals(type)){
                return ButtonType.OK;
            }else if("PREVIOUS".equals(type)){
                return ButtonType.PREVIOUS;
            }else if("YES".equals(type)){
                return ButtonType.YES;
            }
        }

        String text = self.getStringBlankAsNull("text");
        if(text != null){
            if(self.valueExists("buttonData")){
                return new ButtonType(text, ButtonBar.ButtonData.valueOf(self.getString("buttonData")));
            }else{
                return new ButtonType(text);
            }
        }else{
            return null;
        }
    }
}
