package xworker.libdgx.functions.scenes.scene2d.utils;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class SpriteDrawableFunctions {
	public static Object createSpriteDrawable(ActionContext actionContext){
		return new SpriteDrawable();
	}
	
	public static Object createSpriteDrawable_sprite(ActionContext actionContext){
		Sprite sprite = (Sprite) actionContext.get("sprite");
		return new SpriteDrawable(sprite);
	}
	
	public static Object createSpriteDrawable_drawable(ActionContext actionContext){
		SpriteDrawable drawable = (SpriteDrawable) actionContext.get("drawable");
		return new SpriteDrawable(drawable);
	}
}
