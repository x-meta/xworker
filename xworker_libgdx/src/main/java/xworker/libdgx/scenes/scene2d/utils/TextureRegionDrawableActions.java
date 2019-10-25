package xworker.libdgx.scenes.scene2d.utils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextureRegionDrawableActions {
	public static TextureRegionDrawable create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		if("textureRegion".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("textureRegion"));
			return new TextureRegionDrawable(textureRegion);
		}else if("textureRegionDrawable".equals(constructor)){
			TextureRegionDrawable textureRegionDrawable = (TextureRegionDrawable) actionContext.get(self.getString("textureRegionDrawable"));
			return new TextureRegionDrawable(textureRegionDrawable);
		}else{
			return new TextureRegionDrawable();
		}
	}
}
