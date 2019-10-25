package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SequenceActionActions {
	public static SequenceAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SequenceAction action = Actions.action(SequenceAction.class);
		
		Actor actor = (Actor) actionContext.get(self.getString("actor"));
		if(actor != null){
			action.setActor(actor);
		}
		
		ParallelActionActions.addAction(action, self, "action1", actionContext);
		ParallelActionActions.addAction(action, self, "action2", actionContext);
		ParallelActionActions.addAction(action, self, "action3", actionContext);
		ParallelActionActions.addAction(action, self, "action4", actionContext);
		ParallelActionActions.addAction(action, self, "action5", actionContext);
		
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", null);
			bindings.put("actor", null);
			
			for(Thing child : self.getChilds()){
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Action){
					action.addAction((Action) obj);
				}
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
		
	}
}
