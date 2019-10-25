package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeCursor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;

public class TreeCursorCreator {
	public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(self.getBoolean("BORDER")){
		    style = style | SWT.BORDER;
		}
		
		Tree parent = (Tree) actionContext.get("parent");
		TreeCursor cursor = new TreeCursor(parent, style);
		
		//背景颜色
		Color background = (Color) ResourceManager.createResource(self.getString("background"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(background != null){
		    cursor.setBackground(background);
		}
		
		//字体颜色
		Color foreground = (Color) ResourceManager.createResource(self.getString("foreground"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(foreground != null){
		    cursor.setForeground(foreground);
		}
		
		try{
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", cursor);
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		actionContext.getScope(0).put(self.getString("name"), cursor);
		return cursor;
	}
}
