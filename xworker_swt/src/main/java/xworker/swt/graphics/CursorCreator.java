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
package xworker.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Resource;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.util.ResourceManager;

public class CursorCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
		if("Var".equals(self.getString("type"))){
		    return OgnlUtil.getValue(self.getString("varName"), actionContext);
		}
		
	    Resource cursor = ResourceManager.createResource(self, actionContext);
	    actionContext.getScope(0).put(self.getString("name"), cursor);
	    
	    return cursor;
	}

    public static Object getKey(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		return self.getString("style");       
	}

    public static Object createResource(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SWT.NONE;
		String selfStyle = self.getString("style");
		if("CURSOR_ARROW".equals(selfStyle)){
			style = SWT.CURSOR_ARROW;
		}else if("CURSOR_WAIT".equals(selfStyle)){
			style = SWT.CURSOR_WAIT;
		}else if("CURSOR_CROSS".equals(selfStyle)){
			style = SWT.CURSOR_CROSS;
		}else if("CURSOR_APPSTARTING".equals(selfStyle)){
			style = SWT.CURSOR_APPSTARTING;
		}else if("CURSOR_HELP".equals(selfStyle)){
			style = SWT.CURSOR_HELP;
		}else if("CURSOR_SIZEALL".equals(selfStyle)){
			style = SWT.CURSOR_SIZEALL;
		}else if("CURSOR_SIZENESW".equals(selfStyle)){
			style = SWT.CURSOR_SIZENESW;
		}else if("CURSOR_SIZENS".equals(selfStyle)){
			style = SWT.CURSOR_SIZENS;
		}else if("CURSOR_SIZENWSE".equals(selfStyle)){
			style = SWT.CURSOR_SIZENWSE;
		}else if("CURSOR_SIZEWE".equals(selfStyle)){
			style = SWT.CURSOR_SIZEWE;
		}else if("CURSOR_SIZEN".equals(selfStyle)){
			style = SWT.CURSOR_SIZEN;
		}else if("CURSOR_SIZES".equals(selfStyle)){
			style = SWT.CURSOR_SIZES;
		}else if("CURSOR_SIZEE".equals(selfStyle)){
			style = SWT.CURSOR_SIZEE;
		}else if("CURSOR_SIZEW".equals(selfStyle)){
			style = SWT.CURSOR_SIZEW;
		}else if("CURSOR_SIZENE".equals(selfStyle)){
			style = SWT.CURSOR_SIZENE;
		}else if("CURSOR_SIZESE".equals(selfStyle)){
			style = SWT.CURSOR_SIZESE;
		}else if("CURSOR_SIZESW".equals(selfStyle)){
			style = SWT.CURSOR_SIZESW;
		}else if("CURSOR_SIZENW".equals(selfStyle)){
			style = SWT.CURSOR_SIZENW;
		}else if("CURSOR_UPARROW".equals(selfStyle)){
			style = SWT.CURSOR_UPARROW;
		}else if("CURSOR_IBEAM".equals(selfStyle)){
			style = SWT.CURSOR_IBEAM;
		}else if("CURSOR_NO".equals(selfStyle)){
			style = SWT.CURSOR_NO;
		}else if("CURSOR_HAND".equals(selfStyle)){
			style = SWT.CURSOR_HAND;
		}

		return new Cursor(null, style);        
	}

}