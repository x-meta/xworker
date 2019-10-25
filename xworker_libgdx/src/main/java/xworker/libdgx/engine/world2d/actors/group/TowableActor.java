package xworker.libdgx.engine.world2d.actors.group;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import xworker.util.Point;
import xworker.util.UtilMath;

public class TowableActor {
	public static final byte LEFT = 1;
	public static final byte RIGHT = 0;
	public static final byte TOP = 2;
	public static final byte BOTTOM = 3;
	
	Vector2 start;
	Vector2 end;
	Actor actor;
	byte direction;
	/* start和end之间的距离 */
	double distance;
	
	public TowableActor(Actor actor){
		this(actor,  RIGHT);
	}
		
	public TowableActor(Actor actor, byte direction){
		this.actor = actor;
		
		float x = actor.getX();
		float y = actor.getY();
		float width = actor.getWidth();
		float height = actor.getHeight();
		
		switch(direction){
		case LEFT:
			start = new Vector2(x, y + height / 2);
			end = new Vector2(x + width, y + height / 2);
			break;
		case TOP:
			start = new Vector2(x + width /2 , y + height);
			end = new Vector2(x + width / 2, y);
			break;
		case BOTTOM:
			start = new Vector2(x + width /2 , y);
			end = new Vector2(x + width /2 , y + height);
			break;
			default:
				start = new Vector2(x + width, y + height / 2);
				end = new Vector2(x, y + height / 2);
		}
		
		distance = UtilMath.getDistance(start.x, start.y, end.x, end.y);
		
	}
	
	public TowableActor(Actor actor, Vector2 start, Vector2 end,  byte direction){
		this.actor = actor;
		this.start = start;
		this.end = end;
		this.direction = direction;
	}
	
	public TowableActor(Actor actor, Vector2 start, float rotation,  byte direction){
		
	}
	
	/**
	 * 重新设置起点。
	 * 
	 * @param start
	 */
	public void setStart(Vector2 start){
		this.start = start;
		
		Point newEnd = UtilMath.getPointByDistance(start.x, start.y, end.x, end.y, distance);
		end.set((float) newEnd.getX(), (float) newEnd.getY());
		
		float degree = (float) Math.atan2(start.y - end.y, start.x - end.x);
		//System.out.println(degree);
		actor.setRotation((float) (degree * 180 / Math.PI));
		Point p = UtilMath.getPointByDegree(end.x, end.y, degree - 2 * Math.PI , actor.getHeight() / 2);
		actor.setPosition((float) p.getX(), (float) p.getY());
	}
	
	public static void main(String args[]){
		try{
			System.out.println(UtilMath.getPointByDistance(0, 0, 0, -20, 4));
		}catch(Exception e){
			e.printStackTrace();
		
		}
	}
}
