package xworker.swt.design;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.swt.ActionContainer;
import xworker.util.ThingGroup;

public class DesignToolDialog {
	public static boolean isDifferentDesigner(Control control, Control newControl){
		boolean oldIsAttribute = Designer.isAttribute(control);
		boolean newIsAttribute = Designer.isAttribute(newControl);
		if(oldIsAttribute != newIsAttribute){
			//一个是属性，另一个不是
			return false;
		}
		
		//Thing oldCreator = Designer.getCreator(control);
		Thing newCreator = Designer.getCreator(newControl);
		Thing thing = Designer.getThing(newControl);
		if(newCreator != null && newCreator != thing){
			//如果是组合控件(newCreator != null)且不是组合控件的根，那么被认为是不同的设计器
			return false;
		}
		
		//应该是同一个设计器
		return true;
	}
	
	public static void init(ActionContext actionContext){
		Shell shell = actionContext.getObject("shell");
		shell.forceActive();
		
		Control control = (Control) actionContext.get("newControl");
		if(control == null){
			return;
		}
		String thingPath = (String) control.getData(Designer.DATA_THING);
		Thing thing = World.getInstance().getThing(thingPath);
		if(thing == null){
			return;
		}
		ActionContext controlContext = (ActionContext) control.getData("_designer_actionContext");
		
		StackLayout topMainCompositeStackLayout = actionContext.getObject("topMainCompositeStackLayout");
		Composite operationComposite = actionContext.getObject("operationComposite");
		//System.out.println("newControl=" + control);
		//System.out.println("oldControl=" + actionContext.get("control"));
		Thing rootCreator = Designer.getCreator(control);
		Control rootControl = Designer.getCreatorControl(control);
		Thing creator = Designer.getCreator(control, -1);
		Control creatorControl = Designer.getCreatorControl(control, -1);
		boolean isAttribute = Designer.isAttribute(control);
		//System.out.println(rootCreator);
		if(topMainCompositeStackLayout.topControl != operationComposite) { 
				//&& isDifferentDesigner((Control) actionContext.get("control"), control)){
			//已经有操作正在处理，先判断那个操作是否处理新的控件
            ActionContainer toolActions = actionContext.getObject("toolActions");
            if(toolActions != null){
            	Object obj = toolActions.execute("handleNewControl", "newControl", control, 
            			"newControlThing", thing, "newControlContext", controlContext, 
            			"rootCreator", rootCreator, "rootControl", rootControl,
            			"isAttribute", isAttribute, "creator", creator, "creatorControl", creatorControl);
            	if(UtilData.isTrue(obj)){
            		//已有的操作接受了新的控件
            		return;
            	}
            }
		}
			
		actionContext.peek().put("control", control);
		reinit(actionContext);
	}
	
	/**
	 * 为设计对话框设置新的控件，仅在变量上下文中保存。
	 * 
	 * @param actionContext
	 * @param control
	 */
	public static void setNewControl(ActionContext actionContext, Control control){
		String thingPath = (String) control.getData(Designer.DATA_THING);
		Thing thing = World.getInstance().getThing(thingPath);
		if(thing == null){
			return;
		}
		
		actionContext.g().put("controlThing", thing);
		actionContext.g().put("control", control);
		actionContext.g().put("rootCreator", Designer.getCreator(control));
		actionContext.g().put("rootControl", Designer.getCreatorControl(control));
		actionContext.g().put("creator", Designer.getCreator(control, -1));
		actionContext.g().put("creatorControl", Designer.getCreatorControl(control, -1));
		actionContext.g().put("isAttribute", Designer.isAttribute(control));
		actionContext.g().put("controlContext", control.getData(Designer.DATA_ACTIONCONTEXT));
		
		Shell shell = actionContext.getObject("shell");
		if(shell == null) {
			shell = Display.getCurrent().getActiveShell();
		}
		Thing currentTool = actionContext.getObject("currentTool");
		setTitle(shell, currentTool, thing);
	}
	
	public static void setTitle(Shell shell, Thing toolThing, Thing controlThing) {
		String title = "";
		if(toolThing != null) {
			title = toolThing.getMetadata().getLabel() + " - ";
			int index1 = title.indexOf("(");
			int index2 = title.indexOf(")");
			if(index2 > index1  && index1 > -1) {
				title = title.substring(0, index1) + title.substring(index2 + 1, title.length() );
			}
		}
		title = title + controlThing.getMetadata().getName();
		title = title + "(" + controlThing.getDescriptor().getMetadata().getLabel() + ")";
		shell.setText(title);
	}
	
