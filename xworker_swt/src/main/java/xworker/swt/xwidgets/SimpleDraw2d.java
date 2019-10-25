package xworker.swt.xwidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.actions.KeyTravel;
import xworker.swt.util.ResizeUtil;

/**
 * 一个基于事物的简单的系统。
 * 
 * @author zyx
 *
 */
public class SimpleDraw2d implements Listener, PaintListener{
	private static final Logger logger = LoggerFactory.getLogger(SimpleDraw2d.class);
	
	public static final int DRAG_MOVE = 1;	
	public static final int DRAG_RESIZE_LEFT = 2;
	public static final int DRAG_RESIZE_RIGHT = 4;
	public static final int DRAG_RESIZE_TOP = 8;
	public static final int DRAG_RESIZE_BOTTOM = 16;
	public static final int DRAG_CREATE = 32;
	
	List<SimpleShape> shapes = new ArrayList<SimpleShape>();
	/*已被选中的Shape列表 */
	List<SimpleShape> selectedShapes = new ArrayList<SimpleShape>();
	/*鼠标点击一个点时，这个点下所有候选的Shape列表 */
	List<SimpleShape> shapesAtPoint = null;
	/** 候选列表的顺序 */
	int shapesAtPointIndex = 0;
	
	int dragStartX;
	int dragStartY;
	int width;
	int height;
	int canvasWidth; 
	int canvasHeight;
	boolean dragging = false;
	int dragType;	
	
	ScrolledComposite scrollComposite;
	Canvas imageCanvas;
	//背景图
	Image backgroundImage;
	Thing thing;
	ActionContext actionContext;
	
