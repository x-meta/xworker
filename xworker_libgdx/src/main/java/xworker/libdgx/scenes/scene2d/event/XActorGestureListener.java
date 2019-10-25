package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

public class XActorGestureListener extends ActorGestureListener{
	private static Logger logger = LoggerFactory.getLogger(XActorGestureListener.class);
	
	Thing thing;
	ActionContext actionContext;
	
	public XActorGestureListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
		
	@Override
	public void fling(InputEvent event, float velocityX, float velocityY,
			int button) {
		Map<String, Object> params = UtilMap.toMap("event", event, "velocityX", velocityX, "velocityY", velocityY, "button", button);
		try{			
			thing.doAction("fling", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=fling, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public boolean longPress(Actor actor, float x, float y) {
		Map<String, Object> params = UtilMap.toMap("actor", actor, "x", x, "y", y);
		try{	
			Object obj = thing.doAction("longPress", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=longPress, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public void pan(InputEvent event, float x, float y, float deltaX,
			float deltaY) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "deltaX", deltaX, "deltaY", deltaY);
		try{	
			thing.doAction("pan", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=pan, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void pinch(InputEvent event, Vector2 initialPointer1,
			Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		Map<String, Object> params = UtilMap.toMap("event", event, "initialPointer1", initialPointer1, 
				"initialPointer2", initialPointer2, "pointer1", pointer1, "pointer2", pointer2);
		try{	
			thing.doAction("pinch", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=pinch, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void tap(InputEvent event, float x, float y, int count, int button) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "count", count, "button", button);
		try{	
			thing.doAction("tap", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=tap, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer, "button", button);
		try{	
			thing.doAction("touchDown", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=touchDown, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer, "button", button);
		try{	
			thing.doAction("touchUp", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=touchUp, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void zoom(InputEvent event, float initialDistance, float distance) {
		Map<String, Object> params = UtilMap.toMap("event", event, "initialDistance", initialDistance, "distance", distance);
		try{	
			thing.doAction("zoom", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=zoom, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	public static XActorGestureListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XActorGestureListener l = new XActorGestureListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
