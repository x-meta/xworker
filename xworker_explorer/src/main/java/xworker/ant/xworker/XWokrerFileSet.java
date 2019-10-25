package xworker.ant.xworker;

import java.io.File;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

public class XWokrerFileSet {
	public static void toString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
		
		//类库
		String libs = self.getStringBlankAsNull("libs");
		if(libs != null){
			String classPath = world.getClassLoader().getClassPath();
			String[] jars = classPath.split("[" + File.pathSeparator + "]");
			for(String lib : libs.split("[,]")){
				for(String jar : jars){
					if(jar.indexOf(lib) != -1){
						toFilesetString(jar, actionContext);
						break;
					}
				}
			}
		}
		
		//事物列表
		String things = self.getStringBlankAsNull("things");
		if(things != null){
			for(String thing : things.split("[,]")){
				ThingManager thingManager = world.getThingManager(thing);
				if(thingManager != null && thingManager instanceof FileThingManager){
					FileThingManager fileThingManager = (FileThingManager) thingManager;
					toFilesetString(fileThingManager.getFilePath(), actionContext);
				}
			}
		}
		
		//Classpath下的目录
		if(self.getBoolean("allClassPathDir")){
			List<String> jars = world.getClassLoader().getAlClassPathDirs();
			File current = new File(".");
			for(String jar : jars){
				File file = new File(jar);
				if(file.exists() && file.isDirectory() && !file.equals(current)){
					toFilesetString(jar, actionContext);
				}
			}			
		}
	}
	
	public static void toFilesetString(String filePath, ActionContext actionContext){
		File file = new File(filePath);
		if(!file.exists()){
			return;
		}
		
		Thing fileset = new Thing("xworker.ant.types.fileset");
		fileset.initDefaultValue();
		
		if(file.isFile()){
			fileset.put("file", filePath);
		}else if(file.isDirectory()){
			fileset.put("dir", filePath);
		}
		
		fileset.doAction("toString", actionContext);
	}
}