	public static void reinit(ActionContext actionContext){	
		Shell shell = actionContext.getObject("shell");
		Control control = (Control) actionContext.get("control");
		if(control.isDisposed()){
			shell.dispose();
			return;
		}
		String thingPath = (String) control.getData(Designer.DATA_THING);
		Thing thing = World.getInstance().getThing(thingPath);
		Thing creator = Designer.getCreator(control);
		if(thing == null && creator == null){
			return;
		}
		
		//对话框改造后使用Toolbar的菜单，会调用createOperationGroupsMenu方法，里面用到parentContext
		//未兼容设置该变量
		if(actionContext.get("parentContext") == null) {
			actionContext.put("parentContext", actionContext);
		}
		
		ActionContainer actions = actionContext.getObject("actions");
		actions.doAction("setNewControl", actionContext, "newControl", control);
		StackLayout topMainCompositeStackLayout = actionContext.getObject("topMainCompositeStackLayout");
		
		//System.out.println(things);
		Composite operationGroupComposite = (Composite) actionContext.get("operationGroupComposite"); 
		//for(Control child : operationGroupComposite.getChildren()){
		//	child.dispose();
		//}
		
		//获取注册的操作列表和分组                                           
		Thing registThing = null;
		if(Designer.isAttribute(control) && creator != null){
			registThing = new Thing(creator.getDescriptor().getMetadata().getPath());
			registThing.put("extends", thingPath + "," + creator.getMetadata().getPath() + ",xworker.lang.MetaDescriptor3/@attribute");
		}else if(creator != null && Designer.isCreatorRoot(control) && !Designer.isUnderCreator(control)){
			//组合控件的根部
			registThing = new Thing(creator.getDescriptor().getMetadata().getPath());
			registThing.put("extends", "xworker.swt.widgets.Widget," + creator.getMetadata().getPath());
		}else if(creator != null){
			registThing = creator;
		}else if(Designer.isAttribute(control)){
			registThing = new Thing();
			registThing.put("extends", thingPath + ",xworker.lang.MetaDescriptor3/@attribute");
		}else{
			registThing = thing;
		}
		
		List<Thing> things = actions.execute("getRegistThings", "registorThing", registThing, "registType", "design");
		if(things == null) {
			things = new ArrayList<Thing>();
		}
		actionContext.g().put("registThing", registThing);
		
		//设计工具是都需要的，因此它没有注册，直接添加的
		things.add(World.getInstance().getThing("xworker.swt.design.designtools.CreateDesignTool"));
		
		//SwtCreator所创建的组合控件
		if(creator != null){
			things.add(World.getInstance().getThing("xworker.swt.design.designtools.SwtCreatorControlTools"));
		}
		
		ThingGroup thingGroup = new ThingGroup();
		for(Thing t : things) {
			for(Thing child : t.getChilds()) {
				if(child.getBoolean("visible")) {
					thingGroup.addThing(child);
				}
			}
		}
		
		thingGroup.sort();
		List<ThingGroup> groups = thingGroup.getChilds();//XWorkerUtils.getThingGroups(things);
		actionContext.g().put("thingGroups", groups);				
		
		//createOperationGroups(groups, operationGroupComposite, actionContext);
		operationGroupComposite.layout(true);	
				
		shell.setVisible(false);
		Control operationComposite = (Control) actionContext.get("operationComposite");
		if(topMainCompositeStackLayout.topControl != operationComposite){
			topMainCompositeStackLayout.topControl = operationComposite;
			((Composite) actionContext.get("topMainComposite")).layout(true);
			
			Point oldSize = (Point) shell.getData("oldSize");
			if(oldSize != null){		    
			    shell.setSize(oldSize);
			}
			
			Point oldLocation = (Point) shell.getData("oldLocation");
			if(oldLocation != null){		    
			    shell.setLocation(oldLocation);		    
			}
		}
		
		//执行默认打开的tools
		Thing tools = actionContext.getObject("tools");
		if(tools != null) {
			ActionContext ac = new ActionContext();
			ac.put("parent", shell);
			ac.put("parentContext", actionContext);
			tools.doAction("run", ac);
		}else {
			shell.setSize(250,85);
		}
		
		//迫使ScrollComposite重新布局			
		//shell.pack();
		
		/*
		Point shellSize = shell.getSize();
		shell.setSize(shellSize.x - 1, shellSize.y - 1);
		shell.setSize(shellSize);
		*/

		shell.setVisible(true);
	}
	
