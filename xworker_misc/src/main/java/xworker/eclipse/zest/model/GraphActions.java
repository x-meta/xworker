package xworker.eclipse.zest.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.util.UtilData;

public class GraphActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Composite parent = (Composite) actionContext.get("parent");
		
		int style = SWT.None;
		if(self.getBoolean("V_SCROL")){
			style = style | SWT.V_SCROLL;
		}
		
		if(self.getBoolean("H_SCROL")){
			style = style | SWT.H_SCROLL;
		}
		
		if(self.getBoolean("NO_REDRAW_RESIZE")){
			style = style | SWT.NO_REDRAW_RESIZE;
		}
		
		if(self.getBoolean("DOUBLE_BUFFERED")){
			style  = style | SWT.DOUBLE_BUFFERED;
		}
		
		if(self.getBoolean("NO_BACKGROUND")){
			style = style | SWT.NO_BACKGROUND;
		}
		
		if(self.getBoolean("BORDER")){
			style = style | SWT.BORDER;
		}
		
		String align = self.getString("align");
		if("RIGHT_TO_LEFT".equals(align)){
			style = style | SWT.RIGHT_TO_LEFT;
		}else if("LEFT_TO_RIGHT".equals(align)){
			style = style | SWT.LEFT_TO_RIGHT;
		}
		
		Graph graph = new Graph(parent, style);
		if(self.getStringBlankAsNull("width") != null && self.getStringBlankAsNull("height") != null){
			graph.setPreferredSize(self.getInt("width"), self.getInt("height"));
		}
		
		actionContext.peek().put("parent", graph);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		LayoutAlgorithm layoutAlgorithm = UtilData.getObject(self.getString("layoutAlgorithm"), actionContext, LayoutAlgorithm.class);
		if(layoutAlgorithm != null){
			graph.setLayoutAlgorithm(layoutAlgorithm, true);
		}
		
		Designer.attach(graph, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), graph);
		
		return graph;
	}
}
