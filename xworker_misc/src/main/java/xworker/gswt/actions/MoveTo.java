package xworker.gswt.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Action;
import xworker.gswt.SimpleGame;

public class MoveTo extends Action{
	public MoveTo(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void doAction(SimpleGame game, ActionContext actionContext) {	
		int x = Math.abs(thing.getInt("x"));
		int y = Math.abs(thing.getInt("y"));
		int speedX = thing.getInt("speedX");
		int speedY = thing.getInt("speedY");
		
		if(game.delta > 0){
			int moveX = (int) (game.delta * speedX / 1000);
			int moveY = (int) (game.delta * speedY / 1000);
			
			int disX = Math.abs(x - actor.x);
			int disY = Math.abs(y - actor.y);
			
			if(disX <= moveX){
				actor.x = x;
			}else{
				if(x > actor.x){
					actor.x = actor.x + moveX;
				}else{
					actor.x = actor.x - moveX;
				}
			}
			if(disY <= moveY){
				actor.y = y;
			}else{
				if(y > actor.y){
					actor.y = actor.y + moveY;
				}else{
					actor.y = actor.y - moveY;
				}
			}
			
			if(actor.x == x && actor.y == y){
				this.finished = true;
			}
		}
	}


}
