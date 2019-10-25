package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ImageCanvas {
	private static Logger logger = LoggerFactory.getLogger(ImageCanvas.class);
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Composite parent = (Composite) actionContext.get("parent");
		
		//创建控件
		//ScrolledComposite composite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		//composite.setExpandHorizontal(true);
		//composite.setExpandVertical(true);
		
		//Canvas canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		Canvas canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);
		canvas.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}

			@Override
			public void mouseDown(MouseEvent event) {
				if(event.button == 1){
					Canvas canvas = (Canvas) event.widget;
					Thing thing = (Thing) canvas.getData();
					thing.put("drag", true);
					thing.put("lastX", event.x);
					thing.put("lastY", event.y);
				}
			}

			@Override
			public void mouseUp(MouseEvent event) {
				Canvas canvas = (Canvas) event.widget;
				Thing thing = (Thing) canvas.getData();
				thing.put("drag", false);
			}
		});
		
		if(!SwtUtils.isRWT()) {
			canvas.addMouseMoveListener(new MouseMoveListener(){
				@Override
				public void mouseMove(MouseEvent event) {
					Canvas canvas = (Canvas) event.widget;
					Thing thing = (Thing) canvas.getData();
					if(thing.getBoolean("drag")){
						int lastX = thing.getInt("lastX");
						int lastY = thing.getInt("lastY");
						
						int x = thing.getInt("x") + lastX - event.x;
						int y = thing.getInt("y") + lastY - event.y;
						if(x < 0){
							x = 0;
						}
						if( y < 0){
							y = 0;
						}
						thing.put("x", x);
						thing.put("y", y);
						thing.put("lastX", event.x);
						thing.put("lastY", event.y);
					}
					
					canvas.redraw();
				}
				
			});
			canvas.addMouseWheelListener(new MouseWheelListener(){
				public void mouseScrolled(MouseEvent event) {				
					Canvas canvas = (Canvas) event.widget;
					Thing thing = (Thing) canvas.getData();
					
					Image image = (Image) thing.get("image");
					if(image != null && !image.isDisposed()){
						float scale = thing.getFloat("scale");
						scale = scale + 0.5f / event.count;
											
						Rectangle rt = image.getBounds();
						Rectangle ct = canvas.getClientArea();
											
						float minScale = 1f;
						if(rt.width > ct.width || rt.height > ct.height){
							minScale = 1f * ct.width / rt.width;
							float hScale = 1f * ct.height / rt.height;
							if(minScale > hScale){
								minScale = hScale;
							}
						}
						
						if(scale < minScale){
							scale = minScale;
							thing.put("x", 0);
							thing.put("y", 0);
						}
						
						thing.put("scale", scale);
					}
					
					canvas.redraw();
				}			
			});
		}
		canvas.addListener(SWT.Activate, new Listener() {
		    public void handleEvent(Event e) {
		    	Canvas sc = (Canvas) e.widget;		    	
		        sc.setFocus();
		    }
		}); 
		
		Thing s = self.detach();
		//s.put("composite", composite);
		s.put("canvas", canvas);
		s.put("actionContext", actionContext);
		s.put("scale", 0f);
		s.put("x", 0);
		s.put("y", 0);
		s.set("image", null);
		canvas.addListener(SWT.Paint, new PaintListener(s));
		canvas.setData(s);
		//composite.setContent(canvas);
		
		//设置子节点
		Bindings bindings = actionContext.push(null);
		//bindings.put("parent", composite);
		bindings.put("parent", canvas);
		try{
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//设置图片，如果存在
		String image = self.getStringBlankAsNull("image");
		 if(image != null){			 
			 Image im = (Image) actionContext.get(image);
			 if(im != null){
				 s.doAction("setImage", actionContext, UtilMap.toMap("image", im));
			 }
		 }
		 
		Designer.attach(canvas, self.getMetadata().getPath(), actionContext);
		actionContext.put(self.getMetadata().getName(), s);
		return canvas;
	}
	
	public static void setImage(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//ScrolledComposite composite = (ScrolledComposite) self.get("composite");
		Canvas canvas = (Canvas) self.get("canvas");
		Image image = (Image) actionContext.get("image");
		self.set("image", image);
		/*
		Rectangle rect = image.getBounds();
		
		composite.setMinSize(rect.width, rect.height);
		canvas.setSize(rect.width, rect.height);
		composite.update();
		*/
		canvas.redraw();
	}
	
	static class PaintListener implements Listener{
		Thing thing;
		
		public PaintListener(Thing thing){
			this.thing = thing;
		}
		
		@Override
		public void handleEvent(Event event) {
			try{
				Canvas canvas = (Canvas) thing.get("canvas");
				Image image = (Image) thing.get("image");
				
				//清空屏幕
				GC gc = event.gc;
				Color oldColor = gc.getBackground();
				gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				gc.fillRectangle(canvas.getClientArea());
				gc.setBackground(oldColor);
								
				if(image != null && !image.isDisposed()){
					float scale = thing.getFloat("scale");
					
					Rectangle rt = image.getBounds();
					Rectangle ct = canvas.getClientArea();
					int x  = thing.getInt("x");
					int y = thing.getInt("y");
					if(scale == 0){
						float minScale = 1f;
						if(rt.width > ct.width || rt.height > ct.height){
							minScale = 1f * ct.width / rt.width;
							float hScale = 1f * ct.height / rt.height;
							if(minScale > hScale){
								minScale = hScale;
							}
						}
						
						scale = minScale;
					}
					Rectangle rc = image.getBounds();
					int rwidth = (int) (rc.width * scale);
					int rheight = (int) (rc.height * scale);			
										
					if(rwidth - x < ct.width && x > 0){
						x = rwidth - ct.width;
						thing.put("x", x);
					}
					if(rheight - y < ct.height && y > 0){
						y = rheight - ct.height;
						thing.put("y", y);
					}
					
					gc.drawImage(image, 0, 0, rc.width, rc.height, -x, -y, rwidth, rheight) ;
				}
			}catch(Exception e){
				logger.error("Paint image error, path=" + thing.getMetadata().getPath(), e);
			}
		}
	}
}
