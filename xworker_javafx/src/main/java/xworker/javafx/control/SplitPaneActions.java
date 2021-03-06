package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class SplitPaneActions {
	public static void init(SplitPane node, Thing thing, ActionContext actionContext) {
		ControlActions.init(node, thing, actionContext);
		        
		if(thing.valueExists("orientation")){
            node.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("dividerPositions")){
        	String dp = thing.getString("dividerPositions");
        	
        	int index = 0;
        	for(String d : dp.split("[,]")) {
        		d = d.trim();
        		node.setDividerPosition(index, Double.parseDouble(d));
        		index++;
        	}
        }
	}
	
	public static SplitPane create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		SplitPane item = new SplitPane();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node) {
				item.getItems().add((Node) obj);
			}
		}
		
		return item;
	}
}
