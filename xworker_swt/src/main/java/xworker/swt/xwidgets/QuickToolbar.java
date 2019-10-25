package xworker.swt.xwidgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;
import xworker.swt.util.QuickWidgetUtils;
import xworker.swt.util.ResourceManager;

public class QuickToolbar {
	private static Logger logger = LoggerFactory.getLogger(QuickToolbar.class);
	private static SelectionListener listener = new SelectionListener(){

		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			if(event.stateMask == SWT.CTRL){
				System.out.println("xxxx");
			}
			Thing thing = (Thing) event.widget.getData();			
			ActionContext actionContext = (ActionContext) event.widget.getData("actionContext");
			
			if(event.stateMask == SWT.CTRL && Designer.isEnabled()){
				//编辑Item
				Thing editor = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.QuickItemEditor");
				ActionContext ac = new ActionContext();
				ac.put("parent", event.widget.getDisplay().getActiveShell());
				ac.put("parentContext", actionContext);
				Shell shell = editor.doAction("create", ac);
				ActionContainer thingEditor = ac.getObject("thingEditor");
				thingEditor.doAction("setThing", actionContext, "thing", thing);
				shell.setText(thing.getMetadata().getLabel());
				shell.open();
				return;
			}
			
			try{
				if("DROP_DOWN".equals(thing.getString("type"))){
					if (event.detail == SWT.ARROW) {
						Menu menu = (Menu) event.widget.getData("menu");
						if(menu != null){
							ToolItem item = (ToolItem) event.widget;
	
							Rectangle rect = item.getBounds();
							Point pt = new Point(rect.x, rect.y + rect.height);
							pt = item.getParent().toDisplay(pt);
	
						    menu.setLocation(pt.x, pt.y);
						    //menu.update();
						    menu.setVisible(true);
						    return;
						}
					}
				}
				
				QuickWidgetUtils.invokeEvent(event, thing, "run", actionContext);
				/*
				String actionContainer = thing.getStringBlankAsNull("actionContainer");
				String actionName = thing.getStringBlankAsNull("actionName");
				if(actionName != null && actionContainer != null){
					Object ac = OgnlUtil.getValue(actionContainer, actionContext);					
					if(ac != null){
						if(ac instanceof Thing){
							((Thing) ac).doAction(actionName, actionContext, "event", event, "itemThing", thing);
						}else if(ac instanceof ActionContainer){
							((ActionContainer) ac).doAction(actionName, actionContext, "event", event, "itemThing", thing);
						}
					}
				}else{
					thing.doAction("run", actionContext, "event", event, "itemThing", thing);
				}*/
				
				
			}catch(Exception e){
				logger.warn("QuickMenu selection error， path=" + thing.getMetadata().getPath(), e);
			}
		}		
	};
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SWT.NONE;
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}
		
		if(self.getBoolean("FLAT"))
		    style |= SWT.FLAT;
		if(self.getBoolean("WRAP"))
		    style |= SWT.WRAP;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("SHADOW_OUT"))
		    style |= SWT.SHADOW_OUT;
		if(self.getBoolean("RIGHT"))
		    style |= SWT.RIGHT;
		    
		Composite parent = (Composite) actionContext.get("parent");
		ToolBar bar = new ToolBar (parent, style);
		try{
			Designer.pushCreator(self);
			//保存变量和创建子事物
			actionContext.getScope(0).put(self.getString("name"), bar);
			actionContext.peek().put("parent", bar);
			for(Thing child : self.getChilds()){
			    child.doAction("create", actionContext);
			}
			//bar.pack();
			
			actionContext.peek().remove("parent");
		}finally{
			Designer.popCreator();
		}
		
		Designer.attach(bar, self.getMetadata().getPath(), actionContext);
		Designer.attachCreator(bar, self.getMetadata().getPath(), actionContext);
		
		return bar;        
	}
	
	public static void createItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
        int style = SWT.NONE;
        String selfType = self.getString("type");
        if("PUSH".equals(selfType)){
        	style |= SWT.PUSH;
        }else if("DROP_DOWN".equals(selfType)){
        	style |= SWT.DROP_DOWN;
        }else if("RADIO".equals(selfType)){
        	style |= SWT.RADIO;
        }else if("CHECK".equals(selfType)){
        	style |= SWT.CHECK;
        }else if("SEPARATOR".equals(selfType)){
        	style |= SWT.SEPARATOR;
        }
        
        Integer index = ItemIndex.getRemove();
        ToolItem item = null;
        if(index != null) {
        	item = new ToolItem ((ToolBar) actionContext.get("parent"), style, index);
        }else {
        	item = new ToolItem ((ToolBar) actionContext.get("parent"), style);
        }
        item.setData(self);
        item.setData("actionContext", actionContext);
        item.setData(Designer.DATA_THING, self.getMetadata().getPath());
        item.addSelectionListener(listener);
        String icon = self.getStringBlankAsNull("image");
		if(icon != null){
			Image image = (Image) ResourceManager.createIamge(icon, actionContext);
			if(image != null){
				item.setImage(image);
			}
		}
		if(self.getBoolean("showText")){
			item.setText(self.getMetadata().getLabel());
		}
		if(self.getStringBlankAsNull("toolTiptext") != null){
			item.setToolTipText(UtilString.getString(self, "toolTiptext", actionContext));
		}
		String width = self.getString("width");
        if(width != null && !"".equals(width)){
            item.setWidth(self.getInt(self.getString("width"), 0));
        }    
        if(self.getBoolean("enabled") == false){
            item.setEnabled(false);
        }
        
        if("DROP_DOWN".equals(selfType)){
        	//创建菜单节点
        	List<Thing> menus = self.getChilds("Menu");
        	
        	if(menus.size() > 0){
	        	//创建Menu
	        	Menu menu = new Menu(item.getParent());
	        	item.setData("menu", menu);
	        	item.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent e) {
						Menu menu = (Menu) e.widget.getData("menu");
						if(menu != null) {
							menu.dispose();
						}
					}
	        		
	        	});
	        	actionContext.peek().put("menu", menu);
	        	
	        	for(Thing child : menus){
	        		child.doAction("create", actionContext);
	        	}
        	}
        }else if("SEPARATOR".equals(selfType)){
        	//创建控件
        	Thing control = self.getThing("Control@0");
        	if(control != null){
        		Object obj = control.doAction("create", actionContext);
        		if(obj instanceof Control){
        			Control c = (Control) obj;
        			c.pack();
                    item.setWidth(c.getSize ().x);
                    item.setControl(c);
        		}
        	}
        }
        
        actionContext.getScope(0).put(self.getMetadata().getName(), item);
	}
	
	public static Object createControl(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		if(self.getChilds().size() > 0){
		    return self.getChilds().get(0).doAction("create", actionContext);
		}

		return null;
	}
}
