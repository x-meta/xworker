package xworker.javafx.control;

import javafx.scene.paint.Color;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.ColorPicker;
import xworker.javafx.util.JavaFXUtils;

public class ColorPickerActions {
	public static void init(ColorPicker picker, Thing thing, ActionContext actionContext) {
		ComboBoxBaseActions.init(picker, thing, actionContext);


		if(thing.valueExists("value")){
			Color value = JavaFXUtils.getObject(thing, "value", actionContext);
			if(value != null){
				picker.setValue(value);
			}
		}
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
