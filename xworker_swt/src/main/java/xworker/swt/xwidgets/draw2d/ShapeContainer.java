package xworker.swt.xwidgets.draw2d;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;

import xworker.swt.xwidgets.SimpleDraw2d;
import xworker.swt.xwidgets.SimpleShape;

public class ShapeContainer {
	public static final String KEY = "Control_Data";
	@ActionParams(names="canvas,gc,shape")
	public static void draw(Canvas canvas, GC gc, SimpleShape shape, ActionContext actionContext) {
		Control control = (Control) shape.getData(KEY);
		if(control != null && !control.isDisposed()) {
			int offset = shape.isSelected() ? 12 : 0;
			//System.out.println("ControlShape, selected=" + shape.isSelected());
			Point clientSize = new Point(shape.getWidth() - offset, shape.getHeight() -offset);
			control.setSize(clientSize);
			control.setLocation(shape.getX() + offset/2, shape.getY() + offset/2);
			
			//Point size = control.getSize();
			//System.out.println(bounds[0] + "," + bounds[1] + "," + bounds[2] + "," + bounds[3]);
			Transform oldTransform = new Transform(gc.getDevice());
			gc.getTransform(oldTransform);
			Transform transform = new Transform(gc.getDevice());
			transform.translate(shape.getX() + offset/2, shape.getY() + offset/2);
			//transform.scale(1f * shape.getWidth() / (clientSize.x), 1f * shape.getHeight() / (clientSize.y));
			
			//transform.multiply(oldTransform);
			gc.setTransform(transform);

			control.print(gc);
			gc.setTransform(oldTransform);
			transform.dispose();
			oldTransform.dispose();
		}	
	}
	
	@ActionParams(names="shape")
	public static void dispose(SimpleShape shape, ActionContext actionContext) {
		Control control = (Control) shape.getData(KEY);
		if(control != null && !control.isDisposed()) {
			control.dispose();
		}	
	}
	
	@ActionParams(names="shape")
	public static void init(SimpleShape shape, ActionContext actionContext) {
		Thing thing = shape.getThing();
		Control control = (Control) shape.getData(KEY);
		if(control != null && !control.isDisposed()) {
			control.dispose();
		}		
		
		Thing controlThing = World.getInstance().getThing("xworker.swt.xwidgets.draw2d.ShapeContainerShell/@mainComposite");
		if(controlThing != null) {
			ActionContext ac = new ActionContext();
			ac.put("parent", shape.getCanvas());
			ac.put("parentContext", actionContext);
			
			control = controlThing.doAction("create", ac);
			control.setVisible(false);
			shape.setData("actionContext", ac);
			
			//设置Shapes
			SimpleDraw2d draw2d= ac.getObject("draw2d");
			Thing shapes = thing.doAction("getShapes", actionContext);
			draw2d.setShapes(shapes);
			//Point size = control.getSize();
			//shape.setWidth(size.x);
			//shape.setHeight(size.y);
		}
		
		shape.setData(KEY, control);
	}
	
	@ActionParams(names="shape")
	public static void selection(SimpleShape shape, ActionContext actionContext) {
		Control control = (Control) shape.getData(KEY);
		if(control != null && !control.isDisposed()) {
			//int offset = shape.isSelected() ? 6 : 0;
			//control.setLocation(shape.getX() + offset, shape.getY() + offset);
			control.setVisible(true);
		}
	}
	
	@ActionParams(names="shape")
	public static void deSelection(SimpleShape shape, ActionContext actionContext) {
		Control control = (Control) shape.getData(KEY);
		if(control != null && !control.isDisposed()) {
			control.setVisible(false);
		}
	}
	
	@ActionParams(names="shape")
	public static void defaultSelection(SimpleShape shape, ActionContext actionContext) {
		
	}
	
	@ActionParams(names="shape")
	public static void save(SimpleShape shape, ActionContext actionContext) {
		ActionContext ac = (ActionContext) shape.getData("shape");
		if(ac != null) {
			SimpleDraw2d draw2d= ac.getObject("draw2d");
			if(draw2d != null) {
				draw2d.save();
			}
		}
	}
}
