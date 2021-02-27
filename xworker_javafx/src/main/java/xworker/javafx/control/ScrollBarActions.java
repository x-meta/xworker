package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;

public class ScrollBarActions {
	public static void init(ScrollBar s, Thing thing, ActionContext actionContext) {
		ControlActions.init(s, thing, actionContext);
		
        if(thing.valueExists("blockIncrement")){
            s.setBlockIncrement(thing.getDouble("blockIncrement"));
        }
        if(thing.valueExists("max")){
            s.setMax(thing.getDouble("max"));
        }
        if(thing.valueExists("min")){
            s.setMin(thing.getDouble("min"));
        }
        if(thing.valueExists("orientation")){
            s.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("unitIncrement")){
            s.setUnitIncrement(thing.getDouble("unitIncrement"));
        }
        if(thing.valueExists("value")){
            s.setValue(thing.getDouble("value"));
        }
	}
	
	public static ScrollBar create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ScrollBar item = new ScrollBar();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);			
		}
		
		return item;
	}
}
