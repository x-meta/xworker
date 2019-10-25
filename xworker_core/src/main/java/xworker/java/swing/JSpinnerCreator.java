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

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JSpinnerCreator {
	public static void createModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSpinner parent = (JSpinner) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			SpinnerModel component = (SpinnerModel) child.doAction("create", actionContext);
			if(component != null){
				parent.setModel(component);
				break;
			}
		}
	}
	
	public static void createEditor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSpinner parent = (JSpinner) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			JComponent component = (JComponent) child.doAction("create", actionContext);
			if(component != null){
				parent.setEditor(component);
				break;
			}
		}
	}
	
	public static JSpinner create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JSpinner comp = new JSpinner();
		if(parent != null){
			parent.add(comp);
		}
		
		//初始化
		init(comp, self, null, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", comp);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), comp);
		return comp;
	}
	
	public static void init(JSpinner comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer value = JavaCreator.createInteger(thing, "value");
		if(value != null){
			comp.setValue(value);
		}
	}
}