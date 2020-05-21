package xworker.util.deploy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ModuleActions {
	public static void copyToDirectory(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		String scope = self.doAction("getScope", actionContext);
		//要拷贝到的目标目录
		File directory = self.doAction("getDirectory", actionContext);
		if(directory == null) {
			throw new ActionException("Target directory is null, action=" + self.getMetadata().getPath());
		}
		
		ModuleManager mm = new ModuleManager();
		for(Thing module : self.getChilds("Module")) {
			String modulePath = module.getStringBlankAsNull("module");
			boolean noDependencies = module.getBoolean("noDependencies");
			if(modulePath != null) {
				mm.addModule(modulePath, noDependencies);
			}
			
			for(Thing child : module.getChilds("Module")) {
				mm.addModule(child, child.getBoolean("noDependencies"));
			}
		}
		
		List<String> modules = self.doAction("getModules", actionContext);
		if(modules != null) {
			for(String module : modules) {
				mm.addModule(module, false);
			}
		}
		
		mm.calculate();
		
		for(ModuleResource mr : mm.getLibs()) {
			if(!mr.acceptScope(scope)) {
				continue;
			}
			
			String targetPath = self.doAction("getTargetPath", actionContext, "moduleResource", mr);
			if(targetPath != null) {
				FileUtils.copyFile(mr.getFile(), new File(directory, targetPath), true);
			}
		}
		
		for(ModuleResource mr : mm.getResources()) {
			if(!mr.acceptScope(scope)) {
				continue;
			}
			
			String targetPath = self.doAction("getTargetPath", actionContext, "moduleResource", mr);
			if(targetPath != null) {
				if(mr.getFile().isFile()) {
					FileUtils.copyFile(mr.getFile(), new File(directory, targetPath), true);
				}else {
					FileUtils.copyDirectoryToDirectory(mr.getFile(), new File(directory, targetPath).getParentFile());
				}
			}
		}
	}
	
	public static String getTargetPath(ActionContext actionContext) {
		ModuleResource mr = actionContext.getObject("moduleResource");
		return mr.getPath();
	}
}
