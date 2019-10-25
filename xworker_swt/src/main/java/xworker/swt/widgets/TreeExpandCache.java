package xworker.swt.widgets;

import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.XWorkerTreeUtil;

public class TreeExpandCache implements TreeListener{
	String cachePath;
	
	public TreeExpandCache(String cachePath){
		this.cachePath = cachePath;
	}

	public void setCachePath(String cachePath){
		this.cachePath = cachePath;
	}
	
	@Override
	public void treeCollapsed(TreeEvent event) {
		Object data = event.item.getData();
		if(data != null){
			String name = String.valueOf(data);
			if(!"".equals(name)){
				XWorkerTreeUtil.setExpaned(cachePath, name, false);
			}
		}
	}

	@Override
	public void treeExpanded(TreeEvent event) {
		Object data = event.item.getData();
		if(data != null){
			String name = String.valueOf(data);
			if(!"".equals(name)){
				XWorkerTreeUtil.setExpaned(cachePath, name, true);
			}
		}
	}

	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String cachePath = (String) self.doAction("getCachePath", actionContext);
		if("".equals(cachePath)){
			cachePath = null;
		}
		
		TreeExpandCache cache = new TreeExpandCache(cachePath);
		Tree tree = actionContext.getObject("parent");
		tree.addTreeListener(cache);
		
		actionContext.g().put(self.getMetadata().getName(), cache);
	}
}
