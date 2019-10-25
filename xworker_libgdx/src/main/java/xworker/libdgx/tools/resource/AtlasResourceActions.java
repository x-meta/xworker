package xworker.libdgx.tools.resource;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ognl.OgnlException;

public class AtlasResourceActions {
	public static Object create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		TextureAtlas atlas = UtilData.getObjectByType(self, "atlas", TextureAtlas.class, actionContext);
		if(atlas == null){
			throw new ActionException("Atlas is null, thing=" + self.getMetadata().getPath());
		}
		
		String type = self.getString("type");
		String name = self.getString("regionName");
		int regionIndex = self.getInt("regionIndex", -1);
		
		Object obj = null;
		if("ninePatch".equals(type)){
			obj = atlas.createPatch(name);
		}else if("sprite".equals(type)){
			if(regionIndex >= 0){
				obj = atlas.createSprite(name, regionIndex);
			}else{
				obj = atlas.createSprite(name);
			}
		}else if("textureRegion".equals(type)){
			if(regionIndex >= 0){
				obj = atlas.findRegion(name, regionIndex);
			}else{
				obj = atlas.findRegion(name);
			}
		}else if("sprites".equals(type)){
			if(name != null && !"".equals(name)){
				obj = atlas.createSprites(name);
			}else{
				obj = atlas.createSprites();
			}
		}else if("textureRegions".equals(type)){
			if(name != null && !"".equals(name)){
				obj = atlas.findRegions(name);
			}else{
				obj = atlas.getRegions();
			}
		}else if("textures".equals(type)){
			obj = atlas.getTextures();
		}else{
			throw new ActionException("must select a type, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), obj);
		return obj;
		
	}
}
