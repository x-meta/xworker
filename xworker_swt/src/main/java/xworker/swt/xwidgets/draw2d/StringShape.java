package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class StringShape {
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		String text = thing.doAction("getText", actionContext);
		if(text != null) {
			if(shape.getThing().getBoolean("transparent")) {
				gc.drawText(text, 0, 0, true);
			}else {
				gc.drawText(text, 0, 0, false);
			}
		
			Point size = gc.textExtent(text);
			shape.setWidth(size.x);
			shape.setHeight(size.y);
		}
	}
}
