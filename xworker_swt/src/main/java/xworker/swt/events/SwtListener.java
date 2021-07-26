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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.ThingLoader;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;

/**
 * <p>Swt的通用事件。</p>
 * 
 * Swt可以自定义动作也可以引用其他Swt通用时间、事物或动作集合，当引用动作集合的动作是使用
 * &lt;actionContainerName&gt;.&lt;actionName&gt;的字符串格式。
 * 
 * @author <a href="mailto:zhangyuxiang@tom.com">zyx</a>
 *
 */
public class SwtListener implements Listener{
	/** 日志 */
	private static final String TAG = SwtListener.class.getName();

	static final byte METHOD_E_A = 0;
	static final byte METHOD_E = 1;
	static final byte METHOD_A = 2;
	static final byte METHOD = 3;
	
	private static World world = World.getInstance();
	
	private ThingEntry entry = null;
	private ActionContext actionContext;
	private boolean isThingAction;

	Object methodOwner;
	Method handlerMethod;
	String methodName;
	byte methodType = METHOD;
	
	public SwtListener(Thing swtListenerThing, ActionContext actionContext){
		this(swtListenerThing, actionContext, false);
	}
	
	public SwtListener(Thing athing, ActionContext actionContext, boolean isThingAction){
		if(athing == null){
			throw new ActionException("SwtListener thing is null");
		}

		entry = new ThingEntry(athing.getMetadata().getPath(), athing);
		this.actionContext = actionContext;
		this.isThingAction = isThingAction;

		this.methodOwner = ThingLoader.getObject();
		this.methodName = athing.getStringBlankAsNull("methodName");
	}
	
	public Thing getThing(){
		return entry.getThing();
	}
	
	public void handleEvent(Event event) {
		if(methodOwner != null){
			ThingLoader.push(methodOwner);
		}
		try {
			//long start = System.currentTimeMillis();
			Thing swtListenerThing = entry.getThing();

			if (swtListenerThing == null) {
				if (Executor.isLogLevelEnabled(TAG, Executor.INFO)) {
					Executor.info(TAG, "action is null : " + entry.getPath());
				}
				return;
			} else {
				Map<String, String> params = null;
				String ref = swtListenerThing.getString("ref");
				if (ref != null && !"".equals(ref)) {
					int index = ref.indexOf("?");
					if (index != -1) {
						String paramsStr = ref.substring(index + 1, ref.length());
						ref = ref.substring(0, index);
						params = UtilString.getParams(paramsStr);
					}
					Object refObj = actionContext.get(ref);
					if (refObj == null) {
						Thing refThing = world.getThing(ref);
						if (refThing != null) {
							handleEvent(event, refThing, false, params);
						} else if (ref.indexOf(".") != -1 || ref.indexOf(":") != -1) {
							String refs[] = ref.split("[.:]");
							if (refs.length == 2) {
								refObj = actionContext.get(refs[0]);
								if (refObj instanceof ActionContainer) {
									ActionContainer actionContainer = (ActionContainer) refObj;
									Map<String, Object> parameters = new HashMap<String, Object>();
									parameters.put("event", event);
									actionContainer.doAction(refs[1], actionContext, parameters);
								}
							}
						}
					} else {
						if (refObj instanceof SwtListener) {
							((SwtListener) refObj).handleEvent(event);
						} else if (refObj instanceof Thing) {
							handleEvent(event, (Thing) refObj, false, params);
						} else {
							Executor.warn(TAG, "swt listener " + ref + " is not valid");
						}
					}
				}

				if (this.isThingAction) {
					handleEvent(event, swtListenerThing, false, params);
				} else {
					handleEvent(event, swtListenerThing, true, params);
				}
			}
		}finally {
			if(methodOwner != null){
				ThingLoader.pop();
			}
		}
		//log.info("事件处理事件：" + (System.currentTimeMillis() - start));
	}
	
	protected void handleEvent(Event event, Thing thing, boolean runChild, Map<String, String> params){
		//System.out.println("swt handler event, thing=" + thing.getMetadata().getPath() + ", event=" + event);
		Bindings bindings = actionContext.push(null);
		bindings.put("event", event);
		try{
			if(params != null) {
				bindings.putAll(params);
			}
			
			if(!runChild){
				Action action = world.getAction(thing);
				action.run(actionContext);
			}else{
				for(Thing child : thing.getChilds()){
					Action action = world.getAction(child.getMetadata().getPath());
					if(action != null){
						action.run(actionContext);
					}else{
						Executor.warn(TAG, "action is null : " + child.getMetadata().getPath());
					}
				}
			}
		}catch(Throwable e){
			Executor.error(TAG, "Swt listener :" + thing.getMetadata().getPath(), e);
		}finally{
			actionContext.pop();
		}

		if(methodOwner != null && methodName != null) {
			if (handlerMethod == null) {
				try {
					handlerMethod = getMethod(methodOwner.getClass(), methodName, Event.class, ActionContext.class);
					if (handlerMethod == null) {
						handlerMethod = getMethod(methodOwner.getClass(), methodName, Event.class);

						if (handlerMethod == null) {
							handlerMethod = getMethod(methodOwner.getClass(), methodName, ActionContext.class);

							if (handlerMethod == null) {
								handlerMethod = getMethod(methodOwner.getClass(), methodName);

								if (handlerMethod != null) {
									methodType = METHOD;
								}
							} else {
								methodType = METHOD_A;
							}
						} else {
							methodType = METHOD_E;
						}
					} else {
						methodType = METHOD_E_A;
					}

				} catch (Exception ignored) {
				}
			}

			try {
				if (handlerMethod != null) {
					switch (methodType) {
						case METHOD_E_A:
							handlerMethod.invoke(methodOwner, event, actionContext);
							break;
						case METHOD_E:
							handlerMethod.invoke(methodOwner, event);
							break;
						case METHOD_A:
							handlerMethod.invoke(methodOwner, actionContext);
							break;
						case METHOD:
							handlerMethod.invoke(methodOwner);
							break;
					}
				} else {
					Executor.warn(TAG, "Can not invoke method " + methodOwner.getClass().getName() + ":" + methodName);
				}
			} catch (Exception e) {
				Executor.warn(TAG, "Invoker event handler error, thing=" + thing.getMetadata().getPath() + ",method=" + methodName, e);
			}
		}
	}

	private static Method getMethod(Class<?> cls, String name, Class<?> ... params){
		try{
			return cls.getMethod(name, params);
		}catch(Exception e){
			return null;
		}
	}
}