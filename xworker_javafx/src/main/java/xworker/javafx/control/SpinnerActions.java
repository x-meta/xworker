package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import xworker.javafx.util.JavaFXUtils;

public class SpinnerActions {
	@SuppressWarnings("unchecked")
	public static void init(Spinner<Object> node, Thing thing, ActionContext actionContext) {
		ControlActions.init(node, thing, actionContext);
		        
		if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("valueFactory")){
        	SpinnerValueFactory<Object> lf = (SpinnerValueFactory<Object>) JavaFXUtils.getObject(thing, "valueFactory", actionContext);
        	if(lf != null) {
        		node.setValueFactory(lf);
        	}
        }
	}
	
	public static Spinner<Object> create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Spinner<Object> item = new Spinner<Object>();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
