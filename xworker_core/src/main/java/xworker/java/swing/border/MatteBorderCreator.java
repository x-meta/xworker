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
package xworker.java.swing.border;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.border.MatteBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;
import xworker.java.swing.SwingCreator;

public class MatteBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int top = self.getInt("top");
		int left = self.getInt("left");
		int bottom = self.getInt("bottom");
		int right = self.getInt("right");
		Color matteColor = AwtCreator.createColor(self, "matteColor", actionContext);
		Icon tileIcon = SwingCreator.createIcon(self, "tileIcon", actionContext);
		
		MatteBorder border = null;
		if(top ==0 && left == 0 && bottom == 0 && right == 0 && matteColor == null){
			border = new MatteBorder(tileIcon);
		}else if(matteColor == null){
			border = new MatteBorder(top, left, bottom, right, tileIcon);
		}else{
			border = new MatteBorder(top, left, bottom, right, matteColor);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}