package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;

import ognl.OgnlException;

public class ColorActionActions {
	public static ColorAction create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ColorAction action = Actions.action(ColorAction.class);
		Color startColor = (Color) self.getObject("startColor", actionContext);
		if(startColor != null){
			action.setColor(startColor);
		}
		
		Color endColor = (Color) self.getObject("endColor", actionContext);
		if(endColor != null){
			action.setEndColor(endColor);
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
