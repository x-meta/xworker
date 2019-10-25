package xworker.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

import xworker.util.UtilTemplate;

public class ScalaAction {
	private static Logger log = LoggerFactory.getLogger(ScalaAction.class);	
	
	private static String codeTemplate = null;
	static{
		codeTemplate = "import org.xmeta.ActionContext;\n" +
				"\n" +
				"object ${className} {\n" +
				"    def run(actionContext: ActionContext) = {\n" +
				"        ${code}\n" +
				"    }\n" +
				"}";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void compile(Action action, String classPath, String sourcePath, String fileName) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException{
		World world = World.getInstance();
	
		classPath = classPath.replaceAll("\\%20", " ");
	
		Class mainClass = null;
		try{
			mainClass = world.getClassLoader().loadClass("scala.tools.nsc.Main");
		}catch(Exception e){
			//应该是不存在，还没有导入Scala的类库
			Thing globalConfig  = world.getThing("_local.xworker.config.GlobalConfig");
			String scalaHome = globalConfig.getStringBlankAsNull("scalaHome");
			if(scalaHome == null){
				throw new ActionException("ScalaHome not setted, please set it in _local.xworker.config.GlobalConfig, action=" + sourcePath);
			}else{
				world.getClassLoader().addJarOrZip(new File(scalaHome + "/lib/"));
				mainClass = world.getClassLoader().loadClass("scala.tools.nsc.Main");
			}
		}
		Method compileMethod = mainClass.getDeclaredMethod("process", new Class[]{String[].class});
			
		if(sourcePath != null && !"".equals(sourcePath)){
			compileMethod.invoke(mainClass, new Object[]{new String[]{
					"-classpath", classPath,
					"-sourcepath", sourcePath, 
					"-d", action.getClassTargetDirectory(),
					fileName
			}});
		}else{		
			compileMethod.invoke(mainClass, new Object[]{new String[]{
					"-classpath", classPath,
					"-d", action.getClassTargetDirectory(),
					fileName
			}});
		}
	}
	
	
	public static Object run(ActionContext context) throws Exception{				
		//脚本的上下文		
		Bindings bindings = context.getScope(context.getScopesSize() - 2);
		
		//World world = bindings.world;
		Action action = null;
		if(bindings.getCaller() instanceof Thing){
			Thing actionThing = (Thing) bindings.getCaller();
			action = actionThing.getAction();
			action.checkChanged();
		}else{
			action = (Action) bindings.getCaller();
			action.checkChanged();
		}
		
		if(action.actionClass == null || action.changed){
			//查看代码是否需要重新编译			
			boolean recompile = false;
			if(action.changed){
				recompile = true;
			}
			if(action.actionClass == null){
				File classFile = new File(action.classFileName);
				if(!classFile.exists()){
					recompile = true;
				}
			}
			
			if(recompile){
				action.method = null;
				//重新编译并装载脚本
				Thing actionThing = action.getThing();
				
				File codeFile = null;
				if(actionThing.getBoolean("useInnerScala")){
					if(!(actionThing.getMetadata().getThingManager() instanceof FileThingManager)){
						throw new ActionException("Innter class only used in FileThingManager, actionThing=" + actionThing.getMetadata().getPath());
					}
					
					String className = actionThing.getStringBlankAsNull("outerClassName");
					FileThingManager manager = (FileThingManager) actionThing.getMetadata().getThingManager();
					codeFile = new File(manager.getFilePath(), className.replace('.', '/') + ".scala");
				}else{
					//更新代码
					codeFile = new File(action.fileName + ".scala");
					if(!codeFile.exists()){
						codeFile.getParentFile().mkdirs();
					}
					
					FileOutputStream fout = new FileOutputStream(codeFile);
					try{						
						String code = action.code;
						if(actionThing.getBoolean("isScript")){
							Map<String, Object> contexts = new HashMap<String, Object>();
							String className_ = action.className;
							int index = className_.lastIndexOf(".");
							if(index != -1){
								className_ = className_.substring(index + 1, className_.length());
							}
							contexts.put("className", className_);
							contexts.put("code", action.code);
							
							code = UtilTemplate.processString(contexts, codeTemplate);
						}
						fout.write(("/*path:" + action.getThing().getMetadata().getPath() + "*/\n").getBytes());
						fout.write(("package " + action.packageName + ";\n\n").getBytes());
						fout.write(code.getBytes("utf-8"));
						
					}finally{
						fout.close();
					}
				}
				
				//int dotIndex = action.className.lastIndexOf(".");			
				//String compileClassName = action.className.substring(dotIndex + 1, action.className.length());
				//compiler.compile(file);
				try{
					String sourcePath = null;
					ThingManager thingManager = actionThing.getMetadata().getThingManager();
					if(thingManager instanceof FileThingManager){
						sourcePath = new File(((FileThingManager) thingManager).getFilePath()).getAbsolutePath();
					}
					compile(action, action.getCompileClassPath(), sourcePath, codeFile.getAbsolutePath());
					//log.info("compile groovy " + action.getThing().getMetadata().getPath());
					action.updateCompileTime();					
				}catch(Exception me){
					log.error("compile scala code error: " + action.getThing().getMetadata().getPath());
					throw me;
				}
			}
			action.changed = false;
		}
		
		if(action.actionClass == null){
			Thing actionThing = action.getThing();
			if(actionThing.getBoolean("useInnerScala")){
				String className = actionThing.getStringBlankAsNull("outerClassName");
				if(className != null){
					action.actionClass = action.classLoader.loadClass(className);
				}
			}else{
				action.actionClass = action.classLoader.loadClass(action.className);	
			}			
			
			java.lang.Compiler.compileClass(action.actionClass);
		}
				
		if(action.actionClass  != null){			
			if(action.method == null){
				String methodName = action.getThing().getStringBlankAsNull("methodName");
				if(methodName == null){
					methodName = "run";
				}
				action.method = action.actionClass.getDeclaredMethod(methodName, new Class[]{ActionContext.class});				
			}
			
			if(action.method != null){
				return action.method.invoke(null, new Object[]{context});
			}else{
				log.warn("Scala action " + action.getThing().getMetadata().getPath() + " method is null");
			}
		}else{
			log.warn("Scala action " + action.getThing().getMetadata().getPath() + " class is null");
		}
		
		return null;
	}
}
