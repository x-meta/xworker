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

import javax.swing.Icon;
import javax.swing.JLabel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JLabelCreator {
	public static JLabel create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JLabel comp = new JLabel();
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
	
	public static void init(JLabel comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Icon disabledIcon = SwingCreator.createIcon(thing, "disabledIcon", actionContext);
		if(disabledIcon != null){
			comp.setDisabledIcon(disabledIcon);
		}
		
		Integer displayedMnemonic = AwtCreator.createMnemonic(thing, "displayedMnemonic");
		if(displayedMnemonic != null){
			comp.setDisplayedMnemonic(displayedMnemonic);
		}
		
		Integer displayedMnemonicIndex = JavaCreator.createInteger(thing, "displayedMnemonicIndex");
		if(displayedMnemonicIndex != null){
			comp.setDisplayedMnemonicIndex(displayedMnemonicIndex);
		}
		
		Integer horizontalAlignment = null;
		String v = thing.getString("horizontalAlignment");
		if("LEFT".equals(v)){
			horizontalAlignment = JLabel.LEFT;
		}else if("CENTER".equals(v)){
			horizontalAlignment = JLabel.CENTER;
		}else if("RIGHT".equals(v)){
			horizontalAlignment = JLabel.RIGHT;
		}else if("LEADING".equals(v)){
			horizontalAlignment = JLabel.LEADING;
		}else if("TRAILING".equals(v)){
			horizontalAlignment = JLabel.TRAILING;
		}
		if(horizontalAlignment != null){
			comp.setHorizontalAlignment(horizontalAlignment);
		}
		
		Integer horizontalTextPosition = null;
		v = thing.getString("horizontalTextPosition");
		if("LEFT".equals(v)){
			horizontalTextPosition = JLabel.LEFT;
		}else if("CENTER".equals(v)){
			horizontalTextPosition = JLabel.CENTER;
		}else if("RIGHT".equals(v)){
			horizontalTextPosition = JLabel.RIGHT;
		}else if("LEADING".equals(v)){
			horizontalTextPosition = JLabel.LEADING;
		}else if("TRAILING".equals(v)){
			horizontalTextPosition = JLabel.TRAILING;
		}
		if(horizontalTextPosition != null){
			comp.setHorizontalTextPosition(horizontalTextPosition);
		}
		
		Icon icon = SwingCreator.createIcon(thing, "icon", actionContext);
		if(icon != null){
			comp.setIcon(icon);
		}
		
		Integer iconTextGap = JavaCreator.createInteger(thing, "iconTextGap");
		if(iconTextGap != null){
			comp.setIconTextGap(iconTextGap);
		}
		
		String text = JavaCreator.createText(thing, "text", actionContext);
		if(text != null){
			comp.setText(text);
		}
		
		Integer verticalAlignment = null;
		v = thing.getString("verticalAlignment");
		if("TOP".equals(v)){
			verticalAlignment = JLabel.TOP;
		}else if("CENTER".equals(v)){
			verticalAlignment = JLabel.CENTER;
		}else if("BOTTOM".equals(v)){
			verticalAlignment = JLabel.BOTTOM;
		}
		if(verticalAlignment != null){
			comp.setVerticalAlignment(verticalAlignment);
		}
		
		Integer verticalTextPosition = null;
		v = thing.getString("verticalTextPosition");
		if("TOP".equals(v)){
			verticalTextPosition = JLabel.TOP;
		}else if("CENTER".equals(v)){
			verticalTextPosition = JLabel.CENTER;
		}else if("BOTTOM".equals(v)){
			verticalTextPosition = JLabel.BOTTOM;
		}
		if(verticalTextPosition != null){
			comp.setVerticalTextPosition(verticalTextPosition);
		}
	}
}