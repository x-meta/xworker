package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.files.FileHandle;

import ognl.OgnlException;

public class ParticleEffectParameterActions {
	public static ParticleEffectParameter create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ParticleEffectParameter item = new ParticleEffectParameter();
		
		if(self.getStringBlankAsNull("imagesDir")  != null){
			FileHandle imagesDir = UtilData.getObjectByType(self, "imagesDir", FileHandle.class, actionContext);
			item.imagesDir = imagesDir;
		}
		
		if(self.getStringBlankAsNull("atlasFile") != null){
			item.atlasFile = self.getString("atlasFile");
		}
		
		if(self.getStringBlankAsNull("atlasPrefix") != null){
			item.atlasPrefix = self.getString("atlasPrefix");
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
