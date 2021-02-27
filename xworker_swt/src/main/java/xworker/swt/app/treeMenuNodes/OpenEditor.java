package xworker.swt.app.treeMenuNodes;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class OpenEditor {
    public static void run(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
    	Action openEditor = actionContext.getObject("openEditor");
    	
	    //xworker.swt.app.treeMenuNodes.OpenEditor/@actions1/@run
	    Object id = self.doAction("getId", actionContext);
	    Object editor = self.doAction("getEditor", actionContext);
	    Object params = self.doAction("getParams", actionContext);
	    
	    //println id +  editor  + params + editorContainer;
	    openEditor.run(actionContext, "id", id, "editor", editor, "params", params);
	}
}
