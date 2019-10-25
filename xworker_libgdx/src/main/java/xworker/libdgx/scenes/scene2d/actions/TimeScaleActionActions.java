package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TimeScaleAction;

public class TimeScaleActionActions {
	public static TimeScaleAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TimeScaleAction action = Actions.action(TimeScaleAction.class);
		if(self.getStringBlankAsNull("scale") != null){
			action.setScale(self.getFloat("scale"));
		}
		
		DelegateActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
