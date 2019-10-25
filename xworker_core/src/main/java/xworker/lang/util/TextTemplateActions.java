/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.lang.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.velocity.VelocityContext;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.util.UtilTemplate;

public class TextTemplateActions {
    public static Object run(ActionContext actionContext) throws Throwable{
        Thing self = actionContext.getObject("self");
        
        String templateName = self.getStringBlankAsNull("templatePath");        
        if(templateName == null){
            //如果路径为空，那么使用templateCode所定义的模板
            templateName = "thing:" + self.getMetadata().getPath();
        }
        
        //模板的上下文
        Object templateContext = null;
        String contextName = self.getStringBlankAsNull("contextName");
        if(contextName == null){
            if("velocity".equals(self.getString("type"))){
                templateContext = new VelocityContext(actionContext);
            }else{
                templateContext = actionContext;
            }
        }else{
            templateContext = actionContext.get(self.getString("contextName"));
        }
        
        //设置输出
        Object output = null;
        FileOutputStream fileOut = null;
        try{
            //设置输出编码
            String outputEncoding = self.getStringBlankAsNull("outputEncoding");
            if(outputEncoding == null){
                outputEncoding = "UTF-8";
            }
            
            String soutput = self.getStringBlankAsNull("output");
            if(soutput != null && soutput != ""){
                output = actionContext.get(soutput);
                if(output instanceof java.io.OutputStream){
                    output = new OutputStreamWriter((OutputStream) output, (String) outputEncoding);
                }else if(output instanceof OutputStreamWriter){
                }else if(output instanceof String){
                	if(output instanceof File){
                		fileOut = new FileOutputStream((File) output);
                	}else{
                		fileOut = new FileOutputStream((String) output);
                	}
                    output = new OutputStreamWriter(fileOut, outputEncoding);
                }else{
                    if(soutput.startsWith("\"")){
                        //""包围的字符串常量，当作文件路径
                        String fileName = soutput.substring(1, soutput.length() - 1);
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
                if(outputEncoding != null){
                    output = new OutputStreamWriter(out, outputEncoding);
                }
                isString = true;
            }
        
            //log.info("templateName=" + templateName);
            UtilTemplate.process(templateContext, templateName, self.getString("type"), (OutputStreamWriter) output, self.getString("templateEncoding"));
            if(isString){
                //String result = out.toString();
                String result = new String(out.toByteArray(), outputEncoding);
                //log.info(result);
                String outputVarName = self.getStringBlankAsNull("outputVarName");
                if(outputVarName != null){
                    String varScope = self.getString("varScope");
                    if("Global".equals(varScope) || varScope == null){
                    	actionContext.getScope(0).put(outputVarName, result);
                    }else{
                        try{
                            int scopeIndex = Integer.parseInt(varScope);
                            if(scopeIndex >= 0){
                            	actionContext.getScope(scopeIndex).put(outputVarName, result);
                            }else{
                             	actionContext.getScope(actionContext.getScopesSize() + scopeIndex).put(outputVarName, result);
                            }
                        }catch(Exception e){
                            Bindings actionBindings = actionContext.getScope(varScope);
                            if(actionBindings != null){
                                actionBindings.put(outputVarName, result);
                            }else{
                                actionContext.put(outputVarName, result);
                            }
                        }
                    }
        
                    return result;
                }else{
                    return result;
                }
            }
            
            return "";
        }finally{
            if(fileOut != null)
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
    }

    public static Object process(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return self.doAction("run", actionContext);
    }

}