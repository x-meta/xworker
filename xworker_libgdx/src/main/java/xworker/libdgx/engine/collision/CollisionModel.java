package xworker.libdgx.engine.collision;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ognl.OgnlException;
import xworker.libdgx.utils.PoolManager;

public class CollisionModel {
	/** 名称 */
	private String name;
	
	private Thing thing;
	
	/** 碰撞模型所包含的碰撞区域 */	
	private List<Shape2D> shapes = new ArrayList<Shape2D>();
	
	public CollisionModel(Thing thing, ActionContext actionContext){
		 this.thing = thing;
		 this.name = thing.getMetadata().getName();
		 
		 for(Thing child : thing.getChilds()){
			 Object obj = child.doAction("create", actionContext);
			 if(obj instanceof Shape2D){
				 shapes.add((Shape2D) obj);
			 }
		 }
	}
		
	public static Polygon createActor(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		if(actor != null){
			Polygon p = new Polygon();
			float[] vs = new float[]{0, 0, actor.getWidth(), 0, actor.getWidth(), actor.getHeight(),
					0, actor.getHeight()};
			p.setVertices(vs);
			return p;
		}else{
			return null;
		}
	}
	
	public static Polygon createActor(Actor actor){
		Polygon p = new Polygon();
		float[] vs = new float[]{0, 0, actor.getWidth(), 0, actor.getWidth(), actor.getHeight(),
				0, actor.getHeight()};
		p.setVertices(vs);
		return p;
	}
	
	public static CollisionModel create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return new CollisionModel(self, actionContext);
	}
	
	public static Polygon createPolygon(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Polygon p = new Polygon();
		p.setPosition(self.getFloat("x"), self.getFloat("y"));
		String vertices = self.getStringBlankAsNull("vertices");
		if(vertices != null){
			String[] vs = vertices.split("[,]");
			float[] vf = new float[vs.length];
			for(int i =0; i<vs.length; i++){
				vf[i] = Float.parseFloat(vs[i]);
			}
			p.setVertices(vf);			
		}
		
		return p;
	}
	
	public static Circle createCircle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Circle c = new Circle();
		c.x = self.getFloat("x");
		c.y = self.getFloat("y");
		c.radius = self.getFloat("radius");
		
		return c;
	}
	
	public static Rectangle createRectangle(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Rectangle r = new Rectangle();
		r.x = self.getFloat("x");
		r.y = self.getFloat("y");
		r.width = self.getFloat("width");
		r.height = self.getFloat("height");
		
		return r;
	}
	
	public String getName(){
		return name;
	}
	
	public Thing getThing(){
		return thing;
	}
	
	public boolean overlaps(CollisionModel model){
		for(Shape2D ms : shapes){
			for(Shape2D os : model.shapes){
				if(ms instanceof Polygon){
					if(os instanceof Polygon){
						return Intersector.overlapConvexPolygons((Polygon) ms, (Polygon) os);
					}else if(os instanceof Rectangle){
						return overlaps((Polygon) ms, (Rectangle) os);
					}else if(os instanceof Circle){
						return overlaps((Polygon) ms, (Circle) os);
					}
					
					return false;
				}else if(ms instanceof Rectangle){
					if(os instanceof Polygon){
						return overlaps((Polygon) os, (Rectangle) ms);
					}else if(os instanceof Rectangle){
						return ((Rectangle) os).overlaps((Rectangle) os);
					}else if(os instanceof Circle){
						return overlaps((Rectangle) ms, (Circle) os);
					}
					
					return false;
				}else if(ms instanceof Circle){
					if(os instanceof Polygon){
						return overlaps((Polygon) os, (Circle) ms);
					}else if(os instanceof Rectangle){
						return overlaps((Rectangle) os, (Circle) ms);
					}else if(os instanceof Circle){
						return ((Circle) ms).overlaps((Circle) os);
					}
					
					return false;
				}
			}
		}
		
		return false;
	}
	
	public boolean overlaps(Rectangle r , Circle c){
		Vector2 center = PoolManager.obtainVector2(c.x, c.y);
		float radius = c.radius;
		
		Vector2 start = PoolManager.obtainVector2(r.x, r.y);
		Vector2 end = PoolManager.obtainVector2(r.x + r.width , r.y);
		try{
			if(Intersector.intersectSegmentCircle(start, end, center, radius)){
				return true;
			}
			
			start = PoolManager.obtainVector2(r.x + r.width , r.y);
			end = PoolManager.obtainVector2(r.x + r.width , r.y + r.height);
			if(Intersector.intersectSegmentCircle(start, end, center, radius)){
				return true;
			}
			
			start = PoolManager.obtainVector2(r.x + r.width , r.y + r.height);
			end = PoolManager.obtainVector2(r.x , r.y + r.height);
			if(Intersector.intersectSegmentCircle(start, end, center, radius)){
				return true;
			}
			
			start = PoolManager.obtainVector2(r.x , r.y + r.height);
			end = PoolManager.obtainVector2(r.x , r.y);
			if(Intersector.intersectSegmentCircle(start, end, center, radius)){
				return true;
			}
			
			return false;
		}finally{
			PoolManager.free(start, end);
		}
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
	
	public boolean overlaps(Polygon p, Rectangle r){
		Polygon rp = new Polygon(new float[]{r.getX(), r.getY(), r.getX() + r.getWidth(), r.getY(),
				r.getX() + r.getWidth(), r.getY() + r.getHeight(), r.getX(), r.getY() + r.getHeight()});
		return Intersector.overlapConvexPolygons(p, rp);
	}
	
	public void setPosition(float x, float y){
		for(Shape2D shape : shapes){
			if(shape instanceof Rectangle){
				((Rectangle) shape).setPosition(x, y);
			}else if(shape instanceof Circle){
				((Circle) shape).setPosition(x, y);
			}else if(shape instanceof Ellipse){
				((Ellipse) shape).setPosition(x, y);
			}else if(shape instanceof Polygon){
				((Polygon) shape).setPosition(x, y);
			}else if(shape instanceof  Polyline){
				((Polyline) shape).setPosition(x, y);
			}
		}
	}
	
	public void setRotation(float degrees) {
		for(Shape2D shape : shapes){
			if(shape instanceof Polygon){
				((Polygon) shape).setRotation(degrees);
			}else if(shape instanceof  Polyline){
				((Polyline) shape).setRotation(degrees);
			}
		}
	}
	
	public void setScale(float scaleX, float scaleY){
		for(Shape2D shape : shapes){
			if(shape instanceof Polygon){
				((Polygon) shape).setScale(scaleX, scaleY);
			}else if(shape instanceof  Polyline){
				((Polyline) shape).setScale(scaleX, scaleY);
			}
		}
	}
}
