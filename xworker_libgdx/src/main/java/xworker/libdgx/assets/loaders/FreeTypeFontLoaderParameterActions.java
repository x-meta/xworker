package xworker.libdgx.assets.loaders;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

import ognl.OgnlException;
import xworker.game.ChineseChars;
import xworker.libdgx.graphics.TextureActions;

public class FreeTypeFontLoaderParameterActions {
	public static FreeTypeFontLoaderParameter create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		FreeTypeFontLoaderParameter item = new FreeTypeFontLoaderParameter();
		item.fontFileName = self.getString("fontFileName");
		
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		item.fontParameters = param;
		
		if(self.getStringBlankAsNull("size") != null){
			param.size = self.getInt("size");
		}
		
		if(self.getStringBlankAsNull("color") != null){
			param.color = UtilData.getObjectByType(self, "color", Color.class, actionContext);
		}
		
		if(self.getStringBlankAsNull("borderWidth") != null){
			param.borderWidth = self.getFloat("borderWidth");
		}
		
		if(self.getStringBlankAsNull("borderStraight") != null){
			param.borderStraight = self.getBoolean("borderStraight");
		}
		
		if(self.getStringBlankAsNull("shadowOffsetX") != null){
			param.shadowOffsetX = self.getInt("shadowOffsetX");
		}
		
		if(self.getStringBlankAsNull("shadowOffsetY") != null){
			param.shadowOffsetY = self.getInt("shadowOffsetY");
		}
		
		if(self.getStringBlankAsNull("shadowColor") != null){
			param.shadowColor = UtilData.getObjectByType(self, "shadowColor", Color.class, actionContext);
		}
		
		if(self.getStringBlankAsNull("characters") != null){			
			param.characters = self.getString("characters");
		}else{
			param.characters = ChineseChars.CHINESE_CHARS;
		}
		
		if(self.getStringBlankAsNull("kerning") != null){
			param.kerning = self.getBoolean("kerning");
		}
		
		if(self.getStringBlankAsNull("packer") != null){
			param.packer = UtilData.getObjectByType(self, "packer", PixmapPacker.class, actionContext);
		}
		
		if(self.getStringBlankAsNull("flip") != null){
			param.flip = self.getBoolean("flip");
		}
		
		
		if(self.getStringBlankAsNull("genMipMaps") != null){
			param.genMipMaps = self.getBoolean("genMipMaps");
		}
		
		if(self.getStringBlankAsNull("minFilter") != null){
			param.minFilter = TextureActions.getTextureFilter(self.getString("minFilter"));
		}
		
		if(self.getStringBlankAsNull("magFilter") != null){
			param.magFilter = TextureActions.getTextureFilter(self.getString("magFilter"));
		}
		
		if(self.getStringBlankAsNull("incremental") != null){
			param.incremental = self.getBoolean("incremental");
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
