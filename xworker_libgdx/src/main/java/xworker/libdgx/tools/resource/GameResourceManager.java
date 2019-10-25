package xworker.libdgx.tools.resource;

import java.io.File;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader.AtlasTiledMapLoaderParameters;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class GameResourceManager {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String resource = self.getStringBlankAsNull("resource");
		if(resource == null){
			return null;
		}
		
		String[] args = resource.split("[|]");
		long id = Long.parseLong(args[0]);
		
		DataObject resObj = DataObjectUtil.load("xworker.libgdx.tools.dataobjects.Resource", id, actionContext);
		String type = resObj.getString("type");
		String resType = self.getStringBlankAsNull("type");
		actionContext.peek().put("resource", resObj);
		actionContext.peek().put("type", type);
		actionContext.peek().put("args", args);
		if(resType == null){
			//没有指定类型，使用资源默认的类型
			GameResourceUpdater.initFileTypes();
			Thing fileType = GameResourceUpdater.getFileType(type);
			if(fileType != null){
				Object obj = fileType.doAction("getResource", actionContext);
				actionContext.getScope(0).put(self.getMetadata().getName(), obj);
				return obj;
			}else{
				throw new ActionException("can not find file type , thing=" + self.getMetadata().getPath() + "\n    resourceObj=" + resObj);
			}
		}else{
			//使用指定的类型
			Object obj = null;
			if("fileHandle".equals(resType)){
				obj = FileTypeActions.getFileHandleResource(actionContext);
			}else if("texture".equals(resType)){
				obj = FileTypeActions.getTextureResource(actionContext);				
			}else if("textureAtals".equals(resType)){
				obj = FileTypeActions.getTextureAtlasResource(actionContext);
			}else if("textureRegion".equals(resType)){
				obj = FileTypeActions.getTextureRegionRresource(actionContext);
			}else if("textureRegions".equals(resType)){
				obj = FileTypeActions.getTextureRegionsRresource(actionContext);
			}else if("textureRegionIndex".equals(resType)){
				obj = FileTypeActions.getTextureRegionIndexRresource(actionContext);
			}else if("textureAtalsSprite".equals(resType)){
				obj = FileTypeActions.getTextureAtalsSpriteRresource(actionContext);
			}else if("textureAtalsSprites".equals(resType)){
				obj = FileTypeActions.getTextureAtalsSpritesRresource(actionContext);
			}else if("textureAtalsSpriteIndex".equals(resType)){
				obj = FileTypeActions.getTextureAtalsSpriteIndexRresource(actionContext);
			}else if("textureAtalsNinePatch".equals(resType)){
				obj = FileTypeActions.getTextureAtalsNinePatchRresource(actionContext);
			}else if("tiledMap".equals(resType)){
				
			}
			
			actionContext.getScope(0).put(self.getMetadata().getName(), obj);
			return obj;
		}		
	}
	
	public static Object createFileResource(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");		
		
		String file = getFilePath(self.getStringBlankAsNull("file"));
		if(file == null){
			throw new ActionException("File is null, thing=" + self.getMetadata().getPath());
		}
		
		String type = self.getStringBlankAsNull("type");
		
		Object resObj = null;
		if("texture".equals(type)){
			resObj = load(self, type, file, Texture.class, actionContext);
		}else if("textureAtals".equals(type)){
			resObj = load(self, type, file, TextureAtlas.class, actionContext);
		}else if("fileHandle".equals(type)){
			resObj = Gdx.files.absolute(file);
		}else if("cubemap".equals(type)){
			resObj = load(self, type, file, Cubemap.class, actionContext);
		}else if("bitmapFont".equals(type)){
			resObj = load(self, type, file, BitmapFont.class, actionContext);
		}else if("freeTypeFont".equals(type)){
			resObj = load(self, type, file, BitmapFont.class, actionContext);
		}else if("i18NBundle".equals(type)){
			resObj = load(self, type, file, I18NBundle.class, actionContext);
		}else if("music".equals(type)){
			resObj = load(self, type, file, Music.class, actionContext);
		}else if("particleEffect".equals(type)){
			resObj = load(self, type, file, ParticleEffect.class, actionContext);
		}else if("sound".equals(type)){
			resObj = load(self, type, file, Sound.class, actionContext);
		}else if("skin".equals(type)){
			resObj = load(self, type, file, Skin.class, actionContext);
		}else if("tiledMap".equals(type) || "atlasTmxMap".equals(type) 
				|| "baseTmxMap".equals(type) || "tideMap".equals(type) || "tmxMap".equals(type)){
			resObj = load(self, type, file, TiledMap.class, actionContext);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), resObj);
		return resObj;
	}
	
	/**
	 * 数据库资源。
	 * 
	 * @param actionContext
	 */
	public static void resourcePreview(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String resource = self.getString("resource");
		DataObject res = DataObjectUtil.load("xworker.libgdx.tools.dataobjects.Resource", Long.parseLong(resource), actionContext);
		GameResourceUpdater.initFileTypes();
		Thing fileType = GameResourceUpdater.getFileType(res.getString("type"));
		if(fileType != null){
		    fileType.doAction("preview", actionContext, UtilMap.toMap("resource", res));
		}
	}
	
	public static String getFilePath(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			return filePath;
		}else{			
			Thing config = World.getInstance().getThing("_local.xworker.config.GlobalConfig");
			String libgdxPath =config.getString("libgdxPath");
			
			if(libgdxPath == null || "".equals(libgdxPath)){
				File dir = new File(World.getInstance().getPath() + "/work/libgdx/");
				if(dir.exists() && dir.isDirectory()) {
					libgdxPath = dir.getPath();
				}
			}
			
			if(libgdxPath == null || "".equals(libgdxPath)){
				throw new ActionException("Please set libgdxpath in _local.xworker.config.GlobalConfig");
			}
			return libgdxPath + filePath;
		}
	}
	
	/**
	 * 文件资源预览。
	 * 
	 * @param actionContext
	 */
	public static void fileResourcePreview(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
				
		String file = getFilePath(self.getString("file"));
		String type = self.getString("type");

		Thing fileType = null;
		if("texture".equals(type)){
		    fileType = world.getThing("xworker.libgdx.tools.resource.FileTypes/@Image");
		}else if("textureAtals".equals(type)){
		    fileType = world.getThing("xworker.libgdx.tools.resource.FileTypes/@TextureAtals");
		}else{
			fileType = world.getThing("xworker.libgdx.tools.resource.FileTypes/@DefaultFile");
		}

		DataObject resource = new DataObject("xworker.libgdx.tools.dataobjects.Resource");
		resource.put("path", file);

		if(fileType != null){
		    fileType.doAction("preview", actionContext, UtilMap.toMap("resource", resource));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T load(Thing self, String type, String file, Class<T> t, ActionContext actionContext){
		FileHandle fileHandle = Gdx.files.absolute(file);
		AssetManager assetManager = GameResourceManager.getAssetManager(actionContext);
		
		AssetLoaderParameters param = FileTypeActions.getLoadParameter(self, AssetLoaderParameters.class, actionContext);
		
		AssetDescriptor<T> desc = null;
		if(param != null){
			desc = new AssetDescriptor<T> (fileHandle, t, param);
		}else{
			desc = new AssetDescriptor<T> (fileHandle, t);
		}
		
		if("atlasTmxMap".equals(type)){
			if(param != null){
				return (T) new AtlasTmxMapLoader(new AbsoluteFileHandleResolver()).load(file, (AtlasTiledMapLoaderParameters) param);
			}else{
				return (T) new AtlasTmxMapLoader(new AbsoluteFileHandleResolver()).load(file);
			}
		}else if("tideMap".equals(type)){
			return (T) new TideMapLoader(new AbsoluteFileHandleResolver()).load(file);
		}else if("tmxMap".equals(type)){
			if(param != null){
				return (T) new TmxMapLoader(new AbsoluteFileHandleResolver()).load(file, (Parameters) param);
			}else{
				return (T) new TmxMapLoader(new AbsoluteFileHandleResolver()).load(file);
			}
		}
		if(assetManager.getLoader(t) == null){
			if(t == TiledMap.class){
				assetManager.setLoader(TiledMap.class, new TideMapLoader());
			}
		}
		
		T object = null;
		if(assetManager.isLoaded(file)){
			object = assetManager.get(desc);
		}else{
			assetManager.load(desc);
			assetManager.finishLoading();
			object = assetManager.get(desc);
		}
		
		return object;
	}
	
	/**
	 * 获取AssetManager，如果没有创建一个，放到actionContext的全局变量层中。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static AssetManager getAssetManager(ActionContext actionContext){
		synchronized(actionContext){
			AssetManager assetManager = (AssetManager) actionContext.get("assetManager");
			if(assetManager == null){
				assetManager = new AssetManager();
				actionContext.getScope(0).put("assetManager", assetManager);
			}
			
			return assetManager;
		}
	}
}
