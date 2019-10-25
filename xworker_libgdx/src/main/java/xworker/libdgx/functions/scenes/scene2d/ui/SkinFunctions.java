package xworker.libdgx.functions.scenes.scene2d.ui;

import org.xmeta.ActionContext;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinFunctions {
	public static Object createSkin(ActionContext actionContext){
		return new Skin();
	}
	
	public static Object createSkin_skinFile(ActionContext actionContext){
		FileHandle skinFile = (FileHandle) actionContext.get("skinFile");
		return new Skin(skinFile);
	}
	
	public static Object createSkin_skinFile_atlas(ActionContext actionContext){
		FileHandle skinFile = (FileHandle) actionContext.get("skinFile");
		TextureAtlas atlas = (TextureAtlas) actionContext.get("atlas");
		return new Skin(skinFile, atlas);
	}
	
	public static Object createSkin_atlas(ActionContext actionContext){
		TextureAtlas atlas = (TextureAtlas) actionContext.get("atlas");
		return new Skin(atlas);
	}
}
