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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;

public class TableCursorCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(self.getBoolean("BORDER")){
		    style = style | SWT.BORDER;
		}
		
		Table parent = (Table) actionContext.get("parent");
		TableCursor cursor = new TableCursor(parent, style);
		
		//背景颜色
		Color background = (Color) ResourceManager.createResource(self.getString("background"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(background != null){
		    cursor.setBackground(background);
		}
		
		//字体颜色
		Color foreground = (Color) ResourceManager.createResource(self.getString("foreground"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(foreground != null){
		    cursor.setForeground(foreground);
		}
		
		try{
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", cursor);
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		actionContext.getScope(0).put(self.getString("name"), cursor);
		return cursor;
	}

}