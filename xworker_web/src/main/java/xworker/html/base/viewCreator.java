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
package xworker.html.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.html.HtmlConstants;
import xworker.html.module.ModuleProvider;
import xworker.html.module.ModuleRequire;
import xworker.http.ResultView;
import xworker.util.ThingRegistUtils;

public class viewCreator {
	private static Logger logger = LoggerFactory.getLogger(viewCreator.class);
	
    public static Object toHtml(ActionContext actionContext){
        //HTML分为head、body和bottom三个部分
        //head是HTML头，body是主内容，bottom是页面最后的部分，比如yahoo ui的初始化脚本在最后
        
        //初始化heads、bottoms和bottomThings，使个元素可以添加heads和bottoms
        //添加head和bottom可以通过view的addHead和addBottom方法
        List<Map<String, String>> heads = new ArrayList<Map<String, String>>();
        List<Map<String, String>> bottoms = new ArrayList<Map<String, String>>();
        List<Thing> bottomThings = new ArrayList<Thing>();
        List<ModuleProvider> moduleProviders = new ArrayList<ModuleProvider>();
        List<ModuleRequire> requiredModules = new ArrayList<ModuleRequire>();
        
        Thing self = (Thing) actionContext.get("self");
        ActionContext newContext = new ActionContext(actionContext);
        newContext.put("heads", heads);
        newContext.put("bottoms", bottoms);
        newContext.put("bottomThings", bottomThings);
        newContext.put("moduleProviders", moduleProviders);
        newContext.put("requiredModules", requiredModules);
        Map<String, String> bodyAttributes = new HashMap<String, String>();
        newContext.put(HtmlConstants.BODY_ATTRIBUTES, bodyAttributes);
        
        newContext.put("bodyAttributes", bodyAttributes);
        Map<String, String> headAttributes = new HashMap<String, String>();
        newContext.put(HtmlConstants.HTML_ATTRIBUTES, headAttributes);
        Thing javaScriptThing = new Thing("xworker.html.base.scripts.JavaScript");
        javaScriptThing.put("useChildsCode", "true");
        newContext.put(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING, javaScriptThing);
        Thing headJavaScriptThing = new Thing("xworker.html.base.scripts.JavaScript");
        headJavaScriptThing.put("useChildsCode", "true");
        newContext.put(HtmlConstants.HTML_HEADS_JAVASCRIPT_THING, headJavaScriptThing);
        
        //循环递归生成子事物的界面，每个子事物都应实现toHtml方法，toHtml方法返回的是body部分的代码
        //在toHtml方法里，子事物可以把头部的代码和底部的代码加入到heads和bottoms变量中
        String bodyContent = "";
        for(Thing child : self.getAllChilds()){
            String content = (String) child.doAction("toHtml", newContext);
            if(content != null){
                bodyContent = bodyContent + content;
            }
        }
        
        //检查模块
        checkModules(requiredModules, moduleProviders, actionContext);
        
        //-------------生成HTML头--------------------------
        String html = "";
        if("true".equals(self.getString("belongToHtml"))){
        	String attr = "";
        	for(String key : headAttributes.keySet()){
        		attr = attr + " " + key + "=\"" + headAttributes.get(key) + "\"";
        	}
        	String doctype = (String) self.doAction("getDoctype", actionContext);
        	if(doctype != null){
        		html = html + doctype + "\n";
        	}
            html = html + "<html" + attr + ">\n<head>";                
        }
        
        if(self.getBoolean("belongToHtml") || self.getBoolean("hasHead")){
        	if(self.get("title") != null){            		
                html = html + "\n<title>" + self.doAction("getTitle", actionContext) + "</title>\n";
            }
        	
            if(self.get("otherHeads") != null){
                html = html + self.get("otherHeads") + "\n";
            }
            
            for(Map<String, String> head  : heads){
                html = html + head.get("value") + "\n";
            }
            
            if(headJavaScriptThing.getChilds().size() > 0){
            	html = html + headJavaScriptThing.doAction("toHtml", newContext);
            }
        }
        
        //--------------生成HTML的body部分-------------------------
        if(self.getBoolean("belongToHtml")){
            //log.info(newContext.get("bodyAttributes"));        	
        	String attr = self.getStringBlankAsNull("bodyAttributes");
        	if(attr == null){
        		attr = "";
        	}
        	for(String key : bodyAttributes.keySet()){
        		attr = attr + " " + key + "=\"" + bodyAttributes.get(key) + "\"";
        	}
        	
            html = html + "</head>\n<body " + attr + ">\n";
        }    
        
        if(bodyContent != null){
            html = html + bodyContent;
        }
        
        //html = html+ "<div id=\"loadingDiv\">";
        //html = html + "</div>";
        
        //--------------生成HTML的底部部分--------------------------
        if(self.getBoolean("belongToHtml") || self.getBoolean("hasBottom")){
            for(Map<String, String> bottom : bottoms){
                html = html + bottom.get("value");
            }
            for(Thing bottomThing : bottomThings){
                html = html + bottomThing.doAction("toHtml", newContext);
            }
            if(javaScriptThing.getChilds().size() > 0){
            	html = html + javaScriptThing.doAction("toHtml", newContext);
            }
        }    
        
        if(self.getBoolean("belongToHtml")){
            html = html + "\n" +
            		" </body>\n" +
            		"</html>";
        }
        
        return html;
    }

