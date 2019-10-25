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

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JDesktopPaneCreator {
	public static void ceateFrames(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JDesktopPane parent = (JDesktopPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			JInternalFrame frame = (JInternalFrame) child.doAction("create", actionContext);
			
			if(frame != null){
				parent.add(frame);
			}
		}
	}
	
	public static JDesktopPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JDesktopPane comp = new JDesktopPane();
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
	
	public static void init(JDesktopPane comp, Thing self, Container parent, ActionContext actionContext){
		JLayeredPaneCreator.init(comp, self, parent, actionContext);
		
		Integer dragMode = null;
		String m = self.getStringBlankAsNull("dragMode");
		if("LIVE_DRAG_MODE".equals(m)){
			dragMode = JDesktopPane.LIVE_DRAG_MODE;
		}else if("OUTLINE_DRAG_MODE".equals(m)){
			dragMode = JDesktopPane.OUTLINE_DRAG_MODE;
		} 
		
		if(dragMode != null){
			comp.setDragMode(dragMode);
		}		
	}
}