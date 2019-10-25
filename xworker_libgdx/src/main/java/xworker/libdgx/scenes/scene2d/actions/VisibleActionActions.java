package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;

public class VisibleActionActions {
	public static VisibleAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		VisibleAction action = Actions.action(VisibleAction.class);
		if(self.getStringBlankAsNull("visible") != null){
			action.setVisible(self.getBoolean("visible"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
