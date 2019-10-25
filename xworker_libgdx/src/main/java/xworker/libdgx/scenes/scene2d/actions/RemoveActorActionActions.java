package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;

public class RemoveActorActionActions {
	public static RemoveActorAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RemoveActorAction action = Actions.action(RemoveActorAction.class);
		
		Actor removeActor = (Actor) actionContext.get(self.getString("removeActor"));
		action.setTarget(removeActor);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
