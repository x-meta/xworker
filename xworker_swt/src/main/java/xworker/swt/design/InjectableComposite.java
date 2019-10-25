package xworker.swt.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.command.interactive.InteractiveListener;
import xworker.command.interactive.InteractiveUI;

public class InjectableComposite {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//创建一个按钮
		Composite parent = actionContext.getObject("parent");
		Button setButton = new Button(parent, SWT.NONE);
		setButton.setText(UtilString.getString("lang:d=替换我&en=Replace Me", actionContext));
		setButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				//打开设计页面的工具
				Designer.showDesignDialog((Control) event.widget,
						World.getInstance().getThing("xworker.swt.design.DesignToolBase/@insert"));
			}
			
		});
		
		actionContext.peek().put("parent", setButton);
		//创建子节点，一般是布局等
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//保存变量
		Designer.attach(setButton, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), setButton);		
		return setButton;
		
		/*
		Composite parent = actionContext.getObject("parent");
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		
		//通过模板创建Compoiste的子节点
		
		composite.setData("parentContext", actionContext);
		composite.setData("thing", self);
		actionContext.peek().put("parent", composite);
		
		//创建InteractiveUI		
		Thing uiThing = World.getInstance().getThing("xworker.swt.design.prototype.InjectableCompositeUI");
		uiThing = uiThing.detach();
		
		copyAttributes(self, uiThing);
		InteractiveUI ui = new InteractiveUI(uiThing, Collections.EMPTY_MAP, actionContext);
		composite.setData(InteractiveUI.DATA_KEY, ui);
				
		//如果已设置子节点
		Thing control = self.getThing("Control@0");
		if(control != null && control.getChilds().size() > 0){
			control = control.getChilds().get(0);
			Object obj = control.doAction("create", actionContext);
			
			if(obj instanceof Control){
				//创建菜单
				actionContext.peek().put("parent", obj);
				//创建菜单
				Thing menu = self.getThing("Menu@0");
				if(menu != null){
					for(Thing child : menu.getChilds()){
						child.doAction("create", actionContext);
						break;
					}
				}
			}
		}else{
			//创建设置按钮
			ActionContext ac = new ActionContext();
			ac.put("parent", composite);
			Thing thing = World.getInstance().getThing("xworker.swt.design.prototype.InjectableComposite/@setButton");	
			thing.doAction("create", ac);
		}
		
		//创建动作容器
		Thing acContainer = self.getThing("ActionContainer@0");
		if(acContainer != null){
			acContainer.doAction("create", actionContext);
		}
		
		actionContext.peek().put("parent", composite);
		//创建子节点，一般是布局等
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		if("_local.test.swt.design.TestInjectableComposite/@InjectableComposite".equals(self.getMetadata().getPath())){
			System.out.println();
		}
		//执行初始化脚本
		for(Thing init : self.getChilds("Init")){
			for(Thing child : init.getChilds()){
				child.getAction().run(actionContext);
			}
		}
		*/
		
	}
	
	public static void copyAttributes(Thing self, Thing uiThing){
		uiThing.set("name", self.get("name"));
		uiThing.set("label", self.get("label"));
		uiThing.set("group", self.get("group"));
		uiThing.set("description", self.get("description"));
		uiThing.set("zh_label", self.get("zh_label"));
		uiThing.set("zh_group", self.get("zh_group"));
		uiThing.set("en_label", self.get("en_label"));
		uiThing.set("en_group", self.get("en_group"));
		uiThing.set("zh_description", self.get("zh_description"));
		uiThing.set("en_description", self.get("en_description"));
		uiThing.setData("thing", self);
	}
	
	public static void setButtonSelection(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		Button button = (Button) event.widget;
		
		Composite parent = button.getParent();
		ActionContext parentContext = (ActionContext) parent.getData("parentContext");
		Thing thing = (Thing) parent.getData("thing");
		
		if(parentContext == null || thing == null){
			//当前事物并不是InjectableComposite或相应变量丢失
			return;
		}
		
		//弹出帮助小精灵，通过帮助小精灵设置子控件
		Action action = World.getInstance().getAction("xworker.ide.worldExplorer.swt.SimpleExplorer/@shell1/@mainComposite/@mainCoolBar1/@mainCollItem/@mainToolBar/@commandAssistorItem/@listeners/@openCommander/@openAssistor");
		action.run(actionContext);
		
		//连接帮助小精灵
		InteractiveUI ui = (InteractiveUI) parent.getData(InteractiveUI.DATA_KEY);
		InteractiveListener interactiveListener = Designer.getInteractiveListener(ui.getListenerName());
		if(interactiveListener != null){
			interactiveListener.connected(ui);
			ui.setInteractiveListener(interactiveListener);						//ui.addListener();
		}
	}
	
	/**
	 * 判断当前可注入界面中是否已经包含了子节点。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static boolean isHasChild(ActionContext actionContext){
		//获取InteractiveUI
		ActionContext p = (ActionContext) actionContext.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		p = (ActionContext) p.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		InteractiveUI ui = (InteractiveUI) p.get("ui");
		if(ui == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		
		//获取InjectableComposite
		Composite parent = (Composite) ui.getControl();
		Thing thing = (Thing) parent.getData("thing");
		
		//移除原来设定的子事物
		Thing control = thing.getThing("Control@0");
		if(control == null){
			return false;
		}else{
			return control.getChilds().size() > 0;
		}
	}
	
	/**
	 * 返回当前的Composite对象。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Composite getComposite(ActionContext actionContext){
		//获取InteractiveUI
		ActionContext p = (ActionContext) actionContext.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		p = (ActionContext) p.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		InteractiveUI ui = (InteractiveUI) p.get("ui");
		if(ui == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		
		//获取InjectableComposite
		Composite parent = (Composite) ui.getControl();
		return parent;
	}
	
	public static Thing getThing(Widget widget){
		Thing thing = (Thing) widget.getData("thing");
		if(thing == null){
			thing = World.getInstance().getThing((String) widget.getData(Designer.DATA_THING));
		}
		
		return thing;
	}
	
	/**
	 * 为InjectableComposite设置菜单。
	 * 
	 * @param menuThing 菜单事物
	 * @param actionContext 上下文
	 */
	public static void setMenu(Thing menuThing, ActionContext actionContext){
		if(menuThing == null){
			throw new ActionException("Child thing is null");
		}
		
		//获取InteractiveUI
		ActionContext p = (ActionContext) actionContext.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		p = (ActionContext) p.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		InteractiveUI ui = (InteractiveUI) p.get("ui");
		if(ui == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		
		//获取InjectableComposite
		Composite parent = (Composite) ui.getControl();
		ActionContext ac = (ActionContext) parent.getData("parentContext");
		Thing thing = (Thing) parent.getData("thing");
		
		//移除原来设定的子事物
		Thing menuThings = thing.getThing("Menu@0");
		if(menuThings == null){
			menuThings = new Thing("xworker.swt.design.InjectableComposite/@Menu");
			thing.addChild(menuThings);
		}
		
		//删除子节点
		if(menuThings.getChilds().size() == 0 || menuThings.getChilds().get(0) != menuThing){
			while(menuThings.getChilds().size() > 0){
				menuThings.removeChild(menuThings.getChildAt(0));
			}
			
			//保存子节点
			Thing dchild = menuThing.detach();
			menuThings.addChild(dchild);
			menuThings.save();
			
			menuThing = dchild;
		}
		
		//让控件生效
		Object parent_ = parent;
		if(parent instanceof Shell){		
		}else{
			//不是Shell的时候，菜单是绑定到第一个子节点上的
			if(parent.getChildren().length > 0){
				parent_ = parent.getChildren()[0];
			}
		}
		Menu menu = parent.getMenu();
		if(menu != null){
			menu.dispose();
		}
				
		ac.peek().put("parent", parent_);
		menu = menuThing.doAction("create", ac);

		parent.layout();
	}
	
	/**
	 * 为InjectableComposite设置一个新的子节点，并刷新界面，如果新子节点是原来的只刷新界面，只能在InteractiveUI中使用。
	 *  
	 * @param child 会先detach然后再加入
	 * @param actionContext
	 */
	public static void setChild(Thing child, ActionContext actionContext){
		if(child == null){
			throw new ActionException("Child thing is null");
		}
		
		//获取InteractiveUI
		ActionContext p = (ActionContext) actionContext.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		p = (ActionContext) p.getObject("parentContext");
		if(p == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		InteractiveUI ui = (InteractiveUI) p.get("ui");
		if(ui == null){
			throw new ActionException("Not undder InteractiveUI");
		}
		
		//获取InjectableComposite
		Composite parent = (Composite) ui.getControl();
		ActionContext ac = (ActionContext) parent.getData("parentContext");
		Thing thing = (Thing) parent.getData("thing");
		
		//移除原来设定的子事物
		Thing control = thing.getThing("Control@0");
		if(control == null){
			control = new Thing("xworker.swt.design.InjectableComposite/@Control");
			thing.addChild(control);
		}
		
		//删除子节点
		if(control.getChilds().size() == 0 || control.getChilds().get(0) != child){
			while(control.getChilds().size() > 0){
				control.removeChild(control.getChildAt(0));
			}
			
			//保存子节点
			Thing dchild = child.detach();
			control.addChild(dchild);
			control.save();
			
			child = dchild;
		}
		
		//让控件生效
		for(Control c : parent.getChildren()){
			c.dispose();
		}
		
		ac.peek().put("parent", parent);
		Object obj = child.doAction("create", ac);
		if(obj instanceof Control){
			//创建菜单
			actionContext.peek().put("parent", obj);
			//创建菜单
			Thing menu = thing.getThing("Menu@0");
			if(menu != null){
				for(Thing menuChild : menu.getChilds()){
					menuChild.doAction("create", actionContext);
					break;
				}
			}
		}
		
		if(parent.getParent() != null){
			parent.getParent().layout(true);
		}
		parent.layout();		
	}
}
