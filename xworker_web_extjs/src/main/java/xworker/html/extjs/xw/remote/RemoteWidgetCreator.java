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
package xworker.html.extjs.xw.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.http.controls.SimpleControlCreator;
import xworker.util.UtilTemplate;

public class RemoteWidgetCreator {
    public static void httpDo(ActionContext actionContext) throws Throwable{
    	World world = World.getInstance();    	
    	Thing self = (Thing) actionContext.get("self");
    	
    	if(!SimpleControlCreator.checkPermission(self, actionContext)){
        	return;
        }
    	
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        response.setContentType("text/javascript; charset=utf-8");
        
        //获取ExtThing
        Thing extThing = self;
        if(extThing != null){
            //文件名
            String fileName = extThing.getRoot().getMetadata().getPath().replace(':','/').replace('.','/');
            String templateFileName = "work/extjs/" + fileName + "/p_" + extThing.getMetadata().getPath().hashCode() + ".js";
            fileName = world.getPath() + "/" + templateFileName;
            File file = new File(fileName);
            
            //是否要重新生成
            if(!file.exists() || file.lastModified() < extThing.getMetadata().getLastModified() || self.getBoolean("toJavaScriptAllTime")){
                 if(!file.exists()){
                      file.getParentFile().mkdirs();
                 }
                 
                 String jsCode = (String) extThing.doAction("toJavaScriptCode", actionContext);
                 FileOutputStream fout = new FileOutputStream(file);
                 try{
                      fout.write(jsCode.getBytes("utf-8"));
                 }finally{
                     fout.close();
                 }
            }
            
            UtilTemplate.process(actionContext, templateFileName, "freemarker", new OutputStreamWriter(response.getOutputStream(), "UTF-8"), "UTF-8");
            /*
            FileInputStream fin = new FileInputStream(file);
            try{
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes);
            }finally{ 
                fin.close();
            }*/
        }
    }

    public static Object toJavaScriptCode(ActionContext actionContext){
    	World world = World.getInstance();  
    	Thing self = (Thing) actionContext.get("self");
    	
        //------------------子控件代码------------
        Thing itemThing = null;
        String childCode = "";
        String widgetPath = self.getString("widgetPath");
        if(widgetPath != null && !"".equals(widgetPath)){
            itemThing = world.getThing(widgetPath);
            
        }else{
            itemThing = self.getThing("item@0/@0");    
        }
        
        String namespace = (String) self.doAction("getNamespace", actionContext);
        if(itemThing == null){
            childCode = "Ext.Msg.alert('提示', '控件不存在，path=" + self.getMetadata().getPath() + "/item@0/@0');";
        }else{
            List<String> extGlobalVars = new ArrayList<String>();
            Bindings bindings = actionContext.peek();
            bindings.put("extGlobalVars", extGlobalVars);
            bindings.put("namespace", namespace);
            childCode = (String) itemThing.doAction("toJavaScriptCode", actionContext);   
            if(childCode == null || childCode == ""){
                 childCode = "Ext.Msg.alert('提示', '控件没有生成JavaScript代码，path=" + self.getMetadata().getPath() + "/item@0/@0');";
            }else{
                 String code = "";
                 for(String ext : extGlobalVars){
                     code = code + ext + "\n";
                 }
                 childCode = code + "rw.item = new " + itemThing.doAction("getExtType", actionContext) + "(" + childCode + ");";
            }
        }
        
        //---------------初始化函数------------
        String initCode = "";
        Thing initThing = self.getThing("initFunction@0");
        if(initThing != null){
            initCode = "rw.init = " + initThing.doAction("toJavaScriptCode", actionContext) + ";";
        }else{
            initCode = "rw.init = function(values){};";
        }
        String code = "var rw = Ext.ns('" + namespace + "');\n" + 
        	childCode + "\n" +	initCode;

        return code;
    }

    /**
     * 获取名称空间。
     * 
     * @param actionContext
     * @return
     */
    public static String getNamespace(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return self.getString("namespace");
    }
}