package xworker.ide.assistor.guide.mark;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

/**
 * 用于标记控件，一般用于介绍时控件时引人注意。
 * 
 * @author Administrator
 *
 */
public class ControlMarker {
	public static int ARROW_UP = 0;
	public static int ARROW_DOWN = 1;
	public static int ARROW_LEFT = 2;
	public static int ARROW_RIGHT = 3;
	public static int MIDDLE = 4;
	public static int LARGE = 5;
	
	private static Map<Control, Shell> markers = new HashMap<Control, Shell>();
	
	public static void mark(final Control control, final int type, final String align, final int offsetX, final int offsetY ){
		Shell shell = markers.get(control);
		if(shell != null && !shell.isDisposed()){
			return;
		}
		
		control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				ActionContext ac = new ActionContext();
				ac.put("parent", control.getShell());
				
				Thing shellThing = World.getInstance().getThing("xworker.ide.guide.marker.SwtControlMarker");
				Thing agif = new Thing("xworker.swt.Widgets/@AnimationGif");
				Shell shell = (Shell) shellThing.doAction("create", ac);
				
				int size = 0;
				if(type == ARROW_UP){
					agif.put("gifPath", "images/guide/mark/arrow_up.gif");
					shell.setSize(16, 16);
					size = 0;
				}else if(type == ARROW_DOWN){
					agif.put("gifPath", "images/guide/mark/arrow_down.gif");
					shell.setSize(16, 16);
					size = 0;
				}else if(type == ARROW_LEFT){
					agif.put("gifPath", "images/guide/mark/arrow_left.gif");
					shell.setSize(16, 16);
					size = 0;
				}else if(type == ARROW_RIGHT){
					agif.put("gifPath", "images/guide/mark/arrow_right.gif");
					shell.setSize(16, 16);
					size = 0;
				}else if(type == MIDDLE){
					agif.put("gifPath", "images/guide/mark/mark_middle.gif");
					shell.setSize(32, 32);
					size = 32;
				}else{
					agif.put("gifPath", "images/guide/mark/mark_large.gif");
					shell.setSize(60, 60);
					size = 60;
				}
				
				ac.peek().put("parent", ac.get("canvas"));
				agif.doAction("create", ac);
				
				Rectangle rect = control.getBounds();
				Point pt = null;
				int halfSize = size /  2;
				if("topleft".equals(align)){
					pt = new Point(rect.x, rect.y);
				}else if("top".equals(align)){
					pt = new Point(rect.x + rect.width / 2, rect.y);
				}else if("topright".equals(align)){
					pt = new Point(rect.x + rect.width, rect.y);
				}else if("left".equals(align)){
					pt = new Point(rect.x, rect.y+rect.height / 2);
				}else if("right".equals(align)){
					pt = new Point(rect.x + rect.width, rect.y+rect.height / 2);
				}else if("bottomleft".equals(align)){
					pt = new Point(rect.x, rect.y + rect.height);
				}else if("bottom".equals(align)){
					pt = new Point(rect.x + rect.width / 2, rect.y + rect.height);
				}else if("bottomright".equals(align)){
					pt = new Point(rect.x + rect.width, rect.y + rect.height);
				}else {
					pt = new Point(rect.x + rect.width / 2, rect.y+rect.height / 2);
				}
				pt.x = pt.x - halfSize + offsetX;
				pt.y = pt.y - halfSize + offsetY;
				if(control.getParent() != null){
					pt = control.getParent().toDisplay(pt);
				}
				shell.setLocation(pt);				
				shell.setVisible(true);
				markers.put(control, shell);
			}
		});
	}
	
	public static void unmark(Control control){
		Shell shell = markers.get(control);
		if(shell != null){
			shell.dispose();
			markers.remove(control);
		}
	}
	
	public static void markAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Control control = (Control) self.doAction("getControl", actionContext);
		if(control != null){
			ControlMarker.mark(control, self.getInt("markIconType"), self.getString("align"), self.getInt("offsetX"), self.getInt("offsetY"));
		}
	}
	
	public static Object getControlAction(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		return OgnlUtil.getValue(self, "control", actionContext);
	}
	
	public static void unmarkAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Control control = (Control) self.doAction("getControl", actionContext);
		if(control != null){
			ControlMarker.unmark(control);
		}
	}
}
