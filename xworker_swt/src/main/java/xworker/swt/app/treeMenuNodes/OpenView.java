package xworker.swt.app.treeMenuNodes;

import java.util.Collections;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.app.Workbench;

public class OpenView {
	private static final String TAG = OpenView.class.getName();
	
	public static void run(ActionContext actionContext) {
		//World world = World.getInstance();
		Thing self = actionContext.getObject("self");
		ActionContext parentContext = actionContext.getObject("parentContext");
		
		Workbench workbench = parentContext.getObject("workbench");
		if(workbench == null){
		    Executor.warn(TAG, "OpenView need wrokbench, path=" + self.getMetadata().getPath());
		    return;
		}

		String id = self.doAction("getId", actionContext);
		String type = self.doAction("getType", actionContext);
		Thing composite = self.doAction("getComposite", actionContext);
		Boolean closeable = self.doAction("isCloseable", actionContext);

		workbench.openView(id, composite, type, closeable, Collections.emptyMap());
	}
}
