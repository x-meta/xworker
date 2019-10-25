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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Resource;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.util.ResourceManager;

public class FontCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
	    Thing self = (Thing) actionContext.get("self");
	    
		if("var".equals(self.getString("type"))){
		    return OgnlUtil.getValue(self.getString("varName"), actionContext);
		}
		
	    Resource font = ResourceManager.createResource(self, actionContext);
        actionContext.getScope(0).put(self.getString("name"), font);
	    return font;
	}

    public static Object getKey(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		return self.getString("fontData");        
	}

    public static Object createResource(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		String fontStr = self.getString("fontData");
		if(fontStr == null || "".equals(fontStr)){
		    return null;
		}
		
		if(fontStr.startsWith("\"") && fontStr.endsWith("\"")){
		    fontStr = fontStr.substring(1, fontStr.length() - 1);
		}
		
		String[] fs = fontStr.split("[|]");
		String name = "";
		int height = -1;
		int style = SWT.NORMAL;
		
		if(fs.length >= 1){
			name = fs[0];
		}
		
		if(fs.length >= 2){
			height = Integer.parseInt(fs[1]);
		}
		
		if(fs.length >= 3){
			style = Integer.parseInt(fs[2]);
		}
		
		FontData fontData = new FontData(name, height, style);
		return new Font(null, fontData);        
	}

}