package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ToggleGroup;

public class ToggleGroupActions {
	public static ToggleGroup create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ToggleGroup tg = new ToggleGroup();
		actionContext.g().put(self.getMetadata().getName(), tg);
		
		return tg;
	}
}
