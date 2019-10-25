package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveListenerAction;

public class RemoveListenerActionActions {
	public static RemoveListenerAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RemoveListenerAction ac = Actions.action(RemoveListenerAction.class);
		
		if(self.getStringBlankAsNull("capture") != null){
			ac.setCapture(self.getBoolean("capture"));
		}
		
		EventListener listener = (EventListener) actionContext.get("listener");
		ac.setListener(listener);
		
		Actor actor = (Actor) actionContext.get("actor");
		ac.setActor(actor);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), ac);
		
		return ac;
	}
}
