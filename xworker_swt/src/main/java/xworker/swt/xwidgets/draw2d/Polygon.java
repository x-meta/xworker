package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;
import xworker.util.UtilData;

public class Polygon {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		if(thing.getStringBlankAsNull("pointArray") == null) {
			return;
		}
		int[] pointArray = UtilData.getIntArray(thing.getString("pointArray"));
		
		//计算pintArray的width和height
		int width = 0;
		int height = 0;
		for(int i=0; i<pointArray.length; i++) {
			if(pointArray[i] > width) {
				width = pointArray[i];
			}
			if(pointArray.length > i + 1) {
				if(pointArray[i + 1] > height) {
					height = pointArray[i + 1];
				}
			}
			
			i++;
		}
		
		//计算缩放
		if(shape.getWidth() != 0) {
			double scalex = 1d * shape.getWidth() / width;
			for(int i=0; i<pointArray.length; i++) {
				pointArray[i] = (int) (pointArray[i] * scalex);
				i++;
			}
		}
		if(shape.getHeight() != 0) {
			double scaley = 1d * shape.getHeight() /height ;
			for(int i=0; i<pointArray.length; i++) {
				if(pointArray.length > i + 1) {
					pointArray[i + 1] = (int) (pointArray[i + 1] * scaley);
				}
				i++;
			}
		}
		
		if(shape.getThing().getBoolean("fill")) {
			gc.fillPolygon(pointArray);
		}else {
			gc.drawPolygon(pointArray);
		}
	}
}
