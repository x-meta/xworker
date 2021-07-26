package xworker.ide.worldexplorer.editor.thingEditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.util.UtilBrowser;
import xworker.util.XWorkerUtils;

public class ThingEditorMenuActions {
	private static final String TAG = ThingEditorMenuActions.class.getName();
	
	public static void toolBarItemSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");

		ToolItem item = (ToolItem) event.widget;

		Rectangle rect = item.getBounds();
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = item.getParent().toDisplay(pt);
		Menu menu = (Menu) item.getData("menu");
		menu.setLocation(pt.x, pt.y);
		//menu.update();
		menu.setVisible(true);
	}
	
	public static void menuDispose(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		Widget menu = (Widget) event.widget.getData("menu");
		if(menu != null){
		    menu.dispose();
		}
	}
	
	public static void menuSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		
		String actionUrl = (String) event.widget.getData("url");
		Thing menuThing = (Thing) event.widget.getData("menu");
		ActionContext explorerContext = actionContext.getObject("explorerContext");
		ActionContainer explorerActions = actionContext.getObject("explorerActions");
		Thing currentThing = actionContext.getObject("currentThing");
		World world = World.getInstance();
		CoolBar coolBar = actionContext.getObject("coolBar");
		UtilBrowser utilBrowser = actionContext.getObject("utilBrowser");
		ActionContext currentModelContext = actionContext.getObject("currentModelContext");
		Thing currentModel = actionContext.getObject("currentModel"); 
		ActionContainer actions = actionContext.getObject("actions");
		Action showMessage = actionContext.getObject("showMessage");
		Action showException = actionContext.getObject("showException");

		ActionContext context = new ActionContext();
		context.put("parent", coolBar.getShell());
		context.put("editorShell", coolBar.getShell());
		context.put("explorerActions", explorerActions);
		context.put("explorerContext", explorerContext);
		context.put("thingContext", actionContext);
		context.put("utilBrowser",  utilBrowser);
		context.put("thing", currentThing);
		context.put("currentThing", currentThing);
		context.put("currentModelContext", currentModelContext);
		context.put("currentModel", currentModel);
		context.put("editorContext", actionContext);
		context.put("editorActions", actions);
		context.put("event", actionContext.get("event"));
		context.put("parentContext", actionContext);

		try{
		    if(actionUrl != null){
		        if(actionUrl.startsWith("http:") || actionUrl.startsWith("https:")){
		            explorerActions.doAction("openUrl", explorerContext, "url", actionUrl, "name", actionUrl);
		        }else if(actionUrl == "do?sc="){
		            Thing cfg = world.getThing("_local.xworker.config.GlobalConfig");
		            explorerActions.doAction("openUrl", explorerContext, "url", cfg.getString("webUrl") + actionUrl + currentThing.getMetadata().getPath(), "name", actionUrl);
		        }else if(actionUrl.startsWith("do?")){
		            Thing cfg = world.getThing("_local.xworker.config.GlobalConfig");
		            explorerActions.doAction("openUrl", explorerContext, "url", cfg.getString("webUrl") + actionUrl + "&currentThing=" + currentThing.getMetadata().getPath(), "name",actionUrl);
		        }else if(actionUrl == "doc"){
		            String url = XWorkerUtils.getThingDescUrl(currentThing);
		            explorerActions.doAction("openUrl", explorerContext, "url", url, "name", currentThing.getMetadata().getPath());
		        }else if(actionUrl.startsWith("action:")){
		            String actionName = actionUrl.substring(7, actionUrl.length());
		            currentThing.doAction(actionName, context);
		        }else{
		            Action action = world.getAction(actionUrl);
		            //log.info(actionUrl);
		            if(action != null){    
		                action.run(context);
		            }
		        }
		    }else if(menuThing.getStringBlankAsNull("shell") != null){
		        String shellPath = menuThing.getString("shell");
		        int index = shellPath.indexOf("?");
		        Map<String, String> params = new HashMap<String, String>();
		        if(index != -1){
		            String paramsStr = shellPath.substring(index + 1, shellPath.length());
		            shellPath = shellPath.substring(0, index);
		            params = UtilString.getParams(paramsStr); 
		        }
		        Thing shellThing = (Thing) world.get(shellPath);
		        if(shellThing != null){

					context.put("params", params);
		            Shell newShell = shellThing.doAction("create", context);
		            if(newShell != null){
		                if(UtilData.isTrue(newShell.getData("menuShellDispose"))){
		                    newShell.dispose();
		                }else{
		                    newShell.open();
		                }
		            }
		        }else{
		            //Executor.info(TAG, "shell thing is not exists, path={}", shellPath);
		            showMessage.run(actionContext, "message", "窗体不存在, path=" + shellPath) ;
		        }
		    }else{
		        Executor.warn(TAG, "menu action is null, menuThing=" + menuThing.getMetadata().getPath());
		    	//menuThing.doAction("doAction", actionContext);
		        //showMessage.run(actionContext, "message", "需要设置动作或窗体。");
		    }
		}catch(Exception e){
			showException.run(actionContext, "exception", e);
		    //Executor.error(TAG, "open thing menu errror", e);
		    //showMessage.run(actionContext, "message", e.getMessage());
		}
	}
}
