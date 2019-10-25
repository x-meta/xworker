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

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JProgressBarCreator {
	
	public static void createBoundedRangeModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JProgressBar parent = (JProgressBar) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			BoundedRangeModel l = (BoundedRangeModel) child.doAction("create", actionContext);
			if(l != null){
				parent.setModel(l);
			}
		}
	}
	
	public static void createChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JProgressBar parent = (JProgressBar) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ChangeListener l = (ChangeListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addChangeListener(l);
			}
		}
	}
	
	public static JProgressBar create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JProgressBar comp = new JProgressBar();
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
	
	public static void init(JProgressBar comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean borderPainted = JavaCreator.createBoolean(thing, "borderPainted");
		if(borderPainted != null){
			comp.setBorderPainted(borderPainted);
		}
		
		Boolean indeterminate = JavaCreator.createBoolean(thing, "indeterminate");
		if(indeterminate != null){
			comp.setIndeterminate(indeterminate);
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
		if("HORIZONTAL".equals(v)){
			orientation = JProgressBar.HORIZONTAL;
		}else if("VERTICAL".equals(v)){
			orientation = JProgressBar.VERTICAL;
		}
		if(orientation != null){
			comp.setOrientation(orientation);
		}
		
		String string = JavaCreator.createText(thing, "string", actionContext);
		if(string != null){
			comp.setString(string);
		}
		
		Boolean stringPainted = JavaCreator.createBoolean(thing, "stringPainted");
		if(stringPainted != null){
			comp.setStringPainted(stringPainted);
		}
		
		Integer value = JavaCreator.createInteger(thing, "value");
		if(value != null){
			comp.setValue(value);
		}
	}
}