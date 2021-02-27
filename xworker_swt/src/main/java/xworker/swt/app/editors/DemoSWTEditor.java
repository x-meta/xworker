package xworker.swt.app.editors;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;

@ActionClass(creator="createInstance")
public class DemoSWTEditor {
    public void setContent(){
	    //xworker.swt.app.editors.DemoSWTEditor/@actions/@setContent
    	Map<String, Object> params = actionContext.getObject("params");
	    Object composite = params.get("composite");
	    if(composite instanceof String){
	        composite = world.getThing((String) composite);
	    }
	    
	    actionContext.g().put("composite", composite);
	    demoSWT.doAction("setComposite", actionContext, "composite", composite);
	}
	public Object isSameContent(){
	    //xworker.swt.app.editors.DemoSWTEditor/@actions/@isSameContent
		Map<String, Object> params = actionContext.getObject("params");
	    Object composite = params.get("composite");
	    if(composite instanceof String){
	        composite = world.getThing((String) composite);
	    }
	    
	    return actionContext.get("composite") == composite;
	}
	public Object getSimpleTitle(){
	    //xworker.swt.app.editors.DemoSWTEditor/@actions/@getSimpleTitle
	    if(actionContext.get("composite") ==null){
	        return "No Composite";
	    }else{
	    	Thing composite = actionContext.getObject("composite");
	    	
	        return composite.getMetadata().getLabel();
	    }
	}
	public Object getTitle(){
	    //xworker.swt.app.editors.DemoSWTEditor/@actions/@getTitle
	    if(actionContext.get("composite") ==null){
	        return "No Composite";
	    }else{
	    	Thing composite = actionContext.getObject("composite");
	        return composite.getMetadata().getPath();
	    }
	}
	
	public static DemoSWTEditor createInstance(ActionContext actionContext){
	    //return new MyClass();    
	    String key = DemoSWTEditor.class.getName();
	    DemoSWTEditor obj = actionContext.getObject(key);
	    if(obj == null){
	        obj = new DemoSWTEditor();
	        actionContext.g().put(key, obj);
	    }
	    
	    return obj;
	}    
	
	World world = World.getInstance();
	
	@ActionField
	ActionContainer demoSWT;
	
	@ActionField
	ActionContext actionContext;
	
	@ActionField
	public org.eclipse.swt.widgets.Shell shell;
}
