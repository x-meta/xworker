package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class XInputListener extends InputListener{
	private static Logger logger = LoggerFactory.getLogger(XInputListener.class);
	Thing thing;
	ActionContext actionContext;
	
	public XInputListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static XInputListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XInputListener l = new XInputListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer,
			Actor fromActor) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer, "fromActor", fromActor);
		try{
			thing.doAction("enter", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=enter, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer,
			Actor toActor) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer, "fromActor", toActor);
		try{
			thing.doAction("exit", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=exit, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		Map<String, Object> params = UtilMap.toMap("event", event, "keycode", keycode);
		try{
			Object obj = thing.doAction("keyDown", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=keyDown, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public boolean keyTyped(InputEvent event, char character) {
		Map<String, Object> params = UtilMap.toMap("event", event, "character", character);
		try{
			Object obj = thing.doAction("keyTyped", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=keyTyped, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
		Map<String, Object> params = UtilMap.toMap("event", event, "keycode", keycode);
		try{
			Object obj = thing.doAction("keyUp", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=keyUp, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y);
		try{
			Object obj = thing.doAction("mouseMoved", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=mouseMoved, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	public boolean scrolled(InputEvent event, float x, float y, int amount) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "amount", amount);
		try{
			Object obj = thing.doAction("scrolled", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=scrolled, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer, "button", button);
		try{
			Object obj = thing.doAction("touchDown", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=touchDown, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer);
		try{
			thing.doAction("touchDragged", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=touchDragged, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
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
	
}
