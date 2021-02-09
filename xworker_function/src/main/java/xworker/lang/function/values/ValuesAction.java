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
package xworker.lang.function.values;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;
import xworker.util.UtilAction;

public class ValuesAction {
	//private static Logger logger = LoggerFactory.getLogger(ValuesAction.class);
	
	public static int IntValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getInt("value");
	}
	
	public static double DoubleValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getDouble("value");		
	}
	
	public static char CharValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getChar("value");
	}
	
	public static float FloatValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getFloat("value");
	}
	
	@SuppressWarnings("rawtypes")
	public static Map newMap(ActionContext actionContext){
		return new HashMap();
	}
	
	@SuppressWarnings("rawtypes")
	public static List newList(ActionContext actionContext){
		return new ArrayList();
	}
	
	@SuppressWarnings("rawtypes")
	public static Set newSet(ActionContext actionContext){
		return new HashSet();
	}
	
	@SuppressWarnings("rawtypes")
	public static Stack newStack(ActionContext actionContext){
		return new Stack();
	}
	
	public static boolean BooleanValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getBoolean("value");		
	}
	
	public static String textFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing descriptor = FunctionRequest.getRealDescriptor(self.getDescriptor());

		return descriptor.getString("text");
	}
	
	public static long LongValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getLong("value");
	}
	
	public static byte ByteValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getByte("value");		
	}
	
	public static BigInteger BigIntegerValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return self.getBigInteger("value");
	}
	
	public static BigDecimal BigDecimalValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		return self.getBigDecimal("value");
	}
	
	public static String StringValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String value = self.getStringBlankAsNull("value");
		if(value == null){
			String whenBlank = self.getString("whenBlank");
			if("null".equals(whenBlank)){
				value = null;
			}else if("blank".equals(whenBlank)){
				value = "";
			}else{				
				return null;
			}
		}
			
		return value;		
	}	
	
	public static Object GroovyValue(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
	
		try {
			return UtilAction.runGroovyAction(self, actionContext);
		}catch(Exception e) {
			throw new ActionException(e);
		}
		/*
		try{
			Bindings bindings = actionContext.push();
			bindings.put("self", self);
			bindings.setCaller(self, "groovyValue");

			//模拟groovy动作，所以变量要压两次栈
			actionContext.push();
			
			return GroovyAction.run(actionContext);			
		}catch(Exception e){
			throw new ActionException(e);
		}finally{
			actionContext.pop();
			actionContext.pop();
		}*/
	}
	
	public static Object NullValue(ActionContext actionContext){
		return null;
	}
	
	/**
	 * 对象数组。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object[] objectArrayDoFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<Thing> params = self.getChilds();
		Object[] values = new Object[params.size()];
		for(int i=0; i<params.size(); i++){
			values[i] = actionContext.get(params.get(i).getMetadata().getName());
		}
		
		return values;
	}
	
	public static String callbackParamValueDoCheck(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		if(self.getStringBlankAsNull("paramName") == null){
			return "请设置函数的paramName属性。";
		}
				
		return null;
		
	}
	
	public static Object callbackParamValueDoFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String paramName = self.getStringBlankAsNull("paramName");
		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		FunctionCallback callback = request.getCallback();
		if(callback != null){
			FunctionParameter parameter = callback.getRequest().getParameter(paramName);
			if(parameter != null){
				return parameter.getValue();
			}
		}

		return null;
	}
}