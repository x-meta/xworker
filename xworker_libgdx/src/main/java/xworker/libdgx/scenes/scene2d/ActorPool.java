package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;

public class ActorPool extends Pool<Actor>{
	Thing thing;
	ActionContext actionContext;
	
	public ActorPool(Thing thing, ActionContext actionContext){
		this.thing = thing;
		
		//避免创建后的角色进入到原有的变量上下文中
		this.actionContext = new ActionContext(actionContext);
	}
	
	@Override
	protected Actor newObject() {
		if(thing.getChilds().size() > 0){
			return (Actor) thing.getChilds().get(0).doAction("create", actionContext);
		}else{
			return null;
		}
	}

	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActorPool pool = new ActorPool(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), pool);
		
		return pool;
	}
}
