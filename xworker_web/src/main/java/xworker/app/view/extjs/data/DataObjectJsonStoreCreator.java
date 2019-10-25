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
package xworker.app.view.extjs.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class DataObjectJsonStoreCreator {
	private static Logger log = LoggerFactory.getLogger(DataObjectJsonStoreCreator.class);
	
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //----------------------------------数据对象-----------------------
        //数据对象
        Object dataObject = self.doAction("getDataObject", actionContext);
        if(dataObject == null){
            log.warn("DataObjectGridPanel: dataObject is null - " + self.getString("dataObject"));
            return null;//self.doAction("toJavaScriptCode", actionContext);
        }
        
        //组件标识，子控件可能会用到
        actionContext.peek().put("storeId", self.getString("storeId"));
        Thing stores = (Thing) self.doAction("createExtStore", actionContext, UtilMap.toMap(new Object[]{"dataObject", dataObject}));
        Thing store = stores.getChilds().get(0);
        //store.storeId = self.name;
        //store.varglobal = "true";
        //store.varname = self.name;
        //log.info("store.varname=" + store.varname);
        
        //--------------------------返回代码-----------------------
        return store.doAction("toJavaScriptCode", actionContext);
    }

    public static Object getExtType(ActionContext actionContext){
        return "Ext.data.JsonStore";
    }

}