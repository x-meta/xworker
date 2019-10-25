package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class RapLibsEntry implements CompressEntry{
	private static Logger logger = LoggerFactory.getLogger(RapLibsEntry.class);
	
	List<CompressEntry> childs = new ArrayList<CompressEntry>();	
	boolean decompress;
	
	public RapLibsEntry(String rootPath, boolean decompress, boolean store){
		this.decompress = decompress;
		File worldLib = new File(World.getInstance().getPath() + "/lib_rap/");
		if(worldLib.exists() == false){
			String worldHome = World.getInstance().getHomeFormSytsem();
			worldLib = new File(worldHome + "/lib_rap/");
		}		
		if(worldLib.exists() == false){
			logger.warn("Can not find xworker home or xworker home has no lib_rap directory");
			return;
		}
		
		childs.add(new XWorkerLibsEntry("xworker_rap", rootPath, decompress, store));
		for(File file : worldLib.listFiles()){
			if(file.isFile()) {
				String name = file.getName();
				if(decompress) {
					childs.add(new JarFileEntry(file, rootPath, store));
				}else{
					childs.add(new FileEntry(rootPath + name, file, store));
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
	
	public static RapLibsEntry create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String path = self.doAction("getPath", actionContext);
		boolean decompress = self.doAction("isDecompress", actionContext);
		Boolean store = self.doAction("isStore", actionContext);
		return new RapLibsEntry(path, decompress, store);
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
