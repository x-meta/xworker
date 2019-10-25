package xworker.libdgx.engine.world2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ognl.OgnlException;

public class GameThing extends Actor{
	/** 对应定义游戏的事物 */
	Thing thing;
	
	/** 变量上下文 */
	ActionContext actionContext;
	
	/** 当前所在的游戏世界 */
	GameWorld world;
	
	/** 角色组 */
	Group actorsGroup = new Group();
		
	/** 正在执行角色动作的缓存 */
	Map<String, Map<Actor, ActorAction>> actionScripts = new HashMap<String, Map<Actor, ActorAction>>();
	Map<String, ActorActionPool> actionScriptPools = new HashMap<String, ActorActionPool>();
	
	public GameThing(GameWorld world, Thing thing, ActionContext actionContext){
		this.world = world;
		this.thing = thing;
		
		this.actionContext = actionContext;
				
		//自己是不可见的
		this.setVisible(false);
		
		Stage stage = world.getScreen().getCurrentStage();
		if(stage != null){
			stage.addActor(actorsGroup);
		}
		actorsGroup.addActor(this);
		
		//初始化
		init();
		
		world.addGameThing(this);
	}
	
	public void init(){
		actionContext.peek().put("gameThing", this);
		for(Thing child : thing.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//启动初始		
		for(String key : actionScripts.keySet()){
			Map<Actor, ActorAction> scripts = actionScripts.get(key);
			for(Actor ackey : scripts.keySet()){
				ActorAction script = scripts.get(ackey);
				if(script.isRoot() && script.isStartOnInit()){
					script.start();
				}
			}
		}
	}
	
	public Action startAction(String name, Actor actor){
		ActorActionPool pool = actionScriptPools.get(name);
		if(pool != null){
			//先停止相同组的动作
			stopAtionByGroup(pool.getGroup(), actor);
			
			ActorAction script = pool.obtain();
			script.setActor(actor);
			
			Map<Actor, ActorAction> scripts = actionScripts.get(name);
			if(scripts == null){
				scripts = new HashMap<Actor, ActorAction>();
				actionScripts.put(name, scripts);
			}
			
			scripts.put(actor, script);
			
			return script.start();
		}else{
			return null;
		}
	}

	public void stopAtionByGroup(String group, Actor actor){
		for(String key : actionScriptPools.keySet()){
			ActorActionPool pool = actionScriptPools.get(key);
			if(pool.getGroup().equals(group)){
				this.stopAction(key, actor);
			}
		}
		
	}
	
	public void stopAction(String name, Actor actor){
		Map<Actor, ActorAction> scripts = actionScripts.get(name);
		if(scripts != null){
			ActorAction script = scripts.get(actor);
			if(script != null){
				script.stop();
				scripts.remove(actor);
			}
		}	
	}
	
	public void pauseAction(String name, Actor actor){
		Map<Actor, ActorAction> scripts = actionScripts.get(name);
		if(scripts != null){
			ActorAction script = scripts.get(actor);
			if(script != null){
				script.pause();
			}
		}	
	}
	
	public void resumeAction(String name, Actor actor){
		Map<Actor, ActorAction> scripts = actionScripts.get(name);
		if(scripts != null){
			ActorAction script = scripts.get(actor);
			if(script != null){
				script.resume();
			}
		}	
	}
	
	public void putActionScriptPool(ActorActionPool pool){
		actionScriptPools.put(pool.getName(), pool);
	}
	
	/** 
	 * 从游戏世界中移除自己。
	 * 
	 */
	public void removeThing(){
		Stage stage = world.getScreen().getCurrentStage();
		if(stage != null){
			stage.getActors().removeValue(actorsGroup, true);
		}
	}
	
	public void act(float delta){
		for(String key : actionScripts.keySet()){
			List<Actor> finishedList = new ArrayList<Actor>();
			Map<Actor, ActorAction> ass = actionScripts.get(key);
			for(Actor ackey : ass.keySet()){
				ActorAction as = ass.get(ackey);
				if(as.isRunning()){
					as.act(delta);
				}else if(as.isFinished()){
					finishedList.add(ackey);
				}
			}
			
			for(Actor ackey : finishedList){
				ass.remove(ackey);
			}
		}
		
		thing.doAction("act", actionContext, UtilMap.toMap("delta", delta));
	}
	
	public static GameThing create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GameWorld gameWorld = (GameWorld) actionContext.get("gameWorld");
		
		GameThing gameThing = new GameThing(gameWorld, self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), gameThing);
		
		return gameThing;
	}
	
	public static void createChilds(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
	
	public static void createActorActionPool(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GameThing gameThing = (GameThing) actionContext.get("gameThing");
		
		for(Thing child : self.getChilds()){
			try {
				ActorActionPool actionScriptPool = new ActorActionPool(gameThing, child, actionContext, null);
				gameThing.putActionScriptPool(actionScriptPool);
				actionContext.getScope(0).put(child.getMetadata().getName(), actionScriptPool);
			} catch (OgnlException e) {
				e.printStackTrace();
			}			
		}
	}
}
