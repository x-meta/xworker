package xworker.game.cocos2d.editors;

import java.io.File;

import org.xmeta.Thing;

public class ResourceFileInfo {
	String path;
	File file;
	Thing fileType;
	
	public ResourceFileInfo(String path, File file, Thing fileType) {
		this.path = path;
		this.file = file;
		this.fileType = fileType;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getName(){
		String fileName = file.getName();
		int index = fileName.indexOf(".");
		return fileName.substring(0, index);
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public Thing getFileType() {
		return fileType;
	}

	public void setFileType(Thing fileType) {
		this.fileType = fileType;
	}
}
