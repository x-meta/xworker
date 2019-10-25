package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionFunctions {
	public static Object createTextureRegion(ActionContext actionContext){
		return new TextureRegion();
	}
	
	public static Object createTextureRegion_texture(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		return new TextureRegion(texture);
	}
	
	public static Object createTextureRegion_texture_u_v_u2_v2(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Number u = (Number) actionContext.get("u");
		Number v = (Number) actionContext.get("v");
		Number u2 = (Number) actionContext.get("u2");
		Number v2 = (Number) actionContext.get("v2");
		return new TextureRegion(texture, u.floatValue(), v.floatValue(), u2.floatValue(), v2.floatValue());
	}
	
	public static Object createTextureRegion_texture_width_height(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		return new TextureRegion(texture, width.intValue(), height.intValue());
	}
	
	public static Object createTexttureRegion_texture_x_y_width_height(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		return new TextureRegion(texture, x.intValue(), y.intValue(), width.intValue(), height.intValue());
	}
	
	public static Object createTextureRegion_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		return new TextureRegion(region);
	}
	
	public static Object createTextureRegion_region_x_y_width_height(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		Number x = (Number) actionContext.get("x");
		Number y = (Number) actionContext.get("y");
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		return new TextureRegion(region, x.intValue(), y.intValue(), width.intValue(), height.intValue());
	}
}
