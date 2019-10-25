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
import java.awt.Font;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class TitledBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Border childBorder = null;
		Thing childBorders = self.getThing("border@0");
		if(childBorders != null && childBorders.getChilds().size() > 0){
			childBorder = (Border) childBorders.getChilds().get(0).doAction("create", actionContext);
		}
		
		String title = self.getStringBlankAsNull("title");		
		Color titleColor = AwtCreator.createColor(self, "titleColor", actionContext);
		Font titleFont = AwtCreator.createFont(self, "titleFont", actionContext);
		Integer titleJustification = JavaCreator.createInteger(self, "titleJustification");
		Integer titlePosition = null;
		String p = self.getStringBlankAsNull("titlePosition");
		if("ABOVE_BOTTOM".equals(p)){
			titlePosition = TitledBorder.ABOVE_BOTTOM;
		}else if("ABOVE_TOP".equals(p)){
			titlePosition = TitledBorder.ABOVE_TOP;
		}else if("BELOW_BOTTOM".equals(p)){
			titlePosition = TitledBorder.BELOW_BOTTOM;
		}else if("BELOW_TOP".equals(p)){
			titlePosition = TitledBorder.BELOW_TOP;
		}else if("BOTTOM".equals(p)){
			titlePosition = TitledBorder.BOTTOM;
		}else if("CENTER".equals(p)){
			titlePosition = TitledBorder.CENTER;
		}else if("DEFAULT_POSITION".equals(p)){
			titlePosition = TitledBorder.DEFAULT_POSITION;
		}else if("LEADING".equals(p)){
			titlePosition = TitledBorder.LEADING;
		}else if("LEFT".equals(p)){
			titlePosition = TitledBorder.LEFT;
		}else if("RIGHT".equals(p)){
			titlePosition = TitledBorder.RIGHT;
		}else if("TOP".equals(p)){
			titlePosition = TitledBorder.TOP;
		} else if("TRAILING".equals(p)){
			titlePosition = TitledBorder.TRAILING;
		} 
			
		Border border = null;
		if(childBorder == null){
			border = new TitledBorder(title);
		}else if(titleJustification == null && titlePosition == null ){
			if(title == null){
				border = new TitledBorder(childBorder);
			}else{
				border = new TitledBorder(childBorder, title);
			}
		}else if(titleFont != null && titleColor != null){
			border = new TitledBorder(childBorder, title, titleJustification, titlePosition, titleFont, titleColor);
		}else if(titleFont != null){
			border = new TitledBorder(childBorder, title, titleJustification, titlePosition, titleFont);
		}else{
			border = new TitledBorder(childBorder, title, titleJustification, titlePosition);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}