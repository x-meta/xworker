package xworker.libdgx.scenes.scene2d.event;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import xworker.lang.executor.Executor;

public class XClickListener extends ClickListener{
	private static final String TAG = XClickListener.class.getName();
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
			Executor.error(TAG, "do event error ,eventName=clicked, params=" + params + ",thing=" + thing.getMetadata().getPath(), e);
		}
	}

	public static XClickListener create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XClickListener l = new XClickListener(self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), l);
		return l;
	}
}
