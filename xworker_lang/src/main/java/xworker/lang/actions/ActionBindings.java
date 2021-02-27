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

public class ActionBindings implements Bindings{
	org.xmeta.Bindings bindings;
	
	public ActionBindings(org.xmeta.Bindings bindings){
		this.bindings = bindings;
	}
	
	public boolean containsKey(Object key) {
		return bindings.containsKey(key);
	}

	public Object get(Object key) {
		return bindings.get(key);
	}

	public Object put(String name, Object value) {
		return bindings.put(name, value);
	}

	public void putAll(Map<? extends String, ? extends Object> arg0) {
		bindings.putAll(arg0);
	}

	public Object remove(Object key) {
		return bindings.remove(key);
	}

	public void clear() {
		bindings.clear();
	}

	public boolean containsValue(Object value) {
		return bindings.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return bindings.entrySet();
	}

	public boolean isEmpty() {
		return bindings.isEmpty();
	}

	public Set<String> keySet() {
		return bindings.keySet();
	}

	public int size() {
		return bindings.size();
	}

	public Collection<Object> values() {
		return bindings.values();
	}

}