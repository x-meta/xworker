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

import javax.swing.border.LineBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class LineBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Color color = AwtCreator.createColor(self, "color", actionContext);
		Integer thickness = JavaCreator.createInteger(self, "thickness");
		Boolean roundedCorners = JavaCreator.createBoolean(self, "roundedCorners");
		
		LineBorder border = null;
		if(thickness != null && roundedCorners != null){
			
		}else if(color != null && thickness != null && roundedCorners != null){
			border = new LineBorder(color, thickness, roundedCorners);
		}else if(color != null && thickness != null){
			border = new LineBorder(color, thickness);
		}else if(color != null){
			border = new LineBorder(color);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}