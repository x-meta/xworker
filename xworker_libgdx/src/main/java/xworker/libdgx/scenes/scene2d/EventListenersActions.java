package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class EventListenersActions {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object parent = actionContext.get("parent");
		boolean capture = false;
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			
			if(obj instanceof EventListener){
				EventListener l = (EventListener) obj;
				
				if(capture){
					if(parent instanceof Stage){
						((Stage) parent).addCaptureListener(l);
					}else if(parent instanceof Actor){
						((Actor) parent).addCaptureListener(l);
					}
				}else{
					if(parent instanceof Stage){
						((Stage) parent).addListener(l);
					}else if(parent instanceof Actor){
						((Actor) parent).addListener(l);
					}
				}
			}
		}
	}
	
	public static void createCaptureListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object parent = actionContext.get("parent");
		boolean capture = true;
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			
			if(obj instanceof EventListener){
				EventListener l = (EventListener) obj;
				
				if(capture){
					if(parent instanceof Stage){
						((Stage) parent).addCaptureListener(l);
					}else if(parent instanceof Actor){
						((Actor) parent).addCaptureListener(l);
					}
				}else{
					if(parent instanceof Stage){
						((Stage) parent).addListener(l);
					}else if(parent instanceof Actor){
						((Actor) parent).addListener(l);
					}
				}
			}
		}
	}
	
	
}
