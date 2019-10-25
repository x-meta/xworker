package xworker.libdgx.functions.scenes.scene2d.utils;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextureRegionDrawableFunctions {
	public static Object createTextureRegionDrawable(ActionContext actionContext){
		return new TextureRegionDrawable();
	}
	
	public static Object createTextureRegionDrawable_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		
		return new TextureRegionDrawable(region);
	}
	
	public static Object createTextureRegionDrawable_drawable(ActionContext actionContext){
		TextureRegionDrawable drawable = (TextureRegionDrawable) actionContext.get("drawable");
		
		return new TextureRegionDrawable(drawable);
	}
}
