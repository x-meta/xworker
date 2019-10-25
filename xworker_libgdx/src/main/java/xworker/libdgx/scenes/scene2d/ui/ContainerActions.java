package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

import ognl.OgnlException;
import xworker.libdgx.utils.AlignActions;

public class ContainerActions {
	public static Container<Actor> create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Container<Actor> item = null;
		Actor actor = null;
		if(self.getStringBlankAsNull("actor") != null){
			actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		}
		if(actor !=  null){
			item = new Container<Actor>(actor);
		}else{
			item = new Container<Actor>();
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void childActorCreated(ActionContext actionContext){
		Container parent = (Container) actionContext.get("parent");
		Actor actor = (Actor) actionContext.get("actor");
		parent.setActor(actor);
	}
	
	public static void init(Thing self, Container<Actor> item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("align") != null){
			item.align(AlignActions.getAlign(self.getString("align", null, actionContext)));
		}
		
		if(self.getStringBlankAsNull("fillX") != null && self.getStringBlankAsNull("fillY") != null){
			item.fill(self.getBoolean("fillX", false, actionContext), self.getBoolean("fillY", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxHeight") != null){
			item.maxHeight(self.getFloat("maxHeight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("maxWidth") != null){
			item.maxWidth(self.getFloat("maxWidth", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("minHeight") != null){
			item.minHeight(self.getFloat("minHeight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("minWidth") != null){
			item.minWidth(self.getFloat("minWidth", 0, actionContext));
		}
		
		
		if(self.getStringBlankAsNull("padTop") != null){
			item.padTop(self.getFloat("padTop", 0, actionContext));
		}
		
		
		if(self.getStringBlankAsNull("padRight") != null){
			item.padRight(self.getFloat("padRight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("padBottom") != null){
			item.padBottom(self.getFloat("padBottom", 0, actionContext));
		}
		
		
		if(self.getStringBlankAsNull("padLeft") != null){
			item.padLeft(self.getFloat("padLeft", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("prefHeight") != null){
			item.prefHeight(self.getFloat("prefHeight", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("prefWidth") != null){
			item.prefWidth(self.getFloat("prefWidth", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("clip") != null){
			item.setClip(self.getBoolean("clip", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("round") != null){
			item.setRound(self.getBoolean("round", false, actionContext));
		}
	}
}
