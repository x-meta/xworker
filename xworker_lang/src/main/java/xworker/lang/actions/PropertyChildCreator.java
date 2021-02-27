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

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class PropertyChildCreator {
	private static final String TAG = PropertyChildCreator.class.getName();
	
    public static Object run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        String object = self.getString("object");
        
        if(object == null || "".equals(object)){
            Executor.warn(TAG, "object not setted, path=" + self.getMetadata().getPath());
            return null;
        }
        
        //执行本动作的事物
        List<Thing> things = actionContext.getThings();
        Thing realSelf = null;
        if(things.size() > 1){
            realSelf = things.get(things.size() - 2);
        }
        if(realSelf == null){
            Executor.warn(TAG, "PropertyChild should run as thing action");
            return null;
        }
        if(realSelf.getChilds().size() <= 0){
            return null;
        }
        
        //要设置值的对象
        Object obj = OgnlUtil.getValue(object, actionContext);
        if(obj == null){
            return null;
        }
        
        //子节点和设置属性
        Object result = realSelf.getChilds().get(0).doAction(self.getString("method"), actionContext);
        if(result == null && self.getBoolean("ignoreNull")){
            return null;
        }
        OgnlUtil.setValue(self.getString("propertyName"), obj, result);
        
        //返回值
        if(self.getBoolean("returnNull")){
            return null;
        }else{
            return result;
        }
    }

}