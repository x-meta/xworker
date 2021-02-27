package xworker.java.assist;

import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

public class MethodInfo implements DocInfo , java.lang.Comparable<MethodInfo>{
	CtMethod ctMethod;

	public MethodInfo(CtMethod ctMethod) {
		this.ctMethod = ctMethod;
	}

	public List<ParameterInfo> getParameters(){
		return Javaassist.getParameterInfo(ctMethod);
	}
	
	public CtClass getCtClass(){
		return ctMethod.getDeclaringClass();
	}
	
	public String getName(){
		return ctMethod.getName();
	}
	
	public String toString(){
		String params = "";
		for(ParameterInfo pinfo : this.getParameters()){
			String type = pinfo.getType();
			int index = type.lastIndexOf(".");
			if(index != -1){
				type = type.substring(index + 1, type.length());
			}
			if(params.equals("")){
				params = type + " " + pinfo.getName();
			}else{
				params = params + ", " + type + " " + pinfo.getName();;
			}
		}
		return ctMethod.getName() + "(" + params + ")";
	}
	
	@Override
	public String getJavaDoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJavaCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(MethodInfo o) {
		return this.getName().compareTo(o.getName());
	}

	public CtMethod getCtMethod() {
		return ctMethod;
	}

}
