package xworker.util.compress;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.Patterns;
import xworker.util.UtilData;

public class EntryActions {
	private static Logger logger = LoggerFactory.getLogger(EntryActions.class);
	
	public static FileEntry createFileEntry(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = self.doAction("getFile", actionContext);
		String path = self.doAction("getPath", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		if(path == null){
			path = file.getName();
		}
		if(file != null){
			return new FileEntry(path, file, store);
		}else{
			return null;
		}
	}	
	
	public static DirectoryEntry createDirectoryEntry(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		File directory = self.doAction("getDirectory", actionContext);
		if(directory == null || directory.exists() == false) {
			logger.warn("Directory is null or not exists, ignore it, thing=" + self.getMetadata().getPath());
			return null;
		}
		
		File rootDirectory = self.doAction("getRootDirectory", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		String excludes = self.doAction("getExcludes", actionContext);
		String includes = self.doAction("getIncludes", actionContext);
		
		if(rootDirectory == null){
			rootDirectory = directory;
		}
		
		String pathPrefix = self.doAction("getPathPrefix", actionContext);
		return new DirectoryEntry(rootDirectory, directory, pathPrefix, store, 
				new Patterns(excludes, false), new Patterns(includes, true));
	}
	
	public static IconsEntry createIconsEntry(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String thingManager = self.getMetadata().getThingManager().getName();
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		String excludes = self.doAction("getExcludes", actionContext);
		String includes = self.doAction("getIncludes", actionContext);
		
		String pathPrefix = self.doAction("getPathPrefix", actionContext);
		return new IconsEntry(pathPrefix, thingManager, store, 
				new Patterns(excludes, false), new Patterns(includes, true));
	}
	
	public static long getLastModified(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return self.getMetadata().getLastModified();
	}
	
	public static ThingEntry createThingEntry(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new ThingEntry(self, actionContext);
	}
	
	public static ClassFileEntry createClassFileEntry(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		String className = self.doAction("getClassName", actionContext);
		Long lastModified = self.doAction("getLastModified", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		return new ClassFileEntry(className, lastModified, store);		
	}
	
	public static ResourceEntry createResourceEntry(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		String resource = self.doAction("getResource", actionContext);
		Long lastModified = self.doAction("getLastModified", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		return new ResourceEntry(resource, lastModified, store);		
	}
	
	public static BytesEntry createStringEntry(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String name = self.doAction("getName", actionContext);
		String content = self.doAction("getContent", actionContext);
		Long lastModified = self.doAction("getLastModified", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		byte[] bytes = content != null ? content.getBytes() : new byte[]{};
		
		return new BytesEntry(name, lastModified, bytes, store);
	}
	
	public static BytesEntry createManifestEntry(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String name = self.doAction("getName", actionContext);
		String content = self.doAction("getContent", actionContext);
		Long lastModified = self.doAction("getLastModified", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		if(content == null){
			throw new ActionException("Manifest content is null, thing=" + self.getMetadata().getPath());
		}
		
		//content = content.replaceAll("\n", "\r\n");
		byte[] bytes = content != null ? content.getBytes() : new byte[]{};
		
		return new BytesEntry(name, lastModified, bytes, store);
	}
	
	public static BytesEntry createBytesEntry(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String name = self.doAction("getName", actionContext);
		byte[] bytes = self.doAction("getBytes", actionContext);
		Long lastModified = self.doAction("getLastModified", actionContext);
		boolean store = UtilData.isTrue(self.doAction("isStore", actionContext));
		
		return new BytesEntry(name, lastModified, bytes, store);
	}
	
	public static XWorkerModuleEntry createXWorkerModuleEntry(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return new XWorkerModuleEntry(self, actionContext);
	}
}
