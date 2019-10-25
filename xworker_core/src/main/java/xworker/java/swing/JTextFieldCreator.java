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
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JTextField;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;
import xworker.java.swing.text.JTextComponentCreator;

public class JTextFieldCreator {
	
	public static void createAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextField parent = (JTextField) actionContext.get("JTextField");
		
		for(Thing child : self.getChilds()){
			Action l = (Action) child.doAction("create", actionContext);
			if(l != null){
				parent.setAction(l);
				break;
			}
		}
	}
	
	public static void createActionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextField parent = (JTextField) actionContext.get("JTextField");
		
		for(Thing child : self.getChilds()){
			ActionListener l = (ActionListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addActionListener(l);
			}
		}
	}
	
	public static JTextField create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JTextField comp = new JTextField();
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
	
	public static void init(JTextField comp, Thing thing, Container parent, ActionContext actionContext){
		JTextComponentCreator.init(comp, thing, parent, actionContext);
		
		String actionCommand = thing.getStringBlankAsNull("actionCommand");
		if(actionCommand != null){
			comp.setActionCommand(actionCommand);
		}
		
		Integer columns = JavaCreator.createInteger(thing, "columns");
		if(columns != null){
			comp.setColumns(columns);
		}
		
		Font font = AwtCreator.createFont(thing, "font", actionContext);
		if(font != null){
			comp.setFont(font);
		}
		
		Integer horizontalAlignment = null;
		String value = thing.getString("horizontalAlignment");
		if("LEFT".equals(value)){
			horizontalAlignment = JTextField.LEFT;
		}else if("CENTER".equals(value)){
			horizontalAlignment = JTextField.CENTER;
		}else if("RIGHT".equals(value)){
			horizontalAlignment = JTextField.RIGHT;
		}else if("LEADING".equals(value)){
			horizontalAlignment = JTextField.LEADING;
		}else if("TRAILING".equals(value)){
			horizontalAlignment = JTextField.TRAILING;
		}
		if(horizontalAlignment != null){
			comp.setHorizontalAlignment(horizontalAlignment);
		}
		
		Integer scrollOffset = JavaCreator.createInteger(thing, "scrollOffset");
		if(scrollOffset != null){
			comp.setScrollOffset(scrollOffset);
		}
	}
}