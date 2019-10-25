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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BorderLayoutConstraintCreator {
	public static void create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		Component parent = (Component) actionContext.get("parent");
		
		String constranit = self.getString("constranit");
		if("AFTER_LAST_LINE".equals(constranit)){
			constranit = BorderLayout.AFTER_LAST_LINE;
		}else if("AFTER_LINE_ENDS".equals(constranit)){
			constranit = BorderLayout.AFTER_LINE_ENDS;
		}else if("BEFORE_FIRST_LINE".equals(constranit)){
			constranit = BorderLayout.BEFORE_FIRST_LINE;
		}else if("BEFORE_LINE_BEGINS".equals(constranit)){
			constranit = BorderLayout.BEFORE_LINE_BEGINS;
		}else if("CENTER".equals(constranit)){
			constranit = BorderLayout.CENTER;
		}else if("EAST".equals(constranit)){
			constranit = BorderLayout.EAST;
		}else if("LINE_END".equals(constranit)){
			constranit = BorderLayout.LINE_END;
		}else if("LINE_START".equals(constranit)){
			constranit = BorderLayout.LINE_START;
		}else if("NORTH".equals(constranit)){
			constranit = BorderLayout.NORTH;
		}else if("PAGE_END".equals(constranit)){
			constranit = BorderLayout.PAGE_END;
		}else if("PAGE_START".equals(constranit)){
			constranit = BorderLayout.PAGE_START;
		}else if("SOUTH".equals(constranit)){
			constranit = BorderLayout.SOUTH;
		}else if("WEST".equals(constranit)){
			constranit = BorderLayout.WEST;
		}
		
		LayoutManager layoutMgr = parent.getParent().getLayout();
		if (layoutMgr != null) {
			if (layoutMgr instanceof LayoutManager2) {
				((LayoutManager2) layoutMgr).addLayoutComponent(parent, constranit);
			} else if (constranit instanceof String) {
				layoutMgr.addLayoutComponent((String) constranit, parent);
			}
		}
	}
}