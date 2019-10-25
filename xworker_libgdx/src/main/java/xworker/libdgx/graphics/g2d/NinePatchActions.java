package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NinePatchActions {
	public static NinePatch create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getString("constructor");
		NinePatch n = null;
		if("ninePatch".equals(constructor)){
			NinePatch ninePatch = (NinePatch) actionContext.get("ninePatch");
			n = new NinePatch(ninePatch);
		}else if("ninePatch_color".equals(constructor)){
			NinePatch ninePatch = (NinePatch) actionContext.get("ninePatch");
			Color color = (Color) actionContext.get("color");
			n = new NinePatch(ninePatch, color);
		}else if("texture".equals(constructor)){
			Texture texture = (Texture) actionContext.get("texture");
			n = new NinePatch(texture);
		}else if("texture_color".equals(constructor)){
			Texture texture = (Texture) actionContext.get("texture");
			Color color = (Color) actionContext.get("color");
			n = new NinePatch(texture, color);
		}else if("texture_left_right_top_bottom".equals(constructor)){
			Texture texture = (Texture) actionContext.get("texture");
			n = new NinePatch(texture, self.getInt("left"), self.getInt("right"), self.getInt("top"), self.getInt("bottom"));
		}else if("patches".equals(constructor)){
			TextureRegion[] patches = (TextureRegion[]) actionContext.get("patches");
			n = new NinePatch(patches);
		}else if("region".equals(constructor)){
			TextureRegion region = (TextureRegion) actionContext.get("region");
			n = new NinePatch(region);
		}else if("region_color".equals(constructor)){
			TextureRegion region = (TextureRegion) actionContext.get("region");
			Color color = (Color) actionContext.get("color");
			n = new NinePatch(region, color);
		}else if("region_left_right_top_bottom".equals(constructor)){
			TextureRegion region = (TextureRegion) actionContext.get("region");
			n = new NinePatch(region, self.getInt("left"), self.getInt("right"), self.getInt("top"), self.getInt("bottom"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), n);
		return n;
	}
}
