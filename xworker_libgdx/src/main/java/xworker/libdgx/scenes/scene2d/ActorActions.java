package xworker.libdgx.scenes.scene2d;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ActorActions {
	public static Actor create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Actor actor = new Actor();
		initActor(self, actor, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), actor);
		
		return actor;
	}
	
	public static void createActors(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Object parent = actionContext.get("parent");
		for(Thing child : self.getChilds()){
			Object ac = child.doAction("create", actionContext);
			if(ac instanceof Actor){
				Actor actor = (Actor) ac;
				if(parent != null){
					if(parent instanceof Table){
						((Table) parent).add(actor);
					}else if(parent instanceof Stage){
						((Stage) parent).addActor(actor);
					}else if(parent instanceof Group){
						((Group) parent).addActor(actor);
					}
				}
			}
		}
	}
	
	public static void initActor(Thing self, Actor actor, ActionContext actionContext){
		actor.setName(self.getMetadata().getName());
	
		if(self.getStringBlankAsNull("x") != null){
			actor.setX(self.getFloat("x"));
		}
		if(self.getStringBlankAsNull("y") != null){
			actor.setY(self.getFloat("y"));
		}
		if(self.getStringBlankAsNull("width") != null){
			actor.setWidth(self.getFloat("width"));
		}
		if(self.getStringBlankAsNull("height") != null){
			actor.setHeight(self.getFloat("height"));
		}
		if(self.getStringBlankAsNull("color") != null){
			actor.setColor((Color) actionContext.get(self.getStringBlankAsNull("color")));
		}
		if(self.getStringBlankAsNull("originX") != null){
			actor.setOriginX(self.getFloat("originX"));
		}
		if(self.getStringBlankAsNull("originY") != null){
			actor.setOriginY(self.getFloat("originY"));
		}
		if(self.getStringBlankAsNull("rotation") != null){
			actor.setRotation(self.getFloat("rotation"));
		}
		if(self.getStringBlankAsNull("scaleX") != null){
			actor.setScaleX(self.getFloat("scaleX"));
		}
		if(self.getStringBlankAsNull("scaleY") != null){
			actor.setScaleY(self.getFloat("scaleY"));
		}
		if(self.getStringBlankAsNull("scale") != null){
			actor.setScale(self.getFloat("scale"));
		}
		String touchable = self.getStringBlankAsNull("touchable");
		if(touchable != null){
			if("childrenOnly".equals(touchable)){
				actor.setTouchable(Touchable.childrenOnly);
			}else if("disabled".equals(touchable)){
				actor.setTouchable(Touchable.disabled);
			}else{
				actor.setTouchable(Touchable.enabled);
			}
		}
		if(self.getStringBlankAsNull("visible") != null){
			actor.setVisible(self.getBoolean("visible"));
		}
		if(self.getStringBlankAsNull("zindex") != null){
			actor.setZIndex(self.getInt("zindex"));
		}
	
		if(self.getStringBlankAsNull("positionX") != null && self.getStringBlankAsNull("positionY") != null){
			actor.setPosition(self.getFloat("positionX"), self.getFloat("positionY"));
		}
		
		Bindings bindings = actionContext.push();
		try{
			bindings.put("parent", actor);
			for(Thing child : self.getChilds()){
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof Action){
					actor.addAction((Action) obj);
				}else if(obj instanceof Actor){
					self.doAction("childActorCreated", actionContext, UtilMap.toMap("actor", obj));
				}
			}
		}finally{
			actionContext.pop();
		}
	}
}
