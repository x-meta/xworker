package xworker.eclipse.zest.zestmodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.style.StyleSetStyleCreator;

public class Node {
	/**
	 * 改方法应该由节点的实例调用。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static GraphNode create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Graph parent = actionContext.getObject("parent");
		
		GraphNode node = new GraphNode(parent, SWT.NONE);
		
		//背景顏色
		Color backgroundColor = (Color) StyleSetStyleCreator.createResource(self.getString("backgroundColor"), 
	                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(backgroundColor != null){
            node.setBackgroundColor(backgroundColor);
        }
	        
        //边框颜色borderColor
		Color borderColor = (Color) StyleSetStyleCreator.createResource(self.getString("borderColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
	    if(borderColor != null){
	        node.setBorderColor(borderColor);
	    }        
	    
	    //borderHighlightColor
		Color borderHighlightColor = (Color) StyleSetStyleCreator.createResource(self.getString("borderHighlightColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
	    if(borderHighlightColor != null){
	        node.setBorderHighlightColor(borderHighlightColor);
	    }          
	    
	    //borderWidth
	    if(self.getStringBlankAsNull("borderWidth") != null){
	    	node.setBorderWidth(self.getInt("borderWidth"));
	    }
	    
	    //cacheLabel
	    if(self.getStringBlankAsNull("cacheLabel") != null){
	    	node.setCacheLabel(self.getBoolean("cacheLabel"));
	    }
	    
	    //font
        Font font = (Font) StyleSetStyleCreator.createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            node.setFont(font);
        }
        
        //foregroundColor
		Color foregroundColor = (Color) StyleSetStyleCreator.createResource(self.getString("foregroundColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
	    if(foregroundColor != null){
	        node.setForegroundColor(foregroundColor);
	    }           
	    
	    //highlightColor
		Color highlightColor = (Color) StyleSetStyleCreator.createResource(self.getString("highlightColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
	    if(highlightColor != null){
	        node.setHighlightColor(highlightColor);
	    }     	    
	    
	    //image
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            node.setImage(image);
        }
        
        //x,y
        if(self.getStringBlankAsNull("x") != null && self.getStringBlankAsNull("y") != null){
        	double x = self.getDouble("x");
        	double y = self.getDouble("y");
        	node.setLocation(x, y);
        }
        
        //width,height
        if(self.getStringBlankAsNull("width") != null && self.getStringBlankAsNull("height") != null){
        	double width = self.getDouble("width");
        	double height = self.getDouble("height");
        	node.setSize(width, height);
        }
        
        node.setVisible(self.getBoolean("visible"));
        
        String text = self.getStringBlankAsNull("text");
        if(text != null){
        	node.setText(text);
        }
        
        //数据是事物本身
        node.setData(self);
        
        //保存变量
        actionContext.g().put(self.getMetadata().getName(), node);
        
		return node;
	}
}
