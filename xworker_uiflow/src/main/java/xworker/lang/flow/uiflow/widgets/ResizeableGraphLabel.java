package xworker.lang.flow.uiflow.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.internal.GraphLabel;

import xworker.swt.util.ResizeUtil;

public class ResizeableGraphLabel extends GraphLabel{
	GraphNode node;
	
	public ResizeableGraphLabel(GraphNode node, String text, Image i, boolean cacheLabel) {
		super(text, i, cacheLabel);
		this.node = node;
	}

	public void paint(Graphics graphics) {
		super.paint(graphics);
		
		if(node.isSelected()){
			Rectangle rectt = getBounds();
			Color oldColor = graphics.getForegroundColor();
			graphics.setForegroundColor(node.getGraphModel().getDisplay().getSystemColor(SWT.COLOR_BLACK));
			Map<String, Rectangle> recs = getResizeRetangles(rectt.x, rectt.y, rectt.width, rectt.height);
			Rectangle rect = recs.get(ResizeUtil.BOTTOM);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.BOTTOM_LEFT);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.BOTTOM_RIGHT);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.LEFT);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.RIGHT);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.TOP);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.TOP_LEFT);
			graphics.fillRectangle(rect);
			
			rect = recs.get(ResizeUtil.TOP_RIGHT);
			graphics.fillRectangle(rect);
			
			graphics.setForegroundColor(oldColor);
		}
	}
	
	public static Map<String, Rectangle> getResizeRetangles(int x, int y , int width, int height){
		int h = 6;

		if(h > (width / 3)){
			h = width / 3;
		}
		if(h > (height / 3)){
			h = height / 3;
		}
		int hf = h / 2;
				
		Map<String, Rectangle> recs = new HashMap<String, Rectangle>();
		recs.put(ResizeUtil.BOTTOM, new Rectangle(x + width / 2 - hf, y + height - h , h, h));
		
		recs.put(ResizeUtil.BOTTOM_LEFT, new Rectangle(x, y + height - h, h, h));
		
		recs.put(ResizeUtil.BOTTOM_RIGHT, new Rectangle(x + width - h, y + height - h, h, h));
		
		recs.put(ResizeUtil.LEFT, new Rectangle(x, y + height/2 - hf, h, h));
		
		recs.put(ResizeUtil.RIGHT, new Rectangle(x + width - h, y + height /2 - hf, h, h));
		
		recs.put(ResizeUtil.TOP, new Rectangle(x + width / 2 - hf, y, h, h));
		
		recs.put(ResizeUtil.TOP_LEFT, new Rectangle(x, y, h, h));
		
		recs.put(ResizeUtil.TOP_RIGHT, new Rectangle(x + width - h, y, h, h));
		
		return recs;
		
	}
}
