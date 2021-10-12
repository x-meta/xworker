package xworker.swt.design;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.DesignShellActions.Item;
import xworker.swt.design.tools.CTabFolderDesignTools;
import xworker.swt.design.tools.CompositeDesignTools;
import xworker.swt.design.tools.CoolBarDesignTools;
import xworker.swt.design.tools.ExpandBarDesignTools;
import xworker.swt.design.tools.GroupDesignTool;
import xworker.swt.design.tools.SashFormDesignTools;
import xworker.swt.design.tools.ShellDesignTools;
import xworker.swt.design.tools.TabFolderDesignTools;
import xworker.swt.design.tools.ToolBarDesignTools;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtUtils;
import xworker.swt.widgets.ShellCreator;
import xworker.util.StringUtils;
import xworker.util.UtilData;

/**
 * DesignTools是SWT设计器的工具类。
 *
 * @author zyx
 *
 */
public class DesignTools {
	public static final int ABOVE = 1;
	public static final int BELOW = 2;
	public static final int INSIDE = 3;
	public static final int REPLACE = 4;
	public static final int UPDATE = 5;

	/** 控件的工具*/
	private static final Map<Class<?>, IDesignTool<?>> tools = new HashMap<>();
	/** 不能插入子控件的但继承于Composite的类 */
	private static final Map<Class<?>, Class<?>> unInsertables = new HashMap<>();
	/** 可以包含布局的类 */
	private static final Map<Class<?>, Class<?>> hasLayouts = new HashMap<>();
	private static boolean disableMessage = false;
	static{
		tools.put(CTabFolder.class, new CTabFolderDesignTools());
		tools.put(TabFolder.class, new TabFolderDesignTools());
		tools.put(CoolBar.class, new CoolBarDesignTools());
		tools.put(ToolBar.class, new ToolBarDesignTools());
		tools.put(Composite.class, new CompositeDesignTools());
		tools.put(Group.class, new GroupDesignTool());
		tools.put(SashForm.class, new SashFormDesignTools());
		tools.put(ExpandBar.class, new ExpandBarDesignTools());
		tools.put(Shell.class, new ShellDesignTools());

		unInsertables.put(Canvas.class, Canvas.class);
		unInsertables.put(Decorations.class, Decorations.class);
		unInsertables.put(Browser.class, Browser.class);
		unInsertables.put(CBanner.class, CBanner.class);
		unInsertables.put(CCombo.class, CCombo.class);
		unInsertables.put(Combo.class, Combo.class);
		unInsertables.put(DateTime.class, DateTime.class);

		unInsertables.put(Spinner.class, Spinner.class);
		unInsertables.put(Table.class, Table.class);
		unInsertables.put(Tree.class, Tree.class);
		unInsertables.put(ViewForm.class, ViewForm.class);

		try{
			Class<?> cls = Class.forName("org.eclipse.swt.ole.win32.OleClientSite");
			unInsertables.put(cls, cls);
			cls = Class.forName("org.eclipse.swt.ole.win32.OleFrame");
			unInsertables.put(cls, cls);
		}catch(Exception ignored){
		}
		registUnInsertable("org.eclipse.swt.custom.TableTree");
		registUnInsertable("org.eclipse.swt.custom.StyledText");


		hasLayouts.put(Composite.class, Composite.class);
		hasLayouts.put(Shell.class, Shell.class);
		hasLayouts.put(Group.class, Group.class);
	}

	public static boolean isDisableMessage() {
		return disableMessage;
	}

	public static void setDisableMessage(boolean disableMessage) {
		DesignTools.disableMessage = disableMessage;
	}

	/**
	 * 注册继承了Composite，但是不能添加子节点的控件。
	 */
	public static void registUnInsertable(String className) {
		try {
			Class<?> cls = Class.forName(className);
			unInsertables.put(cls, cls);
		}catch(Exception ignored) {
		}
	}

	public static void registUnInsertable(Class<?> cls) {
		unInsertables.put(cls, cls);
	}

	/**
	 * 注册一个设计工具。
	 */
	public static void registDesignTool(Class<?> cls, IDesignTool<?> tool) {
		tools.put(cls, tool);
	}

