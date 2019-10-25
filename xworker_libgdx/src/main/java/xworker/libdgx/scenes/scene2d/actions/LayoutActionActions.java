package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.LayoutAction;

public class LayoutActionActions {
	public static LayoutAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		LayoutAction action = Actions.action(LayoutAction.class);
		Actor actor = (Actor) actionContext.get("actor");
		action.setActor(actor);
		
		if(self.getStringBlankAsNull("layoutEnabled") != null){
			action.setLayoutEnabled(self.getBoolean("layoutEnabled"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
