package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xworker.util.IconsUtils;
import xworker.util.Patterns;
import xworker.util.IconsUtils.Icon;

public class IconsEntry implements CompressEntry{
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	String pathPrefix = null;
	
	public IconsEntry(String pathPrefix, String thingManager,  boolean store, Patterns excludes,  Patterns includes) {
		List<Icon> icons = IconsUtils.getIcons(thingManager);
		
		for(Icon icon : icons) {
			String path = icon.name;
			path = path.replace('\\', '/');
			if(excludes != null && excludes.mattches(path)) {
				continue;
			}
			if(includes == null || includes.mattches(path)) {
				String name = path;
				if(pathPrefix != null) {
					name = pathPrefix + name;
				}
				
				childs.add(new FileEntry(name, icon.file, store));
			}
		}
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
	public long getSize() {
		return 0;
	}

	@Override
	public Iterator<CompressEntry> getChildsIterator() throws IOException {
		return childs.iterator();
	}

	@Override
	public void open() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public long getCRC32() {
		return 0;
	}

	@Override
	public int getMethod() {
		return 0;
	}
	

}
