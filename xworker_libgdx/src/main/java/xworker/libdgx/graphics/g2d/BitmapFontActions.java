package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import ognl.OgnlException;

public class BitmapFontActions {
	@SuppressWarnings("unchecked")
	public static BitmapFont create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		
		BitmapFont font = null;
		if("default".equals(constructor)){
			font = new BitmapFont();
		}else if("data_pageRegions_integer".equals(constructor)){
			BitmapFont.BitmapFontData data = (BitmapFont.BitmapFontData) actionContext.get("data");
			Array<TextureRegion>  pageRegions = (Array<TextureRegion>) actionContext.get("pageRegions");
			font = new BitmapFont(data, pageRegions, self.getBoolean("integer"));
		}else if("data_region_integer".equals(constructor)){
			BitmapFontData data = UtilData.getObjectByType(self, "data", BitmapFontData.class, actionContext);
			TextureRegion  region = UtilData.getObjectByType(self, "region", TextureRegion.class, actionContext);
			font = new BitmapFont(data, region, self.getBoolean("integer"));
		}else if("flip".equals(constructor)){
			font = new BitmapFont(self.getBoolean("flip"));
		}else if("fontFile".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			font = new BitmapFont(fontFile);
		}else if("fontFile_flip".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			font = new BitmapFont(fontFile, self.getBoolean("flip"));
		}else if("fontFile_imageFile_flip".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			FileHandle imageFile = UtilData.getObjectByType(self, "imageFile", FileHandle.class, actionContext);
			font = new BitmapFont(fontFile, imageFile, self.getBoolean("flip"));
		}else if("fontFile_imageFile_flip_integer".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			FileHandle imageFile = UtilData.getObjectByType(self, "imageFile", FileHandle.class, actionContext);
			font = new BitmapFont(fontFile, imageFile, self.getBoolean("flip"), self.getBoolean("integer"));
		}else if("fontFile_region".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			TextureRegion  region = UtilData.getObjectByType(self, "region", TextureRegion.class, actionContext);
			font = new BitmapFont(fontFile, region);
		}else if("fontFile_region_flip".equals(constructor)){
			FileHandle fontFile = UtilData.getObjectByType(self, "fontFile", FileHandle.class, actionContext);
			TextureRegion  region = UtilData.getObjectByType(self, "region", TextureRegion.class, actionContext);
			font = new BitmapFont(fontFile, region, self.getBoolean("flip"));
		}else{
			throw new ActionException("No matched constructor, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), font);
		return font;
	}
}
