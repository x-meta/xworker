package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;

public class RotateToActionActions {
	public static RotateToAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RotateToAction action = Actions.action(RotateToAction.class);
		
		if(self.getStringBlankAsNull("rotation") != null){
			action.setRotation(self.getFloat("rotation"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
