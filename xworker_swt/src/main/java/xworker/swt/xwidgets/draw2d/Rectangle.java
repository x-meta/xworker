package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class Rectangle {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		if(shape.getThing().getBoolean("fill")) {
			gc.fillRectangle(0, 0, shape.getWidth(), shape.getHeight());
		}else {
			gc.drawRectangle(0, 0, shape.getWidth(), shape.getHeight());
		}
	}
}
