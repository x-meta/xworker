package xworker.java.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.World;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import xworker.lang.executor.Executor;

public class Javaassist {
	public static Thing createParameterValue(String name) {
		Thing param = new Thing("xworker.ui.function.common.GetParameterValue");
		param.set("name", name);

		Thing value = new Thing("xworker.lang.function.values.StringValue");
		value.set("name", "paramName");
		value.set("value", name);

		param.addChild(value);
		return param;
	}

	/**
	 * 返回Java方法转成函数后的路径。
	 * 
	 * @param methodInfo
	 * @return
	 */
	public static String getMethodFunctionThingPath(MethodInfo methodInfo) {
		String path = "xworker.functions.";
		CtClass cls = methodInfo.getCtClass();
		path = path + cls.getPackageName() + "." + cls.getSimpleName() + "."
				+ getMethodFuncitonName(methodInfo);
		return path;
	}

	public static String createDocScript(String docControl, String thingPath) {
		String script = "<script language=\"javascript\">\r\n"
				+ "    document.location='do?sc=" + docControl + "&path="
				+ thingPath + "';\r\n" + "</script>";
		return script;
	}

	public static String getClassDocDescription(String thingPath) {
		return createDocScript("xworker.functions.ShowClassDoc", thingPath);
	}

	public static String getMethodDocDescription(String thingPath) {
		return createDocScript("xworker.functions.ShowMethodDoc", thingPath);
	}

	public static String getFieldDocDescripiton(String thingPath) {
		return createDocScript("xworker.functions.ShowFieldDoc", thingPath);
	}

	/**
	 * 返回Java方法转成函数的后事物名。
	 * 
	 * @param methodInfo
	 * @return
	 */
	public static String getMethodFuncitonName(MethodInfo methodInfo) {
		CtMethod method = methodInfo.getCtMethod();
		String name = method.getName();
		for (ParameterInfo pinfo : methodInfo.getParameters()) {
			name = name + "_" + pinfo.getName();
		}

		return name;
	}

	/**
	 * 通过CtFeild返回获取对象字段或敬爱字段的值的方法。
	 * 
	 * @param ctField
	 * @return
	 */
	public static Thing getJavaFieldFunction(CtField ctField) {
		if (Modifier.isStatic(ctField.getModifiers())) {
			// 静态字段
			return World.getInstance().getThing(
					"xworker.lang.function.java.GetStaticFieldValue");
		} else {
			// 普通字段
			return World.getInstance().getThing(
					"xworker.lang.function.java.GetFieldValue");
		}
	}

	public static Thing getJavaFieldOgnlInstance(String varName, CtField ctField) {
		if (Modifier.isStatic(ctField.getModifiers())) {
			// 静态字段			
			Thing field = new Thing("xworker.lang.function.java.Ognl");
			String exp = "@" + ctField.getDeclaringClass().getName() + "@" + ctField.getName();
			field.set("expression", exp);
			
			return field;
		} else {
			// 普通字段
			Thing field = new Thing("xworker.lang.function.java.Ognl");
			String exp = null;
			if(varName == null){
				exp = "_object." + ctField.getName();
				field.set("expression", exp);
				Thing param = createParameterValue("_object");
				field.addChild(param);
			}else{
				exp = varName + "." + ctField.getName();
				field.set("expression", exp);
			}
			
			return field;
		}
	}
	
	/**
	 * 返回获取字段的函数实例。
	 * 
	 * @param ctField
	 * @return
	 */
	public static Thing getJavaFieldFunctionInstance(CtField ctField) {
		if (Modifier.isStatic(ctField.getModifiers())) {
			// 静态字段
			Thing field = new Thing(
					"xworker.lang.function.java.GetStaticFieldValue");
			Thing clsThing = new Thing(
					"xworker.lang.function.values.StringValue");
			clsThing.put("name", "className");
			clsThing.put("value", ctField.getDeclaringClass().getName());
			field.addChild(clsThing);

			Thing fieldThing = new Thing(
					"xworker.lang.function.values.StringValue");
			fieldThing.put("name", "name");
			fieldThing.put("value", ctField.getName());
			field.addChild(fieldThing);

			return field;
		} else {
			// 普通字段
			Thing field = new Thing("xworker.lang.function.java.GetFieldValue");

			Thing object = new Thing(
					"xworker.ui.function.swt.function.SelectParentParameterName");
			object.put("name", "object");
			field.addChild(object);

			Thing name = new Thing("xworker.lang.function.values.StringValue");
			name.put("name", "name");
			name.put("value", ctField.getName());
			field.addChild(name);

			return field;
		}
	}

