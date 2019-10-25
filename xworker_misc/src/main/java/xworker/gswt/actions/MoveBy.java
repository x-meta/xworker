package xworker.gswt.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Action;
import xworker.gswt.SimpleGame;

public class MoveBy extends Action{
	public MoveBy(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void doAction(SimpleGame game, ActionContext actionContext) {
		int x = thing.getInt("x");
		int y = thing.getInt("y");
		
		if(game.delta > 0){
			actor.x = (int) (actor.x + (x / game.delta));
			actor.y = (int) (actor.y + (y / game.delta));
		}
	}

}
