package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;

public class TextureAtlasParameterActions {
	public static TextureAtlasParameter create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TextureAtlasParameter item = null;
		if(self.getStringBlankAsNull("flip") != null){
			item = new TextureAtlasParameter(self.getBoolean("flip"));
		}else{
			item = new TextureAtlasParameter();
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
