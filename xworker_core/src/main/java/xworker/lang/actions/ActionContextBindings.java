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
package xworker.lang.actions;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.xmeta.ActionContext;

public class ActionContextBindings implements Bindings {
	ActionContext actionContext;
	
	public ActionContextBindings(ActionContext actionContext){
		this.actionContext = actionContext;
		
	}
	public boolean containsKey(Object key) {
		return actionContext.containsKey(key);
	}

	public Object get(Object key) {
		return actionContext.get(key);
	}

	public Object put(String name, Object value) {
		return actionContext.put(name, value);
	}

	public void putAll(Map<? extends String, ? extends Object> arg0) {
		actionContext.putAll(arg0);
	}

	public Object remove(Object key) {
		return actionContext.remove(key);
	}

	public void clear() {
		actionContext.clear();
	}

	public boolean containsValue(Object value) {
		return actionContext.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return actionContext.entrySet();
	}

	public boolean isEmpty() {
		return actionContext.isEmpty();
	}

	public Set<String> keySet() {
		return actionContext.keySet();
	}

	public int size() {
		return actionContext.size();
	}

	public Collection<Object> values() {
		return actionContext.values();
	}

}