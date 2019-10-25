package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionParams;

import xworker.util.UtilData;
import xworker.util.deploy.ModuleManager;
import xworker.util.deploy.ModuleResource;

public class XWorkerModuleEntry implements CompressEntry{
	private static Logger logger = LoggerFactory.getLogger(XWorkerModuleEntry.class);
	
	Thing thing;
	ActionContext actionContext;
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	boolean decompress = false;
	boolean store = false;
	String path;
	String scope;
	String type;
	String os;
	boolean trimParentPath;
	List<Pattern> excludes = new ArrayList<Pattern>(); 	
	
	public XWorkerModuleEntry(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;				
		
		this.decompress = UtilData.isTrue(thing.doAction("isDecompress", actionContext));
		this.store = UtilData.isTrue(thing.doAction("isStore", actionContext));
		this.path = thing.doAction("getPath", actionContext);
		this.scope = thing.doAction("getScope", actionContext);
		this.type = thing.doAction("getType", actionContext);
		this.trimParentPath = thing.doAction("isTrimParentPath", actionContext);
		this.os = thing.doAction("getOs", actionContext);
		String exls = thing.doAction("getExcludes", actionContext);
		if(exls != null && !"".equals(exls)) {
			for(String exs : exls.split("[\n]")) {
				for(String ex : exs.split("[,]")) {
					ex = ex.trim();
					if("".equals(ex)) {
						continue;
					}
					
					excludes.add(Pattern.compile(ex));
				}
			}
		}
		
		init();
	}
	
	private boolean isExcluded(String path) {
		for(Pattern pattern : excludes) {
			if(pattern.matcher(path).matches()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isInclude(String atype) {
		return type == null || "".equals(type) || type.equals(atype);
	}
	
	private boolean isAccept(ModuleResource mr) {
		if(!mr.acceptScope(scope) || isExcluded(mr.getPath())) {
			return false;
		}
		
		if(this.os != null && mr.getOs() != null && !this.os.equals(mr.getOs())) {
			return false;
		}
		
		return true;
	}
	
	private void init() {
		//添加和计算模块
		ModuleManager mm = new ModuleManager();
		for(Thing module : thing.getChilds("Module")) {
			String modulePath = module.getStringBlankAsNull("module");
			if(modulePath != null) {
				mm.addModule(modulePath);
			}
			
			for(Thing child : module.getChilds("Module")) {
				mm.addModule(child);
			}
		}
		
		for(Thing module : thing.getChilds("NewModule")) {
			mm.addModule(module);			
		}
		
		List<String> modules = thing.doAction("getModules", actionContext);
		if(modules != null) {
			for(String module : modules) {
				mm.addModule(module);
			}
		}
		mm.calculate();
		
		//添加类库
		if(isInclude("lib")) {
			for(ModuleResource mr : mm.getLibs()) {
				if(!isAccept(mr)) {
					continue;
				}
				
				
				String path = null;
				if(trimParentPath) {
					path = thing.doAction("getEntryPath", actionContext,			
							"path", this.path, "resPath", mr.getFile().getName(), "moduleResource", mr);
				}else {
					path = thing.doAction("getEntryPath", actionContext,				
							"path", this.path, "resPath", mr.getPath(), "moduleResource", mr);
				}
				if(path != null) {
					if(decompress) {
						childs.add(new JarFileEntry(mr.getFile(), this.path, store));
					}else{
						childs.add(new FileEntry(path, mr.getFile(), store));
					}
				}
			}
		}
		
		//添加资源
		if(isInclude("resource")) {
			for(ModuleResource mr : mm.getResources()) {
				if(!isAccept(mr)) {
					continue;
				}
				
				String path = null;
				if(trimParentPath) {
					path = thing.doAction("getEntryPath", actionContext,				
							"path", this.path, "resPath", mr.getFile().getName(), "moduleResource", mr);
				}else {
					path = thing.doAction("getEntryPath", actionContext,				
							"path", this.path, "resPath", mr.getPath(), "moduleResource", mr);
				}
				if(path != null) {
					if(mr.getFile().isDirectory()) {
						try {
							childs.add(new DirectoryEntry(mr.getFile(), mr.getFile(), path, store, null, null));
						} catch (IOException e) {
							logger.warn("init directory erro", e);
						}
					}else {
						childs.add(new FileEntry(path, mr.getFile(), store));
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

	@ActionParams(names="path, resPath, moduleResource")
	public static String getEntryPath(String path, String resPath, ModuleResource moduleResource, ActionContext actionContext) {
		if(path == null && !"".equals(path)) {
			return resPath;
		}else {
			return path + resPath;
		}
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
