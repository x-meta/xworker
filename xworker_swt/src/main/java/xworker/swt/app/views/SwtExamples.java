package xworker.swt.app.views;

import java.util.Map;

import org.eclipse.swt.widgets.Tree;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SwtExamples {
    @SuppressWarnings("unchecked")
	public static void treeSelection(ActionContext actionContext){
	    //xworker.swt.app.views.SwtExamples/@tree/@Listeners/@treeSelection/@treeSelection
	    //获取节点的事物
    	Tree tree = actionContext.getObject("tree");
	    Thing thing = (Thing) ((Map<String, Object>) tree.getSelection()[0].getData()).get("_source");
	    String compositePath = thing.getStringBlankAsNull("extends");
	    if(compositePath != null){
	    	Action openEditor = actionContext.getObject("openEditor");
	        openEditor.run(actionContext, "composite", compositePath);
	    }
	}
}
