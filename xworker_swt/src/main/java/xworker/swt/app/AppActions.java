package xworker.swt.app;

import java.util.Collections;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;
import xworker.workbench.IView;
import xworker.util.XWorkerUtils;

public class AppActions {
	private static final String TAG = AppActions.class.getName();
	
	public static xworker.workbench.IEditor<Composite, Control, Image> getEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String editorId = self.doAction("getEditorId", actionContext);
		IEditorContainer editorContainer = self.doAction("getEditorContainer", actionContext);
		for(xworker.workbench.IEditor<Composite, Control, Image> editor : editorContainer.getEditors()) {
			if(editor.getId().equals(editorId)) {
				return editor;
			}
		}
		
		return null;
	}
	
	public static IView<Composite, Control> getView(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String viewId = self.doAction("getViewId", actionContext);
		Workbench workbench = self.doAction("getWorkbench", actionContext);
		if(workbench == null) {
			throw new ActionException("Workbench is null, thing=" + self.getMetadata().getPath());
		}
		
		return workbench.getView(viewId);
	}
	
	public static xworker.workbench.IEditor<Composite, Control, Image> openEditor(final ActionContext actionContext) {
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

		if(editorContainer == null){
			XWorkerUtils.openEditor(id, editor, params);
		}else {
			editorContainer.getComposite().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						editorContainer.openEditor(id, editor, ps);
					} catch (Exception e) {
						Executor.error(AppActions.class.getSimpleName(), "open editor error", e);
					}
				}
			});
		}
		
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
	
	public static Object createOutline(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//outline的parent控件		
		Composite outlineParent = null;
		IEditorContainer editorContainer = actionContext.getObject("editorContainer");
		String type = null;

		if(actionContext.exists("tab", "outlineComposite")){
		    //XWorker的事物管理器
		    outlineParent = actionContext.getObject("outlineComposite");
		    type = "xworker";
		}else if(actionContext.exists("editorContainer")){
		    //SWT的workbentch
		    outlineParent = editorContainer.getOutlineParent();
		    type = "workbentch";
		}

		//println type;
		if(outlineParent != null){
		    //可以创建Outline
		    ActionContext ac = actionContext;
		    if(UtilData.isTrue(self.doAction("isNewActionContext", actionContext))){
		        ac = new ActionContext();
		        ac.put("parentContext", actionContext);
		    }
		    
		    boolean isBrowser = false;
		    Thing compositeThing = self.doAction("getOutlineComposite", actionContext);
		    if(compositeThing == null){
		        compositeThing = World.getInstance().getThing("xworker.swt.app.prototypes.OutlineComposites/@outlineBrowser");
		        isBrowser = true;
		    }
		    
		    //println "compositeThing=" + compositeThing;
		    ac.peek().put("parent", outlineParent);
		    Control outlineControl = compositeThing.doAction("create", ac);
		    CTabItem tab = actionContext.getObject("tab"); 
		    if(type == "xworker"){
		        tab.setData("outlineTree", outlineControl);
		    }
		    
		    String varName = self.doAction("getVarName", actionContext);
		    if(varName != null){
		        actionContext.g().put(varName, outlineControl);
		    }
		    if(isBrowser){
		        String url = self.doAction("getOutlineBrowserUrl", actionContext);
		        //println "browser=" + outlineControl + ",brower url=" + url;
		        if(url != null){
		            ((Browser) outlineControl).setUrl(url);
		        }
		    }
		    
		    //println "type = " + type + ",outline=" + outlineControl;
		    return outlineControl;
		}else{
		    Executor.info("CreateOutline", "No outline parent, path=" + self.getMetadata().getPath());
		    return null;
		}
	}
	
	public static void exit(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Workbench workbench = self.doAction("getWorkbench", actionContext);
		if(workbench != null){
		    workbench.exit();
		}else{
		    Executor.warn(TAG, "Can not call workbench.exit(), workbench is null, action={}", 
		        self.getMetadata().getPath());
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
	
	public static IView<Composite, Control> openView(ActionContext actionContext) {
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
