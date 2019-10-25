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

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CompoundBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Border outside = null;
		Border inside = null;
		
		Thing outsideBorder = self.getThing("outsideBorder@0");
		Thing insideBorder = self.getThing("insideBorder@0");
		if(outsideBorder != null && outsideBorder.getChilds().size() > 0){
			outside = (Border) outsideBorder.getChilds().get(0).doAction("create", actionContext);
		}
		if(insideBorder != null && insideBorder.getChilds().size() > 0){
			inside = (Border) insideBorder.getChilds().get(0).doAction("create", actionContext);
		}
		
		CompoundBorder border = new CompoundBorder(outside, inside);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}