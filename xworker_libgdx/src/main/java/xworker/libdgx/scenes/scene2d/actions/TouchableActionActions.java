package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;

import xworker.libdgx.ConstantsUtils;

public class TouchableActionActions {
	public static TouchableAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TouchableAction action = Actions.action(TouchableAction.class);
		
		String touchable = self.getStringBlankAsNull("touchable");
		if(touchable != null){
			Touchable t = ConstantsUtils.getTouchable(touchable);
			if(t != null){
				action.setTouchable(t);
			}
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
