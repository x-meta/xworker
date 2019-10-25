package xworker.gswt;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.gswt.actions.Duration;
import xworker.gswt.actions.InVisible;
import xworker.gswt.actions.MoveBy;
import xworker.gswt.actions.MoveTo;
import xworker.gswt.actions.Reset;
import xworker.gswt.actions.Visible;

/**
 * 动作。
 * 
 * @author zyx
 *
 */
public abstract class Action {
	public Thing thing;
	public boolean finished = false;
	public List<Action> childActions = new ArrayList<Action>();
	protected  Actor actor;
	
	public Action(Thing thing, ActionContext actionContext){
		this.thing = thing;
		
		for(Thing childActions : thing.getChilds("Childs")){
			for(Thing child : childActions.getChilds()){
				Action action = (Action) child.doAction("create", actionContext);
				if(action != null){
					this.childActions.add(action);
				}
			}
		}
	}
	
	public void setActor(Actor actor){
		this.actor = actor;
	}
	
	public void run(SimpleGame game, ActionContext actionContext){
		if(!finished){
			doAction(game, actionContext);
			
			if(!finished){
				for(Action action : childActions){
					action.run(game, actionContext);
				}
			}
			
			//加入结束事件中的动作
			if(finished){
				for(Thing childActions : thing.getChilds("Finishs")){
					for(Thing child : childActions.getChilds()){
						Action action = (Action) child.doAction("create", actionContext);
						if(action != null){
							actor.addAction(action);
						}
					}
				}
			}
		}
	}
	
	public abstract void doAction(SimpleGame game, ActionContext actionContext);
	
	public static Action moveBy(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new MoveBy(self, actionContext);
	}
	
	public static Action moveTo(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new MoveTo(self, actionContext);
	}
	
	public static Action reset(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new Reset(self, actionContext);
	}
	
	public static Action duration(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new Duration(self, actionContext);
	}
	
	public static Action visible(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new Visible(self, actionContext);
	}
	
	public static Action inVisible(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new InVisible(self, actionContext);
	}
}
