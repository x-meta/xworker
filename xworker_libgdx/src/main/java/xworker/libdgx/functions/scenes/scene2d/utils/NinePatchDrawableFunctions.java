package xworker.libdgx.functions.scenes.scene2d.utils;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinePatchDrawableFunctions {
	public static Object createNinePatchDrawable(ActionContext actionContext){
		return new NinePatchDrawable();
	}
	
	public static Object createNinePatchDrawable_patch(ActionContext actionContext){
		NinePatch patch = (NinePatch) actionContext.get("patch");
		return new NinePatchDrawable(patch);
	}
	
	public static Object createNinePatchDrawable_drawable(ActionContext actionContext){
		NinePatchDrawable drawable = (NinePatchDrawable) actionContext.get("drawable");
		return new NinePatchDrawable(drawable);
	}
}
