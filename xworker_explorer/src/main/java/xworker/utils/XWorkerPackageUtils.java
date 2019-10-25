package xworker.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XWorkerPackageUtils {
	/**
	 * 当XWorker打包后，由于事物资源是打包在jar中的，而我们需要把它当作项目，因此需要创建它们。
	 * @throws IOException 
	 */
	public static void createXWorkerProject() throws IOException{
		File libs = new File("./target/xworker/lib/");
		
		for(File lib : libs.listFiles()){
			if(lib.isFile()){
				String name = lib.getName();				
				if(name.startsWith("xworker_") && !name.startsWith("xworker_explorer")){
					createXWorkerProject(lib);
				}
			}
		}
	}
	
	public static void createXWorkerProject(File file) throws IOException{
		String fileName = file.getName();
		int index = fileName.indexOf("-");
		if(index  != -1) {
			String projectName = fileName.substring(0, index);
			
			//创建项目
			File project = new File("./target/xworker/projects/" + projectName);
			project.mkdirs();
			
			//创建配置
			File config = new File(project, "config.properties");
			FileOutputStream fout = new FileOutputStream(config);
			fout.write(("link=./lib/" + fileName).getBytes());
			fout.close();
			
			System.out.println(projectName + " created");
		}
	}
	
	public static void main(String args[]){
		try{
			createXWorkerProject();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
