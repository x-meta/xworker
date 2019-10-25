package xworker.manong;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

public class MaNongProjectZipper {
	private static Logger logger = LoggerFactory.getLogger(MaNongProjectZipper.class);
	
	/**
	 * 打包码农项目到ZIP压缩文件中。
	 * 
	 * @param project
	 * @return
	 */
	public static File encodeToZip(Thing project) {
		ThingManager thingManager = project.getMetadata().getThingManager();
		String thingManagerName = thingManager.getName();
		//只有_share和xworker_的项目才允许压缩和上传
		if(!thingManagerName.startsWith("_share") && !thingManagerName.startsWith("xworker_")){
			throw new MaNongException(MaNongI18n.getMessage("thingManagerError"));
		}
		
		//只允许上传文件管理器下的项目
		if(!(thingManager instanceof FileThingManager)){
			throw new MaNongException(MaNongI18n.getMessage("fileThingManagerError"));
		}
	
		String sourceFilePath = getProjectFilePath(project);
		String targetFilePath = getZipFilePath(project);
		
		File zipFile = new File(targetFilePath);
		if(!zipFile.exists()){
			zipFile.getParentFile().mkdirs();
		}
		
		ZipOutputStream zout = null;
		try{
			zout = new ZipOutputStream(new FileOutputStream(targetFilePath));
			File sourceFile = new File(sourceFilePath);
			File thingManagerFile = new File(getThingManagerFilePath(project));
			//根目录从事物管理器开始算起
			zip(thingManagerFile.getParentFile().getParentFile().getAbsolutePath(), sourceFile, zout);
		}catch(Exception e){
			logger.error("压缩码农项目的文件失败！", e);
		}finally{
			if(zout != null){
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
		}
		
		return zipFile;
	}
	
	/**
	 * 压缩文件。
	 * 
	 * @param file
	 * @param zout
	 * @throws IOException 
	 */
	public static void zip(String rootPath, File file, ZipOutputStream zout) throws IOException{
		if(file.isDirectory()){
			for(File childFile : file.listFiles()){
				zip(rootPath, childFile, zout);
			}
		}else{
			String filePath = file.getAbsolutePath();
			filePath = filePath.substring(rootPath.length(), filePath.length());
			
			zout.putNextEntry(new ZipEntry(filePath));
			byte[] bytes = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);
			fin.read(bytes);
			fin.close();
			zout.write(bytes);
			zout.closeEntry();
		}
	}
	
	/**
	 * 获取项目压缩后的文件目录。
	 * 
	 * @param project
	 * @return
	 */
	public static String getZipFilePath(Thing project){
		ThingManager thingManager = project.getMetadata().getThingManager();
		String thingManagerName = thingManager.getName();
	
		String projectPath = project.getMetadata().getPath();
		String targetFile = World.getInstance().getPath() + "/work/manong/" + thingManagerName + "/" + projectPath.replace('.', '/') + ".zip";
		return targetFile;
	}
	
	/**
	 * 获取项目的原始目录。
	 * 
	 * @param project
	 * @return
	 */
	public static String getProjectFilePath(Thing project){
		ThingManager thingManager = project.getMetadata().getThingManager();
		FileThingManager fileThingManager = (FileThingManager) thingManager;
	
		String projectPath = project.getMetadata().getPath();
		int index = projectPath.lastIndexOf(".");
		if(index != -1){
			projectPath = projectPath.substring(0, index);
		}
		String sourceFile = fileThingManager.getFilePath() + "/" + projectPath.replace('.', '/');
		return sourceFile;
	}
	
	/**
	 * 返回事物管理器的文件路径。
	 * 
	 * @param project
	 * @return
	 */
	public static String getThingManagerFilePath(Thing project){
		ThingManager thingManager = project.getMetadata().getThingManager();
		FileThingManager fileThingManager = (FileThingManager) thingManager;
	
		String projectPath = project.getMetadata().getPath();
		int index = projectPath.lastIndexOf(".");
		if(index != -1){
			projectPath = projectPath.substring(0, index);
		}
		String sourceFile = fileThingManager.getFilePath();
		return sourceFile;
	}
}
