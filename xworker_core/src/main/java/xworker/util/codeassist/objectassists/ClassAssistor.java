package xworker.util.codeassist.objectassists;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import xworker.java.assist.Javaassist;
import xworker.java.assist.ParameterInfo;
import xworker.lang.VariableDesc;
import xworker.lang.executor.Executor;
import xworker.swt.xworker.ClassUtils;
import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.ObjectAssistor;

public class ClassAssistor implements ObjectAssistor{
	private static final String TAG = ClassAssistor.class.getName();
	@Override
	public List<CodeAssitContent> getContents(VariableDesc var, Thing thing, ActionContext actionContext) {
		List<CodeAssitContent> list = new ArrayList<CodeAssitContent>();
		String className = var.getClassName();
		//过滤泛型，如Map<String, Object>
		int index = className.indexOf("<");
		if(index != -1) {
			className = className.substring(0, index);
		}
		initContents(className, list);
		
		return list;
	}
	
	public static void initContents(String className, List<CodeAssitContent> list) {
		try {			
			CtClass ctClass = ClassUtils.get(className);
			if(ctClass != null){
				for(CtField field : ctClass.getFields()){
					if((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
						list.add(new CodeAssitContent(field.getName(), field.getName() + ": " + field.getType().getName(), "fieldImage"));
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

				for(CtConstructor contr : ctClass.getConstructors()) {
					if((contr.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC){
						List<ParameterInfo> params = Javaassist.getParameterInfo(contr);
						String label = "new " + ctClass.getSimpleName() + "(";
						String methodName = "(";
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
						
						methodName = methodName + ");";
						label = label + ");";
						list.add(new CodeAssitContent(methodName, label, "methodImage"));
					}
				}
			}
		}catch(Exception e) {
			Executor.warn(TAG, "Get class content error, " + e.getMessage());
		}
	}
}
