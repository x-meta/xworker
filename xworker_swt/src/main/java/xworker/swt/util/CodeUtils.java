package xworker.swt.util;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import xworker.lang.VariableDesc;

public class CodeUtils {

	/**
	 * 查找事物模型的变量定义，通过sentens
	 * 
	 * @param sentens
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static VariableDesc getVariableDesc(List<String> sentens, Thing thing, ActionContext actionContext) {
		List<VariableDesc> varDescs = VariableDesc.getVariableDescs(thing, actionContext);
		
		String varName = sentens.get(0);
		int index = 0;
		while(varName != null) {
			//遍历sentens
			for(VariableDesc var : varDescs) {
				if(var.getName().equals(varName)) {
					return var;
				}
				
			}
			
			index++;
			if(index < sentens.size()) {
				varName = varName + "." + sentens.get(index);
			}else {
				varName = null;
			}
		}
		
		return null;
	}
	
	public static boolean isObject(VariableDesc varDesc) {
		return "object".equals(varDesc.getType());
	}
	
	
	/**
	 * 通过CtClass返回语句列表末尾对应的类。如a.b.c，已知a的类型，要返回c的类型。
	 * 
	 * @param sentens
	 * @param sindex
	 * @param cls
	 * @return
	 * @throws NotFoundException 
	 */
	public static CtClass getClass(List<String> sentens, int sindex, CtClass cls) throws NotFoundException {
		if(cls == null) {
			return null;
		}
		
		for(int i=sindex; i<sentens.size(); i++) {
			String name = sentens.get(i);
			//先找方法
			boolean have = false;
			for(CtMethod method : cls.getMethods()){				
				if(method.getName().equals(name)){
					have = true;
					cls = method.getReturnType();
					if(cls == null){
						return null;
					}
				}
			}
			
			//找方法
			if(!have){
				CtField field = cls.getField(name);
				if(field == null){
					return null;
				}
				
				cls = field.getType();
			}
		}
		
		return cls;
	}
}
