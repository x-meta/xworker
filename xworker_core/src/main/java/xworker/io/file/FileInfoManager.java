package xworker.io.file;

import java.io.File;
import java.io.IOException;

import xworker.util.TimeoutMap;

public class FileInfoManager {
	static TimeoutMap<String, FileInfo> map = new TimeoutMap<String, FileInfo>(30 * 1000);
	
	public static synchronized FileInfo getFileInfo(File file) throws IOException {
		String path = file.getCanonicalPath();
		
		FileInfo fileInfo = map.get(path);
		if(fileInfo == null) {
			fileInfo = new FileInfo(file);
			map.put(path, fileInfo);
		}
		
		return fileInfo;
	}
}
