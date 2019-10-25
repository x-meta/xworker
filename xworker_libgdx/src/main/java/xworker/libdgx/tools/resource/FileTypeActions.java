package xworker.libdgx.tools.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.libdgx.tools.textureAtlasEditor.Page;
import xworker.libdgx.tools.textureAtlasEditor.Region;
import xworker.libdgx.tools.textureAtlasEditor.TextureAtlasInfo;
import xworker.swt.ActionContainer;

public class FileTypeActions {
	private static Logger logger = LoggerFactory.getLogger(FileTypeActions.class);
	
	/**
	 * 更新资源。
	 * 
	 * @param actionContext
	 */
	public static DataObject updateResource(ActionContext actionContext){
		Long treeParentId = (Long) actionContext.get("treeParentId");
		Long treeId = (Long) actionContext.get("treeId");
		File file = (File) actionContext.get("file");
		String type = (String) actionContext.get("type");
		//类型默认使用文件后缀
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		if(index != -1){
			type = fileName.substring(index + 1, fileName.length()).toLowerCase();
		}
		List<DataObject> objs = DataObjectUtil.query("xworker.libgdx.tools.dataobjects.Resource", 
				UtilMap.toMap("treeId", treeId), actionContext);
		if(objs.size() > 0){
			DataObject obj = objs.get(0);
			long lastModified = obj.getDate("lastModified").getTime();
			if(lastModified != file.lastModified() || !type.endsWith(obj.getString("type"))){
				obj.put("lastModified", new Date(file.lastModified()));
				obj.put("type", type);
				obj.doAction("update", actionContext);
			}
			
			return obj;
		}else{
			DataObject obj = new DataObject("xworker.libgdx.tools.dataobjects.Resource");
			obj.put("name", file.getName());
			obj.put("type", type);
			obj.put("path", file.getAbsolutePath());
			obj.put("lastModified", new Date(file.lastModified()));
			obj.put("treeParentId", treeParentId);
			obj.put("treeId", treeId);
			obj = obj.create(actionContext);
			return obj;
		}
	}
	
	public static FileHandle getFileHandleResource(ActionContext actionContext){
		DataObject resource = (DataObject) actionContext.get("resource");
		String path = resource.getString("path");
		return Gdx.files.external(path);
	}
	
	public static Texture getTextureResource(ActionContext actionContext){		
		Thing self = (Thing) actionContext.get("self");
		return loadResource(self, Texture.class, actionContext);		
	}
	
	public static TextureAtlas getTextureAtlasResource(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return loadResource(self, TextureAtlas.class, actionContext);	
	}
		
