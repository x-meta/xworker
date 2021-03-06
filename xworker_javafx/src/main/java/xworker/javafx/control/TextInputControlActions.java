package xworker.javafx.control;

import javafx.scene.control.Control;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TextInputControlActions {
    public static void init(TextInputControl node, Thing thing, ActionContext actionContext){
        ControlActions.init(node, thing, actionContext);

        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("font")){
            Font font = JavaFXUtils.getObject(thing, "font", actionContext);
            if(font != null) {
                node.setFont(font);
            }
        }
        if(thing.valueExists("promptText")){
            node.setPromptText(thing.getString("promptText"));
        }
        if(thing.valueExists("textFormatter")){
            TextFormatter<?> textFormatter = JavaFXUtils.getObject(thing, "textFormatter", actionContext);
            if(textFormatter != null) {
                node.setTextFormatter(textFormatter);
            }
        }
        if(thing.valueExists("text")){
            node.setText(thing.getString("text"));
        }
    }
}
