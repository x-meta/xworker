package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

public class ActionPool extends Pool<Action>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionPool(Thing thing, ActionContext actionContext){
		this.thing = thing;
		
		//避免创建后的角色进入到原有的变量上下文中
		this.actionContext = new ActionContext(actionContext);
	}
	
	@Override
	protected Action newObject() {
		if(thing.getChilds().size() > 0){
			Action action = (Action) thing.getChilds().get(0).doAction("create", actionContext);
			if(action != null){
				//action已经被本池所管理，因此取消原来的池
				action.setPool(null);
			}
			
			return action;
		}else{
			return null;
		}
	}

	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionPool pool = new ActionPool(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), pool);
		
		return pool;
	}

}
