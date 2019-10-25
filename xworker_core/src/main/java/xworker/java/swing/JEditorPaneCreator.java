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
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.EditorKit;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.swing.text.JTextComponentCreator;

public class JEditorPaneCreator {
	public static void createHyperlinkListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JEditorPane parent = (JEditorPane) actionContext.get("JEditorPane");
		
		for(Thing child : self.getChilds()){
			HyperlinkListener c = (HyperlinkListener) child.doAction("create", actionContext);
			if(c != null){
				parent.addHyperlinkListener(c);
			}
		}
	}
	
	public static void createEditorKit(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JEditorPane parent = (JEditorPane) actionContext.get("JEditorPane");
		
		for(Thing child : self.getChilds()){
			EditorKit c = (EditorKit) child.doAction("create", actionContext);
			if(c != null){
				parent.setEditorKit(c);
				break;
			}
		}
	}
	
	public static JEditorPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JEditorPane comp = new JEditorPane();
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
	
	public static void init(JEditorPane comp, Thing thing, Container parent, ActionContext actionContext){
		JTextComponentCreator.init(comp, thing, parent, actionContext);
		
		String contentType = thing.getStringBlankAsNull("contentType");
		if(contentType != null){
			comp.setContentType(contentType);
		}
		
		String url = thing.getStringBlankAsNull("url");
		if(url != null){
			try {
				comp.setPage(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String text = thing.getStringBlankAsNull("text");
		if(text != null){
			comp.setText(text);
		}
		
	}
}