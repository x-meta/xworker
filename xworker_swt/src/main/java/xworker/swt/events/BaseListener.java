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
package xworker.swt.events;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class BaseListener {
	private static Logger log = LoggerFactory.getLogger(BaseListener.class);
	static World world = World.getInstance();
	
	ActionContext actionContext;
	Thing actionThing;
	
	public BaseListener(ActionContext actionContext, Thing actionThing){
		this.actionContext = actionContext;
		this.actionThing = actionThing;
	}
	
	public void invokeMethod(String methodName, Object[] parameters){
		invokeMethod(methodName, actionContext, parameters);
	}
	
	public void invokeMethod(String methodName, ActionContext context, Object[] parameters){
		Bindings bindings = actionContext.push(null);
		
		try{
			for(int i=0; i<parameters.length; i++){
				bindings.put((String) parameters[i], parameters[i+1]);
				i++;
			}
						
			Object refObj = getReference();			
			if(refObj instanceof BaseListener){
				((BaseListener) refObj).invokeMethod(methodName, context, parameters);
			}else if(refObj instanceof Thing){
				((Thing) refObj).doAction(methodName, context);
			}
			
			actionThing.doAction(methodName, context);
		}finally{
			actionContext.pop();
		}
	}
	
	public Object getReference(){
		String referenceStr = actionThing.getString("reference");
		if(referenceStr != null){
			Object refObj = actionContext.get(referenceStr);
			if(refObj != null){
				return refObj;
			}else{
				return world.getThing(referenceStr);
			}
		}else{
			return null;
		}
	}
	
	/**
	 * 把自己添加到parent的监听器中，由于不知道parent的类型，所以使用反射机制实现。
	 * 
	 * @param actionContext
	 * @param methodName
	 */
	public void addToParent(ActionContext actionContext, String methodName, Class<?> aclass){
		Object parent = actionContext.get("parent");
		if(parent != null){
			try{
				Method method = parent.getClass().getMethod(methodName, new Class<?>[]{aclass});
				if(method != null){
					method.invoke(parent, new Object[]{this});
				}
			}catch(Exception e){
				log.warn("add listener error, methodName=" + methodName + ",class=" + aclass, e);
			}
		}
	}
	
	public void addToParent(int eventType, Listener listener, ActionContext actionContext) {
		Object parent = actionContext.get("parent");
		if(parent != null && listener != null && parent instanceof Control){
			((Control) parent).addListener(eventType, listener);
		}
	}
}