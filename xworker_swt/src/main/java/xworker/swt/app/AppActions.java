package xworker.swt.app;

import java.util.Collections;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class AppActions {
	public static void openEditor(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		final String id = self.doAction("getId", actionContext);
		//println id;
		final IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		Map<String, Object> params = self.doAction("getParams", actionContext);
		if(params == null) {
			params = Collections.emptyMap();
		}
		final Map<String, Object> ps = params;
		
		editorContainer.getComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					Thing editor = self.doAction("getEditor", actionContext);
					editorContainer.openEditor(id, editor, ps);
				}catch(Exception e) {
					Executor.error(AppActions.class.getSimpleName(), "open editor error", e);
				}
			}
		});
		
	}
	
	public static void saveAllEdtiors(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
			editorContainer.getComposite().getDisplay().asyncExec(new Runnable() {
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
			editorContainer.getComposite().getDisplay().asyncExec(new Runnable() {
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
	
	public static void openView(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Workbench workbench = self.doAction("getWorkbench", actionContext);
		String id = self.doAction("getId", actionContext);
		String type = self.doAction("getType", actionContext);
		Thing composite = self.doAction("getComposite", actionContext);
		boolean closeable = self.doAction("isCloseable", actionContext);
		Map<String, Object> params = self.doAction("getParams", actionContext);

		workbench.getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				try {
					workbench.openView(id, composite, type, closeable, params);
				}catch(Exception e) {
					Executor.error(AppActions.class.getSimpleName(), "open view error", e);
				}
			}
		});
		
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
			editorContainer.getComposite().getDisplay().asyncExec(new Runnable() {
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
