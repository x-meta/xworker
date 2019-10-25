/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.lang;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class ClassWrapperActionsActions {
	private static Logger logger = LoggerFactory.getLogger(ClassWrapperActionsActions.class);
	
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        boolean debug = false;
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String type = getClassWrapperDesc(self.getAllDescriptors()).getString("type");
        Object obj = null;
        if("class".equals(type)){
            if(debug){
                logger.info(ident + "create class, " + self.getString("name"));
            }
            obj = self.doAction("createClass", actionContext, "debug", debug, "ident", ident + "    ");
        }else{
            if(debug){
                logger.info(ident + "create interface, " + self.getString("name"));
            }
            obj = self.doAction("createInterface", actionContext, "debug", debug, "ident", ident + "    ");
        }
        self.doAction("addObjectToContext", actionContext, "obj", obj, "debug", debug, "ident", ident + "    ");
        return obj;
    }
    
    public static Thing  getClassWrapperDesc(List<Thing> descs){
        for(Thing desc : descs){
            if("ClassWrapper".equals(desc.getThingName())){
                return desc;
            }
        }
        
        return null;
    }

    public static Object createClass(ActionContext actionContext){
    	throw new ActionException("Have not converted groovy to java");
    	/*
        Thing self = actionContext.getObject("self");
        
        //初始化实例
        def obj = self.doAction("newInstance", actionContext);
        def cls = obj.class;
        if(obj == null){
            if(debug){
                log.info(ident + "new instance reutrn null, create class obj failed");
            }
            return null;
        }
        
        if(debug){
            log.info(ident + "new instance = " + obj);
            log.info(ident + "create attributes");
        }
        
        //设置基本属性
        def attributesDescriptors = self.getAllAttributesDescriptors();
        for(attributeDescriptor in attributesDescriptors){
            def desPath = attributeDescriptor.metadata.path;
            if(desPath.startsWith("_transient")){
                //属性的属性不设置，为什么？忘记了.......
                if(debug){
                    log.info(ident + attributeDescriptor.name + " is transient thing, do nothing");
                }
                continue;
            }
            
            if(attributeDescriptor.getBoolean("noAttribute")){    
                //不是属性
                if(debug){
                    log.info(ident + attributeDescriptor.name + " is not a attribute, do nothing");
                }
                continue;
            }
            
            def value = self.get(attributeDescriptor.name);
            if(value == null || value == "" || value == attributeDescriptor.get("default")){
                //空值和默认值不处理
                if(debug){
                    log.info(ident + attributeDescriptor.name + " value is null or default, do nothing, value=" + value);
                }
                continue;
            }
            value = commonMethods.getAttributeValue(attributeDescriptor, self, value, actionContext);
            
            //设置属性值
            if(attributeDescriptor.setMethod == null || attributeDescriptor.setMethod == ""){
                OgnlUtil.setValue(attributeDescriptor.name, obj, value);
            }else{
                commonMethods.setValue(cls, obj, attributeDescriptor, attributeDescriptor.vType, self, value);
            }
            if(debug){
                log.info(ident + attributeDescriptor.name + " is " + value);
            }
        }
        
        if(debug){
            log.info(ident + "create child things");
        }
        //设置其他属性
        for(child in self.getChilds()){
            if(child.thingName != "constructor" && child.thingName != "actions"){       
                child.doAction("create", actionContext, 
                     ["ident":ident + "    ",
                      "cls":cls,
                      "obj":obj,
                      "parent":self]);
            }
        }
        
        return obj;
        */
    }

    public static Object createInterface(ActionContext actionContext){
    	throw new ActionException("Have not converted groovy to java");
    	/*
        Thing self = actionContext.getObject("self");
        
        def descriptor = self.doAction("getDescriptor", actionContext);
        
        //接口名
        def cls = Class.forName(descriptor.className);
        
        //对象实例
        def obj = [:];
        for(childDesc in descriptor.get("method@")){
            obj.put(childDesc.name, createMethod(childDesc, self, actionContext));
        }
        return obj.asType(cls);
        
        //创建接口的实现方法
        def createMethod(methodDescriptor, objectThing, actionContext){
            return {
                Object[] args->
                def ac = new ActionContext(actionContext);
                def bindings = ac.push(null);
                try{
                    int i=0;
                    for(parameter in methodDescriptor.get("parameter@")){
                        if(i < args.length){
                            bindings.put(parameter.metadata.name, args[i]);
                            i++;
                        }else{
                            break;
                        }                
                    }
                    
                    return objectThing.doAction(methodDescriptor.metadata.name, ac);
                }finally{
                    ac.pop();
                }
            }
        }*/
    }

    public static Object newInstance(ActionContext actionContext){
    	throw new ActionException("Have not converted groovy to java");
    	/*
        Thing self = actionContext.getObject("self");
        //类
        if(debug){
            log.info(ident + "ClassName=" + getClassName(self.descriptors[0]));
        }
        def cls = Class.forName(getClassName(self.descriptors[0]));
        
        def obj = null;
        def constructor = null;
        def constructorThing = self.get("constructor@0");
        if(constructorThing != null){
            def paramDefs = constructorThing.getAttributesDescriptors();
            def paramClss = new Class[paramDefs.size()];
            def paramValues = new Object[paramDefs.size()];
            int i = 0;
            for(paramDef in paramDefs){
                paramClss[i] = getClassByName(paramDef.className);
                i++;
            }    
            constructor = cls.getConstructor(paramClss);
            if(constructor != null){
                i=0;
                for(param in paramDefs){
                    paramValues[i] = getAttributeValue(param, constructorThing, actionContext);
                }
            }
            obj = constructor.newInstance(paramValues);     
        }else{
            obj = cls.newInstance();
        }
        
        return obj;
        
        def getClassName(desc){
            def value = desc.get("className");
            if(value == null){
                for(extend_ in desc.getAllExtends()){
                    value = extend_.get("className");
                    if(value != null){
                        return value;
                    }
                }
            }else{
                return value;
            }
        }
        
        //设置一个属性的值
        def setValue(cls, obj, attributeDescriptor, type, thing, value){
            def setMethod = attributeDescriptor.setMethod;
                
            if(setMethod != null){   
                //指定了设置值的方法 
                def classArray = new Class[1];
                classArray = getClassByName(type);
                def valueArray = new Object[1];
                valueArray = value;
            
                //使用setMethod中定义的方法    
                if(invokeMethod(attributeDescriptor, cls, obj, setMethod, classArray, valueArray)){
                    return;
                }
            }else{
                //其他按属性设置
                OgnlUtil.setValue(attributeDescriptor.name, obj, value);
            }           
        }
        
        //调用一个方法
        def invokeMethod(thing, cls, obj, methodName, paramClass, paramValue){
            try{
                def method = cls.getMethod(methodName, paramClass);
                if(method != null){
                    method.invoke(obj, paramValue);
                    return true;
                }else{
                    return false;
                }
            }catch(Exception e){
                log.info("invoke method error, method=" + methodName + ",path=" + thing.metadata.path, e);
                return false;
            }
        }
        
        //获取属性的值
        def getAttributeValue(descThing, thing, value, actionContext){
            def attributeName = descThing.name;
            if(value == null){
                return null;
            }else if(value instanceof String && value.startsWith("var:")){
                //变量引用
                return actionContext.get(value.substring(4, value.length()));
            }else if(value instanceof String && value == "=null"){
                return null;
            }else if(value instanceof String && value.startsWith("label:")){
                //i18n标签
                def thingPath = value.substring(6, value.length());
                def labelThing = world.getThing(thingPath);
                if(labelThing != null){
                    return labelThing.metadata.label;
                }else{
                    return thingPath;
                }
            }else{
                switch(descThing.vType){
                    case "string":
                        return thing.getString(attributeName);
                    case "bigDecimal":
                        return thing.getBigDecimal(attributeName);
                    case "bigInteger":
                        return thing.getBigInteger(attributeName);
                    case "boolean":
                        return thing.getBoolean(attributeName);
                    case "byte":
                        return thing.getByte(attributeName);
                    case "bytes":
                        return thing.getBytes(attributeName);
                    case "char":
                        return thing.getChar(attributeName);
                    case "date":
                        return thing.getDate(attributeName);
                    case "double":
                        return thing.getDouble(attributeName);
                    case "float":
                        return thing.getFloat(attributeName);
                    case "int":
                        return thing.getInt(attributeName);
                    case "long":
                        return thing.getLong(attributeName);
                    case "short":
                        return thing.getShort(attributeName);
                    default:
                        return value;
                }
            }
        }
        */
    }

    public static Object getDescriptor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        List<Thing> descriptors = self.getAllDescriptors();
        for(Thing desc : descriptors){
            if(desc.getMetadata().getPath().indexOf(":/") == -1){
                return desc;
            }
        }
        
        return null;
    }

    public static Object createClassWrapperFromDoc(ActionContext actionContext){
    	throw new ActionException("Have not converted groovy to java");
    	/*
        Thing self = actionContext.getObject("self");
        
        //将要创建的类包装器
        Thing thing = new Thing("xworker.lang.ClassWrapper");
        
        //类名和类型
        def index = content.indexOf("START OF CLASS DATA");
        index = content.indexOf("<FONT SIZE=\"-1\">", index) + 17;
        def index1 = content.indexOf("</FONT>", index);
        def packageName = content.substring(index, index1).trim();
        index = content.indexOf("<BR>", index) + 4;
        index1 = content.indexOf("</H2>", index);
        def str = content.substring(index, index1).trim();
        def strs = str.split("[ ]");
        def classType = strs[0].trim();
        def className = strs[1].trim();
        thing.type = docConfig.doAction("getClassType", actionContext, ["typeName":classType]);
        thing.name = className;
        thing.className = packageName + "." + className;
        
        //初始化继承和实现类
        def extendstr = "";
        index = content.indexOf("<PRE>", index1);
        def limitIndex = content.indexOf("</PRE>", index);
        while(index < limitIndex){
            index1 = content.indexOf("<A", index);
            if(index1 > limitIndex || index1 == -1){
                break;
            }
            
            index = content.indexOf("\">", index1) + 2;
            index1 = content.indexOf("</A>", index);
            def extendClass = content.substring(index, index1);
            if(extendClass.startsWith(packageLimit)){
                extendClass = extendClass.replaceAll("(" + packageLimit + ")", packagePath);
                if(extendstr != ""){
                    extendstr = extendClass + "," + extendstr;
                }else{
                    extendstr = extendClass;
                }
            }
        }
        index1 = content.indexOf("All Implemented Interfaces", index);
        if(index1 != -1){
            limitIndex = content.indexOf("</DL>", index1);
            while(index1 < limitIndex){
                index1 = content.indexOf("<A HREF", index1);
                if(index1 >= limitIndex || index1 == -1){
                    break;
                }   
                index = content.indexOf("interface in ", index1) + 13;
                index1 = content.indexOf("\">", index);
                def pk = content.substring(index, index1);
                index = index1 + 2;
                index1 = content.indexOf("</A>", index);
                pk = pk + "." + content.substring(index, index1);
                if(pk.startsWith(packageLimit)){
                    pk = pk.replaceAll("(" + packageLimit + ")", packagePath);
                    if(extendstr != ""){
                        extendstr = extendstr + "," + pk;
                    }else{
                        extendstr = pk;
                    }
                }
            }
        }
        thing.put("extends", extendstr);
        
        //类描述
        def classDesc = "";
        index = content.indexOf("<P>", index) + 3;
        index1 = content.indexOf("<HR>", index) - 9;
        if(index > 0 && index1 > 0 && index1 > index){
            classDesc = content.substring(index, index1);
            thing.set("description", classDesc);
        }
        
        //构造函数
        index1 = content.indexOf("CONSTRUCTOR SUMMARY");
        indexLimit = content.indexOf("</TABLE>", index1);
        index1 = content.indexOf("<B>Constructor Summary</B>", index1);
        while(true){
            index1 = content.indexOf("TD><CODE><B>", index1);
            if(index1 >= indexLimit || index1 == -1){
                break;
            }
            
            index = content.indexOf("</B>(", index1) + 5;
            index1 = content.indexOf(")", index);
            def paramStr = content.substring(index, index1);
            if(paramStr == ""){
                continue;
            }
            
            index = content.indexOf("<BR>", index1) + 66;
            index1 = content.indexOf("</TD>", index);
            def desc = content.substring(index, index1);
        
            def construct = new Thing("xworker.lang.ClassWrapper/@constructor");
            construct.set("description", desc);
            construct.set("name", "constructor");
            construct.set("label", "构造器");
            construct.set("label_en", "Constructor");
            def params = paramStr.split("[,]");
            for(param in params){
                def p = parseParam(param);
                def pm = new Thing("xworker.lang.ClassWrapper/@constructor/@parmeter");
                pm.set("name", p.name);
                pm.set("className", p.type);
                def ptype = getType(p.type);
                if(!ptype.isObj){
                    pm.set("type", ptype.type);
                }
                construct.addChild(pm);
            }
            thing.addChild(construct);
        }
        
        //字段
        index1 = content.indexOf("FIELD SUMMARY");
        index1 = content.indexOf("</B></FONT></TH>", index1);
        indexLimit = content.indexOf("TABLE", index1);
        while(index < indexLimit){
            index1 = content.indexOf("<TR BGCOLOR", index1);
            if(index1 >= indexLimit || index1 == -1){
                break;
            }
            if(index1 < indexLimit){
                index = content.indexOf("<CODE>", index1) + 6;
                index1 = content.indexOf("</CODE>", index);
                def field = content.substring(index, index1).trim();
               
                if(field.indexOf("&nbsp;") == 0){
                    //是pulbic的字段
                    field = field.substring(6, field.length());
                    def type = getType(field);
                    index = content.indexOf("\">", index1) + 2;
                    index1 = content.indexOf("</A", index);
                    def fieldName = content.substring(index, index1);
                    index = content.indexOf("<BR>", index1) + 66;
                    index1 = content.indexOf("</TD>", index);
                    def methodDesc = content.substring(index, index1);
                    def attribute = null;
                    if(type.isObj){
                        attribute = new Thing("xworker.lang.ClassWrapper/@childThing");
                        attribute.set("name", fieldName);
                        attribute.set("className", type.type);
                        attribute.set("many", "false");
                        atrribute.set("description", fieldDesc);
                    }else{
                        attribute = new Thing("xworker.lang.ClassWrapper/@attribute");
                        attribute.set("name", fieldName);
                        attribute.set("type", type.type);
                        attribute.set("description", fieldDesc);
                    }
                    thing.addChild(attribute);
                }
            }
        }
        
        //方法
        index1 = content.indexOf("METHOD SUMMARY");
        index1 = content.indexOf("</B></FONT></TH>", index1);
        indexLimit = content.indexOf("TABLE", index1);
        while(index < indexLimit){
            index1 = content.indexOf("<TR BGCOLOR", index1);
            if(index1 >= indexLimit){
                break;
            }
            if(index1 < indexLimit){
                index = content.indexOf("<CODE>", index1) + 6;
                index1 = content.indexOf("</CODE>", index);
                def returnType = content.substring(index, index1).trim();
               
                if(returnType.indexOf("&nbsp;") == 0){
                    //是pulbic的方法
                    returnType = returnType.substring(6, returnType.length());
                    def type = getType(returnType);
                    index = content.indexOf("\">", index1) + 2;
                    index1 = content.indexOf("</A", index);
                    def methodName = content.substring(index, index1);
                    index = content.indexOf("</B>(", index1) + 5;
                    index1 = content.indexOf(")", index);
                    def paramStr = content.substring(index, index1);
                    index = content.indexOf("<BR>", index1) + 66;
                    index1 = content.indexOf("</TD>", index);
                    def methodDesc = content.substring(index, index1);
                    if((methodName.startsWith("add") || methodName.startsWith("set")) &&
                        paramStr.indexOf(",") == -1 && paramStr != ""){
                        //如果方法的参数只有一个，并且是add或set，当作属性
                        def attributeName = methodName.substring(3, methodName.length()).toLowerCase();
                        def param = parseParam(paramStr); 
                        type = getType(param.type);
                        def attribute = null;
                        if(type.isObj){
                            attribute = new Thing("xworker.lang.ClassWrapper/@childThing");
                            attribute.set("name", attributeName);
                            attribute.set("className", type.type);
                            if(methodName.startsWith("add")){
                                attribute.set("many", "true");
                            }else{
                                attribute.set("many", "false");
                            }
                            attribute.set("description", methodDesc);
                        }else{
                            attribute = new Thing("xworker.lang.ClassWrapper/@attribute");
                            attribute.set("name", attributeName);
                            attribute.set("type", type.type);
                            attribute.set("description", methodDesc);
                        }
                        attribute.set("setMethod", methodName);
                        if(thing.get("attribute@" + attributeName) == null || thing.get("childThing@" + attributeName) == null){
                            thing.addChild(attribute);
                        }
                    }
                    
                    if(thing.type == "interface"){
                        //如果类类型是接口，那么添加方法
                        def method = new Thing("xworker.lang.ClassWrapper/@thing");
                        method.set("name", methodName);
                        method.set("description", methodDesc);
                        if(paramStr != ""){
                        def params = paramStr.split("[,]");
                            for(param in params){                    
                                def p = parseParam(param);
                                def pm = new Thing("xworker.lang.ClassWrapper/@thing/@parameter");
                                pm.set("name", p.name);
                                pm.set("className", p.type);
                                def ptype = getType(p.type);
                                if(!ptype.isObj){
                                    pm.set("type", ptype.type);
                                }
                                method.addChild(pm);
                            }
                        }
                        thing.addChild(method);
               		}
                }
            }
        }
        return thing;
        
        */
    }
    
  //分析参数
    public static Map<String, Object> parseParam(String param){
        String[] ps = param.split("&nbsp;");
        Map<String, Object> p = new HashMap<String, Object>();
        if(ps[0].indexOf("title=") != -1){
            int i1 = ps[0].indexOf("in ", ps[0].indexOf("title=")) + 3;
            int i2 = ps[0].indexOf("\">", i1);
            String pk = ps[0].substring(i1, i2);
            i1 = i2 + 2;
            i2 = ps[0].indexOf("</A>", i1);
            p.put("type", pk + "." + ps[0].substring(i1, i2));
        }else{
            p.put("type", ps[0].trim());
        }
        p.put("name", ps[1]);
        return p;	
    }
    
    public static Map<String, Object> getType(String typeName){
        Map<String, Object> type = new HashMap<String, Object>();
        type.put("isObj", false);
        if("java.lang.String".equals(typeName)){
            type.put("type", "string");
        }else if("java.math.BigDecimal".equals(typeName)){
        	type.put("type", "bigDecimal");
        }else if("java.math.BigInteger".equals(typeName)){
            type.put("type", "bigInteger");
        }else if("byte".equals(typeName) || "java.lang.Byte".equals(typeName)){
            type.put("type", "byte");
        }else if("int".equals(typeName) || "java.lang.Integer".equals(typeName)){
            type.put("type", "int");
        }else if("char".equals(typeName) || "java.lang.Char".equals(typeName)){
            type.put("type", "char");
        }else if("short".equals(typeName) || "java.lang.Short".equals(typeName)){
            type.put("type", "short");
        }else if("long".equals(typeName) || "java.lang.Long".equals(typeName)){
            type.put("type", "long");
        }else if("float".equals(typeName) || "java.lang.Float".equals(typeName)){
            type.put("type", "float");
        }else if("double".equals(typeName) || "java.lang.Double".equals(typeName)){
            type.put("type", "double");
        }else if("byte[]".equals(typeName)){
            type.put("type", "bytes");
        }else if("boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)){
            type.put("type", "boolean");
        }else {
            type.put("type", typeName);
            type.put("isObj", true);       
        }
        
        return type;
    }

    public static Object toGroovyCreateCode(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        def code = "";
        //类
        def cls = Class.forName(self.descriptors[0].className);
        code = code + "import " + cls + ";\n\n";
        
        //构造器
        code = code + "def obj = null; //实例\n\n";
        cdoe = code + "\\初始化实例\n";
        code = code + "def constructor = null;\n";
        code = code + "def constructorThing = self.get(\"constructor@0\");\n";
        code = code + "if(constructorThing != null){\n";
        code = code + "    obj = constructorThing.doAction(\"create\", actionContext);\n";
        code = code + "}else{\n";
        code = code + "    obj = new " + self.descriptors[0].name + ";\n\n";
        
        //基本属性
        code = code + "\\基本属性\n";
        def attributesDescriptors = self.getAllAttributesDescriptors();
        for(attributeDescriptor in attributesDescriptors){
            code = code + "def " + attributeDescriptor.name + " = self.getString(";
            def value = self.get(attributeDescriptor.name);
            if(value == null){
                //空值不处理
                continue;
            }
            value = getAttributeValue(attributeDescriptor, self, actionContext);
            
            //设置属性值
            OgnlUtil.setValue(attributeDescriptor.name, obj, value);
        }
        
        //设置其他属性
        for(child in self.getChilds()){
            if(child.thingName != "constructor"){
                def values = child.doAction("create", actionContext);
                
                for(value in values){
                    //设置属性值
                    setValue(cls, obj, child.getDescriptos().get(0), self, value);
                }
            }
        }
        
        def setValue(cls, obj, attributeDescriptor, thing, value){
            def setMethod = attributeDescriptor.setMethod;
            def classArray = new Class[1];
            classArray = Class.forName(attributeDescriptor.className);
            def valueArray = new Object[1];
            valueArray = value;
            
            if(setMethod != null){    
                //使用setMethod中定义的方法    
                if(invokeMethod(cls, obj, setMethod, classArray, valueArray)){
                    return;
                }
                
                //使用setxxx方法
                def name = attributeDescriptor.name;
                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                setMethod = "set" + name;
                if(invokeMethod(cls, obj, setMethod, classArray, valueArray)){
                    return;
                }
                
                //使用put方法
                def classses = new Class[2];
                classes[0] = String.class;
                classes[1] = Object.class;
                def objects = new Object[2];
                objects[0] = "put";
                objects[1] = value;
                if(invokeMethod(cls, obj, setMethod, lassses,objects)){
                    return;
                }
               
                //使用属性设置
                def field = cls.getDeclaredField(attributeDescriptor.name);
                if(field != null){
                    field.set(obj, value);
                }
            }
        }
        
        //调用一个方法
        def invokeMethod(cls, obj, methodName, paramClass, paramValue){
            def method = cls.getDeclaredMethod(methodName, paramClass);
            if(method != null){
                method.invoke(obj, paramValue);
                return true;
            }else{
                return false;
            }
        }
        
        //获取属性的值
        def getAttributeValue(descThing, thing, actionContext){
            def attributeName = descThing.name;
            def value = thing.get(attributeName);
        
            if(value == null){
                return null;
            }else if(value instanceof String && value.startsWith("var:")){
                //变量引用
                return actionContext.get(value.substring(4, value.length()));
            }else if(value instanceof String && value == "=null"){
                return null;
            }else{
                switch(descThing.type){
                    case "string":
                        return "self.getString(attributeName)";
                    case "bigDecimal":
                        return "self.getBigDecimal(attributeName)";
                    case "bigInteger":
                        return "self.getBigInteger(attributeName)";
                    case "boolean":
                        return "self.getBoolean(attributeName)";
                    case "byte":
                        return "self.getByte(attributeName)";
                    case "bytes":
                        return "self.getBytes(attributeName)";
                    case "char":
                        return "self.getChar(attributeName)";
                    case "date":
                        return "self.getDate(attributeName)";
                    case "double":
                        return "self.getDouble(attributeName)";
                    case "float":
                        return "self.getFloat(attributeName)";
                    case "int":
                        return "self.getInt(attributeName)";
                    case "long":
                        return "self.getLong(attributeName)";
                    case "short":
                        return "self.getShort(attributeName)";
                    default:
                        return value;
                }
            }
        }
        
        def getAttributeClass(attributeDesc){
           switch(attributeDesc.type){
                case "string":
                    return "java.lang.String".class;
                case "bigDecimal":
                    return Class.forName("java.math.BigDecimal");
                case "bigInteger":
                    return Class.forName("java.math.BigInteger");
                case "boolean":
                    return Class.forName("java.lang.Boolean");
                case "byte":
                    return Class.forName("java.lang.Byte");
                case "bytes":
                    return new byte[0].class;
                case "char":
                    return Class.forName("java.lang.Char");
                case "date":
                    return Class.forName("java.util.Date");
                case "double":
                    return Class.forName("java.lang.Double");
                case "float":
                    return Class.forName("java.lang.Float");
                case "int":
                    return Class.forName("java.lang.Integer");
                case "long":
                    return Class.forName("java.lang.Long");
                case "short":
                    return Class.forName("java.lang.Short");
                default:
                    return "java.lang.String".class;
            }
        }*/
    	return null;
    }

    
    //设置一个属性的值
    public static void setValue(Class<?> cls, Object obj, Thing attributeDescriptor, String type, Thing thing, Object value) throws ClassNotFoundException, OgnlException{
        String setMethod = attributeDescriptor.getString("setMethod");
            
        if(setMethod != null){   
            //指定了设置值的方法 
            Class[] classArray = new Class[1];
            //println("get attribute type,name=" + attributeDescriptor.name + ",type=" + type);
            
            classArray[0] = getClassByName(type);
            Object[] valueArray = new Object[1];
            valueArray[0] = value;
        
            //使用setMethod中定义的方法    
            invokeMethod(attributeDescriptor, cls, obj, setMethod, classArray, valueArray);            
        }else{
            //其他按属性设置
        	OgnlUtil.setValue(attributeDescriptor.getString("name"), obj, value);			
        }           
    }
    
    //调用一个方法
    public static Object invokeMethod(Thing thing, Class<?> cls, Object obj, String methodName, Class[] paramClass, Object[] paramValue){
        try{
            Method method = cls.getMethod(methodName, paramClass);
            if(method != null){
                method.invoke(obj, paramValue);
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
           throw new ActionException("invoke method error", e);
        }
    }
    
    //获取属性的值
    public static Object getAttributeValue(Thing descThing, Thing thing, Object value, ActionContext actionContext){
        String attributeName = descThing.getString("name");
        if(value == null){
            return null;
        }else if(value instanceof String && ((String) value).startsWith("var:")){
            //变量引用
        	String v = (String) value;
            return actionContext.get(v.substring(4, v.length()));
        }else if(value instanceof String && "=null".equals(value)){
            return null;
        }else if(value instanceof String && ((String) value).startsWith("label:")){
            //i18n标签
        	String v = (String) value;
            String thingPath = v.substring(6, v.length());
            Thing labelThing = World.getInstance().getThing(thingPath);
            if(labelThing != null){
                return labelThing.getMetadata().getLabel();
            }else{
                return thingPath;
            }
        }else{
        	String type = descThing.getString("vType");
            if("string".equals(type)){
                return thing.getString(attributeName);
            }else if("bigDecimal".equals(type)){
                return thing.getBigDecimal(attributeName);
            }else if("bigInteger".equals(type)){
                return thing.getBigInteger(attributeName);
            }else if("boolean".equals(type)){
                return thing.getBoolean(attributeName);
            }else if("byte".equals(type)){
                return thing.getByte(attributeName);
            }else if("bytes".equals(type)){
                return thing.getBytes(attributeName);
            }else if("char".equals(type)){
                return thing.getChar(attributeName);
            }else if("date".equals(type)){
                return thing.getDate(attributeName);
            }else if("double".equals(type)){
                return thing.getDouble(attributeName);
            }else if("float".equals(type)){
                return thing.getFloat(attributeName);
            }else if("int".equals(type)){
                return thing.getInt(attributeName);
            }else if("long".equals(type)){
                return thing.getLong(attributeName);
            }else if("short".equals(type)){
                return thing.getShort(attributeName);
            }else{
                return value;
            }
        }
    }
    
    public static Class<?> getClassByName(String className) throws ClassNotFoundException{
        if("int.class".equals(className) || "int".equals(className)){
            return int.class;
        }else if("byte.class".equals(className) ||  "byte".equals(className)){
            return byte.class;
        }else if("char.class".equals(className) || "char".equals(className)){
            return char.class;
        }else if("short.class".equals(className) || "short".equals(className)){
            return short.class;
        }else if("long.class".equals(className) || "long".equals(className)){
            return long.class;
        }else if("float.class".equals(className) || "float".equals(className)){
            return float.class;
        }else if("double.class".equals(className) || "double".equals(className)){
            return double.class;
        }else if("boolean.class".equals(className) || "boolean".equals(className)){
            return boolean.class;
        }else if("string".equals(className)){
            return String.class;
        }else{
            return Class.forName(className);
            
        }
    }

}