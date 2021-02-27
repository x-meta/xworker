package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;

public class SeparatorActions {
	public static void init(Separator s, Thing thing, ActionContext actionContext) {
		ControlActions.init(s, thing, actionContext);
		
        
        if(thing.valueExists("halignment")){
            s.setHalignment(HPos.valueOf(thing.getString("halignment")));
        }
        if(thing.valueExists("orientation")){
            s.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("valignment")){
            s.setValignment(VPos.valueOf(thing.getString("valignment")));
        }
	}
	
	public static Separator create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Separator item = new Separator();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
