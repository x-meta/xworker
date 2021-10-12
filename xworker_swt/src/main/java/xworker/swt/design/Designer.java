/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.UtilData;

import xworker.command.interactive.InteractiveListener;
import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.swt.util.ResizeUtil;
import xworker.swt.util.SwtUtils;
import xworker.util.IIde;
import xworker.util.XWorkerUtils;

/**
 * SWT设计器。
 * 
 * 如果创建控件的事物的create方法使用了设计器的attach绑定控件，那么改控件可以是设计状态，
 * 当一个控件是设计状态时，使用鼠标中键可以选择控件，并通过鼠标中键的双击打开设计菜单。
 * 
 * @author zyx
 *
 */
public class Designer {
	private static final String TAG = Designer.class.getName();
	/** SWT控件对应的模型 */
	public static final String DATA_THING = "_designer_thingPath";
	public static final String DATA_THING_ENTRY = "_designer_thing_entry__";
	/** 创建SWT控件时的变量上下文 */
	public static final String DATA_ACTIONCONTEXT = "_designer_actionContext";
	/** 是否是表单的属性控件 */
	public static final String DATA_ISATTRIBUTE = "_designer_isAttribute";
	/** 当前是否被选中 */
	public static final String DATA_DESING_SELECTED = "_design_selected";
	/** 是否被标记工具标记了 */
	public static final String DATA_MARKED = "__design_marked__";
	public static final String DATA_MARKED_PAINTED = "__design_marked__painted";
	public static final String DATA_RESIZE_TYPE = "__design_resize__type__";
	public static final String DATA_RESIZEING = "__design_resizeing__";
	/** creator是组合控件模型，控件时组合模型创建 */
	public static final String DATA_CREATOR = "__design_creator__";
	public static final String DATA_CREATOR_ROOT = "__design_creator__root__";
	public static final String DATA_PAINT_LISTENER = "__design_Shell_PaintListener__";
	public static final String DATA_OLD_BACKGROUND = "__design_old_background__";
	public static final String DATA_DESIGN_TARGET = "__design_desing_target__";
	public static final String DATA_DESIGNER = "__xworker_designer_itself__";
	
	public static final String DATA_DESIGNER_REAL_PARENT = "__xworker_designer_real_parent__";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	//private static ActionContainer explorerActions = null;
	private Display explorerDisplay = null; 
	private boolean enabled = false;
	private boolean designEditMode = false;
	/** SWT控件Attach时如果大于0，那么先sleep，为测试用 */
	private long attachSleepTime = 0;
	private final DesignListener listener = new DesignListener();
	/** 所有的控件都保存在controlMap中，在销毁时移除 */
	private final Map<String, List<Widget>> controlMap = new java.util.concurrent.ConcurrentHashMap<>();
	private final List<Control> markedControls = new ArrayList<>();
	/** 标记提示的窗口 */
	private Shell markTooltipShell = null;
	
	private static final byte BOTTOM = 1;
	private static final byte UP = 2;
	private static final byte RIGHT = 3;
	private static final byte LEFT = 4;
	/** 等待交互的UI监听器 */
	private final Map<String, List<InteractiveListener>> interactiveListeners = new HashMap<>();
	/** SWT创建者的栈，创建者是创建SWT时的事物，一般是没有的，但是一些通过模型创建的复合SWT组件，
	 * 创建者则是这些模型。使用创建者是因为创建者下的模型有可能是动态生成的，并不能在运行时不能通过直接修改
	 * SWT控件的方式来设计它。
	 */
	private static final ThreadLocal<Stack<String>> creators = new ThreadLocal<>();
	
	private static final Designer defaultDesigner = new Designer();
	
	private Designer() {
		//目前设计器需要XWorker的环境来运行
		try {
			if(World.getInstance().getThing("xworker.ide.worldexplorer.swt.SimpleExplorerRunner") == null) {
				enabled = false;
			}
		}catch(Exception e) {
			enabled = false;
		}
	}
	
	public static Designer getDesigner() {
		Designer designer;
		if(XWorkerUtils.isRWT()) {
			designer = (Designer) XWorkerUtils.getRWTAttribute(DATA_DESIGNER, null);
			if(designer == null) {
				designer = new Designer();
				XWorkerUtils.setRWTAttribute(DATA_DESIGNER, designer, null);
			}
			
		}else {
			designer = defaultDesigner;
		}

		
		return designer;
	}
	
	/**
	 * 返回第一个可以被设计的Control，如果当前Control不是，那么试图判断其父Contorl。
	 */
	public static Control getDesignableControl(Control control) {
		if(control == null) {
			return null;
		}
		
		//是否是通过模型创建的
		Thing thing = Designer.getThing(control);
		if(thing == null) {
			return getDesignableControl(control.getParent());
		}
		
		//是否是属性
		boolean isAttribute = Designer.isAttribute(control);
		if(isAttribute) {
			return getDesignableControl(control.getParent());
		}
		
		//是否是组合控件
		Thing creator = Designer.getCreator(control);
		if(creator != null) {
			return getDesignableControl(control.getParent());
		}
		
		return control;
	}
	
