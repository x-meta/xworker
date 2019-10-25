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

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JOptionPaneCreator {
	public static JOptionPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JOptionPane comp = new JOptionPane();
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
	
	public static void init(JOptionPane comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Icon icon = SwingCreator.createIcon(thing, "icon", actionContext);
		if(icon != null){
			comp.setIcon(icon);
		}
		
		String message = JavaCreator.createText(thing, "message", actionContext);
		if(message != null){
			comp.setMessage(message);
		}
		
		Integer messageType = null;
		String value = thing.getString("messageType");
		if("ERROR_MESSAGE".equals(value)){
			messageType = JOptionPane.ERROR_MESSAGE;
		}else if("INFORMATION_MESSAGE".equals(value)){
			messageType = JOptionPane.INFORMATION_MESSAGE;
		}else if("WARNING_MESSAGE".equals(value)){
			messageType = JOptionPane.WARNING_MESSAGE;
		}else if("QUESTION_MESSAGE".equals(value)){
			messageType = JOptionPane.QUESTION_MESSAGE;
		}else if("PLAIN_MESSAGE".equals(value)){
			messageType = JOptionPane.PLAIN_MESSAGE;
		}
		if(messageType != null){
			comp.setMessageType(messageType);
		}
		
		Integer optionType = null;
		value = thing.getString("optionType");
		if("DEFAULT_OPTION".equals(value)){
			optionType = JOptionPane.DEFAULT_OPTION;
		}else if("YES_NO_OPTION".equals(value)){
			optionType = JOptionPane.YES_NO_OPTION;
		}else if("YES_NO_CANCEL_OPTION".equals(value)){
			optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		}else if("OK_CANCEL_OPTION".equals(value)){
			optionType = JOptionPane.OK_CANCEL_OPTION;
		}
		if(optionType != null){
			comp.setOptionType(optionType);
		}
		
		Boolean wantsInput = JavaCreator.createBoolean(thing, "wantsInput");
		if(wantsInput != null){
			comp.setWantsInput(wantsInput);
		}
	}
}