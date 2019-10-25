package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

import ognl.OgnlException;
import xworker.libdgx.graphics.TextureActions;

public class BitmapFontParameterActions {
	public static BitmapFontParameter create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		BitmapFontParameter item = new BitmapFontParameter();
		
		if(self.getStringBlankAsNull("atlasName") != null){
			item.atlasName = self.getString("atlasName");
		}
		
		if(self.getStringBlankAsNull("bitmapFontData") != null){
			BitmapFontData bitmapFontData = UtilData.getObjectByType(self, "bitmapFontData", BitmapFontData.class, actionContext);
			item.bitmapFontData = bitmapFontData;
		}
		
		if(self.getStringBlankAsNull("flip") != null){
			item.flip = self.getBoolean("flip");
		}
		
		if(self.getStringBlankAsNull("genMipMaps") != null){
			item.genMipMaps = self.getBoolean("genMipMaps");
		}
		
		if(self.getStringBlankAsNull("magFilter") != null){
			item.magFilter = TextureActions.getTextureFilter(self.getString("magFilter"));
		}
		
		if(self.getStringBlankAsNull("minFilter") != null){
			item.minFilter = TextureActions.getTextureFilter(self.getString("minFilter"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
