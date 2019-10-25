package xworker.filesync;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileList {
	List<FileInfo> fileList = new ArrayList<FileInfo>();
	Map<String, FileInfo> fileMap = new HashMap<String, FileInfo>();
	
	/**
	 * 从根文件夹中构建一个FileList。
	 * 
	 * @param rootDir
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public FileList(File rootDir, FileFilter filter) throws IOException, NoSuchAlgorithmException{		
		if(rootDir.isFile()){
			throw new IOException("rootDir is a file, need a directory");
		}else{
			init(rootDir, rootDir.getAbsolutePath(), filter);
		}
	}
		
	/**
	 * 从文本中构造一个FileList
	 * 
	 * @param listInfo
	 */
	public FileList(String listInfo){
		for(String line : listInfo.split("[\n]")){
			line = line.trim();
			if("".equals(line)){
				continue;
			}
			
			String ls[] = line.split("[|]");
			if(ls.length == 2){
				FileInfo info = new FileInfo(ls[0].trim(), ls[1].trim());
				addFileList(info);				
			}
		}
	}
	
	public void init(File file, String rootPath, FileFilter filter) throws IOException, NoSuchAlgorithmException{
		if(filter != null && filter.isExcluded(file)){
			return;
		}
		
		if(file.isFile()){
			String path = file.getAbsolutePath();
			path = path.substring(rootPath.length(), path.length());
			String checkString = getCheckString(file);
			
			FileInfo info = new FileInfo(path, checkString);
			addFileList(info);
			
			System.out.println("add file info: " + path);
		}else if(file.isDirectory()){
			for(File child : file.listFiles()){
				init(child, rootPath, filter);
			}
		}
	}
	
	public void addFileList(FileInfo fileInfo){
		if(fileMap.get(fileInfo.path) == null){
			fileList.add(fileInfo);
			fileMap.put(fileInfo.path, fileInfo);
		}
	}
	
	/**
	 * 和新的目标FileList做对比，目标FileList对应升级的新版。
	 * 
	 * @param newFileList
	 * @return
	 */
	public CompareResult compare(FileList newFileList){
		CompareResult result = new CompareResult();
		
		//找出已删除和更新的
		for(FileInfo info : fileList){
			FileInfo newInfo = newFileList.fileMap.get(info.path);
			if(newInfo == null){
				result.removeList.add(info);
			}else if(info.isChanged(newInfo)){
				result.changedList.add(newInfo);
			}
		}
		
		//找出新
		for(FileInfo newInfo : newFileList.fileList){
			if(fileMap.get(newInfo.path) == null){
				result.newList.add(newInfo);
			}
		}
		
		/*
		String date = String.valueOf(System.currentTimeMillis());
		try{
			FileUtils.write(new File("./xworker/work/update/files/" + date + "_old.txt"), this.toString());
			FileUtils.write(new File("./xworker/work/update/files/" + date + "_new.txt"), newFileList.toString());
		}catch(Exception e){
			e.printStackTrace();
		}*/
		return result;
	}
	
	/**
	 * 获取文件的校验值。
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getCheckString(File file) throws IOException, NoSuchAlgorithmException{
		//使用SHA-1生成校验码
		MessageDigest digest = MessageDigest.getInstance("SHA-1");		
		digest.update(FileSync.readFileToByteArray(file));
		byte messageDigest[] = digest.digest();		
		return file.length() + "_" + toHexString(messageDigest);
				
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(FileInfo info : fileList){
			buf.append(info.path + "|" + info.checkString);
			buf.append("\n");
		}
		
		return buf.toString();
	}
	

	 public static String toHexString(byte[] bytes) {
		char[] buf = new char[bytes.length * 2];
		int radix = 1 << 4;
		int mask = radix - 1;
		for (int i = 0; i < bytes.length; i++) {
			buf[2 * i] = hexDigit[(bytes[i] >>> 4) & mask];
			buf[2 * i + 1] = hexDigit[bytes[i] & mask];
		}

		return new String(buf);
	}
	 
    /** A table of hex digits */
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
}
