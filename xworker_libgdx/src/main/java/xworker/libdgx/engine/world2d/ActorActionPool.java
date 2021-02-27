package xworker.libdgx.engine.world2d;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.utils.Pool;

import ognl.OgnlException;
import xworker.lang.executor.Executor;
import xworker.libdgx.scenes.scene2d.ActionPool;

public class ActorActionPool extends Pool<ActorAction>{
	private static final String TAG = ActorActionPool.class.getName();
	
	GameThing gameThing;
	ActionPool actionPool;
	Thing thing;
	ActorActionPool parent;
	ActionContext actionContext;
	String group;
	List<ActorActionPool> childs = new ArrayList<ActorActionPool>();
	
	public ActorActionPool(GameThing gameThing, Thing thing, ActionContext actionContext, ActorActionPool parent) throws OgnlException{
		this.gameThing = gameThing;
		this.thing = thing;
		this.parent = parent;
		this.actionContext = actionContext;
		
		actionPool = UtilData.getObjectByType(thing, "actionPool", ActionPool.class, actionContext);
		
		group = thing.getStringBlankAsNull("actionGroup");
		if(group == null){
			group = ActorAction.defaultGroup;
		}
				
		for(Thing child : thing.getChilds()){
			childs.add(new ActorActionPool(gameThing, child, actionContext, this));
		}		
	}
	
	public String getGroup(){
		return group;
	}

	@Override
	protected ActorAction newObject() {
		try {
			return new ActorAction(this);
		} catch (Exception e) {
			Executor.error(TAG, "new object error", e);
			return null;
		}
	}
	
	public String getName(){
		return thing.getMetadata().getName();
	}
}
