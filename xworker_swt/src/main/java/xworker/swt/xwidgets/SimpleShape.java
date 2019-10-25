package xworker.swt.xwidgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.ResizeUtil;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;

public class SimpleShape implements Comparable<SimpleShape>{
	public static final int MIN_WIDTH = 1;
	public static final int MIN_HEIGHT = 1;
	
	int x;
	int y;
	int width = MIN_WIDTH;
	int height = MIN_HEIGHT;
	boolean selected = false;
	boolean keepSelected = false;
	boolean showSelection = true;
	int zindex = 0;	
	SimpleDraw2d simpleDraw2d;
	
	
	Thing thing;
	ActionContext actionContext;
	/** 供用户存储数据的地方 */	
	Map<String, Object> data = new HashMap<String, Object>();
	
	//颜色和样式等
	Color foreground = null;
	Color background = null;
	LineAttributes lineAttributes = null;
	Font font = null;
	Image backgroundImage;
	
	public SimpleShape(SimpleDraw2d simpleDraw2d, Thing thing, ActionContext actionContext){
		this.simpleDraw2d = simpleDraw2d;
		this.thing = thing;
		this.actionContext = actionContext;
		
		init();
	}
	
	public Device getDevice() {
		return simpleDraw2d.imageCanvas.getDisplay();
	}
	
