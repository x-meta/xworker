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
package xworker.dataObject.utils;

import org.xmeta.ActionContext;

import ognl.OgnlException;

public class JsonDataFormatCreator {
	//private static Logger log = LoggerFactory.getLogger(JsonDataFormatCreator.class);
	
    //为给定一个字符串的每一行前加入缩进
    public static String addIdent(String str, String ident){
        if(str == null){
            return null;
        }
        
        String s = "";
        for(String line : str.split("[\n]")){
            if(!"".equals(s)){
                s = s + "\n";
            }
            s = ident + line;
        }
        return s;
    }
    
	public static Object format(ActionContext actionContext) throws OgnlException{
    	Object data = actionContext.get("data");
        return JsonFormator.format(data);
    }
    

  
 
    public static Object parse(ActionContext actionContext) throws Exception{
        String jsonText = (String) actionContext.get("jsonText");
        
        return JsonFormator.parse(jsonText, actionContext);
    }

    public static Object parseDataObject(ActionContext actionContext){
        return null;
    }

    public static Object getData(ActionContext actionContext) throws OgnlException{
    	return format(actionContext);
    	/*
        if(actionContext.get("data") == null){
            return null;
        }else if(data instanceof Thing){
            return processThing(data, "");
        }else if(data instanceof DataObject){
            return processDataObject(data, "");
        }else if(data instanceof List || data instanceof Thing[] || data instanceof DataObject[]){
            def str = "[";
            for(childThing in data){
                if(str != "["){
                    str = str + ",";
                }
        
                if(childThing instanceof DataObject){
                    str = str + "\n" + processDataObject(childThing, "    ");
                }else if(childThing != null){
                    str = str + "\n" + processThing(childThing, "    ");
                }
            }
            str = str + "\n" + "]";
            return str;
        }else{
            return null;
        }
        
        //转化一个数据对象到json数据格式的字符串
        def processDataObject(dataObject, ident){
            def str = ident + "{";
            def descriptor = dataObject.getMetadata().getDescriptor();
            def context = [:];
            //属性
            for(attribute in descriptor.getAllChilds("attribute")){
                if(context.get(attribute.name) != null){
                    //取所有的子事物可能有重名的，比如多继承时
                    continue;
                }
                context.put(attribute.name, attribute);
                def value = getAttributeValue(dataObject, attribute);
                if(value != null){
                    if(str != (ident + "{")){
                        str = str + ",\n    " + ident + attribute.name + ":" + value;
                    }else{
                        str = str + "\n    " + ident + attribute.name + ":" + value;
                    }
                }
            }
            
            def __dataId = dataObject.get("__dataId");
            if(__dataId != null && __dataId != ""){
                if(str != (ident + "{")){
                    str = str + ",\n    " + ident + "__dataId: '" + __dataId + "'";
                }else{
                    str = str + "\n    " + ident + "__dataId: '" + __dataId + "'";
                }
            }
            //子事物
            for(child in descriptor.getAllChilds("thing")){
                if(context.get(child.name) != null){
                    continue;
                }
                context.put(child.name, child);
                
                if(child.getBoolean("many")){
                    def childs = dataObject.get(child.name);
                    if(childs == null || (!childs instanceof List && !childs instanceof Thing[] && !childs instanceof DataObject[])){
                        continue;
                    }
                    
                    if(childs.size() > 0){
                        def childStr = "[";
                        for(childThing in childs){
                            if(childStr != "["){
                                childStr = childStr + ",";
                            }
                            if(childThing instanceof DataObject){
                                childStr = childStr + "\n" + processDataObject(childThing, ident + "    ");
                            }else{
                                childStr = childStr + "\n" + processThing(childThing, ident + "    ");
                            }
                        }
                        childStr = childStr + "\n    " + ident + "]";
                        if(str != (ident + "{")){
                            str = str + ",";
                        }
                        str = str + "\n    " + ident + child.name + ":" + childStr;
                    }
                }else{
                    def childThing = dataObject.get(child.name);
                    if(childThing != null){
                        if(str != (ident + "{")){
                            str = str + ",";
                        }
                        if(childThing instanceof DataObject){
                            str = str + "\n    " + child.name + ":" + processDataObject(childThing, "    " + ident);
                        }else{
                            str = str + "\n    " + child.name + ":" + processThing(childThing, "    " + ident);
                        }
                    }
                }
            }
            return str + "\n" + ident + "}";
        }
        
        //转化一个事物到json数据格式的字符串
        def processThing(thing, ident){
            def str = ident + "{";
            def descriptor = thing.getDescriptor();
            def context = [:];
            //属性
            for(attribute in descriptor.getAllChilds("attribute")){
                if(context.get(attribute.name) != null){
                    //取所有的子事物可能有重名的，比如多继承时
                    continue;
                }
                context.put(attribute.name, attribute);
                def value = getAttributeValue(thing, attribute);
                if(value != null){
                    if(str != (ident + "{")){
                        str = str + ",\n    " + ident + attribute.name + ":" + value;
                    }else{
                        str = str + "\n    " + ident + attribute.name + ":" + value;
                    }
                }
            }
            
            //子事物
            for(child in descriptor.getAllChilds("thing")){
                if(context.get(child.name) != null){
                    continue;
                }
                context.put(child.name, child);
                
                if(child.getBoolean("many")){
                    def childs = thing.get(child.name + "@");
                    if(childs.size() > 0){
                        def childStr = "[";
                        for(childThing in childs){
                            if(childStr != "["){
                                childStr = childStr + ",";
                            }
                            childStr = childStr + "\n" + processThing(childThing, ident + "    ");
                        }
                        childStr = childStr + "\n    " + ident + "]";
                        if(str != (ident + "{")){
                            str = str + ",";
                        }
                        str = str + "\n    " + ident + child.name + ":" + childStr;
                    }
                }else{
                    def childThing = thing.get(child.name + "@0");
                    if(childThing != null){
                        if(str != (ident + "{")){
                            str = str + ",";
                        }
                        str = "\n    " + child.name + ":" + processThing(childThing, "    " + ident);
                    }
                }
            }
            return str + "\n" + ident + "}";
        }
        
        //为给定一个字符串的每一行前加入缩进
        def addIdent(str, ident){
            if(str == null){
                return null;
            }
            
            def s = "";
            for(line in str.split("[\n]")){
                if(s != ""){
                    s = s + "\n";
                }
                s = ident + line;
            }
            return s;
        }
        
        //获取jason中要表示的属性值
        def getAttributeValue(data, descriptor){
            def value = data.get(descriptor.name);
            if(value == null || "" == value){
                //值为空
                return null;
            }
            
            if(descriptor.get("default") == "" + value){
                //等于默认值
                return null;
            }
            
            switch(descriptor.type){
                case "byte":
                case "short":
                case "int":
                case "long":
                case "float":
                case "double":
                case "boolean":
                    return "" + value;
                case "date":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("yyyy-MM-dd");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                case "datetime":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                case "time":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("HH:mm:ss");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                default:
                    return "'" + value + "'";
                
            }
        }*/
    }

