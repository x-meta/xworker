package xworker.libdgx.engine.collision;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.scenes.scene2d.Actor;

import ognl.OgnlException;

/**
 * 可以碰撞的物品。
 * 
 * @author Administrator
 *
 */
public class CollideableActor extends Collideable{
	/** 游戏中的对象 */
	private Actor actor;
	/** 碰撞模型列表 */
	private List<CollisionModel> collisionModels = new ArrayList<CollisionModel>();
	/** 是否通过actor更新位置等，如果对象是角色并且角色是会移动位置的 */
	boolean updateXY = false;
	boolean updateScale = false;
	boolean updateRotation = false;
	
	public CollideableActor(Thing thing, Actor actor, CollisionManager collisionManager, ActionContext actionContext){
		super(thing, collisionManager, actionContext);
		this.actor = actor;
		
		this.type = thing.getString("type");
		this.updateXY = thing.getBoolean("updateXY");
		this.updateScale = thing.getBoolean("updateScale");
		this.updateRotation = thing.getBoolean("updateRotation");
				
		collisionManager.putObject(this);
	}
	
	public void update(){
		if((updateXY || updateScale || updateRotation) && actor != null){
			for(CollisionModel model : collisionModels){
				if(updateXY){
					model.setPosition(actor.getX(), actor.getY());
				}
				
				if(updateScale){
					model.setScale(actor.getScaleX(), actor.getScaleY());
				}
				
				if(updateRotation){
					model.setRotation(actor.getRotation());
				}
			}
		}
	}
	
	public void addCollisionModel(CollisionModel model){
		collisionModels.add(model);
	}
	
	public void removeCollisionModel(CollisionModel model){
		collisionModels.remove(model);
	}
	
	public List<CollisionModel> getCollisionModels() {
		return collisionModels;
	}

	public Actor getActor() {
		return actor;
	}

	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		
		CollideableActor ca = new CollideableActor(self, actor, CollisionManager.getCollisionManager(actionContext), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), ca);
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		return ca;
	}
	
	public static void createCollisionModels(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		CollideableActor object = (CollideableActor) actionContext.get("object");
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof CollisionModel){
				object.collisionModels.add((CollisionModel) obj);
			}
		}
	}
	
	public static CollisionModel createCollisionModelPool(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		CollisionModelPool modelPool = UtilData.getObjectByType(self, "modelPool", CollisionModelPool.class, actionContext);
		if(modelPool != null){
			return modelPool.obtain();
		}else{
			return null;
		}
	}

	@Override
	public boolean checkCollide(Collideable object) {
		if(object == this){
			return false;
		}
		
		if(object instanceof CollideableActor){
			CollideableActor obj = (CollideableActor) object;
			for(CollisionModel myModel : collisionModels){
				for(CollisionModel objModel : obj.collisionModels){
					if(myModel.overlaps(objModel)){
						return true;
					}
				}
			}
			
			return false;
		}else{
			return false;
		}
	}
}