	public static void registInteractiveListener(String name, InteractiveListener interactiveListener){
		if(interactiveListener == null){
			return;
		}
		
		if(name == null){
			name = "";
		}
		
		Designer designer = getDesigner();

		List<InteractiveListener> listeners = designer.interactiveListeners.computeIfAbsent(name, k -> new ArrayList<>());

		if(!listeners.contains(interactiveListener)) {
			listeners.add(interactiveListener);
		}
	}
		
	public static List<InteractiveListener> getInteractiveListeners(String name){
		if(name == null){
			name = "";
		}
		
		Designer designer = getDesigner();
		return designer.interactiveListeners.get(name);
	}
	
	private static final Thread markControlThread = new Thread(() -> {
		while(true){
			try{
				Designer designer = Designer.getDesigner();
				synchronized(designer.markedControls){
					for(int i=0;i<designer.markedControls.size(); i++){
						final Control control = designer.markedControls.get(i);
						if(!control.isDisposed()){
							control.getDisplay().asyncExec(new Runnable(){
								public void run(){
									if(!control.isDisposed()){
										//control.getDisplay().update();
										control.redraw();
									}
								}
							});
						}
					}
				}

				//未知原因controlMap中有些disposed的控件没有移除，这里加入移除代码
				//System.out.println("check control map");
				for(String key : designer.controlMap.keySet()) {
					List<Widget> widgets = designer.controlMap.get(key);
					for(int i=0; i<widgets.size(); i++) {
						if(widgets.get(i) == null || widgets.get(i).isDisposed()) {
							widgets.remove(i);
							i--;
						}
					}
				}
				Thread.sleep(1000);
			}catch(Throwable t){
				Executor.error(TAG, "Mark control thread error", t);
			}

		}
	}, "designer control thread");
	static{
		//RWT模式暂时不支持
		if(!XWorkerUtils.isRWT()) {
			markControlThread.setDaemon(true);
			markControlThread.start();
		}
	}
	
	/**
	 * 慢动作演示SWT创建过程用，用了最后记得设置为0。
	 */
	public static void setAttachSleepTime(long attachSleepTime) {
		Designer designer = Designer.getDesigner();
		designer.attachSleepTime = attachSleepTime;
	}

	/**
	 * 返回是否是设计编辑模型，默认false。
	 */
	public static boolean isDesignEditMode(){
		Designer designer = Designer.getDesigner();
		return designer.designEditMode;
	}
	
	public static Shell getDesignerDialogShell(){
		Designer designer = Designer.getDesigner();
		return designer.listener.designDialog.shell;
	}
	/**
	* 设计编辑模型。
	 */
	public static void setDesignEditMode(boolean designEditMode){
		Designer designer = Designer.getDesigner();
		designer.designEditMode = designEditMode;
	}
	
	/**
	 * 让指定的Control变得可见。
	 * 
	 * @param control 控件
	 */
	public static void setVisible(Control control) {
		Composite parent = control.getParent();
		if(parent instanceof ScrolledComposite) {
			ScrolledComposite sc = (ScrolledComposite) parent;
			sc.showControl(control);
		}else if(parent instanceof CTabFolder) {
			CTabFolder tab = (CTabFolder) parent;
			for(CTabItem item : tab.getItems()) {
				if(item.getControl() == control) {
					SwtUtils.setSelection(item);
					break;
				}
			}
		}else if(parent instanceof TabFolder) {
			TabFolder tab = (TabFolder) parent;
			for(TabItem item : tab.getItems()) {
				if(item.getControl() == control) {
					SwtUtils.setSelection(item);
					break;
				}
			}
		}else if(parent instanceof Shell) {
			control.setVisible(true);
		}
		
		if(parent != null) {
			setVisible(parent);
		}
	}
	
	/**
	 * 设置当前正在设计的控件。
	 */
	public static void setCurrentDesignControl(Control control){
		Designer designer = Designer.getDesigner();
		designer.listener.setCurrentControL(control);
	}

	public static Control getCurrentDesignControl(){
		return Designer.getDesigner().listener.getCurrentControl();
	}
		
	/**
	 * 绑定设计器到控件。
	 * 
	 * @param control 控件
	 * @param thingPath 创建控件的事物路径
	 * @param actionContext 创建控件时使用的动作上下文
	 */
	public static void attach(Control control, String thingPath, ActionContext actionContext){
		attach(control, thingPath, actionContext, false);
	}
	
