package xworker.swt.xwidgets.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.events.SwtListener;

public class AppActions {
	private static Logger logger = LoggerFactory.getLogger(AppActions.class);
	
	/**
	 * xworker.swt.xwidgets.app.AppActions/%SaveEditor动作的实现。
	 * 
	 * @param actionContext
	 */
	public static void saveEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//编辑器的变量上下文
		ActionContext editorContext = self.doAction("getEditorContext", actionContext);
		if(editorContext == null) {
			logger.info("Can not execute saveEditor, editorContext is null, path=" + self.getMetadata().getPath());
			return;
		}
		
		//okButtonSelection
		SwtListener okButtonSelection = editorContext.getObject("okButtonSelection");
		if(okButtonSelection != null) {
			okButtonSelection.handleEvent(null);
			return;
		}
		
		//actions
		ActionContainer actions = editorContext.getObject("actions");
		if(actions != null) {
			actions.doAction("doSave", editorContext);
			return;
		}
		
	}
	
	/**
	 * xworker.swt.xwidgets.app.AppActions/%InitEditor动作的实现。
	 * 
	 * @param actionContext
	 */
	public static void initEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		//参数
		Object params = self.doAction("getParams", actionContext);
		
		//编辑器的变量上下文
		ActionContext editorContext = self.doAction("getEditorContext", actionContext);
		if(editorContext == null) {
			logger.info("Can not execute initEditor, editorContext is null, path=" + self.getMetadata().getPath());
			return;
		}
		
		//actions
		ActionContainer actions = editorContext.getObject("actions");
		if(actions != null) {
			actions.doAction("doInit", editorContext, "params", params);
			return;
		}
		
	}
}
