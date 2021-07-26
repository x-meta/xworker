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

public class ExportWarActions {
	/**
	 * ExportWar任务的转化为ant的方法。
	 * 
	 */
	public static void toString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String warDirPath = self.getString("warDir");		
		String warFilePath = self.getString("warFile");
		if(warDirPath == null || "".equals(warDirPath)){
			throw new XMetaException("XWorker ant: please set warDir, " + self);
			
		}
		if(warDirPath.endsWith("/") || warDirPath.endsWith("\\")){			
		}else{
			warDirPath = warDirPath + "/";
		}
				
		List<Thing> tasks = new ArrayList<Thing>();
		
		//删除war临时目录
		if(self.getBoolean("clean")){
			tasks.add(AntUtils.createDeleteFileTask(warDirPath, true));
		}
		
		//创建war临时目录
		tasks.add(AntUtils.createMkdirTask(warDirPath));
		
		//拷贝自动引入的编译类
		if(self.getBoolean("autoImportClasses")){
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader instanceof URLClassLoader){
				String paths = ThingClassLoader.getClassPathFormClassLoader((URLClassLoader) loader, "", new HashMap<String, String>());
				if(paths != null){
					Thing thing = new Thing("xworker.ant.file.copy");
					thing.put("todir", warDirPath + "/WEB-INF/classes/");
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
			thing.put("todir", warDirPath + "/WEB-INF/classes/");
			Thing fileSet = new Thing("xworker.ant.file.copy/@fileset");
			fileSet.put("erroronmissingdir", "false");							
			fileSet.put("dir", World.getInstance().getPath() + "/work/actionClasses/");
			thing.addChild(fileSet);
			
			tasks.add(thing);
		}
		
		//拷贝自定义的文件
		List<XWorkerFile> files = XWorkerUtils.getFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = file.getPath("", warDirPath);
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝自定义的类库文件
		files = XWorkerUtils.getLibFilesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = file.getPath("WEB-INF/lib/", warDirPath);	
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝自定义的编译的类
		files = XWorkerUtils.getClassesFormPathSetting(self);
		for(XWorkerFile file : files){
			String targetPath = file.getPath("WEB-INF/classes/", warDirPath);
			
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		if("webinfoData".equals(self.getString("thingManagerTarget"))){
			//把事物管理器拷贝到WEB-INFO/data目录下
			files = XWorkerUtils.getThingManagers(self);
			for(XWorkerFile file : files){
				String targetPath = file.getPath("WEB-INF/data/", warDirPath);				
				Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
				tasks.add(copyTask);
			}			
		}else{			
			//拷贝事物管理器的类库
			files = XWorkerUtils.getThingManagerLibs(self);
			for(XWorkerFile file : files){
				String targetPath = file.getPath("WEB-INF/lib/", warDirPath);					
				Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
				tasks.add(copyTask);
			}
			
			//拷贝事物
			files = XWorkerUtils.getThingManagerThings(self);
			for(XWorkerFile file : files){
				String targetPath = file.getPath("WEB-INF/classes/", warDirPath);	
				Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
				tasks.add(copyTask);
			}
		}
		
		//拷贝clases
		files = XWorkerUtils.getThingManagerClasses(self);
		for(XWorkerFile file : files){
			String targetPath = file.getPath("WEB-INF/classes/", warDirPath);	
			Thing copyTask = AntUtils.createCopyTask(targetPath, file.file.getAbsolutePath(), file.excludes, file.file.isDirectory());
			tasks.add(copyTask);
		}
		
		//拷贝web.xml
		String webxml = self.getString("web_xml");
		if(webxml != null && !"".equals(webxml)){
			tasks.add(AntUtils.createCopyTask(warDirPath + "WEB-INF/web.xml", webxml, null, false));
		}else{
			tasks.add(AntUtils.createCopyTask(warDirPath + "WEB-INF/web.xml", World.getInstance().getPath() + "/webroot/WEB-INF/web.xml", null, false));
		}
		
		//indexUrl
		String indexUrl = self.getStringBlankAsNull("indexUrl");
		if(indexUrl != null){
			//创建一个property
			Thing property = new Thing("xworker.ant.Tasks/@filter");
			property.put("token", "__index_url_redirect_to__");
			property.put("value", indexUrl);
			
			Thing copy = new Thing("xworker.ant.file.copy");
			copy.put("file", World.getInstance().getPath() +"/deploy/webroot/index.html");
			copy.put("tofile", warDirPath + "index.html");
			copy.put("filtering", "true");
			
			tasks.add(property);
			tasks.add(copy);			
		}
		
		//jboss_context_root
		String jboss_context_root = self.getStringBlankAsNull("jboss_context_root");
		if(jboss_context_root != null){
			//创建一个property
			Thing property = new Thing("xworker.ant.Tasks/@filter");
			property.put("token", "__jboss_web_xml__");
			property.put("value", jboss_context_root);
			
			Thing copy = new Thing("xworker.ant.file.copy");
			copy.put("file", World.getInstance().getPath() +"/deploy/webroot/WEB-INF/jboss-web.xml");
			copy.put("filtering", "true");
			copy.put("tofile", warDirPath + "WEB-INF/jboss-web.xml");
			
			tasks.add(property);
			tasks.add(copy);			
		}
		
		//拷贝额外定义的根目录文件
		createCopyTask(self, tasks, "RootFiles", warDirPath);
		//拷贝额外的WEB-INFO目录文件
		createCopyTask(self, tasks, "WEBINF", warDirPath + "WEB-INF/");
		//拷贝额外定义的classes文件
		createCopyTask(self, tasks, "Classes", warDirPath + "WEB-INF/classes/");
		//拷贝额外的lib文件
		createCopyTask(self, tasks, "Lib", warDirPath + "WEB-INF/lib/");
		
		//自定义的其他任务
		Thing otherTask = self.getThing("OtherTasks@0");
		if(otherTask != null){
			for(Thing child : otherTask.getChilds()){
				tasks.add(child);
			}
		}
		
		//生成 WAR文件
		if(warFilePath != null && !"".equals(warFilePath)){
			tasks.add(AntUtils.createJarTask(warDirPath, warFilePath));
		}
		
		//生成xml		
		for(Thing task : tasks){
			task.doAction("toString", actionContext);			
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
}