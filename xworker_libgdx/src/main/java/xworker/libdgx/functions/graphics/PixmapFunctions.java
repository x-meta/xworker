package xworker.libdgx.functions.graphics;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

public class PixmapFunctions {
	public static Object createPixmap_encodedData_offset_len(ActionContext actionContext){
		byte[] encodedData = (byte[]) actionContext.get("encodedData");
		Number offset = (Number) actionContext.get("offset");
		Number len = (Number) actionContext.get("len");
		
		return new Pixmap(encodedData, offset.intValue(), len.intValue());
	}
	
	public static Object createPixmap_file(ActionContext actionContext){
		FileHandle file = (FileHandle) actionContext.get("file");
		
		return new Pixmap(file);
	}
	
	public static Object createPixmap_pixmap(ActionContext actionContext){
		Gdx2DPixmap pixmap = (Gdx2DPixmap) actionContext.get("pixmap");
		
		return new Pixmap(pixmap);
	}
	
	public static Object createPixmap_width_height_format(ActionContext actionContext){
		Pixmap.Format format = (Pixmap.Format) actionContext.get("encodedData");
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		
		return new Pixmap(width.intValue(), height.intValue(), format);
	}
	
	public static Object formatAlpha(ActionContext actionContext){
		return Pixmap.Format.Alpha;
	}
	
	public static Object formatIntensity(ActionContext actionContext){
		return Pixmap.Format.Intensity;
	}
	
	public static Object formatLuminanceAlpha(ActionContext actionContext){
		return Pixmap.Format.LuminanceAlpha;
	}
	
	public static Object formatRGB565(ActionContext actionContext){
		return Pixmap.Format.RGB565;
	}
	
	public static Object formatRGB888(ActionContext actionContext){
		return Pixmap.Format.RGB888;
	}
	
	public static Object formatRGBA4444(ActionContext actionContext){
		return Pixmap.Format.RGBA4444;
	}
	
	public static Object formatRGBA8888(ActionContext actionContext){
		return Pixmap.Format.RGBA8888;
	}
}
