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
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.awt.FrameCreator;

public class JFrameCreator {
	public static void createJMenuBar(ActionContext actionContext){
		JFrame parent = (JFrame) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JMenuBar");
		JMenuBar c = (JMenuBar) thing.run("create", actionContext);
		if(c != null){
			parent.setJMenuBar(c);		
		}
	}
	
	
	public static void createContentPane(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JFrame parent = (JFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Container c = (Container) child.doAction("create", actionContext);
			if(c != null){
				parent.setContentPane(c);
				break;
			}
		}
	}
	
	public static JFrame create(ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		//变量
		Thing self = (Thing) actionContext.get("self");
		//创建
		JFrame comp = new JFrame();
		
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
		
		if(self.getBoolean("centerScreen")){
		      double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		      double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		      comp.setLocation( (int) (width - comp.getWidth()) / 2,
		                  (int) (height - comp.getHeight()) / 2);
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), comp);
		return comp;
	}
	
	
	public static JFrame run(ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		JFrame frame = (JFrame) self.doAction("create", actionContext);
		if(frame != null){
			frame.setVisible(true);
		}
		
		return frame;
	}
	
	public static Object run_menu(ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		//变量
		Thing self = (Thing) actionContext.get("currentThing");
		return self.doAction("run", actionContext);
	}
	
	public static void init(JFrame comp, Thing thing, Container parent, ActionContext actionContext) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		FrameCreator.init(comp, thing, parent, actionContext);
		
		Integer defaultCloseOperation = null;
		String v = thing.getString("defaultCloseOperation");
		if("DO_NOTHING_ON_CLOSE".equals(v)){
			defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE;
		}else if("HIDE_ON_CLOSE".equals(v)){
			defaultCloseOperation = JFrame.HIDE_ON_CLOSE;
		}else if("DISPOSE_ON_CLOSE".equals(v)){
			defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE;
		}else if("EXIT_ON_CLOSE".equals(v)){
			defaultCloseOperation = JFrame.EXIT_ON_CLOSE;
		}
		if(defaultCloseOperation != null){
			comp.setDefaultCloseOperation(defaultCloseOperation);
		}
	}
}