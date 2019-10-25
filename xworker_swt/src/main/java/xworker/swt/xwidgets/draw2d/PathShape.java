package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.xwidgets.SimpleShape;

public class PathShape {
	private static final String PATH = "path";
	
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		Path path = (Path) shape.getData(PATH);		
		if(path != null && !path.isDisposed()) {
			float bounds[] = new float[4];
			path.getBounds(bounds);
			//System.out.println(bounds[0] + "," + bounds[1] + "," + bounds[2] + "," + bounds[3]);
			Transform oldTransform = new Transform(gc.getDevice());
			gc.getTransform(oldTransform);
			Transform transform = new Transform(gc.getDevice());
			transform.translate(shape.getX(), shape.getY());
			transform.scale(shape.getWidth() / (bounds[0] + bounds[2]), shape.getHeight() / (bounds[1] + bounds[3]));
			
			//transform.multiply(oldTransform);
			gc.setTransform(transform);

			if(thing.getBoolean("fill")) {
				gc.fillPath(path);
			}else {
				gc.drawPath(path);
			}
			
			gc.setTransform(oldTransform);
			transform.dispose();
			oldTransform.dispose();
		}
	}
	
	@ActionParams(names="shape")
	public static void dispose(SimpleShape shape, ActionContext actionContext) {
		Path path = (Path) shape.getData(PATH);
		if(path != null && !path.isDisposed()) {
			path.dispose();
		}
	}
	
	@ActionParams(names="shape")
	public static void init(SimpleShape shape, ActionContext actionContext) {
		Path path = (Path) shape.getData(PATH);
		//创建新的Path
		if(path != null && path.isDisposed() == false) {
			path.dispose();			
		}
		
		path = new Path(shape.getDevice());
		shape.setData(PATH, path);
		Thing thing = shape.getThing();
		for(Thing child : thing.getChilds()) {
			child.doAction("initPath", actionContext, PATH, path);
		}
		
		
	}
		
	@ActionParams(names="self,shape,path")
	public static void initArc(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.addArc(self.getFloat("x"), self.getFloat("y"), self.getFloat("width"), self.getFloat("height"),
				self.getFloat("startAngle"), self.getFloat("arcAngle"));
	}
	
	@ActionParams(names="self,shape,path")
	public static void initPath(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		for(Thing child : self.getChilds()) {
			child.doAction("initPath", actionContext, PATH, path);
		}
	}
	
	@ActionParams(names="self,shape,path")
	public static void initRectangle(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.addRectangle(self.getFloat("x"), self.getFloat("y"), self.getFloat("width"), self.getFloat("height"));
	}
	
	@ActionParams(names="self,shape,path")
	public static void initString(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		String str = self.getStringBlankAsNull("string");
		if(str != null) {
			Font font = (Font) StyleSetStyleCreator.createResource(self.getString("font"), 
	                "xworker.swt.graphics.Font", "fontData", actionContext);
			path.addString(str, self.getFloat("x"), self.getFloat("y"), font);
		}
	}
	
	@ActionParams(names="self,shape,path")
	public static void initClose(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.close();
	}
	
	@ActionParams(names="self,shape,path")
	public static void initCubicTo(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.cubicTo(self.getFloat("cx1"), self.getFloat("cy1"), self.getFloat("cx2"),
				self.getFloat("cy2"), self.getFloat("x"), self.getFloat("y"));		
	}
	
	@ActionParams(names="self,shape,path")
	public static void initLineTo(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.lineTo(self.getFloat("x"), self.getFloat("y"));
	}
	
	@ActionParams(names="self,shape,path")
	public static void initMoveTo(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.moveTo(self.getFloat("x"), self.getFloat("y"));
	}
	
	@ActionParams(names="self,shape,path")
	public static void initQuadTo(Thing self, SimpleShape shape, Path path, ActionContext actionContext) {
		path.quadTo(self.getFloat("cx"), self.getFloat("cy"), self.getFloat("x"), self.getFloat("y"));		
	}
}
