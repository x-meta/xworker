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

import java.awt.Container;

import javax.swing.Box;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BoxCreator {
	public static Object create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		int axis = BoxLayoutCreator.getConstans(self.getString("axis"));
		Box box = new Box(axis);
		if(parent != null){
			parent.add(box);
		}
		
		//初始化
		init(box, self, parent, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", box);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), box);
		return box;
	}
	
	public static void init(Box box, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(box, thing, parent, actionContext);
	}
}