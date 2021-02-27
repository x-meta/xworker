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
package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import xworker.lang.executor.Executor;

public class ObjectPropertiesSetterCreator {
	private static final String TAG = ObjectPropertiesSetterCreator.class.getName();
	
    public static Object run(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");
        
        Object obj = OgnlUtil.getValue(self.getString("objectVarName"), actionContext);
        
        //设置属性
        actionContext.peek().put("_object", obj);
        Thing properties = self.getThing("Properties@0");
        if(properties != null){
            for(Thing property : properties.getChilds()){
                try{
                    property.doAction("run", actionContext);
                }catch(Exception e){
                    Executor.warn(TAG, "set property error, property=" + property.getMetadata().getPath());
                    throw e;
                }
            }
        }
        
        return obj;
    }

}