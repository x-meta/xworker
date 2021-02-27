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

public class JdkScriptBindings implements Bindings{
	ActionContext actionContext;
	
	public JdkScriptBindings(ActionContext actionContext){
		this.actionContext = new ActionContext(actionContext);
	}
	
	@Override
	public int size() {
		return actionContext.size();
	}

	@Override
	public boolean isEmpty() {
		return actionContext.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return actionContext.containsKey(value);
	}

	@Override
	public void clear() {
		actionContext.clear();
	}

	@Override
	public Set<String> keySet() {
		return actionContext.keySet();
	}

	@Override
	public Collection<Object> values() {
		return actionContext.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return actionContext.entrySet();
	}

	@Override
	public Object put(String name, Object value) {
		return actionContext.put(name, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		actionContext.putAll(toMerge);
	}

	@Override
	public boolean containsKey(Object key) {
		return actionContext.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		return actionContext.get(key);
	}

	@Override
	public Object remove(Object key) {
		return actionContext.remove(key);
	}

}