package xworker.swt.app.editorContainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;
import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;
import xworker.swt.app.editors.EditorImpl;

public class CTabFolderEditorContainer extends AbstractEditorContianer implements  CTabFolder2Listener, SelectionListener{
	public static final String ID = "__CTabFolderEditorContainer_id__";
	public static final String EDITOR_CONTEXT = "__CTabFolderEditorContainer_editor_context__";	
	public static final String EDITOR_ACTIONS = "__CTabFolderEditorContainer_editor_actions__";
	public static final String EDITOR_THING = "__CTabFolderEditorContainer_editor_thing__";
	public static final String EDITOR = "__CTabFolderEditorContainer_editor__";
	public static final String ITEM = "__CTabFolderEditorContainer_item__";
	private static final String TAG = "CTabFolderEditorContainer";
	
	CTabFolder tabFolder;
	/** 上一次选中的条目 */
	CTabItem lastItem; 
	
	/** 由于在监听到CTabItem.close的事件里，item已经disposed，这样就取不出IEditor了 */
	Map<CTabItem, IEditor> editors = new HashMap<CTabItem, IEditor>();
	
	public CTabFolderEditorContainer(CTabFolder tabFolder, ActionContext actionContext) {
		super(actionContext);
				
		Thing containerThing = World.getInstance().getThing("xworker.swt.app.prototypes.CTabFolderEditorContainer/@cTabFolder");
		containerContext.peek().put("parent", tabFolder);
		for(Thing child : containerThing.getChilds()) {
			child.doAction("create", containerContext);
		}
		
		this.actions = containerContext.getObject("actions");
		this.tabFolder = tabFolder;
		this.tabFolder.addCTabFolder2Listener(this);
		this.tabFolder.addSelectionListener(this);		
	}
	
	
	
	@ActionParams(names="id,editor,params")
	public IEditor openEditor(String id, Thing editor, Map<String, Object> params) {
		//查看编辑器是否已经存在了
		CTabItem editItem = null;
		for(CTabItem item : tabFolder.getItems()) {
			String itemId = (String) item.getData(ID);
			if(id.equals(itemId)) {
				editItem = item;
				break;
			}
		}
		
		if(editItem != null) {
			//编辑器已经存在
			updateEditor(editItem, params);
			setSelection(editItem);
			return getEditorUtils(editItem);
		}else {		
			//创建编辑器
			IEditor editor_ = createEditor(id, editor, params);
			if(editor_ != null) {
				this.fireOnCreated(editor_);
			}
			
			return editor_;
		}
	}
	
	@ActionParams(names="editorActions")
	public void editorModified(ActionContainer editorActions) {
		ActionContext editorContext = editorActions.getActionContext();
		CTabItem item = editorContext.getObject(ITEM);
		EditorImpl editor = getEditorUtils(item);
		updateEditorStatus(editor, item);
		
		thing.doAction("editorModified", actionContext, "editor", editor);
	}
	
	@Override
	public Composite getComposite() {
		return tabFolder;
	}
	
