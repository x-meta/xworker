package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class XDragListener extends DragListener{
	private static Logger logger = LoggerFactory.getLogger(XDragListener.class);
	Thing thing;
	ActionContext actionContext;
	
	public XDragListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer);
		try{		
			thing.doAction("drag", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=drag, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void dragStart(InputEvent event, float x, float y, int pointer) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer);
		try{		
			thing.doAction("dragStart", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=dragStart, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y, "pointer", pointer);
		try{		
			thing.doAction("dragStop", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=dragStop, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	public static XDragListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XDragListener l = new XDragListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
