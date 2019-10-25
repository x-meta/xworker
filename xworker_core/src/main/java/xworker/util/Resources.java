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

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyuxiang
 *
 */
public class Resources {
	private static Logger log = LoggerFactory.getLogger(Resources.class);
	
	ResourceBundle messages;
	Locale locale;
	String name;
	ClassLoader bundleLoader;
	
	public Resources(String name, Locale local){
		this.name = name;
		if(local == null){
			local = Locale.getDefault();
		}
		
		this.locale = local;
		messages = ResourceBundle.getBundle(name,local);
		bundleLoader = Thread.currentThread().getContextClassLoader();
	}
	
	public String get(String name){
		try{
			return messages.getString(name);
		}catch(Exception e){
			//e.printStackTrace();
			if(log.isDebugEnabled()){
				log.debug("在资源" + this.name + "找不到资源信息：" + name);
			}
			return null;
		}
	}
	
	public String get(String name, String defaultValue){
		String message = get(name);
		
		if(message == null){
			return defaultValue;
		}else{
			return message;
		}
	}
	
	public void clear(){
		ResourceBundle.clearCache(bundleLoader);
	}
}