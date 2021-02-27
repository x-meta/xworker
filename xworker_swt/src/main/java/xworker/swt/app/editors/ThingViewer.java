package xworker.swt.app.editors;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;

import xworker.util.XWorkerUtils;

@ActionClass(creator = "createInstance")
public class ThingViewer {
	public void selection(){
        //xworker.swt.app.editors.ThingViewer/@EditorComposite/@thingViewer/@actions/@selection		
        if(actionContext.get("browser") != null){
        	Browser browser = actionContext.getObject("browser");
        	Thing thing = actionContext.getObject("thing");
        	
            String url = XWorkerUtils.getThingDescUrl(thing.getDescriptor());
            browser.setUrl(url);
        }
	}

	public void setContent(){
	    //xworker.swt.app.editors.ThingViewer/@ActionContainer/@setContent
	        
        //判断参数是否存在
		Map<String, Object> params = actionContext.getObject("params");
		Action noParams = actionContext.getObject("noParams");
        if(params == null){    
            noParams.run(actionContext);
            return;
        }
        
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = world.getThing((String) thing);
        }
        actionContext.g().put("thing", thing);
        thingViewer.doAction("setThing", actionContext, "thing", thing);
        
        if(actionContext.get("browser") != null){
        	Browser browser = actionContext.getObject("browser");
            String url = XWorkerUtils.getThingDescUrl(((Thing) thing).getDescriptor());
            browser.setUrl(url);
        }
        //thingEditor.doAction("selectThing", actionContext, "thing", thing);
        //EditorComposite.layout();
        //println thingEditor;
    }

	public Object isSameContent() {
		// xworker.swt.app.editors.ThingViewer/@ActionContainer/@isSameContent
		return false;
		/*
		 * if(actionContext.get("thing") == null){ return false; }
		 * 
		 * if(actionContext.get("params") == null){ return false; }
		 * 
		 * def t = params.thing; if(t instanceof String){ t = world.getThing(t); } if(t
		 * != null && thing.getRoot() == t.getRoot()){ return true; }else{ return false;
		 * }
		 */
	}

	public Object getSimpleTitle() {
		// xworker.swt.app.editors.ThingViewer/@ActionContainer/@getSimpleTitle
		Thing thing = actionContext.getObject("thing");
		if (actionContext.get("thing") != null) {
			return thing.getMetadata().getLabel();
		} else {
			return "ThingViewer";
		}
	}

	public Object getTitle() {
		// xworker.swt.app.editors.ThingViewer/@ActionContainer/@getTitle
		Thing thing = actionContext.getObject("thing");
		if (actionContext.get("thing") != null) {
			return thing.getMetadata().getPath();
		} else {
			return "ThingViewer";
		}
	}

	public static ThingViewer createInstance(ActionContext actionContext) {
		// return new MyClass();
		String key = ThingViewer.class.getName();
		ThingViewer obj = actionContext.getObject(key);
		if (obj == null) {
			obj = new ThingViewer();
			actionContext.g().put(key, obj);
		}

		return obj;
	}

	@ActionField
	public ActionContext actionContext;

	@ActionField
	public Thing thingViewer;
	
	public World world = World.getInstance();
}
