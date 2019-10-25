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
import java.util.Hashtable;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JSliderCreator {
	public static void createModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSlider parent = (JSlider) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			BoundedRangeModel l = (BoundedRangeModel) child.doAction("create", actionContext);
			if(l != null){
				parent.setModel(l);
				break;
			}
		}
	}
	
	public static void createChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSlider parent = (JSlider) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ChangeListener l = (ChangeListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addChangeListener(l);
			}
		}
	}
	
	public static void createLabelTable(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSlider parent = (JSlider) actionContext.get("parent");
		
		Hashtable<Integer, JComponent> table = new Hashtable<Integer, JComponent>();
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", null);
			for(Thing child : self.getChilds()){
				Integer value = JavaCreator.createInteger(child, "value");
				if(value != null){
					Thing labelThing = child.getThing("Label@0");
					JComponent component = (JComponent) labelThing.doAction("create", actionContext);
					if(component != null){
						table.put(value, component);
					}
				}
			}
		}finally{
			actionContext.pop();
		}
		
		parent.setLabelTable(table);		
	}
	
	public static JSlider create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JSlider comp = new JSlider();
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
	
	public static void init(JSlider comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer extent = JavaCreator.createInteger(thing, "extent");
		if(extent != null){
			comp.setExtent(extent);
		}
		
		Font font = AwtCreator.createFont(thing, "font", actionContext);
		if(font != null){
			comp.setFont(font);
		}
		
		Boolean inverted = JavaCreator.createBoolean(thing, "inverted");
		if(inverted != null){
			comp.setInverted(inverted);
		}
		
		Integer majorTickSpacing = JavaCreator.createInteger(thing, "majorTickSpacing");
		if(majorTickSpacing != null){
			comp.setMajorTickSpacing(majorTickSpacing);
		}
		
		Integer maximum = JavaCreator.createInteger(thing, "maximum");
		if(maximum != null){
			comp.setMaximum(maximum);
		}
		
		Integer minimum = JavaCreator.createInteger(thing, "minimum");
		if(minimum != null){
			comp.setMinimum(minimum);
		}
		
		Integer minorTickSpacing = JavaCreator.createInteger(thing, "minorTickSpacing");
		if(minorTickSpacing != null){
			comp.setMinorTickSpacing(minorTickSpacing);
		}
		
		Integer orientation = null;
		String v = thing.getString("orientation");
		if("HORIZONTAL".equals(v)){
			orientation = JSlider.HORIZONTAL;
		}else if("VERTICAL".equals(v)){
			orientation = JSlider.VERTICAL;
		}
		if(orientation != null){
			comp.setOrientation(orientation);
		}
		
		Boolean paintLabels = JavaCreator.createBoolean(thing, "paintLabels");
		if(paintLabels != null){
			comp.setPaintLabels(paintLabels);
		}
		
		Boolean paintTicks = JavaCreator.createBoolean(thing, "paintTicks");
		if(paintTicks != null){
			comp.setPaintTicks(paintTicks);
		}
		
		Boolean paintTrack = JavaCreator.createBoolean(thing, "paintTrack");
		if(paintTrack != null){
			comp.setPaintTicks(paintTrack);
		}
		
		Boolean snapToTicks = JavaCreator.createBoolean(thing, "snapToTicks");
		if(snapToTicks != null){
			comp.setSnapToTicks(snapToTicks);
		}
		
		Integer value = JavaCreator.createInteger(thing, "value");
		if(value != null){
			comp.setValue(value);
		}
		
		Boolean valueIsAdjusting = JavaCreator.createBoolean(thing, "valueIsAdjusting");
		if(valueIsAdjusting != null){
			comp.setValueIsAdjusting(valueIsAdjusting);
		}
	}
}