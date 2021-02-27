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
package xworker.swt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

/**
 * 可以缓存的控件工厂。
 * 
 * 用于解决使用事物定义控件初始化需消耗很长时间的问题，如果控件初始化需要很长时间，那么感觉就会很慢，
 * 如果使用缓存工厂，那么可以避免多次初始化，可以提高速度。
 * 
 * 控件是按照父控件和事物的路径来做缓存的。
 * 
 * 创建控件的数据变量范围放在控件的_poolActionContext数据下，通过control.getData("_poolActionContext")取。
 * 
 * @author zyx
 *
 */
public class PoolableControlFactory {
	private static final String TAG = PoolableControlFactory.class.getName();
	
	private static Map<Control, HashMap<String, ArrayList<ControlEntry>>> caches = new HashMap<Control, HashMap<String, ArrayList<ControlEntry>>>();
	
	public static Control borrowControl(final Control parent, String thingPath, ActionContext actionContext){
		synchronized(parent){
			if(parent != null && !parent.isDisposed()){
				HashMap<String, ArrayList<ControlEntry>> cache = caches.get(parent);
				if(cache == null){
					cache = new HashMap<String, ArrayList<ControlEntry>>();
					caches.put(parent, cache);
					
					//缓存控件随父控件销毁而销毁
					parent.addDisposeListener(new DisposeListener(){
						public void widgetDisposed(DisposeEvent arg0) {
							HashMap<String, ArrayList<ControlEntry>> cache = caches.get(parent);
							if(cache != null){
								for(String key : cache.keySet()){
									ArrayList<ControlEntry> controls = cache.get(key);
									if(controls != null){
										for(ControlEntry entry : controls){
											if(entry.control != null){
												entry.control.dispose();
											}
										}
									}
								}
								caches.remove(parent);
							}
						}					
					});
				}
							
				ArrayList<ControlEntry> controls = cache.get(thingPath);
				if(controls == null){
					controls = new ArrayList<ControlEntry>();
					cache.put(thingPath, controls);
				}
				
				Control control = null;
				List<ControlEntry> forRemoved = new ArrayList<ControlEntry>();
				
				for(ControlEntry entry : controls){
					if(!entry.isUsed && entry.control != null && !entry.control.isDisposed()){
						entry.isUsed = true;
						control = entry.control;
					}else if(entry.control.isDisposed()){
						forRemoved.add(entry);
					}
				}
				
				//删除已经销毁的控件实体
				for(ControlEntry entry : forRemoved){
					controls.remove(entry);
				}
				
				if(control == null){
					//创建一个新的Entry
					Thing controlThing = World.getInstance().getThing(thingPath);
					if(controlThing != null){
						ActionContext context = actionContext;
						if(context == null ) context = new ActionContext();
						try{
							context.put("parent", parent);
							control = (Control) controlThing.doAction("create", context);
							if(control != null){
								control.setData("_poolActionContext", context);
								ControlEntry entry = new ControlEntry(control);
								entry.control = control;
								entry.isUsed = true;							
								controls.add(entry);
							}
						}catch(Exception e){
							Executor.error(TAG, "create control : " + thingPath, e);
						}
					}
				}				
				
				return control;
			}else{
				return null;
			}
		}
	}
	
	public static void returnControl(Control parent, String path, Control control){
		synchronized(parent){
			if(parent != null && !parent.isDisposed()){
				HashMap<String, ArrayList<ControlEntry>> cache = caches.get(parent);
				if(cache != null){
					 ArrayList<ControlEntry> controls = cache.get(path);
					 if(controls != null){
						 for(ControlEntry entry : controls){
							 if(entry.control == control){
								 entry.isUsed = false;
							 }
						 }
					 }
				}
			}
		}		
	}
	
	static class ControlEntry{		
		public Control control;
		public boolean isUsed = false;
		
		public ControlEntry(Control control){
			this.control = control;
		}
	}
}