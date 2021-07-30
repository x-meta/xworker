package xworker.swt.app;

import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import org.xmeta.util.ThingLoader;
import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.util.ItemIndex;
import xworker.swt.widgets.CoolBarCreator;
import xworker.thingeditor.ICoolBarContainer;

public class CoolBarContainer implements ICoolBarContainer {
	private static final String TAG = CoolBarContainer.class.getName();
	private static final String ID = "__CoolbarContainer_id__";
	
	Thing self;
	CoolBar coolBar;
	
	public CoolBarContainer(CoolBar coolbar, ActionContext actionContext) {
		this.self = actionContext.getObject("self");
		
		//MenuContainer是Menu的子节点
		this.coolBar = coolbar;
		
		actionContext.put(ID, this);
	}
	
	/**
	 * 设置应用的菜单
	 * 
	 * @param menuConfig
	 * @param actionContext
	 */
	@ActionParams(names="menuConfig")
	public void setEditorCoolBar(Thing menuConfig, ActionContext actionContext) {
		Thing itemThing = menuConfig.doAction("getCoolItem", actionContext);
		if(itemThing == null) {
			return;
		}

		//编辑器以及编辑器绑定的对象
		Thing editorThing = actionContext.getObject("editorThing");
		Object objectForThingLoader = null;
		if(editorThing != null){
			objectForThingLoader = editorThing.doAction("getObjectForThingLoader", actionContext);
		}

		String refType = menuConfig.doAction("getRefItemType", actionContext);
		String refMenuPath = menuConfig.doAction("getRefItemPath", actionContext);
		CoolItem refItem = getReferenceItem(coolBar, refMenuPath);
		CoolBar parent = coolBar;		
		int index = parent.getItemCount();
		if(refItem != null) {
			parent = refItem.getParent();			
			index = parent.indexOf(refItem);
			if("above".equals(refType)) {				
			}else {
				index = index + 1;
			}
		}
		
		ItemIndex.set(index);
		Bindings bindings = actionContext.push();
		try {
			bindings.put("parent", parent);
			if(objectForThingLoader == null){
				itemThing.doAction("create", actionContext);
			}else{
				ThingLoader.load(objectForThingLoader, itemThing, actionContext);
			}
		}finally {
			actionContext.pop();
		}
		CoolBarCreator.initCoolBar(parent);
		parent.layout();
		parent.getParent().layout();
	}
	
	@ActionParams(names="menuConfig")
	public void removeEditorCoolItem(Thing menuConfig, ActionContext actionContext) {
		Thing menuThing = menuConfig.doAction("getCoolItem", actionContext);
		if(menuThing == null) {
			return;
		}
		
		for(CoolItem item : coolBar.getItems()) {
			String path = (String) item.getData(Designer.DATA_THING);
			if(path != null && path.equals(menuThing.getMetadata().getPath())) {
				item.getControl().dispose();
				item.dispose();
			}
		}
		
		CoolBar parent = coolBar;	
		parent.layout();
		parent.getParent().layout();
	}
	
	private CoolItem getReferenceItem(CoolBar parent, String refPath) {
		for(CoolItem item : parent.getItems()) {
			String thingPath = (String) item.getData(Designer.DATA_THING);
			if(thingPath != null && thingPath.equals(refPath)) {
				return item;
			}
		}
		
		return null;
	}
	
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CoolBar coolBar = self.doAction("getCoolBar", actionContext);
		if(coolBar == null) {
			Executor.warn(TAG, "CoolBarContainer: does not create, coolBar is null, path=" + self.getMetadata().getPath());
			return;
		}else {
			CoolBarContainer menuContainer = new CoolBarContainer(coolBar, actionContext);
			actionContext.g().put(self.getMetadata().getName(), menuContainer);
		}
	}
}
