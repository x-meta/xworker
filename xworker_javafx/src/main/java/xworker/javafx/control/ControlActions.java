package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Control;
import xworker.javafx.layout.RegionActions;

public class ControlActions {
	public static void init(Control control, Thing thing, ActionContext actionContext) {
		RegionActions.init(control, thing, actionContext);
	}
}
