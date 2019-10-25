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
package xworker.html.base.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;

public class codeCreator {
    public static Object toHtml(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String code =  self.getStringBlankAsNull("code");
        if(code != null){
        	for(Thing child : self.getChilds()){
        		String html = (String) child.doAction("toHtml", actionContext);
        		if(html == null){
        			html = "";
        		}
        		
        		code = code.replaceAll("%%" + child.getMetadata().getName() + "%%", Matcher.quoteReplacement(html));
        	}
        }
        
        return code;
    }

    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String code =  self.getStringBlankAsNull("code");
        if(code != null){
        	for(Thing child : self.getChilds()){
        		String html = (String) child.doAction("toJavaScriptCode", actionContext);
        		if(html == null){
        			html = "";
        		}
        		
        		code = code.replaceAll("%%" + child.getMetadata().getName() + "%%", Matcher.quoteReplacement(html));
        	}
        }
        
        return code;
    }
    
    public static Object withChildToHtml(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String code = self.getStringBlankAsNull("code");
    	if(code == null){
    		return null;
    	}
    	
    	Map<String, String> context = new HashMap<String, String>();
    	for(Thing child : self.getChilds()){
    		Object obj = child.doAction("toHtml", actionContext);
    		if(obj instanceof String){
    			context.put(child.getMetadata().getName(), (String) obj);    			
    		}
    	}
    	
        return UtilTemplate.processString(context, code);
    }
}