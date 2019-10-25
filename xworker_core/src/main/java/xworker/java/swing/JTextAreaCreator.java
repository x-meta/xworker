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

import javax.swing.JTextArea;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;
import xworker.java.swing.text.JTextComponentCreator;

public class JTextAreaCreator {
	public static JTextArea create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JTextArea comp = new JTextArea();
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
	
	public static void init(JTextArea comp, Thing thing, Container parent, ActionContext actionContext){
		JTextComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer columns = JavaCreator.createInteger(thing, "columns");
		if(columns != null){
			comp.setColumns(columns);
		}
		
		Font font = AwtCreator.createFont(thing, "font", actionContext);
		if(font != null){
			comp.setFont(font);
		}
		
		Boolean lineWrap = JavaCreator.createBoolean(thing, "lineWrap");
		if(lineWrap != null){
			comp.setLineWrap(lineWrap);
		}
		
		Integer rows = JavaCreator.createInteger(thing, "rows");
		if(rows != null){
			comp.setRows(rows);
		}
		
		Integer tabSize = JavaCreator.createInteger(thing, "tabSize");
		if(tabSize != null){
			comp.setTabSize(tabSize);
		}
		
		Boolean wrapStyleWord = JavaCreator.createBoolean(thing, "wrapStyleWord");
		if(wrapStyleWord != null){
			comp.setWrapStyleWord(wrapStyleWord);
		}
	}
}