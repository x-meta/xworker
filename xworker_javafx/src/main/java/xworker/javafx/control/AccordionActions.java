package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class AccordionActions {
	public static void init(Accordion accordion, Thing thing, ActionContext actionContext) {
		ControlActions.init(accordion, thing, actionContext);
	}
	
	public static Accordion create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Accordion accordion = new Accordion();
		init(accordion, self, actionContext);		
		actionContext.g().put(self.getMetadata().getName(), accordion);
		
		actionContext.peek().put("parent", accordion);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof TitledPane) {
				accordion.getPanes().add((TitledPane) obj);
			}
		}
		
		return accordion;
	}
	
}
