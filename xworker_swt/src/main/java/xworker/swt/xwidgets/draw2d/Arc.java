package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class Arc {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		if(shape.getThing().getBoolean("fill")) {
			gc.fillArc(0, 0, shape.getWidth(), shape.getHeight()
					,thing.getInt("startAngle"), thing.getInt("endAngle"));
		}else {
			gc.drawArc(0, 0, shape.getWidth(), shape.getHeight()
					,thing.getInt("startAngle"), thing.getInt("endAngle"));
		}
	}
}
