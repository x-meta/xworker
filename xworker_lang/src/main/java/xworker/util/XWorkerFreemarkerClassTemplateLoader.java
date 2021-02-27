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

import java.net.URL;

import org.xmeta.ThingManager;
import org.xmeta.World;

import freemarker.cache.ClassTemplateLoader;

/**
 * World的从类路径中寻找模板，主要是实现从事物管理器中寻找模板。
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class XWorkerFreemarkerClassTemplateLoader extends ClassTemplateLoader{

	@Override
	protected URL getURL(String name) {
		World world = World.getInstance();
		
		for(ThingManager thingManager : world.getThingManagers()){
			URL url = thingManager.findResource(name);
			if(url != null){
				return url;
			}
		}

		//由于ThingClassLoader会把eclipse的classpath排放在后面，造成项目中的模板不是最先读取的，
		//所以把线程的ClassLoader放在了前面
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);		
		if(url == null){
			url = world.getClassLoader().findResource(name);
		}
		
		return url;
	}

}