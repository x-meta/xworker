package xworker.filesync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFilter {
	List<File> filterFiles = new ArrayList<File>();
	
	public FileFilter(File rootDir, String filters){
		for(String f : filters.split("[\n]")){
			f = f.trim();
			if("".equals(f)){
				continue;
			}
			
			filterFiles.add(new File(rootDir, f));
		}
	}
	
	public boolean isExcluded(File file){
		for(File f : filterFiles){
			if(file.getAbsolutePath().startsWith(f.getAbsolutePath())){
				return true;
			}
		}
		
		return false;
	}
}
