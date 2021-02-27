package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ButtonBase;

public class ButtonBaseActions {
	public static void init(ButtonBase button, Thing thing, ActionContext actionContext) {
		 LabeledActions.init(button, thing, actionContext);
	}
}
