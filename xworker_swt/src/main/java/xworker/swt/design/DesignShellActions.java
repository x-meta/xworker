package xworker.swt.design;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.swt.widgets.CoolBarCreator;
import xworker.swt.widgets.ShellCreator;

/**
 * 设计器界面的相关动作，对应模型xworker.swt.design.DesignToolBase，大部分方法应该已经没有再使用。
 * 
 * @author Administrator
 *
 */
public class DesignShellActions {
	public static void designTreeInit(ActionContext actionContext){
		designTreeInit(actionContext, true);
	}
	
	public static void showControlsOnTree(Control control, Tree tree, ActionContext actionContext){
		control = Designer.getDesignableControl(control);
		if(control == null) {
			tree.removeAll();
			return;
		}
		
		String thingPath = (String) control.getData("_designer_thingPath");
		Thing thing = World.getInstance().getThing(thingPath);
		if(thing == null){
			return;
		}
		
		if(tree == null){
			return;
		}
		tree.removeAll();
		
		List<Item> items = new ArrayList<Item>();
		initItem(control, items, thing);
		
		//在树上显示
		for(Item item : items){
			initItemAtTree(tree, item, actionContext, control);
		}	
	}
	
	public static void designTreeInit(ActionContext actionContext, boolean reInit){
		Tree tree = (Tree) actionContext.get("tree");
		ActionContext parentContext = actionContext.getObject("parentContext");	
		Control control = (Control) parentContext.get("control");
		designTreeInit(actionContext, tree, control, reInit);
	}
	
	/**
	 * 控件的设计树。
	 * 
	 * @param actionContext
	 * @param reInit
	 */
	public static void designTreeInit(ActionContext actionContext, Tree tree, Control control, boolean reInit){
		//初始化条目			
		String thingPath = (String) control.getData("_designer_thingPath");
		Thing thing = World.getInstance().getThing(thingPath);
		if(thing == null){
			return;
		}
		Thing rootThing = thing.getRoot();
		Control rootControl = getRootControl(control, rootThing);
		
		if(reInit || rootControl != actionContext.getScope(0).get("rootControl")){
			actionContext.getScope(0).put("rootControl", rootControl);
			
			if(tree == null){
				return;
			}
			tree.removeAll();
			
			List<Item> items = new ArrayList<Item>();
			initItem(rootControl, items, rootThing);
			
			//在树上显示
			for(Item item : items){
				initItemAtTree(tree, item, actionContext, control);
			}	
		}
		
		//Text pathText = (Text) actionContext.get("pathText");
		//pathText.setText(thingPath);
		
		ActionContainer ac = (ActionContainer) actionContext.get("actions");
		if(ac != null){
			if(tree.getItems().length == 0){
				ac.doAction("itemUnSelected", actionContext);	
			}else{
				ac.doAction("itemSelected", actionContext);
			}
		}
		/*
		//设置编辑器
		ActionContext thingEditor = (ActionContext) actionContext.get("thingEditor");
		thingEditor.put("variablesActionContext", control.getData("_designer_actionContext"));
		ActionContainer editorActions = (ActionContainer) thingEditor.get("editorActions");
		editorActions.doAction("setThing", UtilMap.toMap("thing", thing));
		editorActions.doAction("selectThing", UtilMap.toMap("thing", thing));
						
		//代码辅助，查看变量
		actionContext.g().put("controlContext", control.getData(Designer.DATA_ACTIONCONTEXT));
		ActionContext actionContextViewer = actionContext.getObject("actionContextViewer");
		ActionContainer aactions = actionContextViewer.getObject("actions");
		aactions.doAction("setActionContext", actionContext, "ac", control.getData(Designer.DATA_ACTIONCONTEXT));
		
		//可交互控件列表
		ActionContainer uiActions = actionContext.getObject("interactiveActions");
		if(uiActions != null){
			uiActions.doAction("initUI", actionContext, "control", control.getShell());
		}		
		*/
	}
	
