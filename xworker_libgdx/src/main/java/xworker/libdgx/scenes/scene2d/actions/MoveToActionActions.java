package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;


public class MoveToActionActions {
	public static MoveToAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		MoveToAction action = Actions.action(MoveToAction.class);
		
		if(self.getStringBlankAsNull("x") != null){
			action.setX(self.getFloat("x"));
		}
		
		if(self.getStringBlankAsNull("y") != null){
			action.setY(self.getFloat("y"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
