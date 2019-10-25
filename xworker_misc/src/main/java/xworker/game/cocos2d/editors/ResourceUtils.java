package xworker.game.cocos2d.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.World;

public class ResourceUtils {
	/**
	 * 根据资源分类获取所有的资源列表。
	 * 
	 * @param fileRoot
	 * @param category
	 * @return
	 */
	public static List<ResourceFileInfo> getAllResourceFiles(String fileRoot, String category){
		List<ResourceFileInfo> resList = new ArrayList<ResourceFileInfo>();
		List<Thing> fileTypes = getFileTypes(category);
		File root = new File(fileRoot);
		if(root.isDirectory()){
			for(File child : root.listFiles()){
				initFiles(child, null, fileTypes, resList);
			}
		}
		
		return resList;
	}
	
	private static void initFiles(File file, String path, List<Thing> fileTypes, List<ResourceFileInfo> fileList){
		String newPath = null;
		if(path == null){
			newPath = file.getName();
		}else{
			newPath = path + "/" + file.getName();
		}
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				initFiles(child, newPath, fileTypes, fileList);
			}
		}else{
			String fileName = file.getName();
			for(Thing fileType : fileTypes){
				String fileExt = fileType.getString("name");
				if(fileName.endsWith("." + fileExt)){
					ResourceFileInfo info = new ResourceFileInfo(newPath, file, fileType);
					fileList.add(info);
					break;
				}
			}
		}
	}
	
	/**
	 * 根据资源分类获取对应的文件类型。
	 * 
	 * @param category
	 * @return
	 */
	public static List<Thing> getFileTypes(String category){
		List<Thing> types = new ArrayList<Thing>();
		Thing resourceTypes = World.getInstance().getThing("xworker.cocos2d.resource.ResourceTypes/@ResourceTypes");
		if(resourceTypes != null){
			for(Thing child : resourceTypes.getChilds()){
				if(category.equals(child.getString("type"))){
					types.add(child);
				}
			}
		}
		
		return types;
	}
}
