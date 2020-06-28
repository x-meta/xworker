
/**
 *        (c) 2007-2010 IKOffice GmbH
 *
 *        http://www.ikoffice.de
 */
package xworker.swt.xwidgets;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;


/**
 * SplitButton
 * 
 * @author       junhuang   huangjun78@gmail.com
 * @version      1.0
 */
public class SplitButton extends Button {
    
	private final static int DEFAULT_SPACES = 21;
    private List<SplitButtonSelectionListener> listeners = new LinkedList<SplitButtonSelectionListener>();
    
    private int x1 = -1;
    private int y1 = -1;
    private int x2 = -1;
    private int y2 = -1;
    //private Menu menu;
    
    public SplitButton(Composite parent, int style) {
        super(parent, SWT.PUSH);
        setText("");
        if(SwtUtils.isRWT()) {
        	super.addListener(SWT.Resize, new Listener() {

				@Override
				public void handleEvent(Event e) {
                    //draw the split line and arrow
	            	Rectangle rect = getBounds();
	            	x1 = e.x + rect.width-20;
	            	y1 = e.y;
	            	x2 = e.x + rect.width;
	            	y2 = e.y + rect.height;            
				}
        		
        	});
        }else {
        	super.addPaintListener(new PaintListener() {
            
	            @Override
	            public void paintControl(PaintEvent e) {
	                // draw the split line and arrow
	            	
	            	Rectangle rect = getBounds();
	//               System.out.println("Rect width " + rect.width + " event width " + e.width);
	//               System.out.println("Rect height " + rect.height + " event height " + e.height);
	            	Color oldForeground = e.gc.getForeground();
	            	Color oldBackground = e.gc.getBackground();
	            	x1 = e.x + rect.width-20;
	            	y1 = e.y;
	            	x2 = e.x + rect.width;
	            	y2 = e.y + rect.height;
	//            	e.gc.setClipping(e.x, e.y, e.width, e.height);
	            	int dx = -e.gc.getClipping().x;
	                int dy = -e.gc.getClipping().y;
	               
	                Color COLOR_WIDGET_NORMAL_SHADOW = e.widget.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	                Color COLOR_WIDGET_HIGHLIGHT_SHADOW = e.widget.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	                Color COLOR__BLACK = e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	                
	            	e.gc.setForeground(COLOR_WIDGET_NORMAL_SHADOW);
	            	e.gc.setBackground(COLOR_WIDGET_NORMAL_SHADOW);
	            	e.gc.setLineWidth(1);
	            	 e.gc.drawLine(e.x + rect.width-20 + dx, e.y+6+dy, e.x + rect.width -20+ dx, e.y + rect.height-6+dy);
	               
	            	e.gc.setForeground(COLOR_WIDGET_HIGHLIGHT_SHADOW);
	            	e.gc.setBackground(COLOR_WIDGET_HIGHLIGHT_SHADOW);
	            	e.gc.setLineWidth(1);
	                e.gc.drawLine(e.x + rect.width-19 + dx, e.y+6+dy, e.x + rect.width -19+ dx, e.y + rect.height-6+dy);
	
	            	e.gc.setForeground(COLOR__BLACK);
	            	e.gc.setBackground(COLOR__BLACK);
	            	e.gc.fillPolygon(new int[] {
	                        e.x + rect.width-15+ dx, e.y + rect.height/2-1+dy, 
	                        e.x + rect.width-8+ dx, e.y + rect.height/2-1+dy, 
	                        e.x + rect.width-12+ dx, e.y + rect.height/2+3+dy}); 
	               
	            	e.gc.setForeground(oldForeground);
	            	e.gc.setBackground(oldBackground);
	                
	//                e.gc.drawImage(ARROW_DOWN, e.x + e.width-15, e.y + e.height/2-2);  
	            }
	        });
        }
        super.addListener(SWT.MouseDown, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                if (isShowMenu(event.x, event.y)) {
                    
                    for (SplitButtonSelectionListener listener : listeners) {
                        if (!listener.showMenu()) {
                        	event.doit = false;
                            return;
                        }
                    }
                    
                    event.widget.getDisplay().asyncExec(new Runnable() {
                    	public void run() {
                    		openMenu();
                    	}
                    });
                } else {
                	event.widget.notifyListeners(SWT.DefaultSelection, event);
                	
                    for (SplitButtonSelectionListener listener : listeners) {
                        listener.buttonSelected();
                    }
                }
            }
        });
        //menu = new Menu (getShell(), SWT.POP_UP);
    }
    
    public void openMenu() {
    	//触发MenuDetect事件                    
    	Event event = new Event();
    	event.widget = this;
        this.notifyListeners(SWT.MenuDetect, event);
        Menu menu = getMenu();
        if(menu == null || menu.isDisposed() || event.doit == false) {
        	event.doit = false;
        	return;
        }
        
        Button button = (Button) event.widget;
        Rectangle rect = button.getBounds();
        Point p = button.toDisplay(rect.x, rect.y + rect.height);
        menu.setLocation(p.x-rect.x, p.y-rect.y);
        menu.setVisible(true);
    }
    
    private boolean isShowMenu(int x, int y) {
        return x>=x1 && y>=y1 && x<=x2 && y<=y2;
    }
    
    public void addSplitButtonSelectionListener(SplitButtonSelectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public Menu getMenu() {
        return super.getMenu();
    }

    @Override
    public void setMenu(Menu menu) {    	
        super.setMenu(menu);
    }

    @Override
    protected void checkSubclass() {
     // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void setText(String string) {
        if (string != null) {
        	String EMPTY_SPACE = FontsUtils.getSpaceByWidth(DEFAULT_SPACES);
            super.setText(string + EMPTY_SPACE);
        } 
    }

    @Override
    public String getText() {
        return super.getText().trim();
    }
    
    public static Object create(ActionContext actionContext){
    	  Thing self = (Thing) actionContext.get("self");
  		
  		//println binding;
  		int style = SWT.NONE;
  		String selfType = self.getString("type");
  		if("SWT.ARROW".equals(selfType)){
  			style |= SWT.ARROW;
  		}else if("SWT.CHECK".equals(selfType)){
  			style |= SWT.CHECK;
  		}else if("SWT.PUSH".equals(selfType)){
  			style |= SWT.PUSH;
  		}else if("SWT.RADIO".equals(selfType)){
  			style |= SWT.RADIO;
  		}else if("SWT.TOGGLE".equals(selfType)){
  			style |= SWT.TOGGLE;
  		}
  		String selfStyle = self.getString("style");
  		if("UP".equals(selfStyle)){
  			style |= SWT.UP;
  		}else if("DOWN".equals(selfStyle)){
  			style |= SWT.DOWN;
  		}else if("LEFT".equals(selfStyle)){
  			style |= SWT.LEFT;
  		}else if("RIGHT".equals(selfStyle)){
  			style |= SWT.RIGHT;
  		}		
  		
  		if(self.getBoolean("border")){
  		    style |= SWT.BORDER;
  		}
  		
  		Composite parent = (Composite) actionContext.get("parent");
  		SplitButton button = new SplitButton(parent, style);
  		
  		//父类的初始化方法
  		Bindings bindings = actionContext.push(null);
  		bindings.put("control", button);
  		bindings.put("self", self);
  		try{
  		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
  		    initAction.run(actionContext);
  		}finally{
  		    actionContext.pop();
  		}
  		
  		//初始化本事物所定义的属性
  		button.setText(UtilString.getString(self.getString("text"), actionContext));
  		if(self.getBoolean("enabled") == false){
  		    button.setEnabled(false);
  		}
  		
  		Object image = actionContext.get(self.getString("image"));
  		if(image != null && image instanceof Image){
  		    button.setImage((Image) image);
  		}
  		
  		if(self.getString("selected") != null) button.setSelection(self.getBoolean("selected"));
  		
  		//保存变量和创建子事物
  		actionContext.getScope(0).put(self.getString("name"), button);
  		actionContext.peek().put("parent", button);
  		for(Thing child : self.getAllChilds()){
  		    child.doAction("create", actionContext);
  		}
  		actionContext.peek().remove("parent");
  		
  		Designer.attach(button, self.getMetadata().getPath(), actionContext);
  		return button;  
    }

}
