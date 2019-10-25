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
import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.thingManagers.FileThingManager;

public class XWorkerUtils {
	/**
	 * 获取通过事物定义的路径。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<XWorkerFile> getLibFilesFormPathSetting(Thing thing){
		return getFilesFormPathSetting(thing, "lib");
	}
	
	public static List<XWorkerFile> getFilesFormPathSetting(Thing thing){
		return getFilesFormPathSetting(thing, "file");
	}
	
	public static List<XWorkerFile> getThingsFormPathSetting(Thing thing){
		return getFilesFormPathSetting(thing, "things");
	}
	
	public static List<XWorkerFile> getThingManagersFormPathSetting(Thing thing){
		return getFilesFormPathSetting(thing, "thingmanager");
	}
	
	public static List<XWorkerFile> getClassesFormPathSetting(Thing thing){
		return getFilesFormPathSetting(thing, "classes");
	}
	
	private static List<XWorkerFile> getFilesFormPathSetting(Thing thing, String type){
		Thing setThing = World.getInstance().getThing("xworker.ant.xworker.XWorkerPathSetting");
		List<XWorkerFile> xfList = new ArrayList<XWorkerFile>();
		for(Thing child : setThing.getChilds()){
			//System.out.println(child.getString("type") + ": " + child);
			if(type.equals(child.getString("type"))){
				String name = child.getString("name");
				
				if(thing.getBoolean(name)){
					String path = getPath(child.getString("repository"), child.getString("path"));
					File file = new File(path);
					if(file.exists()){
						if(file.getName().contains("gitignore") || (".svn").equals(file.getName())){	
							continue;
						}
						if("lib".equals(type) && file.isDirectory()){
							initDBFiles(file, xfList, child.getString("targetPath"), child.getString("excludes"), child.getString("repository"));
						}else{
							XWorkerFile xf = new XWorkerFile();
							xf.file = file;
							xf.name = name;
							xf.targetPath = child.getString("targetPath");
							xf.excludes = child.getString("excludes");
							xf.repository = child.getString("repository");
							xfList.add(xf);
						}
					}else{
						throw new XMetaException("XWorker ant: Path not exists, path=" + path + ",thing=" + child);
					}
				}
			}
		}
		
		return xfList;
	}
	
	private static void initDBFiles(File file, List<XWorkerFile> xfList, String targetPath, String excludes, String repository){
		if(file.isFile()){
			if(file.getName().contains("gitignore") || (".svn").equals(file.getName())){	
				return;
			}
			XWorkerFile xf = new XWorkerFile();
			xf.file = file;
			xf.name = file.getName();
			xf.targetPath = file.getName();
			xf.excludes = excludes;
			xf.repository = excludes;
			xfList.add(xf);
		}else if(file.isDirectory()){
			for(File child : file.listFiles()){
				initDBFiles(child, xfList, targetPath, excludes, repository);
			}
		}
	}
	
	/**
	 * 获取thing的ThingManager子节点下定义的事物管理器的类库。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<XWorkerFile> getThingManagerLibs(Thing thing){
		return getThingManagerFiles(thing, "/lib/");
	}
	
	/**
	 * 获取thing的ThingManager子节点定义的事物管理器的事物目录。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<XWorkerFile> getThingManagerThings(Thing thing){
		return getThingManagerFiles(thing, "/things/");
	}
	
	/**
	 * 如果ThingManager是开发状态，那么获取变异的class目录，默认为是bin目录。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<XWorkerFile> getThingManagerClasses(Thing thing){
		return getThingManagerFiles(thing, "/bin/");
	}
	
	/**
	 * 获取事物管理器的根目录。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<XWorkerFile> getThingManagers(Thing thing){
		return getThingManagerFiles(thing, "");
	}
	
	private static List<XWorkerFile> getThingManagerFiles(Thing thing, String fileType){
		List<XWorkerFile> fileList = new ArrayList<XWorkerFile>();
		String thingManagers = thing.getString("thingManagers");
		if(thingManagers != null){
			for(String name : thingManagers.split("[,]")){
				ThingManager thingManager = World.getInstance().getThingManager(name);
				if(thingManager != null && thingManager instanceof FileThingManager){
					FileThingManager fm = (FileThingManager) thingManager;
					String libFilePath = fm.getFilePath() + "/../" +  fileType;
					File lib = new File(libFilePath);
					if(lib.exists()){
						XWorkerFile f = new XWorkerFile();
						f.file = lib;
						f.name = name;
						fileList.add(f);
					}
				}else{
					//throw new XMetaException("XWorekr ant: can only copy FileThingManager lib and things, " + thingManagerThing);
				}
			}
		}
		
		return fileList;
	}
	
	public static String getPath(String repository, String path){
		World world = World.getInstance();
		if("world".equals(repository)){
			return world.getPath() + "/" + path;
		}else if("webroot".equals(repository)){
			return world.getPath() + "/" + "webroot" + "/" + path;
		}else{
			return path;
		}
	}

}