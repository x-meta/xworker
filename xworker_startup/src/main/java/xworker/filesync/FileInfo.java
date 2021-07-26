package xworker.filesync;

public class FileInfo {
	String path;
	String checkString;
	
	public FileInfo(String path, String checkString){
		this.path = path.replace('\\','/');
		this.checkString = checkString;
	}
	
	/**
	 * 和目标对比是否checkString已经变化了。
	 * 
	 * @param newInfo
	 * @return
	 */
	public boolean isChanged(FileInfo newInfo){
		return !checkString.equals(newInfo.checkString);
	}
}