	public static Thing getOgnlFunctionInstance(String varName, MethodInfo methodInfo) {		// ----------------创建调用方法的函数--------------------
		// 创建调用方法的函数实例
		Thing methodInvoker = new Thing("xworker.lang.function.java.Ognl");
		
		String exp = "";
		// 变量参数
		if (Modifier.isStatic(methodInfo.getCtMethod().getModifiers())) {
			exp = "@" + methodInfo.getCtClass().getName() + "@";
		} else {
			if(varName == null){
				exp =  "_object.";
				Thing param = createParameterValue("_object");
				methodInvoker.addChild(param);
			}else{
				exp = varName + ".";
			}
			
		}

		// 获取函数
		exp = exp + methodInfo.getName() + "(";

		int index = 0;
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			if(index > 0){
				exp = exp + ",";
			}
			
			exp = exp +  parameterInfo.getName();
			index++;
		}
		
		exp = exp + ")";
		methodInvoker.set("expression", exp);

		// 设置参数
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = new Thing("xworker.lang.function.Parameter");
			param.set("name", parameterInfo.getName());
			methodInvoker.addChild(param);
		}

		return methodInvoker;
	}
	/**
	 * 返回方法的调用函数实例。
	 * 
	 * @param methodInfo
	 * @return
	 */
	public static Thing getJavaMethodFunctionInstance(MethodInfo methodInfo) {
		// ----------------创建调用方法的函数--------------------
		// 创建调用方法的函数实例
		Thing methodInvoker = new Thing(
				"xworker.lang.function.java.MethodInvoker");

		// 变量参数
		if (!Modifier.isStatic(methodInfo.getCtMethod().getModifiers())) {
			Thing object = createParameterValue("object");
			methodInvoker.addChild(object);
		} else {
			Thing object = new Thing("xworker.lang.function.values.NullValue");
			object.set("name", "object");
			methodInvoker.addChild(object);
		}

		// 获取函数
		Thing method = new Thing("xworker.lang.function.java.GetMethod");
		method.set("name", "method");
		Thing cls = new Thing("xworker.lang.function.values.StringValue");
		cls.set("name", "class");
		cls.set("value", methodInfo.getCtClass().getName());
		method.addChild(cls);

		Thing methodName = new Thing("xworker.lang.function.values.StringValue");
		methodName.set("name", "methodName");
		methodName.set("value", methodInfo.getName());
		method.addChild(methodName);

		Thing paramTypes = new Thing("xworker.lang.function.values.ObjectArray");
		paramTypes.set("name", "paramTypes");
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = new Thing("xworker.lang.function.values.StringValue");
			if ("object".equals(parameterInfo.getName())) {
				param.set("name", parameterInfo.getName() + "1");
			} else {
				param.set("name", parameterInfo.getName());
			}
			param.set("value", parameterInfo.getType());
			paramTypes.addChild(param);
		}
		method.addChild(paramTypes);
		methodInvoker.addChild(method);

		// 设置参数
		Thing params = new Thing("xworker.lang.function.values.ObjectArray");
		params.set("name", "params");
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = createParameterValue(parameterInfo.getName());
			params.addChild(param);
		}

		methodInvoker.addChild(params);

		return methodInvoker;
	}

	/**
	 * 通过MethodInfo获取Java方法的函数原型，如果不存在创建一个。
	 * 
	 * @param methodInfo
	 * @return
	 */
	public static Thing getJavaMethodFunction(MethodInfo methodInfo) {
		String thingPath = getMethodFunctionThingPath(methodInfo); // 要保存的函数事物的路径
		Thing proto = World.getInstance().getThing(thingPath);
		if (proto != null) {
			return proto;
		}

		String thingName = getMethodFuncitonName(methodInfo); // 要保存的函数名称

		// ------------------创建函数原型-------------------
		proto = new Thing("xworker.lang.function.FunctionFunction");
		proto.set("name", thingName);

		// 对象参数
		if (!Modifier.isStatic(methodInfo.getCtMethod().getModifiers())) {
			// 如果不是静态方法，那么需要有对象
			Thing object = new Thing("xworker.lang.function.Function/@thing");
			object.set("name", "object");
			object.set("description", "<p>调用方法的对象实例。</p>");
			proto.addChild(object);
		}
		// 参数参数
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = new Thing("xworker.lang.function.Function/@thing");
			if ("object".equals(parameterInfo.getName())) {
				param.set("name", parameterInfo.getName() + "1"); // 不要和对象重复
			} else {
				param.set("name", parameterInfo.getName());
			}
			proto.addChild(param);
		}

		// 函数函数
		Thing function = new Thing(
				"xworker.lang.function.FunctionFunction/@Function");
		function.set("name", "Function");
		proto.addChild(function);

		// ----------------创建调用方法的函数--------------------
		// 创建调用方法的函数实例
		Thing methodInvoker = new Thing(
				"xworker.lang.function.java.MethodInvoker");
		function.addChild(methodInvoker);

		// 变量参数
		if (!Modifier.isStatic(methodInfo.getCtMethod().getModifiers())) {
			Thing object = createParameterValue("object");
			methodInvoker.addChild(object);
		} else {
			Thing object = new Thing("xworker.lang.function.values.NullValue");
			object.set("name", "object");
			methodInvoker.addChild(object);
		}

		// 获取函数
		Thing method = new Thing("xworker.lang.function.java.GetMethod");
		method.set("name", "method");
		Thing cls = new Thing("xworker.lang.function.values.StringValue");
		cls.set("name", "class");
		cls.set("value", methodInfo.getCtClass().getName());
		method.addChild(cls);

		Thing methodName = new Thing("xworker.lang.function.values.StringValue");
		methodName.set("name", "methodName");
		methodName.set("value", methodInfo.getName());
		method.addChild(methodName);

		Thing paramTypes = new Thing("xworker.lang.function.values.ObjectArray");
		paramTypes.set("name", "paramTypes");
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = new Thing("xworker.lang.function.values.StringValue");
			if ("object".equals(parameterInfo.getName())) {
				param.set("name", parameterInfo.getName() + "1");
			} else {
				param.set("name", parameterInfo.getName());
			}
			param.set("value", parameterInfo.getType());
			paramTypes.addChild(param);
		}
		method.addChild(paramTypes);
		methodInvoker.addChild(method);

		// 设置参数
		Thing params = new Thing("xworker.lang.function.values.ObjectArray");
		params.set("name", "params");
		for (ParameterInfo parameterInfo : methodInfo.getParameters()) {
			Thing param = createParameterValue(parameterInfo.getName());
			params.addChild(param);
		}

		methodInvoker.addChild(params);

		proto.saveAs("xworker_java", thingPath);
		return World.getInstance().getThing(thingPath);
	}

	/**
	 * 通过Java类获取方法信息列表。
	 * 
	 * @param clss
	 * @return
	 * @throws NotFoundException
	 */
	public static List<MethodInfo> getMethods(Class<?> clss)
			throws NotFoundException {
		CtClass ctClass = ClassPool.getDefault().get(clss.getName());
		return getMethods(ctClass);
	}

	/**
	 * 获取类的方法列表。
	 * 
	 * @param ctClass
	 * @return
	 */
	public static List<MethodInfo> getMethods(CtClass ctClass) {
		List<MethodInfo> methods = new ArrayList<MethodInfo>();
		for (CtMethod ctMethod : ctClass.getMethods()) {
			methods.add(new MethodInfo(ctMethod));
		}

		Collections.sort(methods);
		return methods;
	}

	public static  List<ParameterInfo> getParameterInfo(int methodModifiers, javassist.bytecode.MethodInfo methodInfo, CtClass[] parameterTypes) {
		List<ParameterInfo> params = new ArrayList<ParameterInfo>();

		try {
			// 使用javaassist的反射方法获取方法的参数名
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			LocalVariableAttribute attr = null;
			if (codeAttribute != null) {
				attr = (LocalVariableAttribute) codeAttribute
						.getAttribute(LocalVariableAttribute.tag);
			}
			if (attr == null) {
				int index = 0;
				for (CtClass paramCls : parameterTypes) {
					params.add(new ParameterInfo(paramCls.getName(), "param"
							+ (index + 1)));
					index++;
				}
			} else {
				String[] paramNames = new String[parameterTypes.length];
				int pos = Modifier.isStatic(methodModifiers) ? 0 : 1;
				for (int i = 0; i < paramNames.length; i++) {
					try {
						paramNames[i] = attr.variableName(i + pos);
					} catch (Exception e) {
						paramNames[i] = "param" + (i + 1);
					}
				}
				// paramNames即参数名
				for (int i = 0; i < paramNames.length; i++) {
					params.add(new ParameterInfo(
							parameterTypes[i].getName(),
							paramNames[i]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return params;
	}
	
	/**
	 * 获取方法的参数信息。
	 * 
	 * @param ctMethod
	 * @return
	 */
	public static List<ParameterInfo> getParameterInfo(CtMethod ctMethod) {
		try {
			return getParameterInfo(ctMethod.getModifiers(), ctMethod.getMethodInfo(), ctMethod.getParameterTypes());
		}catch(Exception e) {
			Executor.warn(Javaassist.class.getName(), "get method-" + ctMethod.getName() + " parameter type exception", e);
			return Collections.emptyList();
		}
	}
	
	/**
	 * 获取构造函数的参数信息。
	 * 
	 * @param ctConstructor
	 * @return
	 */
	public static List<ParameterInfo> getParameterInfo(CtConstructor ctConstructor) {
		try {
			return getParameterInfo(ctConstructor.getModifiers(), ctConstructor.getMethodInfo(), ctConstructor.getParameterTypes());
		}catch(Exception e) {
			Executor.warn(Javaassist.class.getName(), "get constructor parameter type exception", e);
			return Collections.emptyList();
		}
	}
}
