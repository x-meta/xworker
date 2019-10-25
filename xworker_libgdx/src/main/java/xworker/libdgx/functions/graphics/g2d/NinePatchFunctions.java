package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NinePatchFunctions {
	public static Object createNinePatch_ninePatch(ActionContext actionContext){
		NinePatch ninePatch = (NinePatch) actionContext.get("ninePatch");
		
		return new NinePatch(ninePatch);
	}
	
	public static Object createNinePatch_ninePatch_color(ActionContext actionContext){
		NinePatch ninePatch = (NinePatch) actionContext.get("ninePatch");
		Color color = (Color) actionContext.get("color");
		
		return new NinePatch(ninePatch, color);
	}
	
	public static Object createNinePatch_texture(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		
		return new NinePatch(texture);
	}
	
	public static Object createNinePatch_texture_color(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Color color = (Color) actionContext.get("color");
		
		return new NinePatch(texture, color);
	}
	
	public static Object createNinePatch_texture_left_right_top_bottom(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Number left = (Number) actionContext.get("left");
		Number right = (Number) actionContext.get("right");
		Number top = (Number) actionContext.get("top");
		Number bottom = (Number) actionContext.get("bottom");		
		
		return new NinePatch(texture, left.intValue(), right.intValue(), top.intValue(), bottom.intValue());
	}
	
	public static Object createNinePatch_patches(ActionContext actionContext){
		TextureRegion[] patches = (TextureRegion[]) actionContext.get("patches");
		
		return new NinePatch(patches);
	}
	
	public static Object createNinePatch_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		
		return new NinePatch(region);
	}
	
	public static Object createNinePatch_region_color(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		Color color = (Color) actionContext.get("color");
		
		return new NinePatch(region, color);
	}
	
	public static Object createNinePatch_left_right_top_bottom(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		Number left = (Number) actionContext.get("left");
		Number right = (Number) actionContext.get("right");
		Number top = (Number) actionContext.get("top");
		Number bottom = (Number) actionContext.get("bottom");		
		
		return new NinePatch(region, left.intValue(), right.intValue(), top.intValue(), bottom.intValue());
	}
}
