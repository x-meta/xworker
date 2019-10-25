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

import javax.swing.border.EtchedBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.awt.AwtCreator;

public class EtchedBorderCreator {
	public static EtchedBorder create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Integer type = null;
		String etchType = self.getString("etchType");
		if("LOWERED".equals(etchType)){
			type = EtchedBorder.LOWERED;
		}else if("RAISED".equals(etchType)){
			type = EtchedBorder.RAISED;
		}
		
		Color highlight = AwtCreator.createColor(self, "highlight", actionContext);
		Color shadow= AwtCreator.createColor(self, "shadow", actionContext);
		
		EtchedBorder border = null;
		if(type == null && highlight == null && shadow == null){
			border = new EtchedBorder();
		}else if(type == null){
			border = new EtchedBorder(highlight, shadow);
		}else if(highlight == null && shadow == null){
			border = new EtchedBorder(type);
		}else{
			border = new EtchedBorder(type, highlight, shadow);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}