package xworker.swt.app.treeMenuNodes;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;

public class OpenDataObjectEditor {
	private static final String TAG = OpenDataObjectEditor.class.getName();
	
    public static void run(ActionContext actionContext){
	    //xworker.swt.app.treeMenuNodes.OpenDataObjectEditor/@actions/@run
    	Thing self = actionContext.getObject("self");
    	World world = World.getInstance();
	    Action openEditor = world.getAction("xworker.swt.app.treeMenuNodes.OpenEditor/@actions1/@run/@ActionDefined/@openEditor");
	    Thing editor = world.getThing("xworker.swt.app.editors.DataObjectEditor");
	    Thing object = self.doAction("getDataObject", actionContext);
	    if(object != null){
	        openEditor.run(actionContext, "id", object.getMetadata().getPath(), 
	                                  "editor", editor, 
	                                  "params", UtilMap.toMap("dataObject",  object));
	    }else{
	        Executor.info(TAG, "DataObject not exists, path=" + self.getMetadata().getPath());
	    }
	}
}
