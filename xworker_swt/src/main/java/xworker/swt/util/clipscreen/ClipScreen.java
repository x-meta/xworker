package xworker.swt.util.clipscreen;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 剪裁屏幕的对话框。
 * 
 * @author Administrator
 *
 */
public class ClipScreen implements MouseMoveListener,MouseListener,PaintListener{
	public static final int DRAG_LEFT = 1;
	public static final int DRAG_TOP = 2;
	public static final int DRAG_RIGHT = 4;
	public static final int DRAG_BOTTOM = 8;	
	public static final int DRAG_ALL = 16;
	
	/** 是否是自己启动的Display，如果是需要dispose再最后 */
	boolean closeDisplayOnEnd = false;
	Display display;
	Shell shell;
	Canvas canvas;
	Image screenImage;
	int screenWidth;
	int screenHeight;
	
	/** 裁剪的区域 */
	int startX,startY,endX,endY=0;
	
	/** 是否正在拖动 */
	boolean draging = false;
	/** 拖动类型 */
	int dragType = 0;
	int lastX;
	int lastY;
	boolean cliped = false;
	ClipSelector selector = new ClipSelector();
	
	private ClipScreen(Display display, Image screenImage, boolean closeDisplayOnEnd){
		this.closeDisplayOnEnd = closeDisplayOnEnd;
		this.screenImage = screenImage;
		this.display = display;
		
		Rectangle screenRec = display.getBounds();
		screenWidth = screenRec.width;
		screenHeight = screenRec.height;		
	}
	
	/**
	 * 打开截图对话框。
	 * 
	 * @return
	 */
	public ImageData open(){
		shell = new Shell(display, SWT.NO_TRIM);
		shell.setSize(screenWidth, screenHeight);
		shell.setLocation(0, 0);
		shell.setLayout(new FillLayout());
		
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addPaintListener(this);
		
		shell.open();
		
		 while (!shell.isDisposed()) {
	     if (!display.readAndDispatch())
	         display.sleep();
	     }
		 
		 ImageData imageData = null;
		 if(cliped){
			 int width = endX - startX;
			 int height = endY - startY;
			 if(width > 0 && height > 0){
				 Image newImage = new Image(display, width, height);
				 GC gc = new GC(newImage);
				 gc.drawImage(screenImage, startX, startY, width, height, 0, 0, width, height);
				 gc.dispose();
				 imageData = newImage.getImageData();
				 newImage.dispose();
			 }
		 }
		 screenImage.dispose();
		 
		 if(closeDisplayOnEnd){
			 display.dispose();		
		 }
		 
		 return imageData;
	}
	
	public static ClipScreen create(){
		//获取display
		Display display = Display.getCurrent();
		boolean closeDisplayOnEnd = false;
		if(display == null){
			display = new Display();
			closeDisplayOnEnd = true;
		}
		
		//截取屏幕
		Rectangle screenREc = display.getBounds();
		Image screenImage = new Image(display, screenREc.width, screenREc.height);
		GC gc = new GC(display);
		gc.copyArea(screenImage, 0, 0);
		gc.dispose();
		
		ClipScreen dialog = new ClipScreen(display, screenImage, closeDisplayOnEnd);
		return dialog;
	}


	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		//画截图
		gc.drawImage(screenImage, 0, 0);
		
		//画蒙版，即灰色
		gc.setAlpha(128);
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.fillRectangle(0, 0, screenWidth, startY);
		gc.fillRectangle(0, startY, startX, screenHeight);
		gc.fillRectangle(startX, endY, screenWidth, screenHeight);
		gc.fillRectangle(endX, startY, screenWidth, endY - startY);
		gc.setAlpha(255);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
     
        //画选择框
        selector.paint(gc);
        
