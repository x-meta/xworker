package xworker.javafx.stage;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.stage.Window;

public class WindowActions {
	public static void init(Thing thing, Window window, ActionContext actionContext) {
		if(thing.getStringBlankAsNull("width") != null) {
			window.setWidth(thing.getDouble("width"));
		}
		if(thing.getStringBlankAsNull("height") != null) {
			window.setHeight(thing.getDouble("height"));
		}
		if(thing.getStringBlankAsNull("x") != null) {
			window.setX(thing.getDouble("x"));
		}
		if(thing.getStringBlankAsNull("y") != null) {
			window.setY(thing.getDouble("y"));
		}
	}
}
