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
package xworker.html.base;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.html.HtmlUtil;
import xworker.util.UtilTemplate;

public class TempalteViewThingCreator {
    public static Object toHtml(ActionContext actionContext) throws Throwable{
        Thing self = (Thing) actionContext.get("self");
        
        Thing desc = self;
        if(!self.getDescriptor().getMetadata().getPath().equals("xworker.html.base.TempalteViewThing")){
        	for(Thing descriptor : self.getAllDescriptors()){
        		if("xworker.html.base.TempalteViewThing".equals(descriptor.getDescriptor().getMetadata().getPath())){
        			desc = descriptor;
        			break;
        		}
        	}
        }
        
        String head = desc.getString("headers");
        if(head != null && !"".equals(head)){
            HtmlUtil.addHeader(desc.getString("headerKey"), head, actionContext);
        }
        
        String objName = desc.getString("selfObjName");
        return UtilTemplate.process(UtilMap.toMap(new Object[]{objName, self}), desc.getString("templatePath"), "freemarker", "utf-8");
    }

}