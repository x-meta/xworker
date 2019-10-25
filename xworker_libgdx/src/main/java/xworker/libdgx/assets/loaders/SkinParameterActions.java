package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.utils.ObjectMap;

import ognl.OgnlException;

public class SkinParameterActions {
	@SuppressWarnings("unchecked")
	public static SkinParameter create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		SkinParameter item = null;
		
		String textureAtlasPath = self.getStringBlankAsNull("textureAtlasPath");
		String resources = self.getStringBlankAsNull("resources");
		
		if(resources != null && textureAtlasPath != null){
			item  = new SkinParameter(textureAtlasPath, UtilData.getObjectByType(self, "resources", ObjectMap.class, actionContext));
		}else if(resources != null){
			item  = new SkinParameter(UtilData.getObjectByType(self, "resources", ObjectMap.class, actionContext));
		}else if(textureAtlasPath != null){
			item  = new SkinParameter(textureAtlasPath);
		}else{
			item = new SkinParameter();
		}		
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
