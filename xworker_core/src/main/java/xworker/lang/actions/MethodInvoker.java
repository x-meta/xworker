package xworker.lang.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class MethodInvoker {
	public static Object run(ActionContext actionContext) throws ClassNotFoundException, OgnlException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Thing self = (Thing) actionContext.get("self");
		
		//获取方法
		Method method = (Method) self.getData("__MethodCache__");
		Long lastModified = (Long) self.getData("__LastModified__");
		if(method == null || self.getMetadata().getLastModified() != lastModified){
			String className = self.getStringBlankAsNull("className");
			String methodName = self.getStringBlankAsNull("methodName");
			String paramClasses = self.getStringBlankAsNull("paramClasses");
			if(className == null || methodName == null){
				throw new ActionException("Class and Method can not be null, thing=" + self.getMetadata().getPath());
			}			
			String ps[] = null;
			if(paramClasses != null){
				ps = paramClasses.split("[,]");
			}
			
			Class<?> cls = Class.forName(className);
			for(Method m : cls.getDeclaredMethods()){
				if(m.getName().equals(methodName)){
					Class<?> types[] = m.getParameterTypes();
					if(ps == null && (types == null  || types.length == 0)){
						method = m;
						break;
					}else if(ps != null && (types != null && types.length == ps.length)){
						boolean ok = true;
						for(int i=0; i<ps.length; i++){
							if(!ps[i].equals(types[i].getName())){
								ok = false;
								break;
							}
						}
						
						if(ok){
							method = m;
							break;
						}
					}
				}
					
			}
			
			if(method == null){
				throw new ActionException("Method " + methodName + " not found, thing=" + self.getMetadata().getName());
			}
			self.setData("__MethodCache__", method);
			self.setData("__LastModified__", self.getMetadata().getLastModified());
		}
		
		//把子事物当做动作执行，并且把结果放到局部中
		Bindings bindings = actionContext.peek();
		for(Thing child : self.getChilds()){
			bindings.put(child.getMetadata().getName(), child.getAction().run(actionContext));
		}
		
		//执行方法
		Object[] args = new Object[method.getParameterTypes().length];
		for(int i=0; i<args.length; i++){
			String name = self.getStringBlankAsNull("param" + i);
			if(name != null){
				args[i] = actionContext.get(name);
			}
		}
		Object object = UtilData.getData(self, "object", actionContext);
		return method.invoke(object, args);
	}
}
