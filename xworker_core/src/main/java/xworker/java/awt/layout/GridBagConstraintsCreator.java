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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class GridBagConstraintsCreator {
	public static void create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		Component parent = (Component) actionContext.get("parent");
		
		GridBagConstraints c = new GridBagConstraints();
		Integer gridx = JavaCreator.createInteger(self, "gridx");
		if(gridx != null){
			c.gridx = gridx;
		}
		
		Integer gridy = JavaCreator.createInteger(self, "gridy");
		if(gridy != null){
			c.gridy = gridy;
		}
		
		Integer gridwidth = JavaCreator.createInteger(self, "gridwidth");
		if(gridwidth != null){
			c.gridwidth = gridwidth;
		}
		
		Integer gridheight = JavaCreator.createInteger(self, "gridheight");
		if(gridheight != null){
			c.gridheight = gridheight;
		}
		
		Integer weightx = JavaCreator.createInteger(self, "weightx");
		if(weightx != null){
			c.weightx = weightx;
		}
		
		Integer weighty = JavaCreator.createInteger(self, "weighty");
		if(weighty != null){
			c.weighty = weighty;
		}
		
		Integer ipadx = JavaCreator.createInteger(self, "ipadx");
		if(ipadx != null){
			c.ipadx = ipadx;
		}
		
		Integer ipady = JavaCreator.createInteger(self, "ipady");
		if(ipady != null){
			c.ipady = ipady;
		}
		
		String anchor = self.getString("anchor");
		if("CENTER".equals(anchor)){
			c.anchor = GridBagConstraints.CENTER;
		}else if("NORTH".equals(anchor)){
			c.anchor = GridBagConstraints.NORTH;
		}else if("NORTHEAST".equals(anchor)){
			c.anchor = GridBagConstraints.NORTHEAST;
		}else if("EAST".equals(anchor)){
			c.anchor = GridBagConstraints.EAST;
		}else if("SOUTHEAST".equals(anchor)){
			c.anchor = GridBagConstraints.SOUTHEAST;
		}else if("SOUTH".equals(anchor)){
			c.anchor = GridBagConstraints.SOUTH;
		}else if("SOUTHWEST".equals(anchor)){
			c.anchor = GridBagConstraints.SOUTHWEST;
		}else if("WEST".equals(anchor)){
			c.anchor = GridBagConstraints.WEST;
		}else if("NORTHWEST".equals(anchor)){
			c.anchor = GridBagConstraints.NORTHWEST;
		}else if("PAGE_START".equals(anchor)){
			c.anchor = GridBagConstraints.PAGE_START;
		}else if("PAGE_END".equals(anchor)){
			c.anchor = GridBagConstraints.PAGE_END;
		}else if("LINE_START".equals(anchor)){
			c.anchor = GridBagConstraints.LINE_START;
		}else if("LINE_END".equals(anchor)){
			c.anchor = GridBagConstraints.LINE_END;
		}else if("FIRST_LINE_START".equals(anchor)){
			c.anchor = GridBagConstraints.FIRST_LINE_START;
		}else if("FIRST_LINE_END".equals(anchor)){
			c.anchor = GridBagConstraints.FIRST_LINE_END;
		}else if("LAST_LINE_START".equals(anchor)){
			c.anchor = GridBagConstraints.LAST_LINE_START;
		}else if("LAST_LINE_END".equals(anchor)){
			c.anchor = GridBagConstraints.LAST_LINE_END;
		}else if("BASELINE".equals(anchor)){
			c.anchor = GridBagConstraints.BASELINE;
		}else if("BASELINE_LEADING".equals(anchor)){
			c.anchor = GridBagConstraints.BASELINE_LEADING;
		}else if("BASELINE_TRAILING".equals(anchor)){
			c.anchor = GridBagConstraints.BASELINE_TRAILING;
		}else if("ABOVE_BASELINE".equals(anchor)){
			c.anchor = GridBagConstraints.ABOVE_BASELINE;
		}else if("ABOVE_BASELINE_LEADING".equals(anchor)){
			c.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
		}else if("ABOVE_BASELINE_TRAILING".equals(anchor)){
			c.anchor = GridBagConstraints.ABOVE_BASELINE_TRAILING;
		}else if("BELOW_BASELINE".equals(anchor)){
			c.anchor = GridBagConstraints.BELOW_BASELINE;
		}else if("BELOW_BASELINE_LEADING".equals(anchor)){
			c.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		}else if("BELOW_BASELINE_TRAILING".equals(anchor)){
			c.anchor = GridBagConstraints.BELOW_BASELINE_TRAILING;
		}
		
		String fill = self.getString("fill");
		if("NONE".equals(fill)){
			c.fill = GridBagConstraints.NONE;
		}else if("HORIZONTAL".equals(fill)){
			c.fill = GridBagConstraints.HORIZONTAL;
		}else if("VERTICAL".equals(fill)){
			c.fill = GridBagConstraints.VERTICAL;
		}else if("BOTH".equals(fill)){
			c.fill = GridBagConstraints.BOTH;
		}
		
		Insets insets = AwtCreator.createInsets(self, "insets", actionContext);
		if(insets != null){
			c.insets = insets;
		}
		
		LayoutManager layoutMgr = parent.getParent().getLayout();
		if (layoutMgr != null) {
			if (layoutMgr instanceof LayoutManager2) {
				((LayoutManager2) layoutMgr).addLayoutComponent(parent, c);
			} 
		}
	}
}