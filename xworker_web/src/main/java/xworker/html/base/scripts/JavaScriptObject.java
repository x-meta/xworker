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
package xworker.html.base.scripts;

import java.io.IOException;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import xworker.html.HtmlActions;

public class JavaScriptObject {
	//private static Logger logger = LoggerFactory.getLogger(JavaScriptObject.class);
	
	@SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String varref = self.getString("varref");
        if(varref != null && !"".equals(varref)){
            return varref;
        }
        
        //缩进
        String ident = (String) actionContext.get("ident");
        if(ident == null || self.getBoolean("varglobal")){
            ident = "    ";
        }
        String myident = ident + "    ";
        
        String scriptCode = "";
        //是否有头和尾
        for(Thing descriptor : self.getAllDescriptors()){
        	if(descriptor.isThingByName("JavaScriptObjectType")){
        		String htmlKey = descriptor.getString("htmlKey");
        		String headers = descriptor.getString("headers");
        		String bottoms = descriptor.getString("bottoms");
        		HtmlActions.addHead(self, htmlKey, headers, actionContext);
        		HtmlActions.addBottom(htmlKey, bottoms, actionContext);
        	}
        }
        
        //显示属性
        for(Thing descriptor : self.getAllAttributesDescriptors()){ 
        	String path = descriptor.getMetadata().getPath();
            if(path.startsWith("xworker.lang.MetaThing") || path.startsWith("xworker.html.base.scripts.JavaScriptObject")
            		|| path.startsWith("xworker.lang.MetaDescriptor3")){
                //事物的定义的属性不包括
                continue;
            }
            
            String name = descriptor.getString("name");
            String value = self.getString(descriptor.getString("name"));
            
            //logger.info("name=" + name + ", path=" + path);
        
            if(value != null &&  !"".equals(value) && !value.equals(descriptor.getString("default"))){
                
                int index = 0;
                for(String line : value.split("[\n]")){
                	if(index == 0){
                		scriptCode = scriptCode + ",\n" + myident + name + ": " + line;
                		index++;
                	}else{
                		scriptCode = scriptCode + "\n" + myident + "    " + line;
                	}
                }
            }
        }
        
        //显示子节点
        for(Thing child : self.getChilds()){    
            String childScriptCode = null;
            String cvarref = child.getString("varref");
            if(cvarref != null && !"".equals(cvarref)){
                childScriptCode = cvarref;
            }else{                
                if(!child.isThingByName("JavaScriptFunction")){
                	childScriptCode = (String) child.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
	                String type = (String) child.doAction("getJavaScriptObjectType", actionContext);
	                childScriptCode = "new " + type + "(" + childScriptCode + ")";
                }else{
                	String params = child.getString("params");
                	childScriptCode = "function(" + params + "){";
            		childScriptCode = childScriptCode + JavaScript.getJavaScriptCode(child, actionContext);
            		childScriptCode = childScriptCode + "\n}";
                }
            }
            
            if(childScriptCode != null && childScriptCode != ""){
                int index = 0;
                for(String line : childScriptCode.split("[\n]")){
                	if(index == 0){
                		scriptCode = scriptCode + ",\n" + myident +  child.getString("name")  + ": " + line;
                		index++;
                	}else{
                		scriptCode = scriptCode + "\n" + myident + "    " + line;
                	}
                }
            }
        }
        
        if(!"".equals(scriptCode)){
            scriptCode = "{\n" + scriptCode.substring(2, scriptCode.length()) + "\n" + ident + "}";
        }else{
            scriptCode = "{\n" + ident + "}";
        }
        
        if(self.getBoolean("varglobal")){
            //log.info("self.varme=" + self.varname);
            scriptCode = "var " + self.getString("varname") + " = new " + self.doAction("getExtType", actionContext) + "(" + scriptCode + ");";
            if(actionContext.get("extGlobalVars") != null){
                ((List<String>) actionContext.get("extGlobalVars")).add(scriptCode);
            }
            return self.getString("varname");
        }else{
            return scriptCode;
        }
    }
	
	public static String getJavaScriptObjectType(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return self.getDescriptor().getString("javaScriptObjectName");
	}
}