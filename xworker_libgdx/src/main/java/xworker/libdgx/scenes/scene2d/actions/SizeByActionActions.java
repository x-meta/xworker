package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SizeByAction;

public class SizeByActionActions {
	public static SizeByAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SizeByAction action = Actions.action(SizeByAction.class);
		
		if(self.getStringBlankAsNull("amountHeight") != null){
			action.setAmountHeight(self.getFloat("amountHeight"));
		}
		
		if(self.getStringBlankAsNull("amountWidth") != null){
			action.setAmountWidth(self.getFloat("amountWidth"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
