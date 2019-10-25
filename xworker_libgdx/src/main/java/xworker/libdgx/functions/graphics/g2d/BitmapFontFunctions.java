package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BitmapFontFunctions {
	public static Object createBitmapFont(ActionContext actionContext){
		return new BitmapFont();
	}
	
	public static Object createBitmapFont_data_region_integer(ActionContext actionContext){
		BitmapFont.BitmapFontData data = (BitmapFont.BitmapFontData) actionContext.get("data");
		TextureRegion region = (TextureRegion) actionContext.get("region");
		Boolean integer = (Boolean) actionContext.get("integer");
		
		return new BitmapFont(data, region, integer);
	}
	
	public static Object createBitmapFont_flip(ActionContext actionContext){
		Boolean flip = (Boolean) actionContext.get("flip");
		
		return new BitmapFont(flip);
	}
	
	public static Object createBitmapFont_fontFile_flip(ActionContext actionContext){
		FileHandle fontFile = (FileHandle) actionContext.get("fontFile");
		Boolean flip = (Boolean) actionContext.get("flip");
		
		return new BitmapFont(fontFile, flip);
	}
	
	public static Object createBitmapFont_fontFile_imageFile_flip(ActionContext actionContext){
		FileHandle fontFile = (FileHandle) actionContext.get("fontFile");
		FileHandle imageFile = (FileHandle) actionContext.get("imageFile");
		Boolean flip = (Boolean) actionContext.get("flip");
		
		return new BitmapFont(fontFile, imageFile, flip);
	}
	
	public static Object createBitmapFont_fontFile_imageFile_flip_integer(ActionContext actionContext){
		FileHandle fontFile = (FileHandle) actionContext.get("fontFile");
		FileHandle imageFile = (FileHandle) actionContext.get("imageFile");
		Boolean flip = (Boolean) actionContext.get("flip");
		Boolean integer = (Boolean) actionContext.get("integer");
		
		return new BitmapFont(fontFile, imageFile, flip, integer);
	}
	
	public static Object createBitmapFont_fontFile_region_flip(ActionContext actionContext){
		FileHandle fontFile = (FileHandle) actionContext.get("fontFile");
		TextureRegion region = (TextureRegion) actionContext.get("region");
		Boolean flip = (Boolean) actionContext.get("flip");
		
		return new BitmapFont(fontFile, region, flip);
	}
}
