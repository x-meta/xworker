package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.control.Hyperlink;

public class HyperlinkActions {
	public static void init(Hyperlink link, Thing thing, ActionContext actionContext) {
		ButtonBaseActions.init(link, thing, actionContext);

		if (thing.valueExists("visited")) {
			link.setVisited(thing.getBoolean("visited"));
		}
	}

	public static Hyperlink create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		Hyperlink link = new Hyperlink();
		init(link, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), link);

		actionContext.peek().put("parent", link);
		for (Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}

		return link;
	}
}
