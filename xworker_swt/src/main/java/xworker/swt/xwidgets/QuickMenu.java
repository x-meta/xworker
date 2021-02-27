package xworker.swt.xwidgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;
import xworker.swt.util.QuickWidgetUtils;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;

public class QuickMenu {
	//private static Logger logger = LoggerFactory.getLogger(QuickMenu.class);
	private static final String TAG = QuickMenu.class.getName();
	private static SelectionListener listener = new SelectionListener(){

		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			Thing thing = (Thing) event.widget.getData();
			Thing menuThing = (Thing) event.widget.getData("menuThing");
			ActionContext actionContext = (ActionContext) event.widget.getData("actionContext");
			try {
				//System.out.println("Quick menu selected");
				boolean debug = menuThing != null && menuThing.getBoolean("debug");
				if(debug && event.stateMask == SWT.CTRL && Designer.isEnabled()){
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
				
				QuickWidgetUtils.invokeEvent(event, thing, "run", actionContext);
				/*
				try{
					String actionContainer = thing.getStringBlankAsNull("actionContainer");
					String actionName = thing.getStringBlankAsNull("actionName");
					if(actionName != null && actionContainer != null){
						ActionContainer ac = (ActionContainer) actionContext.get(actionContainer);
						if(ac != null){
							ac.doAction(actionName, actionContext, "event", event, "menuThing", thing);
						}
					}else{
						Thing acThing = thing.doAction("getActionThing", actionContext);
						if(acThing != null) {
							acThing.doAction("run", actionContext, "event", event, "menuThing", thing);
						}else {
							thing.doAction("run", actionContext, "event", event);
						}
					}
				}catch(Exception e){
					logger.warn("QuickMenu selection error， path=" + thing.getMetadata().getPath(), e);
				}*/
			}catch(Exception e){
				Executor.error(TAG, "Menu selection error, path=" + thing.getMetadata().getPath(), e);
			}
		}		
	};

	public static Menu createQuickMenu(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Menu menu = null;
		Object parent = actionContext.get("parent");
		if(parent instanceof Shell){
			Shell shell = (Shell) parent;
			menu = new Menu(shell, SWT.BAR);
			menu.setData(Designer.DATA_THING, self.getMetadata().getPath());
			shell.setMenuBar(menu);
		}else if(parent instanceof Menu) {
			menu = (Menu) parent;
		}else if(parent instanceof MenuItem) {
			menu = new Menu((MenuItem) parent);
			menu.setData(Designer.DATA_THING, self.getMetadata().getPath());
		}else if(parent instanceof ToolItem) {
			menu = new Menu(((ToolItem) parent).getParent());
			menu.setData(Designer.DATA_THING, self.getMetadata().getPath());
		}else{
			menu = new Menu((Control) parent);		
			menu.setData(Designer.DATA_THING, self.getMetadata().getPath());
			((Control) parent).setMenu(menu);
		}
		
		actionContext.peek().put("menu", menu);
		actionContext.peek().put("menuThing", self);
		for(Thing child : self.getChilds("Menu")){
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), menu);
		return menu;
	}
	
	public static void createMenuItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Menu menu = (Menu) actionContext.get("menu");
		if(menu == null) {
			menu = actionContext.getObject("parent");
		}
		
		MenuItem item = null;
		Integer index = ItemIndex.getRemove(); //调用设置的索引，如果存在
		int style = SWT.PUSH;
		List<Thing> childs = self.getChilds("Menu");
		
		if(childs.size() > 0){
			style = SWT.CASCADE;
		}else if(self.getBoolean("seperator")){
			style =  SWT.SEPARATOR;
		}else{
			style = SWT.PUSH;
		}
		if(index != null) {
			item = new MenuItem(menu, style, index);
		}else {
			item = new MenuItem(menu, style);
		}
		
		item.setText(self.getMetadata().getLabel());
		item.setData(self);
		item.setData("actionContext", actionContext);
		item.setData("menuThing", actionContext.get("menuThing"));
		item.setData(Designer.DATA_THING, self.getMetadata().getPath());
		item.addSelectionListener(listener);
		String icon = self.getStringBlankAsNull("icon");
		if(icon != null){
			Image image = (Image) ResourceManager.createIamge(icon, actionContext);
			if(image != null){
				item.setImage(image);
			}
		}
		String accelerator = self.getStringBlankAsNull("accelerator");
		if(accelerator != null){
			item.setAccelerator(SwtUtils.getAccelerator(accelerator));
		}
		actionContext.g().put(self.getMetadata().getName(), item);
		
		if(childs.size() > 0){
			Menu childMenu = new Menu(item);
			item.setMenu(childMenu);
			actionContext.peek().put("menu", childMenu);
			
			for(Thing child : childs){
				child.doAction("create", actionContext);
			}
		}
	}
}
