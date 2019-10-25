package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilThing;

import xworker.swt.util.SwtUtils;

public class AppTreeMenu implements Listener{
	private static Logger logger = LoggerFactory.getLogger(AppTreeMenu.class);
	public final static String key_thing = "__AppTreeMenu_item_key_thing__";
	public final static String key_actioncontext = "__AppTreeMenu_item_key_actioncontext__";
	
	Tree tree;
	Thing thing;
	ActionContext actionContext;
	
	public AppTreeMenu(Tree tree, Thing thing, ActionContext actionContext){
		this.tree = tree;
		this.thing = thing;
		this.actionContext = actionContext;
		
		refresh();
	}
	
	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//树
		Tree parent = actionContext.getObject("parent");

		//添加事件
		AppTreeMenu menu = new AppTreeMenu(parent, self, actionContext);
		parent.addListener(SWT.Selection, menu);
		
		//保存变量
		actionContext.getScope(0).put(self.getMetadata().getName(), menu);
	}
	
	/**
	 * 在树上重新刷新和显示菜单。
	 */
	public void refresh(){
		//先清空树
		tree.removeAll();
		
		for(Thing item : thing.getChilds("Item")){
			SwtUtils.showThingOnTree(item, tree, actionContext);
		}
	}

	@Override
	public void handleEvent(Event event) {
		Thing itemThing = (Thing) event.item.getData();
		
		//目标容器
		Composite target = getTarget();
		
		//面板事物
		Thing compositeThing = UtilThing.getThingFromAttributeOrChild(itemThing, "compositePath", "Composite@0");
		if(target != null && compositeThing != null){
			if(target instanceof TabFolder){
				this.showCompositeOnTabFodler(itemThing, compositeThing, (TabFolder) target);
			}else if(target instanceof CTabFolder){
				this.showCompositeOnCTabFodler(itemThing, compositeThing, (CTabFolder) target);
			}else{
				this.showCompositeOnComposite(itemThing, compositeThing, target);
			}
		}
	}
	
	public void showCompositeOnTabFodler(Thing menuItem, Thing compositeThing, TabFolder tabFolder){
		if(menuItem.getBoolean("singleInstance")){
			for(TabItem tabItem : tabFolder.getItems()){
				Thing oldItem = (Thing) tabItem.getData(key_thing);	
				if(menuItem == oldItem && menuItem.getBoolean("singleInstance")){
					tabFolder.setSelection(tabItem);
					return;
				}
			}
		}
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);	
		tabItem.setText(menuItem.getMetadata().getLabel());
		tabItem.setData(key_thing, menuItem);
		Composite composite = this.createComposite(menuItem, compositeThing, tabFolder);
		if(composite != null){
			tabItem.setData(key_actioncontext, composite.getData("actionContext"));
			tabItem.setControl(composite);
		}
		tabFolder.setSelection(tabItem);
	}
	
	public void showCompositeOnCTabFodler(Thing menuItem, Thing compositeThing, CTabFolder tabFolder){
		if(menuItem.getBoolean("singleInstance")){
			for(CTabItem tabItem : tabFolder.getItems()){
				Thing oldItem = (Thing) tabItem.getData(key_thing);	
				if(menuItem == oldItem && menuItem.getBoolean("singleInstance")){
					tabFolder.setSelection(tabItem);
					return;
				}
			}
		}
		
		CTabItem tabItem = new CTabItem(tabFolder, menuItem.getBoolean("closeable") ? SWT.CLOSE : SWT.NONE);
		tabItem.setText(menuItem.getMetadata().getLabel());
		tabItem.setData(key_thing, menuItem);
		Composite composite = this.createComposite(menuItem, compositeThing, tabFolder);		
		if(composite != null){
			tabItem.setData(key_actioncontext, composite.getData("actionContext"));
			tabItem.setControl(composite);
		}
		tabFolder.setSelection(tabItem);
	}
	
	public void showCompositeOnComposite(Thing menuItem, Thing compositeThing, Composite parent){
		Thing oldItem = (Thing) parent.getData(key_thing);	
		if(menuItem == oldItem){
			return;
		}
		
		ActionContext ac = (ActionContext) parent.getData(key_actioncontext);
		if(oldItem != null){
			//触发先前组件的close方法，如果返回false，说明先前组件还未处理完毕
			if(UtilData.isTrue(oldItem.doAction("close", ac)) == false){
				return;
			}
		}
		
		for(Control c : parent.getChildren()){
			c.dispose();
		}
		
		Composite comp = createComposite(menuItem, compositeThing, parent);
		comp.setData(key_thing, menuItem);
		comp.setData(key_actioncontext, comp.getData("actionContext"));
	}
	
	public Composite createComposite(Thing menuItem, Thing compositeThing, Widget parent){
		ActionContext ac = new ActionContext();
		ac.put("parent", parent);
		ac.put("menu", this);
		ac.put("item", menuItem);
		ac.put("parentContext", actionContext);
		ac.put("parentActionContext", actionContext);
				
		Composite composite = (Composite) compositeThing.doAction("create", ac);
		if(composite != null){
			composite.setData("actionContext", ac);
		}
		
		//执行初始化
		menuItem.doAction("init", ac, "menu", this, "item", menuItem);
		
		return composite;
	}
	
	public Composite getTarget(){
		try {
			return (Composite) thing.getObject("targetControl", actionContext);
		} catch (Exception e) {
			logger.error("get control error, path=" + thing.getMetadata().getPath(), e);
			return null;
		}
	}
}