	/**
	 * 注册一个继承于Composite的容器，使用hasLayout参数指明是否有布局。
	 */
	public static void registContainer(Class<? extends Composite> cls, boolean hasLayout) {
		if(hasLayout) {
			hasLayouts.put(cls, cls);
		}else {
			unInsertables.put(cls, cls);
		}
	}

	/**
	 * 返回当前控件是否有布局。
	 */
	public static boolean hasLayout(Control control){
		if(control == null){
			return false;
		}

		Class<?> ctrClass = control.getClass();
		return hasLayouts.get(ctrClass) != null;
	}

	public static void addToParentThing(Thing parentThing, Thing itemThing, Thing controlThing, int actionType){
		if(controlThing == null){
			parentThing.addChild(itemThing);
		}else{
			int index = parentThing.getChilds().indexOf(controlThing);

			if(index != -1){
				if(actionType == REPLACE){
					parentThing.removeChild(index);
				}else if(actionType == BELOW){
					index++;
				}
				parentThing.addChild(itemThing, index);
			}else{
				parentThing.addChild(itemThing);
			}
		}
		parentThing.save();
	}

	/**
	 * 返回当前控件是否是SWT的容器。
	 */
	public static boolean isContainer(Control control){
		if(control == null){
			return false;
		}

		//在Creator下创建的，默认也不能添加子节点
		if(Designer.getCreator(control) != null) {
			return false;
		}

		if(!(control instanceof Composite)){
			return false;
		}else{
			return unInsertables.get(control.getClass()) == null;
		}
	}

	public static boolean checkInsertAble(Composite parent, Control control){
		if(parent == null){
			showErrorMessage("lang:d=不能操作，当前控件是Shell。" +
					"&en=Cannot operate. The current control is a Shell.");
			return false;
		}

		Thing parentThing = Designer.getThing(parent);
		Thing controlThing = Designer.getThing(control);
		if(controlThing == null){
			control.dispose();
			SwtUtils.layout(parent);
			return false;
		}

		Thing pthing = controlThing.getParent();
		while(pthing != null){
			if(pthing == parentThing){
				return true;
			}

			pthing = pthing.getParent();
		}

		//System.out.println(pthing);
		//System.out.println(parentThing);
		showErrorMessage("lang:d=不能操作，父控件和子控件不是一个模型。" +
				"&en=Cannot operate. The parent control and child control are not in a same model.");
		return false;
	}

	public static void showErrorMessage(String message){
		if(DesignTools.disableMessage){
			return;
		}

		Shell parent = Designer.getDesignerDialogShell();
		String title = getLangString("lang:d=SWT设计器&en=SWT Deisnger");
		String msg = getLangString(message);

		MessageBox box = new MessageBox(parent, SWT.OK | SWT.ICON_ERROR);
		box.setText(title);
		box.setMessage(msg);
		SwtUtils.openDialog(box, null, Designer.getControlActionContext(parent));
	}

