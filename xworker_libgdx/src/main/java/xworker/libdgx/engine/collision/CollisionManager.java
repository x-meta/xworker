package xworker.libdgx.engine.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmeta.ActionContext;
import org.xmeta.Thing;


public class CollisionManager {
	/** 根据类型缓存的可碰撞的对象 */
	Map<String, List<CollideableActor>> objects = new java.util.concurrent.ConcurrentHashMap<String, List<CollideableActor>>();
	
	/** 碰撞管理器所在的变量上下文 */
	ActionContext actionContext;
	
	public static CollisionManager getCollisionManager(ActionContext actionContext){
		CollisionManager manager = (CollisionManager) actionContext.getScope(0).get("collisionManager");
		if(manager == null){
			manager = new CollisionManager();
			actionContext.getScope(0).put("collisionManager", manager);
		}
		
		return manager;
	}
	
	public void checkCollide(float delta){
		update();
		
		
	}
	
	public List<CollideableActor> getObjects(String type){
		return objects.get(type);
	}
	
	public void update(){
		for(String key : objects.keySet()){
			List<CollideableActor> objs = objects.get(key);
			for(CollideableActor obj : objs){
				obj.update();
			}
		}
	}
	
	public void putObject(CollideableActor object){
		String type = object.getType();
		synchronized(objects){			
			List<CollideableActor> objs = objects.get(type);
			if(objs == null){
				objs = new ArrayList<CollideableActor>();
				objects.put(type, objs);
			}
			
			if(!objs.contains(object)){
				objs.add(object);
			}
		}
	}
	
	public void removeObject(CollideableActor object){
		String type = object.getType();
		synchronized(objects){			
			List<CollideableActor> objs = objects.get(type);
			if(objs != null){
				objs.remove(object);
			}
		}
	}
	
	/**
	 * 检查碰撞。
	 */
	public void checkCollision(){
		Set<String> keys  = objects.keySet();
		for(String type : keys){
			List<CollideableActor> objs = objects.get(type);
			for(CollideableActor obj : objs){
				if(obj.isActive()){
					for(String objType : keys){
						if(obj.acceptType(objType) ){
							List<CollideableActor> tobjs = objects.get(objType);
							for(CollideableActor tobj : tobjs){
								obj.check(tobj);
							}
						}
					}
				}
			}
		}
	}
	
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
}
