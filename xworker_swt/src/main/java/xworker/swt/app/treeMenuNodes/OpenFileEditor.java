package xworker.swt.app.treeMenuNodes;

import java.io.File;
import java.io.IOException;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;

public class OpenFileEditor {
	private static final String TAG = OpenFileEditor.class.getName();
	
    public static void  run(ActionContext actionContext) throws IOException{
	    //xworker.swt.app.treeMenuNodes.OpenFileEditor/@actions/@run
    	Thing self = actionContext.getObject("self");
    	World world = World.getInstance();
	    Action openEditor = world.getAction("xworker.swt.app.treeMenuNodes.OpenEditor/@actions1/@run/@ActionDefined/@openEditor");
	    Thing editor = world.getThing("xworker.swt.app.editors.FileTextEditor");
	    File file = self.doAction("getFile", actionContext);
	    if(file != null){
	        openEditor.run(actionContext, "id", file.getCanonicalPath(), 
	                                  "editor", editor, 
	                                  "params",UtilMap.toMap("file", file));
	    }else{
	        Executor.info(TAG, "file not exists, path=" + self.getMetadata().getPath());
	    }
	}
}
