package xworker.swt.app.editors;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.app.IEditor;
import xworker.swt.app.IEditorContainer;

/**
 * Editor的帮助类。Editor的实现一般放在ActionContainer中，通过帮助类便于调用这些方法。
 * 
 * @author zyx
 *
 */
public class EditorImpl implements IEditor, Comparable<EditorImpl>{
	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	Thing editor;
	ActionContainer actions;
	ActionContext actionContext;
	long lastActiveTime = 0;
	String id;
	Control control;
	IEditorContainer editorContainer;
	
	public EditorImpl(IEditorContainer editorContainer, Thing editor, String id, ActionContext parentContext) {
		this.editorContainer = editorContainer;
		this.editor = editor;
		this.id = id;
		this.actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
		actionContext.put("editor", this);
		actionContext.put("editorContainer", editorContainer);
		if(editorContainer != null){
			actionContext.put("workbench", editorContainer.getWorkbench());
		}
	}
	
	public EditorImpl(IEditorContainer editorContainer, Thing editor, String id, ActionContainer actions, ActionContext actionContext) {
		this.editorContainer = editorContainer;
		this.editor = editor;
		this.actions = actions;
		this.actionContext = actionContext;
		this.id = id;
		actionContext.g().put("editor", this);
		actionContext.g().put("editorContainer", editorContainer);
		if(editorContainer != null){
			actionContext.g().put("workbench", editorContainer.getWorkbench());
		}
	}
	
	/**
	 * 创建编辑器自己。
	 * 
	 * @param parent
	 */
	public Control create(Composite parent) {
		 control = editor.doAction("create", actionContext, "parent", parent);
		 actions = actionContext.getObject("actions");
		 return control;
	}
	
	/**
	 * 设置编辑器要编辑的内容。参数有编辑器自行定义。
	 * 
	 * @param params
	 */
	public void setContent(Map<String, Object> params) {
		if(actions != null) {
			actions.doAction("setContent", actionContext, "params", params);
		}
	}
	
	/**
	 * 是否和当前正在编辑的内容一致。如果一致返回true，否则返回false。
	 * 
	 * @param params
	 * @return
	 */
	public boolean isSameContent(Map<String, Object> params) {
		if(actions == null) {
			return false;
		}
		
		Boolean result = actions.doAction("isSameContent", actionContext, "params", params);
		if(result != null) {
			return result;
		}else {
			return false;
		}
	}
	
	/**
	 * 执行保存操作。
	 */
	public void doSave() {
		if(actions != null) {
			actions.doAction("doSave", actionContext);
		}
	}
	
	/**
	 * 数据是否已经被修改了。
	 * @return
	 */
	public boolean isDirty() {
		if(actions == null) {
			return false;
		}
		
		Boolean result = actions.doAction("isDirty", actionContext);
		if(result != null) {
			return result;
		}else {
			return false;
		}
	}
	
	/**
	 * 返回编辑器的短标题。
	 * 
	 * @return
	 */
	public String getSimpleTitle() {
		if(actions == null) {
			return editor.getMetadata().getLabel();
		}
		return actions.doAction("getSimpleTitle", actionContext);
	}
	
	/**
	 * 返回编辑器的长标题。长标题通常显示在Shell的标题上。
	 * 
	 * @return
	 */
	public String getTitle() {
		if(actions == null) {
			return editor.getMetadata().getLabel();
		}
		
		return actions.doAction("getTitle", actionContext);
	}
	
	/**
	 * 返回编辑器的概要组建。不存在返回null。
	 * 
	 * @return
	 */
	public Composite getOutline() {
		if(actions != null) {
			return actions.doAction("getOutline", actionContext);
		}else {
			return null;
		}
	}
	
	public void doDispose() {
		if(actions != null) {
			actions.doAction("doDispose", actionContext);
		}else {
			
		}
	}
	
	public Thing getMenuConfig() {
		if(editor != null) {
			return editor.getThing("MenuConfig@0");
		}else {
			return null;
		}
	}

	public Thing getCoolBarConfig() {
		if(editor != null) {
			return editor.getThing("CoolBarConfig@0");
		}else {
			return null;
		}
	}
	
	public Thing getStatusBarConfig() {
		if(editor != null) {
			return editor.getThing("StatusBarConfig@0");
		}else {
			return null;
		}
	}
	
	public Composite getEditor() {
		return (Composite) control;
	}
	
	public Thing getThing() {
		return editor;
	}

	public ActionContainer getActions() {
		return actions;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	@Override
	public Image getIcon() {
		if(actions != null) {
			Object object = actions.doAction("getIcon", actionContext);
			if(object instanceof  Image){
				return (Image) object;
			}

			return null;
		}else {
			return null;
		}
	}

	@Override
	public int compareTo(EditorImpl o) {
		if(lastActiveTime > o.lastActiveTime) {
			return 1;
		}else if(lastActiveTime == o.lastActiveTime) {
			return 0;
		}else{
			return -1;
		}
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public String getId() {
		return id;
	}

	public Control getControl() {
		return control;
	}
	
	public void setControl(Control control) {
		this.control = control;
	}

	@Override
	public xworker.thingeditor.IEditorContainer<Composite, Control, Image> getEditorContainer() {
		return editorContainer;
	}

	@Override
	public void fireStateChanged() {
		editorContainer.fireStateChanged(this);
	}

	@Override
	public Object doAction(String name) {
		return actions.doAction(name, actionContext);
	}

	@Override
	public Object doAction(String name, Object... params) {
		return actions.doAction(name, actionContext, params);
	}

	@Override
	public ActionContainer getActionContainer() {
		return actions;
	}
}
