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
package xworker.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class AttibuteObjectActions {
	private static Logger logger = LoggerFactory.getLogger(AttibuteObjectActions.class);
	
	public static void create(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Thing self = (Thing) actionContext.get("self");
		Object parent = actionContext.get("parent");
		if(parent == null){
			logger.warn("AttributeObject: parent is null,  path=" + self.getMetadata().getPath());
			return;
		}
				
		//关于属性对象的定义是在描述者上，应该是第一个描述者
		Thing descriptor = self.getDescriptor();
		String key = "__AttributeObjectMethodCache__";
		MethodCache m = (MethodCache) descriptor.getData(key);
		if(m == null || m.lastmodified != descriptor.getMetadata().getLastModified()){		
			//初始化方法的定义
			String extendsStr = descriptor.getString("extends");
			boolean createChild = descriptor.getBoolean("attr_createChild");
			boolean many = descriptor.getBoolean("attr_many");
			String parentClass = descriptor.getString("attr_parentClass");
			String method = descriptor.getString("attr_method");
			String paramClass = descriptor.getString("attr_paramClass");			
		}else{
			if(m.createChild == false){
				Thing objectThing = World.getInstance().getThing(m.extendsPath);
				if(objectThing == null){
					logger.warn("AttributeObject: attribute object thing is null,  path=" + m.extendsPath + ",owner=" + self.getMetadata().getPath());
					return;
				}
				
				Object attributeObj = objectThing.run("create", actionContext, (Map<String, Object>) null, true, false);
				m.method.invoke(parent, new Object[]{attributeObj});
			}else{
				for(Thing child : self.getChilds()){
					Object attributeObj = child.doAction("create", actionContext);
					m.method.invoke(parent, new Object[]{attributeObj});
					
					if(!m.many){
						break;
					}
				}
			}
		}
	}
	
	static class MethodCache{
		boolean createChild;
		boolean many;
		long lastmodified;
		Method method = null;
		String extendsPath = null;
	}
}