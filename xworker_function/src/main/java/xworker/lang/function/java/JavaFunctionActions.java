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
package xworker.lang.function.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.ui.function.FunctionRequest;

public class JavaFunctionActions {
	public static String methodInvokerDoCheck(ActionContext actionContext){
		Method method = (Method) actionContext.get("method");
		if(method == null){
			return "Java方法调用是方法不能为空！";
		}
		
		Object[] params = (Object[]) actionContext.get("params");
		if(params == null){
			return "参数不能为空，对于没有参数的方法应设置参数为长度为0的Object[]。";
		}
		
		return null;
	}
	
	public static Object methodInvokerDoFunction(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Object object = actionContext.get("object");
		Method method = (Method) actionContext.get("method");			
		Object[] params = (Object[]) actionContext.get("params");
		return method.invoke(object, params);
	}
	
	/**
	 * 获取对象的指定指定字段的值。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public static Object getFieldValue(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException{
		Object object = actionContext.get("object");
		String name = (String) actionContext.get("name");
		FunctionRequest fr = (FunctionRequest) actionContext.get("request");
		String key = "__field__";
		Field field = (Field) fr.getThing().getCachedData(key);
		if(field == null){
			field = object.getClass().getDeclaredField(name);
			fr.getThing().setCachedData(key, field);
		}
		return field.get(object);		
	}
	
	/**
	 * 获取类的静态字段的值。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public static Object getStaticFieldValue(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException, ClassNotFoundException{
		String className = (String) actionContext.get("className");
		String name = (String) actionContext.get("name");
		FunctionRequest fr = (FunctionRequest) actionContext.get("request");
		String key = "__field__";
		Field field = (Field) fr.getThing().getCachedData(key);
		if(field == null){
			field = Class.forName(className).getDeclaredField(name);
			fr.getThing().setCachedData(key, field);
		}
		
		return field.get(null);	
	}
	
	/**
	 * 获取对象的指定指定字段的值。
	 * 
	 * @param actionContext
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public static void setFieldValue(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException{
		Object object = actionContext.get("object");
		String name = (String) actionContext.get("name");
		FunctionRequest fr = (FunctionRequest) actionContext.get("request");
		String key = "__field__";
		Field field = (Field) fr.getThing().getCachedData(key);
		if(field == null){
			field = object.getClass().getDeclaredField(name);
			fr.getThing().setCachedData(key, field);
		}
		
		field.set(object, actionContext.get("value"));						
	}
	
	/**
	 * 获取类的静态字段的值。
	 * 
	 * @param actionContext
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public static void setStaticFieldValue(ActionContext actionContext) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException, ClassNotFoundException{
		String className = (String) actionContext.get("className");
		String name = (String) actionContext.get("name");
		FunctionRequest fr = (FunctionRequest) actionContext.get("request");
		String key = "__field__";
		Field field = (Field) fr.getThing().getCachedData(key);
		if(field == null){
			field = Class.forName(className).getDeclaredField(name);
			fr.getThing().setCachedData(key, field);
		}
		
		field.set(null, actionContext.get("value"));
	}
	
	/**
	 * 获取Java方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static Object getMethod(ActionContext actionContext) throws ClassNotFoundException, SecurityException, NoSuchMethodException{
		//做缓存		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Object methodObj = null;
		if(request != null){
			methodObj = request.getCachedData();
			if(methodObj != null){
				return methodObj;
			}
		}
				
		//类
		Object clsObj = actionContext.get("class");
		Class<?> cls = null;
		if(clsObj instanceof Class<?>){
			cls = (Class<?>) clsObj;
		}else{
			cls = getClass((String) clsObj);
		}
		
		//方法名称
		String methodName = (String) actionContext.get("methodName");
		
		//参数的类别
		Object[] pTypes = (Object[]) actionContext.get("paramTypes");
		Class<?>[] paramTypes = new Class<?>[pTypes.length];
		for(int i=0; i<paramTypes.length; i++){
			if(pTypes[i] instanceof Class<?>){
				paramTypes[i] = (Class<?>) pTypes[i];
			}else{
				paramTypes[i] = getClass((String) pTypes[i]);
			}
		}
		
		Object mth = cls.getDeclaredMethod(methodName, paramTypes); 
		if(request != null){
			request.setCachedData(mth);
		}

		//返回方法
		return mth;
	}
	
	public static Class<?> getClass(String name) throws ClassNotFoundException{
		if("int".equals(name)){
			return int.class;
		}else if("double".equals(name)){
			return double.class;
		}else if("float".equals(name)){
			return float.class;
		}else if("long".equals(name)){
			return long.class;
		}else if("byte".equals(name)){
			return byte.class;
		}else if("char".equals(name)){
			return char.class;
		}else if("boolean".equals(name)){
			return boolean.class;
		}else if("short".equals(name)){
			return short.class;
		}else{
			return Class.forName(name);
		}
	}
	
	public static Object ognl(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取Ognl表达式
		Object exp = self.getData("exp");
		Long expTime = (Long) self.getData("expTime");
		if(expTime == null || expTime.longValue() != self.getMetadata().getLastModified()){
			exp = Ognl.parseExpression(self.getString("expression"));
			self.setData("exp", exp);
			self.setData("expTime", self.getMetadata().getLastModified());
		}
		
		return OgnlUtil.getValue(exp, actionContext);
	}
	
	public static void main(String args[]){
		try{
			String[] css = new String[10];
			
			int[] ints = new int[11];
			System.out.println(css.getClass().getName());
			System.out.println(ints.getClass().getName());
			
			System.out.println(Class.forName("[I"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}