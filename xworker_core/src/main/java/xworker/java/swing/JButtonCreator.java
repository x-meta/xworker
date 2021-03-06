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

import javax.swing.JButton;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JButtonCreator {

	public static JButton create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JButton jbutton = new JButton();
		if(parent != null){
			parent.add(jbutton);
		}
		
		//初始化
		init(jbutton, self, parent, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", jbutton);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), jbutton);
		return jbutton;
	}
	
	public static void init(JButton jbutton, Thing self, Container parent, ActionContext actionContext){
		AbstractButtonCreator.init(jbutton, self, parent, actionContext);
				
		Boolean defaultCapable = JavaCreator.createBoolean(self, "defaultCapable");
		if(defaultCapable != null){
			jbutton.setDefaultCapable(defaultCapable);
		}
	}
}