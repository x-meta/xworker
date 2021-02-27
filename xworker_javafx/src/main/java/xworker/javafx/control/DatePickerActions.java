package xworker.javafx.control;

import java.time.chrono.Chronology;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.DatePicker;
import xworker.javafx.util.JavaFXUtils;

public class DatePickerActions {
	public static void init(DatePicker picker, Thing thing, ActionContext actionContext) {
		ComboBoxBaseActions.init(picker, thing, actionContext);
		
		if(thing.valueExists("chronology")) {
			Chronology chronology = JavaFXUtils.getChronology(thing.getString("chronology"));
			if(chronology != null) {
				picker.setChronology(chronology);
			}
		}
		
		if(thing.valueExists("showWeekNumbers")) {
			picker.setShowWeekNumbers(thing.getBoolean("showWeekNumbers"));
		}
	}
	
	public static DatePicker create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DatePicker picker = new DatePicker();
		init(picker, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), picker);
		
		actionContext.peek().put("parent", picker);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return picker;
	}
}
