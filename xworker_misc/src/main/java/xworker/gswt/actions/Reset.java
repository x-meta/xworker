package xworker.gswt.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Action;
import xworker.gswt.SimpleGame;

public class Reset extends Action{
	public Reset(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void doAction(SimpleGame game, ActionContext actionContext) {	
		actor.x = actor.orignX;
		actor.y = actor.orignY;
		
		this.finished = true;
	}

}
