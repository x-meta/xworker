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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.awt.DialogCreator;

public class JDialogCreator {
	public static void createContentPane(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JDialog parent = (JDialog) actionContext.get("JDialog");

		for(Thing child : self.getChilds()){
			Container c = (Container) child.doAction("create", actionContext);
			if(c != null){
				parent.setContentPane(c);
			}
		}
	}
	
	public static void createJMenuBar(ActionContext actionContext){
		JDialog parent = (JDialog) actionContext.get("JDialog");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JMenuBar");
		JMenuBar item = (JMenuBar) thing.doAction("create", actionContext);
		if(item != null){
			parent.setJMenuBar(item);
		}
	}
	
	public static JDialog create(ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		//创建
		String owner = self.getStringBlankAsNull("owner");
		JDialog comp = null;
		if(owner == null){
			comp = new JDialog();
		}else{
			Object o = actionContext.get("owner");
			if(o instanceof Dialog){
				comp = new JDialog((Dialog) o);
			}else if (o instanceof Frame){
				comp = new JDialog((Frame) o);
			}else if(o instanceof Window){
				comp = new JDialog((Window) o);
			}else{
				comp = new JDialog();
			}
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
	
	public static void init(JDialog comp, Thing thing, Container parent, ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		DialogCreator.init(comp, thing, parent, actionContext);
		
		Integer defaultCloseOperation = null;
		String value = thing.getString("defaultCloseOperation");
		if("DO_NOTHING_ON_CLOSE".equals(value)){
			defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE;
		}else if("HIDE_ON_CLOSE".equals(value)){
			defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE;
		}else if("DISPOSE_ON_CLOSE".equals(value)){
			defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;
		}
		if(defaultCloseOperation != null){
			comp.setDefaultCloseOperation(defaultCloseOperation);
		}
	}
}