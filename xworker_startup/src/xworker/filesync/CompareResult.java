package xworker.filesync;

import java.util.ArrayList;
import java.util.List;

public class CompareResult {
	/** 已删除的文件列表 */
	public List<FileInfo> removeList = new ArrayList<FileInfo>();
	
	/** 已更新的文件列表 */
	public List<FileInfo> changedList = new ArrayList<FileInfo>();
	
	/** 新文件列表 */
	public List<FileInfo> newList = new ArrayList<FileInfo>();
	
	public boolean isChanged(){
		return removeList.size() > 0 || changedList.size() > 0 || newList.size() > 0;
	}
}