	public SimpleDraw2d(Composite parent, Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		//创建相应的SWT控件
		scrollComposite = new ScrolledComposite(parent, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		imageCanvas = new Canvas(scrollComposite, SWT.DOUBLE_BUFFERED);
		scrollComposite.setContent(imageCanvas);
		
		width = (Integer) thing.doAction("getWidth", actionContext);
		height = (Integer) thing.doAction("getHeight", actionContext);
		backgroundImage = (Image) thing.doAction("getImage", actionContext);
		if(backgroundImage != null && thing.getBoolean("useImageSize")){
			width = backgroundImage.getImageData().width;
			height = backgroundImage.getImageData().height;
		}
		
		Thing shapesThing = (Thing) thing.doAction("getShapes", actionContext); 
		setShapes(shapesThing);
		
		//加入监听释放资源
		scrollComposite.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				dispose();
			}			
		});
		
		//注册各种事件
		imageCanvas.addListener(SWT.MouseDown, this);
		imageCanvas.addListener(SWT.MouseMove, this);
		imageCanvas.addListener(SWT.MouseHover, this);		
		imageCanvas.addListener(SWT.MouseUp, this);
		imageCanvas.addListener(SWT.KeyDown, this);
		imageCanvas.addListener(SWT.KeyUp, this);
		imageCanvas.addListener(SWT.MouseWheel, this);
		imageCanvas.addPaintListener(this);
		
		//创建子节点，如布局等
		actionContext.peek().put("parent", scrollComposite);
		for(Thing child : thing.getChilds()){
			child.doAction("create", actionContext);
		}
		
		setCanvasSize(width, height);
		
		//保存变量
		actionContext.g().put(thing.getMetadata().getName(), this);
		
		update();
	}
	
	public void setShapes(Thing shapesThing) {
		if(shapesThing != null) {
			for(Thing sthing : shapesThing.getChilds()){
				try {
					SimpleShape shape = new SimpleShape(this, sthing, actionContext);
					shapes.add(shape);
				}catch(Exception e) {
					logger.warn("Init SimpleDraw2d shape exception, path=" + sthing.getMetadata().getPath(), e);
				}
			}
		}
	}
	
	public void save(){
		for(SimpleShape shape : shapes){
			shape.save();
		}
		
		thing.save();
	}
	
	public void update() {
		Collections.sort(shapes);
		
		imageCanvas.redraw();
	}
	
	public void dispose(){
		if(backgroundImage != null){
			backgroundImage.dispose();
		}
		
		for(SimpleShape shape : shapes){
			try {
				shape.dispose();
			}catch(Exception e) {				
			}
		}
	}
	
	public void draw(Canvas canvas, GC gc){
		//打开反锯齿
		int antialias = gc.getAntialias();
		if(antialias != SWT.ON) {
			gc.setAntialias(SWT.ON);
		}
		
		//先填充画面
		Color oldColor = gc.getBackground();
		gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(canvas.getClientArea());
		gc.setBackground(oldColor);
		
		if(backgroundImage != null){
			gc.drawImage(backgroundImage, 0, 0);
		}
		
		//绘画各种形状
		for(SimpleShape shape : shapes){			
			try {
				shape.draw(canvas, gc);
			}catch(Exception e) {
				logger.warn("Draw SimpleDraw2d shape exception, path=" + shape.getThing().getMetadata().getPath(), e);
			}
		}
	}
	
	public boolean selectShapeAt(int stateMask, int x, int y, ActionContext actionContext){
		List<SimpleShape> shapesAtPoint_ = new ArrayList<SimpleShape>();
		for(int i=shapes.size() - 1; i>=0; i--){
			SimpleShape shape = shapes.get(i);
			if(shape.isContains(x, y)){
				shapesAtPoint_.add(shape);
			}
		}
		if(shapesAtPoint != null && shapesAtPoint.equals(shapesAtPoint_)) {
			shapesAtPointIndex++;
			if(shapesAtPointIndex > shapesAtPoint.size() - 1) {
				shapesAtPointIndex = 0;
			}	
		}else {
			shapesAtPointIndex = 0;
			shapesAtPoint =  shapesAtPoint_;
		}
				
		if(haveStateMask(stateMask, SWT.SHIFT)) {
			//SHIF是按堆栈依次选择的功能
			for(int i=0; i<selectedShapes.size(); i++) {
				SimpleShape sshape = selectedShapes.get(i);
				if(shapesAtPoint.contains(sshape) && shapesAtPoint.indexOf(sshape) != shapesAtPointIndex) {
					sshape.setSelected(false);
					selectedShapes.remove(i);
					i--;
				}
			}
		}
		
		if(haveStateMask(stateMask, SWT.CTRL)) {
			if(shapesAtPoint.size() == 0) {
				//setNewSelected((SimpleShape) null);
			}else {				
				if(haveStateMask(stateMask, SWT.SHIFT)) {
					addNewSelected(shapesAtPoint.get(shapesAtPointIndex));
				}else {
					addNewSelected(shapesAtPoint);
				}
			}
		}else if(haveStateMask(stateMask, SWT.SHIFT)) {		
			if(shapesAtPoint.size() == 0) {
				setNewSelected((SimpleShape) null);
			}else {
				setNewSelected(shapesAtPoint.get(shapesAtPointIndex));
			}
		}else {
			if(shapesAtPoint.size() == 0) {
				setNewSelected((SimpleShape) null);
			}else {
				setNewSelected(shapesAtPoint);
			}
			
		}
	
		return selectedShapes.size() > 0;
	}
	
	private void addNewSelected(SimpleShape shape) {
		shape.setSelected(true);
		if(!selectedShapes.contains(shape)) {
			selectedShapes.add(shape);
		}
	}
	
    private void addNewSelected(List<SimpleShape> shapes) {
    	for(SimpleShape shape : shapes) {
    		shape.setSelected(true);
    		
    		if(!selectedShapes.contains(shape)) {
    			selectedShapes.add(shape);
    		}
    	}
	}
    
	private void setNewSelected(SimpleShape shape) {
		for(int i=0; i<selectedShapes.size(); i++) {
			SimpleShape sshape = selectedShapes.get(i);
			if(sshape == shape) {
				continue;
			}else {
				sshape.setSelected(false);
				selectedShapes.remove(i);
				i--;
			}
		}

		if(shape != null) {
			shape.setSelected(true);
			if(!selectedShapes.contains(shape)) {
				selectedShapes.add(shape);
			}
		}
	}
	
	private void setNewSelected(List<SimpleShape> shapes) {
		for(SimpleShape shape : shapes) {
			shape.setSelected(true);
			if(!selectedShapes.contains(shape)) {
				selectedShapes.add(shape);
			}
		}
		
		for(int i =0; i<selectedShapes.size(); i++) {
			SimpleShape sshape = selectedShapes.get(i);
			if(!shapes.contains(sshape)) {
				sshape.setSelected(false);
				selectedShapes.remove(i);
				i--;				
			}
		}
		
	}
	
	
	private boolean haveStateMask(int stateMask, int ... masks) {
		for(int mask : masks) {
			if((mask & stateMask) == mask) {
				return true;
			}
		}
		
		return false;
	}
	
	public void resize(int dragType, int widthx, int heightx, ActionContext actionContext){
		for(SimpleShape shape : selectedShapes){
			shape.resize(dragType, widthx, heightx, actionContext);
		}
		
		//重新计算画布的大小
		int maxWidth=0, maxHeight = 0;
		for(SimpleShape shape : shapes){
			int w = shape.getX() + shape.getWidth();
			if(maxWidth < w) {
				maxWidth = w;
			}
			int h = shape.getY() + shape.getHeight();
			if(maxHeight < h) {
				maxHeight = h;
			}
		}
		
		if(canvasWidth < maxWidth || (canvasWidth - maxWidth > 100) || canvasHeight < maxHeight || canvasHeight - maxHeight > 100) {
			setCanvasSize(maxWidth + 70, maxHeight + 50);
		}		
	}
	
	public void setCanvasSize(int canvasWidth, int canvasHeight) {
		if(canvasWidth > width || this.canvasWidth <= 0) {
			this.canvasWidth = canvasWidth;
		}
		if(canvasHeight > height || this.canvasHeight <= 0) {
			this.canvasHeight = canvasHeight;
		}
		
		if(canvasHeight > 0 && canvasWidth > 0){
			imageCanvas.setSize(this.canvasWidth, this.canvasHeight);
			scrollComposite.setMinSize(this.canvasWidth, this.canvasHeight);
			scrollComposite.layout();
		}
		
	}
	
	public String getCursorType(Display display, int x, int y){
		for(SimpleShape shape : shapes){
			if(shape.isContains(x, y)){
				String cursorType = shape.getCursorType(display, x, y);
				if(cursorType == null){
					cursorType = ResizeUtil.MOVE; 
				}
				return cursorType;
			}
		}
		
		return null;
	}
	
	public void handleCanvasEvent(Event event, ActionContext actionContext){
		switch(event.type){
		case SWT.MouseDown:
			if(event.button == 1){
				if(selectShapeAt(event.stateMask, event.x, event.y, actionContext)){
					dragging = true;
				}
			}
			
			if((dragType & DRAG_CREATE) == DRAG_CREATE){
				if(selectShapeAt(event.stateMask, event.x, event.y, actionContext) == false){
					Button deleteRegionButton = (Button) actionContext.get("deleteRegionButton");
					deleteRegionButton.setEnabled(false);
				}else{
					Button deleteRegionButton = (Button) actionContext.get("deleteRegionButton");
					deleteRegionButton.setEnabled(true);
				}
			}
			dragStartX = event.x;
			dragStartY = event.y;
			break;
		case SWT.MouseUp:
			dragging = false;
			break;
		case SWT.KeyDown:
			if(dragging){
				switch(event.keyCode){
				case KeyTravel.DOWN:
					resize(dragType, 0, 1, actionContext);
					dragStartY = dragStartY + 1;
					break;
				case KeyTravel.LEFT:
					resize(dragType, -1, 0, actionContext);
					dragStartX = dragStartX - 1;
					break;
				case KeyTravel.UP:
					resize(dragType, 0, -1, actionContext);
					dragStartY = dragStartY - 1;
					break;
				case KeyTravel.RIGHT:
					resize(dragType, 1, 0, actionContext);
					dragStartX = dragStartX + 1;
					break;
				}
			}
			break;
		case SWT.MouseMove:
			if(dragging){
				int width = event.x - dragStartX;
				int height = event.y - dragStartY;
								
				resize(dragType, width, height, actionContext);								
				dragStartX = event.x;
				dragStartY = event.y;
			}else{	
				
				//如果鼠标在当前选中的page上，那么
				String cursorType = getCursorType(event.display, event.x, event.y);
				Cursor cursor = ResizeUtil.getCursor(event.display, cursorType);
				if(cursor == null){
					//不再任何Shape之上
					cursor = event.display.getSystemCursor(SWT.CURSOR_ARROW);				
					
					dragType = SimpleDraw2d.DRAG_MOVE;
				}else{
					if(ResizeUtil.BOTTOM.equals(cursorType)){
						dragType = DRAG_RESIZE_BOTTOM;
					}
					
					if(ResizeUtil.BOTTOM_LEFT.equals(cursorType)){
						dragType = DRAG_RESIZE_BOTTOM | DRAG_RESIZE_LEFT;
					}
					
					if(ResizeUtil.BOTTOM_RIGHT.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_BOTTOM | SimpleDraw2d.DRAG_RESIZE_RIGHT;
					}
					
					if(ResizeUtil.LEFT.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_LEFT;
					}
					
					if(ResizeUtil.RIGHT.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_RIGHT;
					}
					
					if(ResizeUtil.TOP.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_TOP;
					}
					
					if(ResizeUtil.TOP_LEFT.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_TOP | SimpleDraw2d.DRAG_RESIZE_LEFT;
					}
					
					if(ResizeUtil.TOP_RIGHT.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_RESIZE_TOP | SimpleDraw2d.DRAG_RESIZE_RIGHT;
					}		
					
					if(ResizeUtil.MOVE.equals(cursorType)){
						dragType = SimpleDraw2d.DRAG_MOVE;
					}
					
				}
				((Control) event.widget).setCursor(cursor);				
			}
		}
		
		((Control) event.widget).redraw();
	}
	
	public static Object create(ActionContext actionContext){ 
		Thing self = actionContext.getObject("self");
		Composite parent = actionContext.getObject("parent");
		
		SimpleDraw2d s2d = new SimpleDraw2d(parent, self, actionContext);
		return s2d.scrollComposite;
	}

	@Override
	public void handleEvent(Event event) {
		this.handleCanvasEvent(event, actionContext);
	}

	@Override
	public void paintControl(PaintEvent event) {
		this.draw(imageCanvas, event.gc);
	}
	
	public void redraw() {
		imageCanvas.redraw();
	}
	
	/**
	 * 如果有多个选中的Shape，把它们移动到最左面的一个位置上。
	 */
	public void alignLeft() {
		if(selectedShapes.size() > 1) {
			int minX = Integer.MIN_VALUE;
			for(SimpleShape shape : selectedShapes) {
				if(shape.getX() < minX || minX == Integer.MIN_VALUE) {
					minX = shape.getX();
				}
			}
			
			for(SimpleShape shape : selectedShapes) {
				shape.setX(minX);
			}
			
			redraw();
		}
	}
	
	public void alignRight() {
		if(selectedShapes.size() > 1) {
			int maxX = Integer.MIN_VALUE;
			for(SimpleShape shape : selectedShapes) {
				if((shape.getX() + shape.getWidth()) > maxX || maxX == Integer.MIN_VALUE) {
					maxX = shape.getX() + shape.getWidth();
				}
			}
			
			for(SimpleShape shape : selectedShapes) {
				shape.setX(maxX - shape.getWidth());
			}
			
			redraw();
		}
	}
	
	public void alignTop() {
		if(selectedShapes.size() > 1) {
			int minY = Integer.MIN_VALUE;
			for(SimpleShape shape : selectedShapes) {
				if(shape.getY() < minY || minY == Integer.MIN_VALUE) {
					minY = shape.getY();
				}
			}
			
			for(SimpleShape shape : selectedShapes) {
				shape.setY(minY);
			}
			
			redraw();
		}
	}
	
	public void alignBottom() {
		if(selectedShapes.size() > 1) {
			int maxY = Integer.MIN_VALUE;
			for(SimpleShape shape : selectedShapes) {
				if((shape.getY() + shape.getHeight()) > maxY || maxY == Integer.MIN_VALUE) {
					maxY = shape.getY() + shape.getHeight();
				}
			}
			
			for(SimpleShape shape : selectedShapes) {
				shape.setY(maxY - shape.getHeight());
			}
			
			redraw();
		}
	}
	
	public void alignXCenter() {
		if(selectedShapes.size() > 1) {
			//算出平均中心点
			int ca = 0;
			for(SimpleShape shape : selectedShapes) {
				ca = ca + (shape.getX() + shape.getWidth()) / 2; 
			}
			ca = ca / selectedShapes.size();
			
			for(SimpleShape shape : selectedShapes) {
				shape.setX(2 * ca - shape.getWidth());
			}
			
			redraw();
		}
	}
	
	public void alignYCenter() {
		if(selectedShapes.size() > 1) {
			//算出平均中心点
			int ca = 0;
			for(SimpleShape shape : selectedShapes) {
				ca = ca + (shape.getY() + shape.getHeight()) / 2; 
			}
			ca = ca / selectedShapes.size();
			
			for(SimpleShape shape : selectedShapes) {
				shape.setY(2 * ca - shape.getHeight());
			}
			
			redraw();
		}
	}
	
	/**
	 * 一个挨着一个水平展开。
	 * 
	 * @param space
	 */
	public void attachX(int space) {
		if(selectedShapes.size() > 1) {
			Collections.sort(selectedShapes, new Comparator<SimpleShape>() {
				@Override
				public int compare(SimpleShape o1, SimpleShape o2) {
					return Integer.compare(o1.getX(), o2.getX());
				}				
			});
			
			for(int i=1; i<selectedShapes.size(); i++) {
				SimpleShape s1 = selectedShapes.get(i - 1);
				SimpleShape s2 = selectedShapes.get(i);
				s2.setX(s1.getX() + s1.getWidth() + space);
			}
			
			redraw();
		}
	}
	
	/**
	 * 一个挨着一个垂直展开。
	 * 
	 * @param space
	 */
	public void attachY(int space) {
		if(selectedShapes.size() > 1) {
			Collections.sort(selectedShapes, new Comparator<SimpleShape>() {
				@Override
				public int compare(SimpleShape o1, SimpleShape o2) {
					return Integer.compare(o1.getY(), o2.getY());
				}				
			});
			
			for(int i=1; i<selectedShapes.size(); i++) {
				SimpleShape s1 = selectedShapes.get(i - 1);
				SimpleShape s2 = selectedShapes.get(i);
				s2.setX(s1.getY() + s1.getHeight() + space);
			}
			
			redraw();
		}
	}
}
