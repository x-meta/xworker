package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteActions {
	public static Sprite create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		Sprite sp = null;
		if("sprite".equals(constructor)){
			Sprite sprite = (Sprite) actionContext.get(self.getString("sprite"));
			sp = new Sprite(sprite);
		}else if("texture".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			sp = new Sprite(texture);
		}else if("texture_srcWidth_srcHeight".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			int width = self.getInt("srcWidth");
			int height = self.getInt("srcHeigh");
			sp = new Sprite(texture, width, height);
		}else if("texture_srcX_srcY_srcWidth_srcHeight".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			int x = self.getInt("rcX");
			int y = self.getInt("srcY");
			int width = self.getInt("srcWidth");
			int height = self.getInt("srcHeight");
			sp = new Sprite(texture, x, y, width, height);
		}else if("region".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("region"));
			sp = new Sprite(textureRegion);
		}else if("region_srcX_srcY_srcWidth_srcHeight".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("region"));
			int x = self.getInt("srcX");
			int y = self.getInt("srcY");
			int width = self.getInt("srcWidth");
			int height = self.getInt("srcHeigh");
			sp = new Sprite(textureRegion, x, y , width, height);
		}else{
			sp = new Sprite();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), sp);
		return sp;
	}
}
