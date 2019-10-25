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

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;

public class SoftBevelBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int type = BevelBorder.LOWERED;
		if("RAISED".equals(self.getString("bevelType"))){
			type = BevelBorder.RAISED;
		}
		
		SoftBevelBorder border = null;
		
		Color highlight = AwtCreator.createColor(self, "highlight", actionContext);
		Color shadow = AwtCreator.createColor(self, "shadow", actionContext);
		if(highlight != null || shadow != null){
			border = new SoftBevelBorder(type, highlight, shadow);
		}else{
			Color highlightOuterColor = AwtCreator.createColor(self, "highlightOuterColor", actionContext);
			Color highlightInnerColor = AwtCreator.createColor(self, "highlightInnerColor", actionContext);
			Color shadowOuterColor = AwtCreator.createColor(self, "shadowOuterColor", actionContext);
			Color shadowInnerColor = AwtCreator.createColor(self, "shadowInnerColor", actionContext);
			
			border = new SoftBevelBorder(type, highlightOuterColor, highlightInnerColor, shadowOuterColor, shadowInnerColor);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}