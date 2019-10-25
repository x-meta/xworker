package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class XAction extends Action{
	Thing thing;
	ActionContext actionContext;
	
	public XAction(){		
	}
	
	public XAction(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public boolean act(float delta) {
		Object obj = thing.doAction("act", actionContext, UtilMap.toMap("delta", delta));
		if(obj instanceof Boolean){
			return (Boolean) obj;
		}else{
			return false;
		}
	}
	
	public static Action create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		XAction action = Actions.action(XAction.class);
		action.thing = self;
		action.actionContext = actionContext;
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		return action;
	}
}
