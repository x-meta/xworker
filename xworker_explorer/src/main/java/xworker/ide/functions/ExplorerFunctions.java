package xworker.ide.functions;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class ExplorerFunctions {
	public static void openUrl(ActionContext actionContext){
		String url = actionContext.getObject("url");
		
		XWorkerUtils.ideOpenUrl(url);
	}
	
	public static void openComposite(ActionContext actionContext){
		Object composite = actionContext.get("composite");
		if(composite instanceof Thing){
			XWorkerUtils.ideOpenComposite((Thing) composite);
		}else if(composite != null){
			String path = String.valueOf(composite);
			Thing com = World.getInstance().getThing(path);
			if(com != null){
				XWorkerUtils.ideOpenComposite(com);
				return;
			}
		}
		
		throw new ActionException("Composite can'not be null");
	}
}
