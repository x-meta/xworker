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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilSwt;

public class RowLayoutCreator {

    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
		RowLayout layout = new RowLayout();
		String selfType = self.getString("type");
		if("SWT.HORIZONTAL".equals(selfType)){
			layout.type = SWT.HORIZONTAL;
		}else if("SWT.VERTICAL".equals(selfType)){
			layout.type = SWT.VERTICAL;
		}
		
	    layout.wrap = self.getBoolean("wrap");
	    layout.fill = self.getBoolean("fill");
	    layout.pack = self.getBoolean("pack");
	    layout.justify = self.getBoolean("justify");
	    
	    int marginWidth = self.getInt("marginWidth", 0);
		if(marginWidth != 0)
		    layout.marginWidth = UtilSwt.getInt(marginWidth);
		int marginHeight = self.getInt("marginHeight", 0);
		if(marginHeight != 0)
		    layout.marginHeight = UtilSwt.getInt(marginHeight);
		int marginLeft = self.getInt("marginLeft", 3);
		if(marginLeft != 3)
		    layout.marginLeft = UtilSwt.getInt(marginLeft);
		int marginTop = self.getInt("marginTop", 3);
		if(marginTop != 3)
		    layout.marginTop = UtilSwt.getInt(marginTop);
		int marginRight = self.getInt("marginRight", 3);
		if(marginRight != 3)
		    layout.marginRight = UtilSwt.getInt(marginRight);
		int marginBottom = self.getInt("marginBottom", 3);
		if(marginBottom != 3)
		    layout.marginBottom = UtilSwt.getInt(marginBottom);
		    
		Composite parent = (Composite) actionContext.get("parent");
		parent.setLayout(layout);
		actionContext.getScope(0).put(self.getString("name"), layout);
		
		return layout;        
    }

}