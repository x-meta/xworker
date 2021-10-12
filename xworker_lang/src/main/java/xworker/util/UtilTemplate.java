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
package xworker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
//import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.runtime.resource.loader.StringResourceLoader;

public class UtilTemplate {
	
	public static final String FREEMARKER = "freemarker";
	public static final String VELOCITY = "velocity";
	static Map<String, Configuration> freemarkerHandlers = new HashMap<String, Configuration>();	
	
	public static freemarker.cache.StringTemplateLoader freemarkerStringLoader = new freemarker.cache.StringTemplateLoader();	
	static Map<String, ThingEntry> stringThingCache = new HashMap<String, ThingEntry>();	
		
	public static String processString(Object object, String templateCode) throws IOException, TemplateException{	
		return processString(object, "", templateCode);
	}
	
	public static String processString(Object object, String name, String templateCode) throws IOException, TemplateException{
		if(templateCode == null){
			return null;
		}
		
		ByteArrayInputStream bin = new ByteArrayInputStream(templateCode.getBytes());
		InputStreamReader reader = new InputStreamReader(bin);
		
		Template template = new Template(name, reader, null);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();   
    	template.process(object,new OutputStreamWriter(out, StandardCharsets.UTF_8));
		return out.toString("utf-8");
            //替换变量里的值，允许设置如${name}形式的配置参数值
		/*String value = templateCode;
        if(templateCode.indexOf("${") != -1){
        	int index = 0;
        	int index1 = -1;
        	String temp = templateCode;
        	StringBuffer v = new StringBuffer();
        	while(true){
        		index = temp.indexOf("${");
        		index1 = temp.indexOf("}");
        		if(index == -1 || index1 == -1){
        			v.append(temp);
        			break;
        		}
        		
        		v.append(temp.substring(0, index));
        		String vName = temp.substring(index + 2, index1);
        		try {
					v.append(OgnlUtil.getValue(vName, object));
				} catch (OgnlException e) {
					log.warn("data not found:" + vName);
					//e.printStackTrace();
				}
        		temp = temp.substring(index1 + 1, temp.length());
        	}
        	value = v.toString();            	
        }
		return value;*/
	}
	
