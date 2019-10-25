package xworker.gswt.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Action;
import xworker.gswt.SimpleGame;

public class Duration extends Action{
	long duration = 0;
	
	public Duration(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
		
		duration = thing.getLong("duration");
	}

	@Override
	public void doAction(SimpleGame game, ActionContext actionContext) {
		duration = duration - game.delta;
		if(duration <= 0){
			this.finished = true;
		}
	}

}
