package xworker.util.deploy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

/**
 * 模块管理，模块是XWorker的模块，是发布在XWorker目录下的类库或资源。
 * @author zyx
 *
 */
public class ModuleManager {
	//private static Logger logger = LoggerFactory.getLogger(ModuleManager.class);
	private static final String TAG = ModuleManager.class.getName();
	
	/** 已经处理的模块 */
	Map<String, Thing> modules = new HashMap<String, Thing>();
	
	/** 类库配置列表 */
	List<ModuleResource> libs = new ArrayList<ModuleResource>();
	
	/** 资源配置列表 */
	List<ModuleResource> resources = new ArrayList<ModuleResource>();
	
	/** 已经匹配成功的类库 */
	List<ModuleResource> matchedLibs = new ArrayList<ModuleResource>();
	
	/** 已经匹配成功的资源 */
	List<ModuleResource> matchedReses = new ArrayList<ModuleResource>();

	File rootDir;
	
	public ModuleManager() {
		
	}
	
	public ModuleManager(Thing moduleThing, ActionContext actionContext) {
		for(Thing module : moduleThing.getChilds("Module")) {
			boolean noDependencies = module.getBoolean("noDependencies");
			String modulePath = module.getStringBlankAsNull("module");
			if(modulePath != null) {
				addModule(modulePath, noDependencies);
			}
			
			for(Thing child : module.getChilds("Module")) {
				addModule(child, module.getBoolean("noDependencies"));
			}
		}
		
		List<String> modules = moduleThing.doAction("getModules", actionContext);
		if(modules != null) {
			for(String module : modules) {
				addModule(module, false);
			}
		}
		
		calculate();
	}
	
	public File getRootDir() {
		return rootDir;
	}
	/**
	 * 计算最终有哪些类库和资源。
	 */
	public void calculate() {
		//优先获取系统中的XWorker，避免使用开发环境下的xworker，开发环境下的xworker目录不全
		World world = World.getInstance();
		String homePath = world.getHomeFormSytsem();
		
		rootDir = new File(homePath);
		if(rootDir.exists() == false) {
			rootDir = new File(world.getPath());
		}
		if(!rootDir.exists()) {
			if(Executor.isLogLevelEnabled(TAG, Executor.DEBUG)) {
				Executor.debug(TAG, "XWorker home path is null, no resources or libs finded.");
			}
			return;
		}
		
		if(Executor.isLogLevelEnabled(TAG, Executor.TRACE)) {
			Executor.trace(TAG, "XWorker home is {}", rootDir.getPath());
		}
		check(rootDir, 0);
	}
	
	/**
	 * 返回已匹配的类库列表。
	 * 
	 * @return
	 */
	public List<ModuleResource> getLibs(){
		return matchedLibs;
	}
	
	/**
	 * 返回匹配成功的资源列表。
	 * 
	 * @return
	 */
	public List<ModuleResource> getResources(){
		return matchedReses;
	}
	
	private void check(File file, int depth) {		
		//没有必要遍历所有xworker的而目录，我们认为最多遍历深度最大4即可
		if(depth > 4) {
			return;
		}
		
		if(file.isFile()) {
			for(int i=0; i<libs.size(); i++) {
				ModuleResource mr = libs.get(i);
				try {
					ModuleResource mrr = mr.accept(file);
					if(mrr != null) {						
						matchedLibs.add(mrr);
						
						Executor.trace(TAG, "Lib file accepted {}", file);
						//一旦匹配就没有必要匹配剩余的了
						return;
					}
				}catch(Exception e) {
					Executor.warn(TAG, "Check resource exception, pathRegex={}, exception={}",
							mr.pattern.toString(), e.getMessage());					
				}
			}
		}
		
		
		for(int i=0; i<resources.size(); i++) {
			ModuleResource mr = resources.get(i);
			try {
				ModuleResource mrr = mr.accept(file);
				if(mrr != null) {						
					matchedReses.add(mrr);
					
					//一旦匹配就没有必要匹配剩余的了
					Executor.trace(TAG, "Resource file accepted {}", file);
					return;
				}
			}catch(Exception e) {
				Executor.warn(TAG, "Check resource exception, pathRegex={}, exception={}",
						mr.pattern.toString(), e.getMessage());
			}
		}		
		
		Executor.trace(TAG, "File not accepted {}", file);
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				check(child, depth + 1);
			}
		}
	}
	
	/**
	 * 添加一个模块。
	 * 
	 * @param modulePath
	 */
	public void addModule(String modulePath, boolean noDependencies) {
		addModule(World.getInstance().getThing(modulePath), noDependencies);
	}
	
	/**
	 * 添加一个模块。
	 * 
	 * @param module
	 */
	public void addModule(Thing module, boolean noDependencies) {
		if(module == null) {
			return;
		}
		
		String path = module.getMetadata().getPath();
		//模块已经存在
		if(modules.get(path) != null) {
			return;			
		}
		
		//添加类库
		for(Thing lib : module.getChilds("Lib")) {
			ModuleResource mr = new ModuleResource(lib, lib.getStringBlankAsNull("pathRegex"), true);
			libs.add(mr);				
		}
		
		//添加资源
		for(Thing res : module.getChilds("Resource")) {
			ModuleResource mr = new ModuleResource(res, res.getStringBlankAsNull("pathRegex"), true);
			resources.add(mr);		
		}
		
		//加载依赖
		String dependencies = module.getStringBlankAsNull("dependencies");
		if(dependencies != null && noDependencies == false) {
			for(String dess : dependencies.split("[\n]")) {
				for(String des : dess.split("[,]")) {
					des = des.trim();
					if(!"".equals(des)) {
						addModule(des, noDependencies);
					}
				}
			}
		}
	}
}
