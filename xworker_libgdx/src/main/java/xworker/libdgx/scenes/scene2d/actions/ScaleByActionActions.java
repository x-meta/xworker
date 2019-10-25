package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;

public class ScaleByActionActions {
	public static ScaleByAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ScaleByAction action = Actions.action(ScaleByAction.class);
		
		if(self.getStringBlankAsNull("amount") != null){
			action.setAmount(self.getFloat("amount"));
		}
		
		if(self.getStringBlankAsNull("amountX") != null){
			action.setAmountX(self.getFloat("amountX"));
		}
		
		if(self.getStringBlankAsNull("amountY") != null){
			action.setAmountY(self.getFloat("amountY"));
		}
		
		TemporalActionActions.init(self, action, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
}
