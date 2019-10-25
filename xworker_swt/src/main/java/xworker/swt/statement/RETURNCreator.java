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
package xworker.swt.statement;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class RETURNCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	//设置动作上下文的状态为RETURN
	    actionContext.setStatus(ActionContext.RETURN);
	    
	    //取返回值
        Thing self = (Thing) actionContext.get("self");        
        String variable = self.getString("variable");
        if(variable != null && !"".equals(variable.trim())){
            if(variable.startsWith("\"")){
                return variable.substring(1, variable.length() - 1);
            }else{
                //System.out.println("variable,name=" + variable + ",value=" + actionContext.get(variable));
                return actionContext.get(variable);
            }
        }else{
            String expression = self.getString("expression");
            //System.out.println("expression=" + expression);
            if(expression != null && !"".equals(expression.trim())){
                return OgnlUtil.getValue(expression, actionContext);
            }else{
                return self.doAction("getObject", actionContext);
            }
        }
    }

}