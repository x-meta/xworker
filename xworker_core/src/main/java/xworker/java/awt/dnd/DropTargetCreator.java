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
package xworker.java.awt.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class DropTargetCreator {
	public static void createDropTargetListeners(ActionContext actionContext) throws TooManyListenersException{
		Thing self = (Thing) actionContext.get("self");
		DropTarget parent = (DropTarget) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			DropTargetListener listener = (DropTargetListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addDropTargetListener(listener);
			}
		}
	}

	public static DropTarget create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Container) actionContext.get("parent");
		
		//创建
		DropTarget dropTarget = new DropTarget();
		if(parent != null){
			dropTarget.setComponent(parent);
		}
		
		//初始化
		init(dropTarget, self, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", dropTarget);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), dropTarget);
		return dropTarget;
	}
	
	public static void init(DropTarget obj, Thing thing, ActionContext actionContext){
		Boolean active = JavaCreator.createBoolean(thing, "active");
		if(active != null){
			obj.setActive(active);
		}
		
		Integer defaultActions = JavaCreator.createInteger(thing, "defaultActions");
		if(defaultActions != null){
			obj.setDefaultActions(defaultActions);
		}
	}
}