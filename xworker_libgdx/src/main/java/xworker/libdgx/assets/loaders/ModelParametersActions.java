package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.ModelLoader.ModelParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;

import ognl.OgnlException;

public class ModelParametersActions {
	public static ModelParameters create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ModelParameters item = new ModelParameters();
		
		if(self.getStringBlankAsNull("textureParameter")  != null){
			TextureParameter textureParameter = UtilData.getObjectByType(self, "textureParameter", TextureParameter.class, actionContext);
			item.textureParameter = textureParameter;
		}		
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
