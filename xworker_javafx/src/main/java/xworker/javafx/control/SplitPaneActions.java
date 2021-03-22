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
		if(self.valueExists("dividerPositions")){
			String dp = self.getString("dividerPositions");

			String dps[] = dp.split("[,]");
			double ds[] = new double[dps.length];
			int index = 0;
			for(String d : dps) {
				ds[index] = Double.parseDouble(d);
				index++;
			}
			item.setDividerPositions(ds);
		}
		return item;
	}
}