	public static Menu createOperationGroupsMenu(Control control, ActionContext actionContext) {
		ActionContext parentContext = actionContext.getObject("parentContext");
		List<ThingGroup> groups = parentContext.getObject("thingGroups");
		if(groups == null || groups.size() == 0) {
			return null;
		}
		
		//先销毁原来的菜单
		if(control.getMenu() != null && control.getMenu().isDisposed() == false) {
			control.getMenu().dispose();
		}
		
		//创建新的菜单
		Thing quickMenu = new Thing("xworker.swt.xwidgets.QuickMenu");
		for(ThingGroup group : groups) {
			quickMenu.addChild(getMenuItem(group));
			/*
			Thing menu = new Thing("xworker.swt.xwidgets.QuickMenu/@Menu");
			menu.put("name", group.getGroup());
			menu.put("label", group.getGroup());
			
			quickMenu.addChild(menu);
			
			for(ThingGroup childGroup : group.getChilds()) {
				Thing thing = childGroup.getThing();
				if(thing != null) {
					if(menu.getChilds().size() > 0) {
						//添加分隔符
						Thing item = new Thing("xworker.swt.xwidgets.QuickMenu/@Menu/@Menu");
						item.set("seperator", "true");
						menu.addChild(item);
					}
					
					for(Thing itemThing : thing.getChilds()) {
						Thing item = new Thing("xworker.swt.xwidgets.QuickMenu/@Menu/@Menu");
						item.set("name", itemThing.getMetadata().getName());
						item.set("label", itemThing.getMetadata().getLabel());
						item.set("thing", itemThing);
						item.set("actionThing", "xworker.swt.design.DesignToolDialog/@topMainComposite/@actions/@toolPopMenuSelection");
						//item.set("actionName", "toolPopMenuSelection");
						
						menu.addChild(item);
					}
				}
			}*/
		}
		
		Menu menu = quickMenu.doAction("create", actionContext);
		return menu;
	}
	
	private static Thing getMenuItem(ThingGroup group) {
		Thing itemThing = group.getThing();		
		Thing item = new Thing("xworker.swt.xwidgets.QuickMenu/@Menu/@Menu");
		if(itemThing != null) {
			item.set("name", itemThing.getMetadata().getName());
			item.set("label", itemThing.getMetadata().getLabel());
			item.set("thing", itemThing);
			item.set("actionThing", "xworker.swt.design.DesignToolDialog/@topMainComposite/@actions/@toolPopMenuSelection");
		}else {
			item.set("label", group.getGroup());
			
			for(ThingGroup child : group.getChilds()) {
				item.addChild(getMenuItem(child));
			}
		}
		
		return item;
	}
	
	public static void createOperationGroups(List<ThingGroup> groups, Composite parent, ActionContext actionContext){
		ActionContext ac = new ActionContext();
		ac.put("parent", parent);
		ac.put("parentContext", actionContext);
		World world = World.getInstance();
		
		Group defaultGroup = null;
		for(ThingGroup group : groups){
			if(group.getThing() != null){
				if(defaultGroup == null){
					ac.put("parent", parent);
					Thing groupThing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@group1");
					defaultGroup = groupThing.doAction("create", ac);
					createGroupItem(defaultGroup, group, true, ac);
				}else{
					createGroupItem(defaultGroup, group, false, ac);
				}
			}else{
				//创建Group
				ac.put("parent", parent);
				Thing groupThing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@group1");
				Group swtGroup = groupThing.doAction("create", ac);
				swtGroup.setText(group.getGroup());
				
				//创建子条目
				int index = 0;
				for(ThingGroup child : group.getChilds()){
					if(child.getThing() == null){
						continue;
					}
					
					createGroupItem(swtGroup, child, index == 0, ac);
					index++;
				}
			}
		}
	}
	
	public static void createGroupItem(Group swtGroup, ThingGroup child, boolean first, ActionContext actionContext){
		World world = World.getInstance();
		Thing thing = child.getThing();
		actionContext.peek().put("parent", swtGroup);
		
		//创建分割
		if(!first){
			Thing sthing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@separator");
			sthing.doAction("create", actionContext);
		}
		
		//标签
		Thing labelThing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@label1");
		Label label = labelThing.doAction("create", actionContext);
		label.setText(thing.getMetadata().getDescription());
		
		//按钮组
		Thing buttonGroupThing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@buttonCompoiste");
		Object buttonGroup = buttonGroupThing.doAction("create", actionContext);
		actionContext.peek().put("parent", buttonGroup);
		
		//创建按钮
		for(Thing cthing : thing.getChilds()){
			if(cthing.getBoolean("visible") == false) {
				continue;
			}
			
			Thing buttonThing = world.getThing("xworker.swt.design.prototype.DesignToolDialogPrototype/@button1");
			Button button = buttonThing.doAction("create", actionContext);
			button.setText(cthing.getMetadata().getLabel());
			button.setData("thing", cthing);
		}
	}
	
