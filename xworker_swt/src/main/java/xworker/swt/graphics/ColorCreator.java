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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Resource;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.UtilSwt;
import xworker.swt.util.XWorkerTreeUtil;

public class ColorCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
		if("Var".equals(self.getString("type"))){
		    return OgnlUtil.getValue(self.getString("varNam"), actionContext);
		}
		
		String rgb = self.getString("rgb");
		if(rgb == null || rgb == ""){
		    return null;
		}else{
		    Resource color = ResourceManager.createResource(self, actionContext);
	        actionContext.getScope(0).put(self.getString("name"), color);
		    return color;
		}      
	}

    public static Object getKey(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		return self.getString("rgb");     
	}

    public static Object createResource(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		String rgb = self.getString("rgb");
		
		//先判断是否是系统颜色
		Color color = XWorkerTreeUtil.getColor(rgb);
		if(color == null) {
			//不是系统的颜色，按照rgb格式
			int[] rgbs = UtilSwt.parseRGB(rgb);
			if(rgbs == null) {
				return null;
			}
			color = new Color(null, rgbs[0], rgbs[1], rgbs[2]);
		}
		return color;
	}
}