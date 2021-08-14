package xworker.swt.app;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.workbench.IView;

public class View implements IView<Composite, Control> {
	String id;
	Thing thing;
	ActionContext actionContext;
	Map<String, Object> params;
	Control control;
	
	Composite parent;
	//如果parent是CTabFolder，那么会有CTabItem
	CTabItem tabItem;
	ActionContext parentContext;
	
	public View(String id, Thing thing, Map<String, Object> params) {
		this.thing = thing;
		this.id = id;
		this.params = params;
	}
	
	public void create(Composite parent, ActionContext parentContext) {
		this.parent = parent;
		this.parentContext = parentContext;
		
		actionContext = new ActionContext();
		actionContext.put("parent", parent);
		actionContext.put("parentContext", parentContext);
		actionContext.put("params", params);
		Object result = thing.doAction("create", actionContext);
		if(result instanceof ActionContainer) {
			//是组件
			control = ((ActionContainer) result).doAction("getControl", actionContext);
		}else if(result instanceof Control) {
			control = (Control) result;
		}
	}

	public void showView(){
		if(tabItem != null && !tabItem.isDisposed()){
			tabItem.getParent().setSelection(tabItem);
		}
	}
	
	public void create(CTabFolder parent, boolean closeable, ActionContext parentContext) {
		this.parent = parent;
		this.parentContext = parentContext;
		
		actionContext = new ActionContext();
		actionContext.put("parent", parent);
		actionContext.put("parentContext", parentContext);
		actionContext.put("params", params);
		actionContext.put("viewThing", thing);
		
		CTabItem item = new CTabItem(parent, closeable ? SWT.CLOSE : SWT.NONE);
		item.setData(Workbench.VIEW_ID, id);
		item.setData(Designer.DATA_THING, thing.getMetadata().getPath());
		item.setText(thing.getMetadata().getLabel());
		item.setData("actionContext", actionContext);
		tabItem.setData(this);
		
		Object result = thing.doAction("create", actionContext);
		Control control = null;
		if(result instanceof ActionContainer) {
			//是组件
			control = ((ActionContainer) result).doAction("getControl", actionContext);
			item.setData("component", result);
		}else if(result instanceof Control) {
			control = (Control) result;
		}
		
		if(control != null) {
			Image image = SwtUtils.getIcon(thing, control, actionContext);
			if(image != null) {
				item.setImage(image);
			}
			item.setControl(control);
		}

		if(parent.getItemCount() == 1){
			parent.setSelection(item);
		}
	}	
	
	public void create(Workbench workbench, CTabFolder parent, boolean closeable, ActionContext parentContext) {
		this.parent = parent;
		this.parentContext = parentContext;
		
		actionContext = new ActionContext();
		actionContext.put("parent", parent);
		actionContext.put("parentContext", parentContext);
		actionContext.put("params", params);
		actionContext.put("workbench", workbench);
		actionContext.put("viewThing", thing);
		
		tabItem = new CTabItem(parent, closeable ? SWT.CLOSE : SWT.NONE);
		tabItem.setData(Workbench.VIEW_ID, id);
		tabItem.setData(Designer.DATA_THING, thing.getMetadata().getPath());
		tabItem.setText(thing.getMetadata().getLabel());
		tabItem.setData("actionContext", actionContext);
		tabItem.setData(this);
		
		Object result = thing.doAction("create", actionContext);
		Control control = null;
		if(result instanceof ActionContainer) {
			//是组件
			control = ((ActionContainer) result).doAction("getControl", actionContext);
			tabItem.setData("component", result);
		}else if(result instanceof Control) {
			control = (Control) result;
		}
		
		if(control != null) {
			Image image = SwtUtils.getIcon(thing, control, actionContext);
			if(image != null) {
				tabItem.setImage(image);
			}
			tabItem.setControl(control);
		}

		if(parent.getItemCount() == 1){
			parent.setSelection(tabItem);
		}
	}

	public String getId() {
		return id;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Composite getParent() {
		return parent;
	}

	public CTabItem getTabItem() {
		return tabItem;
	}

	public ActionContext getParentContext() {
		return parentContext;
	}

	public Control getControl() {
		return control;
	}
	
	public ActionContainer getActionContainer() {
		return actionContext.getObject("actions"); 
	}
	
	public Object doAction(String name) {
		ActionContainer actions = getActionContainer();
		if(actions != null) {
			return actions.doAction(name, actionContext);
		}
		
		return null;
	}
	
	public Object doAction(String name, Object ... params) {
		ActionContainer actions = getActionContainer();
		if(actions != null) {
			return actions.doAction(name, actionContext, params);
		}
		
		return null;
	}
}
