package xworker.swt.xworker;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.World;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import xworker.java.assist.Javaassist;
import xworker.java.assist.ParameterInfo;
import xworker.lang.executor.Executor;

public class ClassUtils {
	private static final String TAG = ClassUtils.class.getName();
	private static ClassPool clsPool = new ClassPool(true);

	static{
		String paths = World.getInstance().getClassLoader().getClassPath();
		for(String path : paths.split("[" + File.pathSeparator + "]")){
			try{
				clsPool.appendClassPath(path);
			}catch(Exception e){
				
			}
		}
	}
	
	public static CtClass get(String classname) throws NotFoundException {
		return clsPool.get(classname);
	}
	
	public static List<CodeAssitContent> getClassContents(String className){
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		try {
			CtClass ctClass = clsPool.get(className);
			if(ctClass != null){
				for(CtField field : ctClass.getFields()){
					if((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
						list.add(new CodeAssitContent(field.getName(), field.getName() + ": " + field.getType(), "fieldImage"));
					}
				}
				
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
						list.add(new CodeAssitContent(methodName, label, "methodImage"));
					}
				}
			}
		} catch (NotFoundException e) {
			Executor.warn(TAG, "get class contents error", e);
		}				
	
		Collections.sort(list);
		return list;
	}
}
