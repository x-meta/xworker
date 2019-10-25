package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class XChangeListener extends ChangeListener{
	private static Logger logger = LoggerFactory.getLogger(XChangeListener.class);
	Thing thing;
	ActionContext actionContext;
	
	public XChangeListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		Map<String, Object> params = UtilMap.toMap("event", event, "actor", actor);
		try{
			thing.doAction("changed", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=changed, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	public static XChangeListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XChangeListener l = new XChangeListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
