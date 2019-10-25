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
package xworker.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyuxiang
 *
 */
public class UtilResource {
	private static Logger log = LoggerFactory.getLogger(UtilResource.class);
	
	static Map<String, UtilResource> resouceCache = new HashMap<String, UtilResource>();
	Map<String, Resources> cache = new HashMap<String, Resources>();
	Locale locale = null;
	
	public static UtilResource getInstance(Locale locale){
		if(locale == null){
			locale = Locale.getDefault();
		}
		
		UtilResource res = resouceCache.get(locale.toString());
		if(res == null){
			res = new UtilResource(locale);
			resouceCache.put(locale.toString(), res);
		}
		
		return res;
	}
	
	public static UtilResource getDefaultInstance(){
		Locale locale = null;//(Locale) SessionManager.getSession(null).getAttribute("xworker_current_user_locale");
		if(locale == null){
			locale = Locale.getDefault();
			//SessionManager.getSession(null).setAttribute("xworker_current_user_locale", locale);
		}
		
		UtilResource resource = UtilResource.getInstance(locale);
		return resource;
	}
	
	public UtilResource(Locale locale){
		this.locale = locale;
	}
	
	public Resources get(String name){
		Resources rs = (Resources) cache.get(name);
		if(rs == null){
			try{
				rs = new Resources(name, locale);
				cache.put(name, rs);
			}catch(Exception e){
				if(log.isWarnEnabled()){
					log.warn("找不到资源:" + name);
				}
				//e.printStackTrace();
			}
		}
		
		return rs;
	}
	
	public String get(String resourceName, String name, String defaultValue){
		Resources rs = get(resourceName);
		if(rs == null){
			return defaultValue;
		}else{
			return rs.get(name, defaultValue);
		}
	}
	
	public static void clear(){	
		try{
			for(String key : resouceCache.keySet()){
				UtilResource ur = resouceCache.get(key);
				ur.clearCache();
			}
			//ResourceBundle.clearCache();
			//resouceCache.clear();
		}catch(Exception e){
			log.warn("i18n resource clear error", e);
		}			
	}
	
	public void clearCache(){
		for(String key : cache.keySet()){
			Resources r = cache.get(key);
			r.clear();
		}
	}
}