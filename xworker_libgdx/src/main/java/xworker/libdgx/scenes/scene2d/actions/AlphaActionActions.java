package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;

import ognl.OgnlException;

public class AlphaActionActions {
	public static AlphaAction create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		AlphaAction action = Actions.action(AlphaAction.class);
		if(self.getStringBlankAsNull("alpha") != null){
			action.setAlpha(self.getFloat("alpha"));
		}
		
		Color color = (Color) self.getObject("color", actionContext);
		if(color != null){
			action.setColor(color);
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
