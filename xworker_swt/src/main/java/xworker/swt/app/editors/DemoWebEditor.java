package xworker.swt.app.editors;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;

@ActionClass(creator="createInstance")
public class DemoWebEditor {
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
