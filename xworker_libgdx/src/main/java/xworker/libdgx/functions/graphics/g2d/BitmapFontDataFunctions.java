package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

public class BitmapFontDataFunctions {
	public static Object createBitmapFontData(ActionContext actionContext){
		return new BitmapFontData();
	}
	
	public static Object createBitmapFontData_fontFile_flip(ActionContext actionContext){
		FileHandle fontFile = (FileHandle) actionContext.get("fontFile");
		Boolean flip = (Boolean) actionContext.get("flip");
		
		return new BitmapFontData(fontFile, flip);
	}
}
