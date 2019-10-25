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
package xworker.swt.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FillLayoutCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		FillLayout layout = new FillLayout();

		String type = self.getString("type");
		if("SWT.HORIZONTAL".equals(type)){
			layout.type = SWT.HORIZONTAL;
		}else if("SWT.VERTICAL".equals(type)){
			layout.type = SWT.VERTICAL;
		}
		
		if(self.getStringBlankAsNull("marginHeight") != null){
			layout.marginHeight = self.getInt("marginHeight");
		}
		if(self.getStringBlankAsNull("marginWidth") != null){
			layout.marginWidth = self.getInt("marginWidth");
		}
		if(self.getStringBlankAsNull("spacing") != null){
			layout.spacing = self.getInt("spacing");
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		parent.setLayout(layout);
		
		actionContext.getScope(0).put(self.getString("name"), layout);	
		
		return layout;
    }

}