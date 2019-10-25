package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

import ognl.OgnlException;
import xworker.libdgx.graphics.PixmapActions;
import xworker.libdgx.graphics.TextureActions;

public class TextureParameterActions {
	public static TextureParameter create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		TextureParameter item = new TextureParameter();
		
		if(self.getStringBlankAsNull("texture")  != null){
			Texture texture = UtilData.getObjectByType(self, "texture", Texture.class, actionContext);
			item.texture = texture;
		}
		
		if(self.getStringBlankAsNull("textureData")  != null){
			TextureData textureData = UtilData.getObjectByType(self, "textureData", TextureData.class, actionContext);
			item.textureData = textureData;
		}
		
		if(self.getStringBlankAsNull("format") != null){
			item.format = PixmapActions.getFormat(self.getString("format"));
		}
		
		if(self.getStringBlankAsNull("magFilter") != null){
			item.magFilter = TextureActions.getTextureFilter(self.getString("magFilter"));
		}
		
		if(self.getStringBlankAsNull("minFilter") != null){
			item.minFilter = TextureActions.getTextureFilter(self.getString("minFilter"));
		}
		
		if(self.getStringBlankAsNull("wrapU") != null){
			item.wrapU = TextureActions.getTextureWrap(self.getString("wrapU"));
		}
		
		if(self.getStringBlankAsNull("wrapV") != null){
			item.wrapV = TextureActions.getTextureWrap(self.getString("wrapV"));
		}
		
		if(self.getStringBlankAsNull("genMipMaps") != null){
			item.genMipMaps = self.getBoolean("genMipMaps");
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
