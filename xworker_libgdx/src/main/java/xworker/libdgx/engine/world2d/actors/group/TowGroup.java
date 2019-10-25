package xworker.libdgx.engine.world2d.actors.group;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import xworker.libdgx.scenes.scene2d.ActorActions;

/**
 * 拖拽，一个Actor拖拽一串Actor。
 * 
 * @author Administrator
 *
 */
public class TowGroup extends Actor{
	List<TowableActor> actors = new ArrayList<TowableActor>();
	Thing thing;
	ActionContext actionContext;
	
	public TowGroup(Thing thing, ActionContext actionContext){
		//this.setVisible(false);		
		this.setSize(1, 1);
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void act(float delta){
		super.act(delta);
		
		if(actors.size() > 0){
			TowableActor actor = actors.get(0);
			actor.setStart(new Vector2(this.getX(), this.getY()));
			//System.out.println(this.getX() + ":" + this.getY());
						
			for(int i=1; i<actors.size(); i++){
				TowableActor pre = actors.get(i-1);
				TowableActor cur = actors.get(i);
				cur.setStart(pre.end);
			}
		}
	}
	
	public void addActor(Actor actor){
		//super.addActor(actor);
		
		TowableActor ta = new TowableActor(actor);		
		if(actors.size() > 0){
			TowableActor last = actors.get(actors.size() - 1);
			ta.setStart(last.end);
		}
		
		actors.add(ta);
	}
		
	@Override
	public void draw(Batch batch, float parentAlpha) {
		for(int i=0; i<actors.size(); i++){
			TowableActor actor = actors.get(i);
			actor.actor.draw(batch, parentAlpha);
		}
	}

	@Override
	public void drawDebug(ShapeRenderer shapes) {
		for(int i=0; i<actors.size(); i++){
			TowableActor actor = actors.get(i);
			actor.actor.drawDebug(shapes);
		}
	}

	public static void childActorCreated(ActionContext actionContext){
		TowGroup parent = (TowGroup) actionContext.get("parent");
		Actor actor = (Actor) actionContext.get("actor");
		parent.addActor(actor);
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TowGroup tg = new TowGroup(self, actionContext);
		
		ActorActions.initActor(self, tg, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), tg);
		return tg;
	}
}