    public static Object getArrayData(ActionContext actionContext) throws OgnlException{
    	return format(actionContext);
    	/*
        import java.text.SimpleDateFormat;
        
        import org.xmeta.Thing;
        import xworker.app.DataObject;
        
        if(actionContext.get("data") == null){
            return null;
        }else if(data instanceof Thing){
            return processThing(data, "");
        }else if(data instanceof DataObject){
            return processDataObject(data, "");
        }else if(data instanceof List || data instanceof Thing[] || data instanceof DataObject[]){
            def str = "[";
            for(childThing in data){
                if(str != "["){
                    str = str + ",";
                }
                if(childThing instanceof DataObject){
                    str = str + "\n" + processDataObject(childThing, "    ");
                }else{
                    str = str + "\n" + processThing(childThing, "    ");
                }
            }
            str = str + "\n" + "]";
            return str;
        }else{
            return null;
        }
        
        //转化一个数据对象到json数据格式的字符串
        def processDataObject(dataObject, ident){
            def str = ident + "[";
            def descriptor = dataObject.getMetadata().getDescriptor();
            def context = [:];
            //属性
            for(attribute in descriptor.getAllChilds("attribute")){
                if(context.get(attribute.name) != null){
                    //取所有的子事物可能有重名的，比如多继承时
                    continue;
                }
                context.put(attribute.name, attribute);
                def value = getAttributeValue(dataObject, attribute);
                if(value != null){
                    if(str != (ident + "[")){
                        str = str + ",\n    " + ident + value;
                    }else{
                        str = str + "\n    " + ident + value;
                    }
                }
            }
            
            //子事物
            for(child in descriptor.getAllChilds("thing")){
                if(context.get(child.name) != null){
                    continue;
                }
                context.put(child.name, child);
                
                if(child.getBoolean("many")){
                    def childs = dataObject.get(child.name);
                    if(childs == null || (!childs instanceof List && !childs instanceof Thing[] && !childs instanceof DataObject[])){
                        continue;
                    }
                    
                    if(childs.size() > 0){
                        def childStr = "[";
                        for(childThing in childs){
                            if(childStr != "["){
                                childStr = childStr + ",";
                            }
                            if(childThing instanceof DataObject){
                                childStr = childStr + "\n" + processDataObject(childThing, ident + "    ");
                            }else{
                                childStr = childStr + "\n" + processThing(childThing, ident + "    ");
                            }
                        }
                        childStr = childStr + "\n    " + ident + "]";
                        if(str != (ident + "[")){
                            str = str + ",";
                        }
                        str = str + "\n    " + ident + child.name + ":" + childStr;
                    }
                }else{
                    def childThing = thing.get(child.name);
                    if(childThing != null){
                        if(str != (ident + "[")){
                            str = str + ",";
                        }
                        if(childThing instanceof DataObject){
                            str = "\n    " + processDataObject(childThing, "    " + ident);
                        }else{
                            str = "\n    " + processThing(childThing, "    " + ident);
                        }
                    }
                }
            }
            return str + "\n" + ident + "]";
        }
        
        //转化一个事物到json数据格式的字符串
        def processThing(thing, ident){
            def str = ident + "[";
            def descriptor = thing.getDescriptor();
            def context = [:];
            //属性
            for(attribute in descriptor.getAllChilds("attribute")){
                if(context.get(attribute.name) != null){
                    //取所有的子事物可能有重名的，比如多继承时
                    continue;
                }
                context.put(attribute.name, attribute);
                def value = getAttributeValue(thing, attribute);
                if(value != null){
                    if(str != (ident + "[")){
                        str = str + ",\n    " + ident + value;
                    }else{
                        str = str + "\n    " + ident + value;
                    }
                }
            }
            
            //子事物
            for(child in descriptor.getAllChilds("thing")){
                if(context.get(child.name) != null){
                    continue;
                }
                context.put(child.name, child);
                
                if(child.getBoolean("many")){
                    def childs = thing.get(child.name + "@");
                    if(childs.size() > 0){
                        def childStr = "[";
                        for(childThing in childs){
                            if(childStr != "["){
                                childStr = childStr + ",";
                            }
                            childStr = childStr + "\n" + processThing(childThing, ident + "    ");
                        }
                        childStr = childStr + "\n    " + ident + "]";
                        if(str != (ident + "[")){
                            str = str + ",";
                        }
                        str = str + "\n    " + ident + childStr;
                    }
                }else{
                    def childThing = thing.get(child.name + "@0");
                    if(childThing != null){
                        if(str != (ident + "[")){
                            str = str + ",";
                        }
                        str = "\n    " + processThing(childThing, "    " + ident);
                    }
                }
            }
            return str + "\n" + ident + "]";
        }
        
        //为给定一个字符串的每一行前加入缩进
        def addIdent(str, ident){
            if(str == null){
                return null;
            }
            
            def s = "";
            for(line in str.split("[\n]")){
                if(s != ""){
                    s = s + "\n";
                }
                s = ident + line;
            }
            return s;
        }
        
        //获取jason中要表示的属性值
        def getAttributeValue(data, descriptor){
            def value = data.get(descriptor.name);
            if(value == null || "" == value){
                //值为空
                return null;
            }
            
            if(descriptor.get("default") == "" + value){
                //等于默认值
                return null;
            }
            
            switch(descriptor.type){
                case "byte":
                case "short":
                case "int":
                case "long":
                case "float":
                case "double":
                case "boolean":
                    return "" + value;
                case "date":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("yyyy-MM-dd");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                case "datetime":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                case "time":
                    if(value instanceof Date){
                        def sf = new SimpleDateFormat("HH:mm:ss");
                        return "'" + sf.format(value) + "'";
                    }else{
                        return "'" + value + "'";
                    }
                default:
                    return "'" + value + "'";
                
            }
        }*/
    }

}