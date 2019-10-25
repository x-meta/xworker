package xworker.eclipse.zest.model;

import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class Container {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		IContainer parent = (IContainer) actionContext.get("parent");
		GraphContainer c = new GraphContainer(parent, SWT.NONE);
		
		LayoutAlgorithm layout = UtilData.getObject(self.getString("layout"), actionContext, LayoutAlgorithm.class);
		if(layout != null){
			c.setLayoutAlgorithm(layout, false);
		}
				
		Node.init(self, c, actionContext);
		
		actionContext.peek().put("parent", c);		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), c);
		
		return c;
	}
}
