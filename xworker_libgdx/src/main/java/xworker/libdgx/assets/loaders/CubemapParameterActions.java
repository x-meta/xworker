package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.CubemapLoader.CubemapParameter;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;

import ognl.OgnlException;
import xworker.libdgx.graphics.PixmapActions;
import xworker.libdgx.graphics.TextureActions;

public class CubemapParameterActions {
	public static CubemapParameter createCubemapParameter(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		CubemapParameter item = new CubemapParameter();
		
		if(self.getStringBlankAsNull("cubemap")  != null){
			Cubemap cubemap = UtilData.getObjectByType(self, "cubemap", Cubemap.class, actionContext);
			item.cubemap = cubemap;
		}
		
		if(self.getStringBlankAsNull("cubemapData")  != null){
			CubemapData cubemapData = UtilData.getObjectByType(self, "cubemapData", CubemapData.class, actionContext);
			item.cubemapData = cubemapData;
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
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