	public static TextureRegion getTextureRegionRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.findRegion(args[1]);
	}
	
	public static TextureRegion getTextureRegionIndexRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.findRegion(args[1], Integer.parseInt(args[2]));
	}
	
	public static Object getTextureRegionsRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.findRegions(args[1]);
	}
	
	public static Object getTextureAtalsSpriteRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.createSprite(args[1]);
	}
	
	public static Object getTextureAtalsSpriteIndexRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.createSprite(args[1], Integer.parseInt(args[2]));
	}
	
	public static Object getTextureAtalsSpritesRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.createSprites(args[1]);
	}
	
	public static Object getTextureAtalsNinePatchRresource(ActionContext actionContext){
		TextureAtlas atlas = getTextureAtlasResource(actionContext);
		String[] args = (String[]) actionContext.get("args");
		return atlas.createPatch(args[1]);
	}
	
	
	public static void imagePreview(ActionContext actionContext){		
		Composite parentComposite = (Composite) actionContext.get("parentComposite");
		DataObject resource = (DataObject) actionContext.get("resource");
		
		//获取ImageViewer
		Thing imageViewer = (Thing) parentComposite.getData("imageViewer");
		if(imageViewer == null){
			ActionContext ac = new ActionContext();
			ac.put("parent", parentComposite);
			Thing canvasThing = new Thing("xworker.swt.xwidgets.ImageCanvas");
			canvasThing.put("name", "canvas");
			Control viewComposite = (Control) canvasThing.doAction("create", ac);
			imageViewer = (Thing) ac.get("canvas");
			parentComposite.setData("imageViewer", imageViewer);
			parentComposite.setData("imageViewerComposite", viewComposite);
			viewComposite.setData(imageViewer);
			
			//确认销毁预览的图片
			viewComposite.addDisposeListener(new DisposeListener(){
				@Override
				public void widgetDisposed(DisposeEvent event) {
					Thing imageViewer = (Thing) event.widget.getData();
					if(imageViewer != null){
						Image image = (Image) imageViewer.get("image");
						if(image != null){
							image.dispose();
						}
					}
				}
				
			});
		}
		
		//显示图片预览控件
		Control viewComposite = (Control) parentComposite.getData("imageViewerComposite");
		StackLayout layout = (StackLayout) parentComposite.getLayout();
		layout.topControl = viewComposite;
		parentComposite.layout();
		
		//显示图片
		Image oldImage = (Image) imageViewer.get("image");
		String path = GameResourceManager.getFilePath(resource.getString("path"));
		try{
			Image image = new Image(parentComposite.getDisplay(), path);
			imageViewer.doAction("setImage", actionContext, UtilMap.toMap("image", image));
		}catch(Exception e){
			logger.error("preview iamge error", e);
		}
		if(oldImage != null){
			oldImage.dispose();
		}		
	}
	
	public static void audioPreview(ActionContext actionContext){
		
	}
	
	public static void atalsPreview(ActionContext actionContext){
		Composite parentComposite = (Composite) actionContext.get("parentComposite");
		DataObject resource = (DataObject) actionContext.get("resource");
		
		//获取textViewer
		ActionContainer viewer = (ActionContainer) parentComposite.getData("atalsViewer");
		
		if(viewer == null){
			ActionContext ac = new ActionContext();
			ac.put("parent", parentComposite);
			ac.put("selector", actionContext.get("selector"));
			Thing canvasThing = World.getInstance().getThing("xworker.libgdx.tools.TextureAtlasEditor/@mainComposite");
			canvasThing.put("name", "atalsViewer");
			Composite viewComposite = (Composite) canvasThing.doAction("create", ac);
			viewer = (ActionContainer) ac.get("actions");
			parentComposite.setData("atalsViewer", viewer);
			parentComposite.setData("atalsViewerComposite", viewComposite);
		}
		
		//显示图片预览控件
		Composite viewComposite = (Composite) parentComposite.getData("atalsViewerComposite");
		StackLayout layout = (StackLayout) parentComposite.getLayout();
		layout.topControl = viewComposite;
		parentComposite.layout();
		
		//显示图片
				
		String path = resource.getString("path");
		if(path == null || "".equals(path.trim())){
			return;
		}
				
		path = GameResourceManager.getFilePath(path);
		try{
			File file = new File(path);
			viewer.doAction("setFile", actionContext, UtilMap.toMap("file", file, "resource", resource));
		}catch(Exception e){
			logger.error("preview text error", e);
		}
	}
	
	public static void filePreview(ActionContext actionContext){
		
	}
	
	public static void textPreview(ActionContext actionContext){
		Composite parentComposite = (Composite) actionContext.get("parentComposite");
		DataObject resource = (DataObject) actionContext.get("resource");
		
		//获取textViewer
		ActionContainer viewer = (ActionContainer) parentComposite.getData("textViewer");
		
		if(viewer == null){
			ActionContext ac = new ActionContext();
			ac.put("parent", parentComposite);
			Thing canvasThing = new Thing("xworker.swt.Widgets/@TextFileEditor");
			canvasThing.put("name", "textViewer");
			Composite viewComposite = (Composite) canvasThing.doAction("create", ac);
			viewer = (ActionContainer) ac.get("textViewer");
			parentComposite.setData("textViewer", viewer);
			parentComposite.setData("textViewerComposite", viewComposite);
		}
		
		//显示图片预览控件
		Composite viewComposite = (Composite) parentComposite.getData("textViewerComposite");
		StackLayout layout = (StackLayout) parentComposite.getLayout();
		layout.topControl = viewComposite;
		parentComposite.layout();
		
		//显示图片
				
		String path = resource.getString("path");
		try{
			File file = new File(path);
			viewer.doAction("setFile", actionContext, UtilMap.toMap("file", file));
		}catch(Exception e){
			logger.error("preview text error", e);
		}
	}
	
	public static void updateAtalsRrsource(ActionContext actionContext) throws IOException{
		//首先更新自己
		DataObject obj = updateResource(actionContext);
		
		//更新Atals中的内容
		File file = (File) actionContext.get("file");
		TextureAtlasInfo info = TextureAtlasInfo.load(file);
		
		//Atals对应的信息
		List<ResourceInfo> pageRes = new ArrayList<ResourceInfo>();
		for(Page page : info.getPages()){
			ResourceInfo resInfo = new ResourceInfo();
			resInfo.lastModified = new Date(file.lastModified());
			resInfo.name = page.getFileName();
			resInfo.parentTreeId = obj.getLong("treeId");
			resInfo.path = obj.getString("path") + "|" + page.getFileName();
			resInfo.type = "page";
			pageRes.add(resInfo);
			
			for(Region region : page.getRegions()){
				ResourceInfo regionInfo = new ResourceInfo();
				regionInfo.lastModified = new Date(file.lastModified());
				regionInfo.name = region.getName();
				regionInfo.path = resInfo.path + "|" + region.getName();
				regionInfo.type = "region";
				resInfo.childs.add(regionInfo);
			}
		}
		
		//数据库中的资源信息
		List<DataObject> resObjs = getAllResourcesByTreeParetId(obj.getLong("treeId"), actionContext);
		
		//更新数据库
		compareAndUpdateResources(obj.getLong("treeId"), pageRes, resObjs, actionContext);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void compareAndUpdateResources(long treeParentId, List<ResourceInfo> resources, List<DataObject> resObjs, ActionContext actionContext){
		//先更新或插入
		for(ResourceInfo info : resources){
			DataObject infoObj = null;
			for(DataObject resObj : resObjs){
				if(resObj.getString("path").equals(info.path)){
					if(resObj.getDate("lastModified").getTime() != info.lastModified.getTime()){
						//已修改，更新
						resObj.put("lastModified", info.lastModified);
						resObj.put("name", info.name);
						resObj.put("type", info.type);
						resObj.update(actionContext);
						
						infoObj = resObj;
						break;
					}
				}
			}
			
			if(infoObj == null){
				//没有，先插入目录树
				DataObject tree = new DataObject("xworker.libgdx.tools.dataobjects.ResourceTree");
				tree.put("name", info.name);
				tree.put("parentId", treeParentId);
				tree.put("type", info.type);
				tree.put("lastModified", info.lastModified);
				tree = tree.create(actionContext);
				
				//插入到资源中
				infoObj = new DataObject("xworker.libgdx.tools.dataobjects.Resource");
				infoObj.put("lastModified", info.lastModified);
				infoObj.put("name", info.name);
				infoObj.put("type", info.type);
				infoObj.put("path", info.path);
				infoObj.put("treeParentId", treeParentId);
				infoObj.put("treeId", tree.getLong("id"));
				infoObj = infoObj.create(actionContext);
				info.treeId = tree.getLong("id");				
			}
			
			List<DataObject> childObjs = (List<DataObject>) infoObj.get("childs");
			if(childObjs == null){
				childObjs = new ArrayList<DataObject>();
			}
			
			//更新子节点
			compareAndUpdateResources(infoObj.getLong("treeId"), info.childs, childObjs, actionContext);
		}
		
		//删除没有的节点
		for(DataObject resObj : resObjs){
			boolean have = false;
			for(ResourceInfo info : resources){
				if(resObj.getLong("treeId") == info.treeId){
					have = true;
					break;
				}
			}
			
			if(!have){
				deleteResourceRecursion(resObj, actionContext);
			}
		}
	}
	
	public static List<DataObject> getAllResourcesByTreeParetId(long treeParentId, ActionContext actionContext){
		List<DataObject> resObjs = DataObjectUtil.query("xworker.libgdx.tools.dataobjects.Resource",
				UtilMap.toMap("treeParentId", treeParentId), actionContext);
		
		for(DataObject obj : resObjs){
			List<DataObject> childObjs = getAllResourcesByTreeParetId(obj.getLong("treeId"), actionContext);
			obj.put("childs", childObjs);			
		}
		
		return resObjs;
	}
	
	@SuppressWarnings("unchecked")
	public static void deleteResourceRecursion(DataObject resource, ActionContext actionContext){
		List<DataObject> childs = (List<DataObject>) resource.get("childs");
		if(childs != null){
			for(DataObject res : childs){
				deleteResourceRecursion(res, actionContext);
			}
		}
		
		//删除资源
		resource.delete(actionContext);
		
		//同时删除目录树		
		DataObjectUtil.delete("xworker.libgdx.tools.dataobjects.ResourceTree", 
				UtilMap.toMap("id", resource.getLong("treeId")), actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getLoadParameter(Thing self, Class<T> t, ActionContext actionContext){
		Thing child = self.getThing("AssetLoaderParameters@0");
		if(child != null && child.getChilds().size() > 0){
			Object obj = child.doAction("create", actionContext);
			if(t.isInstance(obj)){
				return (T) obj;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T loadResource(Thing self, Class<T> t, ActionContext actionContext){
		DataObject resource = (DataObject) actionContext.get("resource");
		AssetManager assetManager = GameResourceManager.getAssetManager(actionContext);
		String path = resource.getString("path");
		FileHandle fileHandle = Gdx.files.absolute(path);
		
		AssetLoaderParameters<T> param = getLoadParameter(self, AssetLoaderParameters.class, actionContext);
		
		AssetDescriptor<T> desc = null;
		if(param != null){
			desc = new AssetDescriptor<T> (fileHandle, t, param);
		}else{
			desc = new AssetDescriptor<T> (fileHandle, t);
		}
		T texture = null;
		if(assetManager.isLoaded(path)){
			texture = assetManager.get(desc);
		}else{
			assetManager.load(desc);
			assetManager.finishLoading();
			texture = assetManager.get(desc);
		}
		
		return texture;
	}
}
