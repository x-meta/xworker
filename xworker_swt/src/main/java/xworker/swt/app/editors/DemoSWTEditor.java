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
public class DemoSWTEditor {
	public void onOutlineCreated(){
		Thing composite = UtilData.getThing(actionContext.get("composite"));
		Browser browser = actionContext.getObject("browser");
		if(composite != null && browser != null){
			SwtUtils.setThingDesc(composite, browser);
		}
	}

    public void setContent(){
	    //xworker.swt.app.editors.DemoSWTEditor/@actions/@setContent
    	Map<String, Object> params = actionContext.getObject("params");
	    Object composite = params.get("composite");
	    if(composite instanceof String){
	        composite = world.getThing((String) composite);
	    }

	    actionContext.g().put("composite", composite);
	    demoSWT.doAction("setComposite", actionContext, "composite", composite);

	    onOutlineCreated();
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
    		if(thing.isThing("xworker.swt.widgets.Composite")){
    			String path = thing.getMetadata().getPath();
				return new EditorParams<Thing>(self, "DemoSWT:" + path, thing) {
					@Override
					public Map<String, Object> getParams() {
						Map<String, Object> params = new HashMap<>();
						params.put("composite", this.getContent().getMetadata().getPath());

						return params;
					}

					@Override
					public int getPriority(){
						return 1000;
					}
				};
			}
		}

    	return null;
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
