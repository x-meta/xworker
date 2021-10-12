package xworker.ide.worldexplorer;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

public class Editor {
	/** Editor的标识，用来区分每一次Editor */
	String id;
	
	/** 编辑器模型 */
	Thing editorThing;
	
	/** 需要编辑的对象 */
	Object object;

	/** 编辑器所在的变量上下文 */
	ActionContext actionContext = new ActionContext();
	
	/** 编辑器的动作容器 */
	ActionContainer actions;
	
	Composite editorComposite;
	Composite outlineComposite;
	
	EditorListener listener;
	
	public Editor(Thing editorThing, Object object, String objectId) {
		this.id = editorThing.getMetadata().getPath() + "|" + objectId;
		this.editorThing = editorThing;
		this.object = object;
		
		actionContext.g().put("editor", this);
	}
		
	public EditorListener getListener() {
		return listener;
	}

	public void setListener(EditorListener listener) {
		this.listener = listener;
	}

	/**
	 * 创建编辑器。
	 */
	public void create(Composite editorParent, Composite outlineParent) {
		editorThing.doAction("create", actionContext, "editorParent", editorParent, 
				"outlineParent", outlineParent, "editor", this);
		
		actions = actionContext.getObject("actions");
		editorComposite = actions.doAction("getEditorComposite", actionContext); 
		outlineComposite = actions.doAction("getOutlineComposite", actionContext);
	}
	
	/**
	 * 返回编辑器区域的Composite。
	 * 
	 * @return
	 */
	public Composite getEditorComposite() {
		return editorComposite;
	}
	
	/**                                                                                                                                                                                                                                                                                                                                                                               
	 * 返回概要区域的Composite。
	 * 
	 * @return
	 */
	public Composite getOutlineComposite() {
		return outlineComposite;
		
	}
	
	public ActionContainer getActionContainer() {
		return actions;
	}


	public String getId() {
		return id;
	}


	public Thing getEditorThing() {
		return editorThing;
	}


	public Object getObject() {
		return object;
	}


	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public void save() {		
		doAction("save", actionContext);
	}
	
	public boolean isDirty() {
		Boolean dirty = doAction("isDirty", actionContext);
		if(dirty == null) {
			return false; 
		}else {
			return dirty;
		}
	}
	
	public String getTitle() {
		return doAction("getTitle", actionContext);
	}
	
	public String getTip() {
		return doAction("getTip", actionContext);
	}
	
	public <T> T doAction(String name, ActionContext actionContext) {
		if(actions != null) {
			return actions.doAction(name, actionContext, "editor", this);
		}else {
			return null;
		}
	}
	
	public <T> T doAction(String name, ActionContext actionContext, Object ... params) {
		if(actions != null) {
			Map<String, Object> ps = UtilMap.toMap(params);
			ps.put("editor", this);
			return actions.doAction(name, actionContext, ps);
		}else {
			return null;
		}
	}
	
	public Image getImage() {
		return doAction("getImage", actionContext);
	}
}
