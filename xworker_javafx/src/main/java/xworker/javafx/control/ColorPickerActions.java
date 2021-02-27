package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ColorPicker;

public class ColorPickerActions {
	public static void init(ColorPicker picker, Thing thing, ActionContext actionContext) {
		ComboBoxBaseActions.init(picker, thing, actionContext);
	}
	
	public static ColorPicker create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ColorPicker picker = new ColorPicker();
		init(picker, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), picker);
		
		actionContext.peek().put("parent", picker);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return picker;
	}
}
