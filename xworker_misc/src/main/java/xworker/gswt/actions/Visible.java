package xworker.gswt.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.Action;
import xworker.gswt.SimpleGame;

public class Visible extends Action{
	public Visible(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void doAction(SimpleGame game, ActionContext actionContext) {	
		actor.visible = true;
		this.finished = true;
	}

}
