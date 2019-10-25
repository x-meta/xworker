package xworker.libdgx.engine.collision;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.utils.Pool;

public class CollisionModelPool extends Pool<CollisionModel> {
	Thing thing;
	ActionContext actionContext;
	
	public CollisionModelPool(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	protected CollisionModel newObject() {
		return new CollisionModel(thing, actionContext);
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		CollisionModelPool pool = new CollisionModelPool(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), pool);
		
		return pool;
	}
}
