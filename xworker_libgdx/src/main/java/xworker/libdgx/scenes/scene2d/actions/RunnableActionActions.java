package xworker.libdgx.scenes.scene2d.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

import ognl.OgnlException;

public class RunnableActionActions {
	private static Logger logger = LoggerFactory.getLogger(RunnableActionActions.class);
	
	public static RunnableAction create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		RunnableAction action = Actions.action(RunnableAction.class);
		Runnable runnable = (Runnable) self.getObject("runnable", actionContext);
		if(runnable != null){
			action.setRunnable(runnable);
		}else{
			Runnable r = new R(self, actionContext);
			action.setRunnable(r);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), action);
		
		return action;
	}
	
	static class R implements Runnable{
		Thing thing;
		ActionContext actionContext;
		
		public R(Thing thing, ActionContext actionContext){
			this.thing = thing;
			this.actionContext = actionContext;
		}
		
		public void run(){
			try{
				thing.doAction("doAction", actionContext);
			}catch(Exception e){
				logger.error("run action error, thing=" + thing.getMetadata().getPath(), e);
			}
		}
	}
}
