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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.border.Border;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;

public class JViewportCreator {
	public static void createView(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JViewport parent = (JViewport) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component c = (Component) child.doAction("create", actionContext);
			if(c != null){
				parent.setView(c);
			}
		}
		
	}
	
	public static void createBorder(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JViewport parent = (JViewport) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Border border = (Border) child.doAction("create", actionContext);
			if(border != null){
				parent.setBorder(border);
				break;
			}
		}
		
	}
	
	public static JViewport create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JViewport comp = new JViewport();
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
	
	public static void init(JViewport comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer scrollMode = null;
		String v = thing.getString("scrollMode");
		if("BLIT_SCROLL_MODE".equals(v)){
			scrollMode = JViewport.BLIT_SCROLL_MODE;
		}else if("BACKINGSTORE_SCROLL_MODE".equals(v)){
			scrollMode = JViewport.BACKINGSTORE_SCROLL_MODE;
		}else if("SIMPLE_SCROLL_MODE".equals(v)){
			scrollMode = JViewport.SIMPLE_SCROLL_MODE;
		}
		if(scrollMode != null){
			comp.setScrollMode(scrollMode);
		}
		
		Dimension extentSize = AwtCreator.createDimension(thing, "extentSize", actionContext);
		if(extentSize != null){
			comp.setExtentSize(extentSize);
		}
		
		Point viewPosition = AwtCreator.createPoint(thing, "viewPosition", actionContext);
		if(viewPosition != null){
			comp.setViewPosition(viewPosition);
		}
		
		Dimension  viewSize = AwtCreator.createDimension(thing, "viewSize", actionContext);
		if(viewSize != null){
			comp.setViewSize(viewSize);
		}
	}
}