package xworker.swt.events.xworker;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 向一个控件注册实现鼠标拖拽的事件，即鼠标左键按下开始拖拽，放开则停止拖拽。
 * 
 * @author Administrator
 *
 */
public class DragMove implements MouseListener, MouseMoveListener{
	private static Logger logger = LoggerFactory.getLogger(DragMove.class);
	
	int x;
	int y;
	int oldx;
	int oldy;
	int lastX;
	int lastY;
	int offsetX;
	int offsetY;
	
	boolean isDrag = false;
	/** 用于监听事件的事物 */
	Thing listener = null;
	/** 监听事物所在的动作上下文 */
	ActionContext actionContext = null;
	
	public int getOffsetX(){
		return offsetX;
	}
	
	public int getOffsetY(){
		return offsetY;
	}
	
	public DragMove(Thing listener, ActionContext actionContext){
		this.listener = listener;
		this.actionContext = actionContext;
	}
	
	@Override
	public void mouseMove(MouseEvent event) {
		if(isDrag){
			x = event.x;
			y = event.y;

			setLastXY((Control) event.widget, x, y, true);
			fireEvent("dragMoveMoved");
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {		
	}

	@Override
	public void mouseDown(MouseEvent event) {
		if(event.button == 1){
			isDrag = true;
			oldx = event.x;
			oldy = event.y;
			x = event.x;
			y = event.y;
			
			setLastXY((Control) event.widget, x, y, false);
			fireEvent("dragMoveStarted");
		}
	}

	private void setLastXY(Control control, int x, int y, boolean initOffset){
		Control parent = control.getParent();
		if(parent != null){
			
		}else{
			parent = control;
		}
		Point last = parent.toDisplay(x, y);
		if(initOffset){
			offsetX = last.x - lastX;
			offsetY = last.y - lastY;
		}
		lastX = last.x;
		lastY = last.y;
	}
	
	@Override
	public void mouseUp(MouseEvent event) {
		isDrag = false;
		x = event.x;
		y = event.y;
		setLastXY((Control) event.widget, x, y, true);
		
		fireEvent("dragMoveFinished");
	}
	
	public void fireEvent(String name){
		try{
			listener.doAction(name, actionContext, UtilMap.toMap(new Object[]{"dragMove", this}));
		}catch(Exception e){
			logger.error("DragMove fire event error, event=" + name + ", listener=" + listener.getMetadata().getPath());
		}
	}

	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Control control = (Control) actionContext.get("parent");
		DragMove dragMove = new DragMove(self, actionContext);
		control.addMouseListener(dragMove);
		control.addMouseMoveListener(dragMove);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getOldx() {
		return oldx;
	}

	public int getOldy() {
		return oldy;
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}
}
