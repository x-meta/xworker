package xworker.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.xmeta.ActionException;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class JavaUtils {
	private static final String TAG = JavaUtils.class.getName();
	public static Object newInstance(String className, Class<?>[] parameterTypes, Object[] initargs) {
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> constructor = cls.getConstructor(parameterTypes);
			if(constructor != null) {
				return constructor.newInstance(initargs);
			}
			
			return null;
		}catch(Throwable t) {
			throw new ActionException("NewInstance error", t);
		}
	}
	
	public static Object call(String className, String methodName, Object obj, Class<?>[] parameterTypes, Object[] args) {
		try {
			Class<?> cls = Class.forName(className);
			Method method = cls.getMethod(methodName, parameterTypes);
			return method.invoke(obj, args);
		}catch(Throwable t) {
			throw new ActionException("Call java method error", t);
		}
	}
	
	/**
	 * 设置一个对象的属性值。
	 * 
	 * @param object
	 * @param name
	 * @param value
	 */
	public static void setAttribute(Object object, String name , Object value) {
		try {
			Ognl.setValue(name, object, value);
		} catch (OgnlException e) {
			Executor.warn(TAG, "Set object attribute error, name={}", name, e);
		}
	}
}
