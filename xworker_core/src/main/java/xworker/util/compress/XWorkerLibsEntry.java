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

public class XWorkerLibsEntry implements CompressEntry{
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	boolean decompress;
	boolean store = false;
	List<String> libs = new ArrayList<String>();	
	
	public XWorkerLibsEntry(String jars, String rootPath, boolean decompress, boolean store){
		if(jars != null) {
			for(String jarss : jars.split("[,]")) {
				for(String jar : jarss.split("[\n]")) {
					libs.add(jar.trim());
				}
			}
		}
		this.decompress = decompress;
		this.store = store;
		File worldLib = new File(World.getInstance().getPath() + "/lib/");
		if(worldLib.exists() == false){
			String worldHome = World.getInstance().getHomeFormSytsem();
			worldLib = new File(worldHome + "/lib/");
		}		
		if(worldLib.exists() == false){
			throw new ActionException("Can not find xworker home or xworker home has no lib directory");
		}
		
		initChilds(worldLib, rootPath);
	}
	
	private void initChilds(File file, String rootPath) {
		if(file.isFile()) {
			String name = file.getName();
			
			for(String lib : libs){
				if(name.indexOf(lib) != -1){
					if(decompress) {
						childs.add(new JarFileEntry(file, rootPath, store));
					}else{
						childs.add(new FileEntry(rootPath + name, file, store));
					}
				}
			}	
		}else {
			for(File child : file.listFiles()) {
				initChilds(child, rootPath);
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
	
	public static XWorkerLibsEntry create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String path = self.doAction("getPath", actionContext);
		boolean decompress = self.doAction("isDecompress", actionContext);
		String jars = self.doAction("getJars", actionContext);
		Boolean store = self.doAction("isStore", actionContext);
		return new XWorkerLibsEntry(jars, path, decompress, store);
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