	public static String getLangString(String msg){
		try {
			return StringUtils.getString(msg, null);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 根据当前控件中插入一个新的控件，可以在当前控件之前(ABOVE)、之后(BELOW)、之中（INSIDE)或替换(REPLACE)。
	 *
	 * @param control       当前控件
	 * @param controlThing  子控件
	 * @param actionType    插入类型，ABOVE、BELOW、INSIDE、REPLACE
	 * @return 新的控件
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Control insert(Control control, Thing controlThing, int actionType){
		Thing thing = controlThing.detach();
		Composite parent = getRealParent(control);//control.getParent();
		IDesignTool tool = null;
		if(actionType == REPLACE || actionType == ABOVE || actionType == BELOW){
			if(!checkInsertAble(parent, control)){
				return null;
			}

			tool = tools.get(parent.getClass());
			if(!checkTool(tool, parent)){
				return null;
			}
		}

		Control newControl = null;
		if(actionType == REPLACE){
			SwtUtils.trimLayoutData(parent, thing);
			newControl = tool.replace(parent, control, thing);
		}else if(actionType == ABOVE || actionType == BELOW ){
			SwtUtils.trimLayoutData(parent, thing);
			newControl = tool.insertAboveOrBelow(parent, control, thing, actionType);
		}else if(actionType == INSIDE){
			if(!isContainer(control)){
				showErrorMessage("lang:d=当前控件不是容器。" +	"&en=Current control is not a container.");
				return null;
			}
			tool = tools.get(control.getClass());
			if(!checkTool(tool, parent)){
				return null;
			}

			SwtUtils.trimLayoutData((Composite) control, thing);
			newControl = tool.insert((Composite) control, thing);
		}

		return newControl;
		/*if(newControl != null){
			Designer.setCurrentDesignControl(newControl);
		}*/
	}

	/**
	 * 把源控件移动到目标控件的相对位置处。
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void move(Control sourceControl, Control targetControl, int actionType, boolean copy, ActionContext parentContext){
		if(sourceControl instanceof Shell){
			showErrorMessage("lang:d=不能移动Shell。" +
					"&en=Can not move Shell.");
			return;
		}

		Thing sourceThing = Designer.getThing(sourceControl);
		Thing thing = sourceThing.detach();

		Composite parent = getRealParent(targetControl);//.getParent();
		SwtUtils.trimLayoutData(parent, thing);

		IDesignTool tool = null;
		if(actionType == REPLACE || actionType == ABOVE || actionType == BELOW){
			if(!checkInsertAble(parent, targetControl)){
				return;
			}

			tool = tools.get(parent.getClass());
			if(!checkTool(tool, parent)){
				return;
			}
		}

		Control newControl = null;
		if(actionType == REPLACE){
			newControl = tool.replace(parent, targetControl, thing);

		}else if(actionType == ABOVE || actionType == BELOW ){
			newControl = tool.insertAboveOrBelow(parent, targetControl, thing, actionType);
		}else if(actionType == INSIDE){
			if(!isContainer(targetControl)){
				showErrorMessage("lang:d=当前控件不是容器。" +
						"&en=Current control is not a container.");
				return;
			}
			tool = tools.get(targetControl.getClass());
			if(!checkTool(tool, parent)){
				return;
			}
			newControl = tool.insert((Composite) targetControl, thing);
		}

		//移动返回主页
		//parentContext.g().put("toolActions", null);
		//ActionContainer actions = parentContext.getObject("actions");
		//actions.doAction("reInit");
		Shell shell = parentContext.getObject("shell");
		if(!shell.isDisposed()){
		    shell.forceActive();
		}

		if(!copy){
			remove(sourceControl, false);
		}

		if(newControl != null){
			Designer.setCurrentDesignControl(newControl);
		}
	}

	/**
	 * 通过事物更新所有已经在运行的窗口中使用该事物创建的控件。
	 */
	public static void updateAllControls(Thing controlThing) {
		List<Widget> widgets = Designer.getWidgets(controlThing.getMetadata().getPath());
		if(widgets != null) {
			for(Widget widget : widgets) {
				if(widget == null || widget.isDisposed()) {
					continue;
				}

				//不要更新编辑器的
				if(widget instanceof Control) {
					final Control control = (Control) widget;
					widget.getDisplay().asyncExec(() -> {
						try {
							if(!UtilData.isTrue(control.getData(Designer.DATA_ISATTRIBUTE))) {
								update(control, false);
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
		}
	}

	/**
	 * 更新指定的控件，获取控件事物重新创建或更新控件。
	 */
	public static void update(Control control) {
		update(control, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void update(Control control, boolean setCurrentControl){
		if(control instanceof Shell){
			//Shell特殊处理
			Thing thing = Designer.getThing(control);
			ActionContext  actionContext = Designer.getActionContext(control);
			actionContext.peek().put("self", thing);
			actionContext.peek().put("shell", control);
			ShellCreator.update(actionContext);
			if(setCurrentControl) {
				Designer.setCurrentDesignControl(control);
			}
			return;
		}

		Composite parent = getRealParent(control);//.getParent();
		if(parent == null){
			showErrorMessage("lang:d=不能操作，当前控件是Shell。" +
					"&en=Cannot operate. The current control is a Shell.");
			return;
		}

		IDesignTool tool = tools.get(parent.getClass());
		if(!checkTool(tool, parent)){
			return;
		}

		Control newControl = tool.update(parent, control);
		if(newControl != null && setCurrentControl){
			Designer.setCurrentDesignControl(newControl);
		}
	}

	public static void remove(Control control){
		remove(control, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void remove(Control control, boolean autoSelectNextControl){
		if(control instanceof Shell){
			//Shell特殊处理
			Thing thing = Designer.getThing(control);
			Thing parentThing = thing.getParent();
			if(parentThing != null){
				parentThing.removeChild(thing);
				parentThing.save();
			}else{
				thing.remove();
			}
			control.dispose();
			return;
		}

		Composite parent = getRealParent(control);//control.getParent();
		if(!checkInsertAble(parent, control)){
			return;
		}

		IDesignTool tool = tools.get(parent.getClass());
		if(!checkTool(tool, parent)){
			return;
		}

		Control newControl = tool.remove(parent, control);
		if(newControl != null && autoSelectNextControl){
			Designer.setCurrentDesignControl(newControl);
		}
	}

	public static boolean checkTool(IDesignTool<?> tool, Composite parent){
		if(tool == null){
			String name = parent.getClass().getSimpleName();
			showErrorMessage("lang:d=不支持对" + name+"的操作。" +
					"&en=Not support operations for " + name + ".");
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 返回一个控件的真实的父控件，排除自定义组件。
	 *
	 * @param control 控件
	 * @return 父控件
	 */
	public static Composite getRealParent(Control control){
		if(control == null || control.isDisposed()) {
			return null;
		}

		Control creatorControl = Designer.getCreatorControl(control);
		if(creatorControl != null){
			control = creatorControl;
		}

		Composite parent = control.getParent();
		if(parent == null) {
			return null;
		}

		//DATA_DESIGNER_REAL_PARENT不知道在哪里设置的
		Composite p = (Composite) parent.getData(Designer.DATA_DESIGNER_REAL_PARENT);
		if(p != null) {
			return p;
		}else {
			return parent;
		}
	}

	/**
	 * 返回一个Control已经注册事件列表。
	 */
	public static List<String> getRegistedListeners(Control control){
		List<String> listeners = new ArrayList<>();
		for(String name : SwtUtils.getListenerNames()) {
			int type = SwtUtils.getSWT(name);
			for(Listener lis : control.getListeners(type)) {
				if(lis instanceof SwtListener) {
					listeners.add(name);
					break;
				}
			}
		}

		return listeners;
	}

	public static List<String> getUnregistedListeners(Control control){
		List<String> listeners = new ArrayList<>();
		for(String name : SwtUtils.getListenerNames()) {
			int type = SwtUtils.getSWT(name);
			boolean have = false;
			for(Listener lis : control.getListeners(type)) {
				if(lis instanceof SwtListener) {
					have = true;
					break;
				}
			}

			if(!have) {
				listeners.add(name);
			}
		}

		return listeners;
	}


	/**
	 * 指定一个控件，返回相关的控件列表，其中列表是树形结构的。
	 *
	 * 相关的控件是指和指定的控件是由同一个事物模型创建的。
	 */
	public static List<Item> getControlItems(Control control){
		//初始化条目
		String thingPath = (String) control.getData("_designer_thingPath");
		Thing thing = World.getInstance().getThing(thingPath);
		if(thing == null){
			return Collections.emptyList();
		}
		Thing rootThing = thing.getRoot();
		Control rootControl = DesignShellActions.getRootControl(control, rootThing);

		List<Item> items = new ArrayList<>();
		DesignShellActions.initItem(rootControl, items, rootThing);

		return  items;
	}

	/**
	 * 返回控件所在变量上下文。
	 */
	public static ActionContext getActionContext(Control control) {
		return Designer.getActionContext(control);
	}

	/**
	 * 返回控件是否是模型的属性编辑器中属性对应的控件，如果是那么这里控件一般是不可设计的，因为这些控件一般是自动生成的。
	 */
	public static boolean isAttribute(Control control) {
		return Designer.isAttribute(control);
	}

	/**
	 * 返回是否是一个组件下的控件。组件是XWorker的复合控件模型创建的，一般不能修改，因为修改后会影响所有的实例，因此通常是禁止修改的。
	 */
	public static boolean isComponent(Control control) {
		return Designer.isUnderCreator(control);
	}

	public static Thing getLayoutThing(Control control) {
		if(!DesignTools.hasLayout(control)) {
			return null;
		}

		Thing thing = Designer.getThing(control);
		return thing.getThing("Layout@0");
	}

	/**
	 * 改变布局。
	 *
	 * @param control 控件
	 * @param newLayout 可以是FillLayout、GridLayout、RowLayout、FormLayout和StackLayout等。
	 */
	public static void changeLayout(Control control, String newLayout) {
		if(!DesignTools.hasLayout(control)) {
			return;
		}

		Composite composite = (Composite) control;
		Layout layout = composite.getLayout();
		Thing thing = Designer.getThing(control);
		if(layout != null) {
			if(layout.getClass().getSimpleName().equals(newLayout)) {
				//相同，没必要切换布局
				return;
			}

			//移除原来的Layout和子节点的Layout
			Thing layoutThing = getLayoutThing(control);
			if(layoutThing != null) {
				thing.removeChild(layoutThing);
			}

			for(Control childControl : composite.getChildren()) {
				Thing childThing = Designer.getThing(childControl);
				if(childThing != null) {
					for(Thing child : childThing.getChilds()) {
						if(child.isThingByName("LayoutData")) {
							childThing.removeChild(child);
							break;
						}
					}
				}
				childControl.setLayoutData(null);
			}
		}

		//设置新的布局
		Thing layoutThing = null;
		if("FillLayout".equals(newLayout)) {
			layoutThing = new Thing("xworker.swt.Layouts/@FillLayout");
			thing.addChild(layoutThing);
		}else if("GridLayout".equals(newLayout)) {
			layoutThing = new Thing("xworker.swt.layout.GridLayout");
			thing.addChild(layoutThing);
		}else if("RowLayout".equals(newLayout)) {
			layoutThing = new Thing("xworker.swt.Layouts/@RowLayout");
			thing.addChild(layoutThing);
		}else if("StackLayout".equals(newLayout)) {
			layoutThing = new Thing("xworker.swt.custom.StackLayout");
			thing.addChild(layoutThing);
		}else if("FormLayout".equals(newLayout)) {
			layoutThing = new Thing("xworker.swt.layout.FormLayout");
			thing.addChild(layoutThing);
		}

		if(layoutThing != null) {
			ActionContext actionContext = Designer.getActionContext(control);
			layoutThing.doAction("create", actionContext, "parent", control);

			if("GridLayout".equals(newLayout)) {
				//初始化子节点的LayoutData，先让它能够显示出来
				for(Control childControl : composite.getChildren()) {
					DesignTools.getLayoutDataThing(childControl);
				}
			}else {
				thing.save();
			}
		}

		composite.layout();
	}

	public static Thing getLayoutDataThing(Control control) {
		Thing thing = Designer.getThing(control);
		Composite parent = control.getParent();
		if(!DesignTools.hasLayout(parent)) {
			return null;
		}

		Thing layoutData = thing.getThing("LayoutData@0");
		if(layoutData == null) {
			//如果没有创建一个
			Layout layout = parent.getLayout();
			if(layout instanceof GridLayout) {
				layoutData = new Thing("xworker.swt.layout.LayoutDatas/@GridData");
			}else if(layout instanceof RowLayout) {
				layoutData = new Thing("xworker.swt.layout.LayoutDatas/@RowData");
			}else if(layout instanceof FormLayout) {
				layoutData = new Thing("xworker.swt.layout.LayoutDatas/@FormData");
			}

			if(layoutData != null) {
				thing.addChild(layoutData);
				thing.save();

				ActionContext ac = Designer.getActionContext(control);
				layoutData.doAction("create", ac, "parent", control);
			}
		}

		return layoutData;
	}
}
