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

import javax.swing.border.EmptyBorder;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class EmptyBorderCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int top = self.getInt("top");
		int left = self.getInt("left");
		int bottom = self.getInt("bottom");
		int right = self.getInt("right");
		
		EmptyBorder border = new EmptyBorder(top, left, bottom, right);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), border);
		
		return border;
	}
}