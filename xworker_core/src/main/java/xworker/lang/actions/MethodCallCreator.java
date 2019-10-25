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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilJava;

import ognl.OgnlException;

public class MethodCallCreator {
	private static Logger log = LoggerFactory.getLogger(MethodCallCreator.class);
	
    public static Object run(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, OgnlException, ClassNotFoundException, NoSuchMethodException, SecurityException{
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        //获取对象
        Object obj = null;
        String object = self.getString("object");
        if(object != null && !"".equals(object)){
            obj = OgnlUtil.getValue(object, actionContext);
        }
        
        //类
        Class<?> cls = null;
        if(obj != null){
            cls = obj.getClass();
        }else{
            if(self.getBoolean("actionClass")){
                cls = world.getActionClass(self.getString("className"), actionContext);
            }else{
                cls = Class.forName(self.getString("className"));
            }
        }
        
        //参数列表
        List<Object> params = new ArrayList<Object>();
        for(Thing child : self.getChilds()){
            //log.info(child.thingName);
        	String thingName = child.getThingName();
            if("PropertyGetter".equals(thingName) || "ValueFactory".equals(thingName)){
                Object param = child.doAction("run", actionContext);
                //if(param == null){
                //    log.info("MethodCall: " + child.metadata.label + "=null");
                //}else{
                //    log.info("MethodCall: " + child.metadata.label + "=" + param.getClass());
                //}
                params.add(param);
            }
        }
        
        //方法
        String methodName = self.getString("method");
        Method method = UtilJava.getMethod(cls, methodName, params);

        if(method != null){
            //调用方法和返回结果
            Object result = method.invoke(obj, params.toArray(new Object[params.size()]));
            UtilAction.putVarByActioScope(self, self.getString("varName"), result, actionContext);
            
            return result;
        }else{
            log.info("MethodCalll: method not exists, name=" + self.getString("method") + ",thing=" + self.getMetadata().getPath());
            return null;
        }
    }

}