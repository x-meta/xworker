/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.ant;

import java.util.List;

import org.xmeta.Thing;

public class AntUtils {
	
	/**
	 * 创建删除文件或目录的任务。
	 * 
	 * @param filePath
	 * @param isDir
	 * @return
	 */
	public static Thing createDeleteFileTask(String filePath, boolean isDir){
		Thing thing = new Thing("xworker.ant.file.delete");
		if(isDir){
			thing.put("dir", filePath);
		}else{
			thing.put("file", filePath);
		}
		
		return thing;
	}
	
	/**
	 * 创建mkdir的任务。
	 * 
	 * @param filePath
	 * @return
	 */
	public static Thing createMkdirTask(String filePath){
		Thing thing = new Thing("xworker.ant.file.mkdir");
		thing.put("dir", filePath);
				
		return thing;
	}
	
	/**
	 * 创建拷贝文件的任务。
	 * 
	 * @param targetPath
	 * @param sourcePath
	 * @param excludes
	 * @param isDir
	 * @return
	 */
	public static Thing createCopyTask(String targetPath, String sourcePath, String excludes, boolean isDir){
		Thing thing = new Thing("xworker.ant.file.copy");
		Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
		fileSet.put("erroronmissingdir", "false");
		if(isDir){
			thing.put("todir", targetPath);
			fileSet.put("dir", sourcePath);
		}else{
			thing.put("tofile", targetPath);
			fileSet.put("file", sourcePath);
		}
		if(excludes != null && !"".equals(excludes)){
			for(String exc : excludes.split("[,]")){
				Thing ext = new Thing("xworker.ant.types.fileset/@exclude");
				ext.put("name", exc);
				fileSet.addChild(ext);
			}
		}
		
		thing.addChild(fileSet);
		return thing;
	}
	
	/**
	 * 拷贝task事物子事物定义的FileSet到指定目录下。
	 * 
	 * @param self
	 * @param tasks
	 * @param thingName
	 * @param targetDir
	 */
	public static void createCopyTask(Thing self, List<Thing> tasks, String thingName, String targetDir){
		Thing taskThing = self.getThing(thingName + "@0");
		if(taskThing != null && taskThing.getChilds().size() > 0){
			Thing copy = new Thing("xworker.ant.file.copy");
			copy.put("todir", targetDir);
			for(Thing child : taskThing.getChilds()){
				copy.addChild(child, false);
			}
			
			tasks.add(copy);
		}
	}
	
	/**
	 * 创建一个打包 jar的任务。
	 * 
	 * @param sourceDirPath
	 * @param jarFilePath
	 * @return
	 */
	public static Thing createJarTask(String sourceDirPath, String jarFilePath){
		Thing thing = new Thing("xworker.ant.archive.jar");
		thing.put("destfile", jarFilePath);
		thing.put("filesetmanifest", "merge");
		
		Thing fileSet = new Thing("xworker.ant.types.fileset");
		fileSet.put("dir", sourceDirPath);
		fileSet.put("erroronmissingdir", "false");
		thing.addChild(fileSet);
		
		return thing;
	}
	
	/**
	 * 创建解压的任务。
	 * 
	 * @param zipFile
	 * @param destDir
	 * @return
	 */
	public static Thing createUnZip(String zipFile, String destDir){
		Thing thing = new Thing("xworker.ant.archive.unzip");
		thing.put("src", zipFile);
		thing.put("dest", destDir);
		
		return thing;
	}
	
	/**
	 * 创建具有filter的单个文件拷贝。
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param tokens
	 * @param values
	 * @return
	 */
	public static Thing createFileFilterCopy(String srcFile, String destFile, String[] tokens, String[] values){
		Thing copy = new Thing("xworker.ant.file.copy");
		copy.put("tofile", destFile);
		copy.put("file", srcFile);
		Thing filterset = new Thing("xworker.ant.types.filterset");
		for(int i=0; i<tokens.length; i++){
			Thing filter = new Thing("xworker.ant.types.filterset/@filter");
			filter.put("token", tokens[i]);
			filter.put("value", values[i]);
			filterset.addChild(filter);
		}
		copy.addChild(filterset);
		return copy;
	}
}