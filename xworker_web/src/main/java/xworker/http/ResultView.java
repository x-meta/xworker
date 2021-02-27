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
package xworker.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import xworker.lang.executor.Executor;
import xworker.util.UtilTemplate;

/**
 * HttpControl的Result和一些界面处理。
 *  
 * @author <a href="mailto:zhangyuxiang@tom.com">zyx</a>
 *
 */
public class ResultView {
	private static final String TAG = ResultView.class.getName();
	
	public static void doResult(ActionContext actionContext) throws IOException, TemplateException{		
		//long start = System.currentTimeMillis();
		Thing resultObject = (Thing) actionContext.get("self");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        
        //取得子区域的定义，处理区域
        List<Thing> regions = resultObject.getChilds("region");
        if(regions != null){
            for(int k=0; k<regions.size(); k++){
                Thing region = (Thing) regions.get(k);
                
                //替换region的变量
                String name = region.getString("name");
                actionContext.put(name, region.doAction("doRegion", actionContext));
            }
        }
                
        String contentType = resultObject.getString("contentType");
        if(contentType.indexOf("${") != -1){
        	contentType = UtilTemplate.processThingAttributeToString(resultObject, "contentType", actionContext);
        }
        if(contentType != null && !"".equals(contentType)){
          	response.setContentType(contentType);
        }
                       
        //执行结果
        String type = resultObject.getString("type"); 
        if(type.indexOf("${") != -1){
        	//type = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "type", type);
        	type = UtilTemplate.processThingAttributeToString(resultObject, "type", actionContext);
        }
        resultObject.doAction(type, actionContext);
        //System.out.println("do result : " + (System.currentTimeMillis() - start));
	}
	
	public static void freemarker(ActionContext actionContext) throws Throwable{		
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		if(path == null || "".equals(path)){
			Executor.warn(TAG, "Freemarker template not found:" + path);
			return;
		}
		
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		UtilTemplate.process(actionContext, path, "freemarker", new OutputStreamWriter(response.getOutputStream(), "UTF-8"), "UTF-8");
	}
	
