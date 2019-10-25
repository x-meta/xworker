package xworker.libdgx.functions.scenes.scene2d.utils;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledDrawableFunctions {
	public static Object createTiledDrawable(ActionContext actionContext){
		return new TiledDrawable();
	}
	
	public static Object createTiledDrawable_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		
		return new TiledDrawable(region);
	}
	
	public static Object createTiledDrawable_drawable(ActionContext actionContext){
		TiledDrawable drawable = (TiledDrawable) actionContext.get("drawable");
		
		return new TiledDrawable(drawable);
	}
}
