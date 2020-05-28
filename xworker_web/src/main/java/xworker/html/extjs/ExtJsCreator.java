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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.util.UtilTemplate;

public class ExtJsCreator {
	private static Logger log = LoggerFactory.getLogger(ExtJsCreator.class);
	
	@SuppressWarnings("unchecked")
	public static void importExtjs(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");	
    	
        if(actionContext.get("heads") != null){
            boolean haveExtJs = false;
            List<Map<String, String>> heads = (List<Map<String, String>>) actionContext.get("heads");
            for(Map<String, String> head : heads){
                if("ExtJs".equals(head.get("name"))){
                    haveExtJs = true;
                    break;
                }        
            }
            
            if(!haveExtJs){
                String html = null;
                
                if(self.getBoolean("debug")){
	                html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/resources/css/ext-all.css\"/>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/adapter/ext/ext-base.js\"></script>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ext-all-debug.js\"></script>\n" +
	                		"        <link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/ux/css/ux-all.css\"/>\n" +
	                		"        <link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/ux/css/LovCombo.css\"/>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/ux-all-debug.js\"></script>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/DateTime.js\"></script>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/LovCombo.js\"></script>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/util.js\"></script>\n" +
	                		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/xworker/xworkerExtjs.js\"></script>\n";
                }else{
                	html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/resources/css/ext-all.css\"/>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/adapter/ext/ext-base.js\"></script>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ext-all.js\"></script>\n" +
            		"        <link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/ux/css/ux-all.css\"/>\n" +
            		"        <link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/extjs/ux/css/LovCombo.css\"/>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/ux-all.js\"></script>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/DateTime.js\"></script>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/LovCombo.js\"></script>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/extjs/ux/util.js\"></script>\n" +
            		"        <script type=\"text/javascript\" src=\"${request.contextPath}/js/xworker/xworkerExtjs.js\"></script>\n";
                }
          
                Map<String, String> head = new HashMap<String, String>(); 
                head.put("name", "ExtJs");
                head.put("value", html);
                heads.add(head);
            }
        }
	}
	
    @SuppressWarnings("unchecked")
	public static Object toHtml(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        if(actionContext.get("heads") != null){
        	importExtjs(actionContext);
            
            boolean haveSelf = false;
            List<Map<String, String>> heads = (List<Map<String, String>>) actionContext.get("heads");
            for(Map<String, String> head : heads){
                if((self.getMetadata().getPath() + "ExtJs").equals(head.get("name"))){
                    haveSelf = true;
                    break;
                }
            }
            
            if(!haveSelf){
            	String params = self.getString("params");
                String html = "        <script type=\"text/javascript\" src=\"${request.contextPath}/do?sc=" + self.getMetadata().getPath() + "&" + params + "\"></script>\n";
          
                Map<String, String> head = new HashMap<String, String>(); 
                head.put("name", self.getMetadata().getPath());
                head.put("value", html);
                heads.add(head);                
            }
        }
        
        if(self.getBoolean("generateRenderDiv")){
        	return "<div id=\"XWorker_Extjs_" + self.getMetadata().getPath().hashCode() +"\"/>";
        }
        return "";
    }