	public static void dataSet(ActionContext actionContext) throws Throwable{
		
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		
		int index = path.indexOf(":");
		String type = path.substring(0, index);
		String modelPath = path.substring(index + 1, path.length());
		Thing model = World.getInstance().getThing(modelPath);
		
		Long formLastModified = (Long) model.getData("_form_LastModified_" + type);
		String templateName = (String) model.getData("_form_TemplateName_" + type);	
		
		//File f = (File) formObject.getData("_form_file");
		if(templateName == null || formLastModified < model.getMetadata().getLastModified()){
	        //取表单的临时文件名
			StringBuffer fileNameBuff = new StringBuffer(World.getInstance().getPath());		
			StringBuffer tName = new StringBuffer();
			tName.append("/work/forms");
			tName.append("/" + UtilString.getThingManagerFileName(model));
			tName.append("/" + model.getRoot().getMetadata().getPath().hashCode());
			tName.append("/" + model.getMetadata().getPath().hashCode());
			tName.append("/" + model.getMetadata().getName());
			tName.append("_" + type);
			tName.append(".ftl");
			String fileName = fileNameBuff.append(tName).toString();
					
			File file = new File(fileName);
			if(!file.exists() || file.lastModified() < model.getMetadata().getLastModified()){
				if(!file.exists()){				
					File dir = file.getParentFile();
					if(!dir.exists() || !dir.isDirectory()){
						dir.mkdirs();
					}
				}
				try{
					//List formElements = UtilView.
					//System.out.println("org.xmeta.util.UtilForm:114 re formed");
					Thing formThing = (Thing) model.doAction(type, actionContext); 
					String ftl = (String) formThing.doAction("toHtml", actionContext);
					OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
					w.write(ftl);
					w.flush();
					w.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
						
			templateName = tName.toString();
			model.setData("_form_LastModified_" + type, model.getMetadata().getLastModified());
			model.setData("_form_TemplateName_" + type, templateName);
		}
		
		try{
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			UtilTemplate.process(actionContext, templateName, "freemarker", new OutputStreamWriter(response.getOutputStream(), "UTF-8"), "UTF-8");
		}catch(Throwable t){
			model.setData("_form_LastModified_" + type, null);
			model.setData("_form_TemplateName_" + type, null);
			
			Executor.error(TAG, "template error : " + templateName);
			
			throw t;
		}
	}
	
	public static void form(ActionContext actionContext) throws Throwable{
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		Thing formObject = null;
		if(path.startsWith("$")){
			path = path.substring(1, path.length());
			Thing parent = resultObject.getParent();
			for(Thing child : parent.getAllChilds()){
				if(child.isThingByName("view") && path.equals(child.getString("name"))){
					formObject = child;
					break;
				}
			}
		}else{
			formObject = World.getInstance().getThing(path);
		}		
		if(formObject == null){
			Executor.warn(TAG, "form thing is not found : " + resultObject.getString("value"));
			return;
		}
		
		processViewThing(formObject, actionContext);
	}
	
	/**
	 * 处理表单事物生成Freemaker模板，并返回模板的路径。
	 * 
	 * @param formObject
	 * @param actionContext
	 * @return
	 * @throws Throwable
	 */
	public static String processViewThingTemplate(Thing formObject, ActionContext actionContext) throws Throwable{
		Long formLastModified = (Long) formObject.getData("_form_LastModified_");
		String templateName = (String) formObject.getData("_form_TemplateName_");	
		//File f = (File) formObject.getData("_form_file");
		if(formLastModified == null || templateName == null || formLastModified < formObject.getMetadata().getLastModified()){
	        //取表单的临时文件名
			StringBuffer fileNameBuff = new StringBuffer(World.getInstance().getPath());		
			StringBuffer tName = new StringBuffer();
			tName.append("/work/forms");
			tName.append("/" + UtilString.getThingManagerFileName(formObject));
			tName.append("/" + formObject.getRoot().getMetadata().getPath().replace('.', '/'));
			tName.append("/" + formObject.getMetadata().getPath().hashCode());
			tName.append("/" + formObject.getMetadata().getName());
			tName.append(".ftl");
			String fileName = fileNameBuff.append(tName).toString();
					
			File file = new File(fileName);
			if(!file.exists() || file.lastModified() < formObject.getMetadata().getLastModified()){
				if(!file.exists()){				
					File dir = file.getParentFile();
					if(!dir.exists() || !dir.isDirectory()){
						dir.mkdirs();
					}
				}
				try{
					//List formElements = UtilView.
					//System.out.println("org.xmeta.util.UtilForm:114 re formed");
					String ftl = (String) formObject.doAction("toHtml", actionContext);
					OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
					w.write(ftl);
					w.flush();
					w.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
						
			templateName = tName.toString();			
			//formObject.setData("_form_file", file);
			formObject.setData("_form_LastModified_", formObject.getMetadata().getLastModified());
			formObject.setData("_form_TemplateName_", templateName);
			
			UtilTemplate.removeTempalteCache(templateName);
		}
		
		return templateName;
	}
	
	public static String processViewThingToString(Thing formObject, ActionContext actionContext) throws Throwable{
		String templateName = processViewThingTemplate(formObject, actionContext);
		
		try{
			return UtilTemplate.process(actionContext, templateName, "freemarker","UTF-8");
		}catch(Throwable t){
			formObject.setData("_form_LastModified_", null);
			formObject.setData("_form_TemplateName_", null);
			
			throw t;
		}
	}
	
	public static void processViewThing(Thing formObject, ActionContext actionContext) throws Throwable{
		String templateName = processViewThingTemplate(formObject, actionContext);
		
		try{
//			if(formObject.getBoolean("noCache")){
//				UtilTemplate.removeTempalteCache(templateName);
//			}
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			UtilTemplate.process(actionContext, templateName, "freemarker", new OutputStreamWriter(response.getOutputStream(), "UTF-8"), "UTF-8");
		}catch(Throwable t){
			formObject.setData("_form_LastModified_", null);
			formObject.setData("_form_TemplateName_", null);
			
			throw t;
		}
	}
	
	public static String doRegion(ActionContext actionContext) throws IOException{
		Thing region = (Thing) actionContext.get("self");
		String type = region.getString("type");        
        
		Bindings bindings = actionContext.push(null);
		try{
			FakeHttpResponese response = new FakeHttpResponese();
			
			bindings.put("response", response);
			region.doAction(type, actionContext);
			
			response.getOutputStream().flush();
			String str = new String(((FakeServletOutputStream) response.getOutputStream()).getBytes(), "UTF-8");
			return str;
		}finally{
			actionContext.pop();			
		}
	}
}