	public static void reinitDesignTree(ActionContext actionContext){
		designTreeInit(actionContext, true);
	}
	
	
	public static void initItemAtTree(Object parentItem, Item item, ActionContext actionContext, Control control){
		TreeItem treeItem = null;
		if(parentItem instanceof Tree){
			treeItem = new TreeItem((Tree) parentItem, SWT.None);
		}else{
			treeItem = new TreeItem((TreeItem) parentItem, SWT.None);
		}
		
		XWorkerTreeUtil.initItem(treeItem, item.thing, actionContext);
		treeItem.setData(item);
		for(Item childItem : item.child){
			initItemAtTree(treeItem, childItem, actionContext, control);
		}
		
		treeItem.setExpanded(true);
		
		if(item.control == control){
			treeItem.getParent().select(treeItem);
			treeItem.getParent().showItem(treeItem);
			actionContext.getScope(0).put("item", item);
		}
	}
	
	/**
	 * 删除选中的控件，会同时删除相关的事物。
	 * 
	 * @param control
	 */
	public static void deleteControl(final Control control){
		String thingPath = (String) control.getData("_designer_thingPath");
		Thing thing = World.getInstance().getThing(thingPath);
		Thing parentThing = thing.getParent();
		if(parentThing != null){
			parentThing.removeChild(thing);
		}else{
			thing.remove();
		}
		
		control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Composite parent = control.getParent();
				Composite parentParent =  parent.getParent();
				
				control.dispose();
				if(parent != null){
					if(parentParent != null){
						parentParent.layout();
					}else{
						parent.redraw();
					}
				}
			}
		});
		
		
	}
	
	public static void updateControl(final Item item, final ActionContainer parentActions){		
		item.control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				ActionContext actionContext = (ActionContext) item.control.getData("_designer_actionContext");
				Composite parent = item.control.getParent();
				if(item.control instanceof Shell){
					Shell shell = (Shell) item.control.getShell();
		
					actionContext.peek().put("shell", shell);
					actionContext.peek().put("self", item.thing);
					ShellCreator.update(actionContext);
					/*
					shell.dispose();
					actionContext.peek().put("parent", parent);
					shell = (Shell) item.thing.doAction("create", actionContext);
					item.control = shell;					
					shell.setLocation(location);
					shell.open();
					*/
				}else if(parent instanceof CoolBar){
					Thing itemThing =  item.thing;
					CoolBar coolbar = (CoolBar) parent;
					CoolItem coolItem = null;
					for(CoolItem citem : coolbar.getItems()){
						if(citem.getControl() == item.control){
							coolItem = citem;
							break; 
						}
					}
					
					item.control.dispose();
					actionContext.peek().put("parent", parent);
					if(coolItem != null){
						Control newControl = (Control) itemThing.doAction("create", actionContext);
						item.control = newControl;
						coolItem.setControl(newControl);
					}else{
						itemThing.getParent().doAction("create", actionContext);
					}
					
					CoolBarCreator.initCoolBar((CoolBar) parent);
				}else if(parent instanceof ToolBar){
					Thing itemThing =  item.thing;
					ToolBar toolbar = (ToolBar) parent;
					ToolItem toolItem = null;
					for(ToolItem citem : toolbar.getItems()){
						if(citem.getControl() == item.control){
							toolItem = citem;
							break; 
						}
					}
					
					item.control.dispose();
					actionContext.peek().put("parent", parent);
					if(toolItem != null){
						Control newControl = (Control) itemThing.doAction("create", actionContext);
						item.control = newControl;
						toolItem.setControl(newControl);
					}else{
						itemThing.getParent().doAction("create", actionContext);
					}
					
					for (ToolItem coolItem : toolbar.getItems()) {
					    Control control = coolItem.getControl();
					    if(control == null) continue;
					    Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					    coolItem.setWidth(size.y);
					}
				}else{					
					int index = -1;
					int childCount = parent.getChildren().length;
					for(int i=0;i<parent.getChildren().length;i++){
						if(parent.getChildren()[i] == item.control){
							index = i;
							break;
						}
					}
					
					item.control.dispose();
					actionContext.peek().put("parent", parent);
					
					Control newControl = (Control) item.thing.doAction("create", actionContext);
					item.control = newControl;
				
					//如果不是最后一个位置，那么移动到原来的位置
					if(index != -1 && index < childCount - 1){
						Control[] childs = parent.getChildren();
						newControl.moveAbove(childs[index]);						
					}
										
					parent.layout();
					parent.getShell().forceActive();
				}
								
				Shell shell = (Shell) parentActions.getActionContext().get("shell");
				shell.getDisplay().asyncExec(new Runnable(){
					public void run(){
						parentActions.doAction("init", parentActions.getActionContext(), UtilMap.toMap("control", item.control));
					}
				});
			}
		});
		
	}
	
	public static void designerTreeSelection(ActionContext actionContext){
		ActionContainer ac = (ActionContainer) actionContext.get("actions");
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		Item ditem = (Item) item.getData();
		if(ditem.control.isDisposed()){
			item.dispose();
			event.doit = false;
			
			Tree tree = (Tree) actionContext.get("tree");
			if(tree.getItems().length == 0){
				//((Shell) actionContext.get("shell")).dispose();
				ac.doAction("itemUnSelected", actionContext);				
			}
			return;
		}

		actionContext.getScope(0).put("item", ditem);
		Designer.setCurrentDesignControl(ditem.control);
	
		Text pathText = (Text) actionContext.get("pathText");
		if(pathText != null){
			pathText.setText(ditem.thing.getMetadata().getPath());
			
			ac.doAction("itemSelected", actionContext);
		}
		
		//设置编辑器 
		Thing thing = ditem.thing;
		//Control control = (Control) data.get("control");
		/*
		ActionContext thingEditor = (ActionContext) actionContext.get("thingEditor");
		ActionContainer editorActions = (ActionContainer) thingEditor.get("editorActions");
		editorActions.doAction("setThing", UtilMap.toMap("thing", thing));
		editorActions.doAction("selectThing", UtilMap.toMap("thing", thing));
		*/
		//代码辅助
		/*
		StyledText codeText = (StyledText) actionContext.get("codeText");
		CodeAssistor.attach(null, codeText, (ActionContext) ditem.control.getData(Designer.DATA_ACTIONCONTEXT));
		*/
	}
	
	public static void initItem(Control control, List<Item> items, Thing rootThing){
		String thingPath = (String) control.getData("_designer_thingPath");
		//ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		Boolean isAttribute = (Boolean) control.getData("_designer_isAttribute");
		String creator = (String) control.getData(Designer.DATA_CREATOR);
		Boolean creatorRoot = (Boolean) control.getData(Designer.DATA_CREATOR_ROOT);
		if(isAttribute != null && isAttribute) {
			return;
		}
		
		if(thingPath != null && !isAttribute && (creator == null || (creatorRoot != null && creatorRoot == true))){
			Item item = new Item();
			item.thing = World.getInstance().getThing(thingPath);
			if(item.thing == null || item.thing.getRoot() != rootThing){
				return;
			}
			
			item.control = control;
			items.add(item);
			items = item.child;
		}
		
		if(control instanceof Composite){
			Composite composite = (Composite) control;
			for(Control childControl : composite.getChildren()){
				initItem(childControl, items, rootThing);
			}
		}	
	}
	
	
	public static Control getRootControl(Control control, Thing rootThing){
		Composite parent = control.getParent();
		if(parent  != null){// && !(control instanceof Shell)){
			String thingPath = (String) parent.getData(Designer.DATA_THING);
			Thing thing = World.getInstance().getThing(thingPath);
			if(thing != null && thing.getRoot() == rootThing){
				return getRootControl(parent, rootThing);
			}
		}
		
		return control;		
	}
	
	public static class Item{
		public Control control;
		public Thing thing;
		public List<Item> child = new ArrayList<Item>();
	}
}
