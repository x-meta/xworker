package xworker.swt.app.editorContainers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.Executor;
import xworker.swt.app.CoolBarContainer;
import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;
import xworker.swt.app.IEditorListener;
import xworker.swt.app.MenuContainer;
import xworker.swt.app.OutlineContainer;

public abstract class AbstractEditorContianer implements IEditorContainer, IEditorListener{
	private static final String TAG = AbstractEditorContianer.class.getName();
	protected  Thing thing;
	protected ActionContext actionContext;
	protected ActionContext containerContext; 
	
	protected ActionContainer actions;
	protected MenuContainer menuContainer;
	protected OutlineContainer outlineContainer;
	protected CoolBarContainer coolBarContainer;
	protected CoolBarContainer statusBarContainer;
	private List<IEditorListener> editorListeners = new ArrayList<IEditorListener>();
	
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
		
		editorListeners.add(this);
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

	@Override
	public void addIEditorListener(IEditorListener editorListener) {
		if(editorListener != null && editorListeners.contains(editorListener) == false) {
			editorListeners.add(editorListener);
		}		
	}

	@Override
	public void removeIEditorListener(IEditorListener editorListener) {
		editorListeners.remove(editorListener);
	}

	@Override
	public void fireStateChanged(IEditor editor) {
		try {
			for(IEditorListener listener : editorListeners) {
				listener.stateChanged(this, editor);
			}
		}catch(Exception e) {
			Executor.error(TAG, "Fire editor statechanged error", e);
		}
	}
	
	public void fireOnCreated(IEditor editor) {
		try {
			for(IEditorListener listener : editorListeners) {
				listener.onCreated(this, editor);
			}
		}catch(Exception e) {
			Executor.error(TAG, "fireOnCreated error", e);
		}
	}
	
	public void fireOnActive(IEditor editor) {
		try {
			for(IEditorListener listener : editorListeners) {
				listener.onActive(this, editor);
			}
		}catch(Exception e) {
			Executor.error(TAG, "fireOnActive error", e);
		}
	}
	
	public void fireOnDisposed(IEditor editor) {
		try {
			for(IEditorListener listener : editorListeners) {
				listener.onDisposed(this, editor);
			}
		}catch(Exception e) {
			Executor.error(TAG, "fireOnDisposed error", e);
		}
	}

	@Override
	public void onCreated(IEditorContainer editorContainer, IEditor editor) {
		thing.doAction("onEditorCreated", actionContext, "editorContainer", editorContainer, "editor", editor);
	}

	@Override
	public void onActive(IEditorContainer editorContainer, IEditor editor) {
		thing.doAction("onEditorActive", actionContext, "editorContainer", editorContainer, "editor", editor);
	}

	@Override
	public void onDisposed(IEditorContainer editorContainer, IEditor editor) {
		thing.doAction("onEditorDisposed", actionContext, "editorContainer", editorContainer, "editor", editor);
	}

	@Override
	public void stateChanged(IEditorContainer editorContainer, IEditor editor) {
		thing.doAction("stateEditorChanged", actionContext, "editorContainer", editorContainer, "editor", editor);
	}
	
	
}
