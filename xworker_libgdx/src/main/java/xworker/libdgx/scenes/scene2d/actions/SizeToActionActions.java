package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;

public class SizeToActionActions {
	public static SizeToAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SizeToAction action = Actions.action(SizeToAction.class);
		if(self.getStringBlankAsNull("width") != null){
			action.setWidth(self.getFloat("width"));
		}
		
		if(self.getStringBlankAsNull("height") != null){
			action.setHeight(self.getFloat("height"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
