package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class XClickListener extends ClickListener{
	private static Logger logger = LoggerFactory.getLogger(XClickListener.class);
	Thing thing;
	ActionContext actionContext;
	
	public XClickListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
		
	@Override
	public void clicked(InputEvent event, float x, float y) {
		Map<String, Object> params = UtilMap.toMap("event", event, "x", x, "y", y);
		try{	
			thing.doAction("clicked", actionContext, params);
		}catch(Exception e){
			logger.error("do event error ,eventName=clicked, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	public static XClickListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XClickListener l = new XClickListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
