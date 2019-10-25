package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

public class ScaleToActionActions {
	public static ScaleToAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ScaleToAction action = Actions.action(ScaleToAction.class);
		if(self.getStringBlankAsNull("scale") != null){
			action.setScale(self.getFloat("scale"));
		}
		
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
