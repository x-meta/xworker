package xworker.swt.app.editorContainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;

import xworker.swt.app.IEditor;
import xworker.swt.app.editors.EditorImpl;

public class CompositeEditorContainer extends AbstractEditorContianer{
	StackLayout layout;
	Composite composite;
	List<EditorImpl> editors = new ArrayList<EditorImpl>();
	IEditor activeEditor = null;
	
	public CompositeEditorContainer(Composite composite, ActionContext actionContext) {
		super(actionContext);
		
		if(composite.getLayout() == null) {
			composite.setLayout(new StackLayout());
		}else if(!(composite.getLayout() instanceof StackLayout)) {
			throw new ActionException("Compostie as EditorContainer must has StackLayout");
		}
		
		this.composite = composite;
		this.layout = (StackLayout) composite.getLayout();
	}
	
	@Override
	public IEditor openEditor(String id, Thing editor, Map<String, Object> params) {
		for(EditorImpl editorImpl : editors) {
			if(editorImpl.getId().equals(id)) {
				editorImpl.setContent(params);
				
				//编辑器已经存在，放到最上层				
				setActive(editorImpl);
				return editorImpl;
			}
		}
		
		if(UtilData.isTrue(editor.doAction("hasComposite", this.actionContext)) == false) {
			ActionContext editorContext = new ActionContext();
			editorContext.put("parentContext", containerContext);
			editorContext.put("parent", composite);		
			editorContext.put("editorContainer", this);
			editor.doAction("setContent", editorContext, "params", params);
			return null;			
		}
		
		//编辑器不存在，创建一个
		EditorImpl editorImpl = new EditorImpl(this, editor, id, this.containerContext);
		editorImpl.create(composite);
		editorImpl.setContent(params);
		editors.add(editorImpl);
		setActive(editorImpl);
		
		this.fireOnCreated(editorImpl);
		
		//判断是否超过了上限
		int maxEditors = thing.getInt("maxEditors");
		if(maxEditors > 0 && editors.size() > maxEditors) {
			Collections.sort(editors);
			EditorImpl oldEditor = editors.remove(0);
			if(thing.getBoolean("maxEditorsSaveOnClose")) {
				oldEditor.doSave();
			}
			close(oldEditor);
		}
		
		return editorImpl;
	}
	
	private void setActive(EditorImpl editor) {
		editor.setLastActiveTime(System.currentTimeMillis());
		layout.topControl = editor.getControl();
		composite.layout();
		
		//概要
		if(outlineContainer != null) {
			checkOutline(editor);

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
		
		//触发编辑器的onActive事件
		if(editor.getActions() != null) {
			editor.getActions().doAction("onActive", editor.getActionContext());
		}		
		
		this.fireOnActive(editor);
		
		activeEditor = editor;
	}
	
	
	@Override
	public IEditor getActiveEditor() {
		return activeEditor;
	}

	private EditorImpl getEditor(ActionContainer editorActions) {
		for(EditorImpl editorImpl : editors) {
			if(editorImpl.getActions() == editorActions) {
				return editorImpl;
			}
		}
		
		return null;
	}

	@Override
	public void editorModified(ActionContainer editorActions) {
		thing.doAction("editorModified", actionContext, "editor", getEditor(editorActions));
	}

	@Override
	public boolean isDirty() {
		for(EditorImpl editorImpl : editors) {
			if(editorImpl.isDirty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void saveAll() {
		for(EditorImpl editorImpl : editors) {
			editorImpl.doSave();
		}
	}

	@Override
	public void save() {
		if(activeEditor != null) {
			activeEditor.doSave();
		}
		/*
		for(EditorImpl editorImpl : editors) {
			if(editorImpl.isDirty()) {
				editorImpl.doSave();
			}
		}*/
	}

	@Override
	public List<xworker.workbench.IEditor<Composite, Control, Image>> getEditors() {
		List<xworker.workbench.IEditor<Composite, Control, Image>> items = new ArrayList<>();
		for(EditorImpl editorImpl : editors) {
			items.add(editorImpl);
		}
		return items;
	}

	@Override
	public List<xworker.workbench.IEditor<Composite, Control, Image>> getEditors(boolean dirty) {
		List<xworker.workbench.IEditor<Composite, Control, Image>> items = new ArrayList<>();
		for(EditorImpl editorImpl : editors) {
			if(editorImpl.isDirty() == dirty) {
				items.add(editorImpl);
			}
		}
		return items;
	}

	@Override
	public void close(xworker.workbench.IEditor<Composite, Control, Image> editor) {
		EditorImpl impl = (EditorImpl) editor;
		impl.doDispose();
		editors.remove(impl);
		
		this.fireOnDisposed(editor);
		if(impl == activeEditor) {
			activeEditor = null;
		}
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public IEditor getEditor(String id) {
		for(IEditor editor : editors) {
			if(editor.getId().equals(id)) {
				return editor;
			}
		}
		
		return null;
	}

}
