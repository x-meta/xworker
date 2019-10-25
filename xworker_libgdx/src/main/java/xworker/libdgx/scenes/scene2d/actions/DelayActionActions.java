package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;

public class DelayActionActions {
	public static DelayAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DelayAction action = Actions.action(DelayAction.class);
		if(self.getStringBlankAsNull("duration") != null){
			action.setDuration(self.getFloat("duration"));
		}
		
		if(self.getStringBlankAsNull("time") != null){
			action.setTime(self.getFloat("time"));
		}
		
		DelegateActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
