package xworker.libdgx.engine.collision;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public abstract class Collideable {
	/** 角色的类型 */
	String type;
	/** 不要检测碰撞的类型 */
	List<String> excludeCollideTypes = new ArrayList<String>();
	/** 需要检测的碰撞类型 */
	List<String> includeCollideTypes = new ArrayList<String>();	
	/** 碰撞管理器 */
	CollisionManager collisionManager;
	/** 游戏的变量上下文 */
	ActionContext actionContext;
	/** 处理碰撞事件的事物 */
	private Thing thing;
	/** 是否激发没有碰撞的事件 */
	boolean unoverlaps = false;
	/** 是否主动碰撞其他角色，如果否，那么不会主动检测碰撞 */
	boolean active = false;
	
	public Collideable(Thing thing, CollisionManager collisionManager, ActionContext actionContext){
		this.thing = thing;
		this.type = thing.getString("type");
		this.collisionManager = collisionManager;
		this.actionContext = actionContext;
	}
	
	/**
	 * 是否接受指定类型对象的碰撞。
	 * 
	 * @param type
	 * @return
	 */
	public boolean acceptType(String type){
		for(String ex : excludeCollideTypes){
			if(ex.equals(type)){
				return false;
			}
		}
		
		for(String in : includeCollideTypes){
			if(in.equals(type)){
				return true;
			}
		}
		
		if(includeCollideTypes.size() > 0){
			return false;
		}else{
			return true;
		}
	}
	
	public String getType(){
		return type;
	}
	
	/**
	 * 检测和对象物体是否碰撞。
	 * 
	 * @param obj
	 */
	protected void check(Collideable obj){
		if(acceptType(obj.getType())){
			if(checkCollide(obj)){
				if(unoverlaps){
					thing.doAction("overlaps", actionContext, UtilMap.toMap("target", obj, "source", this));
				}
			}else if(unoverlaps){
				thing.doAction("unoverlaps", actionContext, UtilMap.toMap("target", obj, "source", this));
			}
		}
	}
	
	protected abstract boolean checkCollide(Collideable object);
	
	public void update(){}

	public boolean isUnoverlaps() {
		return unoverlaps;
	}

	public boolean isActive() {
		return active;
	};
	
	
}
