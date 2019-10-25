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
package xworker.swt.custom;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StackLayoutCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		StackLayout layout = new StackLayout();
		    
		Object control = actionContext.get(self.getString("topControl"));
		if(control != null && control instanceof Control){
			layout.topControl = (Control) control;
		}
	
		Composite parent = (Composite) actionContext.get("parent");
		parent.setLayout(layout);
		parent.layout();
		
		actionContext.getScope(0).put(self.getMetadata().getName(), layout);
		
		return layout;      
	}

}