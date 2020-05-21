package xworker.swt.app;

import java.util.Collections;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AppActions {
	public static void openEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String id = self.doAction("getId", actionContext);
		//println id;
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		Thing editor = self.doAction("getEditor", actionContext);
		Map<String, Object> params = self.doAction("getParams", actionContext);
		if(params == null) {
			params = Collections.emptyMap();
		}

		editorContainer.openEditor(id, editor, params);
	}
	
	public static void saveAllEdtiors(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
		    editorContainer.saveAll();
		}
	}
	
	public static void saveEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		if(editorContainer != null){
		    editorContainer.save();
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

		workbench.openViewer(id, composite, type, closeable, params);
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
		    editorContainer.close(editor);
		}
	}
}
