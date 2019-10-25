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
package xworker.java.awt;

import java.awt.Container;
import java.awt.event.ContainerListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ContainerCreator {
	public static void createContainerListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ContainerListener listener = (ContainerListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addContainerListener(listener);
			}
		}
	}
	
	public static void init(Container obj, Thing thing, Container parent, ActionContext actionContext){
		ComponentCreator.init(obj, thing, parent, actionContext);
	}
}