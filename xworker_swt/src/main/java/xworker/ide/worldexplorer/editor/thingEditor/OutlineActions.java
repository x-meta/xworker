package xworker.ide.worldexplorer.editor.thingEditor;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.Executor;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class OutlineActions {
	private final static String TAG = OutlineActions.class.getName();
	@SuppressWarnings("unchecked")
	public static void attributeTableSelection(ActionContext actionContext) {
		World world = World.getInstance();
		Event event = actionContext.getObject("event");

		//获取选中的事物路径
		TableItem item = ((Table) event.widget).getSelection()[0];

		//在浏览器中显示文档
		Map<String, Object> data = (Map<String, Object>) item.getData();
		Thing thing = world.getThing((String) data.get("path"));
		Browser outlineBrowser = actionContext.getObject("outlineBrowser");
		SwtUtils.setThingDesc(thing, outlineBrowser);
	}
	
	public static void attributeTableDefaultSelection(ActionContext actionContext) {
		World world = World.getInstance();
		Event event = actionContext.getObject("event");

		//属性的描述者
		//TableItem item = (TableItem) event.item;
		String name = (String) event.item.getData("name");
		ActionContainer actions = actionContext.getObject("actions");
		Thing thing = actions.doAction("xmlEditorGetSelectThing", actionContext);
		if(thing == null){
		    return;
		}

		Thing descriptor = thing.getAttributeDescriptor(name);

		if(descriptor != null){
		    ActionContext ac = new ActionContext();
		    Table attributesTable = actionContext.getObject("attributesTable");
		    ac.put("parent", attributesTable.getShell());
		    ac.put("thing", thing.getRoot().getMetadata().getPath());
		    ac.put("currentThing", thing);
		    ac.put("ec", actionContext);
		    
		    Thing desthing = new Thing("xworker.lang.MetaDescriptor3");
		    desthing.addChild(descriptor.detach());
		    
		    Thing newThing = new Thing(desthing.getMetadata().getPath());
		    newThing.put(name, thing.get(name));
		    
		    Thing shellThing = world.getThing("xworker.ide.worldexplorer.swt.command.EditAttributeShell");
		    Shell shell = shellThing.doAction("create", ac);
		    shell.setText("编辑属性-" + descriptor.getMetadata().getName() 
		              + "(" + descriptor.getMetadata().getLabel() + ")");
		    
		    Thing thingForm = ac.getObject("thingForm");
		    thingForm.doAction("setThing", ac, "thing", newThing);
		    shell.open();
		    thingForm.doAction("setFocus", ac);
		    
		    SwtDialog dialog = new SwtDialog(shell, ac);
		    dialog.open(new SwtDialogCallback() {

				@SuppressWarnings("unchecked")
				@Override
				public void disposed(Object result) {
					if(result == null) {
						return;
					}
					
					Map<String, Object> values = (Map<String, Object>) result;
					
					thing.put(name, values.get(name));
			        //重新刷新XML
			        actions.doAction("xmlEditorSetXmlThing", actionContext, "thing", thing.getRoot());
			        
			        actions.doAction("xmlEditorShowThing", actionContext, "thing", thing);
				}		    	
		    });		    
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void actionsTableSelection(ActionContext actionContext) {
		World world = World.getInstance();
		Event event = actionContext.getObject("event");

		//获取选中的事物路径
		TableItem item = ((Table) event.widget).getSelection()[0];

		//在浏览器中显示文档
		Map<String, Object> data = (Map<String, Object>) item.getData();
		Thing thing = world.getThing((String) data.get("path"));		

		Browser outlineBrowser = actionContext.getObject("outlineBrowser");		
		String url = XWorkerUtils.getWebControlUrl(World.getInstance().getThing("xworker.ide.worldexplorer.swt.http.ThingDoc/@showThingAction"));
		ActionContainer actions = actionContext.getObject("actions");
		Thing currentThing = actions.doAction("getCurrentThing", actionContext);
		url = url + "&thing=" + currentThing.getMetadata().getPath() + "&action=" + thing.getMetadata().getName();
		outlineBrowser.setUrl(url);
	}
	
	@SuppressWarnings("unchecked")
	public static void actionsViewTypeMenuSelection(ActionContext actionContext) {
		Table actionsTable = actionContext.getObject("actionsTable");

		//获取选中的事物路径
		TableItem item = actionsTable.getSelection()[0];

		//在浏览器中显示文档
		Map<String, Object> data = (Map<String, Object>) item.getData();
		Thing thing = World.getInstance().getThing((String) data.get("path"));	
		Browser outlineBrowser = actionContext.getObject("outlineBrowser");	
		outlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
	}
	
	public static void actionsExecuteMenuSelection(ActionContext actionContext) {
		Table actionsTable = actionContext.getObject("actionsTable");
		String actionName = (String) actionsTable.getSelection()[0].getData("name");
		ActionContainer actions = actionContext.getObject("actions");
		Thing thing = actions.doAction("getCurrentThing", actionContext);
		Executor.info(TAG, "Execute thing action, thing={}, action={}", thing.getMetadata().getPath(), actionName);
		Executor.info(TAG, "Result={}", (Object) thing.doAction(actionName, actionContext));
	}
	
	public static void actionsExecuteBackMenuSelection(ActionContext actionContext) {
		Table actionsTable = actionContext.getObject("actionsTable");
		String actionName = (String) actionsTable.getSelection()[0].getData("name");
		ActionContainer actions = actionContext.getObject("actions");
		Thing thing = actions.doAction("getCurrentThing", actionContext);
					
		new Thread(new Runnable() {
			public void run() {
				Executor.info(TAG, "Execute thing action, thing={}, action={}", thing.getMetadata().getPath(), actionName);
				Executor.info(TAG, "Result={}", (Object) thing.doAction(actionName, actionContext));
			}
		}).start();
	}
	
	@SuppressWarnings("unchecked")
	public static void descriptorTableSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		
		//获取选中的事物路径
		TableItem item = ((Table) event.widget).getSelection()[0];

		//在浏览器中显示文档
		Map<String, Object> data = (Map<String, Object>) item.getData();
		Thing thing = World.getInstance().getThing((String) data.get("path"));	
		
		Browser outlineBrowser = actionContext.getObject("outlineBrowser");	
		SwtUtils.setThingDesc(thing, outlineBrowser);
		//outlineBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));

		//动作文档
		ActionContainer actions = actionContext.getObject("actions");
		actions.doAction("initOutlineThingActions", actionContext, "thing", thing);
	}
	
	@SuppressWarnings("unchecked")
	public static void descriptorTableDefaultSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		
		//获取选中的事物路径
		TableItem item = ((Table) event.widget).getSelection()[0];

		//在浏览器中显示文档
		Map<String, Object> data = (Map<String, Object>) item.getData();
		Thing thing = World.getInstance().getThing((String) data.get("path"));	
		
		XWorkerUtils.ideOpenThing(thing);
	}
}
