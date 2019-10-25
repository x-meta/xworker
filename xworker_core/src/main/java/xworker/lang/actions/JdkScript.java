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
package xworker.lang.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class JdkScript {
	private static final String scriptCache = "__jdkscript__scirpt_cache__";
	private static final String scriptCacheTime = "__jdkscript__scirpt_cache_time__";
	static World world = World.getInstance();

	/** 脚本管理器 */
	static ScriptEngineManager scriptEngineManager = new ScriptEngineManager(
			world.getClassLoader());

	public static Object run(ActionContext actionContext) throws Exception {
		// 获取脚本本身
		Bindings bindings = actionContext.getScope(actionContext.getScopesSize() - 2);
		Thing self = null;
		Action action = null;
		if (bindings.getCaller() instanceof Thing) {
			self = (Thing) bindings.getCaller();
			action = self.getAction();
		} else {
			action = (Action) bindings.getCaller();
			self = action.getThing();
		}

		// 首先看是否有已经编译好的脚本
		String codeFilePath = null;
		CompiledScript script = (CompiledScript) self.getData(scriptCache);
		//脚本不存在或有修改过需要重新生成
		Long cacheTime = (Long)  self.getData(scriptCacheTime);
		if (script == null || cacheTime == null || cacheTime != self.getMetadata().getLastModified()) {
			ScriptEngine engine = scriptEngineManager.getEngineByName((String) self.doAction("getScriptEngine", actionContext));
			if(engine == null){
				throw new ActionException("Script engine not exists, language=" + self.getString("language") + ",self=" + self);
			}
			
			codeFilePath = self.getStringBlankAsNull("scriptFilePath");
			if(codeFilePath == null){
				//保存code到文件
				String fileExt = (String) self.doAction("getFileExt", actionContext);
				File codeFile = new File(action.fileName.replaceFirst("actionSources", "scripts") + "." + fileExt);
				codeFilePath = codeFile.getAbsolutePath();
				if(!codeFile.exists()){
					codeFile.getParentFile().mkdirs();
				}
				
				FileOutputStream fout = new FileOutputStream(codeFile);
				try{					
					String code = action.getThing().getString("code");
					if(code != null){
						fout.write(code.getBytes());
					}
				}finally{
					fout.close();
				}
			}
			
			//创建Script
			if(engine instanceof Compilable){
				//编译并保存到缓存中
				Compilable compilable = (Compilable) engine;
				FileReader fileReader = new FileReader(new File(codeFilePath));
				try{
					script = compilable.compile(fileReader);
					self.setData(scriptCache, script);
					self.setData(scriptCacheTime, self.getMetadata().getLastModified());
				}finally{
					fileReader.close();
				}
			}
		}
		
		//执行脚本
		JdkScriptBindings scriptBindings = new JdkScriptBindings(actionContext);
		Object result = null;
		if(script != null){
			result = script.eval(scriptBindings);
		}else{
			FileReader fileReader = new FileReader(new File(codeFilePath));
			try{
				ScriptEngine engine = (ScriptEngine) self.doAction("getScriptEngine", actionContext);
				if(engine == null){
					throw new ActionException("Script engine not exists, language=" + self.getString("language") + ",self=" + self);
				}
				
				result = engine.eval(fileReader, scriptBindings);
			}finally{
				fileReader.close();
			}
		}
		
		//放入变量，已无效，用户自行管理变量
		/*
		Bindings scriptGloableBindings = scriptBindings.actionContext.getScope(0);
		if(scriptGloableBindings.size() > 0){
			Bindings scopeBindinds = UtilAction.getVarScope(self, actionContext);
			if(scopeBindinds != null){
				scopeBindinds.putAll(scriptGloableBindings);
			}
		}*/
		
		//返回值
		return result;		
	}

	/**
	 * 获取脚本引擎。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getScriptEngine(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getString("language");		
	}
	
	/**
	 * 获取脚本的文件名后缀。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getFileExt(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return self.getStringBlankAsNull("fileExt");
	}
}