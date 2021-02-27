package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.web.HTMLEditor;

public class HTMLEditorActions {
	public static void init(HTMLEditor editor, Thing thing, ActionContext actionContext) {
		ControlActions.init(editor, thing, actionContext);
	}
	
	public static HTMLEditor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		HTMLEditor editor = new HTMLEditor();
		init(editor, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), editor);
		
		actionContext.peek().put("parent", editor);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		return editor;
	}
}
