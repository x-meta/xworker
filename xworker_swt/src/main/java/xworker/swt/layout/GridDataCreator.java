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

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilSwt;

public class GridDataCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = -1;
		String selfStyle = self.getString("style");
		if("FILL_BOTH".equals(selfStyle)){
			style = GridData.FILL_BOTH;
		}else if("FILL_HORIZONTAL".equals(selfStyle)){
			style = GridData.FILL_HORIZONTAL;
		}else if("FILL_VERTICAL".equals(selfStyle)){
			style = GridData.FILL_VERTICAL;
		}else if("GRAB_HORIZONTAL".equals(selfStyle)){
			style = GridData.GRAB_HORIZONTAL;
		}else if("GRAB_VERTICAL".equals(selfStyle)){
			style = GridData.GRAB_VERTICAL;
		}
		
		GridData data = null;
		if(style != -1){
		    data = new GridData(style);
		}else{
		    data = new GridData();
		}
		
		int width = self.getInt("width", -1);
		if(width != -1){
		    data.widthHint = UtilSwt.getInt(width);
		}
		int height =  self.getInt("height", -1);
		if(height != -1){
		    data.heightHint = UtilSwt.getInt(height);
		}
		String horizontalAlignment = self.getString("horizontalAlignment");
		if(!"BEGINNING".equals(horizontalAlignment)){
			if("BEGINNING".equals(horizontalAlignment)){
				data.horizontalAlignment = GridData.BEGINNING;
			}else if("CENTER".equals(horizontalAlignment)){
				data.horizontalAlignment = GridData.CENTER;
			}else if("END".equals(horizontalAlignment)){
				data.horizontalAlignment = GridData.END;
			}else if("FILL".equals(horizontalAlignment)){
				data.horizontalAlignment = GridData.FILL;
			}
		}
		
		String verticalAlignment = self.getString("verticalAlignment");
		if(!"CENTER".equals(verticalAlignment)){
			if("BEGINNING".equals(verticalAlignment)){
				data.verticalAlignment = GridData.BEGINNING;
			}else if("CENTER".equals(verticalAlignment)){
				data.verticalAlignment = GridData.CENTER;
			}else if("END".equals(verticalAlignment)){
				data.verticalAlignment = GridData.END;
			}else if("FILL".equals(verticalAlignment)){
				data.verticalAlignment = GridData.FILL;
			}
		}
	
		int horizontalIndent = self.getInt("horizontalIndent", 0);
		if(horizontalIndent!= 0){
		    data.horizontalIndent = horizontalIndent;
		}
		int horizontalSpan = self.getInt("horizontalSpan", 1);
		if(horizontalSpan != 1){
		    data.horizontalSpan = horizontalSpan;
		}
		int verticalSpan = self.getInt("verticalSpan", 1);
		if(verticalSpan != 1){
		    data.verticalSpan = verticalSpan;
		}
		
		Control parent = (Control) actionContext.get("parent");
	    parent.setLayoutData(data);
	    	    
	    if(parent.getParent().getLayout() instanceof FillLayout) {
	    	System.out.println("GridDataCreator : grid data error");
	    }
		
		actionContext.getScope(0).put(self.getString("name"), data);
		return data;       
    }

}