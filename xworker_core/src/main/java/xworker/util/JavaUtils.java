package xworker.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.xmeta.ActionException;

public class JavaUtils {
	
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
}
