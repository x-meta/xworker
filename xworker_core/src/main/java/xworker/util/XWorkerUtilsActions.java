package xworker.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class XWorkerUtilsActions {
	public static Object getIde(ActionContext actionContext){
		return XWorkerUtils.getIde();
	}
	
	public static void ideOpenThing(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenThing(thing);
	}
	
	public static void ideOpenFile(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		File file = (File) self.doAction("getFile", actionContext);		
		XWorkerUtils.ideOpenFile(file);
	}
	
	public static void ideOpenThingAndSelectCodeLine(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		String codeAttrName = (String) self.doAction("getCodeAttrName", actionContext);
		Integer lineIndex = (Integer) self.doAction("getLineIndex", actionContext);
		XWorkerUtils.ideOpenThingAndSelectCodeLine(thing, codeAttrName, lineIndex);
	}
	
	public static void ideOpenComposite(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getCompositeThing", actionContext);
		XWorkerUtils.ideOpenComposite(thing);
	}
	
	public static void ideOpenUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String type = self.doAction("getType", actionContext);		
		String url = (String) self.doAction("getUrl", actionContext);
		if("webControl".equals(type)) {
			url = XWorkerUtils.getWebControlUrl(World.getInstance().getThing(url));
		}else {
			url = XWorkerUtils.getThingDescUrl(url);
		}
		XWorkerUtils.ideOpenUrl(url);
	}
	
	public static void ideOpenThingTab(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenThingTab(thing);
	}
	
	public static void ideOpenWebControl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		XWorkerUtils.ideOpenWebControl(thing);
	}
	
	public static String getWebControlUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		return XWorkerUtils.getWebControlUrl(thing);
	}
	
	public static String getWebUrl(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getWebUrl();
	}
	
	@SuppressWarnings("unchecked")
	public static void ideDoAction(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String name = (String) self.doAction("getActionName", actionContext);
		Map<String, Object> params = (Map<String, Object>) self.doAction("getParams", actionContext);
		XWorkerUtils.ideDoAction(name, params);
	}
	
	public static ActionContainer getIdeActionContainer(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getIdeActionContainer();
	}
	
	public static ActionContext getIdeActionContext(ActionContext actionContext) {
		return XWorkerUtils.getIdeActionContainer().getActionContext();
	}
	
	public static Object getIDEShell(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		return XWorkerUtils.getIDEShell();
	}
	
	public static void ideShowMessageBox(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String title = (String) self.doAction("getTitle", actionContext);
		String message = (String) self.doAction("getMessage", actionContext);
		Integer style = (Integer) self.doAction("getStyle", actionContext);
		
		XWorkerUtils.ideShowMessageBox(title, message, style);
	}
	

	public static Object getThingDescUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		
		return XWorkerUtils.getThingDescUrl(thing);
	}
	
	public static Thing getThingIfNotExistsCopy(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String thing = (String) self.doAction("getThing", actionContext);
		String thingManager = (String) self.doAction("getThingManager", actionContext);
		String copyFrom = (String) self.doAction("getCopyFrom", actionContext);
		
		return XWorkerUtils.getThingIfNotExistsCopy(thing, thingManager, copyFrom);
	}
	
	public static Thing getThingIfNotExistsCreate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String thing = (String) self.doAction("getThing", actionContext);
		String thingManager = (String) self.doAction("getThingManager", actionContext);
		String descriptor = (String) self.doAction("getDescriptor", actionContext);
		
		return XWorkerUtils.getThingIfNotExistsCreate(thing, thingManager, descriptor);
	}
	
	public static Thing getPreference(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing defaultConfig = self.doAction("getDefaultConfig", actionContext);
		return XWorkerUtils.getPreference(defaultConfig);
	}
	
	public static void compileJavaFiles(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String classPath = self.doAction("getClassPath", actionContext);
		String sourcePath = self.doAction("getSourcePath", actionContext);
		String targetDir = self.doAction("getTargetDir", actionContext);
		List<String> javaFiles = self.doAction("getJavaFiles", actionContext);
		
		World world = World.getInstance();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler != null){
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits1 =	fileManager.getJavaFileObjectsFromStrings(javaFiles);
						
			String bootClass = System.getProperty("sun.boot.class.path");
			String classPath_ = world.getClassLoader().getCompileClassPath();
			String cp = classPath + File.pathSeparator + bootClass + File.pathSeparator + classPath_;
			List<String> options = null;
			if(sourcePath != null && !"".equals(sourcePath)){
				options = Arrays.asList("-cp", cp, "-sourcepath", sourcePath, "-d", world.getPath() + "/work/actionClasses");
			}else{
				options = Arrays.asList("-cp", cp, "-d", targetDir);
			}
			compiler.getTask(null, fileManager, null, options, null, compilationUnits1).call();
			
		}else{
			throw new ActionException("Can not get JavaCompiler from ToolProvider, path=" + self.getMetadata().getPath());
		}
	
	}
}
