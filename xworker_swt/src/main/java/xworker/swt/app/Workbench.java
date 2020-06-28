package xworker.swt.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.widgets.CoolBarCreator;

public class Workbench {
	//private static Logger logger = LoggerFactory.getLogger(Workbench.class);
	private static final String TAG = Workbench.class.getName();
	public static final String VIEW_ID = "__Workbench_view_id__";
	
	Thing thing;
	ActionContainer actions;
	ActionContext actionContext;
	IEditorContainer editorContainer;
	CTabFolder leftTabFolder;
	CTabFolder rightTabFolder;
	CTabFolder bottomTabFolder;
	Shell shell;
	
	public Workbench(Thing thing, ActionContainer actions, ActionContext actionContext) {
		this.thing = thing;
		this.actions = actions;
		this.actionContext = actionContext;
		this.editorContainer = actionContext.getObject("editorContainer");
		this.leftTabFolder = actionContext.getObject("leftTabFolder");
		this.rightTabFolder = actionContext.getObject("rightTabFolder");
		this.bottomTabFolder = actionContext.getObject("bottomTabFolder");
		this.shell = actionContext.getObject("shell");
	}
	
	public Thing getThing() {
		return thing;
	}
		
	public ActionContainer getActions() {
		return actions;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public IEditorContainer getEditorContainer() {
		return editorContainer;
	}

	public CTabFolder getLeftTabFolder() {
		return leftTabFolder;
	}

	public CTabFolder getRightTabFolder() {
		return rightTabFolder;
	}

	public CTabFolder getBottomTabFolder() {
		return bottomTabFolder;
	}

	public Shell getShell() {
		return shell;
	}
	
	public Menu getMenuBar() {
		return actionContext.getObject("mainMenuBar");
	}

	public CoolBar getMainCoolBar() {
		return actionContext.getObject("mainCoolBar");		
	}
	
	public CoolBar getRightCoolBar() {
		return actionContext.getObject("rightCoolBar");		
	}
	
	public CoolBar getStatusBar() {
		return actionContext.getObject("mainStatusBar");
	}
	
	/**
	 * 打开一个编辑器，如果编辑器已经存在则返回已有的编辑器。
	 * 
	 * @param id     编辑器的标识
	 * @param editor 编辑器事物
	 * @param params 参数，参数的内容有编辑器自定义
	 * @return
	 */
	public IEditor openEditor(String id, Thing editor, Map<String, Object> params) {
		params.put("editorThing", editor);
		return actions.doAction("openEditor", actionContext, "id", id, "editor", editor, "params", params);
	}
	
	/**
	 * 打开一个视图，默认可以关闭。
	 * 
	 * @param id
	 * @param view
	 * @param type
	 */
	public View openView(String id, final Thing view, final String type) {
		Map<String, Object> params = view.doAction("getParams", actionContext);
		return openView(id, view, type, true, params);
	}
	
	/**
	 * 打开一个视图。
	 *  
	 * @param id       视图的标识。
	 * @param view   视图事物。
	 * @param type     打开的位置，left、right或bottom。
	 * @param closeable 是否可以关闭
	 */
	public View openView(String id, final Thing view, final String type, final boolean closeable, final Map<String, Object> params) {
		if(view == null) {
			Executor.warn(TAG, "Viewer is null, id=" + id);
			return null;
		}
		
		if("editor".equals(type)) {
			//在编辑区域打开一个View
			Map<String, Object> params1 = new HashMap<String, Object>();
			if(params != null) {
				params1.putAll(params);
			}
			params1.put("composite", view.getMetadata().getPath());
			params1.put("title", view.getMetadata().getLabel());
			params1.put("simpleTitle", view.getMetadata().getLabel());
			params1.put("icon", view.getString("icon"));
			params1.put("editorThing", view);
			
			Thing editor = World.getInstance().getThing("xworker.swt.app.editors.CompositeEditor");
			openEditor(id, editor, params);
			return null;
		}
		
		if(id == null || "".equals(id)) {
			id = view.getMetadata().getPath();
		}
		
		View v = getView(id);
		if(v != null) {
			return v;
		}
		final View vv = new View(id, view, params);
		
		//使用线程异步打开
		final String iid = id;
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					CTabFolder tabFolder = leftTabFolder;
					if("right".equals(type) && rightTabFolder != null) {
						tabFolder = rightTabFolder;
					}else if("bottom".equals(type) && bottomTabFolder != null) {
						tabFolder = bottomTabFolder;
					}
										
					vv.create(Workbench.this, tabFolder, closeable, actionContext);
				}catch(Exception e) {
					Executor.warn(TAG, "Open view exception, id=" + iid + ", view=" + view.getMetadata().getPath(), e);
				}
			}
		});		
		
		return vv;
	}
	
	/**
	 * 返回View所在的CTabItem，如果存在。可以通过item.getData("actionContext")获取view自己的变量上下文。
	 * 
	 * @param id
	 * @return
	 */
	public CTabItem getViewItem(String id) {
		CTabItem item = getViewItem(id, leftTabFolder); 
		if(item != null) {
			return item;
		}
				
		item = getViewItem(id, rightTabFolder); 
		if(item != null) {
			return item;
		}
		
		item = getViewItem(id, bottomTabFolder); 
		if(item != null) {
			return item;
		}		
		
		return null;
	}
	
	/**
	 * 根据视图的标识返回一个视图。
	 * 
	 * @param id
	 * @return
	 */
	public View getView(String id) {
		CTabItem item = getViewItem(id, leftTabFolder); 
		if(item != null) {
			return (View) item.getData();
		}
		
	    item = getViewItem(id, rightTabFolder); 
		if(item != null) {
			return (View) item.getData();
		}
		
		item = getViewItem(id, bottomTabFolder); 
		if(item != null) {
			return (View) item.getData();
		}
		
		return null;
	}
	
	/**
	 * 根据编辑器的ID返回编辑器。
	 * 
	 * @param id
	 * @return
	 */
	public IEditor getEditor(String id) {
		for(IEditor editor : editorContainer.getEditors()) {
			if(editor == null) {
				Executor.warn(TAG, "editor is null");
				continue;
			}
			if(editor.getId() == null) {
				Executor.warn(TAG, "editor is is null");
				continue;
			}
			if(editor != null && editor.getId().equals(id)) {
				return editor;
			}
		}
		
		return null;
	}
	
	private CTabItem getViewItem(String id, CTabFolder tabFolder) {
		if(tabFolder == null) {
			return null;
		}
		
		for(CTabItem item : tabFolder.getItems()) {
			String itemId = (String) item.getData(VIEW_ID);
			if(itemId != null && itemId.equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * 退出工作台。
	 * 考虑到RWT模式，返回值取消，无意义，总是返回false。
	 * 
	 * @return
	 */
	public boolean exit() {		
		if(actions != null) {
			return actions.doAction("exit", actionContext);
		}else {
			//shell.dispose();
			return false;
		}
	}
	
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Shell shell = self.doAction("create", actionContext);
		shell.setVisible(true);
		shell.forceActive();
		if(shell.getDisplay().getShells().length == 1) {
			//只有当前的Shell，唯一的，那么进入Display的等待循环
			Display display = shell.getDisplay();
    		while (!shell.isDisposed ()) {
    			try{
    				if (!display.readAndDispatch ()) display.sleep ();
    			}catch(Exception e){
    				e.printStackTrace();
    			}
		    }
		    display.dispose ();
		}
		return shell;
	}
	
	public static Object create(ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		final ActionContext ac = new ActionContext();
		
		//配置
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("hasMenu", self.getBoolean("menu"));
		config.put("hasCoolBar", self.getBoolean("coolBar"));
		config.put("hasRightTab", self.getBoolean("rightTab"));
		config.put("hasOutline", self.getBoolean("outline"));
		config.put("hasBottomTab", self.getBoolean("bottomTab"));
		config.put("hasStatusBar", self.getBoolean("statusBar"));
		ac.put("config", config);
		ac.put("parent", actionContext.get("parent"));
		//Shell的标题
		ac.put("title", self.getMetadata().getLabel());
		ac.put("navLabel", self.doAction("getNavLabel", actionContext));
		//Workben事物自己
		ac.put("workbenchThing", self);
		ac.put("parentContext", actionContext);
		ac.put("rapInterval", self.getLong("rapInterval"));
		
		//创建Shell
		Shell shell =  null;
		Designer.pushCreator(self);
		try {
			Thing prototype = World.getInstance().getThing("xworker.swt.app.prototypes.WorkbenchShell");
			shell = prototype.doAction("create", ac);
			shell.setMaximized(self.getBoolean("maximized"));
			Designer.attachCreator(shell, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
		
		//追加ActionContainer的定义
		Thing actionContainer = self.getThing("ActionContainer@0");
		if(actionContainer != null) {
			ActionContainer actions = ac.getObject("actions");
			if(actions != null) {
				actions.append(actionContainer);
			}
		}
		
		//初始化
		//树菜单
		Thing appTreeModel  = self.doAction("getAppTreeModel", actionContext);
		if(appTreeModel != null) {			
			ac.peek().put("parent", ac.get("navTree"));
			appTreeModel.doAction("create", ac);
		}
		
		//菜单
		createItems(self, "menu", "getMenu", "mainMenuBar", ac);
				
		//Coolbar
		createItems(self, "coolBar", "getCoolBar", "mainCoolBar", ac);
		CoolBar mainCoolBar = ac.getObject("mainCoolBar");
		if(mainCoolBar != null) {
			CoolBarCreator.initCoolBar(mainCoolBar);
		}
		
		//RightCoolbar
		createItems(self, "rightCoolBar", "getRightCoolBar", "rightCoolBar", ac);
		CoolBar rightCoolBar = ac.getObject("rightCoolBar");
		if(rightCoolBar != null) {
			CoolBarCreator.initCoolBar(rightCoolBar);
		}
		
		//StatusBar
		createItems(self, "statusBar", "getStatusBar", "mainStatusBar", ac);
		CoolBar mainStatusBar = ac.getObject("mainStatusBar");
		if(mainStatusBar != null) {
			CoolBarCreator.initCoolBar(mainStatusBar);
		}
		
		//LeftTabFolder
		createItems(self, null, "getLeftTabFolder", "leftTabFolder", ac);
		
		//RightTabFolder
		createItems(self, "rightTab", "getRightTabFolder", "rightTabFolder", ac);
		
		//bottomTab
		createItems(self, "bottomTab", "getBottomTabFolder", "bottomTabFolder", ac);
			
		Workbench workbench = new Workbench(self, (ActionContainer) ac.getObject("actions"), ac);
		ac.put("workbench", workbench);
		actionContext.g().put(self.getMetadata().getName(), workbench);
		
		//打开视图，如果存在
		Thing views = self.getThing("Views@0");
		if(views != null) {
			for(Thing view : views.getChilds("View")) {
				String id = view.doAction("getId", actionContext);
				String type = view.doAction("getType", actionContext);
				Thing viewThing = view.doAction("getComposite", actionContext);
				
				Map<String, Object> params = view.doAction("getParams", actionContext);
				workbench.openView(id, viewThing, type, view.getBoolean("closeable"), params);
			}
		}
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try{
					//是否打开默认编辑自己
					ActionContainer actions = ac.getObject("actions");
					if(self.getBoolean("editSelf")) {
						actions.doAction("openEditor", ac, "id", self.getMetadata().getPath(), 
								"editor", World.getInstance().getThing("xworker.swt.app.editors.ThingEditor"), 
								"params", UtilMap.toMap("thing", self));
					}
					
					//触发onCreate事件
					self.doAction("onCreate", ac);
					
					//CTabFolder应该选中第一个，默认会选中最后一个
					String[] tabs = new String[] {null, "getLeftTabFolder", "leftTabFolder", "rightTab",
							"getRightTabFolder", "rightTabFolder", "bottomTab", "getBottomTabFolder", "bottomTabFolder"};
					for(int i=0; i<tabs.length; i++) {
						String attrName = tabs[i];
						String parentVarName = tabs[i + 2];
						i = i + 2;
						if(attrName == null || self.getBoolean(attrName)) {
							Object parent = ac.get(parentVarName);							
							if(parent != null && parent instanceof CTabFolder) {
								CTabFolder ctab = (CTabFolder) parent;
								if(ctab.getItemCount() > 0) {
									ctab.setSelection(0);
									Event event = new Event();
									event.item = ctab.getItems()[0];
									ctab.notifyListeners(SWT.Selection, event);
								}
							}
						}
					}
					
					//执行初始化
					self.doAction("init", ac);
				}catch(Exception e) {
					Executor.error(TAG, "init error, workbench=" + self.getMetadata().getPath(),  e);
				}
			}
		});
		
		//RAP下默认不显示，不应该不显示的，增加了下面的代码为了能够显示
		shell.setVisible(true);
		shell.forceActive();
		return shell;
	}

	private static void createItems(Thing self, String attrName, String actionName, String parentVarName, ActionContext actionContext) {
		if(attrName == null || self.getBoolean(attrName)) {
			Thing thing = self.doAction(actionName, actionContext);
			Object parent = actionContext.get(parentVarName);
			if(thing != null && parent != null) {				
				actionContext.peek().put("parent", parent);
				for(Thing child : thing.getChilds()) {
					child.doAction("create", actionContext);
				}
			}
			if(parent instanceof CTabFolder) {
				CTabFolder ctab = (CTabFolder) parent;
				if(ctab.getItemCount() > 1) {
					ctab.setSelection(0);
				}
			}
		}
	}
	
	/**
	 * 保存当前激活的编辑器的内容。
	 */
	public void save() {
		editorContainer.save();
	}
	
	/**
	 * 保存所有已修改的编辑器的内容。
	 * 
	 */
	public void saveAll() {
		editorContainer.saveAll();
	}
	
	/**
	 * 返回是否有编辑器处于已修改状态。
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return editorContainer.isDirty();
	}
	
	/**
	 * 返回当前的编辑器。
	 * 
	 * @return 如果没有返回null
	 */
	public IEditor getActiveEditor() {
		return editorContainer.getActiveEditor();
	}
	
	/**
	 * 返回所有编辑器的列表。
	 * 
	 * @return
	 */
	public List<IEditor> getEditors(){
		return editorContainer.getEditors();
	}
	
	/**
	 * 返回所有的视图。
	 * 
	 * @return
	 */
	public List<View> getViews(){
		List<View> views = new ArrayList<View>();
		initViews(leftTabFolder, views);
		initViews(rightTabFolder, views);
		initViews(bottomTabFolder, views);
		return views;
	}
	
	private void initViews(CTabFolder tab, List<View> views) {
		if(tab == null) {
			return;
		}
		
		for(CTabItem item : tab.getItems()) {
			if(item.getData() instanceof View) {
				views.add((View) item.getData());
			}
		}
	}
}