	/**
	 * 处理模版，返回字符串结果。
	 * 
	 * @param object
	 * @param templateFile
	 * @param templateType 如果模板是数据对象模板，此参数无效
	 * @return
	 * @throws Throwable 
	 */
	public static String process(Object object, String templateFile, String templateType) throws Throwable{
		ByteArrayOutputStream out = new ByteArrayOutputStream();        
        try {
			process(object, templateFile, templateType, new OutputStreamWriter(out, "UTF-8"));
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {			
		}                
		return null;
	}
	
	public static String process(Object object, String templateFile, String templateType, String templateEncoding) throws Throwable{
		ByteArrayOutputStream out = new ByteArrayOutputStream();        
        try {
			process(object, templateFile, templateType, new OutputStreamWriter(out, "UTF-8"), templateEncoding);
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {			
		}                
		return null;
	}
	
	/**
	 * 模板是事物的属性，处理模板然后返回生成的字符串。
	 */
	public static String processThingAttributeToString(Thing thing, String attributeName, Object root) throws IOException, TemplateException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();   
		processThingAttribute(thing, attributeName, root, new OutputStreamWriter(out), null);
		return out.toString();
	}
	
	/**
	 * 模板是事物的属性，会把模板对象放到事物的缓存中已提高性能。使用Freemakrer模板。
	 */
	public static void processThingAttribute(Thing thing, String attributeName, Object root, Writer out, String encoding) throws IOException, TemplateException{
		String key = "__utilTemplate-" + attributeName + "__";
		TemplateEntry templateEntry = (TemplateEntry) thing.getData(key);
		
		//模板不存在或者事物已变成，重新生成模板
		if(templateEntry == null || (templateEntry != null && templateEntry.lastmodified != thing.getMetadata().getLastModified())){
			if(templateEntry == null){
				templateEntry = new TemplateEntry();
			}
			
			String templateCode = thing.getString(attributeName);
			if(templateCode == null){
				templateCode = "";
			}
			
			//创建模板
			ByteArrayInputStream bin = new ByteArrayInputStream(templateCode.getBytes());
			InputStreamReader reader = new InputStreamReader(bin);
			templateEntry.template = new Template(thing.getMetadata().getPath() + ":" + attributeName, reader, freemarkerHandlers.get(""));
			if(encoding != null && !"".equals(encoding)){
				templateEntry.template.setEncoding(encoding);
			}
			
			templateEntry.lastmodified = thing.getMetadata().getLastModified();
		}
		
		templateEntry.template.process(root, out);
	}
	
	/**
	 * 从缓存中移除模板。
	 */
	public static void removeTempalteCache(String templateName) throws IOException{
		Configuration cfg = (Configuration) freemarkerHandlers.get("");			
		if(cfg != null){
			//cfg.removeTemplateFromCache(templateName);
		}
	}
	
	/**
	 * 执行模板。
	 * 
	 * @param object  模板的数据上下文
	 * @param templateFile  模板名称
	 * @param templateType  模板类型，freemarker或velocity
	 * @param outputWriter  输出
	 * @param encoding      编码
	 * @throws Throwable
	 */
	public static void process(Object object, String templateFile, String templateType, Writer outputWriter, String encoding) throws Throwable{
		String[] ts = templateFile.split("[:]");
		String templateConfigName = "";
		String templateName = "";
		
		boolean isStringResource = false;
		if(ts.length == 1){
			templateName = ts[0];
		}else if(ts.length == 2){
			isStringResource = true;
			
			World dataCenter = World.getInstance();
			Thing templateThing = dataCenter.getThing(ts[1]);
			
			String t = templateFile.replaceAll(":/@", ".");
			//System.out.println(t);
			t = t.replaceAll(":/", ".");
			//System.out.println(t);
			t = t.replaceAll(":", ".");
			//System.out.println(t);
			t = t.replaceAll("/@", ".");
			//System.out.println(t);
			t = t.replaceAll("@", ".");
			//System.out.println(t);
			t = t.replaceAll("[.]","_");
			//System.out.println(t);
			templateName = t;

			ThingEntry entry = stringThingCache.get(templateThing.getMetadata().getPath());
			if(entry == null || entry.isChanged()){
				String templateCode = templateThing.getString("templateCode");
				stringThingCache.put(templateThing.getMetadata().getPath(), new ThingEntry(templateThing));
				if("freemarker".equals(templateType)){
					freemarkerStringLoader.putTemplate(templateName, templateCode);
				}else if("velocity".equals(templateType)){
					VelocityTemplate.putStringResource(templateName, templateCode);
				}
			}
		}
		
		if("freemarker".equals(templateType)){
			Configuration cfg = (Configuration) freemarkerHandlers.get(templateConfigName);			
			Template template = null;
			if(cfg == null){
				
				String rootDir = World.getInstance().getPath();
				
				cfg = new Configuration(Configuration.VERSION_2_3_28);				
				cfg.setDateFormat("yyyy-MM-dd");
				cfg.setNumberFormat("#");				
				
				cfg.setDirectoryForTemplateLoading(new File(rootDir));	
				FileTemplateLoader fileLoader = new FileTemplateLoader(new File(rootDir));	
				
				MultiTemplateLoader loader = new MultiTemplateLoader(new TemplateLoader[]{fileLoader, new XWorkerFreemarkerClassTemplateLoader(), freemarkerStringLoader});					
				cfg.setTemplateLoader(loader);
				
				freemarkerHandlers.put(templateConfigName, cfg);
			}			
			
			if(encoding != null){
				template = cfg.getTemplate(templateName, encoding);
			}else{
				template = cfg.getTemplate(templateName);
			}
			if(encoding != null && !"".equals(encoding))
				template.setEncoding(encoding);
			template.process(object, outputWriter);
			outputWriter.flush();
		}else if("velocity".equals(templateType)){
			VelocityTemplate.process(templateConfigName, isStringResource, templateName, encoding, object, outputWriter);
		}else{
			outputWriter.write("格式暂不支持：" + templateType);
		}
	}
	
	/**
	 * 处理模版，内容使用Writer写入。
	 */
	public static void process(Object object, String templateFile, String templateType, Writer outputWriter) throws Throwable{
		process(object, templateFile, templateType, outputWriter, null);
	}
	
	/**
	 * 指定数据对象的路径、模版文件名和模版类型处理模版，输出到Writer中。
	 */
	public static void processThing(String dataObjectName, String templateFile, String templateType, Writer outputWriter) throws Throwable{
		World dataCenter = World.getInstance();
		process(dataCenter.getThing(dataObjectName), templateFile, templateType, outputWriter);
	}
	
	public static void processThing(Thing dataSource, String templateFile, String templateType, Writer outputWriter) throws Throwable{
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("dataObject", dataSource);
		data.put("dataCenter", World.getInstance());	
		process(data, templateFile, templateType, outputWriter);
	}
		
	public static String processThing(String dataObjectName, String templateFile, String templateType) throws UnsupportedEncodingException, Throwable{
		World dataCenter = World.getInstance();
		return processThing(dataCenter.getThing(dataObjectName), templateFile, templateType);
	}
	
	public static String processThing(Thing dataSource, String templateFile, String templateType) throws UnsupportedEncodingException, Throwable{
		ByteArrayOutputStream out = new ByteArrayOutputStream();        
		processThing(dataSource, templateFile, templateType, new OutputStreamWriter(out, "UTF-8"));
		return out.toString("UTF-8");
	}	
	
	public static void main(String args[]){
		try{
			//测试缓存和非缓存的性能
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("name", "123");
			
			String templateCode = "${name}";
			
			int count = 10000;
			
			long start = System.currentTimeMillis();
			for(int i=0; i<count; i++){
				ByteArrayInputStream bin = new ByteArrayInputStream(templateCode.getBytes());
				InputStreamReader reader = new InputStreamReader(bin);
				
				Template template = new Template("abc", reader, null);
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();   
		    	template.process(root, new OutputStreamWriter(out));
			}
			System.out.println("do " + count + " template time: " + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			ByteArrayInputStream bin = new ByteArrayInputStream(templateCode.getBytes());
			InputStreamReader reader = new InputStreamReader(bin);
			Template template = new Template("abc", reader, null);
			for(int i=0; i<count; i++){
				ByteArrayOutputStream out = new ByteArrayOutputStream();   
		    	template.process(root, new OutputStreamWriter(out));
			}
			System.out.println("do " + count + " cache template time: " + (System.currentTimeMillis() - start));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}