	public static void afterApplyControl(Object newControl, ActionContext actionContext){
		ActionContext parentContext = actionContext.getObject("parentContext");
		if(newControl instanceof Control){
			//创建的新的控件，编辑器选择新的控件
			parentContext.put("control", newControl);
			Designer.setCurrentDesignControl((Control) newControl);			
		}else{
			//控件已经发生变化，用户需要重新选择控件，提示
			ActionContainer ac = actionContext.getObject("actions");
			ac.doAction("controlChanged");
		}
	}
	public static void applyControl(final Control control, final Thing thing, final ActionContext pactionContext){
		DesignTools.update(control, false);
		/*
		control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				//ActionContext parentContext = pactionContext.getObject("parentContext");
				ActionContext actionContext = Designer.getActionContext(control);
				Composite parent = control.getParent();
				if(control instanceof Shell){
					Shell shell = (Shell) control.getShell();
		
					actionContext.peek().put("shell", shell);
					actionContext.peek().put("self", thing);
					ShellCreator.update(actionContext);					
				}else if(parent instanceof CoolBar){
					Thing itemThing =  thing;
					CoolBar coolbar = (CoolBar) parent;
					CoolItem coolItem = null;
					for(CoolItem citem : coolbar.getItems()){
						if(citem.getControl() == control){
							coolItem = citem;
							break; 
						}
					}
					
					control.dispose();
					actionContext.peek().put("parent", parent);
					if(coolItem != null){
						Control newControl = (Control) itemThing.doAction("create", actionContext);						
						coolItem.setControl(newControl);
						afterApplyControl(newControl, pactionContext);
					}else{
						Object newControl = itemThing.getParent().doAction("create", actionContext);
						afterApplyControl(newControl, pactionContext);
					}
					
					CoolBarCreator.initCoolBar((CoolBar) parent);
				}else if(parent instanceof TabFolder){
					Thing itemThing =  thing;
					TabFolder tabFolder = (TabFolder) parent;
					TabItem item = null;
					for(TabItem citem : tabFolder.getItems()){
						if(citem.getControl() == control){
							item = citem;
							break; 
						}
					}
					
					control.dispose();
					actionContext.peek().put("parent", parent);
					Control newControl = (Control) itemThing.doAction("create", actionContext);						
					item.setControl(newControl);
					
					afterApplyControl(newControl, pactionContext);
				}else if(parent instanceof CTabFolder){
					Thing itemThing =  thing;
					CTabFolder tabFolder = (CTabFolder) parent;
					CTabItem item = null;
					for(CTabItem citem : tabFolder.getItems()){
						if(citem.getControl() == control){
							item = citem;
							break; 
						}
					}
					
					control.dispose();
					actionContext.peek().put("parent", parent);
					Control newControl = (Control) itemThing.doAction("create", actionContext);						
					item.setControl(newControl);
					
					afterApplyControl(newControl, pactionContext);
				}else if(parent instanceof ToolBar){
					Thing itemThing =  thing;
					ToolBar toolbar = (ToolBar) parent;
					ToolItem toolItem = null;
					for(ToolItem citem : toolbar.getItems()){
						if(citem.getControl() == control){
							toolItem = citem;
							break; 
						}
					}
					
					control.dispose();
					actionContext.peek().put("parent", parent);
					if(toolItem != null){
						Control newControl = (Control) itemThing.doAction("create", actionContext);						
						toolItem.setControl(newControl);
						
						afterApplyControl(newControl, pactionContext);
					}else{
						Object newControl = itemThing.getParent().doAction("create", actionContext);
						afterApplyControl(newControl, pactionContext);
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
						if(parent.getChildren()[i] == control){
							index = i;
							break;
						}
					}
					
					control.dispose();
					actionContext.peek().put("parent", parent);
					
					Control newControl = (Control) thing.doAction("create", actionContext);
					afterApplyControl(newControl, pactionContext);
				
					//如果不是最后一个位置，那么移动到原来的位置
					if(index != -1 && index < childCount - 1){
						Control[] childs = parent.getChildren();
						newControl.moveAbove(childs[index]);						
					}
										
					parent.layout();
					parent.getShell().forceActive();
				}
			}
		});*/
	}
}
