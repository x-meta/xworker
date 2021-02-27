package xworker.swt.app;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.app.editorContainers.CTabFolderEditorContainer;
import xworker.swt.app.editorContainers.CompositeEditorContainer;

public class EditorContainerActions {
	private static final String TAG = EditorContainerActions.class.getName();
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object parent = self.doAction("getParent", actionContext);
		if(parent instanceof CTabFolder) {
			CTabFolderEditorContainer editorContainer = new CTabFolderEditorContainer((CTabFolder) parent, actionContext);
			actionContext.g().put(self.getMetadata().getName(), editorContainer);
		}else if(parent instanceof Composite) {
			CompositeEditorContainer editorContainer = new CompositeEditorContainer((Composite) parent, actionContext);
			actionContext.g().put(self.getMetadata().getName(), editorContainer);
		}else {
			Executor.warn(TAG, "EditorContainer has not supported parent type, parent=" + parent);
		}
	}
}
