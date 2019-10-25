package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;

public class RemoveActionActions {
	public static RemoveAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RemoveAction action = Actions.action(RemoveAction.class);
		Action ac = (Action) actionContext.get(self.getString("action"));
		if(ac != null){
			action.setAction(ac);
		}
		
		Actor actor = (Actor) actionContext.get(self.getString("actor"));
		if(actor != null){
			action.setActor(actor);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
		
	}
}
