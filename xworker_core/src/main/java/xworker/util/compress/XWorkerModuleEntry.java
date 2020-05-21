package xworker.util.compress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
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
	String trimPath;
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
		this.trimPath = thing.getStringBlankAsNull("trimPath");
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
		
		try {
			init();
		}catch(Exception e) {
			throw new ActionException("Init xworker module entry error, model=" + thing.getMetadata().getPath(), e);
		}
	}
	
	private boolean isExcluded(String path) {
		if(path == null) {
			return false;
		}
		
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
		if(mr.getFile() == null) {
			//ModuleResource是为了拷贝文件，如果文件不存在，那么应该是没有用的
			return false;
		}
		
		if(!mr.acceptScope(scope) || isExcluded(mr.getFile().getAbsolutePath())) {
			return false;
		}
		
		if(this.os != null && mr.getOs() != null && !this.os.equals(mr.getOs())) {
			return false;
		}
		
		return true;
	}
	
	private String getEntryPath(String rootPath, File file) throws IOException {
		String resPath = null;
		if(trimParentPath) {
			resPath = file.getName();
		}else {
			resPath = file.getCanonicalPath();
			
			resPath = resPath.replace('\\', '/');
			if(resPath.startsWith(rootPath)) {
				resPath = resPath.substring(rootPath.length(), resPath.length());
			}
			
			if(trimPath != null && resPath.startsWith(trimPath)) {
				resPath = resPath.substring(trimPath.length(), resPath.length());
			}
		}
		
		if(path == null || path.equals("")) {
			return resPath;
		}else{
			return path + resPath;
		}
	}
	
	private void init() throws IOException {
		//添加和计算模块
		ModuleManager mm = new ModuleManager();
		for(Thing module : thing.getChilds("Module")) {
			boolean noDependencies = module.getBoolean("noDependencies");
			String modulePath = module.getStringBlankAsNull("module");
			if(modulePath != null) {
				mm.addModule(modulePath, noDependencies);
			}
			
			for(Thing child : module.getChilds("Module")) {
				mm.addModule(child, noDependencies);
			}
		}
		
		for(Thing module : thing.getChilds("NewModule")) {
			mm.addModule(module, module.getBoolean("noDependencies"));			
		}
		
		List<String> modules = thing.doAction("getModules", actionContext);
		if(modules != null) {
			for(String module : modules) {
				mm.addModule(module, false);
			}
		}
		mm.calculate();
		
		File rootDir = mm.getRootDir();
		String rootPath = rootDir.getCanonicalPath();
		rootPath = rootPath.replace('\\', '/');		
		
		//添加类库
		if(isInclude("lib")) {
			for(ModuleResource mr : mm.getLibs()) {
				if(!isAccept(mr)) {
					continue;
				}
				
				File file = mr.getFile();
				if(file.isDirectory()) {
					childs.add(new DirectoryEntry(mr.getFile(), mr.getFile(), this.path, store, null, null));
				}else if(decompress && file.getName().toLowerCase().endsWith(".jar")) {
					childs.add(new JarFileEntry(mr.getFile(), this.path, store));
				}else {
					String entryPath = getEntryPath(rootPath, mr.getFile());
					childs.add(new FileEntry(entryPath, mr.getFile(), store));
				}				
			}
		}
		
		//添加资源
		if(isInclude("resource")) {
			for(ModuleResource mr : mm.getResources()) {
				if(!isAccept(mr)) {
					continue;
				}
				
				String entryPath = getEntryPath(rootPath, mr.getFile());
				if(entryPath != null) {				
					if(mr.getFile().isDirectory()) {
						try {
							childs.add(new DirectoryEntry(mr.getFile(), mr.getFile(), entryPath, store, null, null));
						} catch (IOException e) {
							logger.warn("init directory erro", e);
						}
					}else {
						childs.add(new FileEntry(entryPath, mr.getFile(), store));
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
		if(path == null || "".equals(path)) {
			if(resPath == null || "".equals(resPath)) {
				return null;
			}
			return resPath;
		}else {
			if(resPath == null || "".equals(resPath)) {
				return path;
			}
			
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
