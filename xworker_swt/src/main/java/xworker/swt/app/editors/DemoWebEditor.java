package xworker.swt.app.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;
import xworker.workbench.EditorParams;

@ActionClass(creator="createInstance")
public class DemoWebEditor {
    public void onOutlineCreated(){
        Thing composite = UtilData.getThing(actionContext.get("thing"));
        Browser browser = actionContext.getObject("browser");
        if(composite != null && browser != null){
            SwtUtils.setThingDesc(composite, browser);
        }
    }

    public void setContent(){    	
        //xworker.swt.app.editors.DemoWebEditor/@actions/@setContent
    	Map<String, Object> params = actionContext.getObject("params");
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = World.getInstance().getThing((String) thing);
        }
        
        actionContext.g().put("thing", thing);
        
        ActionContainer demoWeb = actionContext.getObject("demoWeb");
        demoWeb.doAction("setThing", actionContext, "thing", thing);

        onOutlineCreated();
    }
    
    public Object isSameContent(){
        //xworker.swt.app.editors.DemoWebEditor/@actions/@isSameContent
    	Map<String, Object> params = actionContext.getObject("params");
    	Object thing = params.get("thing");
        if(thing instanceof String){
            thing = World.getInstance().getThing((String) thing);
        }
        
        return actionContext.get("thing") == thing;
    }
    
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.DemoWebEditor/@actions/@getSimpleTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") ==null){
            return "No Web App";
        }else{
            return thing.getMetadata().getLabel();
        }
    }
    
    public Object getTitle(){
        //xworker.swt.app.editors.DemoWebEditor/@actions/@getTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") ==null){
            return "No Web App";
        }else{
            return thing.getMetadata().getPath();
        }
    }

    public static EditorParams<Thing> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Thing thing = null;
        if(content instanceof  String){
            thing = World.getInstance().getThing((String) content);
        }else if(content instanceof Thing){
            thing = (Thing) content;
        }
        if(thing != null){
            if(thing.isThing("xworker.http.controls.SimpleControl")){
                String path = thing.getMetadata().getPath();
                return new EditorParams<Thing>(self, "DemoWeb:" + path, thing) {
                    @Override
                    public Map<String, Object> getParams() {
                        Map<String, Object> params = new HashMap<>();
                        params.put("dataObject", this.getContent().getMetadata().getPath());

                        return params;
                    }
                };
            }
        }

        return null;
    }

    public static DemoWebEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = DemoWebEditor.class.getName();
        DemoWebEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new DemoWebEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
}
