package xworker.eclipse.zest.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Layouts {
	public static int getStyle(String styles){
		int style = SWT.None;
		
		if(styles == null || styles.equals("")){
			return style;
		}
		
		for(String s : styles.split("[,]")){
			if("ENFORCE_BOUNDS".equals(s)){
				style = style | LayoutStyles.ENFORCE_BOUNDS;
			}else if("NO_LAYOUT_NODE_RESIZING".equals(s)){
				style = style | LayoutStyles.NO_LAYOUT_NODE_RESIZING;
			}
		}
		
		return style;
	}
	
	public static CompositeLayoutAlgorithm createCompositeLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		
		//子节点是要组合的布局
		List<LayoutAlgorithm> las = new ArrayList<LayoutAlgorithm>();
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof LayoutAlgorithm){
				las.add((LayoutAlgorithm) obj);
			}
		}
		
		LayoutAlgorithm[] ll = new LayoutAlgorithm[las.size()];
		las.toArray(ll);
		CompositeLayoutAlgorithm l = new CompositeLayoutAlgorithm(style, ll);
		
		actionContext.put(self.getMetadata().getName(), l);
		return l;
	}
	
	public static SpringLayoutAlgorithm createSpringLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		SpringLayoutAlgorithm l = new SpringLayoutAlgorithm(style);
		
		if(self.getStringBlankAsNull("iterations") != null){
			l.setIterations(self.getInt("iterations"));
		}
		
		l.setRandom(self.getBoolean("random"));
		
		if(self.getStringBlankAsNull("gravitation") != null){
			l.setSpringGravitation(self.getDouble("gravitation"));
		}
		
		if(self.getStringBlankAsNull("length") != null){
			l.setSpringLength(self.getDouble("length"));
		}
		
		if(self.getStringBlankAsNull("move") != null){
			l.setSpringMove(self.getDouble("move"));
		}
		
		if(self.getStringBlankAsNull("strain") != null){
			l.setSpringStrain(self.getDouble("strain"));
		}
		
		if(self.getStringBlankAsNull("timeout") != null){
			l.setSpringTimeout(self.getLong("timeout"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static DirectedGraphLayoutAlgorithm createDirectedGraphLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		DirectedGraphLayoutAlgorithm l = new DirectedGraphLayoutAlgorithm(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static GridLayoutAlgorithm createGridLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		GridLayoutAlgorithm l = new GridLayoutAlgorithm(style);

		if(self.getStringBlankAsNull("rowPadding") != null){
			l.setRowPadding(self.getInt("rowPadding"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static HorizontalLayoutAlgorithm createHorizontalLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		HorizontalLayoutAlgorithm l = new HorizontalLayoutAlgorithm(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static HorizontalShift createHorizontalShift(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		HorizontalShift l = new HorizontalShift(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static HorizontalTreeLayoutAlgorithm createHorizontalTreeLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		HorizontalTreeLayoutAlgorithm l = new HorizontalTreeLayoutAlgorithm(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static RadialLayoutAlgorithm createRadialLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		RadialLayoutAlgorithm l = new RadialLayoutAlgorithm(style);
		
		if(self.getStringBlankAsNull("startDegree") != null && self.getStringBlankAsNull("endDegree") != null){
			l.setRangeToLayout(self.getDouble("startDegree"), self.getDouble("endDegree"));
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static VerticalLayoutAlgorithm createVerticalLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		VerticalLayoutAlgorithm l = new VerticalLayoutAlgorithm(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
	
	public static TreeLayoutAlgorithm createTreeLayoutAlgorithm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = getStyle(self.getString("styles"));
		TreeLayoutAlgorithm l = new TreeLayoutAlgorithm(style);

		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
}
