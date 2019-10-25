package xworker.swt.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import xworker.java.assist.Javaassist;
import xworker.java.assist.ParameterInfo;
import xworker.swt.xwidgets.SelectContent;

public class JavaUtils {
	
	/**
	 * 返回执行类的可选择方法列表。
	 * 
	 * value是方法名，label是显示的包括参数的， object是List&lt;ParameterInfo&gt;。
	 * 
	 * @param className 类名
	 * @return 适配的方法列表
	 * 
	 * @throws NotFoundException 异常
	 */
	public static List<SelectContent> getMethodsForSelect(String className) throws NotFoundException{
		List<SelectContent> list = new ArrayList<SelectContent>();
		
		CtClass ctClass = ClassPool.getDefault().get(className);
		if(ctClass != null){
			for(CtMethod method : ctClass.getMethods()){
				if((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
					List<ParameterInfo> params = Javaassist.getParameterInfo(method);
					String label = method.getName() + "(";
					String methodName = method.getName() + "(";
					for(int i=0; i<params.size(); i++){
						methodName = methodName + params.get(i).getName();
						String type = params.get(i).getType();
						int lastIndex = type.lastIndexOf(".");
						if(lastIndex != -1){
							type = type.substring(lastIndex + 1, type.length());
						}
						label = label + type + " " + params.get(i).getName();
						
						if(i < params.size() - 1){
							methodName = methodName + ", ";
							label = label + ", ";
						}
					}
					
					CtClass rcls = method.getReturnType();
					String rtype = "void";
					if(rcls != null){
						rtype = rcls.getSimpleName();
					}
					methodName = methodName + ")";
					label = label + "): " + rtype;
					SelectContent content = new SelectContent(method.getName(), label, "icons/debug/public_co.gif"); 
					content.object = params;
					list.add(content);
				}
			}
		}
		
		Collections.sort(list);
		return list;
	}
}
