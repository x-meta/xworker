package xworker.swt.app.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.app.IEditor;
import xworker.util.XWorkerUtils;

@ActionClass(creator="createInstance")
public class ThingRegistEditor {
	private static final String TAG = ThingRegistEditor.class.getName();
    public Object createDataParams(){
        //xworker.swt.app.editors.ThingRegistEditor/@actions1/@createDataParams
        Object data = actionContext.get("data");
        
        if(data instanceof Thing){
            if(((Thing) data).isThing("xworker.swt.xworker.ThingRegistThing")){
                Map<String, Object> params = UtilMap.toMap("thing", data);
                params.put(IEditor.EDITOR_THING, world.getThing("xworker.swt.app.editors.RegistThingEditor"));
                params.put(IEditor.EDITOR_ID, "ThingRegist:" + ((Thing) data).getMetadata().getPath());
                return params;
            }
        }
        
        return null;
    }
    
    public void selected(){
        //xworker.swt.app.editors.ThingRegistEditor/@EditorComposite/@thingRegistor/@actions/@selected
        Thing thing = actionContext.getObject("thing");
        Browser topicBrowser = actionContext.getObject("topicBrowser");
        
        if(actionContext.get("thing") != null && actionContext.get("topicBrowser") != null){
            String url = XWorkerUtils.getThingDescUrl(thing);
            topicBrowser.setUrl(url);
        }
    }
    
    public void loaded(){
        //xworker.swt.app.editors.ThingRegistEditor/@EditorComposite/@thingRegistor/@actions/@loaded
        Thing thing = null;
        if(actionContext.get("first") == null){
            actionContext.g().put("first", false);
            
            if(actionContext.get("path") != null){
            	String path = actionContext.getObject("path");
            	List<Thing> things = actionContext.getObject("things");
                for(Thing tthing : things){
                    //println tthing.getMetadata().getPath();
                    if(tthing.getMetadata().getPath().equals(path)){
                        thing = tthing;
                        break;
                    }
                }
            }
        }
        
        //println path;
        //println thing;
        if(thing != null){
        	Browser browser = actionContext.getObject("browser");
            String url = XWorkerUtils.getThingDescUrl(thing);
            browser.setUrl(url);
            
            ActionContainer thingRegist = actionContext.getObject("thingRegist");
            thingRegist.doAction("selectThing", actionContext, "thing", thing);
        }
    }
    
    public void setContent(){
        //xworker.swt.app.editors.ThingRegistEditor/@actions/@setContent
        //事物参数
    	Map<String, Object> params = actionContext.getObject("params");
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = world.getThing((String) thing);
        }
        if(thing == null){
            Executor.warn(TAG, "RegistThingEdtior: thing is null");
            return;
        }
        actionContext.g().put("thing", thing);
        
        String type = (String) params.get("type");
        if(type == null || "".equals(type)){
            type = "child";
        }
        
        ActionContainer thingRegistor = actionContext.getObject("thingRegistor");
        thingRegistor.doAction("init", actionContext, "thing", thing, "type", type, 
             "autoLoad", true, "keywords", null, "descritporForNewThing", params.get("descritporForNewThing"));
    }
    
    public Object isSameContent(){
        //xworker.swt.app.editors.ThingRegistEditor/@actions/@isSameContent
    	Map<String, Object> params = actionContext.getObject("params");
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = world.getThing((String) thing);
        }
        
        return thing == actionContext.get("thing");
    }
    
    public Object getTitle(){
        //xworker.swt.app.editors.ThingRegistEditor/@actions/@getTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") != null){
            return thing.getMetadata().getPath();
        }else{
            return "No Regit Thing";
        }
    }
    
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.ThingRegistEditor/@actions/@getSimpleTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") != null){
            return thing.getMetadata().getLabel();
        }else{
            return "No Regit Thing";
        }
    }
    

    public static ThingRegistEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = ThingRegistEditor.class.getName();
        ThingRegistEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new ThingRegistEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
    
    public World world = World.getInstance();
}
