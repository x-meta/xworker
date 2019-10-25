package xworker.libdgx.engine.world2d;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ognl.OgnlException;

public class ActorAction {
	//private static Logger logger = LoggerFactory.getLogger(ActorAction.class);
	public static byte RUNNING = 0;
	public static byte PAUSED = 1;
	public static byte FINISHED = 2;
	public final static String defaultGroup = "default";
	GameThing gameThing;
	Action action;
	private Actor actor;
	Thing thing;
	String group;
	ActionContext actionContext;
	ActorActionPool actionScriptPool = null;
	List<ActorAction> childs = new ArrayList<ActorAction>();
	byte status = RUNNING;
	
	public ActorAction(ActorActionPool actionScriptPool) throws OgnlException{
		this.actionScriptPool = actionScriptPool;
		this.gameThing = actionScriptPool.gameThing;
		this.thing = actionScriptPool.thing;
		this.actionContext = actionScriptPool.actionContext;
		
		//只有actor存在的情况下才有action，才有后续
		if(actionScriptPool.actionPool == null){
			throw new ActionException("ActionPool is null, path=" + thing.getMetadata().getPath());
		}
		
		group = thing.getStringBlankAsNull("actionGroup");
		if(group == null){
			group = ActorAction.defaultGroup;
		}
		
		for(ActorActionPool child : actionScriptPool.childs){
			childs.add(new ActorAction(child));
		}		
	}
	
	public void setActor(Actor actor){				
		this.actor = actor;
	}
	
	public String getGroup(){
		return group;
	}
	
	public boolean isStartOnInit(){
		return thing.getBoolean("startOnInit");
	}
	
	public boolean isRoot(){
		return actionScriptPool.parent == null;
	}
	
	public void act(float delta){
		if(action != null && actor != null && action.getActor() == null){
			//动作已经执行完毕
			String nextActions = thing.getStringBlankAsNull("nextActions");
			if(nextActions != null){
				for(String next : nextActions.split("[,]")){
					gameThing.startAction(next, actor);
				}
			}
			
			status = ActorAction.FINISHED;
						
			clean();
		}
	}
	
	private void clean(){
		if(actionScriptPool.actionPool != null && action != null){
			actionScriptPool.actionPool.free(action);
		}
		actionScriptPool.free(this);
		this.action = null;
	}
	
	public boolean isFinished(){
		return status == ActorAction.FINISHED;
	}
	
	public boolean isRunning(){
		return status == ActorAction.RUNNING;
	}
	
	public void stop(){
		if(actor != null && action != null){
			actor.removeAction(action);
		}
		
		for(ActorAction child : childs){
			child.stop();
		}
		
		status = ActorAction.FINISHED;
		clean();
	}
	
	public void pause(){
		actor.removeAction(action);
		status = ActorAction.PAUSED;
	}
	
	public Action start(){
		if(action ==null){
			action = actionScriptPool.actionPool.obtain();
		}
		
		if(actor != null && action != null){			
			action.reset();
			actor.addAction(action);
		}
		
		for(ActorAction child : childs){ 
			child.start();
		}
		
		status = ActorAction.RUNNING;
		
		return action;
	}
	
	public void resume(){
		if(actor != null && action != null){			
			actor.addAction(action);
		}
		
		for(ActorAction child : childs){
			child.resume();
		}
		
		status = ActorAction.RUNNING;
	}
}
