package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureAtlasAssetsActions {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TextureAtlas atlas = (TextureAtlas) actionContext.get(self.getString("textureAtlas"));
		String type = self.getString("type");
		String name = self.getString("assetName");
		int index = self.getInt("index");
		
		Object obj = null;
		if("NinePatch".equals(type)){
			obj = atlas.createPatch(name);
		}else if("Sprite".equals(type)){
			if(self.getStringBlankAsNull("index") == null){
				obj = atlas.createSprite(name);
			}else{
				obj = atlas.createSprite(name, index);
			}			
		}else if("Sprites".equals(type)){
			if(name == null || "".equals(name)){
				obj = atlas.createSprites();
			}else{
				obj = atlas.createSprites(name);
			}
		}else if("Region".equals(type)){
			if(self.getStringBlankAsNull("index") == null){
				obj = atlas.findRegion(name);
			}else{
				obj = atlas.findRegion(name, index);
			}	
		}else if("Regions".equals(type)){
			if(name == null || "".equals(name)){
				obj = atlas.getRegions();
			}else{
				obj = atlas.findRegions(name);
			}
		}else if("Textures".equals(type)){
			obj = atlas.getTextures();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), obj);
		return obj;
	}
}
