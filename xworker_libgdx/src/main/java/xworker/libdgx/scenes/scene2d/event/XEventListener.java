package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class XEventListener implements EventListener{
	private static Logger logger = LoggerFactory.getLogger(XEventListener.class);
	ActionContext actionContext;
	Thing thing;
	
	public XEventListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public boolean handle(Event event) {
		Map<String, Object> params = UtilMap.toMap("event", event);
		try{
			Object obj = thing.doAction("event", actionContext, params);
			if(obj instanceof Boolean){
				return (Boolean) obj;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("do event error ,eventName=handle, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
			return false;
		}
	}

	public static XEventListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		XEventListener l = new XEventListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		
		return l;
	}
}