	public static void attachCreator(Control control, String thingPath, ActionContext actionContext){
		attach(control, thingPath, actionContext, false, true);
	}
	
	public static void attachCreator(Control control, String thingPath, boolean createRoot, ActionContext actionContext){
		attach(control, thingPath, actionContext, false, createRoot);
	}
	
	public static void attach(Item item, String thingPath, ActionContext actionContext){
		item.setData(DATA_THING, thingPath);
		item.setData(DATA_ACTIONCONTEXT, actionContext);
		item.setData(DATA_ISATTRIBUTE, false);
		item.setData(DATA_CREATOR, peekCreatorPath());
	}
	
	public static ActionContext getControlActionContext(Control control){
		return (ActionContext) control.getData(DATA_ACTIONCONTEXT);
	}
	
	public static boolean isControlAttribute(Control control){
		return (Boolean)  control.getData(DATA_ISATTRIBUTE);
	}
	
	public static void pushCreator(Thing creator){
		Stack<String> stack = creators.get();
		if(stack == null){
			stack = new Stack<>();
			creators.set(stack);
		}
		
		stack.push(creator.getMetadata().getPath());
	}
	
	public static Thing popCreator(){
		Stack<String> stack = creators.get();
		if(stack != null){
			return World.getInstance().getThing(stack.pop());
		}else{
			return null;
		}		
	}
	
	public static Thing peekCreator(){
		Stack<String> stack = creators.get();
		if(stack != null && !stack.empty()){
			return World.getInstance().getThing(stack.peek());
		}else{
			return null;
		}
	}
	
