package xworker.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.*;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.UtilResource;
import xworker.ide.executor.RequestCountListener;
import xworker.lang.executor.Executor;
import xworker.swt.SwtThingEditor;
import xworker.swt.app.View;
import xworker.swt.app.Workbench;
import xworker.swt.design.Designer;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

import java.net.MalformedURLException;
import java.util.Locale;

@ActionClass(creator="createInstance")
public class SimpleThingEditor {
	private static final String TAG = SimpleThingEditor.class.getName();

	public void save() {
		workbench.save();
	}
	
	public void saveAll() {
		workbench.saveAll();
	}
	
	public void changeLanguage() {
		Action saveDialog = actionContext.getObject("saveDialog");

		Workbench oldWorkbench = workbench;
		//当有编辑器未保存时，不修改语言
		if(oldWorkbench.getEditorContainer() != null && oldWorkbench.getEditorContainer().isDirty()){
		    saveDialog.run(actionContext);
		    return;
		}

		boolean zh = true;

		Session session = SessionManager.getSession(null);
		Locale locale = session.getLocale();
		if(locale != null && !locale.getLanguage().equals(new Locale("en").getLanguage())){
		    zh = true;
		}else{
		    zh = false;
		}

		if(zh == true){
		    Locale l = new Locale("en", "US");
		    session.setLocale(l);
		    session.setI18nResource(UtilResource.getInstance(l));
		}else{
		    Locale l = new Locale("zh", "CN");
		    session.setLocale(l);
		    session.setI18nResource(UtilResource.getInstance(l));
		}

		//重新启动Workbench
		Thing thing =  oldWorkbench.getThing();

		//为避免系统退出，先建一个shell，保证不会触发最后一个Shell退出时系统退出
		Shell shell = new Shell(oldWorkbench.getShell().getDisplay(), SWT.NONE);
		Display oldDisplay = shell.getDisplay();
		
		//重新创建一个Workbench
		ActionContext ac = new ActionContext();
		ac.put("parent", shell.getDisplay());
		thing.doAction("run", ac);
				
		//关闭之前的
		oldWorkbench.exit();
		shell.dispose();
		
		try{
		    if(oldDisplay.getShells().length == 0){
		        oldDisplay.dispose();
		    }
		}catch(Exception e){
		}
	}
	
	public Object restartSelection() {
		Action restart = actionContext.getObject("restart");
		
		if(workbench.getEditorContainer().isDirty()){
		    Action openSaveDialog = world.getAction("xworker.swt.app.prototypes.WorkbenchShell/@actions/@exit/@ActionDefined/@openSaveDialog");
		    openSaveDialog.run(actionContext);
		}else{
			//重新启动
		    restart.run(new ActionContext());
		    
		    shell.dispose();
		}

		return false;
	}
	
	public void resourceManagerSelection() {
		Action showMenu = actionContext.getObject("showMenu");
		Action openEditor = actionContext.getObject("openEditor");
		
		if(event.detail == SWT.ARROW){
		    showMenu.run(actionContext);
		}else{
		    openEditor.run(actionContext);
		}
	}
	
	public void onCreate() {
		if(XWorkerUtils.hasXWorker()){
		    //如果XWorker存在，默认打开主页
		    //openXWorkerHomeEditor(actionContext);
		}

		//自定义的配置
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing editorConfig = parentContext.getObject("simpleThingEditorConfig");
		if(editorConfig != null){
			editorConfig.doAction("create", actionContext, "workbench", workbench);
		}
	}
	
	public void init() {
		//启用设计器
		//Designer designer = Designer.getDesigner();
		Designer.setEnabled(true);

		//println "SimpleThingEditor inited.";
		try{
		    /*
		    if(shell == XWorkerUtils.getIDEShell()){
		        def thingManager = world.getThingManagers().get(0).getName();
		        ThingLibs.registLib(thingManager, "thingslib", "工作目录事物库");
		    }*/
		    
		    //启动注册缓存
		    ThingUtils.startRegistThingCache();

		    //外部的FileThingManager自动监听文件改动
		    Thing thing = world.getThing("_local.xworker.config.InnerThingManagers.dml");
		    if(thing != null){
		        for(ThingManager thingManager : world.getThingManagers()){
		            if(!(thingManager instanceof FileThingManager)){
		                continue;
		            }

		            String thName = thingManager.getName();             
		            for(Thing thingManagerThing : thing.getChilds("ThingManager")){
		                if(thName == thingManagerThing.getMetadata().getName()){
		                    ((FileThingManager) thingManager).setMonitor(true);
		                }             
		            }
		        }
		    }
		    
		    Thing actionsThing = world.getThing("xworker.swt.xwidgets.prototypes.SimpleThingEditor/@ActionContainer");
		    XWorkerUtils.getIde().getActionContainer().append(actionsThing);
		    
		    Action preventCtrlS = actionContext.getObject("preventCtrlS");
		    try {
		    	preventCtrlS.run(actionContext);
		    }catch(Exception e) {		    	
		    }

		    //初始化通知和请求的监听，用于显示未读数据的数量
		    ToolItem systemMessageItem = actionContext.getObject("systemMessageItem");
			new RequestCountListener(systemMessageItem, actionContext);
		}catch(Exception e){
		    Executor.info(TAG, "Init simple editor error", e);
		}
	}

    public static SimpleThingEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = SimpleThingEditor.class.getName();
        SimpleThingEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new SimpleThingEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    public void refreshProjectTree(ActionContext actionContext) {    	
    	View view = workbench.getView("SimpleThingEditor_XWokerExplorer");
    	view.doAction("refreshProjectTree");
    }

    World world = World.getInstance();
    
    @ActionField
    public ActionContext actionContext;
    
    @ActionField
    public Event event;
    
    @ActionField
    public Shell shell;
    
    @ActionField
    public xworker.swt.app.Workbench workbench;
    
    public static void run() throws MalformedURLException {
		SwtThingEditor.run();
	}
}
