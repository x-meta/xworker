package xworker.libdgx.engine.world2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import ognl.OgnlException;
import xworker.libdgx.utils.PoolManager;

public class Collide {
	Thing thing;
	ActionContext actionContext;
	
	public Collide(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getSources(){
		return (List<Object>) thing.doAction("getSources", actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getTargets(){
		return (List<Object>) thing.doAction("getTargets", actionContext);
	}
	
	public void checkCollide(float delta){
		List<Object> sources = getSources();
		List<Object> targets = getTargets();
		
		List<CollideResult> overTargets = new ArrayList<CollideResult>();
		List<CollideResult> unoverTargets = new ArrayList<CollideResult>();
		
		if(sources != null && targets != null){
			for(Object source : sources){
				for(Object target : targets){
					if(target == null || source == null){
						continue;
					}
					
					if(checkCollide(source, target)){
						overTargets.add(new CollideResult(source, target, true));
					}else{
						unoverTargets.add(new CollideResult(source, target, false));
					}
				}
			}
		}
		
		thing.doAction("checked", actionContext, UtilMap.toMap("overlapList", overTargets, "unoverlapList", unoverTargets, "delta", delta));
	}
	
	public boolean checkCollide(Object source, Object target){
		if(source instanceof Actor){
			Actor sactor = (Actor) source;
			
			if(target instanceof Actor){
				Actor tactor = (Actor) target;
				
				return check(sactor, tactor);
			}else if(target instanceof Shape2D){
				return check(sactor, (Shape2D) target);
			}
		}else if(target instanceof Shape2D){
			Shape2D shape = (Shape2D) source;
			
			if(target instanceof Actor){
				Actor tactor = (Actor) target;
				
				return check(tactor, shape);
			}else if(target instanceof Shape2D){
				return check(shape, (Shape2D) target);
			}
		}
		
		throw new ActionException("Unimplement ovlate source=" + source + ", target=" +target);
	}
	
	public boolean check(Actor source, Actor target){
		return Intersector.overlaps(new Rectangle(source.getX(), source.getY(), source.getWidth(), source.getHeight()),
				new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight()));
	}
	
	public boolean check(Actor source, Shape2D target){		
		if(target instanceof Circle){
			Rectangle rec = new Rectangle(source.getX(), source.getY(), source.getWidth(), source.getHeight());
			return Intersector.overlaps((Circle) target, rec);
		}else if(target instanceof Rectangle){
			Rectangle rec = new Rectangle(source.getX(), source.getY(), source.getWidth(), source.getHeight());
			return Intersector.overlaps(rec, (Rectangle) target);
		}else if(target instanceof Polygon){
			Polygon p = (Polygon) target;
			Actor r = source;
			Polygon rp = new Polygon(new float[]{r.getX(), r.getY(), r.getX() + r.getWidth(), r.getY(),
					r.getX() + r.getWidth(), r.getY() + r.getHeight(), r.getX(), r.getY() + r.getHeight()});
			return Intersector.overlapConvexPolygons(p, rp);
		}else{
			throw new ActionException("Unimplement ovlate source=" + source + ", target=" +target);
		}
	}
	
	public boolean check(Shape2D source, Shape2D target){
		if(source instanceof Circle){
			if(target instanceof Circle){
				return Intersector.overlaps((Circle) source, (Circle) target);
			}else if(target instanceof Rectangle){
				return Intersector.overlaps((Circle) source, (Rectangle) target);
			}else if(target instanceof Polygon){
				return this.overlaps((Polygon) target, (Circle) source);
			}
		}else if(source instanceof Rectangle){
			if(target instanceof Circle){
				return Intersector.overlaps((Circle) target, (Rectangle) source);
			}else if(target instanceof Rectangle){
				return Intersector.overlaps((Rectangle) source, (Rectangle) target);
			}else if(target instanceof Polygon){
				return this.overlaps((Polygon) target, (Rectangle) source);
			}
		}else if(source instanceof Polygon){
			if(target instanceof Circle){
				return this.overlaps((Polygon) source, (Circle) target);
			}else if(target instanceof Rectangle){
				return this.overlaps((Polygon) source, (Rectangle) target);
			}else if(target instanceof Polygon){
				return Intersector.overlapConvexPolygons((Polygon) source, (Polygon) target);
			}			
		}
		
		throw new ActionException("Unimplement ovlate source=" + source + ", target=" +target);
	}
	
	public boolean overlaps(Polygon p, Rectangle r){
		Polygon rp = new Polygon(new float[]{r.getX(), r.getY(), r.getX() + r.getWidth(), r.getY(),
				r.getX() + r.getWidth(), r.getY() + r.getHeight(), r.getX(), r.getY() + r.getHeight()});
		return Intersector.overlapConvexPolygons(p, rp);
	}
	
	public boolean overlaps(Polygon p, Circle c){
		float[] vs = p.getTransformedVertices();
		Vector2 center = PoolManager.obtainVector2(c.x, c.y);
		float radius = c.radius;
		try{
			for(int i=0; i<vs.length; i++){
				Vector2 start = null;
				Vector2 end = null;
				
				if(i < vs.length - 3){
					start = PoolManager.obtainVector2(vs[i], vs[i + 1]);
					end = PoolManager.obtainVector2(vs[i + 2], vs[i + 3]);					
				}else if(i== vs.length - 3){
					start = PoolManager.obtainVector2(vs[i], vs[i + 1]);
					end = PoolManager.obtainVector2(vs[i + 1], vs[0]);
				}else if(i == vs.length - 2){
					start = PoolManager.obtainVector2(vs[i], vs[i + 1]);
					end = PoolManager.obtainVector2(vs[0], vs[1]);
				}else if(i == vs.length - 1){
					start = PoolManager.obtainVector2(vs[i], vs[0]);
					end = PoolManager.obtainVector2(vs[1], vs[2]);
				}
				
				if(start != null && end != null){
					if(Intersector.intersectSegmentCircle(start, end, center, radius)){
						return true;
					}
				}
				
				PoolManager.free(start, end);
			}
			
			return false;
		}finally{
			PoolManager.free(center);
		}
	}
	
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Collide collide = new Collide(self, actionContext);
		GameWorld gameWorld = (GameWorld) actionContext.get("gameWorld");
		gameWorld.addCollide(collide);
		actionContext.getScope(0).put(self.getMetadata().getName(), collide);
		
		return collide;		
	}
	
	
	public static List<Object> getSources(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return getObjects(self.getThing("Sources@0"), actionContext);
	}
	
	public static List<Object> getTargets(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return getObjects(self.getThing("Targets@0"), actionContext);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Object> getObjects(Thing thing, ActionContext actionContext){
		if(thing ==  null){
			return Collections.emptyList();
		}
		
		List<Object> objs = new ArrayList<Object>();
		
		for(Thing child : thing.getChilds()){
			Object obj = child.doAction("create", actionContext);
			if(obj != null){
				if(obj instanceof List){
					objs.addAll((List) obj);
				}else{
					objs.add(obj);
				}
			}
		}
		
		return objs;
	}
	
	public static Actor getActor(ActionContext actionContext) throws OgnlException{		
		Thing self = (Thing) actionContext.get("self");
		
		return UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
	}
	
	public static List<Actor> getActorsInGroup(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Group group = UtilData.getObjectByType(self, "group", Group.class, actionContext);
		List<Actor> actors = new ArrayList<Actor>();
		Actor[] as = group.getChildren().begin();
		for(Actor a : as){
			actors.add(a);
		}
		return actors;
	}
}
