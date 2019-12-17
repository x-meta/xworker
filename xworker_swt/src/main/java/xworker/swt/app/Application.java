package xworker.swt.app;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.reacts.DataReactor;
import xworker.swt.xwidgets.DataItemContainer;

public class Application {
	private static final String TAG = "Application";
	public static final byte ITEMS_SHELL = 0;
	public static final byte ITEMS_TOPLEFT = 1;
	public static final byte ITEMS_TOPRIGHT = 2;
	public static final byte ITEMS_MAIN = 3;
	public static final byte ITEMS_INIT = 4;
	
	Thing thing;
	Thing theme;
	ActionContext actionContext;
	ActionContext parentContext;
	Composite root;
	Shell shell;
	Widget parent = null;
	DataItemContainer shellItems = null;
	DataItemContainer topLeftItems = null;
	DataItemContainer topRightItems = null;
	DataItemContainer mainItems = null;
	DataItemContainer initItems = null;
	
	public Application(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		this.parentContext = parentContext;
		
		this.actionContext = new ActionContext();
		actionContext.put("parent", parentContext.get("parent"));
		actionContext.put("parentContext", parentContext);
		actionContext.put("application", this);
		
		//创建各种菜单条目
		if(thing.getBoolean("hasShellItems")) {
			shellItems = createItems("ShellItems@0");
		}
		if(thing.getBoolean("hasTopLeftItems")) {
			topLeftItems = createItems("TopLeftItems@0");
		}
		if(thing.getBoolean("hasTopRightItems")) {
			topRightItems = createItems("TopRightItems@0");
		}
		if(thing.getBoolean("hashMainItems")) {
			mainItems = createItems("MainItems@0");
		}
		initItems = createItems("InitItems@0");
		this.parent = parentContext.getObject("parent");
		Thing applicationTheme = thing.doAction("getApplication", parentContext);
		if(applicationTheme != null) {
			setTheme(applicationTheme);
		}
	}
	
	public void setTheme(Thing applicationTheme) {
		if(applicationTheme == null) {
			return;
		}
		
		this.theme = applicationTheme;
		
		//先销毁之前的
		if(root != null && !root.isDisposed()) {
			root.dispose();
		}
				
		boolean isComposite = false;
		if(parent instanceof Shell) {
			//考虑到是作为对话框而创建的形式，只有parent=parentThing时才是Composite
			Thing parentThing = Designer.getThing(parent);
			if(thing.getParent() == parentThing) {
				isComposite = true;
			}
		} else if(parent == null) {
			//如果parent为null，表示应该当作Shell创建
			isComposite = false;
		} else {
			isComposite = true;
		}
		
		//创建Applicatin		
		if(applicationTheme == null) {
			Executor.warn(TAG, "Application impl is null, thing=" + thing.getMetadata().getPath());
			return;
		}
		
		if(isComposite) {
			this.actionContext = new ActionContext();
			actionContext.put("parentContext", parentContext);
			actionContext.peek().put("parent", parent);
			actionContext.put("application", this);
			
			root = applicationTheme.doAction("create", actionContext, "application", this);
			//layout
			parentContext.peek().put("parent", root);
			Thing layoutData = thing.getThing("LayoutDatas@0");
			if(layoutData != null) {
				for(Thing child : layoutData.getChilds()) {
					child.doAction("create", parentContext);
				}
			}
			
			((Composite) parent).layout();
		} else{
			if(shell == null || shell.isDisposed()) {
				Thing shellThing = thing.doAction("getShell", actionContext);
				if(shellThing == null) {
					shellThing = World.getInstance().getThing("xworker.swt.app.prototypes.ApplicationPrototype");
				}
				
				shell = shellThing.doAction("create", actionContext);
				shell.setLayout(new FillLayout());
			}
			
			this.actionContext = new ActionContext();
			actionContext.put("parentContext", parentContext);
			actionContext.peek().put("parent", shell);
			actionContext.g().put("shell", shell);
			if(shellItems != null) {
				shellItems.setActionContext(actionContext);
			}
			if(topLeftItems != null) {
				topLeftItems.setActionContext(actionContext);
			}
			if(topRightItems != null) {
				topRightItems.setActionContext(actionContext);
			}
			if(mainItems != null) {
				mainItems.setActionContext(actionContext);
			}
			root = applicationTheme.doAction("create", actionContext, "application", this);
			shell.layout();
			shell.setVisible(true);
		}
	}
		
	public Thing getTheme() {
		return theme;
	}
	
	public void reset() {
		this.setTheme(theme);
	}

	private DataItemContainer createItems(String name) {
		Thing itemsThing = thing.getThing(name);
		if(itemsThing != null) {
			return (DataItemContainer) itemsThing.doAction("create", actionContext);
		}
		
		return null;
	}
	
	public String getTitle() {
		return thing.doAction("getTitle", actionContext);
	}
	
	public String getTitleImage() {
		return thing.doAction("getTitleImage", actionContext);
	}
	
	/**
	 * 绑定指定的菜单条目到控件上，如果没有菜单条目返回false，否则返回true。
	 * 
	 * @param type
	 * @param widget
	 * @return
	 */
	public DataItemContainer bindItemsToWidget(byte type, Widget widget, DataReactor dataReactor) {
		DataItemContainer items = null;
		switch(type) {
		case ITEMS_SHELL:
			if(shell != null && shellItems != null) {
				items = shellItems;
			}
			break;
		case ITEMS_TOPLEFT:
			items = topLeftItems;
			break;
		case ITEMS_TOPRIGHT:
			items = topRightItems;
			break;
		case ITEMS_MAIN:
			items = mainItems;
			break;
		case ITEMS_INIT:
			items = initItems;
			break;
		}		
		if(items != null) {
			if(widget != null) {
				items.bind(widget);
			}
			if(dataReactor != null){
				items.getDataReactor().addNextReator(dataReactor);
			}
			
			//如果是初始化条目，触发选择事件
			if(type == ITEMS_INIT) {
				items.fireSelectionAll();
			}
			return items;
		}else {
			return null;
		}
	}
	
	
	
	public Control getControl() {
		if(shell != null) {
			return shell;
		}else {
			return root;
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Application application = new Application(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), application);
		
		return application.getControl();
	}
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Application application = new Application(self, actionContext);
		actionContext.peek().put(self.getMetadata().getName(), application);
		
		Control control = application.getControl();
		if(control instanceof Shell) {
			((Shell) control).setVisible(true);
		}
	}
}
