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
package xworker.dataObject.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class ConditionHttpUtils {
	public static Object getHttpRequestAttribute(ActionContext actionContext, String name){
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		if(request != null){
			return request.getAttribute(name);
		}else{
			return null;
		}
	}
	
	public static Object getHttpSessionAttribute(ActionContext actionContext, String name){
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		if(request != null){
			return request.getSession().getAttribute(name);
		}else{
			return null;
		}
	}
	
    @SuppressWarnings("unchecked")
	public static Map<String, Object> parseHttpRequest(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	
        Map<String, Object> data = (Map<String, Object>) actionContext.get("__conditonData");
        if(data == null){
            data = new HashMap<String, Object>();
        }
        
        String dataName= self.getString("dataName");
        if(dataName != null && !"".equals(dataName)){
            data.put(dataName, request.getParameter(dataName));	    
        }
        Bindings bindings = actionContext.push(null);
        bindings.put("__conditonData", data);
        try{
            for(Thing child : self.getChilds()){
                child.doAction("parseHttpRequest", actionContext);
            }
        }finally{
            actionContext.pop();
        }
        
        return data;
    }
}