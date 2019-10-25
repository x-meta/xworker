package xworker.util;

import java.io.File;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public class XWorkerUtilsActions {
	public static Object getIde(ActionContext actionContext){
		return XWorkerUtils.getIde();
	}
	
	public static void ideOpenThing(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenThing(thing);
	}
	
	public static void ideOpenFile(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = (File) self.doAction("getFile", actionContext);
		XWorkerUtils.ideOpenFile(file);
	}
	
	public static void ideOpenThingAndSelectCodeLine(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		String codeAttrName = (String) self.doAction("getCodeAttrName", actionContext);
		Integer lineIndex = (Integer) self.doAction("getLineIndex", actionContext);
		XWorkerUtils.ideOpenThingAndSelectCodeLine(thing, codeAttrName, lineIndex);
	}
	
	public static void ideOpenComposite(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getCompositeThing", actionContext);
		XWorkerUtils.ideOpenComposite(thing);
	}
	
	public static void ideOpenUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String url = (String) self.doAction("getUrl", actionContext);
		XWorkerUtils.ideOpenUrl(url);
	}
	
	public static void ideOpenThingTab(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenThingTab(thing);
	}
	
	public static void ideOpenWebControl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenWebControl(thing);
	}
	
	public static String getWebControlUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		return XWorkerUtils.getWebControlUrl(thing);
	}
	
	public static String getWebUrl(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getWebUrl();
	}
	
	@SuppressWarnings("unchecked")
	public static void ideDoAction(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String name = (String) self.doAction("getActionName", actionContext);
		Map<String, Object> params = (Map<String, Object>) self.doAction("getParams", actionContext);
		XWorkerUtils.ideDoAction(name, params);
	}
	
	public static ActionContainer getIdeActionContainer(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getIdeActionContainer();
	}
	
	public static Object getIDEShell(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getIDEShell();
	}
	
	public static void ideShowMessageBox(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String title = (String) self.doAction("getTitle", actionContext);
		String message = (String) self.doAction("getMessage", actionContext);
		Integer style = (Integer) self.doAction("getStyle", actionContext);
		
		XWorkerUtils.ideShowMessageBox(title, message, style);
	}
	

	public static Object getThingDescUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		
		return XWorkerUtils.getThingDescUrl(thing);
	}
	
	public static Thing getThingIfNotExistsCopy(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String thing = (String) self.doAction("getThing", actionContext);
		String thingManager = (String) self.doAction("getThingManager", actionContext);
		String copyFrom = (String) self.doAction("getCopyFrom", actionContext);
		
		return XWorkerUtils.getThingIfNotExistsCopy(thing, thingManager, copyFrom);
	}
	
	public static Thing getThingIfNotExistsCreate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String thing = (String) self.doAction("getThing", actionContext);
		String thingManager = (String) self.doAction("getThingManager", actionContext);
		String descriptor = (String) self.doAction("getDescriptor", actionContext);
		
		return XWorkerUtils.getThingIfNotExistsCreate(thing, thingManager, descriptor);
	}
}
