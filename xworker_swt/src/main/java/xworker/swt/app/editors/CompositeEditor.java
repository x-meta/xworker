package xworker.swt.app.editors;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

import xworker.swt.app.IEditor;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;

public class CompositeEditor {
	public static void setContent(ActionContext actionContext) {
		Map<String, Object> params = actionContext.getObject("params");
		
		//判断参数是否存在
		if(params == null){    
			Action noParams = actionContext.getObject("noParams");
		    noParams.run(actionContext);
		    return;
		}

		//如果是路径转为事物
		World world = World.getInstance();
		if(params.get("composite") instanceof String){
		    params.put("composite", world.getThing((String) params.get("composite")));
		} 

		actionContext.g().put("compositeEditorParams", params);

		//创造Composite
		Composite editorComposite = actionContext.getObject("editorComposite");
		for(Control child : editorComposite.getChildren()){
		    child.dispose();
		}
		
		Thing composite = (Thing) params.get("composite");
		if(composite != null){
			//内容使用独立的变量上下文，和CompositeEditor自身的变量上下文区分开，因为变量可能会重复
			ActionContext editorContext = new ActionContext();
			editorContext.put("parentContext", actionContext);
			editorContext.put("parent", editorComposite);
			
		    composite.doAction("create", editorContext);
		    editorComposite.layout();
		    
		    actionContext.g().put("editorContext", editorContext);
		    ActionContainer actions = editorContext.getObject("actions");
		    if(actions != null && actions.getActionThing("doInit") != null) {
		    	actions.doAction("doInit", editorContext, "params", params);
		    }
		    //System.out.println(actions);
		}
	}
		
	private static <T> T invokeEditorActions(String name, ActionContext actionContext) {
		ActionContext editorContext = actionContext.getObject("editorContext");
		if(editorContext == null) {
			return null;
		}
		
		ActionContainer actions = editorContext.getObject("actions");
	    if(actions != null && actions.getActionThing(name) != null) {
	    	return actions.doAction(name, editorContext);
	    }else {
	    	return null;
	    }
	}
	
	public static boolean isSameContent(ActionContext actionContext) {
		if(actionContext.get("compositeEditorParams") == null){
		    return false;
		}

		if(actionContext.get("params") == null){
		    return false;
		}    

		Map<String, Object> params = actionContext.getObject("params");
		Map<String, Object> compositeEditorParams = actionContext.getObject("compositeEditorParams");
		//如果是路径转为事物
		World world = World.getInstance();
		if(params.get("composite") instanceof String){
		    params.put("composite", world.getThing((String) params.get("composite")));
		} 

		if(compositeEditorParams.equals(params)){
		    return true;
		}else{
		    return false;
		}
	}
	
	public static String getSimpleTitle(ActionContext actionContext) {
		Map<String, Object> compositeEditorParams = actionContext.getObject("compositeEditorParams");
		String simpleTitle = invokeEditorActions("getSimpleTitle", actionContext);
		if(simpleTitle != null) {
			return simpleTitle;
		}
				
		if(actionContext.get("compositeEditorParams") != null){
			simpleTitle = (String) compositeEditorParams.get("simpleTitle");
			if(simpleTitle != null) {
				return simpleTitle;
			}
			
		    if(compositeEditorParams.get("composite") != null){
		    	Thing composite = (Thing) compositeEditorParams.get("composite");
		        return composite.getMetadata().getLabel();
		    }else{
		        return "No Composite";
		    }
		}else{
		    return "No Composite";
		}
	}
	
	public static String getTitle(ActionContext actionContext) {
		Map<String, Object> compositeEditorParams = actionContext.getObject("compositeEditorParams");
		String title = invokeEditorActions("getTitle", actionContext);
		if(title != null) {
			return title;
		}
		
		if(actionContext.get("compositeEditorParams") != null){
			title = (String) compositeEditorParams.get("title");
			if(title != null) {
				return title;
			}
			
		    if(compositeEditorParams.get("composite") != null){
		    	Thing composite = (Thing) compositeEditorParams.get("composite");
		        return composite.getMetadata().getPath();
		    }else{
		        return "No Composite";
		    }
		}else{
		    return "No Composite";
		}
	}
	
	public static Image getIcon(ActionContext actionContext) {
		Map<String, Object> compositeEditorParams = actionContext.getObject("compositeEditorParams");
		Composite editorComposite = actionContext.getObject("editorComposite");
		
		Object icon = compositeEditorParams.get("icon");
		if(icon instanceof Image) {
			return (Image) icon;
		}else if(icon instanceof String) {
			return SwtUtils.createImage(editorComposite, (String) icon, actionContext);
		}else {
			return null;
		}
		
	}
	
	public static void doDispose(ActionContext actionContext) {
		Composite editorComposite = actionContext.getObject("editorComposite");
		
		invokeEditorActions("doDispose", actionContext);
		
		if(editorComposite.isDisposed() == false){
		    editorComposite.dispose();
		}
	}
	
	public static void doSave(ActionContext actionContext) {
		invokeEditorActions("doSave", actionContext);
	}
	
	public static boolean isDirty(ActionContext actionContext) {
		return UtilData.isTrue(invokeEditorActions("isDirty", actionContext));
	}
	
	public static Object getOutline(ActionContext actionContext) {
		return invokeEditorActions("getOutline", actionContext);
	}
	
	public static Map<String, Object> createDataParams(ActionContext actionContext){
		Object data = actionContext.getObject("data");
		if(data instanceof Thing) {
			Thing thing = (Thing) data;
			if(thing.isThing("xworker.swt.widgets.Composite") 
					|| thing.isThing("xworker.swt.xworker.ThingRegistSelector")
					|| thing.isThing("xworker.swt.xwidgets.QuickContent")) {
				return UtilMap.toMap("composite", data, 
						IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.CompositeEditor"),
						IEditor.EDITOR_ID, "Composite:" + thing.getMetadata().getPath());
			}
		}
		
		return null;
	}
}
