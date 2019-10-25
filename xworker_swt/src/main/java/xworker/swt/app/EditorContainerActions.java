package xworker.swt.app;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.app.editorContainers.CTabFolderEditorContainer;
import xworker.swt.app.editorContainers.CompositeEditorContainer;

public class EditorContainerActions {
	private static Logger logger = LoggerFactory.getLogger(EditorContainerActions.class);
	
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
			logger.warn("EditorContainer has not supported parent type, parent=" + parent);
		}
	}
}
