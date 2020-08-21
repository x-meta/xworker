package xworker.swt.app;

import java.util.Collections;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class AppActions {
	
	public static IEditor getEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String editorId = self.doAction("getEditorId", actionContext);
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		for(IEditor editor : editorContainer.getEditors()) {
			if(editor.getId().equals(editorId)) {
				return editor;
			}
		}
		
		return null;
	}
	
	public static View getView(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String viewId = self.doAction("getViewId", actionContext);
		Workbench workbench = self.doAction("getWorkbench", actionContext);
		if(workbench == null) {
			throw new ActionException("Workbench is null, thing=" + self.getMetadata().getPath());
		}
		
		return workbench.getView(viewId);
	}
	
	public static IEditor openEditor(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		final String id = self.doAction("getId", actionContext);
		//println id;
		final IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		Map<String, Object> params = self.doAction("getParams", actionContext);
		if(params == null) {
			params = Collections.emptyMap();
		}
		final Map<String, Object> ps = params;
		final Thing editor = self.doAction("getEditor", actionContext);
		
		editorContainer.getComposite().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					editorContainer.openEditor(id, editor, ps);
				}catch(Exception e) {
					Executor.error(AppActions.class.getSimpleName(), "open editor error", e);
				}
			}
		});
		
		return editorContainer.getEditor(id);
		
	}
	
	public static void saveAllEdtiors(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
			editorContainer.getComposite().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						editorContainer.saveAll();
					}catch(Exception e) {
						Executor.error(AppActions.class.getSimpleName(), "save all editor error", e);
					}
				}
			});		   
		}
	}
	
	public static void saveEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
			editorContainer.getComposite().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						editorContainer.save();
					}catch(Exception e) {
						Executor.error(AppActions.class.getSimpleName(), "save all editor error", e);
					}
				}
			});		   
		}
	}
	
	public static boolean isEditorDirty(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
		    return editorContainer.isDirty();
		}else {
			return false;
		}
	}
	
	public static View openView(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Workbench workbench = self.doAction("getWorkbench", actionContext);
		String id = self.doAction("getId", actionContext);
		String type = self.doAction("getType", actionContext);
		Thing composite = self.doAction("getComposite", actionContext);
		boolean closeable = self.doAction("isCloseable", actionContext);
		Map<String, Object> params = self.doAction("getParams", actionContext);

		workbench.getShell().getDisplay().syncExec(new Runnable(){
			public void run() {
				try {
					workbench.openView(id, composite, type, closeable, params);
				}catch(Exception e) {
					Executor.error(AppActions.class.getSimpleName(), "open view error", e);
				}
			}
		});
		
		return workbench.getView(id);
	}
	
	public static void closeEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		IEditor editor = self.doAction("getEditor", actionContext);

		if(editorContainer == null){
			Action getEditorContainer = actionContext.getObject("getEditorContainer");
		    editorContainer = getEditorContainer.run(actionContext);
		}
		if(editor == null){
			Action getEditor = actionContext.getObject("getEditor");
		    editor = getEditor.run(actionContext);
		}

		//println editorContainer;
		//println editor;		
		if(editorContainer != null && editor != null){
			final IEditorContainer ed = editorContainer;
			final IEditor edi = editor;
			editorContainer.getComposite().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						ed.close(edi);
					}catch(Exception e) {
						Executor.error(AppActions.class.getSimpleName(), "save all editor error", e);
					}
				}
			});			
		}
	}
}