    public static void httpDo(ActionContext actionContext) throws Throwable{
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        response.setContentType("text/javascript; charset=utf-8");
        
        //获取ExtThing
        Thing extThing = null;
        
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        String path =  request.getParameter("path");
        if(path != null){
            extThing = world.getThing(path);
        }else{
            extThing = self;
        }
        
        if(extThing != null){
            //文件名
            String fileName = extThing.getRoot().getMetadata().getPath().replace(':','/').replace('.','/');
            String templateFileName = "/work/extjs/" + UtilString.getThingManagerFileName(extThing) 
            			+ fileName + "/p_" + extThing.getMetadata().getPath().hashCode() + ".js";
            fileName = world.getPath() + templateFileName;
            File file = new File(fileName);
            
            //是否要重新生成
            if(!file.exists() || file.lastModified() < extThing.getMetadata().getLastModified() || self.getBoolean("dynamic")){
            	synchronized(self){
	                 if(!file.exists()){
	                      file.getParentFile().mkdirs();
	                 }
	                 
	                 String jsCode = (String) extThing.doAction("toJavaScriptCode", actionContext);
	                 //log.info("jsCode=" + jsCode);
	                 FileOutputStream fout = new FileOutputStream(file);
	                 try{
	                      fout.write(jsCode.getBytes("utf-8"));
	                      fout.flush();
	                 }finally{
	                     fout.close();
	                 }
	                 //file = new File(fileName);
	                 
	                 UtilTemplate.removeTempalteCache(templateFileName);
            	}
            }
                        
            if(self.getBoolean("dynamic")){
            	response.addHeader("Cache-Control", "no-store");
            }
            UtilTemplate.process(actionContext, templateFileName, "freemarker", new OutputStreamWriter(response.getOutputStream(), "UTF-8"), "UTF-8");
            /*
            FileInputStream fin = new FileInputStream(file);
            try{
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                response.setCharacterEncoding("utf-8");
                String template = new String(bytes);
                response.getWriter().println(UtilTemplate.processString(actionContext, template));;
                //response.getOutputStream().write(bytes);
                //response.getWriter().print(new String(bytes, "utf-8"));
                //response.getOutputStream().write(bytes);
            }finally{ 
                fin.close();
            }*/
        }else{
            response.getWriter().println("//thing not found ：" + path);
        }
    }

    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        //设置一个全局的变量列表，有些控件需要声明为全局才能使用，比如GridPanel的单选框插件等
        Bindings bindings = actionContext.peek();
        List<String> extGlobalVars = new ArrayList<String>();
        bindings.put("extGlobalVars", extGlobalVars);
        
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String script = "";
        for(Thing item : self.getChilds()){
        	try {
	        	if("Code".equals(item.getThingName())){            	
	                String code = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
	                if(code != null){
	                    if(script != ""){
	                        script = script + "\n" + code;
	                    }else{
	                        script = code;
	                    }
	                }
	            }else{
	            	String varref = item.getStringBlankAsNull("varref");            	
	                if(varref != null && !"".equals(varref)){
	                    if(!"".equals(script)){
	                        script = script + "\n" + ident + "    var " + item.getString("varname") + " = " + varref + ";\n";                
	                    }else{
	                        script =  ident + "    var " + item.getString("varname") + " = " + varref + ";\n";              
	                    }
	                }else{            
	                	if(self.getBoolean("generateRenderDiv")){
	                		//设置renderTo
	                		if(item.getStringBlankAsNull("renderTo") == null){
	                			item.set("renderTo", "'XWorker_Extjs_" + self.getMetadata().getPath().hashCode() + "'");
	                		}
	                	}
	                    String code = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));  
	                    if(item.getBoolean("varglobal")){
	                        continue;//已生成到全局，下面代码不需要重复
	                    }          
	                    
	                    String varname = item.getStringBlankAsNull("varname");
	                    if(varname != null && !"".equals(varname)){
		                    Object thingName = item.doAction("getExtType", actionContext);
		                    code = "new " + thingName + "(" + code;
		                    if(script != ""){
		                        script = script + "\n" + ident + "    var " + varname+ " = " + code + ");\n";
		                    }else{
		                        script = ident + "    var " + varname + " = "  + code + ");\n";
		                    }
	                    }else{
	                    	log.info("Extjs none varglobal root node should have a varname, path=" + item.getMetadata().getPath());
	                    }
	                }
	            }
        	}catch(Exception e) {
        		log.warn("Generate item error, path=" + item.getMetadata().getPath(), e);
        	}
        }
        
        for(String var : extGlobalVars){
            script = "    " + var + "\n\n" + script;
        }
        return "Ext.onReady(function() {\n    Ext.QuickTips.init();\n\n    Ext.BLANK_IMAGE_URL='js/extjs/resources/images/default/s.gif';\n\n" + script + "\n" + ident + "});";
    }

    public static Object toJsCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String script = "";
        for(Thing item : self.getChilds()){
            if("Code".equals(item.getThingName())){
                String code = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
                if(code != null){
                    if(script != ""){
                        script = script + "\n" + code;
                    }else{
                        script = code;
                    }
                }
            }else{
            	String varref = item.getString("varref");
                if(varref != null && !"".equals(varref)){
                    if(script != ""){
                        script = script + "\n" + ident + "    var " + item.getString("varname") + " = " + varref + ";\n";                
                    }else{
                        script =  ident + "    var " + item.getString("varname") + " = " + varref + ";\n";              
                    }
                }else{
                    String code = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
                    Object thingName = item.doAction("getExtType", actionContext);
                    code = "new " + thingName + "(" + code;
                    if(script != ""){
                        script = script + "\n" + ident + "    var " + item.getString("varname") + " = " + code + ");\n";
                    }else{
                        script = ident + "    var " + item.getString("varname") + " = "  + code + ");\n";
                    }
                }
            }
        }
        
        return script;
    }

    public static Object toJsOnReadyCode(ActionContext actionContext){
        return null;
    }

    public static void menu_generateJavaScript(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        
        //打开显示窗体
        Thing shellObj = world.getThing("xworker.ide.worldexplorer.swt.dialogs.JavaScriptDialog/@shell");
        ActionContext context = new ActionContext();
        Bindings bindings = context.peek();
        List<String> extGlobalVars = new ArrayList<String>();
        bindings.put("extGlobalVars", extGlobalVars);
        context.put("parent", ((ActionContext) actionContext.get("explorerContext")).get("shell"));
        context.push(null);
        
        Thing currentThing = (Thing) actionContext.get("currentThing");
        String text = "";
        String extType = (String) currentThing.doAction("getExtType", actionContext);
        String code =  (String) currentThing.doAction("toJavaScriptCode", context);
        for(String var : extGlobalVars){
            text = text + var + "\n\n";
        }
        if(extType != null){
            code = "new " + extType + "(" + code + ")";
        }
        context.put("text", text + code);
        shellObj.doAction("run", context);        
    }

    public static Object getCommonScriptLib(ActionContext actionContext){
    	//Thing self = (Thing) actionContext.get("self");
    	
        Action action = World.getInstance().getAction("xworker.html.extjs.ExtJs/@actions/@commonScriptLib");
        action.run(actionContext);
        return action.actionClass;
    }
}