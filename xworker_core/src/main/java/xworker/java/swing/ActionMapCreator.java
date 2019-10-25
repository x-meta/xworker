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
package xworker.java.swing;

import java.util.TooManyListenersException;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionMapCreator {
	public static void createActions(ActionContext actionContext) throws TooManyListenersException{
		Thing self = (Thing) actionContext.get("self");
		ActionMap parent = (ActionMap) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Action action = (Action) child.doAction("create", actionContext);
			if(action != null){
				parent.put(child.getMetadata().getName(), action);
			}
		}
	}
	
	public static ActionMap create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Object parent = (Object) actionContext.get("parent");
		
		//创建
		ActionMap actionMap = new ActionMap();
		if(parent != null){
			if(parent instanceof JComponent){
				((JComponent) parent).setActionMap(actionMap);
			}else if(parent instanceof ActionMap){
				actionMap.setParent((ActionMap) parent);
			}
		}
		
		//创建子节点
		try{
			actionContext.push().put("parent", actionMap);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), actionMap);
		return actionMap;
	}
}