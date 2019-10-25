package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;

public class RotateByActionActions {
	public static RotateByAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RotateByAction action = Actions.action(RotateByAction.class);
		if(self.getStringBlankAsNull("rotationAmount") != null){
			action.setAmount(self.getFloat("rotationAmount"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
