package xworker.libdgx.scenes.scene2d.utils;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class SpriteDrawableActions {
	public static SpriteDrawable create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		if("sprite".equals(constructor)){
			Sprite sprite = (Sprite) actionContext.get(self.getString("sprite"));
			return new SpriteDrawable(sprite);
		}else if("spriteDrawable".equals(constructor)){
			SpriteDrawable spriteDrawable = (SpriteDrawable) actionContext.get(self.getString("spriteDrawable"));
			return new SpriteDrawable(spriteDrawable);
		}else{
			return new SpriteDrawable();
		}
	}
}
