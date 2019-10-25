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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilSwt;

public class RowDataCreator {

    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	int width = self.getInt("width", -1);
		if(width == -1)
		    width = SWT.DEFAULT;
		
		int height = self.getInt("height", -1);
		if(height == -1)
		    height = SWT.DEFAULT;
		
		RowData data = new RowData(UtilSwt.getInt(width), UtilSwt.getInt(height));
		if(self.getBoolean("exclude"))
		    data.exclude = true;
		
		Control parent = (Control) actionContext.get("parent");
		parent.setLayoutData(data);
		    
		actionContext.getScope(0).put(self.getString("name"), data);
		
		return data;        
	}

}