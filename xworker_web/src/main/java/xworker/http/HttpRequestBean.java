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
package xworker.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpRequestBean，封装Http的请求的一个工具类。
 * 
 * @author zyx
 *
 */
public class HttpRequestBean{
    HttpServletRequest request;
        
    public HttpRequestBean(HttpServletRequest request){
	    this.request = request; 
    }

	public Object get(String name) {
		return request.getParameter(name);
	}

	@SuppressWarnings("unchecked")
	public Map getParameterMap() {
		return request.getParameterMap();
	}
	
	public void set(String name, Object value) {
		request.setAttribute(name, value);
	}

	public Object[] gets(String name) {		
		return request.getParameterValues(name);
	}
    
	@SuppressWarnings("unchecked")
	public Map getDataMap(String objectName){
    	Map pmap = getParameterMap();
    	Map<String, Object> values = new HashMap<String, Object>();
    	Iterator keys = pmap.keySet().iterator();
    	while(keys.hasNext()){
    		String key = (String) keys.next();
    		if(key.startsWith(objectName + ".")){
    			String ks[] = key.split("[.]");
    			String[] vs = (String[]) gets(key);
    			String v = null;
    			if(vs != null){
    				if(vs.length == 1){
    					v = vs[0];
    				}else{
    					for(int i=0; i<vs.length; i++){
    						if(i != 0){
    							v = v + "," + vs[i];
    						}else{
    							v = vs[i];
    						}
    					}
    				}
    			}
    			String value = null;
    			if(v != null){
    				value = v.toString();
    			}
    			if(value != null && !"".equals(value.trim())){
    				putValue(values, ks, 0, value.trim());
    			}
    		}
    	}
    	
    	return values;
    }
    
	private void putValue(Map<String, Object> values, String[] vs, int index, String value){
    	if((vs.length - index)== 2){    		
    		values.put(vs[vs.length - 1], value);
    	}else if((vs.length - index) > 2){
    		@SuppressWarnings("unchecked")
    		Map<String, Object> vm = (Map<String, Object>) values.get(vs[index + 1]);
    		if(vm == null){
    			vm = new HashMap<String, Object>();
    			values.put(vs[index + 1], vm);
    		}
    		
    		index = index + 1;
    		putValue(vm, vs, index, value);
    	}
    }	
}