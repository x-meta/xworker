package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class Line {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		if(shape.getThing().getBoolean("rotate90")) {
			gc.drawLine(0, shape.getHeight(), shape.getWidth(), 0);
		}else {
			gc.drawLine(0, 0, shape.getWidth(), shape.getHeight());
		}
	}
}
