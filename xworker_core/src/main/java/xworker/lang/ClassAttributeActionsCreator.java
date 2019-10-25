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
package xworker.lang;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class ClassAttributeActionsCreator {
	//private static Logger log = LoggerFactory.getLogger(ClassAttributeActionsCreator.class);
	
    public static Object create(ActionContext actionContext) throws ClassNotFoundException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        List<Object> objs = new ArrayList<Object>();
        String varref = self.getString("varref");
        if(varref != null && !"".equals(varref)){
            objs.add(actionContext.get(varref));
        }else{
            for(Thing child : self.getChilds()){
                objs.add(child.doAction("create", actionContext, UtilMap.toMap(new Object[]{"ident", actionContext.get("ident") + "    "})));
            }
        }
        
        for(Object value : objs){
            //设置属性值
        	CommonMethod.setValue((Class<?>) actionContext.get("cls"), actionContext.get("obj"), self.getDescriptors().get(0), self.getDescriptors().get(0).getString("className"), (Thing) actionContext.get("parent"), value);
        }
        
        return objs;
    }

}