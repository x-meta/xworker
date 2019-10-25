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

import javax.swing.JScrollBar;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JScrollBarCreator {
	public static JScrollBar create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JScrollBar comp = new JScrollBar();
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
	
	public static void init(JScrollBar comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer blockIncrement = JavaCreator.createInteger(thing, "blockIncrement");
		if(blockIncrement != null){
			comp.setBlockIncrement(blockIncrement);
		}
		
		Boolean enabled = JavaCreator.createBoolean(thing, "enabled");
		if(enabled != null){
			comp.setEnabled(enabled);
		}
		
		Integer maximum = JavaCreator.createInteger(thing, "maximum");
		if(maximum != null){
			comp.setMaximum(maximum);
		}
		
		Integer minimum = JavaCreator.createInteger(thing, "minimum");
		if(minimum != null){
			comp.setMinimum(minimum);
		}
		
		Integer orientation = null;
		String v = thing.getString("orientation");
		if("VERTICAL".equals(v)){
			orientation = JScrollBar.VERTICAL;
		}else if("HORIZONTAL".equals(v)){
			orientation = JScrollBar.HORIZONTAL;
		}
		if(orientation != null){
			comp.setOrientation(orientation);
		}
		
		Integer unitIncrement = JavaCreator.createInteger(thing, "unitIncrement");
		if(unitIncrement != null){
			comp.setUnitIncrement(unitIncrement);
		}
		
		Integer value = JavaCreator.createInteger(thing, "value");
		if(value != null){
			comp.setValue(value);
		}
		
		Boolean valueIsAdjusting = JavaCreator.createBoolean(thing, "valueIsAdjusting");
		if(valueIsAdjusting != null){
			comp.setValueIsAdjusting(valueIsAdjusting);
		}
		
		Integer visibleAmount = JavaCreator.createInteger(thing, "visibleAmount");
		if(visibleAmount != null){
			comp.setVisibleAmount(visibleAmount);
		}
	}
}