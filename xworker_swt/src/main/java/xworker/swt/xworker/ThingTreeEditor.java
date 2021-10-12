package xworker.swt.xworker;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.ThingUtil;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.ui.swt.Clipboard;
import xworker.util.UtilData;

@ActionClass(creator="getInstance")
public class
ThingTreeEditor {
	ActionContext parentContext;
	ActionContext actionContext;
	Tree tree;
	Thing self;
	Thing thing;
	boolean showRoot;
	MenuItem addMenuItem;
	MenuItem editMenuItem;
	MenuItem removeMenuItem;
	MenuItem moveUpMenuItem;
	MenuItem moveDownMenuItem;
	MenuItem copyMenuItem;
	MenuItem copyPathMenuItem;
	MenuItem cutMenuItem;
	MenuItem pasteMenuItem;
	MenuItem pasteAsChildMenuItem;
	MenuItem refreshMenuItem;
	
	Shell addChildShell = null;
	
	public ThingTreeEditor(Thing self, Tree tree, ActionContext parentContext) {
		this.self = self;
		this.tree = tree;
		
		actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
		actionContext.put("parent", tree);
		actionContext.put("instance", this);
		actionContext.put("tree",  tree);		
		
		World world = World.getInstance();
		//创建菜单
		Thing menuThing = world.getThing("xworker.swt.xworker.prototype.ThingTreeEditor/@tree/@thingTreeEditorMenu");
		menuThing.doAction("create", actionContext);
		
		//树的事件
		Thing listenerThing = world.getThing("xworker.swt.xworker.prototype.ThingTreeEditor/@tree/@Listeners");
		listenerThing.doAction("create", actionContext);
		
		addMenuItem = actionContext.getObject("addMenuItem");
		editMenuItem = actionContext.getObject("editMenuItem");
		removeMenuItem = actionContext.getObject("removeMenuItem");
		moveUpMenuItem = actionContext.getObject("moveUpMenuItem");
		moveDownMenuItem = actionContext.getObject("moveDownMenuItem");
		copyMenuItem = actionContext.getObject("copyMenuItem");
		copyPathMenuItem = actionContext.getObject("copyPathMenuItem");
		cutMenuItem = actionContext.getObject("cutMenuItem");
		pasteMenuItem = actionContext.getObject("pasteMenuItem");
		pasteAsChildMenuItem = actionContext.getObject("pasteAsChildMenuItem");
		refreshMenuItem = actionContext.getObject("refreshMenuItem");
		
		//设置事物
		Thing thing = self.doAction("getThing", parentContext);
		Boolean showRoot = self.doAction("isShowRoot", parentContext);
		if(thing != null) {
			this.setThing(thing, showRoot);
		}
		
		tree.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Thing nodeThing = (Thing) event.item.getData();
				if(nodeThing != null) {
					actionContext.g().put("currentThing", nodeThing);
				}
				
				if(addChildShell != null && addChildShell.isDisposed()== false) {					
					ActionContainer actions = (ActionContainer) addChildShell.getData();
					actions.doAction("setThing", null, "thing", nodeThing);
				}
			}
			
		});
	}
	
	public void setThing(Thing thing, boolean showRoot) {
		this.showRoot = showRoot;
		this.thing = thing;
		
		//先移除所有子节点
		tree.removeAll();
		
		//在树上显示
		if(showRoot) {
			SwtUtils.showThingOnTree(thing, tree, actionContext);
		}else {
			for(Thing child : thing.getChilds()) {
				SwtUtils.showThingOnTree(child, tree, actionContext);
			}
		}
	}
	
	public void setThing(String path, boolean showRoot) {
		Thing thing = World.getInstance().getThing(path);
		if(thing != null) {
			setThing(thing, showRoot);
		}
	}
	
	public void onAddChild(ActionContext actionContext) {
		//Thing currentThing = actionContext.getObject("thing");
		Thing childThing = actionContext.getObject("childThing");

		if(!thing.getChilds().contains(childThing)) {
			thing.addChild(childThing);
		}
		thing.save();
		if(tree.getSelectionCount() == 0) {
			SwtUtils.showThingOnTree(childThing, tree, actionContext);

			if(tree.getSelectionCount() > 0) {
				tree.getSelection()[0].setExpanded(true);
			}
		}else {
			SwtUtils.showThingOnTreeItem(childThing, tree.getSelection()[0], actionContext);
		}
		
		//触发onAdd事件
		self.doAction("onAdded", this.parentContext, "thing", childThing);
		
		//关闭添加窗口
		if(addChildShell != null && !addChildShell.isDisposed()) {
			addChildShell.dispose();
		}
	}

	public void addRootMenuItemSelection(ActionContext actionContext){
		if(thing == null) {
			return;
		}

		if(addChildShell != null && !addChildShell.isDisposed()) {
			return;
		}

		//当前的事物模型
		Thing currentThing = null;
		if(tree.getSelectionCount() > 0) {
			TreeItem treeItem = tree.getSelection()[0];
			currentThing = (Thing) treeItem.getData();
			if(currentThing != thing){
				currentThing = currentThing.getParent();
				if(treeItem.getParentItem() != null){
					tree.setSelection(treeItem.getParentItem());
				}else{
					tree.setSelection(new TreeItem[0]);
				}
			}
		}else {
			currentThing = thing;
		}

		//打开添加子节点的窗口
		ActionContext ac = new ActionContext();
		ac.put("parent", tree.getShell());
		ac.put("instance", this);

		Thing dialogThing = World.getInstance().getThing("xworker.swt.xworker.prototype.ThingTreeEditorAddDialog");
		addChildShell = dialogThing.doAction("create", ac);
		addChildShell.setText(addChildShell.getText() + "-" + currentThing.getMetadata().getLabel());

		ActionContainer actions = ac.getObject("addChildActions");
		actions.doAction("setThing", ac, "thing", currentThing);
		addChildShell.setData(actions);
		addChildShell.setVisible(true);
	}

	public void addMenuItemSelection(ActionContext actionContext) {
		if(thing == null) {
			return;
		}
		
		if(addChildShell != null && !addChildShell.isDisposed()) {
			return;
		}
		
		//当前的事物模型
		Thing currentThing = null;
		if(tree.getSelectionCount() > 0) {
			currentThing = (Thing) tree.getSelection()[0].getData();
		}else {
			currentThing = thing;
		}
		
		//打开添加子节点的窗口
		ActionContext ac = new ActionContext();
		ac.put("parent", tree.getShell());
		ac.put("instance", this);
		
		Thing dialogThing = World.getInstance().getThing("xworker.swt.xworker.prototype.ThingTreeEditorAddDialog");
		addChildShell = dialogThing.doAction("create", ac);
		addChildShell.setText(addChildShell.getText() + "-" + currentThing.getMetadata().getLabel());
		
		ActionContainer actions = ac.getObject("addChildActions");
		actions.doAction("setThing", ac, "thing", currentThing);
		addChildShell.setData(actions);
		addChildShell.setVisible(true);
	}
	
	public void editMenuItemSelection(ActionContext actionContext) {
		Map<String, Object> values = actionContext.getObject("values");
		TreeItem item = tree.getSelection()[0];
		Thing currentThing = (Thing) item.getData();
		currentThing.getAttributes().putAll(values);
		currentThing.save();
		
		XWorkerTreeUtil.initItem(item, currentThing, actionContext);
		
		//触发onModified事件
		self.doAction("onModified", actionContext, "thing", currentThing);
	}
	
	public void removeMenuItemSelection(ActionContext actionContext) {		
		TreeItem item = tree.getSelection()[0];
		Thing currentThing = (Thing) item.getData();
		
		//触发onRemove事件，如果事件返回true，那么不删除
		Object result = self.doAction("beforeRemove", actionContext, "thing", currentThing);
		if(UtilData.isTrue(result)) {
			return;
		}
		
		Thing parent = currentThing.getParent();
		if(parent != null) {
			parent.removeChild(currentThing);
			parent.save();
		}
		
		item.dispose();
	}
	
	public void moveUpMenuItemSelection(ActionContext actionContext) {		
		TreeItem currentItem = tree.getSelection()[0];
		Thing currentThing = (Thing) currentItem.getData();
		Thing parent = currentThing.getParent();
		if(parent != null){
		    parent.changeChildIndex(currentThing, -1, -1);
		    parent.save();
		    
		    TreeItem treeItem = tree.getSelection()[0];
		    TreeItem parentTreeItem = treeItem.getParentItem();  
		    int index = parentTreeItem.indexOf(treeItem);
		    if(index > 0){
		        index --;        
		        TreeItem item = new TreeItem(parentTreeItem, SWT.NONE, index);
		        item.setData(currentThing);
		        item.setText(currentThing.getMetadata().getLabel()+ " (" + currentThing.getThingName() + ")");
		        XWorkerTreeUtil.initItem(item, currentThing, actionContext);
		        
		        for(Thing child : currentThing.getChilds()){
		        	initOutlineTreeItem(item, child, actionContext);
		        }
		    
		        //item.setExpanded(true);
		        tree.setSelection(item);
		        
		        treeItem.dispose();
		    }
		}				
	}
	
	
	private void initOutlineTreeItem(TreeItem treeItem, Thing dataObj, ActionContext actionContext){
	    TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
	    childItem.setData(dataObj);
	    childItem.setText(dataObj.getMetadata().getLabel() + " (" + dataObj.getThingName() + ")");
	    //childItem.setImage(Resources.getImage("dataObject.png"));
	    XWorkerTreeUtil.initItem(childItem, dataObj, actionContext);
	    
	    for(Thing child : dataObj.getChilds()){
	    	initOutlineTreeItem(childItem, child, actionContext);
	    }

	}
	
	public void moveDownMenuItemSelection(ActionContext actionContext) {
		TreeItem currentItem = tree.getSelection()[0];
		Thing currentThing = (Thing) currentItem.getData();
		Thing parent = currentThing.getParent();

		if(parent != null){
		    parent.changeChildIndex(currentThing, -1, 1);
		    parent.save();
		    TreeItem treeItem = currentItem;
		    TreeItem parentTreeItem = treeItem.getParentItem();  
		    int index = parentTreeItem.indexOf(treeItem);
		    if(index < parentTreeItem.getItems().length - 1){
		        index = index + 2;         
		        TreeItem item = new TreeItem(parentTreeItem, SWT.NONE, index);
		        item.setData(currentThing);
		        item.setText(currentThing.getMetadata().getLabel()+ " (" + currentThing.getThingName() + ")");
		        XWorkerTreeUtil.initItem(item, currentThing, actionContext);
		        
		        for(Thing child : currentThing.getChilds()){
		        	initOutlineTreeItem(item, child, actionContext);
		        }
		        
		        //item.setExpanded(true);
		        tree.setSelection(item);
		        
		        treeItem.dispose();
		    }
		}

	}
	
	public void copyMenuItemSelection(ActionContext actionContext) {
		TreeItem treeItem = tree.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		if(thing != null){
			xworker.ui.swt.Clipboard.add(thing.detach());
		}
	}
	
	public void copyPathMenuItemSelection(ActionContext actionContext) {
		Action copyRWT = actionContext.getObject("copyRWT");
		if(SwtUtils.isRWT()){
		    TreeItem treeItem = tree.getSelection()[0];
		    String plainText = ((Thing) treeItem.getData()).getMetadata().getPath();
		    copyRWT.run(actionContext, "path", plainText);
		}else{
			TreeItem treeItem = tree.getSelection()[0];
			org.eclipse.swt.dnd.Clipboard clipboard = new org.eclipse.swt.dnd.Clipboard(tree.getDisplay());
			String plainText = ((Thing) treeItem.getData()).getMetadata().getPath();
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] {plainText}, new Transfer[] {textTransfer});
			clipboard.dispose();
		}
	}
	
	public void cutMenuItemSelection(ActionContext actionContext) {
		Listener copyMenuSelection = actionContext.getObject("copyMenuItemSelection");
		Listener deleteMenuSelection = actionContext.getObject("removeMenuItemSeleciton");
		
		copyMenuSelection.handleEvent(null);
		deleteMenuSelection.handleEvent(null);
	}
	
	public void pasteMenuItemSelection(ActionContext actionContext) {
		World world = World.getInstance();
		TreeItem treeItem = tree.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		thing = world.getThing(thing.getMetadata().getPath());

		Thing memObj = xworker.ui.swt.Clipboard.get();
		if(memObj == null) return;

		memObj = memObj.detach();
		ThingUtil.paste(thing, memObj);
		thing.save();

		this.initOutlineTreeItem(treeItem, thing, actionContext);
	}
	
	public void pasteAsChildMenuItemSelection(ActionContext actionContext) {
		World world = World.getInstance();
		TreeItem treeItem = tree.getSelection()[0];
		Thing thing = (Thing) treeItem.getData();
		thing = world.getThing(thing.getMetadata().getPath());

		Thing memObj = xworker.ui.swt.Clipboard.get();
		if(memObj == null) return;

		memObj = memObj.detach();
		ThingUtil.pasteAsChild(thing, memObj);
		thing.save();

		XWorkerTreeUtil.refresh(treeItem, thing, actionContext);	
	}
	
	public void refreshMenuItemSelection(ActionContext actionContext) {
		if(thing == null) {
			return;
		}
		
		if(showRoot) {
			XWorkerTreeUtil.refresh(tree, null, actionContext);
		}else {
			XWorkerTreeUtil.refresh(tree, thing, actionContext);
		}
	}
	
	
	public void menuDetect(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		if(thing == null) {
			event.doit = false;
			return;
		}
		
		if(tree.getSelectionCount() == 0) {
			copyMenuItem.setEnabled(false);
			copyPathMenuItem.setEnabled(false);
			cutMenuItem.setEnabled(false);
			editMenuItem.setEnabled(false);
			moveDownMenuItem.setEnabled(false);
			moveUpMenuItem.setEnabled(false);
			pasteAsChildMenuItem.setEnabled(false);		
			if(Clipboard.get() != null) {
				pasteMenuItem.setEnabled(true);
			}else {
				pasteMenuItem.setEnabled(false);
			}
			refreshMenuItem.setEnabled(false);
			removeMenuItem.setEnabled(false);
			
		}else {
			copyMenuItem.setEnabled(true);
			copyPathMenuItem.setEnabled(true);
			cutMenuItem.setEnabled(true);
			editMenuItem.setEnabled(true);
			moveDownMenuItem.setEnabled(true);
			moveUpMenuItem.setEnabled(true);			
			if(Clipboard.get() != null) {
				pasteMenuItem.setEnabled(true);
				pasteAsChildMenuItem.setEnabled(true);	
			}else {
				pasteMenuItem.setEnabled(false);
				pasteAsChildMenuItem.setEnabled(false);	
			}
			refreshMenuItem.setEnabled(true);
			removeMenuItem.setEnabled(true);
		}
		
	}
	public static ThingTreeEditor getInstance(ActionContext actionContext) {
		return (ThingTreeEditor) actionContext.get("instance");
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Tree parent = actionContext.getObject("parent");
		
		ThingTreeEditor editor = new ThingTreeEditor(self, parent, actionContext);
		actionContext.g().put(self.getMetadata().getName(), editor);
	}
}
