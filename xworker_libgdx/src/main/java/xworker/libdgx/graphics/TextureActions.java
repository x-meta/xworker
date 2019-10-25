package xworker.libdgx.graphics;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.TextureData;

import ognl.OgnlException;

public class TextureActions {
	public static Texture create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String constructor = self.getStringBlankAsNull("constructor");
		Texture texture = null;
		if("file".equals(constructor)){
			FileHandle file = UtilData.getObjectByType(self, "file", FileHandle.class, actionContext);
			texture = new Texture(file);
		}else if("file_useMipMaps".equals(constructor)){
			FileHandle file = UtilData.getObjectByType(self, "file", FileHandle.class, actionContext);
			texture = new Texture(file, self.getBoolean("useMipMaps"));
		}else if("file_format_useMipMaps".equals(constructor)){
			FileHandle file = UtilData.getObjectByType(self, "file", FileHandle.class, actionContext);
			Pixmap.Format format = PixmapActions.getFormat(self.getString("format"));
			texture = new Texture(file, format, self.getBoolean("useMipMaps"));
		}else if("width_height_format".equals(constructor)){
			Pixmap.Format format = PixmapActions.getFormat(self.getString("format"));
			texture = new Texture(self.getInt("width"), self.getInt("height"), format);
		}else if("pixmap".equals(constructor)){
			Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
			texture = new Texture(pixmap);
		}else if("pixmap_useMipMaps".equals(constructor)){
			Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
			texture = new Texture(pixmap, self.getBoolean("useMipMaps"));
		}else if("pixmap_format_useMipMaps".equals(constructor)){
			Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
			Pixmap.Format format = PixmapActions.getFormat(self.getString("format"));
			texture = new Texture(pixmap, format, self.getBoolean("useMipMaps"));
		}else if("internalPath".equals(constructor)){
			texture = new Texture(self.getString("internalPath"));
		}else if("data".equals(constructor)){
			TextureData data = (TextureData) actionContext.get("data");
			texture = new Texture(data);
		} 
	 
		String minFilter = self.getStringBlankAsNull("magFilter");
		String magFilter = self.getStringBlankAsNull("minFilter");
		if(minFilter != null && magFilter != null){
			texture.setFilter(getTextureFilter(minFilter), getTextureFilter(magFilter));
		}
		
		String uWrap = self.getStringBlankAsNull("uWrap");
		String vWrap = self.getStringBlankAsNull("vWrap");
		if(uWrap != null && vWrap != null){
			texture.setWrap(getTextureWrap(uWrap), getTextureWrap(vWrap));
		}
				
		actionContext.getScope(0).put(self.getMetadata().getName(), texture);
		
		return texture;
	}
	
	public static TextureFilter getTextureFilter(String name){
		if("Linear".equals(name)){
			return TextureFilter.Linear;
		}else if("MipMap".equals(name)){
			return TextureFilter.MipMap;
		}else if("MipMapLinearLinear".equals(name)){
			return TextureFilter.MipMapLinearLinear;
		}else if("MipMapLinearNearest".equals(name)){
			return TextureFilter.MipMapLinearNearest;
		}else if("MipMapNearestLinear".equals(name)){
			return TextureFilter.MipMapNearestLinear;
		}else if("MipMapNearestNearest".equals(name)){
			return TextureFilter.MipMapNearestNearest;
		}else if("Nearest".equals(name)){
			return TextureFilter.Nearest;
		}else{
			return TextureFilter.Linear;
		}
	}
	
	public static TextureWrap getTextureWrap(String name){
		if("ClampToEdge".equals(name)){
			return TextureWrap.ClampToEdge;
		}else if("MirroredRepeat".equals(name)){
			return TextureWrap.MirroredRepeat;
		}else if("Repeat".equals(name)){
			return TextureWrap.Repeat;
		}else{
			return TextureWrap.ClampToEdge;
		}
	}
}
