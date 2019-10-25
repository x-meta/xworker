package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class Oval {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		if(shape.getThing().getBoolean("fill")) {
			gc.fillOval(0, 0, shape.getWidth(), shape.getHeight());
		}else {
			gc.drawOval(0, 0, shape.getWidth(), shape.getHeight());
		}
	}
}
