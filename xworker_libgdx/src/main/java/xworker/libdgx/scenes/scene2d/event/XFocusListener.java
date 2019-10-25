package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

public class XFocusListener extends FocusListener{
	private static Logger logger = LoggerFactory.getLogger(XFocusListener.class);
	Thing thing;
	ActionContext actionContext;
	
	public XFocusListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}


	@Override
	public void keyboardFocusChanged(FocusEvent event, Actor actor,
			boolean focused) {
		Map<String, Object> params = UtilMap.toMap("event", event, "actor", actor, "focused", focused);
		try{	
			thing.doAction("keyboardFocusChanged", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=keyboardFocusChanged, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}


	@Override
	public void scrollFocusChanged(FocusEvent event, Actor actor,
			boolean focused) {
		Map<String, Object> params = UtilMap.toMap("event", event, "actor", actor, "focused", focused);
		try{	
			thing.doAction("scrollFocusChanged", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=scrollFocusChanged, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}


	public static XFocusListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XFocusListener l = new XFocusListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
