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
package xworker.html.extjs;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class ExtThingCreator {
    @SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	String varref = self.getString("varref");
        if(varref != null && !"".equals(varref)){
            return varref;
        }
        
        if(self.getBoolean("useChildExtThing") && self.getChilds().size() > 0){
        	return self.getChilds().get(0).doAction("toJavaScriptCode", actionContext);
        }
        
        //缩进
        String ident = (String) actionContext.get("ident");
        if(ident == null || self.getBoolean("varglobal")){
            ident = "    ";
        }
        String myident = ident + "    ";
        
        String scriptCode = "";
        //显示属性
        for(Thing descriptor : self.getAllAttributesDescriptors()){ 
        	String name = descriptor.getString("name");
            
        	String path = descriptor.getMetadata().getPath();
            if(path.startsWith("xworker.lang") || path.startsWith("_transient") || path.startsWith("xworker.html.extjs.ExtThing")){
                //事物的定义的属性不包括
                continue;
            }
            
            
            if("haveTypeInCode".equals(name)){
                continue;
            }
            if("varname".equals(name) || "varref".equals(name) || "haveTypeInCode".equals(name) || "varglobal".equals(name)){
                continue;
            }  
            String value = self.getString(descriptor.getString("name"));
        
            if(value != null &&  !"".equals(value) && !value.equals(descriptor.getString("default"))){
                scriptCode = scriptCode + ",\n" + myident + name + ": " + value;
            }
        }
        //显示子节点
        for(Thing child : self.getChilds()){    
            String childScriptCode = null;
            String cvarref = child.getString("varref");
            if(cvarref != null && !"".equals(cvarref)){
                childScriptCode = cvarref;
            }else{
                childScriptCode = (String) child.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
                if(childScriptCode != null && child.getBoolean("haveTypeInCode")){
                    String type = (String) child.doAction("getExtType", actionContext);
                    childScriptCode = "new " + type + "(" + childScriptCode + ")";
                }
            }
            if(childScriptCode != null && childScriptCode != ""){
                scriptCode = scriptCode + ",\n" + myident + child.getString("name") + ": " + childScriptCode;
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

    public static Object getExtType(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	if(self.getBoolean("useChildExtThing") && self.getChilds().size() > 0){
        	return self.getChilds().get(0).doAction("getExtType", actionContext);
        }
    	
        //从属性中获取
    	String extTypeName = self.getString("extTypeName");
        if(extTypeName != null && !"".equals(extTypeName)){
            return extTypeName;
        }
        
        //从描述者中获取
        for(Thing desc : self.getAllDescriptors()){
            String path = desc.getMetadata().getPath();
            if(path.startsWith("xworker.html.extjs.Ext") && desc.getParent() == null){
                path = path.substring(19, path.length());
                if(path.indexOf(":") != -1){
                    continue;            
                }else{
                    return path;
                }
            }
        }
        
        return "unknown";
    }

}