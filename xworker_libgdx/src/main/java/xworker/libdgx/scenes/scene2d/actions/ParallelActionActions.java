package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;

public class ParallelActionActions {
	public static ParallelAction create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ParallelAction action = Actions.action(ParallelAction.class);
		Actor actor = (Actor) actionContext.get(self.getString("actor"));
		if(actor != null){
			action.setActor(actor);
		}
		
		addAction(action, self, "action1", actionContext);
		addAction(action, self, "action2", actionContext);
		addAction(action, self, "action3", actionContext);
		addAction(action, self, "action4", actionContext);
		addAction(action, self, "action5", actionContext);
		
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
	
	public static void addAction(ParallelAction paction, Thing self, String name, ActionContext actionContext){
		String v = self.getStringBlankAsNull(name);
		if(v != null){
			Action action = (Action) actionContext.get(v);
			if(action != null){
				paction.addAction(action);
			}
		}
	}
}
