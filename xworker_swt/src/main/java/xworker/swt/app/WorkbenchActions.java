package xworker.swt.app;

import java.util.Collections;
import java.util.Map;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.swt.util.SwtUtils;

public class WorkbenchActions {
	public static void initConfig(ActionContext actionContext) {
		if(actionContext.get("config") == null){
		    Map<String, Object> config = UtilMap.toMap("hasOutlineTab", true, 
		                   "hasBottomTab", true, 
		                   "hasStatusBar", true, 
		                   "hasCoolBar", true, 
		                   "hasMenu", true);
		                   
		    actionContext.g().put("config", config);
		}
	}
	
	public static void setMainSashFormWeights(ActionContext actionContext) {
		SashForm mainSashForm = actionContext.getObject("mainSashForm");
		
		mainSashForm.setWeights(new int[] {20, 80});
	}
	
	public static void restart(ActionContext actionContext) {
		Shell shell = actionContext.getObject("shell");
		shell.dispose();
	}
	
	public static Object exit(ActionContext actionContext) {
		IEditorContainer editorContainer = actionContext.getObject("editorContainer");
		Action openSaveDialog = actionContext.getObject("openSaveDialog");
		Shell shell = actionContext.getObject("shell");
		
		if(editorContainer.isDirty()){
		    openSaveDialog.run(actionContext);
		}else{
		    shell.dispose();
		}

		return false;
	}
	
	public static void shellClosed(ActionContext actionContext) {
		//调用workbench的退出方法
		Workbench workbench = actionContext.getObject("workbench");
		ShellEvent event = actionContext.getObject("event");
		workbench.exit();

		if(SwtUtils.isRWT()){
		    //event.doit为false，rwt模式下是异步的，因此如果存在未保存的资源，需要对话框确认
		    event.doit = false;
		}
	}
	
	public static void save(ActionContext actionContext) {
		IEditorContainer editorContainer = actionContext.getObject("editorContainer");
		editorContainer.save();
	}
	
	public static void onDispose(ActionContext actionContext) {
		Shell shell = actionContext.getObject("shell");
	    if(UtilData.isTrue(actionContext.get("result"))){
            shell.dispose();
        }
	}
	
	public static void menuRun(ActionContext actionContext){
        //xworker.swt.app.Workbench/@menuActions/@menuRun
		Thing currentThing = actionContext.getObject("currentThing");
        currentThing.doAction("run", new ActionContext());
    }
    
    public static void restart1(ActionContext actionContext){
        //xworker.swt.app.Workbench/@menuActions/@restart
        IEditorContainer editorContainer = actionContext.getObject("editorContainer");
        Action noEnv = actionContext.getObject("noEnv");
        
        if(editorContainer == null){
            noEnv.run(actionContext);
        }else{
        	ActionContext ac = new ActionContext();
        	CoolBar coolBar = actionContext.getObject("coolBar");
        	Thing currentThing = actionContext.getObject("currentThing");
            coolBar.getShell().dispose();
            currentThing.doAction("run", ac);
        }
    }
    
    public static void openViewer(ActionContext actionContext){
        //xworker.swt.app.Workbench/@menuActions/@openViewer
    	Event event = actionContext.getObject("event");
    			
        String view = ((Thing) event.widget.getData("menu")).getString("extends");
        if(view != null){
            //println view;
            String[] sview = view.split("[|]");
            Workbench workbench = actionContext.getObject("workbench");
            workbench.openView(sview[1], World.getInstance().getThing(sview[1]), sview[0]);
        }
    }
    
    public static void openEditor(ActionContext actionContext){
        //xworker.swt.app.Workbench/@menuActions/@openEditor
    	Workbench workbench = actionContext.getObject("workbench");
    	Event event = actionContext.getObject("event");
    	
        String editor = ((Thing) event.widget.getData("menu")).getString("extends");
        if(editor != null){
            //println view;    
            workbench.openEditor(editor, World.getInstance().getThing(editor), Collections.emptyMap());
        }
    }
    
    public static void openCompositeEditor(ActionContext actionContext){
        //xworker.swt.app.Workbench/@menuActions/@openCompositeEditor
    	Workbench workbench = actionContext.getObject("workbench");
    	Event event = actionContext.getObject("event");
    	
        String composite = ((Thing) event.widget.getData("menu")).getString("extends");
        Thing editor = World.getInstance().getThing("xworker.swt.app.editors.CompositeEditor");
        if(composite != null){
            //println view;    
            workbench.openEditor(composite, editor, UtilMap.toMap("composite", composite));
        }
    }
}
