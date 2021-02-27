package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;
import xworker.javafx.util.JavaFXUtils;

public class SliderActions {
	@SuppressWarnings("unchecked")
	public static void init(Slider node, Thing thing, ActionContext actionContext) {
		ControlActions.init(node, thing, actionContext);
		        
		if(thing.valueExists("blockIncrement")){
            node.setBlockIncrement(thing.getDouble("blockIncrement"));
        }
        if(thing.valueExists("labelFormatter")){
        	StringConverter<Double> lf = (StringConverter<Double>) JavaFXUtils.getObject(thing, "labelFormatter", actionContext);
        	if(lf != null) {
        		node.setLabelFormatter(lf);
        	}
        }
        if(thing.valueExists("majorTickUnit")){
            node.setMajorTickUnit(thing.getDouble("majorTickUnit"));
        }
        if(thing.valueExists("max")){
            node.setMax(thing.getDouble("max"));
        }
        if(thing.valueExists("minorTickCount")){
            node.setMinorTickCount(thing.getInt("minorTickCount"));
        }
        if(thing.valueExists("min")){
            node.setMin(thing.getDouble("min"));
        }
        if(thing.valueExists("orientation")){
            node.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("showTickLabels")){
            node.setShowTickLabels(thing.getBoolean("showTickLabels"));
        }
        if(thing.valueExists("showTickMarks")){
            node.setShowTickMarks(thing.getBoolean("showTickMarks"));
        }
        if(thing.valueExists("snapToTicks")){
            node.setSnapToTicks(thing.getBoolean("snapToTicks"));
        }
        if(thing.valueExists("valueChanging")){
            node.setValueChanging(thing.getBoolean("valueChanging"));
        }
        if(thing.valueExists("value")){
            node.setValue(thing.getDouble("value"));
        }
	}
	
	public static Slider create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Slider item = new Slider();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return item;
	}
}
