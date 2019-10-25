package xworker.libdgx.engine.world2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

import ognl.OgnlException;
import xworker.util.UtilMath;

public class GameThingActions {
	private static Logger logger = LoggerFactory.getLogger(GameThingActions.class);
	
	public static void setActorAction(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		GameThing gameThing = UtilData.getObjectByType(self, "gameThing", GameThing.class, actionContext);
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		String action = UtilString.getString(self, "action", actionContext);
		
		if(gameThing != null && actor != null){
			gameThing.startAction(action, actor);
		}else{
			logger.warn("GameThing or Actor is null, thing=" + self.getMetadata().getPath());
		}
	}
	
	public static void moveActorToCurentPosition(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		GameThing gameThing = UtilData.getObjectByType(self, "gameThing", GameThing.class, actionContext);
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		String action = UtilString.getString(self, "action", actionContext);
		
		if(gameThing != null && actor != null){
			Action ac = gameThing.startAction(action, actor);
			if(ac instanceof MoveToAction){
				MoveToAction moveTo = (MoveToAction) ac;
				
				Object event = actionContext.get("event");
				if(event instanceof InputEvent){
					InputEvent ie = (InputEvent) event;
					moveTo.setPosition(ie.getStageX(), ie.getStageY());
					
					float speed = self.getFloat("speed");
					if(speed > 0){
						double dis = UtilMath.getDistance(actor.getX(), actor.getY(), ie.getStageX(), ie.getStageY());
						float duration = (float) (dis / speed);
						moveTo.setDuration(duration);
					}
				}
			}
		}else{
			logger.warn("GameThing or Actor is null, thing=" + self.getMetadata().getPath());
		}
	}
}
