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
package xworker.lang.actions.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import xworker.lang.executor.Executor;
import xworker.util.UtilTemplate;

public class CodeGeneratorActions {
	private static final String TAG = CodeGeneratorActions.class.getName();
	//private static Logger log = LoggerFactory.getLogger(CodeGeneratorActions.class);
	
	/**
	 * 获取文件路径。
	 * 
	 * @param actionContext
	 * @return
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	public static String getFilePath(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		
		String path = UtilString.getString(self, "filePath", actionContext);
		if(path != null){
			return UtilTemplate.processString(actionContext, path);
		}else{
			return null;
		}
	}
	
	/**
	 * 生成模板。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void run(ActionContext actionContext) throws UnsupportedEncodingException, IOException{
		Thing self = (Thing) actionContext.get("self");
		
		//类型
		String type = self.getString("type");
		boolean dolog = self.getBoolean("log");
		//文件
		String filePath = (String) self.doAction("getFilePath", actionContext);
		
		if(dolog){
			Executor.info(TAG, "CodeGenerator: before generate file path=" + filePath);
		}
		
		//内容列表
		Thing contents = self.getThing("Contents@0");
		if(contents == null || contents.getChilds().size() == 0){
			Executor.warn(TAG, "CodeGenerator: Contents not setted, path=" + self.getMetadata().getPath());
			return;
		}
		
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		
		String fileEncoding = UtilString.getString(self, "fileEncoding", actionContext);
		
		if("once".equals(type)){
			if(file.exists()){
				if(dolog){
					Executor.info(TAG, "CodeGenerator: file already exists , do not generate again, filePath=" + filePath);
				}
				return;
			}else{
				Thing contentThing = contents.getChilds().get(0);
				
				generateFile(contentThing, file, fileEncoding, actionContext);
			}
		}else if("overwrite".equals(type)){
			Thing contentThing = contents.getChilds().get(0);
			
			generateFile(contentThing, file, fileEncoding, actionContext);
		}else if("modify".equals(type)){
			if(!file.exists()){
				if(dolog){
					Executor.info(TAG, "CodeGenerator: file not exists ,not modify");
				}
				
				return;
			}else{
				modifyFile(contents, file, fileEncoding, actionContext);
			}
		}else{
			Executor.warn(TAG, "CodeGenerator: unkonwn type '" + type + "', path=" + self.getMetadata().getPath());
			return;
		}
		
		if(dolog){
			Executor.info(TAG, "CodeGenerator: file generated path=" + filePath);
		}
	}
	
	public static void modifyFile(Thing contents, File file, String charset, ActionContext actionContext) throws IOException{
		FileInputStream fin = new FileInputStream(file);
		String fileContent = null;
		try{
			byte[] bytes = new byte[(int) file.length()];
			fin.read(bytes);
			if(charset != null && !"".equals(charset)){
				fileContent = new String(bytes, charset);
			}else{
				fileContent = new String(bytes);
			}
		}finally{
			fin.close();
		}
		
		for(Thing contentThing : contents.getChilds()){
			String content = (String) contentThing.doAction("toString", actionContext);
			if(content == null){
				Executor.warn(TAG, "CodeGenerator: content is null, contentPath=" + contentThing.getMetadata().getPath());
				continue;
			}
			
			String name = "$$" + contentThing.getMetadata().getName() + "$$";
			int index = fileContent.indexOf(name);
			if(index != -1){
				int index2 = fileContent.indexOf(name, index + name.length());
				
				if(index != -1 && index2 != -1){
					fileContent = fileContent.substring(0, index + name.length()) + 	
							 content + 
							 fileContent.substring(index2 + name.length(), fileContent.length());
				}
			}
		}
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			if(charset == null || "".equals(charset)){
				fout.write(fileContent.getBytes());
			}else{
				fout.write(fileContent.getBytes(charset));
			}
		}finally{
			fout.close();
		}
	}
	
	public static void generateFile(Thing contentThing, File file, String charset, ActionContext actionContext) throws UnsupportedEncodingException, IOException{
		String content = (String) contentThing.doAction("toString", actionContext);
		if(content == null){
			Executor.warn(TAG, "CodeGenerator: content is null, contentPath=" + contentThing.getMetadata().getPath());
		}
		
		FileOutputStream fout = new FileOutputStream(file);
		try{
			if(charset == null || "".equals(charset)){
				fout.write(content.getBytes());
			}else{
				fout.write(content.getBytes(charset));
			}
		}finally{
			fout.close();
		}
	}
	
	public static String freemarkerToString(ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("self");
		
		String templatePath = self.getString("templatePath");
		if(templatePath != null && !"".equals(templatePath)){
			return UtilTemplate.process(actionContext, templatePath, "freemarker");
		}else{
			return UtilTemplate.processThingAttributeToString(self, "code", actionContext);
		}
	}
}