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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class ResourceDisposeListener implements Listener{
	List<Widget> widgets = new ArrayList<Widget>();
	List<Resource> resources = new ArrayList<Resource>();
	Map<String, Object> resourceMap = new HashMap<String, Object>();

	public void add(String name, Widget widget){
		resourceMap.put(name, widget);
		widgets.add(widget);
	}
	
	public void add(String name, Resource resource){
		resourceMap.put(name, resource);
		resources.add(resource);
	}
	
	public Object get(String name){
		return resourceMap.get(name);
	}
	
	public void handleEvent(Event event) {
		for(Iterator<Widget> iter = widgets.iterator(); iter.hasNext();){
			try{
				iter.next().dispose();
			}catch(Exception e){				
			}
		}
		
		for(Iterator<Resource> iter = resources.iterator(); iter.hasNext();){
			try{
				iter.next().dispose();
			}catch(Exception e){				
			}
		}
	}
}