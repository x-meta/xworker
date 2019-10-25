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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.event.ChangeListener;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class ButtonModelCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String name = self.getMetadata().getName();
		
		ButtonModel model = new DefaultButtonModel();
		String actionCommand = self.getStringBlankAsNull("actionCommand");
		if(actionCommand != null){
			model.setActionCommand(actionCommand);
		}
		
		Boolean armed = JavaCreator.createBoolean(self, "armed");
		if(armed != null){
			model.setArmed(armed);
		}
		
		Boolean enabled = JavaCreator.createBoolean(self, "enabled");
		if(enabled != null){
			model.setArmed(enabled);
		}
		
		Integer mnemonic = AwtCreator.createMnemonic(self, "mnemonic");
		if(mnemonic != null){
			model.setMnemonic(mnemonic);
		}
		
		Boolean pressed = JavaCreator.createBoolean(self, "pressed");
		if(pressed != null){
			model.setPressed(pressed);
		}
		
		Boolean rollover = JavaCreator.createBoolean(self, "rollover");
		if(rollover != null){
			model.setRollover(rollover);
		}
		
		Boolean selected = JavaCreator.createBoolean(self, "selected");
		if(selected != null){
			model.setSelected(selected);
		}
		
		String buttonGroup = self.getStringBlankAsNull("buttonGroup");
		if(buttonGroup != null){
			ButtonGroup group = (ButtonGroup) actionContext.get(buttonGroup);
			if(group != null){
				model.setGroup(group);
			}
		}
		
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", model);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		actionContext.getScope(0).put(name, model);
		
		return model;
	}
	
	public static void createActionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ButtonModel parent = (ButtonModel) actionContext.get("parent");
		for(Thing child : self.getChilds()){
			ActionListener listener = (ActionListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addActionListener(listener);
			}
		}
	}
	
	public static void createChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ButtonModel parent = (ButtonModel) actionContext.get("parent");
		for(Thing child : self.getChilds()){
			ChangeListener listener = (ChangeListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addChangeListener(listener);
			}
		}
	}
	
	public static void createItemListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ButtonModel parent = (ButtonModel) actionContext.get("parent");
		for(Thing child : self.getChilds()){
			ItemListener listener = (ItemListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addItemListener(listener);
			}
		}
	}
}