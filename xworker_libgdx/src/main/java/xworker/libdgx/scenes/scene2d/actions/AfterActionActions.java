package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;

public class AfterActionActions {
	public static AfterAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		AfterAction action = Actions.action(AfterAction.class);
		
		DelegateActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
