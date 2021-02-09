/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.groovy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.UtilAction;

import groovy.lang.Binding;
import groovy.lang.Script;

public class GroovyAction {
	//private static Logger log = LoggerFactory.getLogger(GroovyAction.class);
	//没发现哪里使用它了，暂时屏蔽
	//private static List<String> codeAssistorCachesList = new ArrayList<String>();
	
	/**
	 * 增加代码辅助缓存的标记。
	 * 
	 * @param thing
	 * @deprecated 已经失效
	 */
	public static void addCodeAssistorCache(Thing thing){
		/*
		String path = thing.getMetadata().getPath();
		if(!codeAssistorCachesList.contains(path)){
			codeAssistorCachesList.add(path);
		}*/
	}	
	
	public static Object run(Action action, ActionContext context) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(action.getActionClass() == null || action.isChanged()){
			//查看代码是否需要重新编译			
			boolean recompile = false;
			if(action.isChanged() || action.isNeedRecompile()){
				recompile = true;
			}
			if(action.getActionClass() == null){
				File classFile = new File(action.getClassFileName());
				if(!classFile.exists()){
					classFile.getParentFile().mkdirs();
					recompile = true;
				}
			}
			
			if(recompile && World.getInstance().getMode() == World.MODE_PROGRAMING){
				//重新编译并装载脚本
				Thing actionThing = action.getThing();
				if(actionThing.getStringBlankAsNull("outterClassName") == null){
					String className = actionThing.getStringBlankAsNull("innerClassName");
	
					org.codehaus.groovy.tools.Compiler compiler;
					Properties prop = new Properties();
					prop.setProperty("groovy.target.directory", action.getClassTargetDirectory());
					prop.setProperty("groovy.classpath", action.getCompileClassPath());
					CompilerConfiguration config = new CompilerConfiguration(prop);
					config.setSourceEncoding("utf-8");
					//System.out.println(config.getClasspath());
					compiler = new org.codehaus.groovy.tools.Compiler(config);
					
					File codeFile = null;
					if(className != null){
						if(!(actionThing.getMetadata().getThingManager() instanceof FileThingManager)){
							throw new ActionException("Innter class only used in FileThingManager, actionThing=" + actionThing.getMetadata().getPath());
						}
						
						FileThingManager manager = (FileThingManager) actionThing.getMetadata().getThingManager();
						codeFile = new File(manager.getFilePath(), className.replace('.', '/') + ".groovy");
					}else{
						//更新代码
						codeFile = new File(action.getFileName() + ".groovy");
						if(!codeFile.exists()){
							codeFile.getParentFile().mkdirs();
						}
						
						FileOutputStream fout = new FileOutputStream(codeFile);
						try{					
							fout.write(("/*path:" + action.getThing().getMetadata().getPath() + "*/\n").getBytes());
							String packageName = action.getPackageName();
							if(packageName != null && !"".equals(packageName)){
								fout.write(("package " + packageName + ";\n\n").getBytes());
							}
							
							fout.write(action.getCode().getBytes("utf-8"));
							
						}finally{
							fout.close();
						}
					}
					
					//int dotIndex = action.className.lastIndexOf(".");			
					//String compileClassName = action.className.substring(dotIndex + 1, action.className.length());
					//compiler.compile(file);
					try{
						compiler.compile(codeFile);
						//log.info("compile groovy " + action.getThing().getMetadata().getPath());
						action.updateCompileTime();					
					}catch(MultipleCompilationErrorsException me){
						//log.error("compile groovy code : " + action.getThing().getMetadata().getPath() + ", " + me.getMessage());
						throw me;
					}
				}
			}
			action.setChanged(false);
		}
		
		if(action.getActionClass() == null){
			Thing actionThing = action.getThing();
			String className = actionThing.getStringBlankAsNull("outterClassName");
			if(className != null){
				action.setActionClass(action.getClassLoader().loadClass(className));
			}else{			
				className = actionThing.getStringBlankAsNull("innerClassName");
				if(className != null){
					action.setActionClass(action.getClassLoader().loadClass(className));
				}else{
					action.setActionClass(action.getClassLoader().loadClass(action.getClassName()));	
				}
			}
			
			//java.lang.Compiler.compileClass(action.actionClass);
		}
				
		if(action.getActionClass()  != null){		
			Script script = (Script) action.getActionClass().getConstructor(new Class<?>[0]) .newInstance();//(Script) action.getData("script");

			Bindings bindings1 = context.push(null);			
			bindings1.put("actionContext", context);
			
			try{
				context.pushAction(action);
				
				//创建变量，已经内置到Action中了，再执行会执行两遍
				/*
				for(Thing variables : action.getThing().getChilds("Variables")) {
					for(Thing child : variables.getChilds()) {
						bindings1.put(child.getMetadata().getName(), child.getAction().run(context));
					}
				}*/
				//取原始的self调用者
				//if(context.getScopesSize() >= 5){
				//	Bindings callerBindings = context.getScope(context.getScopesSize() - 5);
				//	Object self = callerBindings.get("self");
				//	bindings1.put("self", self);
				//}
				
				
				//代码辅助
				Binding binding = new Binding(context);
				script.setBinding(binding);				
				Object result = script.run();			
				return result;
			}finally{
				//bindings1.remove("self");
				context.popAction();
				bindings1.remove("actionContext");
				
				Bindings varBindings = UtilAction.getVarScope(action.getThing(), context);
				if(varBindings != null){
					varBindings.putAll(bindings1);
				}
				
				context.pop();								
			}
		}else{
			throw new ActionException("groovy action " + action.getThing().getMetadata().getPath() + " class is null");
		}		
	}
	
	public static Object run(ActionContext context) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{				
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
		}
		
		//if(action == null){
		//	log.error("er");
		//}
		//log.info("run groovy action : " + action.thing.getMetadata().getPath());
		
		return run(action, context);
	}
}