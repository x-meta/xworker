package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xworker.util.Patterns;

public class DirectoryEntry implements CompressEntry{
	File rootDir;
	File dir;
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	String pathPrefix = null;
	
	public DirectoryEntry(File rootDir, File dir, String pathPrefix, boolean store, Patterns excludes,  Patterns includes) throws IOException{
		String rootPath = rootDir.getCanonicalPath();
		rootPath = rootPath.replace('\\', '/');
		if(rootPath.endsWith("/") == false) {
			rootPath = rootPath + "/";
		}
		for(File child : dir.listFiles()){
			if(child.isFile()){
				String path = child.getCanonicalPath();
				path = path.replace('\\', '/');
				if(excludes != null && excludes.mattches(path)) {
					continue;
				}
				if(includes == null || includes.mattches(path)) {
					String name = path.substring(rootPath.length(), path.length());
					if(pathPrefix != null) {
						name = pathPrefix + name;
					}
					
					childs.add(new FileEntry(name, child, store));
				}
			}else if(child.isDirectory()){
				String path = child.getCanonicalPath();
				path = path.replace('\\', '/');
				if(excludes != null && excludes.mattches(path)) {
					continue;
				}
				if(includes == null || includes.mattches(path)) {
					childs.add(new DirectoryEntry(rootDir, child, pathPrefix, store, excludes, includes));
				}
			}
		}
		
		this.pathPrefix = pathPrefix;
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

}
