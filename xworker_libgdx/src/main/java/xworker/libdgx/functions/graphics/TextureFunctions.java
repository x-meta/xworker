package xworker.libdgx.functions.graphics;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureFunctions {
	public static Object createTexture_file(ActionContext actionContext){
		FileHandle file = (FileHandle) actionContext.get("file");
		return new Texture(file);
	}
	
	public static Object createTexture_file_useMipMaps(ActionContext actionContext){
		FileHandle file = (FileHandle) actionContext.get("file");
		Boolean useMipMaps = (Boolean) actionContext.get("useMipMaps");
		return new Texture(file, useMipMaps);
	}
	
	public static Object createTexture_file_format_useMipMaps(ActionContext actionContext){
		FileHandle file = (FileHandle) actionContext.get("file");
		Pixmap.Format format = (Pixmap.Format) actionContext.get("format");
		Boolean useMipMaps = (Boolean) actionContext.get("useMipMaps");
		return new Texture(file, format, useMipMaps);
	}
	
	public static Object createTexture_width_height_format(ActionContext actionContext){
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		Pixmap.Format format = (Pixmap.Format) actionContext.get("format");
		
		return new Texture(width.intValue(), height.intValue(), format);
	}
	
	public static Object createTexture_pixmap(ActionContext actionContext){
		Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
		
		return new Texture(pixmap);
	}
	
	public static Object createTexture_pixmap_useMipMaps(ActionContext actionContext){
		Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
		Boolean useMipMaps = (Boolean) actionContext.get("useMipMaps");
		return new Texture(pixmap, useMipMaps);
	}
	
	public static Object createTexture_pixmap_format_useMipMaps(ActionContext actionContext){
		Pixmap pixmap = (Pixmap) actionContext.get("pixmap");
		Pixmap.Format format = (Pixmap.Format) actionContext.get("format");
		Boolean useMipMaps = (Boolean) actionContext.get("useMipMaps");
		return new Texture(pixmap, format, useMipMaps);
	}
	
	public static Object createTexture_internalPath(ActionContext actionContext){
		String internalPath = (String) actionContext.get("internalPath");
		return new Texture(internalPath);
	}
}
