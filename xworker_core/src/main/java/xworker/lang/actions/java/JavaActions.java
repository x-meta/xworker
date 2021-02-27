package xworker.lang.actions.java;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.lang.actions.ActionUtils;

public class JavaActions {
	public static Object newInstance(ActionContext actionContext) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		Thing self = actionContext.getObject("self");
		
		String className =UtilData.getString(self, "className", actionContext);
		if(className == null){
			throw new ActionException("Class name is null, action=" + self.getMetadata().getPath());
		}
		
		List<Thing> childs = self.getChilds();
		Object[] args = new Object[childs.size()];
		Class<?> cls = Class.forName(className);
		for(int i=0; i < childs.size(); i++){
			Thing child = childs.get(i);
			Object value = child.getAction().run(actionContext, null, true);
			args[i] = value;
		}
		
		Class<?>[] paramTypes = ActionUtils.getParamTypes(cls, args, null);
		if(self.getBoolean("exact")){			
			if(paramTypes == null){
				return ConstructorUtils.invokeExactConstructor(cls, args);
			}else{
				return ConstructorUtils.invokeExactConstructor(cls, args, paramTypes);
			}
		}else{
			if(paramTypes == null){
				return ConstructorUtils.invokeConstructor(cls, args);
			}else{
				return ConstructorUtils.invokeConstructor(cls, args, paramTypes);
			}
		}
	}
	
	public static Object createObject(ActionContext actionContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, OgnlException {
		Thing self = actionContext.getObject("self");
		
		//创建对象
		String className =UtilData.getString(self, "className", actionContext);
		if(className == null){
			throw new ActionException("Class name is null, action=" + self.getMetadata().getPath());
		}
		
		Thing constructor = self.getThing("Constructor@0");
		List<Thing> constructorChilds = null;
		if(constructor != null) {
			constructorChilds = constructor.getChilds();
		}else {
			constructorChilds = Collections.emptyList();
		}
		
		Object[] args = new Object[constructorChilds.size()];
		Class<?> cls = null;
		ClassLoader classLoader = self.doAction("getClassLoader", actionContext);
		if(classLoader != null) {
			cls = classLoader.loadClass(className);
		}else {
			cls = Class.forName(className);
		}
		
		for(int i=0; i < constructorChilds.size(); i++){
			Thing child = constructorChilds.get(i);
			Object value = child.getAction().run(actionContext, null, true);
			args[i] = value;
		}
		
		Object obj = null;
		Class<?>[] paramTypes = ActionUtils.getParamTypes(cls, args, null);
		if(self.getBoolean("exact")){			
			if(paramTypes == null){
				obj = ConstructorUtils.invokeExactConstructor(cls, args);
			}else{
				obj = ConstructorUtils.invokeExactConstructor(cls, args, paramTypes);
			}
		}else{
			if(paramTypes == null){
				obj = ConstructorUtils.invokeConstructor(cls, args);
			}else{
				obj =  ConstructorUtils.invokeConstructor(cls, args, paramTypes);
			}
		}
		
		//设置对象的属性
		for(Thing attributes : self.getChilds("Attributes")) {
			for(Thing child : attributes.getChilds()) {
				String name = child.getMetadata().getName();
				
				Object value = child.getAction().run(actionContext);
				
				Ognl.setValue(name, obj, value);
			}
		}
		
		return obj;
	}
	
	public static Object invokeMethod(ActionContext actionContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, OgnlException{
		Thing self = actionContext.getObject("self");
		
		String methodName = UtilData.getString(self, "methodName", actionContext);
		if(methodName == null){
			throw new ActionException("Method name is null, action=" + self.getMetadata().getPath());
		}
		
		Object object = UtilData.getData(self, "object", actionContext);
		if(object == null){
			throw new ActionException("Object name is null, action=" + self.getMetadata().getPath());
		}
		
		List<Thing> childs = self.getChilds();
		Object[] args = new Object[childs.size()];
		for(int i=0; i < childs.size(); i++){
			Thing child = childs.get(i);
			Object value = child.getAction().run(actionContext, null, true);
			args[i] = value;
		}
		
		Class<?>[] paramTypes = ActionUtils.getParamTypes(object.getClass(), args, methodName);
		if(self.getBoolean("exact")){
			if(paramTypes == null){
				return MethodUtils.invokeExactMethod(object, methodName, args);
			}else{
				return MethodUtils.invokeExactMethod(object, methodName, args, paramTypes);
			}
			
		}else{
			if(paramTypes == null){
				return MethodUtils.invokeMethod(object, methodName, args);
			}else{
				return MethodUtils.invokeMethod(object, methodName, args, paramTypes);
			}
		}
	}
	
	public static Object invokeStaticMethod(ActionContext actionContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, OgnlException, ClassNotFoundException{
		Thing self = actionContext.getObject("self");
		
		String methodName = UtilData.getString(self, "methodName", actionContext);
		if(methodName == null){
			throw new ActionException("Method name is null, action=" + self.getMetadata().getPath());
		}
		
		String className =UtilData.getString(self, "className", actionContext);
		if(className == null){
			throw new ActionException("Class name is null, action=" + self.getMetadata().getPath());
		}
		
		
		Class<?> cls = Class.forName(className);
		List<Thing> childs = self.getChilds();
		Object[] args = new Object[childs.size()];
		for(int i=0; i < childs.size(); i++){
			Thing child = childs.get(i);
			Object value = child.getAction().run(actionContext, null, true);
			args[i] = value;
		}
		
		Class<?>[] paramTypes = ActionUtils.getParamTypes(cls, args, methodName);
		if(self.getBoolean("exact")){
			if(paramTypes == null){
				return MethodUtils.invokeExactStaticMethod(cls, methodName, args);
			}else{
				return MethodUtils.invokeExactStaticMethod(cls, methodName, args, paramTypes);
			}
		}else{
			if(paramTypes == null){
				return MethodUtils.invokeStaticMethod(cls, methodName, args);
			}else{
				return MethodUtils.invokeStaticMethod(cls, methodName, args, paramTypes);
			}			
		}
	}
	
	public static Object getFieldValue(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		
		Object object = self.doAction("getObject", actionContext);
		String fieldName = (String) self.doAction("getFieldName", actionContext);
		Boolean forceAccess = (Boolean) self.doAction("isForceAccess", actionContext);
		
		return FieldUtils.readField(object, fieldName, forceAccess);
	}
	
	public static Object getStaticFieldValue(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		
		Class<?> cls = (Class<?>) self.doAction("getClass", actionContext);
		String fieldName = (String) self.doAction("getFieldName", actionContext);
		Boolean forceAccess = (Boolean) self.doAction("isForceAccess", actionContext);
		
		return FieldUtils.readStaticField(cls, fieldName, forceAccess);
	}
	
	public static Object getClass(ActionContext actionContext) throws IllegalAccessException, ClassNotFoundException{
		Thing self = actionContext.getObject("self");
		
		String className = UtilData.getString(self, "className", actionContext);
		return Class.forName(className);
	}
	
	public static void writeFieldValue(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		
		Object object = self.doAction("getObject", actionContext);
		String fieldName = (String) self.doAction("getFieldName", actionContext);
		Boolean forceAccess = (Boolean) self.doAction("isForceAccess", actionContext);
		Object value = self.doAction("getValue", actionContext);
		
		FieldUtils.writeField(object, fieldName, value, forceAccess);
	}
	
	
}
