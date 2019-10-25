package xworker.libdgx.tools;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import xworker.libdgx.ConstantsUtils;

public class TexturePackerActions {
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TexturePacker.Settings settings = getSettings(self);
		
		TexturePacker.process(settings, self.getStringBlankAsNull("input"), self.getString("output"), self.getString("packFileName"));
	}
	
	public static void processIfModified(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		TexturePacker.Settings settings = getSettings(self);
		
		TexturePacker.processIfModified(settings, self.getStringBlankAsNull("input"), self.getString("output"), self.getString("packFileName"));
	}
	
	public static TexturePacker.Settings getSettings(Thing self){
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.pot = self.getBoolean("pot");
		settings.paddingX = self.getInt("paddingX");
		settings.paddingY = self.getInt("paddingY");
		settings.edgePadding = self.getBoolean("edgePadding");
		settings.duplicatePadding = self.getBoolean("duplicatePadding");
		settings.rotation = self.getBoolean("rotation");
		settings.minWidth = self.getInt("minWidth");
		settings.minHeight = self.getInt("minHeight");
		settings.maxWidth = self.getInt("maxWidth");
		settings.maxHeight = self.getInt("maxHeight");
		//settings.forceSquareOutput = self.getBoolean("forceSquareOutput");
		settings.stripWhitespaceX = self.getBoolean("stripWhitespaceX");
		settings.stripWhitespaceY = self.getBoolean("stripWhitespaceY");
		settings.alphaThreshold = self.getInt("alphaThreshold");
		settings.filterMin = ConstantsUtils.getTextureFilter(self.getStringBlankAsNull("filterMin"));
		settings.filterMag = ConstantsUtils.getTextureFilter(self.getStringBlankAsNull("filterMag"));
		settings.wrapX = ConstantsUtils.getTextureWrap(self.getStringBlankAsNull("wrapX"));
		settings.wrapY = ConstantsUtils.getTextureWrap(self.getStringBlankAsNull("wrapY"));
		settings.format = ConstantsUtils.getFormat(self.getStringBlankAsNull("format"));
		settings.alias = self.getBoolean("alias");
		settings.outputFormat = self.getString("outputFormat");
		settings.jpegQuality = self.getFloat("jpegQuality");
		settings.ignoreBlankImages = self.getBoolean("ignoreBlankImages");
		settings.fast = self.getBoolean("fast");
		settings.debug = self.getBoolean("debug");
		settings.combineSubdirectories = self.getBoolean("combineSubdirectories");
		settings.flattenPaths = self.getBoolean("flattenPaths");
		settings.premultiplyAlpha = self.getBoolean("premultiplyAlpha");
		settings.useIndexes = self.getBoolean("useIndexes");
		settings.bleed = self.getBoolean("bleed");
		settings.limitMemory = self.getBoolean("limitMemory");
		
		return settings;
	}
}
