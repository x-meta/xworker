package xworker.io.file;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.ThingObjectUtil;

public class FileAlterationMonitorActions {
	public static Object getMonitor(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return ThingObjectUtil.getObject(self, "createMonitor", "stopMonitor", actionContext);
	}
	
	public static Object createMonitor(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Long interval= (Long) self.doAction("getInterval", actionContext);
		FileAlterationMonitor monitor = null;
		if(interval != null && interval > 0){
			monitor = new FileAlterationMonitor(interval);
		}else{
			monitor = new FileAlterationMonitor();
		}
		
		//创建Observer
		for(Thing child : self.getChilds("FileAlterationObserver")){
			FileAlterationObserver obser = (FileAlterationObserver) child.doAction("create", actionContext);
			obser.addListener(new FileAlterationListenerImpl(child));
			monitor.addObserver(obser);
		}
		
		return monitor;
	}
	
	public static Object run(ActionContext actionContext) throws Exception{
		Thing self = actionContext.getObject("self");
		
		FileAlterationMonitor monitor = (FileAlterationMonitor) self.doAction("getMonitor", actionContext);
		monitor.start();
		
		return monitor;
	}
	
	public static void stopMonitor(ActionContext actionContext) throws Exception{
		FileAlterationMonitor monitor = (FileAlterationMonitor) actionContext.get("object");
		if(monitor != null){
			monitor.stop();
		}
	}
	
	public static void stop(ActionContext actionContext) throws Exception{
		Thing self = actionContext.getObject("self");
		
		FileAlterationMonitor monitor = (FileAlterationMonitor) self.doAction("getMonitor", actionContext);
		if(monitor != null){
			monitor.stop();
		}
	}
	
	public static class FileAlterationListenerImpl implements FileAlterationListener{
		Thing thing;
		ActionContext actionContext = new ActionContext();
		
		public FileAlterationListenerImpl(Thing thing){
			this.thing = thing;
		}
		
		@Override
		public void onStart(FileAlterationObserver observer) {
			thing.doAction("onStart", actionContext, "observer", observer);
		}

		@Override
		public void onDirectoryCreate(File directory) {
			thing.doAction("onDirectoryCreate", actionContext, "directory", directory);
		}

		@Override
		public void onDirectoryChange(File directory) {
			thing.doAction("onDirectoryChange", actionContext, "directory", directory);
		}

		@Override
		public void onDirectoryDelete(File directory) {
			thing.doAction("onDirectoryDelete", actionContext, "directory", directory);
		}

		@Override
		public void onFileCreate(File file) {
			thing.doAction("onFileCreate", actionContext, "file", file);			
		}

		@Override
		public void onFileChange(File file) {
			thing.doAction("onFileChange", actionContext, "file", file);
		}

		@Override
		public void onFileDelete(File file) {
			thing.doAction("onFileDelete", actionContext, "file", file);
		}

		@Override
		public void onStop(FileAlterationObserver observer) {
			thing.doAction("onStop", actionContext, "observer", observer);
		}
		
	}
}
