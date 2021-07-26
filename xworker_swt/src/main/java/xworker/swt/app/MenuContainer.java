package xworker.swt.app;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

import org.xmeta.util.ThingLoader;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;

@ActionClass(creator="getMenuContainer")
public class MenuContainer {
	private static final String TAG = MenuContainer.class.getName();
	private static final String ID = "__MenuContainer_id__";
	
	Thing self;
	Menu menu;
	
	public MenuContainer(Menu menu, ActionContext actionContext) {
		this.self = actionContext.getObject("self");
		
		//MenuContainer是Menu的子节点
		this.menu = menu;
		
		actionContext.put(ID, this);
	}
	
	/**
	 * 设置应用的菜单
	 * 
	 * @param menuConfig
	 * @param actionContext
	 */
	@ActionParams(names="menuConfig")
	public void setEditorMenu(Thing menuConfig, ActionContext actionContext) {
		boolean hasRoot = menuConfig.doAction("hasRoot", actionContext);
		Thing menuThing = menuConfig.doAction("getMenu", actionContext);
		if(menuThing == null) {
			return;
		}

		//编辑器以及编辑器绑定的对象
		Thing editorThing = actionContext.getObject("editorThing");
		Object objectForThingLoader = null;
		if(editorThing != null){
			objectForThingLoader = editorThing.doAction("getObjectForThingLoader", actionContext);
		}

		String refType = menuConfig.doAction("getRefType", actionContext);
		String refMenuPath = menuConfig.doAction("getRefMenuPath", actionContext);
		MenuItem refItem = getReferenceMenuItem(menu, refMenuPath);
		Menu parent = menu;		
		int index = parent.getItemCount();
		if(refItem != null) {
			parent = refItem.getParent();			
			index = parent.indexOf(refItem);
			if("above".equals(refType)) {				
			}else {
				index = index + 1;
			}
		}

		Bindings bindings = actionContext.push();
		try {
			bindings.put("parent", parent);
			bindings.put("menu", parent);

			if (hasRoot) {
				ItemIndex.set(index);
				if(objectForThingLoader == null) {
					menuThing.doAction("create", actionContext);
				}else{
					ThingLoader.load(objectForThingLoader, menuThing, actionContext);
				}
			} else {
				for (Thing itemThing : menuThing.getChilds()) {
					ItemIndex.set(index);
					index++;
					if(objectForThingLoader == null) {
						itemThing.doAction("create", actionContext);
					}else{
						ThingLoader.load(objectForThingLoader, itemThing, actionContext);
					}
				}
			}
		}finally {
			actionContext.pop();
		}
	}
	
	@ActionParams(names="menuConfig")
	public void removeEditorMenu(Thing menuConfig, ActionContext actionContext) {
		boolean hasRoot = menuConfig.doAction("hasRoot", actionContext);
		Thing menuThing = menuConfig.doAction("getMenu", actionContext);
		if(menuThing == null) {
			return;
		}
		
		if(hasRoot) {
			removeMenuItem(menu, menuThing.getMetadata().getPath());
		}else {
			for(Thing itemThing : menuThing.getChilds()) {
				removeMenuItem(menu, itemThing.getMetadata().getPath());
			}
		}
	}
	
	private void removeMenuItem(Menu menu, String path) {
		for(MenuItem item : menu.getItems()) {
			removeMenuItem(item, path);
		}
	}
	
	private void removeMenuItem(MenuItem menuItem, String path) {
		String menuPath = (String) menuItem.getData(Designer.DATA_THING);
		if(menuPath != null && menuPath.equals(path)) {
			menuItem.dispose();
			return;
		}
		
		Menu menu = menuItem.getMenu();
		if(menu != null) {
			removeMenuItem(menu, path);
		}
	}
	
	private MenuItem getReferenceMenuItem(Menu rootMenu, String refMenuPath) {
		for(MenuItem item : rootMenu.getItems()) {
			MenuItem refItem = getReferenceMenuItem(item, refMenuPath);
			if(refItem != null) {
				return refItem;
			}
		}
		
		return null;
	}
	
	private MenuItem getReferenceMenuItem(MenuItem item, String refMenuPath) {
		String thingPath = (String) item.getData(Designer.DATA_THING);
		if(thingPath != null && thingPath.equals(refMenuPath)) {
			return item;
		}
		
		//如果含有子菜单
		Menu menu = item.getMenu();
		if(menu != null) {
			return getReferenceMenuItem(menu, refMenuPath);
		}
		
		return null;
	}
	
	public static MenuContainer getMenuContainer(ActionContext actionContext) {
		return actionContext.getObject(ID);
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Menu menu = self.doAction("getMenu", actionContext);
		if(menu == null) {
			Executor.warn(TAG, "MenuContainer: does not create menu is null, path=" + self.getMetadata().getPath());
			return;
		}else {
			MenuContainer menuContainer = new MenuContainer(menu, actionContext);
			actionContext.g().put(self.getMetadata().getName(), menuContainer);
		}
	}
}