	/**
	 * 获取当前Creator的列表，第一个是最先的Creator，如有多个使用英文逗号分隔。
	 */
	public static String peekCreatorPath(){
		Stack<String> stack = creators.get();
		if(stack == null || stack.empty()) {
			return null;
		}else {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<stack.size(); i++) {
				if(i > 0) {
					sb.append(",");
				}
				sb.append(stack.get(i));
			}
			
			return sb.toString();
		}
		/*
		Thing creator = peekCreator();
		if(creator != null){
			return creator.getMetadata().getPath();
		}else{
			return null;
		}*/
	}
	
	public static void attach(Control control, String thingPath, ActionContext actionContext, boolean isAttribute){
		attach(control, thingPath, actionContext, isAttribute, false);
	}
		
	public static void attach(Control control, String thingPath, ActionContext actionContext, boolean isAttribute, boolean creatorRoot){
		Designer designer = Designer.getDesigner();
		/*
		由于一些绑定已经在其它非设计领域也被用到了，所以下面的方法保留执行，不受enabled的影响。
		if(!designer.enabled){			
			//return;
		}*/
		
		try{
			if(control == null || thingPath == null){
				Executor.warn(TAG, "Deisgner can not attach to null control or null thingPath");
				return;
			}
			
			if(Designer.getThing(control) == null){
				if(!SwtUtils.isRWT()) {
					control.addPaintListener(designer.listener);
					control.addMouseTrackListener(designer.listener);
				}
				
				control.addMouseListener(designer.listener);
				
				control.addKeyListener(designer.listener);
				/*
				有严重bug（dragOver模拟插入，控件布局改变，产生不可控连锁反应），取消
				try {
					DragSource dragSource = new DragSource(control, DND.DROP_MOVE | DND.DROP_COPY);
					dragSource.setTransfer(TextTransfer.getInstance());
					dragSource.addDragListener(designer.listener);

					DropTarget dragTarget = new DropTarget(control, DND.DROP_MOVE | DND.DROP_COPY);
					dragTarget.setTransfer(TextTransfer.getInstance());
					dragTarget.setDropTargetEffect(new DesignDropTargetEffect(control));
					dragTarget.addDropListener(designer.listener);
				}catch(Throwable ignored){
				}*/
			}
			/*
			if(control.getShell().getData(Designer.DATA_PAINT_LISTENER) == null){
				Shell shell = control.getShell();
				shell.addPaintListener(listener);
				shell.setData(Designer.DATA_PAINT_LISTENER, listener);
			}*/
			
			control.setData(DATA_THING, thingPath);
			control.setData(DATA_THING_ENTRY, new ThingEntry(World.getInstance().getThing(thingPath)));
			control.setData(DATA_ACTIONCONTEXT, actionContext);
			control.setData(DATA_ISATTRIBUTE, isAttribute);
			Stack<String> creatStack = creators.get();
			if(creatorRoot && (creatStack == null || creatStack.size() == 0)){
				//创建的根目前只设置一个
				control.setData(DATA_CREATOR, thingPath);
				control.setData(DATA_CREATOR_ROOT, creatorRoot);
			}else{
				control.setData(DATA_CREATOR, peekCreatorPath());
			}

			List<Widget> ctls = designer.controlMap.computeIfAbsent(thingPath, k -> new ArrayList<>());

			boolean have = false; //避免重复插入，虽然应该不可能
			synchronized(ctls){
				for(int i=0; i < ctls.size(); i++){
					if(ctls.get(i) == control){
						have = true;
						break;
					}
				}
				
				if(!have){
					ctls.add(control);
				}
			}		
			
			if(designer.attachSleepTime > 0){
				control.getShell().layout();
				if(control.getParent() != null){
					control.getParent().layout();
				}
				Thread.sleep(designer.attachSleepTime);
			}
		}catch(Throwable t){
			Executor.error(TAG, "attach " + thingPath, t);
		}
	}
	
	/**
	 * 标记控件。
	 */
	public static void markControl(final Control control){
		control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Designer designer = Designer.getDesigner();
				control.setData(Designer.DATA_MARKED, Designer.TRUE);
				redraw(control);	
				
				synchronized(designer.markedControls){
					designer.markedControls.add(control);
				}
			}
		});		
	}	
		
	public static void redraw(final Control control){
		if(control != null && !control.isDisposed()){
			control.getDisplay().asyncExec(new Runnable(){
				public void run(){
					control.redraw();
				}
			});
		}
	}
	
	/**
	 * 取消标记控件。
	 */
	public static void unmarkControl(final Control control){
		control.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Designer designer = Designer.getDesigner();
				
				control.setData(Designer.DATA_MARKED, null);
				redraw(control);
				
				synchronized(designer.markedControls){
					designer.markedControls.remove(control);
				}
			}
		});	
	}
	
	/**
	 * 打开设计窗口。
	 */
	public static void showDesignDialog(final Control control, Thing tools){
		Designer designer = Designer.getDesigner();
		designer.listener.setCurrentControL(control, tools);
	}
	
	/**
	 * 解除绑定，在控件销毁时解除。
	 */
	protected static void detach(Widget control){
		String thingPath = (String) control.getData(DATA_THING);
		//ActionContext actionContext = (ActionContext) control.getData("_designer_actionContext");
		Designer designer = Designer.getDesigner();
		if(thingPath != null){
			List<Widget> ctls = designer.controlMap.get(thingPath);
			if(ctls != null){
				synchronized(ctls){
					for(int i=0; i < ctls.size(); i++){
						if(ctls.get(i) == control){
							ctls.remove(i);
							i--;
							//break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 返回当前编辑器中的指定的控件列表。
	 */
	public static Map<String, List<Control>> getControlsInCurrentEditor(List<String> controlPaths){
		Control currentEditor = getActiveEditorRootControl();
		if(currentEditor == null){
			return null;
		}
				
		return getControlsInSameParnet(controlPaths, currentEditor);
	}
	
	/**
	 * 返回编辑器区域内所有编辑器的列表，通常是TabItem或CTabItem。
	 */
	@SuppressWarnings("unchecked")
	public static List<Control> getAllEditorRootControls(){
		ActionContainer ac = XWorkerUtils.getIdeActionContainer();
		if(ac != null){
			return (List<Control>) ac.doAction("getActiveEditorRootControl", null);
		}else{
			return Collections.EMPTY_LIST;
		}
	}
	
	/**
	 * 获取编辑器列表中激活的那个编辑器。
	 */
	public static Control getActiveEditorRootControl(){
		ActionContainer ac = XWorkerUtils.getIdeActionContainer();
		if(ac != null){
			return (Control) ac.doAction("getActiveEditorRootControl", null);
		}else{		
			return null;
		}
	}
	
	/**
	 * 获取指定路径的控件列表，并且这些控件都属于指定的父控件。
	 */
	public static Map<String, List<Control>> getControlsInSameParnet(List<String> controlPaths, Control parent){
		if(controlPaths == null || controlPaths.size() == 0){
			return null;
		}
		
		Map<String, List<Control>> controls = new HashMap<>();
		
		Designer designer = Designer.getDesigner();
		for(String path : controlPaths){
			//根据控件路径查找控件
			List<Widget> widgets = designer.controlMap.get(path);
			if(widgets != null){
				for (Widget widget : widgets) {
					if (widget instanceof Control) {
						Control control = (Control) widget;
						if (isSameParent(control, parent)) {
							List<Control> clist = controls.computeIfAbsent(path, k -> new ArrayList<>());

							clist.add(control);
						}
					}
				}
			}
		}
		
		return controls;
	}
	
	private static boolean isSameParent(Control control, Control parent){
		Control theParent = control;		

		while(theParent != null){
			if(theParent == parent){
				return true;
			}
			
			theParent = theParent.getParent();
		}
		
		return false;
	}
	
	/**
	 * 返回创建控件的事物路径。
	 */
	public static String getControlThingPath(Control control){
		if(control == null || control.isDisposed()){
			return null;
		}
		
		return (String) control.getData(Designer.DATA_THING);
	}
	
	/**
	 * 获取设计器绑定在组件上的事物，一般是创建该组件的事物。
	 */
	public static Thing getThing(Widget widget){
		if(widget == null || widget.isDisposed()){
			return null;
		}
		
		String path = (String) widget.getData(Designer.DATA_THING);
		return World.getInstance().getThing(path);
	}
	
	/**
	 * 获取
	 */
	public static ThingEntry getThingEntry(Widget widget) {
		if(widget == null || widget.isDisposed()){
			return null;
		}
		
		return (ThingEntry) widget.getData(Designer.DATA_THING_ENTRY);
	}
	
	/**
	 * 返回指定的控件是否是事物表单的属性字段或其它属性字段所创建的控件。
	 */
	public static boolean isAttribute(Control control){
		if(control == null || control.isDisposed()){
			return false;
		}
		return UtilData.isTrue(control.getData(Designer.DATA_ISATTRIBUTE));
	}
	
	/**
	 * 返回控件的创建者，只返回根的创建者。
	 *
	 * 创建者一般自定义的组件模型，组件也可以套用组件，因此一个控件存在多个创建者。
	 *
	 * @param control 控件
	 * @return 根创建者
	 */
	public static Thing getCreator(Control control){
		return getCreator(control, 0);
	}
	
	/**
	 * 返回控件的创建者，可以指定返回哪一个创建者。
	 *
	 * 创建者一般自定义的组件模型，组件也可以套用组件，因此一个控件存在多个创建者。
	 *
	 * @param control 控件
	 * @param index -1 表示当前控件的父创建者，0 表示根创建者
	 *
	 * @return 创建者
	 */
	public static Thing getCreator(Control control, int index){
		if(control == null || control.isDisposed()){
			return null;
		}
		
		String path = (String) control.getData(Designer.DATA_CREATOR);
		if(path == null) {
			if(Designer.isAttribute(control)) {
				return getAttributeCreator(control, index);
			}else {
				return null;
			}
		}else {
			String[] paths = path.split("[,]");
					
			if(index == -1) {
				return World.getInstance().getThing(paths[paths.length - 1]);
			}else {
				return World.getInstance().getThing(paths[0]);
			}
		}
		//return World.getInstance().getThing(path);
	}
	
	private static Thing getAttributeCreator(Control control, int index){
		if(control == null || control.isDisposed()){
			return null;
		}
		
		String path = (String) control.getData(Designer.DATA_CREATOR);
		if(path == null) {
			return getAttributeCreator(control.getParent(), index);
		}else {
			String paths[] = path.split("[,]");
					
			if(index == -1) {
				return World.getInstance().getThing(paths[paths.length - 1]);
			}else {
				return World.getInstance().getThing(paths[0]);
			}
		}
	}

	/**
	 * 如果一个控件是在自定义的组件中的，返回自定义组件的根控件，如果该组件是嵌套其它自定义组件中的，那么返回最上面的那个自定义组件的根控件。
	 *
	 * @param control 控件
	 * @return 自定义组件的根控件，不存在返回null
	 */
	public static Control getCreatorControl(Control control){
		return getCreatorControl(control, 0);
	}

	/**
	 * 如果一个控件是自定义的组件中的，返回指定组件的根控件。
	 *
	 * @param control 控件
	 * @param index 组件的索引，0是第一个，1是第一个嵌套的第一个子组件，....其它类推
	 *
	 * @return 组件的根控件，不存在返回null
	 */
	public static Control getCreatorControl(Control control, int index){
		Thing creator = getCreator(control, index);
		if(creator == null){
			return null;
		}
		
		Control p = control;
		while(p != null){
			Thing thing = getThing(p);
			if(thing != creator && isCreatorRoot(p)){
				//如果是创建者调用了创建者，那么在根节点应该是这样的
				creator = getCreator(control);
			}
			
			if(thing == creator && isCreatorRoot(p)){
				return p;
			}
			
			p = p.getParent();
		}
		
		return null;
	}
	
	public static boolean isUnderCreator(Control control){
		if(control == null || control.isDisposed()){
			return false;
		}

		Control p = control.getParent();
		return p != null && getCreator(p) != null;
	}
			
	public static boolean isCreatorRoot(Control control){
		return UtilData.isTrue(control.getData(Designer.DATA_CREATOR_ROOT));
	}
	
	/**
	 * 获取设计器绑定在组件上的变量上下文，该变量上下文通常是用来创建该组件的变量上下文。
	 */
	public static ActionContext getActionContext(Widget widget){
		return  (ActionContext) widget.getData("_designer_actionContext");
	}
	
	/**
	 * 从一个父Control开始寻找对应指定事物的控件，一般该控件是由这个事物创建的。
	 */
	public static Control findControl(Control control, String thingPath){
		String cpath = getControlThingPath(control);
		if(cpath != null && cpath.equals(thingPath)){
			return control;
		}
		
		if(control instanceof Composite){
			Composite composite = (Composite) control;
			for(Control child : composite.getChildren()){
				Control c = findControl(child, thingPath);
				if(c != null){
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 获取属于同一个shell的控件集合，可能或返回多个集合，值返回全部空间都同时存在的集合。
	 */
	public static List<Map<String, List<Control>>> getControlsInSameShell(List<String> controlPaths){
		if(controlPaths == null || controlPaths.size() == 0){
			return null;
		}
		
		List<Map<String, List<Control>>> controlList = new ArrayList<Map<String, List<Control>>>();
		String firstPath = controlPaths.get(0);
		
		Designer designer = Designer.getDesigner();
		for(String path : controlPaths){
			//根据控件路径查找控件
			List<Widget> widgets = designer.controlMap.get(path);
			if(widgets != null){			
				for(int i=0; i<widgets.size(); i++){
					Widget widget = widgets.get(i);
					if(widget instanceof Control){		
						Control control = (Control) widget;
						if(control.isDisposed()){
							continue;
						}
						
						Shell shell = control.getShell();
						boolean have = false;
						
						//查看是否存在相同的shell，如果有加入到一起
						for(Map<String, List<Control>> controlMap : controlList){
							List<Control> controls  = controlMap.get(firstPath);
							if(controls.get(0).getShell() == shell){
								addControl(path, controlMap, control);
								have = true;
								break;
							}
						}
						
						if(!have){
							if(path.equals(firstPath)){
								//如果是第一个控件，那么创建一个map，否则不添加到列表中
								Map<String, List<Control>> controlMap = new HashMap<String, List<Control>>();
								List<Control> controls = new ArrayList<Control>();
								controls.add(control);
								controlMap.put(path, controls);
								controlList.add(controlMap);
							}
						}
					}
				}
			}
		}
		
		//移除控件不全的，必须返回所有路径都有对应控件的
		for(int i=0; i<controlList.size(); i++){
			Map<String, List<Control>> controlMap = controlList.get(i);
			
			boolean have = true;
			for(String path : controlPaths){
				if(controlMap.get(path) == null){
					have = false;
					break;
				}
			}
			
			if(!have){
				controlList.remove(i);
				i--;
			}
		}
		
		return controlList;
	}
	
	private static void addControl(String path, Map<String, List<Control>> controlMap, Control control){
		List<Control> controlList = controlMap.computeIfAbsent(path, k -> new ArrayList<>());

		controlList.add(control);
	}
	
	public static Control getActiveControl(String path){
		Designer designer = Designer.getDesigner();
		List<Widget> widgets = designer.controlMap.get(path);
		if(widgets != null){			
			for(int i=0; i<widgets.size(); i++){
				Widget widget = widgets.get(i);
				if(widget instanceof Control){					
					Control control = (Control) widget;
					if(widgets.size() == 1){
						//如果只有唯一的一个，那么返回吧
						return control;
					}
										
					//control.get
					//返回正在显示的控件，判断是否是正在显示的控件
					if(control.getDisplay() == Display.getCurrent()){
						//目前先支持在同一个Display下的控件
						control.getShell().isFocusControl();
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 返回绑定到thingPath上的所有控件列表。
	 */
	public static List<Widget> getWidgets(String thingPath){
		Designer designer = Designer.getDesigner();
		return designer.controlMap.get(thingPath);
	}
	
	public static ActionContainer getExplorerActions(){
		return XWorkerUtils.getIdeActionContainer();
	}
	
	public static ActionContext getExplorerActionContext(){
		IIde ide = XWorkerUtils.getIde();
		if(ide != null){
			return ide.getActionContext();
		}else{
			return null;
		}
	}
	
	public static Display getExplorerDisplay(){
		Designer designer = Designer.getDesigner();
		if(designer.explorerDisplay != null){
			return designer.explorerDisplay;
		}else{
			return Display.getDefault();
		}
	}
	
	public static Shell getExplorerShell(){
		return (Shell) XWorkerUtils.getIDEShell();
		//return (Shell) explorerActions.getActionContext().get("shell");
	}
	
	public static void setExplorerActions(Display explorerDisplay_, ActionContainer explorerActions_){
		Designer designer = Designer.getDesigner();
		designer.explorerDisplay = explorerDisplay_; 
		//explorerActions = explorerActions_;
	}
	
	public static boolean isEnabled(){
		Designer designer = Designer.getDesigner();
		return designer.enabled;
	}
	
	public static void setEnabled(boolean enabled_){
		Designer designer = Designer.getDesigner();
		designer.enabled = enabled_;
	}
	
	/**
	 * 同时标记和打开ToolTip，ToolTip是一个在最上层的，用于介绍标记的控件窗口。
	 * 
	 * @param control 要标记的控件
	 * @param contentThing ToolTip的内容事物，获取description属性获取
	 * @param width 窗口宽度
	 * @param height 窗口高度
	 */
	public static void markAndTooltip(Control control, Thing contentThing, int width, int height){
		//先标记
		Designer.markControl(control);
		
		showToolTip(control, contentThing, width, height);
		
		//System.out.println("orgLocation=" + shellLocation);
		//System.out.println("tagLocation=" + markTooltipShell.getLocation());
	}
	
	/**
	 * 为一个控件显示一个提示窗口。
	 */
	public static void showToolTip(Control control, Thing toolTipThing, int width, int height) {
		if(!control.isVisible()) {
			control.setVisible(true);
		}
		
		Designer designer = Designer.getDesigner();
		//提示窗口
		if(designer.markTooltipShell == null || designer.markTooltipShell.isDisposed()){
			ActionContext ac = new ActionContext();
			ac.put("parent", designer.explorerDisplay);
			
			Thing shellThing = World.getInstance().getThing("xworker.swt.xworker.design.MarkTooltip");
			
			designer.markTooltipShell = (Shell) shellThing.doAction("create", ac); 
			designer.markTooltipShell.setData(ac);
		}
				
		designer.markTooltipShell.setSize(width, height);
		attachTo(designer.markTooltipShell, control);
		
		//设置浏览器的内容
		ActionContext ac = (ActionContext) designer.markTooltipShell.getData();
		designer.markTooltipShell.setText(toolTipThing.getMetadata().getLabel());
		((Browser) ac.get("browser")).setUrl(getUrlRoot() + "do?sc=xworker.swt.xworker.design.MarkTooltipControl&thing=" + toolTipThing.getMetadata().getPath());
		
		designer.markTooltipShell.setVisible(true);
		designer.markTooltipShell.forceActive();
	}
	
	/**
	 * 把一个Shell依附到指定控件的附近。
	 */
	public static void attachTo(Shell shell, Control control) {
		//确定提示窗口应该显示的位置
		//Designer designer = Designer.getDesigner();
		Rectangle monitorSize = control.getShell().getMonitor().getClientArea();
		//Rectangle monitorSize = shell.getDisplay().getMonitors()[0].getClientArea();
		
		Point location = control.getLocation();
		Point cl = control.getParent().toDisplay(location);
		Point size = control.getSize();
		
		int width = shell.getSize().x;
		int height = shell.getSize().y;
		
		//先看看是否适合显示在最下面，提示框的右下角在窗口内符合
		Point shellLocation = null;
		if(height < size.y){
			shellLocation = getTooltipLocation(control, location, cl, size, width, height, monitorSize, new int[]{Designer.RIGHT, Designer.LEFT, Designer.UP, Designer.BOTTOM});
		}else{
			shellLocation = getTooltipLocation(control, location, cl, size, width, height, monitorSize, new int[]{Designer.BOTTOM, Designer.UP, Designer.RIGHT, Designer.LEFT});
		}
		
		//设置窗口的位置
		shell.setLocation(shellLocation);
	}
	
	/**
	 * 从指定的控件或子控件上获取指定事物对应的控件。
	 */
	public static Control getControl(Control control, String thingPath, boolean isAttribute) {
		String thing = (String) control.getData(Designer.DATA_THING);
		if(thingPath.equals(thing)) {
			if(isAttribute && UtilData.isTrue(control.getData(Designer.DATA_ISATTRIBUTE))) {
				return control;
			}else {
				return control;
			}
		}
		
		//取子节点
		if(control instanceof Composite) {
			for(Control child : ((Composite) control).getChildren()) {
				Control c = getControl(child, thingPath, isAttribute);
				if(c != null) {
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 从指定的控件或子控件上获取指定事物对应的控件，其中控件的的类名要和simpleClassName一致。
	 */
	public static Control getControl(Control control, String thingPath, boolean isAttribute, String simpleClassName) {
		String thing = (String) control.getData(Designer.DATA_THING);
		String name = control.getClass().getSimpleName();
		//System.out.println(name + " : " + thing);
		if(thingPath.equals(thing) && name.equals(simpleClassName)) {			
			if(isAttribute && UtilData.isTrue(control.getData(Designer.DATA_ISATTRIBUTE))) {
				return control;
			}else {
				return control;
			}
		}
		
		//取子节点
		if(control instanceof Composite) {
			for(Control child : ((Composite) control).getChildren()) {
				Control c = getControl(child, thingPath, isAttribute, simpleClassName);
				if(c != null) {
					return c;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 让shell(width, height)显示在控件四周
	 * 
	 * @param control 控件
	 * @param location 控件自身获取的Location
	 * @param cl 控件的Location相对于Display的
	 * @param size 控件的大小
	 * @param width Shell的宽度
	 * @param height Shell的高度
	 * @param monitorSize 监视器的大小
	 * @param seq 位置选择顺序列表
	 * @return 位置
	 */
	private static Point getTooltipLocation(Control control, Point location,Point cl, Point size, int width, int height, Rectangle monitorSize, int[] seq){
		//rec是control在display中的区域
		location = control.toDisplay(location);				
		Rectangle rec = new Rectangle(location.x, location.y, location.x + size.x, location.y + size.y);
		
		
		//System.out.println(monitor.getClientArea());
		int x = 0, y = 0;
		//Point l = null;
		for(int s : seq){
			switch(s){
			case Designer.BOTTOM:
				//判断存在不准确的情况，尤其是多个显示器，且显示器的缩放又不一致的情况
				if(rec.height + height < monitorSize.height) {					
					y = cl.y +size.y;
							
					//可以在下面显示Shell
					if(rec.x +  width < monitorSize.x + monitorSize.width) {
						x = cl.x;
					}else {
						//右面超出屏幕
						x= monitorSize.x + monitorSize.width - width;						
					}
					return new Point(x, y);
				}
				
				break;
			case Designer.UP:
				if(rec.y - height > monitorSize.y) {					
					//可以在上面显示Shell
					y = cl.y - height;
					if(rec.x + width < monitorSize.x + monitorSize.width) {
						x = cl.x;
					}else {
						//右面超出屏幕
						x= monitorSize.x + monitorSize.width - width;						
					}
					return new Point(x, y);
				}
				
				break;
			case Designer.LEFT:
				if(rec.x - width > monitorSize.x) {
					//可以在左面放下
					x = cl.x - width;
					y = cl.y;
					if(rec.y + height > monitorSize.height) {
						//不要超出下边界
						y = monitorSize.y + monitorSize.height - height - 100;
					}
					if(y < 0) {
						//不能超出上边界，要能够看到Shell的标题
						y = 0;
					}
					return new Point(x, y);
				}
				
				break;
			case Designer.RIGHT:
				if(rec.width + width < monitorSize.width + monitorSize.x) {
					x = cl.x + size.x;
					y = cl.y;
					if(rec.y + height > monitorSize.height) {
						//不要超出下边界
						y = monitorSize.y +  monitorSize.height - height - 100;
					}
					if(y < 0) {
						//不能超出上边界，要能够看到Shell的标题
						y = 0;
					}
					return new Point(x, y);
				}
				break;
			}
		}
		
		
		//那里都不行
		rec = control.getBounds();
		if(width < rec.width && height < rec.height) {
			//如果窗口被控件小，那么居中
			x = rec.width / 2 - width / 2;
			y = rec.height / 2 - height / 2;
			return new Point(x + cl.x, y + cl.y);
		}else {		
			//直接放在控件上，留出一点点距离
			return new Point(cl.x, cl.y + 20);
		}
	}
	
	/**
	 * 返回XWorker的URL的根路径。
	 */
	public static String getUrlRoot(){
		return XWorkerUtils.getWebUrl();
		/*
		Thing globalConfig = World.getInstance().getThing("_local.xworker.config.GlobalConfig");
		if(globalConfig != null){
			return globalConfig.getString("webUrl");
		}else{
			return "http://localhost:9001/";
		}*/
	}
	
	public static void paintIfSelected(Control control, GC gc){
		String designData = (String) control.getData(Designer.DATA_DESING_SELECTED);
		if(!"true".equals(designData)){
			return;
		}
		
		paintSelected(control, gc);
	}
	/**
	 * 画一个控件的选择标记。
	 */
	public static void paintSelected(Control control, GC gc){
		
		
		Rectangle rect = null;
		
		Class<?> cls = control.getClass();
		if(cls == Composite.class || cls == Shell.class || cls == ScrolledComposite.class){
			rect = ((Composite) control).getClientArea();
			//rect = new Rectangle(0, 0,	control.getSize().x, control.getSize().y);
		}else{
		    rect = new Rectangle(0, 0,	control.getSize().x, control.getSize().y);
		}
		 
		
		//System.out.println(event.count);
		
		//判断是否是可以拖拽大小
		//if(isResizeAble(control)){
		ResizeUtil.draw(gc, rect.x, rect.y, rect.width, rect.height);
		//}
		
		Color color = gc.getForeground();
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
		gc.setLineStyle(SWT.LINE_DASHDOTDOT);
		gc.setLineWidth(1);
		gc.drawRectangle(rect.x + 3, rect.y + 3, rect.width - 3, rect.height - 3);
		//gc.drawRectangle(rect.x, rect.y, 6, 6);					
		gc.setForeground(color);
	}
}	