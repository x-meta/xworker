package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class TitledPaneActions {
	public static void init(TitledPane titledPane, Thing thing, ActionContext actionContext) {
		LabeledActions.init(titledPane, thing, actionContext);

		if (thing.valueExists("animated")) {
			titledPane.setAnimated(thing.getBoolean("animated"));
		}
		if (thing.valueExists("collapsible")) {
			titledPane.setCollapsible(thing.getBoolean("collapsible"));
		}
		if (thing.valueExists("expanded")) {
			titledPane.setExpanded(thing.getBoolean("expanded"));
		}
	}

	public static TitledPane create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		//Object parent = actionContext.getObject("parent");
		
		TitledPane pane = new TitledPane();
		init(pane, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), pane);
		
		actionContext.peek().put("parent", pane);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node) {
				pane.setContent((Node) obj);
			}
		}
		
		Node content = self.doAction("getContent", actionContext);
		if(content != null) {
			pane.setContent(content);
		}
		
		
		return pane;
	}
}
