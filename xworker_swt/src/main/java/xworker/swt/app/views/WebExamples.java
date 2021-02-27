package xworker.swt.app.views;

import java.util.Map;

import org.eclipse.swt.widgets.Tree;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class WebExamples {
    @SuppressWarnings("unchecked")
	public static void treeSelection(ActionContext actionContext){
	    //xworker.swt.app.views.WebExamples/@tree/@Listeners/@treeSelection/@GroovyAction
	    //获取节点的事物
    	Tree tree = actionContext.getObject("tree");
	    Thing thing = (Thing) ((Map<String, Object>) tree.getSelection()[0].getData()).get("_source");
	    String thingPath = thing.getStringBlankAsNull("extends");
	    if(thingPath != null){
	    	Action openEditor = actionContext.getObject("openEditor");
	    	
	        openEditor.run(actionContext,  UtilMap.toMap("thing", thingPath));
	    }
	}
}
