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
package xworker.app.view.extjs.server.tools.grid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataObjectCreator {
    public static Object getInstances(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        HttpSession session = request.getSession();
        Thing dataObjects = (Thing) session.getAttribute(self.getString("dataObjects"));
        if(dataObjects == null){
            dataObjects = new Thing();
            if(!"app_tools_gridDataObject_EDITOR".equals(self.getString("dataObjects"))){
                Thing dataObject = new Thing("xworker.app.view.extjs.server.tools.grid.DataObject"); 
                dataObject.initDefaultValue();
                dataObject.put("name","DataObject");
                
                
                dataObject.put("dataObjects","app_tools_gridDataObject_EDITOR");
                dataObjects.addChild(dataObject);
            }
            session.setAttribute(self.getString("dataObjects"), dataObjects);
        }
        
        return dataObjects;
    }

}