	public Canvas getCanvas() {
		return simpleDraw2d.imageCanvas;		
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public void init() {
		releaseResource();
		
		setX(thing.getInt("x"));
		setY(thing.getInt("y"));
		setWidth(thing.getInt("width"));
		setHeight(thing.getInt("height"));		
		setZindex(thing.getInt("zindex"));
		setSelected(thing.getBoolean("selected"));
		setShowSelection(thing.getBoolean("showSelection"));
		
		//前景色和背景色
		foreground = (Color) StyleSetStyleCreator.createResource(thing.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
		background = (Color) StyleSetStyleCreator.createResource(thing.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
		font = (Font) StyleSetStyleCreator.createResource(thing.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
		actionContext.peek().put("parent", simpleDraw2d.imageCanvas);
		backgroundImage = (Image) StyleSetStyleCreator.createResource(thing.getString("backgroundImage"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);		
		
		if(thing.getStringBlankAsNull("lineWidth") != null) {
			lineAttributes = new LineAttributes(thing.getInt("lineWidth"));
			
			if(thing.getStringBlankAsNull("cap") != null) {
				lineAttributes.cap = SwtUtils.getSWT(thing.getString("cap"));
			}
			if(thing.getStringBlankAsNull("join") != null) {
				lineAttributes.join = SwtUtils.getSWT(thing.getString("join"));
			}
			if(thing.getStringBlankAsNull("lineStyle") != null) {
				lineAttributes.style = SwtUtils.getSWT(thing.getString("lineStyle"));
			}
			if(thing.getStringBlankAsNull("dash") != null) {
				lineAttributes.dash = UtilData.getFloatArray(thing.getString("dash"));
			}
			if(thing.getStringBlankAsNull("dashOffset") != null) {
				lineAttributes.dashOffset = thing.getFloat("dashOffset"); 
			}
			if(thing.getStringBlankAsNull("miterLimit") != null) {
				lineAttributes.miterLimit = thing.getFloat("miterLimit"); 
			}
				
		}
		thing.doAction("init", actionContext, "shape", this);
	}
	
	private void releaseResource() {
		/*
		 * 通过StyleSetStyleCreator创建的资源，不需要主动释放，会自动释放的。
		if(this.font != null) {
			this.font.dispose();
		}
		
		if(this.foreground != null) {
			this.foreground.dispose();
		}
		
		if(this.background != null) {
			this.background.dispose();
		}
		*/
	}
	
	public void dispose(){
		releaseResource();
		
		thing.doAction("dispose", actionContext);
	}
	
	public void draw(Canvas canvas, GC gc){
		//调用事物的draw方法，用户可以通过此方法画自己的形状
		Color oldForeground = gc.getForeground();
		Color oldBackground = gc.getBackground();
		LineAttributes oldLineAttributes = gc.getLineAttributes();
		Font oldFont = gc.getFont();
		Transform oldTransform = new Transform(simpleDraw2d.imageCanvas.getDisplay());
		Transform  transform = new Transform(simpleDraw2d.imageCanvas.getDisplay());
		
		try {
			if(foreground != null) {
				gc.setForeground(foreground);
			}
			if(background != null) {
				gc.setBackground(background);
			}
			if(lineAttributes != null) {
				gc.setLineAttributes(lineAttributes);
			}
			if(font != null) {
				gc.setFont(font);
			}
			
			//总是使用相对坐标
			gc.getTransform(oldTransform);					
			transform.translate(getX(), getY());
			float rotate = 0;
			try {
				rotate = thing.getFloat("rotate");
			}catch(Exception e) {
				
			}
			
			
			if(rotate != 0) {
				//如果旋转存在，那么围绕中心旋转
				int hw = getWidth() / 2;
				int hh = getHeight() / 2;
				transform.translate(hw, hh);;
				transform.rotate(rotate);
				transform.translate(-hw, -hh);								
			}
			transform.multiply(oldTransform);
			gc.setTransform(transform);			
			
			if(backgroundImage != null) {
				gc.drawImage(backgroundImage, 0, 0, backgroundImage.getImageData().width, backgroundImage.getImageData().height,
							0, 0, getWidth(), getHeight());
			}
			
			
			thing.doAction("draw", actionContext, "canvas", canvas, "gc", gc, "shape", this);
			
			
		}finally {
			gc.setForeground(oldForeground);
			gc.setBackground(oldBackground);
			gc.setLineAttributes(oldLineAttributes);
			gc.setFont(oldFont);
			gc.setTransform(oldTransform);
			oldTransform.dispose();
			transform.dispose();
		}
		
		//transform.translate(-getX(), -getY());
		//gc.setTransform(transform);
		if(this.isSelected() && showSelection){
			int alpha = gc.getAlpha();
			try{
				int linesTyle = gc.getLineStyle();
				gc.setLineStyle(SWT.LINE_DOT);
				gc.drawRectangle(x, y, width, height);
				gc.setLineStyle(linesTyle);
				
				gc.setAlpha(128);
				gc.fillRectangle(x, y, width, height);
				
				//画缩放
				ResizeUtil.draw(gc, x, y, width, height);
			}finally{
				gc.setAlpha(alpha);
			}
			
			gc.drawString(thing.getMetadata().getLabel(), x + width, y + height);
		}
		
	}
	
	public void save(){
		thing.put("x", x);
		thing.put("y", y);
		thing.put("width", width);
		thing.put("height", height);
		
		thing.doAction("save", actionContext, "shape", this);
	}
	
	public boolean isContains(int xx, int yy){
		if(xx >= x && xx <= x + width && yy >= y && yy <= y + height){
			return true;
		}		
		
		return false;
	}
	
	public void resize(int dragType, int widthx, int heightx, ActionContext actionContext){
		if((dragType & SimpleDraw2d.DRAG_MOVE) == SimpleDraw2d.DRAG_MOVE){
			x = x + widthx;
			y = y + heightx;
		}
		if((dragType & SimpleDraw2d.DRAG_RESIZE_LEFT) == SimpleDraw2d.DRAG_RESIZE_LEFT){
			if(x + widthx < 0){
				widthx = -x;
			}
			if(width - widthx < MIN_WIDTH){
				widthx = width - MIN_WIDTH;
			}
			x = x + widthx;
			width = width - widthx;				
		}
		
		if((dragType & SimpleDraw2d.DRAG_RESIZE_RIGHT) == SimpleDraw2d.DRAG_RESIZE_RIGHT){
			width = width + widthx;
			if(width < MIN_WIDTH){
				width = MIN_WIDTH;
			}
			
			//if(x + width > page.maxWidth){
			//	width = page.maxWidth - x;
			//}
		}
		
		if((dragType & SimpleDraw2d.DRAG_RESIZE_TOP) == SimpleDraw2d.DRAG_RESIZE_TOP){
			if(y + heightx < 0){
				heightx = -y;
			}
			if(height - heightx < MIN_HEIGHT){
				heightx = height - MIN_HEIGHT;
			}
			y = y + heightx;
			height = height - heightx;		
		}
		if((dragType & SimpleDraw2d.DRAG_RESIZE_BOTTOM) == SimpleDraw2d.DRAG_RESIZE_BOTTOM){
			height = height + heightx;
			if(height < MIN_HEIGHT){
				height = MIN_HEIGHT;
			}			
			
			//if(y + height > page.maxHeight){
			//	height = page.maxHeight - y;
			//}
		}
			
	}
	
	public String getCursorType(Display display, int x1, int y1){
		 return ResizeUtil.getCursorType(display, x1, y1, x, y, width, height);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		if(this.width < MIN_WIDTH){
			this.width = MIN_WIDTH;
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		if(this.height < MIN_HEIGHT){
			this.height = MIN_HEIGHT;
		}
	}

	public boolean isSelected() {
		return keepSelected || selected;
	}

	public void setSelected(boolean selected) {
		if(this.selected != selected) {
			//触发选择事件
			if(selected) {
				thing.doAction("selection", actionContext, "shape", this);
			}else {
				thing.doAction("deSelection", actionContext, "shape", this);
			}
		}
		this.selected = selected;
	}

	public int getZindex() {
		return zindex;
	}

	public void setZindex(int zindex) {
		this.zindex = zindex;
	}
	
	public void setData(String key, Object value){
		data.put(key, value);
	}
	
	public Object getData(String key){
		return data.get(key);
	}

	public boolean isKeepSelected() {
		return keepSelected;
	}

	public void setKeepSelected(boolean keepSelected) {
		this.keepSelected = keepSelected;
	}

	public boolean isShowSelection() {
		return showSelection;
	}

	public void setShowSelection(boolean showSelection) {
		this.showSelection = showSelection;
	}

	@Override
	public int compareTo(SimpleShape o) {
		if(zindex < o.getZindex()) {
			return -1;
		}else if(zindex == o.getZindex()) {
			return 0;
		}else {
			return 1;
		}
	}
	
	
}