    public static void httpDo(ActionContext actionContext) throws Throwable{
    	Thing self = (Thing) actionContext.get("self");
    	
    	ResultView.processViewThing(self, actionContext);
    }

    public static Object showGenerateFileName(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        StringBuffer tName = new StringBuffer();
        //def project = self.getMetadata().getProject();
        //tName.append(project == null ? "null" : project.getName());
        tName.append("/" + self.getRoot().getMetadata().getPath().hashCode());
        tName.append("/" + self.getMetadata().getPath().hashCode());
        tName.append("/" + self.getMetadata().getName());
        tName.append(".ftl");
        
        //println tName.toString();
        
        return tName.toString();
    }

    @SuppressWarnings("unchecked")
	public static void addHead(ActionContext actionContext){
        if(actionContext.get("heads") != null){
            boolean have = false;
            List<Map<String, String>> heads = (List<Map<String, String>>) actionContext.get("heads");
            String name = (String) actionContext.get("name");
            
            for(Map<String, String> head : heads){            	
                if(head.get("name").equals(name)){
                    have = true;
                    break;
                }        
            }
            
            if(!have){
            	Map<String, String> head = new HashMap<String, String>();
            	head.put("name", name);
            	head.put("value",  (String) actionContext.get("value"));
                heads.add(head);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static void addBottom(ActionContext actionContext){
        if(actionContext.get("bottoms") != null){
            boolean have = false;
            List<Map<String, String>> bottoms = (List<Map<String, String>>) actionContext.get("bottoms");
            String name = (String) actionContext.get("name");
            
            for(Map<String, String> bottom : bottoms){            	
                if(bottom.get("name").equals(name)){
                    have = true;
                    break;
                }        
            }
            
            if(!have){
            	Map<String, String> bottom = new HashMap<String, String>();
            	bottom.put("name", name);
            	bottom.put("value",  (String) actionContext.get("value"));
            	bottoms.add(bottom);
            }
        }
    }

    public static void viewHtmlCode(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing currentThing = (Thing) actionContext.get("currentThing");
    	
        if(actionContext.get("explorerContext") == null){
            //log.warn("Must run in explorer menu");
            System.out.println((Object) currentThing.doAction("toHtml"));
            return;
        }
        
        Thing editorThing = world.getThing("xworker.ide.worldexplorer.swt.dialogs.HtmlCodeViewer/@shell");
        ActionContext context = new ActionContext();
        context.put("parent", ((ActionContext) actionContext.get("explorerContext")).get("shell"));
        String html = (String) currentThing.doAction("toHtml");
        context.put("html", html == null ? "no html generated" : html);
        editorThing.doAction("run", context);
    }

    /**
     * 判断需要的模块提供者是否都存在，如果不存在那么添加合适的模块提供者。
     * 
     * @param moduleRequires
     * @param moduleProviders
     * @param actionContext
     * @return
     */
	public static List<ModuleProvider> checkModules(List<ModuleRequire> moduleRequires, List<ModuleProvider> moduleProviders, ActionContext actionContext){
    	//移除已经有Provider的模块
    	removeExistsRequiredModuels(moduleRequires, moduleProviders);
    	
    	//对未满足的模块需求，从事物注册中查找匹配的Provider
    	if(moduleRequires.size() > 0) {
    		//候选者模块提供者，是从事物注册中查找的
    		List<ModuleProvider> candidateProviders = getCandidateProviders(moduleProviders, actionContext);
    		
    		//挑选模块提供者
    		moduleProviders.addAll(findProperProviders(moduleRequires, candidateProviders));
    	}
    	
    	//执行初始化
    	for(ModuleProvider mr : moduleProviders) {
    		mr.init();
    	}
    	
    	return moduleProviders;
    }
	
	private static List<ModuleProvider> findProperProviders(List<ModuleRequire> moduleRequires, List<ModuleProvider> candidateProviders){
		List<ModuleProvider> properProviders = new ArrayList<ModuleProvider>();
		
		for(int i=0; i<moduleRequires.size(); i++) {
    		ModuleRequire mr = moduleRequires.get(i);
    		ModuleProvider existProvider = null;
    		ModuleProvider acceptProvider = null;
    		for(ModuleProvider mp : candidateProviders) {
    			if(mp.accept(mr.getModule(), mr.getVersion())) {
    				if(mp.resourceExists()) {
    					existProvider = mp;
    					break;
    				}else {
    					acceptProvider = mp;
    				}
    			}
    		}    	
    		
    		//添加存在的
    		if(existProvider != null) {
    			if(!properProviders.contains(existProvider)) {
    				properProviders.add(existProvider);
    			}
    		}else if(acceptProvider != null) {
    			//资源不存在，警告
    			logger.warn("Html module resource not exists, provider=" + acceptProvider.getThing().getMetadata().getPath());
    			if(!properProviders.contains(acceptProvider)) {
    				properProviders.add(acceptProvider);
    			}
    		}else {
    			logger.warn("Can not find html module provider, " + mr);
    		}
    	}
		
		return properProviders;
	}
    
    @SuppressWarnings("unchecked")
	private static List<ModuleProvider> getCandidateProviders(List<ModuleProvider> moduleProviders, ActionContext actionContext) {
    	//获取注册的ModuleProvider，并添加到候选者中
		World world = World.getInstance();
		Thing registThing = world.getThing("xworker.html.module.ModuleProviders");
		List<Thing> providerThings = ThingRegistUtils.searchRegistThings(registThing, "child", Collections.EMPTY_LIST, actionContext);
		List<ModuleProvider> candidateProviders = new ArrayList<ModuleProvider>();
		
    	for(Thing providerThing : providerThings) {
			boolean have = false;
			for(ModuleProvider mr : moduleProviders) {
				if(mr.equals(providerThing)) {
					have = true;
					break;
				}
			}
			if(!have) {
				for(ModuleProvider mr : candidateProviders) {
					if(mr.equals(providerThing)) {
    					have = true;
    					break;
    				}
				}
			}
			
			if(!have) {
				candidateProviders.add(new ModuleProvider(providerThing, actionContext));
			}
		}
    	
    	return candidateProviders;
    }
        
    /**
     * 如果对应的ModuleProvider存在，那么就从moduleRequires中移除。
     * 
     * @param moduleRequires
     * @param moduleProviders
     */
    private static void removeExistsRequiredModuels(List<ModuleRequire> moduleRequires, List<ModuleProvider> moduleProviders) {
    	for(int i=0; i<moduleRequires.size(); i++) {
    		ModuleRequire mr = moduleRequires.get(i);
    		for(ModuleProvider mp : moduleProviders) {
    			if(mp.accept(mr.getModule(), mr.getVersion())) {
    				//存在则移除模块需求
    				moduleRequires.remove(i);
    				i--;
    				break;
    			}
    		}
    				
    	}
    }
}