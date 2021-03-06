package xworker.javafx.stage;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.stage.Window;

public class WindowActions {
	public static void init(Thing thing, Window window, ActionContext actionContext) {
		if(thing.valueExists("width")) {
			window.setWidth(thing.getDouble("width"));
		}
		if(thing.valueExists("height")) {
			window.setHeight(thing.getDouble("height"));
		}
		if(thing.valueExists("x")) {
			window.setX(thing.getDouble("x"));
		}
		if(thing.valueExists("y")) {
			window.setY(thing.getDouble("y"));
		}
		if(thing.valueExists("opacity")){
			window.setOpacity(thing.getDouble("opacity"));
		}
	}
}
