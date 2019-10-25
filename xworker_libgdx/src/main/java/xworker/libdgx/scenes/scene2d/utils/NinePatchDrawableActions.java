package xworker.libdgx.scenes.scene2d.utils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinePatchDrawableActions {
	public static NinePatchDrawable create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getStringBlankAsNull("constructor");
		if("ninePatch".equals(constructor)){
			NinePatch ninePatch = (NinePatch) actionContext.get(self.getString("ninePatch"));
			return new NinePatchDrawable(ninePatch);
		}else if("ninePatchDrawable".equals(constructor)){
			NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) actionContext.get(self.getString("ninePatchDrawable"));
			return new NinePatchDrawable(ninePatchDrawable);
		}else{
			return new NinePatchDrawable();
		}
	}
}
