package xworker.eclipse.zest.model;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class Node {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		IContainer parent = (IContainer) actionContext.get("parent");		
		int style = Constants.getNodeStyle(self.getString("style"));
		
		GraphNode node = new GraphNode(parent, style);
		
		init(self, node, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), node);
		
		return node;
	}
	
	public static void init(Thing self, GraphNode node, ActionContext actionContext){
		node.setData("thing", self);
		node.setData("actionContext", actionContext);
		
		node.setText(self.getMetadata().getLabel());
				
		Color backgroundColor = UtilData.getObject(self.getString("backgroundColor"), actionContext, Color.class);
		if(backgroundColor != null){
			node.setBackgroundColor(backgroundColor);
		}
		
		Color borderColor = UtilData.getObject(self.getString("borderColor"), actionContext, Color.class);
		if(borderColor != null){
			node.setBorderColor(borderColor);
		}
		
		Color borderHighlightColor = UtilData.getObject(self.getString("borderHighlightColor"), actionContext, Color.class);
		if(borderHighlightColor != null){
			node.setBorderHighlightColor(borderHighlightColor);
		}
		
		Color foregroundColor = UtilData.getObject(self.getString("foregroundColor"), actionContext, Color.class);
		if(foregroundColor != null){
			node.setForegroundColor(foregroundColor);
		}
		
		Color highlightColor = UtilData.getObject(self.getString("highlightColor"), actionContext, Color.class);
		if(highlightColor != null){
			node.setHighlightColor(highlightColor);
		}
		
		if(self.getStringBlankAsNull("borderWidth") != null){
			node.setBorderWidth(self.getInt("borderWidth"));
		}
			
		if(self.getStringBlankAsNull("cacheLabel") != null){
			node.setCacheLabel(self.getBoolean("cacheLabel"));
		}
		
		Font font = UtilData.getObject(self.getString("font"), actionContext, Font.class);
		if(font != null){
			node.setFont(font);
		}
		
		Image image = UtilData.getObject(self.getString("image"), actionContext, Image.class);
		if(image != null){
			node.setImage(image);
		}
		
		if(self.getStringBlankAsNull("x") != null && self.getStringBlankAsNull("y") != null){
			node.setLocation(self.getDouble("x"), self.getDouble("y"));
		}
		
		if(self.getStringBlankAsNull("width") != null && self.getStringBlankAsNull("height") != null){
			node.setSize(self.getDouble("width"), self.getDouble("height"));
		}
		
		if(self.getStringBlankAsNull("tooltip") != null){
			node.setTooltip(new Label(self.getString("tooltip")));
		}
		
		node.setVisible(self.getBoolean("visible"));
	}
}
