package xworker.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.xmeta.World;

public class UtilFileIcon {
	//private static Logger logger  = LoggerFactory.getLogger(UtilFileIcon.class);
	static Map<String, String> icons = new HashMap<String, String>();
	static IFileIcon fileIcon = null;
	static {
		try {
			Class.forName("xworker.util.JavaFileIcon");
		}catch(Exception e) {			
		}
	}
	
	public static void setFileIcon(IFileIcon fileIcon) {
		UtilFileIcon.fileIcon = fileIcon;
	}
	
	public static String getFileIcon(String ext, boolean isBigIcon){
		String iconFileName = null;
		if(ext == null || "".equals(ext)){
			iconFileName = "icons/file/" + "folder" + (isBigIcon ? "_big": "") + ".gif";
		}else{
			iconFileName = "icons/file/" + ext.toLowerCase() + (isBigIcon ? "_big": "") + ".gif";
		}
		return iconFileName;
	}
	
	public static String getFileIcon(File file, boolean isBigIcon) throws IOException{
		try{
			String iconFileName = null;
			if(file.isFile()){
				String fileName = file.getName();
				String ext = null;
				int index = fileName.lastIndexOf(".");
				if(index != -1){
					ext = fileName.substring(index + 1, fileName.length());
				}else{
					ext = "_";
				}
				
				ext = ext.toLowerCase();
				iconFileName = "icons/file/" + ext + (isBigIcon ? "_big": "") + ".gif";
			}else if(file.isDirectory()){			
				iconFileName = "icons/file/" + "folder" + (isBigIcon ? "_big": "") + ".gif";
			}
			
			if(icons.get(iconFileName) != null){
				//图标已经存在
				return iconFileName;
			}
			
			String webRoot = World.getInstance().getWebFileRoot();
			if(webRoot == null){
				webRoot = World.getInstance().getPath() + "/webroot";
			}
			File iconFile = new File(webRoot + "/" + iconFileName);
			//System.out.println(iconFile.getAbsolutePath());
			if(iconFile.exists()){
				icons.put(iconFileName, iconFileName);
				//图标文件也已经存在
				return iconFileName;
			}else{
				iconFile.getParentFile().mkdirs();
			}
					
			if(fileIcon == null || !fileIcon.saveFileIcon(file, iconFile)){
				if(file.isFile()){				
					FileUtils.copyFile(new File(World.getInstance().getPath() + "/webroot/icons/page_white.gif"), iconFile);
				}else{
					FileUtils.copyFile(new File(World.getInstance().getPath() + "/webroot/icons/folder.gif"), iconFile);
				}
			}
			icons.put(iconFileName, iconFileName);
			return iconFileName;
			
		}catch(Exception e){
			//logger.error("get file icon error", e);
			return null;
		}		
	}
	
	public static void main(String args[]){
		try{			
			World world = World.getInstance();			
			world.init("xworker");
			
			System.out.println(getFileIcon(new File("e:/temp/111.1"), false));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
