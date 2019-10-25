package xworker.lang.actions.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.mchange.v1.util.ArrayUtils;

public class ArrayUtilsActions {
	public static Object emptyArray(ActionContext actionContext) throws IllegalAccessException{
		Thing self = actionContext.getObject("self");
		
		return FieldUtils.readStaticField(org.apache.commons.lang3.ArrayUtils.class	, (String) self.doAction("getType", actionContext));
	}
	
	public static Object add(ActionContext actionContext) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException{
		Thing self = actionContext.getObject("self");
		
		Object array = self.doAction("getArray", actionContext);
		Integer index  = (Integer) self.doAction("getIndex", actionContext);
		Object element = self.doAction("getElement", actionContext);
		
		if(index == null || index < 0){
			return MethodUtils.invokeExactStaticMethod(ArrayUtils.class, "add", new Object[]{array, element});
		}else{
			return MethodUtils.invokeExactStaticMethod(ArrayUtils.class, "add", new Object[]{array, index, element});
		}
	}
}
