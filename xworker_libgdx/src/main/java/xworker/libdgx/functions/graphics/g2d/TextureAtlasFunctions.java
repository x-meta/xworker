package xworker.libdgx.functions.graphics.g2d;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureAtlasFunctions {
	public static Object createTextureAtlas(ActionContext actionContext){
		return new TextureAtlas();
	}
	
	public static Object createTextureAtlas_packFile(ActionContext actionContext){
		FileHandle packFile = (FileHandle) actionContext.get("packFile");
		return new TextureAtlas(packFile);
	}
	
	public static Object createTextureAtlas_packFile_flip(ActionContext actionContext){
		FileHandle packFile = (FileHandle) actionContext.get("packFile");
		Boolean flip = (Boolean) actionContext.get("flip");
		return new TextureAtlas(packFile, flip);
	}
	
	public static Object createTextureAtlas_packFile_imagesDir(ActionContext actionContext){
		FileHandle packFile = (FileHandle) actionContext.get("packFile");
		FileHandle imagesDir = (FileHandle) actionContext.get("imagesDir");
		return new TextureAtlas(packFile, imagesDir);
	}
	
	public static Object createTextureAtlas_packFile_imagesDir_flip(ActionContext actionContext){
		FileHandle packFile = (FileHandle) actionContext.get("packFile");
		FileHandle imagesDir = (FileHandle) actionContext.get("imagesDir");
		Boolean flip = (Boolean) actionContext.get("flip");
		return new TextureAtlas(packFile, imagesDir, flip);
	}
	
	public static Object createTextureAtlas_internalPackFile(ActionContext actionContext){
		String internalPackFile = (String) actionContext.get("internalPackFile");
		return new TextureAtlas(internalPackFile);
	}
}
