package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

public class FloatActionActions {
	public static FloatAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FloatAction action = Actions.action(FloatAction.class);
		
		if(self.getStringBlankAsNull("value") != null){
			action.setValue(self.getFloat("value"));
		}
		
		if(self.getStringBlankAsNull("startValue") != null){
			action.setStart(self.getFloat("startValue"));
		}
		
		if(self.getStringBlankAsNull("endValue") != null){
			action.setEnd(self.getFloat("endValue"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
