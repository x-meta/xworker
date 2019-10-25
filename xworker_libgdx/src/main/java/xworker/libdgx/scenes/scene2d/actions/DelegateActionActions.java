package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

public class DelegateActionActions {
	public static void init(Thing self, DelegateAction action, ActionContext actionContext){
		String ac = self.getStringBlankAsNull("action");
		if(ac != null){
			action.setAction((Action) actionContext.get("ac"));
		}
		
		String actor = self.getStringBlankAsNull("actor");
		if(actor != null){
			Actor act = (Actor) actionContext.get("actor");
			if(act != null){
				action.setActor(act);
			}
		}
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Action){
				action.setAction((Action) obj);
			}
		}
	}
}
