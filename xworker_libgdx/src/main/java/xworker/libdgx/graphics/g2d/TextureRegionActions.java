package xworker.libdgx.graphics.g2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ognl.OgnlException;

public class TextureRegionActions {
	private static Logger logger = LoggerFactory.getLogger(TextureRegionActions.class);
	
	public static TextureRegion create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		
		TextureRegion region = null;
		if("texture".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			region = new TextureRegion(texture);
		}else if("texture_u_v_u2_v2".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			float u = self.getFloat("u");
			float v = self.getFloat("v");
			float u2 = self.getFloat("u2");
			float v2 = self.getFloat("v2");
			region = new TextureRegion(texture, u , v, u2, v2);
		}else if("texture_width_height".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			int width = self.getInt("width");
			int height = self.getInt("height");
			region = new TextureRegion(texture, width, height);
		}else if("texture_x_y_width_height".equals(constructor)){
			Texture texture = (Texture) actionContext.get(self.getString("texture"));
			int x = self.getInt("x");
			int y = self.getInt("y");
			int width = self.getInt("width");
			int height = self.getInt("height");
			region = new TextureRegion(texture, x, y, width, height);
		}else if("textureRegion".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("textureRegion"));
			region = new TextureRegion(textureRegion);
		}else if("textureRegion_x_y_width_height".equals(constructor)){
			TextureRegion textureRegion = (TextureRegion) actionContext.get(self.getString("textureRegion"));
			int x = self.getInt("x");
			int y = self.getInt("y");
			int width = self.getInt("width");
			int height = self.getInt("height");
			region = new TextureRegion(textureRegion, x, y , width, height);
		}else{
			region = new TextureRegion();
		}
		
		if(self.getStringBlankAsNull("flipX") != null && self.getStringBlankAsNull("flipY") != null){
			region.flip(self.getBoolean("flipX"), self.getBoolean("flipY"));
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), region);
		return region;
	}
	
	public static TextureRegion[][] split(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Texture texture = UtilData.getObjectByType(self, "texture", Texture.class, actionContext);
		int tileWidth = self.getInt("tileWidth", 0, actionContext);
		int tileHeight = self.getInt("tileHeight", 0, actionContext);
		if(texture == null){
			logger.warn("TextureRegion.split: texture is null, path=" + self.getMetadata().getPath());
			
			return null;
		}
		
		TextureRegion[][] regions = TextureRegion.split(texture, tileWidth, tileHeight);
		actionContext.getScope(0).put(self.getMetadata().getName(), regions);
		
		return regions;
	}
}