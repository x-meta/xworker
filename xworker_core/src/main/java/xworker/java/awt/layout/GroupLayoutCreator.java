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

import java.awt.Component;
import java.awt.Container;

import javax.swing.GroupLayout;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class GroupLayoutCreator {
	public static GroupLayout.Alignment createAlignment(Thing thing, String name){
		String value = thing.getStringBlankAsNull(name);
		if(value == null){
			return null;
		}
		
		if("BASELINE".equals(value)){
			return GroupLayout.Alignment.BASELINE;
		}else if("CENTER".equals(value)){
			return GroupLayout.Alignment.CENTER;
		}else if("LEADING".equals(value)){
			return GroupLayout.Alignment.LEADING;
		}else if("TRAILING".equals(value)){
			return GroupLayout.Alignment.TRAILING;
		}
		
		return null;
	}
	
	public static void createGroupChilds(Thing self, GroupLayout.Group group, ActionContext actionContext){
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", group);
			
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
	}
	
	public static GroupLayout.SequentialGroup createSequentialGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		
		GroupLayout.SequentialGroup group = layout.createSequentialGroup();
					
		createGroupChilds(self, group, actionContext);
		
		return group;
	}
	
	public static void createPSequentialGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.ParallelGroup parent = (GroupLayout.ParallelGroup) actionContext.get("parent");
		
		GroupLayout.Alignment alignment = createAlignment(self, "alignment");

		
		GroupLayout.SequentialGroup sgroup = layout.createSequentialGroup();
		GroupLayout.ParallelGroup group = null;
		if(alignment != null){
			group = parent.addGroup(alignment, sgroup);
		}else{
			group = parent.addGroup(sgroup);
		}
					
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createPParallelGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.ParallelGroup parent = (GroupLayout.ParallelGroup) actionContext.get("parent");
		
		boolean baselineGroup = self.getBoolean("baselineGroup");
		Boolean resizable = JavaCreator.createBoolean(self, "resizable");
		Boolean anchorBaselineToTop = JavaCreator.createBoolean(self, "anchorBaselineToTop");
		GroupLayout.Alignment alignment = createAlignment(self, "alignment");
		
		GroupLayout.ParallelGroup group = null;
		if(baselineGroup){
			group = layout.createBaselineGroup(resizable, anchorBaselineToTop);
		}else if(alignment != null && resizable != null){
			group = layout.createParallelGroup(alignment, resizable);
		}else if(alignment != null){
			group = layout.createParallelGroup(alignment);
		}else{
			group = layout.createParallelGroup();
		}
		
		group = parent.addGroup(group);
					
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createSSequentialGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.SequentialGroup parent = (GroupLayout.SequentialGroup) actionContext.get("parent");
				
		GroupLayout.SequentialGroup pgroup = layout.createSequentialGroup();
		
		Boolean useAsBaseline = JavaCreator.createBoolean(self, "useAsBaseline");		
		
		GroupLayout.SequentialGroup group = null;
		if(useAsBaseline){
			group = parent.addGroup(useAsBaseline, pgroup);
		}else{
			group = parent.addGroup(pgroup);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createSParallelGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.SequentialGroup parent = (GroupLayout.SequentialGroup) actionContext.get("parent");
		
		boolean baselineGroup = self.getBoolean("baselineGroup");
		Boolean resizable = JavaCreator.createBoolean(self, "resizable");
		Boolean anchorBaselineToTop = JavaCreator.createBoolean(self, "anchorBaselineToTop");
		GroupLayout.Alignment alignment = createAlignment(self, "alignment");
		
		GroupLayout.ParallelGroup pgroup = null;
		if(baselineGroup){
			pgroup = layout.createBaselineGroup(resizable, anchorBaselineToTop);
		}else if(alignment != null && resizable != null){
			pgroup = layout.createParallelGroup(alignment, resizable);
		}else if(alignment != null){
			pgroup = layout.createParallelGroup(alignment);
		}else{
			pgroup = layout.createParallelGroup();
		}
		
		Boolean useAsBaseline = JavaCreator.createBoolean(self, "useAsBaseline");		
		
		GroupLayout.SequentialGroup group = null;
		if(useAsBaseline){
			group = parent.addGroup(useAsBaseline, pgroup);
		}else{
			group = parent.addGroup(pgroup);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createSContainerGap(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.SequentialGroup parent = (GroupLayout.SequentialGroup) actionContext.get("parent");
		
		Integer max = JavaCreator.createInteger(self, "max");
		Integer pref = JavaCreator.createInteger(self, "pref");
		
		GroupLayout.SequentialGroup group = null;
		if(max != null && pref != null){
			group = parent.addContainerGap(pref, max);
		}else{
			group = parent.addContainerGap();
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createSGap(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.SequentialGroup parent = (GroupLayout.SequentialGroup) actionContext.get("parent");
		
		Integer min = JavaCreator.createInteger(self, "min");
		Integer max = JavaCreator.createInteger(self, "max");
		Integer pref = JavaCreator.createInteger(self, "pref");
		
		GroupLayout.SequentialGroup group = null;
		if(min != null && max != null && pref != null){
			group = parent.addGap(min, pref, max);
		}else{
			group = parent.addGap(pref);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createPGap(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.ParallelGroup parent = (GroupLayout.ParallelGroup) actionContext.get("parent");
		
		Integer min = JavaCreator.createInteger(self, "min");
		Integer max = JavaCreator.createInteger(self, "max");
		Integer pref = JavaCreator.createInteger(self, "pref");
		
		GroupLayout.ParallelGroup group = null;
		if(min != null && max != null && pref != null){
			group = parent.addGap(min, pref, max);
		}else{
			group = parent.addGap(pref);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createSComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.SequentialGroup parent = (GroupLayout.SequentialGroup) actionContext.get("parent");
		
		Component component = (Component) actionContext.get(self.getString("name"));
		if(component == null){
			return;
		}
		
		Boolean useAsBaseline = JavaCreator.createBoolean(self, "useAsBaseline");
		Integer min = JavaCreator.createInteger(self, "min");
		Integer max = JavaCreator.createInteger(self, "max");
		Integer pref = JavaCreator.createInteger(self, "pref");
		
		GroupLayout.SequentialGroup group = null;
		if(useAsBaseline != null && min != null && max != null && pref != null){
			group = parent.addComponent(useAsBaseline, component,  min, pref, max);
		}else if(useAsBaseline != null){
			group = parent.addComponent(useAsBaseline, component);
		}else if(min != null && max != null && pref != null){
			group = parent.addComponent(component, min, pref, max);
		}else{
			group = parent.addComponent(component);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static void createPComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//GroupLayout layout = (GroupLayout) actionContext.get("layout");
		GroupLayout.ParallelGroup parent = (GroupLayout.ParallelGroup) actionContext.get("parent");
		
		Component component = (Component) actionContext.get(self.getString("name"));
		if(component == null){
			return;
		}
		
		GroupLayout.Alignment alignment = createAlignment(self, "alignment");
		Integer min = JavaCreator.createInteger(self, "min");
		Integer max = JavaCreator.createInteger(self, "max");
		Integer pref = JavaCreator.createInteger(self, "pref");
		
		GroupLayout.ParallelGroup group = null;
		if(alignment != null && min != null && max != null && pref != null){
			group = parent.addComponent(component, alignment, min, pref, max);
		}else if(alignment != null){
			group = parent.addComponent(component, alignment);
		}else if(min != null && max != null && pref != null){
			group = parent.addComponent(component, min, pref, max);
		}else{
			group = parent.addComponent(component);
		}
		
		createGroupChilds(self, group, actionContext);
	}
	
	public static GroupLayout.ParallelGroup createParallelGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
		
		boolean baselineGroup = self.getBoolean("baselineGroup");
		Boolean resizable = JavaCreator.createBoolean(self, "resizable");
		Boolean anchorBaselineToTop = JavaCreator.createBoolean(self, "anchorBaselineToTop");
		GroupLayout.Alignment alignment = createAlignment(self, "alignment");
		
		GroupLayout.ParallelGroup group = null;
		if(baselineGroup){
			group = layout.createBaselineGroup(resizable, anchorBaselineToTop);
		}else if(alignment != null && resizable != null){
			group = layout.createParallelGroup(alignment, resizable);
		}else if(alignment != null){
			group = layout.createParallelGroup(alignment);
		}else{
			group = layout.createParallelGroup();
		}
		
		createGroupChilds(self, group, actionContext);
		
		return group;
	}
	
	public static void createVerticalGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
	
		for(Thing child : self.getChilds()){
			GroupLayout.Group group = (GroupLayout.Group ) child.doAction("create", actionContext);
			if(group != null){
				layout.setVerticalGroup(group);
				break;
			}
		}
	}
	
	public static void createHorizontalGroup(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		GroupLayout layout = (GroupLayout) actionContext.get("layout");
	
		for(Thing child : self.getChilds()){
			GroupLayout.Group group = (GroupLayout.Group ) child.doAction("create", actionContext);
			if(group != null){
				layout.setHorizontalGroup(group);
				break;
			}
		}
	}
	
	public static GroupLayout create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		GroupLayout comp = new GroupLayout(parent);
		if(parent != null){
			parent.setLayout(comp);
		}
		
		//初始化
		init(comp, self, null, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("layout", comp);
			actionContext.push().put("parent", null);
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
	
	public static void init(GroupLayout comp, Thing thing, Container parent, ActionContext actionContext){
		Boolean autoCreateContainerPadding = JavaCreator.createBoolean(thing, "autoCreateContainerPadding");
		if(autoCreateContainerPadding != null){
			comp.setAutoCreateContainerGaps(autoCreateContainerPadding);
		}
		
		Boolean autoCreatePadding = JavaCreator.createBoolean(thing, "autoCreatePadding");
		if(autoCreatePadding != null){
			comp.setAutoCreateGaps(autoCreatePadding);
		}
		
		Boolean honorsVisibility = JavaCreator.createBoolean(thing, "honorsVisibility");
		if(honorsVisibility != null){
			comp.setHonorsVisibility(honorsVisibility);
		}
	}
}