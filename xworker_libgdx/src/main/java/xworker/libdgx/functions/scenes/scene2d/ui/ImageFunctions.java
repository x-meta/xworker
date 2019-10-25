package xworker.libdgx.functions.scenes.scene2d.ui;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class ImageFunctions {
	public static Object createImage(ActionContext actionContext){
		return new Image();
	}
	
	public static Object createImage_drawable(ActionContext actionContext){
		Drawable drawable = (Drawable) actionContext.get("drawable");
		return new Image(drawable);
	}
	
	public static Object createImage_drawable_scaling(ActionContext actionContext){
		Drawable drawable = (Drawable) actionContext.get("drawable");
		Scaling scaling = (Scaling) actionContext.get("scaling");
		
		return new Image(drawable, scaling);
	}
	
	public static Object createImage_drawable_scaling_align(ActionContext actionContext){
		Drawable drawable = (Drawable) actionContext.get("drawable");
		Scaling scaling = (Scaling) actionContext.get("scaling");
		Number align = (Number) actionContext.get("align");
		
		return new Image(drawable, scaling, align.intValue());
	}
	
	public static Object createImage_patch(ActionContext actionContext){
		NinePatch patch = (NinePatch) actionContext.get("patch");
		return new Image(patch);
	}
	
	public static Object createImage_skin_drawableName(ActionContext actionContext){
		Skin skin = (Skin) actionContext.get("skin");
		String drawableName = (String) actionContext.get("drawableName");
		return new Image(skin, drawableName);
	}
	
	public static Object createImage_texture(ActionContext actionContext){
		Texture texture = (Texture) actionContext.get("texture");
		return new Image(texture);
	}
	
	public static Object createImage_region(ActionContext actionContext){
		TextureRegion region = (TextureRegion) actionContext.get("region");
		return new Image(region);
	}
}

