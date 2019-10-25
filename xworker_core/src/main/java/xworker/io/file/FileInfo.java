package xworker.io.file;

import java.io.File;

/**
 * 为RandomAccessFile或其它准备的工具。
 * 
 * @author zyx
 *
 */
public class FileInfo {
	long length = 0;
	File file;
	
	public FileInfo(File file) {
		this.file = file;
		if(file.exists() == false && file.getParentFile().exists() == false) {
			file.getParentFile().mkdirs();
		}else {
			length = file.length();
		}
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * 返回下一个偏移量。
	 * 
	 * @param writeLength
	 * @return
	 */
	public synchronized long getNextOffset(long writeLength) {
		long offset = length;
		length += writeLength;
		
		return offset;
	}
}