        //画提示
    	gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
        if(selector.isSelected()){
        	gc.drawText("在选择的区域里双鼠标左键完成截屏", endX, startY);
        }else{
        	gc.drawText("按住鼠标左键选择要截屏的区域", screenWidth / 2,  screenHeight / 2);
        }
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if(selector.isSelected()){
			cliped = true;
			shell.dispose();
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if(e.button == 1 && e.count == 1){
			lastX = e.x;
			lastY = e.y;
			
			//拖动开始
			if(selector.contains(e.x, e.y)){
				draging = true;
				dragType = selector.getDragType(e.x, e.y);				
			}else{
				dragType = ClipScreen.DRAG_BOTTOM | ClipScreen.DRAG_RIGHT;
				draging = true;
				
				startX = e.x;
				startY = e.y;
			}
		}else if(e.button == 3){
			if(selector.isSelected()){
				selector.unselected();
				startX = startY = endX = endY = 0;
				canvas.redraw();
			}else{
				shell.dispose();
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		draging = false;
		dragType = 0;
	}

	private boolean isDragType(int type){
		return (dragType & type) == type;
	}
	
	@Override
	public void mouseMove(MouseEvent e) {
		if(draging){
			//System.out.println("draging, type=" + dragType + ",startX=" + startX + "," + startY + "," + endX + "," + endY);
			if(isDragType(ClipScreen.DRAG_BOTTOM)){
				setEndY(e.y);
			}else if(isDragType(ClipScreen.DRAG_TOP)){
				setStartY(e.y);
			}			
			
			if(isDragType(ClipScreen.DRAG_LEFT)){
				setStartX(e.x);
			}else if(isDragType(ClipScreen.DRAG_RIGHT)){
				setEndX(e.x);
			}
			
			if(isDragType(ClipScreen.DRAG_ALL)){
				int xm = e.x - lastX;
				int ym = e.y - lastY;
				if(xm < 0 && xm + startX < 0){
					xm = -startX;
				}else if(xm > 0 && endX + xm > screenWidth){
					xm = screenWidth - endX;
				}
				if(ym < 0 && ym + startY < 0){
					ym = -startY;
				}else if(ym > 0 && endY + ym > screenHeight){
					ym = screenHeight - endY;
				}
				startX = startX + xm;
				endX = endX + xm;
				startY = startY + ym;
				endY = endY + ym;
				
				lastX = e.x;
				lastY = e.y;
			}
			
			selector.init(startX, startY, endX, endY);			
			canvas.redraw();
		}
		
		if(!draging){
			Cursor cursor = selector.getCursor(display, e.x, e.y);
			if(cursor != null){
				canvas.setCursor(cursor);
			}else{
				canvas.setCursor(display.getSystemCursor(SWT.DEFAULT));
			}
		}
	}

	private void changeDragType(int type1, int type2){
		dragType = dragType ^ type1 | type2;
	}
	
	private void setStartX(int sx){
		startX = sx;
		if(endX < startX){
			startX = endX;
			endX = sx;
			
			if(isDragType(ClipScreen.DRAG_LEFT)){
				changeDragType(ClipScreen.DRAG_LEFT, ClipScreen.DRAG_RIGHT);
			}else if(isDragType(ClipScreen.DRAG_RIGHT)){
				changeDragType(ClipScreen.DRAG_RIGHT, ClipScreen.DRAG_LEFT);
			}
		}
	}
	
	private void setEndX(int ex){
		endX = ex;
		if(endX < startX){
			endX = startX;
			startX = ex;
			
			if(isDragType(ClipScreen.DRAG_LEFT)){
				changeDragType(ClipScreen.DRAG_LEFT, ClipScreen.DRAG_RIGHT);
			}else if(isDragType(ClipScreen.DRAG_RIGHT)){
				changeDragType(ClipScreen.DRAG_RIGHT, ClipScreen.DRAG_LEFT);
			}
		}
	}
	
	private void setStartY(int sy){
		startY = sy;
		if(endY < startY){
			startY = endY;
			endY = sy;
			
			if((dragType & ClipScreen.DRAG_TOP) == ClipScreen.DRAG_TOP){
				dragType = dragType ^ ClipScreen.DRAG_TOP | ClipScreen.DRAG_BOTTOM;
			}else if((dragType & ClipScreen.DRAG_BOTTOM) == ClipScreen.DRAG_BOTTOM){
				dragType = dragType ^ ClipScreen.DRAG_BOTTOM | ClipScreen.DRAG_TOP;
			}
		}
	}
	
	private void setEndY(int ey){
		endY = ey;
		if(endY < startY){
			endY = startY;
			startY = ey;
			
			if((dragType & ClipScreen.DRAG_TOP) == ClipScreen.DRAG_TOP){
				dragType = dragType ^ ClipScreen.DRAG_TOP | ClipScreen.DRAG_BOTTOM;
			}else if((dragType & ClipScreen.DRAG_BOTTOM) == ClipScreen.DRAG_BOTTOM){
				dragType = dragType ^ ClipScreen.DRAG_BOTTOM | ClipScreen.DRAG_TOP;
			}
		}
	}
	
	public static ImageData run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String type = (String) self.doAction("getType", actionContext);
		if("manual".equals(type)){
			return ClipScreen.create().open();
		}else{
			//获取display
			Display display = Display.getCurrent();
			boolean closeDisplayOnEnd = false;
			if(display == null){
				display = new Display();
				closeDisplayOnEnd = true;
			}
			
			//截取屏幕
			Rectangle screenREc = display.getBounds();
			Image screenImage = null;
			int x = (Integer) self.doAction("getX", actionContext);
			int y = (Integer) self.doAction("getY", actionContext);
			int width = (Integer) self.doAction("getWidth", actionContext);
			int height = (Integer) self.doAction("getHeight", actionContext);
			
			GC gc = new GC(display);
			if(x != -1 && y != -1 && width != -1 && height != -1){
				screenImage = new Image(display, width, height);
				gc.copyArea(screenImage, x, y);
			}else{
				screenImage = new Image(display, screenREc.width, screenREc.height);

				gc.copyArea(screenImage, 0, 0);
			}
			
			gc.dispose();
			
			if(closeDisplayOnEnd){
				display.dispose();
			}
			
			ImageData data = screenImage.getImageData();
			screenImage.dispose();
			return data;
		}
	}
	
	public static void main(String args[]){
		try{
			ImageData data = ClipScreen.create().open();
			if(data != null){
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[1];
				loader.data[0] = data;
				loader.save("e:/temp/screen.jpg", SWT.IMAGE_JPEG);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
