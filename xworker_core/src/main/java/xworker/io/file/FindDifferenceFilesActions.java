package xworker.io.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FindDifferenceFilesActions {
	public static List<File[]> run(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		File file1 = (File) self.doAction("getFile1", actionContext);
		File file2 = (File) self.doAction("getFile2", actionContext);
		
		List<File[]> list = new ArrayList<File[]>();
		compare(file1, file2, list);
		
		return list;
	}
	
	public static void compare(File file1, File file2, List<File[]> list) throws IOException{
		if(file1 == null && file2 == null){
			return;
		}
		
		if(file1 != null && file2 == null){
			addDiff(file1, file2, list);
			
			if(file1.isDirectory()){
				addDirs(file1, true, list);
			}
		}else if(file1 == null && file2 != null){
			addDiff(file1, file2, list);
			
			if(file2.isDirectory()){
				addDirs(file2, false, list);
			}
		}else{
			if(file1.isFile() && file2.isFile()){
				compareFileFile(file1, file2, list);
			}else if(file1.isFile() && file2.isDirectory()){
				compareFileDir(file1, file2, list);
			}else if(file1.isDirectory() && file2.isFile()){
				compareDirFile(file1, file2, list);
			}else{
				compareDirDir(file1, file2, list);
			}
		}
	}
	
	public static void compareDirDir(File dir1, File dir2, List<File[]> list) throws IOException{
		for(File child1 : dir1.listFiles()){
			File child2 = new File(dir2, child1.getName());
			if(!child2.exists()){
				child2 = null;
			}
			compare(child1, child2, list);
		}
		
		for(File child2 : dir2.listFiles()){
			File child1 = new File(dir1, child2.getName());
			if(!child1.exists()){
				compare(null, child2, list);
			}			
		}
	}
	
	public static void compareFileFile(File file1, File file2, List<File[]> list) throws IOException{
		if(!FileUtils.contentEquals(file1, file2)){
			addDiff(file1, file2, list);
		}		
	}
	
	public static void compareDirFile(File file, File dir, List<File[]> list){
		//二者本来就不同，加入不同
		addDiff(file, dir, list);
		
		addDirs(dir, true, list);
	} 
	

	public static void compareFileDir(File file, File dir, List<File[]> list){
		//二者本来就不同，加入不同
		addDiff(file, dir, list);
		
		addDirs(dir, false, list);
	}
	
	public static void addDirs(File dir, boolean isFirst, List<File[]> list){
		for(File child : dir.listFiles()){
			if(isFirst){
				addDiff(child, null, list);
			}else{
				addDiff(null, child, list);
			}
			
			if(child.isDirectory()){
				addDirs(child, isFirst, list);
			}
		}
	}
	
	public static void addDiff(File file1, File file2, List<File[]> list){
		File[] diff = new File[2];
		diff[0] = file1;
		diff[1] = file2;
		
		list.add(diff);
	}
}
