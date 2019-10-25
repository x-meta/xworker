package xworker.libdgx.tools.resource;

import java.util.List;

public class ResourceTypes {
	public static final String TYPE_TEXTURE = "texture";
	public static final String TYPE_TEXTUREATLAS = "textureAtlas";
	public static final String TYPE_TEXTUREREGION = "textureRegion";
	public static final String TYPE_SPRITE = "sprite";
	public static final String TYPE_SPRITEBATCH = "spriteBatch";
	public static final String TYPE_NINEPATH = "ninePatch";
	public static final String TYPE_BITMAPFONT = "bitmapFont";
	public static final String TYPE_DRAWABLE = "drawable";
	public static final String TYPE_COLOR = "color";
	public static final String TYPE_FILEHANDLE = "fileHandle";
	public static final String TYPE_MUSIC = "music";
	public static final String TYPE_SOUND = "sound";
	public static final String TYPE_CUBEMAP = "cubemap";
	public static final String TYPE_I18NBUNDLE = "i18NBundle";
	public static final String TYPE_PARTICLEEFFECT = "particleEffect";
	public static final String TYPE_PIXMAP = "pixmap";
	public static final String TYPE_SPRITES = "sprites";
	public static final String TYPE_TEXTUREREGIONS = "textureRegions";
	public static final String TYPE_TEXTURES = "textures";
	public static final String TYPE_TILEDMAP = "tiledMap";
	
	public static final String[] TYPES_DRAWABLE = new String[]{TYPE_TEXTURE, TYPE_TEXTUREATLAS, 
		TYPE_TEXTUREREGION, TYPE_SPRITE, TYPE_NINEPATH, TYPE_DRAWABLE};
	
	public static boolean isResouce(List<String> acceptTypes, String targetType){
		for(String at : acceptTypes){
			if(at.equals(targetType)){
				return true;
			}
		}
		
		if(TYPE_DRAWABLE.equals(targetType)){
			for(String at : acceptTypes){
				for(String ts : TYPES_DRAWABLE){
					if(at.equals(ts)){
						return true;
					}
				}
			}
			
			return false;
		}
		
		return false;
	}
}
