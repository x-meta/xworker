package xworker.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

import xworker.util.XWorkerConstants;

public class XWorkerFileActions {
	
	public static void copyThingsToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		String thingManagerName = self.doAction("getThingManagerName", actionContext);
		File directory = self.doAction("getDirectory", actionContext);
		
		ThingManager tm = World.getInstance().getThingManager(thingManagerName);
		if(tm == null){
			throw new ActionException("ThingMananger is null, name=" + thingManagerName + ", action=" + self.getMetadata().getPath());
		}
		
		if(tm instanceof FileThingManager){
			FileThingManager ftm = (FileThingManager) tm;
			FileUtils.copyDirectory(ftm.getThingRootFile(), directory);
		}else{
			Iterator<Thing> iter = tm.iterator(null, true);
			while(iter.hasNext()){
				Thing thing = iter.next();
				Thing rootThing = thing.getRoot();
				String rootThingPath = rootThing.getMetadata().getPath();
				ThingCoder thingCoder = World.getInstance().getThingCoder(rootThing.getMetadata().getCoderType());
				File thingFile = new File(directory, rootThingPath.replace('.', '/') + "." + thingCoder.getType());
				if(!thingFile.exists()){
					thingFile.getParentFile().mkdirs();
				}
				FileOutputStream fout = new FileOutputStream(thingFile);
				try{
					thingCoder.encode(rootThing, fout);
					fout.flush();
				}finally{
					fout.close();						
				}
				thingFile.setLastModified(rootThing.getMetadata().getLastModified());
			}
		}
	}
	
	public static void copyLibsToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		String thingManagerName = self.doAction("getThingManagerName", actionContext);
		File directory = self.doAction("getDirectory", actionContext);
		
		ThingManager tm = World.getInstance().getThingManager(thingManagerName);
		if(tm == null){
			throw new ActionException("ThingMananger is null, name=" + thingManagerName + ", action=" + self.getMetadata().getPath());
		}
		
		if(tm instanceof FileThingManager){
			FileThingManager ftm = (FileThingManager) tm;
			FileUtils.copyDirectory(new File(ftm.getRootDir(), "lib") , directory);
		}
	}
	
	public static void copyXMetaLibsToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = self.doAction("getDirectory", actionContext);
		
		String[] libs = XWorkerConstants.XMETA_LIBS;
		copyBaseLib(directory,  libs);
	}
	
	public static void copyWebLibsToDirectory(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		File directory = self.doAction("getDirectory", actionContext);
		
		String[] libs = XWorkerConstants.WEB_FORWAR_LIBS;
		copyBaseLib(directory,  libs);
	}
	
	public static void copyBaseLib(File targetDir, String[] libs) throws IOException{ 
		File worldLib = new File(World.getInstance().getPath() + "/lib/");
		if(worldLib.exists() == false){
			String worldHome = World.getInstance().getHomeFormSytsem();
			worldLib = new File(worldHome + "/lib/");
		}		
		if(worldLib.exists() == false){
			throw new ActionException("Can not find xworker home or xworker home has no lib directory");
		}
		
		copyBaseLib(worldLib, targetDir, libs);
	}
	public static void copyBaseLib(File libDir, File targetDir, String[] libs) throws IOException{ 
	    if(libDir.isDirectory()){
	        for(File child : libDir.listFiles()){
	            copyBaseLib(child, targetDir, libs);
	        }
	        return;
	    }
	        
	    //println(dir.getAbsolutePath());
	    String name = libDir.getName().toLowerCase();
	    //println("xxxxxxxx=" + name);
	    boolean have = false;
	    for(String lib : libs){
	        if(name.startsWith(lib)){
	            have = true;
	            break;
	        }
	    }
	    
	    if(have){
	        FileUtils.copyFile(libDir, new File(targetDir, libDir.getName()));
	    }
	}
}
