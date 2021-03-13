package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ComboBoxBase;
import xworker.javafx.util.JavaFXUtils;

public class ComboBoxBaseActions {
	public static void init(ComboBoxBase<?> base, Thing thing, ActionContext actionContext) {
		ControlActions.init(base, thing, actionContext);
		
        if(thing.valueExists("editable")){
            base.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("promptText")){
            String promptText = JavaFXUtils.getString(thing, "promptText", actionContext);
            if(promptText != null) {
                base.setPromptText(promptText);
            }
        }
	}
}
