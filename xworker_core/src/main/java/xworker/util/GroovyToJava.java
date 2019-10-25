/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

/**
 * 并不是把Groovy的代码转化成Java代码，而是一些动作使用Groovy编写了，为了性能需要用Java重写，这里只是一个简单的工具把Groovy动作
 * 变成Java动作。
 * 
 * @author zyx
 *
 */
public class GroovyToJava {
	public static List<GroovyCode> findGroovyAction(Thing thing){
		List<GroovyCode> methodList = new ArrayList<GroovyCode>();
		Map<String, Integer> context = new HashMap<String, Integer>();
		
		findGroovyAction(thing, methodList, context);
		
		return methodList;
	}
	
	public static void findGroovyAction(Thing thing, List<GroovyCode> methodList, Map<String, Integer> context){
		if("GroovyAction".equals(thing.getThingName())){
			methodList.add(new GroovyCode(thing, context));
		}
		
		for(Thing child : thing.getChilds()){
			findGroovyAction(child, methodList, context);
		}
	}
	
	public static void changeToJavaAction(Thing thing, String root){
		List<GroovyCode> codes = findGroovyAction(thing);
		if(codes.size() > 0){
			String className = thing.getMetadata().getCategory().getName() + "." + getJavaName(thing);
			if(isClassFileExists(root, className)){
				for(GroovyCode code : codes){
					Thing child = code.thing;
					child.put("code", null);
					child.put("descriptors", "xworker.lang.actions.Actions/@JavaAction");
					if(child.getBoolean("useOtherAction")){
						
					}else{
						child.put("useOuterJava", "true");
						child.put("outerClassName", className);
						child.put("methodName", code.methodName);
					}
				}
			}
			
			thing.save();
		}
	}
	
	public static boolean isClassFileExists(String root, String className){
		File file = new File(root + "/" + className.replace('.', '/') + ".java");
		return file.exists();
	}
	
	public static String getJavaName(Thing thing){
		String javaName = null;
		if(thing.getParent() == null){
			javaName = thing.getMetadata().getReserve();
		}else{
			Thing root = thing.getRoot();
			String name = root.getMetadata().getReserve();
			String subName = thing.getMetadata().getName();
			subName = subName.substring(0, 1).toUpperCase() + subName.substring(1, subName.length());
			javaName = name + subName;
		}
		
		return javaName.replace('#', '_').replace('-', '_') + "Actions";
	}
	
	public static String getCopyright(){
		return  "/*\n" +
				" * Copyright 2007-2016 The xworker.org.\n" +
				" *\n" +					
				" * Licensed to the X-Meta under one or more\n" +
				" * contributor license agreements.  See the NOTICE file distributed with\n" +
				" * this work for additional information regarding copyright ownership.\n" +
				" * The X-Meta licenses this file to You under the Apache License, Version 2.0\n" +
				" * (the \"License\"); you may not use this file except in compliance with\n" +
						" * the License.  You may obtain a copy of the License at\n" +
						" *\n" +
						" *      http://www.apache.org/licenses/LICENSE-2.0\n" +
						" *\n" +
						" * Unless required by applicable law or agreed to in writing, software\n" +
						" * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
								" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
								" * See the License for the specific language governing permissions and\n" +
								" * limitations under the License.\n" +
								" */\n";
	}
	
	public static void createJavaCode(Thing thing, String rootPath) throws IOException{
		List<GroovyCode> codes = findGroovyAction(thing);
		if(codes.size() > 0){
			String code = getCopyright();
			String javaName = getJavaName(thing);
			code = code + "package " + thing.getMetadata().getCategory().getName() + ";\n\n";
			code = code + "public class " + javaName + " {\n";
			
			for(GroovyCode gcode : codes){
				code = code + "    public static Object " + gcode.methodName + "(ActionContext actionContext){\n";
				//code = code + "        /*\n";
				if(gcode.getCode() == null){
					code = code + "        return null;";
				}else{
					code = code + "        Thing self = actionContext.getObject(\"self\");\n";
					code = code + "" + codeIdent(gcode.getCode());
				}
				code = code + "    }\n\n";
			}
			code = code + "}";
			File javaFile = new File(rootPath + "/" + thing.getMetadata().getCategory().getName().replace('.', '/') + "/" + javaName + ".java");
			javaFile.getParentFile().mkdirs();
						
			FileOutputStream fout = new FileOutputStream(javaFile);
			fout.write(code.getBytes("utf-8"));
			fout.close();
		}
	}	
	
	public static void moveSourceToTarget(Thing thing, String root, String dist) throws IOException{
		String javaName = GroovyToJava.getJavaName(thing);
		
		//移动文件
		File src = new File(root + "/" + thing.getMetadata().getCategory().getName().replace('.', '/') + "/" + javaName + ".java");
		File tag = new File(dist + "/" + thing.getMetadata().getCategory().getName().replace('.', '/') + "/" + javaName + ".java");
		
		FileInputStream fin = new FileInputStream(src);
		byte[] bytes = new byte[fin.available()];
		fin.read(bytes);
		fin.close();
		
		if(!tag.exists()){
			tag.getParentFile().mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(tag);
		fout.write(bytes);
		fout.close();
		
		src.delete();
		
	}
	public static boolean isGroovyOrInnerJava(Thing thing){
		String name = getJavaName(thing);
		if(name.indexOf("{") != -1){
			return false;
		}
		
		String thingName = thing.getThingName();
		if("GroovyAction".equals(thingName)){
			return true;
		}
		
		if("JavaAction".equals(thingName) && thing.getBoolean("useOuterJava") == false){
			return true;
		}
		
		return false;
	}
	
	public static String codeIdent(String code){
		if(code == null){
			return "";
		}
		String c = "";
		for(String l : code.split("[\n]")){
			if(l.startsWith("import ")){
				continue;
			}
			
			c = c + "        " + l + "\n";
		}
		return c;
	}
	
	public static class GroovyCode{
		Thing thing;
		String methodName;
		
		public GroovyCode(Thing thing, Map<String, Integer> context){
			this.thing = thing;
			
			initMethodName(context);
		}
		
		public String getCode(){
			return thing.getStringBlankAsNull("code");
		}

		public void initMethodName(Map<String, Integer> context){
			methodName = thing.getMetadata().getName();
			
			//方法名
			if("GroovyAction".equals(methodName)){
				methodName = thing.getParent().getMetadata().getName();
				if("actions".equals(methodName)){
					methodName = "run";
				}
			}
						
			//判断名字是否重复
			String key = methodName;
			Integer count = context.get(key);
			if(count == null){
				context.put(methodName, 0);
			}else{
				count++;
				methodName = methodName + count;
				context.put(key, count);
			}		
		}

		@Override
		public String toString() {
			return "GroovyCode [thing=" + thing.getMetadata().getPath() + ", methodName=" + methodName
					+ "]";
		}
	}
}