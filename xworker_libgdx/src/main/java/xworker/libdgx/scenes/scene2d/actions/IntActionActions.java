package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;

public class IntActionActions {
	public static IntAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		IntAction action = Actions.action(IntAction.class);
		
		if(self.getStringBlankAsNull("value") != null){
			action.setValue(self.getInt("value"));
		}
		
		if(self.getStringBlankAsNull("startValue") != null){
			action.setStart(self.getInt("startValue"));
		}
		
		if(self.getStringBlankAsNull("endValue") != null){
			action.setEnd(self.getInt("endValue"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
