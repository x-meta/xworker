package xworker.swt.app.treeMenuNodes;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;

public class OpenJdbcExplorer {
	private static final String TAG = OpenJdbcExplorer.class.getName();
			
	public static void run(ActionContext actionContext) {
		World world = World.getInstance();
		Thing self = actionContext.getObject("self");
		
		Action openEditor = world.getAction("xworker.swt.app.treeMenuNodes.OpenEditor/@actions1/@run/@ActionDefined/@openEditor");
		Thing editor = world.getThing("xworker.swt.app.editors.JdbcExplorer");
		Thing object = self.doAction("getDataSource", actionContext);
		if(object != null){
		    openEditor.run(actionContext, "id", object.getMetadata().getPath(), 
		                              "editor", editor, 
		                              "params",UtilMap.toMap("dataSource", object));
		}else{
		    Executor.info(TAG, "Datasource not exists, path=" + self.getMetadata().getPath());
		}
	}
}
