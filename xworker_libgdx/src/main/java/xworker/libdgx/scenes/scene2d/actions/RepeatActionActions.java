package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

public class RepeatActionActions {
	public static RepeatAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RepeatAction action = Actions.action(RepeatAction.class);
		action.setCount(self.getInt("count"));
		
		DelegateActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
