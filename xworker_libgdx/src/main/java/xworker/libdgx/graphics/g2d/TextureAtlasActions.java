package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ognl.OgnlException;

public class TextureAtlasActions {
	public static TextureAtlas create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		
		TextureAtlas atlas = null;
		if("packFile".equals(constructor)){
			FileHandle packFile = UtilData.getObjectByType(self, "packFile", FileHandle.class, actionContext);
			atlas = new TextureAtlas(packFile);
		}else if("packFile_flip".equals(constructor)){
			FileHandle packFile = UtilData.getObjectByType(self, "packFile", FileHandle.class, actionContext);
			atlas = new TextureAtlas(packFile, self.getBoolean("flip"));
		}else if("packFile_imagesDir".equals(constructor)){
			FileHandle packFile = UtilData.getObjectByType(self, "packFile", FileHandle.class, actionContext);
			FileHandle imagesDir = UtilData.getObjectByType(self, "imagesDir", FileHandle.class, actionContext);
			atlas = new TextureAtlas(packFile, imagesDir);
		}else if("packFile_imagesDir_flip".equals(constructor)){
			FileHandle packFile = UtilData.getObjectByType(self, "packFile", FileHandle.class, actionContext);
			FileHandle imagesDir = UtilData.getObjectByType(self, "imagesDir", FileHandle.class, actionContext);
			atlas = new TextureAtlas(packFile, imagesDir, self.getBoolean("flip"));
		}else if("internalPackFile".equals(constructor)){
			atlas = new TextureAtlas(self.getString("internalPackFile"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), atlas);
		return atlas;
	}
}
