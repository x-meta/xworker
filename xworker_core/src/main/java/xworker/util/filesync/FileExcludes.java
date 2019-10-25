package xworker.util.filesync;

import java.util.ArrayList;
import java.util.List;

public class FileExcludes {
	List<String> excludeList = new ArrayList<String>();
	
	public FileExcludes(String excludes){
		if(excludes != null){
			for(String ext : excludes.split("[,]")){
				ext = ext.trim();
				if(!"".equals(ext)){
					excludeList.add(ext);
				}
			}
		}
	}
	
	public boolean exclude(String path){
		for(String ext : excludeList){
			if(path.startsWith(ext)){
				return true;
			}
		}
		
		return false;
	}
}
