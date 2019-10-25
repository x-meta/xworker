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
package xworker.app.view.swt.app.workbentch.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;

public class WorkbentchActionsGetViewCreator {
	private static Logger log = LoggerFactory.getLogger(WorkbentchActionsGetViewCreator.class);
	
    public static Object run(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        ActionContainer workbentchActions = (ActionContainer) OgnlUtil.getValue(self.getString("workbentchActions"), actionContext);
        if(workbentchActions == null){
            log.info("workbentchActions is null, name=" + self.get("workbentchActions"));
            return null;
        }
        
        Object view = workbentchActions.doAction("getView", UtilMap.toMap(new Object[]{"viewName", self.getString("viewName")}));
        
        String varName = self.getString("varName");
        if(varName != null && !"".equals(varName)){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            if(bindings != null){
                bindings.put(varName, view);
            }
        }
 
        
        return view;
    }

}