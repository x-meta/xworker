package xworker.swt.app.editorContainers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.app.CoolBarContainer;
import xworker.swt.app.IEditorContainer;
import xworker.swt.app.MenuContainer;
import xworker.swt.app.OutlineContainer;

public abstract class AbstractEditorContianer implements IEditorContainer{
	Thing thing;
	ActionContext actionContext;
	ActionContext containerContext; 
	
	ActionContainer actions;
	MenuContainer menuContainer;
	OutlineContainer outlineContainer;
	CoolBarContainer coolBarContainer;
	CoolBarContainer statusBarContainer;
	
	public AbstractEditorContianer(ActionContext actionContext) {
		this.thing = actionContext.getObject("self");
		this.actionContext = actionContext;
		
		//创建Container自身的环境
		containerContext = new ActionContext();
		containerContext.put("parentContext", actionContext);
		containerContext.put("parent", actionContext.getObject("parent"));
		containerContext.put(IEditorContainer.EDITOR_CONTAINER, this);
		
		Menu menu = thing.doAction("getMenu", actionContext);
		if(menu != null) {
			this.menuContainer = new MenuContainer(menu, actionContext);
		}
		CoolBar coolBar = thing.doAction("getCoolBar", actionContext);
		if(coolBar != null) {
			this.coolBarContainer = new CoolBarContainer(coolBar, actionContext);
		}
		CoolBar statusBar = thing.doAction("getStatusBar", actionContext);
		if(statusBar != null) {
			this.statusBarContainer = new CoolBarContainer(statusBar, actionContext);
		}
		this.outlineContainer = thing.doAction("getOutlineContainer", actionContext);			
	}
	
	public MenuContainer getMenuContainer() {
		return menuContainer;
	}

	public void setMenuContainer(MenuContainer menuContainer) {
		this.menuContainer = menuContainer;
	}

	public OutlineContainer getOutlineContainer() {
		return outlineContainer;
	}

	public void setOutlineContainer(OutlineContainer outlineContainer) {
		this.outlineContainer = outlineContainer;
	}

	public CoolBarContainer getCoolBarContainer() {
		return coolBarContainer;
	}

	public void setCoolBarContainer(CoolBarContainer coolBarContainer) {
		this.coolBarContainer = coolBarContainer;
	}

	public CoolBarContainer getStatusBarContainer() {
		return statusBarContainer;
	}

	public void setStatusBarContainer(CoolBarContainer statusBarContainer) {
		this.statusBarContainer = statusBarContainer;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContainer getActions() {
		return actions;
	}
	
	/**
	 * 返回Outline的父容器。
	 * 
	 * @return
	 */
	public Composite getOutlineParent() {
		if(outlineContainer != null) {
			return outlineContainer.getParentComposite();
		}	
		
		return null;
	}
	
	/**
	 * 设置编辑器的Outline，可以为null，null表示没有Outline。
	 * 
	 * @param outlineComposite
	 */
	public void setEditorOutline(Composite outlineComposite) {
		if(outlineContainer != null) {
			outlineContainer.setComposite(outlineComposite);
		}
	}
	
	@Override
	public boolean hasOutline() {
		return this.getOutlineParent() != null;
	}

	@Override
	public boolean hasMenu() {
		return this.menuContainer != null;
	}

	@Override
	public boolean hasCoolBar() {
		return this.coolBarContainer != null;
	}

	@Override
	public boolean hasStatusBar() {
		return this.statusBarContainer != null;
	}
}
