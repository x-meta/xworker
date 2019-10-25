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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.UtilString;

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
	private static Logger log = LoggerFactory.getLogger(SwtListener.class);
	
	private static World world = World.getInstance();
	
	private ThingEntry entry = null;
	private ActionContext actionContext;
	private boolean isThingAction;
	
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
	}
	
	public Thing getThing(){
		return entry.getThing();
	}
	
	public void handleEvent(Event event) {
		//long start = System.currentTimeMillis();
		Thing swtListenerThing = entry.getThing();
		
		if(swtListenerThing == null){
			if(log.isInfoEnabled()){
				log.info("action is null : " + entry.getPath());
			}
			return;
		}else{
			Map<String, String> params = null;
			String ref = swtListenerThing.getString("ref");
			if(ref != null && !"".equals(ref)){
				int index = ref.indexOf("?");
				if(index != -1) {
					String paramsStr = ref.substring(index + 1, ref.length());
					ref = ref.substring(0, index);
					params = UtilString.getParams(paramsStr);
				}
				Object refObj = actionContext.get(ref);
				if(refObj == null){
					Thing refThing = world.getThing(ref);
					if(refThing != null){
						handleEvent(event, refThing, false, params);
					}else if(ref.indexOf(".") != -1 || ref.indexOf(":") != -1){
						String refs[] = ref.split("[.:]");						
						if(refs.length == 2){
							refObj = actionContext.get(refs[0]);
							if(refObj instanceof ActionContainer){
								ActionContainer actionContainer = (ActionContainer) refObj;
								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("event", event);
								actionContainer.doAction(refs[1], actionContext, parameters);
							}
						}
					}
				}else{
					if(refObj instanceof SwtListener){
						((SwtListener) refObj).handleEvent(event);
					}else if(refObj instanceof Thing){
						handleEvent(event, (Thing) refObj, false, params);
					}else{
						log.warn("swt listener " + ref + " is not valid");
					}
				}
			}
			
			if(this.isThingAction){
				handleEvent(event, swtListenerThing, false, params);
			}else{
				handleEvent(event, swtListenerThing, true, params);
			}
		}
		//log.info("事件处理事件：" + (System.currentTimeMillis() - start));
	}
	
	protected void handleEvent(Event event, Thing thing, boolean runChild, Map<String, String> params){
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
						log.warn("action is null : " + child.getMetadata().getPath());
					}
				}
			}
		}catch(Throwable e){
			log.error("Swt listener :" + thing.getMetadata().getPath(), e);
		}finally{
			actionContext.pop();
		}
	}
}