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
package xworker.app.model.tree.actions;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class TreeModelActionsUpdateCreator {
	private static final String TAG = TreeModelActionsUpdateCreator.class.getName();
	
    public static void run(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        Thing treeModel = (Thing) OgnlUtil.getValue(self.getString("treeModelName"), actionContext);
        if(treeModel != null){
            Map<String, Object> params = new HashMap<String, Object>();
            Object node = OgnlUtil.getValue(self.getString("nodeDataName"), actionContext);
            if("Nodes".equals(self.getString("nodeDataType"))){
                params.put("nodes", node);
            }else{
                params.put("node", node);
            }
            
            params.put("updateChilds", self.getBoolean("updateChilds"));
            treeModel.doAction("updateNode", actionContext, params);    
        }else{
            Executor.info(TAG, "TreeModelActions: treeModel is null, name=" + self.getString("treeModelName"));
        }
    }

}