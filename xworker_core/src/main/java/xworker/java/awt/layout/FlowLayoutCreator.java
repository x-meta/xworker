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
package xworker.java.awt.layout;

import java.awt.Container;
import java.awt.FlowLayout;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class FlowLayoutCreator {
	public static FlowLayout create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		FlowLayout comp = new FlowLayout();
		if(parent != null){
			parent.setLayout(comp);
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
	
	public static void init(FlowLayout comp, Thing thing, Container parent, ActionContext actionContext){
		Integer hgap = JavaCreator.createInteger(thing, "hgap");
		if(hgap != null){
			comp.setHgap(hgap);
		}
		
		Integer vgap = JavaCreator.createInteger(thing, "vgap");
		if(vgap != null){
			comp.setVgap(vgap);
		}
		
		Integer align = null;
		String v = thing.getString("align");
		if("CENTER".equals(v)){
			align = FlowLayout.CENTER;
		}else if("LEADING".equals(v)){
			align = FlowLayout.LEADING;
		}else if("LEFT".equals(v)){
			align = FlowLayout.LEFT;
		}else if("RIGHT".equals(v)){
			align = FlowLayout.RIGHT;
		}else if("TRAILING".equals(v)){
			align = FlowLayout.TRAILING;
		}
		if(align != null){
			comp.setAlignment(align);
		}
	}
}