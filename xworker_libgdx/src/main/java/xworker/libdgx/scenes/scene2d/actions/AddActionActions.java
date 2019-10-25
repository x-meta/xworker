package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;

public class AddActionActions {
	public static AddAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Action ac = (Action) actionContext.get(self.getString("action"));
		Actor actor = (Actor) actionContext.get(self.getString("actor"));
		AddAction action = Actions.addAction(ac, actor);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
