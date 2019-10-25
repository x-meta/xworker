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
package xworker.lang.text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

import xworker.util.UtilTemplate;

public class TextTemplateActions {
	public static Object run(ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("self");
		
		String templateName = self.getString("templatePath");
		if(templateName == null || "".equals(templateName)){
		    //如果路径为空，那么使用templateCode所定义的模板
		    templateName = "thing:" + self.getMetadata().getPath();
		}

		//模板的上下文
		Object templateContext = null;
		String contextName = self.getString("contextName");
		if(contextName == null || "".equals(contextName)){
		    if("velocity".equals(self.getString("type"))){
		        templateContext = new VelocityContext(actionContext);
		    }else{
		        templateContext = actionContext;
		    }
		}else{
		    templateContext = actionContext.get(contextName);
		}

		//设置输出
		Object output = null;
		FileOutputStream fileOut = null;
		try{
		    //设置输出编码
		    String outputEncoding = self.getString("outputEncoding");
		    if(outputEncoding == null || outputEncoding == ""){
		        outputEncoding = "UTF-8";
		    }
		    
		    String selfOutput = self.getString("output");
		    if(selfOutput != null && !"".equals(selfOutput)){
		        output = actionContext.get(selfOutput);
		        if(output instanceof java.io.OutputStream){
		            output = new OutputStreamWriter((OutputStream) output, outputEncoding);
		        }else if(output instanceof OutputStreamWriter){
		        }else if(output instanceof String || output instanceof File){	
		        	if(output instanceof String){
		        		fileOut = new FileOutputStream((String) output);
		        	}else{
		        		fileOut = new FileOutputStream((File) output);
		        	}
		            output = new OutputStreamWriter(fileOut, outputEncoding);
		        }else{
		            if(self.getString("output").startsWith("\"")){
		                //""包围的字符串常量，当作文件路径
		                String fileName = selfOutput.substring(1, selfOutput.length() - 1);
		                File f = new File(fileName);
		                if(!f.exists()){
		                    f.getParentFile().mkdirs();
		                }
		                fileOut = new FileOutputStream(fileName);
		                output = new OutputStreamWriter(fileOut, outputEncoding);
		            }            
		        }
		    }
		    
		    boolean isString = false;
		    ByteArrayOutputStream out = null;
		    if(output == null){
		        out = new ByteArrayOutputStream();		        
		        if(outputEncoding != null && !"".equals(outputEncoding))
		            output = new OutputStreamWriter(out, (String) outputEncoding);
		        else 
		            output = new OutputStreamWriter(out);
		        isString = true;
		    }

		    //log.info("templateName=" + templateName);
		    try{
		    	UtilTemplate.process(templateContext, templateName, self.getString("type"), (Writer) output, self.getString("templateEncoding"));
		    }catch(Exception e){
		    	throw new ActionException("process freemarker error, TextTemplate=" + self.getMetadata().getPath(), e);
		    }
		    if(isString){
		        //String result = out.toString();
		        String result = new String(out.toByteArray(), outputEncoding);
		        //log.info(result);
		        String outputVarName = self.getString("outputVarName");
		        
		        if(outputVarName != null && !"".equals(outputVarName)){
		        	UtilAction.putVarByActioScope(self, outputVarName, result, actionContext);

		            return result;
		        }else{
		            return result;
		        }
		    }
		    
		    return "";
		}finally{
		    if(fileOut != null) fileOut.close();
		}
	}
}