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

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilSwt;

public class FormLayoutCreator {

    public static Object create(ActionContext actionContext){	
    	Thing self = (Thing) actionContext.get("self");
		FormLayout layout = new FormLayout();
		
		int marginWidth = self.getInt("marginWidth", 0);
		if(marginWidth != 0)
		    layout.marginWidth = UtilSwt.getInt(marginWidth);
		int marginHeight = self.getInt("marginHeight", 0);
		if(marginHeight != 0)
		    layout.marginHeight = UtilSwt.getInt(marginHeight);
		int marginLeft = self.getInt("marginLeft", 0);
		if(marginLeft != 0)
		    layout.marginLeft = UtilSwt.getInt(marginLeft);
		int marginTop = self.getInt("marginTop", 0);
		if(marginTop != 0)
		    layout.marginTop = UtilSwt.getInt(marginTop);
		int marginRight = self.getInt("marginRight", 0);
		if(marginRight != 0)
		    layout.marginRight = UtilSwt.getInt(marginRight);
		int marginBottom = self.getInt("marginBottom", 0);
		if(marginBottom != 0)
		    layout.marginBottom = UtilSwt.getInt(marginBottom);
		int spacing = self.getInt("spacing", 0);
		if(spacing != 0)
		    layout.spacing = UtilSwt.getInt(spacing);
		
		Composite parent = (Composite) actionContext.get("parent");
		parent.setLayout(layout);
		actionContext.getScope(0).put(self.getString("name"), layout);
		return layout;        
    }

}