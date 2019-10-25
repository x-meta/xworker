package xworker.swt.xwidgets.draw2d;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.nebula.cwt.svg.SvgDocument;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleShape;

public class Svg {
	public static final String SVG = "svg";
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		SvgDocument svg = (SvgDocument) shape.getData(SVG);
		if(svg != null) {
			org.eclipse.swt.graphics.Rectangle rec = new org.eclipse.swt.graphics.Rectangle(0, 0, shape.getWidth(), shape.getHeight());
			svg.apply(gc, rec);
			//svg.apply(gc);
		}
	}
	
	@ActionParams(names="shape")
	public static void dispose(SimpleShape shape, ActionContext actionContext) {
		SvgDocument svg = (SvgDocument) shape.getData(SVG);
		if(svg != null) {
			
		}
	}
	
	@ActionParams(names="shape")
	public static void init(SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		SvgDocument svg = null;
		
		File file = thing.doAction("getSvgFile", actionContext);
		if(file != null && file.exists() && file.isFile()) {
			svg = SvgDocument.load(file.getAbsolutePath());			
		}else {
			String svgContent = thing.doAction("getSvgContent", actionContext);
			if(svgContent != null) {
				ByteArrayInputStream bin = new ByteArrayInputStream(svgContent.getBytes());
				svg = SvgDocument.load(bin);
			}
		}
		
		shape.setData(SVG, svg);
	}
}
