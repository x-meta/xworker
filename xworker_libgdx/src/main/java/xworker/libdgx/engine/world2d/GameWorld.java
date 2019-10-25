package xworker.libdgx.engine.world2d;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.libdgx.XWorkerScreen;

/**
 * 游戏世界。
 * 
 * @author Administrator
 *
 */
public class GameWorld {
	Thing thing;
	ActionContext actionContext;
	XWorkerScreen screen;
	List<GameThing> gameThings = new ArrayList<GameThing>();
	List<Collide> collides = new ArrayList<Collide>();
	List<GameEngine2D> gameEngines = new ArrayList<GameEngine2D>();
	
	public GameWorld(XWorkerScreen screen, Thing thing, ActionContext actionContext){
		this.screen = screen;
		this.thing = thing;
		this.actionContext = actionContext;
	
		this.actionContext.peek().put("gameWorld", this);
		for(Thing child : thing.getChilds()){
			child.doAction("create", this.actionContext);
		}
		
		screen.addWorld(this);
	}
	
	public XWorkerScreen getScreen(){
		return screen;
	}
	
	public void addGameThing(GameThing gameThing){
		gameThings.add(gameThing);
		
	}
	
	public void addCollide(Collide collide){
		collides.add(collide);
	}
	
	public void run(float delta){		
		for(int i=0; i<gameThings.size(); i++){
			GameThing gameThing = gameThings.get(i);
			gameThing.act(delta);
		}
		
		for(int i=0; i<collides.size(); i++){
			Collide collide = collides.get(i);
			collide.checkCollide(delta);
		}
		
		for(int i=0; i<gameEngines.size(); i++){
			GameEngine2D gameEngine = gameEngines.get(i);
			gameEngine.render(delta);
		}
	}
	
	public static Thing create(ActionContext actionContext){
		XWorkerScreen screen = (XWorkerScreen) actionContext.get("screen");
		Thing self = (Thing) actionContext.get("self");
		
		GameWorld gameWorld = new GameWorld(screen, self, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), gameWorld);
		return self;
	}
	
	public static Thing createThing(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing thing = new Thing(self.getMetadata().getPath());
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), thing);
		return thing;
	}
	
	public static void createGames(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GameWorld gameWorld = (GameWorld) actionContext.get("gameWorld");
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof GameEngine2D){
				gameWorld.gameEngines.add((GameEngine2D) obj);
			}
		}
	}
}
