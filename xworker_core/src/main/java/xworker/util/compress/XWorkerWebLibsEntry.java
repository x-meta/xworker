package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerConstants;

public class XWorkerWebLibsEntry implements CompressEntry{
	List<CompressEntry> childs = new ArrayList<CompressEntry>();	
	boolean decompress;
	
	public XWorkerWebLibsEntry(String rootPath, boolean decompress, boolean store){
		this.decompress = decompress;
		File worldLib = new File(World.getInstance().getPath() + "/lib/");
		if(worldLib.exists() == false){
			String worldHome = World.getInstance().getHomeFormSytsem();
			worldLib = new File(worldHome + "/lib/");
		}		
		if(worldLib.exists() == false){
			throw new ActionException("Can not find xworker home or xworker home has no lib directory");
		}
		
		for(File file : worldLib.listFiles()){
			String name = file.getName();
			
			for(String lib : XWorkerConstants.XMETA_LIBS){
				if(name.startsWith(lib)){
					if(decompress) {
						childs.add(new JarFileEntry(file, rootPath, store));
					}else {
						childs.add(new FileEntry(rootPath + name, file, store));
					}
				}
			}
			
			for(String lib : XWorkerConstants.WEB_FORWAR_LIBS){
				if(name.startsWith(lib)){
					if(decompress) {
						childs.add(new JarFileEntry(file, rootPath, store));
					}else {
						childs.add(new FileEntry(rootPath + name, file, store));
					}
				}
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
	public Iterator<CompressEntry> getChildsIterator() {
		return childs.iterator();
	}
	
	public static XWorkerWebLibsEntry create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String path = self.doAction("getPath", actionContext);
		boolean decompress = self.doAction("isDecompress", actionContext);
		Boolean store = self.doAction("isStore", actionContext);
		return new XWorkerWebLibsEntry(path, decompress, store);
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public void open() {
		
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
		return -1;
	}
}
