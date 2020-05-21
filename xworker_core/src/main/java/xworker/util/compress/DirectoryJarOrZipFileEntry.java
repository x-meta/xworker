package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.util.Patterns;

public class DirectoryJarOrZipFileEntry implements CompressEntry{
	private static final String TAG = DirectoryJarOrZipFileEntry.class.getName();
	
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	boolean store;
	String path;
	
	public DirectoryJarOrZipFileEntry(File directory, String path, boolean store, Patterns excludes,  Patterns includes) throws IOException{
		this.path = path;
		this.store = store;
		
		initDir(directory, excludes, includes);
	}
		
	private void initDir(File file, Patterns excludes,  Patterns includes) throws IOException {
		if(!isMatch(file, excludes, includes)) {
			return;
		}
		
		if(file.isFile()) {
			String name = file.getName().toLowerCase();
			if(name.endsWith(".jar")) {
				childs.add(new JarFileEntry(file, path, store));
			}else if(name.endsWith(".zip")) {
				childs.add(new ZipFileEntry(file, path, store));
			}
		}else {
			for(File child : file.listFiles()) {
				initDir(child, excludes, includes);
			}
		}		
	}
	
	public boolean isMatch(File file , Patterns excludes,  Patterns includes) throws IOException {
		if(file == null || file.exists() == false) {
			return false;
		}
		
		String rootPath = file.getCanonicalPath();
		
		if(excludes != null && excludes.mattches(rootPath)) {
			return false;
		}
		
		if(includes != null && !includes.mattches(rootPath)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void write(OutputStream output) throws IOException {
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() {
		return childs.iterator();
	}
	
	@Override
	public long getSize() {
		return 0;
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public void open() {
		
	}
	
	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public long getCRC32() {
		return 0;
	}

	@Override
	public int getMethod() {
		return -1;
	}
	
	public static DirectoryJarOrZipFileEntry create(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		File directory = self.doAction("getDirectory", actionContext);
		if(directory == null || directory.exists() == false) {
			Executor.warn(TAG, "Directory is null or not exists, ignore it, thing=" + self.getMetadata().getPath());
			return null;
		}
		
		String path = self.doAction("getPath", actionContext);
		Boolean store = self.doAction("isStore", actionContext);
		if(store == null) {
			store = false;
		}
		
		String excludes = self.doAction("getExcludes", actionContext);
		String includes = self.doAction("getIncludes", actionContext);
		
		return new DirectoryJarOrZipFileEntry(directory, path, store, new Patterns(excludes, false), new Patterns(includes, true));
	}

}
