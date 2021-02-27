package xworker.lang.actions;

import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

public class RefAction {
	private static final String TAG = RefAction.class.getName();
	
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Action action = World.getInstance().getAction(self.getString("refActionPath"));
		if(action == null){
			Executor.info(TAG, "Referenced action not exists, path=" + self.getMetadata().getPath());
			return null;
		}else{
			if(self.getBoolean("keepSelf")){  
	            List<Thing> things = actionContext.getThings();
	            Thing s = null;
	            if(things.size() > 1){
	                s = things.get(things.size() - 2);
	            }
	            actionContext.peek().put("self", s);
	        }
			
			return action.run(actionContext);
		}
	}
}
