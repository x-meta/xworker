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
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JComboBoxCreator {
	public static void createComboBoxModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JComboBox parent = (JComboBox) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ComboBoxModel model = (ComboBoxModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setModel(model);
				break;
			}
		}
	}
	
	public static void createAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JComboBox parent = (JComboBox) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Action action = (Action) child.doAction("create", actionContext);
			if(action != null){
				parent.setAction(action);
				break;
			}
		}
	}
	
	public static void createActionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JComboBox parent = (JComboBox) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ActionListener listener = (ActionListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addActionListener(listener);
			}
		}
	}
	
	public static JComboBox create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JComboBox comp = new JComboBox();
		if(parent != null){
			parent.add(comp);
		}
		
		//初始化
		init(comp, self, parent, actionContext);
		
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
	
	public static void init(JComboBox comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		String actionCommand = thing.getStringBlankAsNull("actionCommand");
		if(actionCommand != null){
			comp.setActionCommand(actionCommand);
		}
		
		Boolean editable = JavaCreator.createBoolean(thing, "editable");
		if(editable != null){
			comp.setEditable(editable);
		}
		
		Boolean enabled = JavaCreator.createBoolean(thing, "enabled");
		if(enabled != null){
			comp.setEnabled(enabled);
		}
		
		Boolean lightWeightPopupEnabled = JavaCreator.createBoolean(thing, "lightWeightPopupEnabled");
		if(lightWeightPopupEnabled != null){
			comp.setLightWeightPopupEnabled(lightWeightPopupEnabled);
		}
		
		Integer maximumRowCount = JavaCreator.createInteger(thing, "maximumRowCount");
		if(maximumRowCount != null){
			comp.setMaximumRowCount(maximumRowCount);
		}
		
		Boolean popupVisible = JavaCreator.createBoolean(thing, "popupVisible");
		if(popupVisible != null){
			comp.setPopupVisible(popupVisible);
		}
		
		Integer selectedIndex = JavaCreator.createInteger(thing, "selectedIndex");
		if(selectedIndex != null){
			comp.setSelectedIndex(selectedIndex);
		}
	}
}