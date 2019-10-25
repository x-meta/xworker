package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteFunctions {
	public static Object createSprite(ActionContext actionContext){
		return new Sprite();
	}
	
	public static Object createSprite_sprite(ActionContext actionContext){
		Sprite sprite = (Sprite) actionContext.get("sprite");
		return new Sprite(sprite);
	}
	
	public static Object createSprite_texture(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		return new Sprite(texture);
	}
	
	public static Object createSprite_texture_srcWidth_srcHeight(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");		
		Number srcWidth = (Number) actionContext.get("srcWidth");
		Number srcHeight = (Number) actionContext.get("srcHeight");
		
		return new Sprite(texture, srcWidth.intValue(), srcHeight.intValue());
	}
	
	public static Object createSprite_texture_srcX_srcY_srcWidth_srcHeight(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		Number srcX = (Number) actionContext.get("srcX");
		Number srcY = (Number) actionContext.get("srcY");
		Number srcWidth = (Number) actionContext.get("srcWidth");
		Number srcHeight = (Number) actionContext.get("srcHeight");
		
		return new Sprite(texture, srcX.intValue(), srcY.intValue(), srcWidth.intValue(), srcHeight.intValue());
	}
	
	public static Object createSprite_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		return new Sprite(region);
	}
}
