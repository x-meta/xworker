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
package xworker.ant.xworker;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.util.ThingClassLoader;

import xworker.ant.AntUtils;

public class ExportApplicationActions {
	public static void toString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		String distPath = self.getString("distPath");
		if(distPath == null || "".equals(distPath)){
			throw new XMetaException("XWorker ant: please set distPath, " + self);
			
		}
				
		List<Thing> tasks = new ArrayList<Thing>();
		
		//删除war临时目录
		if(self.getBoolean("clean")){
			tasks.add(AntUtils.createDeleteFileTask(distPath, true));
		}
		
		//创建war临时目录
		tasks.add(AntUtils.createMkdirTask(distPath));
		
		String singleJarFilePath = self.getString("singleJarFilePath");
		if(singleJarFilePath != null && !"".equals(singleJarFilePath) && self.getBoolean("singleJar")){
			//打包成一个单独的jar
			exportToSingleJar(self, tasks, distPath, singleJarFilePath, actionContext);
		}else{
			//发布到一个目录下
			exportToDir(self, tasks, distPath, actionContext);
		}
		
		//生成xml		
		for(Thing task : tasks){
			task.doAction("toString", actionContext);			
		}	
	}
	
	private static String getPath(XWorkerFile file, String type, String distPath){
		return file.getPath(type, distPath);
	}
	
	private static void exportToDir(Thing self, List<Thing> tasks, String distPath, ActionContext actionContext){
		//拷贝自动引入的编译类
		if(self.getBoolean("autoImportClasses")){
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof URLClassLoader){
				String paths = ThingClassLoader.getClassPathFormClassLoader((URLClassLoader) loader, "", new HashMap<String, String>());
				if(paths != null){
					Thing thing = new Thing("xworker.ant.file.copy");
					thing.put("todir", distPath + "/bin/");
					for(String path : paths.split("[" + File.pathSeparator + "]")){
						File file = new File(path);
						if(file.exists() && file.isDirectory()){
							Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
							fileSet.put("erroronmissingdir", "false");							
							fileSet.put("dir", file.getAbsolutePath());
							thing.addChild(fileSet);
						}
					}
					
					if(thing.getChilds().size() > 0){
						tasks.add(thing);
					}
				}
			}
		}
		
		//是否引用动作的编译后的类
		if(self.getBoolean("importActionClasses")){
			Thing thing = new Thing("xworker.ant.file.copy");
			thing.put("todir", distPath + "/bin/");
			Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
			fileSet.put("erroronmissingdir", "false");							
			fileSet.put("dir", World.getInstance().getPath() + "/work/actionClasses/");
			thing.addChild(fileSet);
			
			tasks.add(thing);
		}
		
		//拷贝自定义的文件
		List<XWorkerFile> files = XWorkerUtils.getFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, null, distPath);

			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝自定义的类库文件
		files = XWorkerUtils.getLibFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, "lib", distPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝自定义的编译的类
		files = XWorkerUtils.getClassesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, "bin", distPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝事物管理器的类库
		files = XWorkerUtils.getThingManagerLibs(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, "lib", distPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝事物
		files = XWorkerUtils.getThingManagerThings(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, "bin", distPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝clases
		files = XWorkerUtils.getThingManagerClasses(self);
		for(XWorkerFile file : files){
			String targetPath = getPath(file, "bin", distPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝额外定义的classes文件
		createCopyTask(self, tasks, "Classes", distPath + "/bin/");
		//拷贝额外的lib文件
		createCopyTask(self, tasks, "Lib", distPath + "/lib/");

		//把bin目录打包成jar并删除bin目录
		String binJarName = self.getString("binJarName");
		if(binJarName == null || "".equals(binJarName)){
			binJarName = self.getMetadata().getName() + ".jar";
		}
		tasks.add(AntUtils.createJarTask(distPath + "/bin/", distPath + "/lib/" + binJarName));
		tasks.add(AntUtils.createDeleteFileTask(distPath + "/bin/", true));
		
		//拷贝xer.bat
		tasks.add(AntUtils.createFileFilterCopy(World.getInstance().getPath() + "/deploy/xer.bat", 
				distPath + "/xer.bat", 
				new String[]{"classPath"}, new String[]{self.getString("mainClass")}));
		
		//拷贝xer.ini
		String actionName = self.getString("actionName");
		if(actionName == null || "".equals(actionName)){
			actionName = "run";
		}
		tasks.add(AntUtils.createFileFilterCopy(World.getInstance().getPath() + "/deploy/xer.ini", 
				distPath + "/xer.ini", 
				new String[]{"thingPath", "actionName"}, new String[]{self.getString("mainThingPath"), actionName}));
		
		//其他应用
		Thing apps = self.getThing("Apps@0");
		if(apps != null){
			for(Thing app : apps.getChilds()){
				String appName = app.getString("name");				
				String thingPath = app.getString("thingPath");
				actionName = app.getString("thingAction");
				if(actionName == null || "".equals(actionName)){
					actionName = "run";
				}
				
				tasks.add(AntUtils.createFileFilterCopy(World.getInstance().getPath() + "/deploy/run.bat", 
						distPath + "/" + appName + ".bat", 
						new String[]{"thingPath", "actionName"}, new String[]{thingPath, actionName}));
			}
		}
		
		//自定义的其他任务
		Thing otherTask = self.getThing("OtherTasks@0");
		if(otherTask != null){
			for(Thing child : otherTask.getChilds()){
				tasks.add(child);
			}
		}
	}
	
	private static void createCopyTask(Thing self, List<Thing> tasks, String thingName, String targetDir){
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
	
	private static void exportToSingleJar(Thing self, List<Thing> tasks, String distPath, String jarPath, ActionContext actionContext){
		//拷贝自动引入的编译类
		if(self.getBoolean("autoImportClasses")){
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof URLClassLoader){
				String paths = ThingClassLoader.getClassPathFormClassLoader((URLClassLoader) loader, "", new HashMap<String, String>());
				if(paths != null){
					Thing thing = new Thing("xworker.ant.file.copy");
					thing.put("todir", distPath);
					for(String path : paths.split("[" + File.pathSeparator + "]")){
						File file = new File(path);
						if(file.exists() && file.isDirectory()){
							Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
							fileSet.put("erroronmissingdir", "false");							
							fileSet.put("dir", file.getAbsolutePath());
							thing.addChild(fileSet);
						}
					}
					
					if(thing.getChilds().size() > 0){
						tasks.add(thing);
					}
				}
			}
		}
		
		//是否引用动作的编译后的类
		if(self.getBoolean("importActionClasses")){
			Thing thing = new Thing("xworker.ant.file.copy");
			thing.put("todir", distPath );
			Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
			fileSet.put("erroronmissingdir", "false");							
			fileSet.put("dir", World.getInstance().getPath() + "/work/actionClasses/");
			thing.addChild(fileSet);
			
			tasks.add(thing);
		}
		
		//拷贝自定义的文件
		List<XWorkerFile> files = XWorkerUtils.getFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = file.targetPath;
			if(targetPath == null || "".equals(targetPath)){
				targetPath = distPath;
			}else{
				targetPath = distPath + "/" + targetPath;
			}
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝自定义的类库文件
		files = XWorkerUtils.getLibFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			unzipAllLib(tasks, file.file, distPath);
		}
		
		//拷贝自定义的编译的类
		files = XWorkerUtils.getClassesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = file.targetPath;
			if(targetPath == null || "".equals(targetPath)){
				targetPath = distPath;
			}else{
				targetPath = distPath + "/" + targetPath;
			}
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝事物管理器的类库
		files = XWorkerUtils.getThingManagerLibs(self);
		for(XWorkerFile file : files){
			unzipAllLib(tasks, file.file, distPath);
		}
		
		//拷贝事物
		files = XWorkerUtils.getThingManagerThings(self);
		for(XWorkerFile file : files){
			String targetPath = file.targetPath;
			if(targetPath == null || "".equals(targetPath)){
				targetPath = distPath;
			}else{
				targetPath = distPath + "/" + targetPath;
			}
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝clases
		files = XWorkerUtils.getThingManagerClasses(self);
		for(XWorkerFile file : files){
			String targetPath = file.targetPath;
			if(targetPath == null || "".equals(targetPath)){
				targetPath = distPath;
			}else{
				targetPath = distPath + "/" + targetPath;
			}
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝额外定义的classes文件
		Thing taskThing = self.getThing("Classes@0");
		if(taskThing != null && taskThing.getChilds().size() > 0){
			Thing copy = new Thing("xworker.ant.file.copy");
			copy.put("todir", distPath);
			for(Thing child : taskThing.getChilds()){
				copy.addChild(child, false);
			}
			
			tasks.add(copy);
		}

		//拷贝MANIFEST.MF
		tasks.add(AntUtils.createFileFilterCopy(World.getInstance().getPath() + "/deploy/META-INF/MANIFEST.MF", 
				distPath + "/META-INF/MANIFEST.MF", 
				new String[]{"classPath"}, new String[]{self.getString("mainClass")}));
		
		//拷贝xer.ini
		String actionName = self.getString("actionName");
		if(actionName == null || "".equals(actionName)){
			actionName = "run";
		}
		tasks.add(AntUtils.createFileFilterCopy(World.getInstance().getPath() + "/deploy/xer.ini", 
				distPath + "/xer.ini", 
				new String[]{"thingPath", "actionName"}, new String[]{self.getString("mainThingPath"), actionName}));
		
		//自定义的其他任务
		Thing otherTask = self.getThing("OtherTasks@0");
		if(otherTask != null){
			for(Thing child : otherTask.getChilds()){
				tasks.add(child);
			}
		}
		
		//删除META-INF下的*.RSA签名文件，否则会签名出错
		Thing deleteThing = new Thing("xworker.ant.file.delete");
		Thing deleteFileSet = new Thing("xworker.ant.types.fileset");
		deleteFileSet.put("dir", distPath + "/META-INF/");
		deleteFileSet.put("includes","**/*.RSA");
		deleteThing.addChild(deleteFileSet);
		tasks.add(deleteThing);
		
		
		//生成 WAR文件
		if(jarPath != null && !"".equals(jarPath)){
			tasks.add(AntUtils.createJarTask(distPath, jarPath));
		}
	}
	
	private static void unzipAllLib(List<Thing> tasks, File file, String distPath){
		if(isZip(file)){
			tasks.add(AntUtils.createUnZip(file.getAbsolutePath(), distPath));
		}else if(file.isFile()){
			tasks.add(AntUtils.createCopyTask(distPath, file.getAbsolutePath(), null, false));
		}else{
			for(File child : file.listFiles()){
				unzipAllLib(tasks, child, distPath);
			}			
		}			
	}
	
	private static boolean isZip(File file){
		String fileName = file.getName().toLowerCase();
		if(fileName.endsWith(".zip") || fileName.endsWith(".jar")){
			return true;
		}else{
			return false;
		}
	}
}