	@Override
	public boolean isDirty() {
		for(CTabItem item : tabFolder.getItems()) {
			EditorImpl editor = getEditorUtils(item);
			if(editor != null && editor.isDirty()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void saveAll() {
		for(CTabItem item : tabFolder.getItems()) {
			EditorImpl editor = getEditorUtils(item);
			if(editor != null && editor.isDirty()) {
				editor.doSave();
			}
		}
	}

	@Override
	public void save() {
		if(tabFolder.getSelection() != null) {
			EditorImpl editor = getEditorUtils(tabFolder.getSelection());
			if(editor != null) {
				editor.doSave();
			}else {
				Executor.info(TAG, "Editor is null, " + tabFolder.getSelection());
			}
		}
	}

	private IEditor createEditor(String id, Thing editor, Map<String, Object> params) {
		ActionContext editorContext = new ActionContext();
		editorContext.put("parentContext", containerContext);
		editorContext.put("parent", tabFolder);		
		editorContext.put("editorContainer", this);
		
		if(UtilData.isTrue(editor.doAction("hasComposite", editorContext)) == false) {
			editor.doAction("setContent", editorContext, "params", params);
			return null;			
		}
		
		//创建条目
		CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setData(EDITOR_CONTEXT, editorContext);
		item.setData(EDITOR_THING, editor);
		item.setData(ID, id);
		editorContext.put(ITEM, item);
		
		//创建编辑器
		Control editorControl = editor.doAction("create", editorContext);
		item.setControl(editorControl);
		
		//设置内容和更新状态
		ActionContainer actions = editorContext.getObject("actions");
		item.setData(EDITOR_ACTIONS, actions);
		
		EditorImpl editorUtil = new EditorImpl(this, editor, id, actions, editorContext);
		editorUtil.setControl(editorControl);

		item.setData(EDITOR, editorUtil);
		
		//EditorImpl editorUtil = getEditorUtils(item);
		editorContext.put("editor", editorUtil);
		editorUtil.setContent(params);
		updateEditorStatus(editorUtil, item);
		
		editors.put(item, editorUtil);
		setSelection(item);
				
		return editorUtil;
	}
	
	/**
	 * 一个条目从选择到不选择。
	 * 
	 * @param item
	 */
	private void deSelection(CTabItem item) {		
		//概要，设置空的概要。如果所有的编辑器都关闭了，要有一个空白的概要。
		if(outlineContainer != null) {
			outlineContainer.setComposite(null);
		}
		
		EditorImpl editor = getEditorUtils(item);
		if(editor == null) {
			return;
		}
		
		//菜单
		if(menuContainer != null) {
			Thing menuConfig = editor.getMenuConfig();
			if(menuConfig != null) {
				menuContainer.removeEditorMenu(menuConfig, actionContext);
			}
		}
		
		//CoolBar
		if(coolBarContainer != null) {
			Thing config = editor.getCoolBarConfig();
			if(config != null) {
				coolBarContainer.removeEditorCoolItem(config, actionContext);
			}
		}
		
		//StatusBar
		if(statusBarContainer != null) {
			Thing config = editor.getStatusBarConfig();
			if(config != null) {
				statusBarContainer.removeEditorCoolItem(config, actionContext);
			}
		}
		
		lastItem = null;
				
		//触发编辑器的onUnActive事件
		if(editor.getActions() != null) {
			editor.getActions().doAction("onUnActive", editor.getActionContext());
		}
	}
	
	/**
	 * 选中一个条目。
	 * 
	 * @param item
	 */
	private void setSelection(CTabItem item) {
		if(lastItem != null) {
			deSelection(lastItem);
		}
		
		//设置成选择
		tabFolder.setSelection(item);
				
		EditorImpl editor = getEditorUtils(item);
		//概要
		if(outlineContainer != null) {
			Composite outline = editor.getOutline();
			outlineContainer.setComposite(outline);
		}
		
		//菜单
		if(menuContainer != null) {
			Thing menuConfig = editor.getMenuConfig();
			if(menuConfig != null) {
				menuContainer.setEditorMenu(menuConfig, editor.getActionContext());
			}
		}
		
		//CoolBar
		if(coolBarContainer != null) {
			Thing config = editor.getCoolBarConfig();
			if(config != null) {
				coolBarContainer.setEditorCoolBar(config, editor.getActionContext());
			}
		}
		
		//StatusBar
		if(statusBarContainer != null) {
			Thing config = editor.getStatusBarConfig();
			if(config != null) {
				statusBarContainer.setEditorCoolBar(config, editor.getActionContext());
			}
		}
		lastItem = item;
		
		//触发编辑器的onActive事件
		if(editor.getActions() != null) {
			editor.getActions().doAction("onActive", editor.getActionContext());
		}
		
		this.fireOnActive(editor);
	}
	
	private EditorImpl getEditorUtils(CTabItem item) {
		//System.out.println(actionContext.get("editor"));
		EditorImpl editorUtils = (EditorImpl) editors.get(item);
		if(editorUtils == null) {
			if(item == null || item.isDisposed()) {
				return null;
			}
			
			editorUtils = (EditorImpl) item.getData(EDITOR);
		}
//		if(editorUtils == null || editorUtils.getActions() == null) {
//			//当创建Editor时如果出发的modified事件，那么获取不到actions,故加上editorsUtils.getActions() != null的判断
//			ActionContext editorContext = (ActionContext) item.getData(EDITOR_CONTEXT);
//			ActionContainer actions = (ActionContainer) item.getData(EDITOR_ACTIONS);
//			Thing editor = (Thing) item.getData(EDITOR_THING);
//			editorUtils = new EditorImpl(editor, actions, editorContext);
//			item.setData(EDITOR, editorUtils);
//		}
		
		return editorUtils;
	}
	
	private void updateEditor(CTabItem item , Map<String, Object> params) {
		EditorImpl editor = getEditorUtils(item);
		if(editor.isSameContent(params) == false) {
			editor.setContent(params);
			updateEditorStatus(editor, item);
		}
	}
	
	private void updateEditorStatus(EditorImpl editor, CTabItem item) {
		
		String simpleTitle = editor.getSimpleTitle();
		String title = editor.getTitle();
		Boolean dirty = editor.isDirty();
		if(simpleTitle != null) {
			if(dirty != null && dirty) {
				item.setText("*" + simpleTitle);
			}else{
				item.setText(simpleTitle);
			}
		}
		if(title != null) {
			item.setToolTipText(title);
			
			//item.getParent().getShell().setText(title);
		}
		
		//设置图标
		Image icon = editor.getIcon();
		if(icon != null) {
			item.setImage(icon);
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		CTabItem item = (CTabItem) e.item;
		setSelection(item);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		
	}
	
	public void doClose(CTabItem item) {
		if(item == null) {
			return;
		}
		
		EditorImpl editor = getEditorUtils(item);
		if(editor == null) {
			return;
		}
		//销毁Outline
		Control outline = editor.getOutline();
		if(outline != null) {
			outline.dispose();
		}
		
		if(item.isDisposed() == false) {
			//选择当前item，为什么要选择当前Item呢？
			//deSelection(item);
			
			//销毁item的控件
			item.getControl().dispose();
			
			item.dispose();
		}
		
		//销毁编辑器
		editor.doDispose();
		
		editors.remove(item);
		
		this.fireOnDisposed(editor);
	}

	@Override
	public void close(CTabFolderEvent event) {
		CTabItem item = (CTabItem) event.item;
		EditorImpl editor = getEditorUtils(item);
				
		//执行关闭操作
		actions.doAction("close", this.containerContext, "item", item, "editor", editor, "shell", tabFolder.getShell(),
				"container", this);				
	}

	@Override
	public void minimize(CTabFolderEvent event) {
	}

	@Override
	public void maximize(CTabFolderEvent event) {
	}

	@Override
	public void restore(CTabFolderEvent event) {
	}

	@Override
	public void showList(CTabFolderEvent event) {
	}

	@Override
	public List<IEditor> getEditors() {
		List<IEditor> editors  = new ArrayList<IEditor>();
		for(CTabItem item : tabFolder.getItems()) {
			editors.add(getEditorUtils(item));
		}
		return editors;
	}

	@Override
	public List<IEditor> getEditors(boolean dirty) {
		List<IEditor> editors  = new ArrayList<IEditor>();
		for(CTabItem item : tabFolder.getItems()) {
			EditorImpl editor = getEditorUtils(item);
			if(dirty && editor.isDirty()) {
				editors.add(editor);
			}else if(!dirty && !editor.isDirty()) {
				editors.add(editor);
			}			
		}
		
		return editors;
	}

	@Override
	public void close(IEditor editor) {
		if(editor == null) {
			return;
		}
		
		CTabItem item = editor.getActionContext().getObject(ITEM);
		
		//执行关闭操作
		actions.doAction("close", this.containerContext, "item", item, "editor", editor, "shell", tabFolder.getShell(),
				"container", this);
	}

	@Override
	public void stateChanged(IEditorContainer editorContainer, IEditor editor) {
		super.stateChanged(editorContainer, editor);
		
		for(CTabItem item : tabFolder.getItems()) {
			if(editor == getEditorUtils(item)) {
				//可能会执行两次，因此editorModified事件也做了同样的事
				this.updateEditorStatus((EditorImpl) editor, item);
				break;
			}
		}
		
	}

	@Override
	public IEditor getActiveEditor() {
		if(tabFolder.getSelection() != null) {
			return getEditorUtils(tabFolder.getSelection());
		}
		
		return null;
	}
	
	@Override
	public IEditor getEditor(String id) {
		for(IEditor editor : getEditors()) {
			if(editor.getId().equals(id)) {
				return editor;
			}
		}
		
		return null;
	}
}
