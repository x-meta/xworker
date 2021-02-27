package xworker.javafx.scene;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;

public class GroupActions {
	public static void init(Group group, Thing thing, ActionContext actionContext) {
		if(thing.valueExists("autoSizeChildren")) {
			group.setAutoSizeChildren(thing.getBoolean("autoSizeChildren"));
		}
	}
	
	public static Group create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = actionContext.getObject("parent");
		
		Group group = new Group();
		init(group, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), group);
		
		actionContext.peek().put("parent", group);
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Node) {
				group.getChildren().add((Node) obj);
			}
		}
		
		if(parent instanceof Scene) {
			((Scene) parent).setRoot(group);
		}
		
		return group;
	}
}
