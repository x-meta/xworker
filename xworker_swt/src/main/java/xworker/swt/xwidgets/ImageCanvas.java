package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.SWTControl;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ImageCanvas implements MouseListener, MouseMoveListener, MouseWheelListener, PaintListener, SWTControl{
	private static String TAG = ImageCanvas.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	Canvas canvas;
	int x;
	int y;
	int lastX;
	int lastY;
	float scale = 0f;
	boolean drag = false;
	Image image = null;
	//是否是自己创建的
	boolean imageSelfCreated = false;
	boolean autoFit = true;
	boolean disableResize = false;
	
	public ImageCanvas(Canvas canvas, Thing thing, ActionContext actionContext) {
		this.canvas = canvas;
		this.thing = thing;
		this.actionContext = actionContext;
		Boolean autoFit = thing.doAction("isAutoFit", actionContext);
		Boolean disableResize = thing.doAction("isDisableResize", actionContext);
		if(autoFit != null) {
			this.autoFit = autoFit;
		}
		if(disableResize != null) {
			this.disableResize = disableResize;
		}
		
		this.canvas.addMouseListener(this);
		if(!SwtUtils.isRWT()) {
			canvas.addMouseMoveListener(this);
			canvas.addMouseWheelListener(this);
		}
		canvas.addPaintListener(this);
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent event) {
	}

	@Override
	public void mouseDown(MouseEvent event) {
		if(disableResize) {
			return;
		}
		
		if(event.button == 1){
			this.drag = true;
			this.lastX = event.x;
			this.lastY = event.y;
		}
	}

	@Override
	public void mouseUp(MouseEvent event) {
		this.drag = false;
	}
	
	@Override
	public void mouseMove(MouseEvent event) {
		if(drag) {
			autoFit = false;
			x = x + lastX - event.x;
			y = y + lastY - event.y;
			lastX = event.x;
			lastY = event.y;
			/*
			if(x < 0){
				x = 0;
			}
			if( y < 0){
				y = 0;
			}*/
			canvas.redraw();
		}
	}
	
	public void mouseScrolled(MouseEvent event) {
		if(disableResize) {
			return;
		}
		
		if(image != null && !image.isDisposed()){
			autoFit = false;
			scale = scale + 0.5f / event.count;
								
			//Rectangle rt = image.getBounds();
			//Rectangle ct = canvas.getClientArea();
								
			float minScale = 0.1f;
			/*if(rt.width > ct.width || rt.height > ct.height){
				minScale = 1f * ct.width / rt.width;
				float hScale = 1f * ct.height / rt.height;
				if(minScale > hScale){
					minScale = hScale;
				}
			}
			
			*/
			if(scale < minScale){
				scale = minScale;
				//x = 0;
				//y = 0;
			}
		}
		
		canvas.redraw();
	}			

	
	
	@Override
	public void paintControl(PaintEvent event) {
		try{
			if(autoFit) {
				fitImage();
			}
			
			//清空屏幕
			GC gc = event.gc;
			Color oldColor = gc.getBackground();
			gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			gc.fillRectangle(canvas.getClientArea());
			gc.setBackground(oldColor);
							
			if(image != null && !image.isDisposed()){
				Rectangle rt = image.getBounds();
				Rectangle ct = canvas.getClientArea();
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
					//x = rwidth - ct.width;
				}
				if(rheight - y < ct.height && y > 0){
					//y = rheight - ct.height;
				}
				
				gc.drawImage(image, 0, 0, rc.width, rc.height, -x, -y, rwidth, rheight) ;
			}
		}catch(Exception e){
			Executor.error(TAG, "Paint image error, path=" + thing.getMetadata().getPath(), e);
		}
	}
	
	@Override
	public Control getControl() {		
		return canvas;
	}
	
	public void setImage(Image image) {
		setImage(image, false);
	}
	
	public void setImage(Image image, boolean selfCreated) {
		if(image == null) {
			return;
		}
		
		if(imageSelfCreated) {
			this.image.dispose();
		}
		
		this.imageSelfCreated = selfCreated;
		this.image = image;
		x=0;
		y=0;
		scale = 1f;
		canvas.getDisplay().asyncExec(new Runnable(){
			public void run() {				
				canvas.redraw();
			}
		});
	}
	
	public boolean isAutoFit() {
		return autoFit;
	}

	public void setAutoFit(boolean autoFit) {
		this.autoFit = autoFit;
	}

	public boolean isDisableResize() {
		return disableResize;
	}

	public void setDisableResize(boolean disableResize) {
		this.disableResize = disableResize;
	}

	private void fitImage() {
		if(this.image != null && this.image.isDisposed() == false) {
			x = 0;
			y = 0;
			Rectangle canvasSize = canvas.getClientArea();
			int width = image.getImageData().width;
			int height = image.getImageData().height;
			float wscale = 1f * canvasSize.width / width;
			float hscale = 1f * canvasSize.height / height;
			scale = Math.min(wscale, hscale);
			
			if(scale == wscale) {
				height = (int) (height * scale);
				y = (height - canvasSize.height ) / 2;
			}else {
				width = (int) (width * scale);
				x = (width - canvasSize.width) / 2;				
			}
		}
	}
	
	public void setImage(String imagePath) {
		Image image = SwtUtils.createImage(canvas, imagePath, actionContext);
		if(image != null) {
			setImage(image, true);
		}
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Composite parent = (Composite) actionContext.get("parent");

		Canvas canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED);

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
		
		ImageCanvas imageCanvas = new ImageCanvas(canvas, self, actionContext);
		
		Image image = self.doAction("getImage", actionContext);
		if(image != null) {
			imageCanvas.setImage(image);
		}else {
			String imagePath = self.doAction("getImagePath", actionContext);
			if(imagePath != null && !"".equals(imagePath)) {
				imageCanvas.setImage(imagePath);
			}
		}
		
		Designer.attach(canvas, self.getMetadata().getPath(), actionContext);
		actionContext.put(self.getMetadata().getName(), imageCanvas);
		return canvas;
	}
	
}
