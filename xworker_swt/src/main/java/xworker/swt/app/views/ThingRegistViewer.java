package xworker.swt.app.views;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.util.SwtUtils;

public class ThingRegistViewer {
	public static void defaultSelected(ActionContext actionContext){
		//xworker.swt.app.views.ThingRegistViewer/@thingRegistor/@actions/@defaultSelection
		Thing thing = actionContext.getObject("thing");
		Thing viewThing = actionContext.getObject("viewThing");
		ActionContext parentContext = actionContext.getObject("parentContext");
		
        if(thing != null){
            if(viewThing.getActionThing("defaultSelected") != null){
                viewThing.doAction("defaultSelected", actionContext, "thing", thing, "editorContainer",
                      parentContext.get("editorContainer"),
                     "path", thing.getMetadata().getPath());
            }else{
            	Action openThingEditor = actionContext.getObject("openThingEditor");
                openThingEditor.run(actionContext, "thing", thing, "editorContainer",
                      parentContext.get("editorContainer"),
                      "path", thing.getMetadata().getPath());
            }
        }
    }
    
    public static void init(ActionContext actionContext){
        //xworker.swt.app.views.ThingRegistViewer/@init
        Map<String, Object> params = actionContext.getObject("params");
        World world = World.getInstance();
        
        if(actionContext.get("params") != null){
            if(params.get("thing") instanceof String){
                 //如果事物是路径，获取实际失误
                 params.put("thing", world.getThing((String) params.get("thing")));
            }
        
            //执行事物注册器的初始化   
            ActionContainer thingRegistor = actionContext.getObject("thingRegistor");
            thingRegistor.doAction("init", actionContext, params);
            
            Item viewItem = actionContext.getObject("viewItem");
            if(params.get("title") != null){
                //Item的标题            	
                viewItem.setText((String) params.get("title"));
            }
            
            if(params.get("icon") != null){
                //图标
            	Composite mainComposite = actionContext.getObject("mainComposite");
                Image icon = SwtUtils.createImage(mainComposite, (String) params.get("icon"), actionContext);
                
                if(icon != null){
                     viewItem.setImage(icon);
                }
            }
        }
    }
}
