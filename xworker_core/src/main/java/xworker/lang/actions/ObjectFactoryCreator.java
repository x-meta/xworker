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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

public class ObjectFactoryCreator {
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException{
    	Thing self = (Thing) actionContext.get("self");
    	String className = self.getString("className");
    	
        Class<?> cls = Class.forName(className);
        Thing constractor = self.getThing("Constractor@0");
        Object obj = null;
        if(constractor != null){
            Constructor<?> c = null;
            List<Object> params = new ArrayList<Object>();
            for(Thing child : constractor.getChilds()){
            	String thingName = child.getThingName();
                if("PropertySetter".equals(thingName) || "ValueFactory".equals(thingName)){
                    params.add(child.doAction("run", actionContext));
                }
            }
        
            for(Constructor<?> con : cls.getConstructors()){
                boolean ok = true;
                Class<?>[] cparamCls = con.getParameterTypes();
                if(cparamCls.length == params.size()){
                    for(int i=0; i<cparamCls.length; i++){
                        Object param = params.get(i);
                        if(param != null && cparamCls[i] != param.getClass()){
                            ok = false;
                            break;
                        }
                    }
                }else{
                    ok = false;
                }
                
                if(ok){
                    c = con;
                    break;
                }
            }
            
            if(c == null){
                //找不到对应构造函数
                obj = null;
            }else{
                obj = c.newInstance(params.toArray(new Object[params.size()]));
            }
        }else{
            obj = cls.newInstance();
        }
        
        //设置属性
        actionContext.peek().put("_newInstance", obj);
        List<Thing> properties = (List<Thing>) self.get("Properties@");
        if(properties != null){
            for(Thing property : properties){
                property.doAction("run", actionContext);
            }
        }
        
        //放入到变量上下文中
        UtilAction.putVarByActioScope(self, self.getString("varName"), obj, actionContext);
        
        return obj;
    }

}