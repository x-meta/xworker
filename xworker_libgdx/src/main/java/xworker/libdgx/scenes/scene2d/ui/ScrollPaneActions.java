package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class ScrollPaneActions {
	public static ScrollPane create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
	
		String constructor = self.getString("constructor");
		ScrollPane item = null;
		if("actor".equals(constructor)){
			Actor actor = getActor(self, actionContext);
			item = new ScrollPane(actor);
		}else if("actor_skin".equals(constructor)){
			Actor actor = getActor(self, actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ScrollPane(actor, skin);
		}else if("actor_skin_styleName".equals(constructor)){
			Actor actor = getActor(self, actionContext);
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ScrollPane(actor, skin, self.getString("styleName"));
		}else if("actor_style".equals(constructor)){
			Actor actor = getActor(self, actionContext);
			ScrollPaneStyle style = UtilData.getObjectByType(self, "style", ScrollPaneStyle.class, actionContext);
			item = new ScrollPane(actor, style);
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);		
				
		return item;
	}
	
	public static Actor getActor(Thing self, ActionContext actionContext) throws OgnlException{
		Actor actor = UtilData.getObjectByType(self, "actor", Actor.class, actionContext);
		if(actor == null){
			Thing child = self.getThing("ChildActor@0");
			if(child != null && child.getChilds().size() > 0){
				return (Actor) child.getChilds().get(0).doAction("create", actionContext, UtilMap.toMap("parent", null));
			}else{
				return null;
			}
		}else{
			return actor;
		}
	}

	public static void init(Thing self, ScrollPane item, ActionContext actionContext) throws OgnlException{
		WidgetGroupActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("cancelTouchFocus") != null){
			item.setCancelTouchFocus(self.getBoolean("cancelTouchFocus", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("clamp") != null){
			item.setClamp(self.getBoolean("clamp", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("fadeScrollBars") != null){
			item.setFadeScrollBars(self.getBoolean("fadeScrollBars", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("flickScroll") != null){
			item.setFlickScroll(self.getBoolean("flickScroll", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("flingTime") != null){
			item.setFlingTime(self.getFloat("flingTime", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("forceOverscrollX") != null || self.getStringBlankAsNull("forceOverscrollY") != null){
			item.setForceScroll(self.getBoolean("forceOverscrollX", false, actionContext),
					self.getBoolean("forceOverscrollY", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("overscrollX") != null || self.getStringBlankAsNull("overscrollY") != null){
			item.setOverscroll(self.getBoolean("overscrollX", false, actionContext),
					self.getBoolean("overscrollY", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollbarsOnTop") != null){
			item.setScrollbarsOnTop(self.getBoolean("scrollbarsOnTop", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollingDisabledX") != null || self.getStringBlankAsNull("scrollingDisabledY") != null){
			item.setScrollingDisabled(self.getBoolean("scrollingDisabledX", false, actionContext),
					self.getBoolean("scrollingDisabledY", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollPercentX") != null){
			item.setScrollPercentX(self.getFloat("scrollPercentX", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollPercentY") != null){
			item.setScrollPercentY(self.getFloat("scrollPercentY", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollX") != null){
			item.setScrollX(self.getFloat("scrollX", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("scrollY") != null){
			item.setScrollY(self.getFloat("scrollY", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("smoothScrolling") != null){
			item.setSmoothScrolling(self.getBoolean("smoothScrolling", false, actionContext));
		}
		
		if(self.getStringBlankAsNull("velocityX") != null){
			item.setVelocityX(self.getFloat("velocityX", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("velocityY") != null){
			item.setVelocityY(self.getFloat("velocityY", 0, actionContext));
		}
	}
}
