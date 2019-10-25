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
package xworker.http.controls;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

public class SimpleControlResultCreator {
	private static Logger log = LoggerFactory.getLogger(SimpleControlResultCreator.class);
	
    public static void redirect(ActionContext actionContext) throws IOException, TemplateException{
        Thing self = (Thing) actionContext.get("self");
        
        String url = self.getString("value");
        if(url.indexOf("${") != -1){
            url = UtilTemplate.processString(actionContext, self.getMetadata().getPath()+ "value", url);
        }    
        
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        response.sendRedirect(url);
    }

    public static void excel(ActionContext actionContext){
    	/*
    	Thing self = (Thing) actionContext.get("self");
        
        String t = self.getString("value");
        if(t.indexOf("${") != -1){
            t = UtilTemplate.processString(actionContext, self.getMetadata().getPath()+ "value", t);
        }   
        String[] ts = t.split("[:]");
        String projectName = self.getMetadata().getThingManager().getName();
        String template = "";
        if(ts.length > 1){
            projectName = ts[0];
            template = ts[1];
        }else{
            template = ts[0];
        }
        //println projectName;
        project = dataCenter.getProject(projectName);
        String templateFile = project.path + "/template/" + template;	
        						
        def response = ctxs.httpContext.response;
        FileInputStream fin = new FileInputStream(templateFile);
        try{
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + System.currentTimeMillis() + ".xls\"");
            ExcelUtils.export(fin, actionContext, response.getOutputStream());
        }finally{
            fin.close();
        }
        
        return;
        */
    }

    public static void control(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
        String url = self.getString("value");
        if(url.indexOf("${") != -1){
            url = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", url);
        }  
        Thing controlObject = World.getInstance().getThing(url);
        if(controlObject != null){
            controlObject.doAction("httpDo", actionContext);
        }else{
            log.warn("Webcontrol不存在：" + self.getString("value"));
        }
        
        return;
    }

    public static void template(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
        String templateThingPath = self.getString("value");
        if(templateThingPath.indexOf("${") != -1){
            templateThingPath = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", templateThingPath);
        }  
        Thing templateObject = World.getInstance().getThing(templateThingPath);
        Bindings bindings = actionContext.peek();
        if(templateObject != null){
        	//bindings.setVariable("____templateWriter", ctxs.httpContext.writer);
            templateObject = templateObject.detach();
            //templateObject.setAttribute("output", "____templateWriter");
            templateObject.doAction("process", actionContext);
        }else{
            log.warn("模板不存在：" + self.getString("value"));
        }
    }

}