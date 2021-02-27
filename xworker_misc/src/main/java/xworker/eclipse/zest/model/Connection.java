package xworker.eclipse.zest.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class Connection {
	private static final String TAG = Connection.class.getName();
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String style = self.getString("style");
		int s = SWT.None;
		if("CONNECTIONS_DASH".equals(style)){
			s = s | ZestStyles.CONNECTIONS_DASH;
		}else if("CONNECTIONS_DASH_DOT".equals(style)){
			s = s | ZestStyles.CONNECTIONS_DASH_DOT;
		}else if("CONNECTIONS_DIRECTED".equals(style)){
			s = s | ZestStyles.CONNECTIONS_DIRECTED;
		}else if("CONNECTIONS_DOT".equals(style)){
			s = s | ZestStyles.CONNECTIONS_DOT;
		}else if("CONNECTIONS_SOLID".equals(style)){
			s = s | ZestStyles.CONNECTIONS_SOLID;
		}
		
		GraphNode source = UtilData.getObject("source", actionContext, GraphNode.class);
		GraphNode destination = UtilData.getObject("destination", actionContext, GraphNode.class);
		
		if(destination != null && source != null){
			org.eclipse.zest.core.widgets.Graph parent = (org.eclipse.zest.core.widgets.Graph) actionContext.get("parent");
			GraphConnection con = new GraphConnection(parent, s, source, destination);
			
			if(self.getStringBlankAsNull("curveDepth") != null){
				con.setCurveDepth(self.getInt("curveDepth"));
			}
			
			Color lineColor = UtilData.getObject("lineColor", actionContext, Color.class);
			if(lineColor != null){
				con.setLineColor(lineColor);
			}
			
			if(self.getStringBlankAsNull("lineWidth") != null){
				con.setLineWidth(self.getInt("lineWidth"));
			}
			
			if(self.getStringBlankAsNull("weight") != null){
				con.setWeight(self.getDouble("weight"));
			}
			
			actionContext.getScope(0).put(self.getMetadata().getName(), con);
			
			return con;
		}else{
			Executor.info(TAG, "source or destination not exists, connection not created, thing=" + self.getMetadata().getPath());
			
			return null;
		}
		
	}
}
