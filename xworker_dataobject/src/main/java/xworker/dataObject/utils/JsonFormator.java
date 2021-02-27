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

import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.lang.executor.Executor;
import xworker.util.JacksonFormator;

public class JsonFormator {
	private static final String TAG = JsonFormator.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String format(Object data) throws OgnlException{
		if(data== null){
            return null;
        }else if(data instanceof Thing){
            return format((Thing) data, "");
        }else if(data instanceof DataObject){
            return format((DataObject) data, "");
        }else if(data instanceof List || data instanceof Thing[] || data instanceof DataObject[]){
            String str = "[";
            for(Object childThing : (Iterable<Object>) data){
                if(!"[".equals(str)){
                    str = str + ",";
                }
                if(childThing instanceof DataObject){
                    str = str + "\n" + format((DataObject) childThing, "    ");
                }else{
                    str = str + "\n" + format((Thing) childThing, "    ");
                }
            }
            str = str + "\n" + "]";
            return str;
        }else{
            return null;
        }
	}
	
    //转化一个事物到json数据格式的字符串
    @SuppressWarnings("unchecked")
	public static String format(Thing thing, String ident){
        String str = ident + "{";
        Map<String, Object> context = new HashMap<String, Object>();
        //属性
        for(Thing attribute : thing.getAllAttributesDescriptors()){
        	String name = attribute.getString("name");
            if(context.get(name) != null){
                //取所有的子事物可能有重名的，比如多继承时
                continue;
            }
            context.put(name, attribute);
            Object value = getAttributeValue(thing, attribute);
            if(value != null && !"".equals(value)){
                if(!(ident + "{").equals(str)){
                    str = str + ",\n    " + ident + "\"" + name + "\":" + value;
                }else{
                    str = str + "\n    " + ident + "\"" + name + "\":" + value;
                }
            }
        }
        
        //子事物
        for(Thing child : thing.getAllChildsDescriptors()){
        	String name = child.getString("name");
            if(context.get(name) != null){
                continue;
            }
            context.put(name, child);
            
            if(child.getBoolean("many")){
                List<Thing> childs = (List<Thing>) thing.get(name + "@");
                if(childs.size() > 0){
                    String childStr = "[";
                    for(Thing childThing : childs){
                        if(!"[".equals(childStr)){
                            childStr = childStr + ",";
                        }
                        childStr = childStr + "\n" + format(childThing, ident + "    ");
                    }
                    childStr = childStr + "\n    " + ident + "]";
                    if(!(ident + "{").equals(str)){
                        str = str + ",";
                    }
                    str = str + "\n    " + ident + "\"" + name + "\":" + childStr;
                }
            }else{
                Thing childThing = thing.getThing(name + "@0");
                if(childThing != null){
                    if(!(ident + "{").equals(str)){
                        str = str + ",";
                    }
                    str = "\n    \"" + name + "\":" + format(childThing, "    " + ident);
                }
            }
        }
        return str + "\n" + ident + "}";
    }
    
    //转化一个数据对象到json数据格式的字符串
    @SuppressWarnings({ "unchecked", "rawtypes"})
	public static String  format(DataObject dataObject, String ident) throws OgnlException{
        String str = ident + "{";
        Thing descriptor = dataObject.getMetadata().getDescriptor();
        Map<String, Object> context = new HashMap<String, Object>();
        //属性
        /*
        if(dataObject.extJsDataId == null || dataObject.extJsDataId == ""){
            def keys = dataObject.doAction("getKeyAttributes", actionContext);
            if(keys != null && keys.size() > 0){
                dataObject.extJsDataId = dataObject.get(keys.get(0).name);
            }
        }
        */
        for(Thing attribute : descriptor.getAllChilds("attribute")){
        	String name = attribute.getString("name");
            if(context.get(name) != null){
                //取所有的子事物可能有重名的，比如多继承时
                continue;
            }
            context.put(name, attribute);
            String value = getAttributeValue(dataObject, attribute);
            value= value==null? "\"\"" : value;
            value = "" + value;
            if( !(ident + "{").equals(str)){
                str = str + ",\n    " + ident + "\"" + name + "\":" + value;
            }else{
                str = str + "\n    " + ident + "\"" + name + "\":" + value;
            }
        }
        //旧标识
        /*
        if(dataObject.extJsDataId != null && dataObject.extJsDataId != ""){
            if(str != (ident + "{")){
                str = str + ",\n    " + ident + "\"extJsDataId\":\"" + dataObject.extJsDataId + "\"";
            }else{
                str = str + "\n    " + ident + "\"extJsDataId\":\"" + dataObject.extJsDataId + "\"";
            }
        }
        */
        
        //子事物
        for(Thing child : (List<Thing>) descriptor.get("thing@")){
        	String name = child.getString("name");
            if(context.get(name) != null){
                continue;
            }
            context.put(name, child);
            
            if(!child.getBoolean("cascadeFormat")){
                continue;
            }
            
            if(child.getBoolean("many")){
                Iterable<Object> childs = (Iterable<Object>) dataObject.get(name);
                if(childs == null){
                    continue;
                }
                
                int size = 0;
                if(childs instanceof List){
                	size = ((List) childs).size();
                }else if(childs.getClass().isArray()){
                	size = (Integer) OgnlUtil.getValue("length", childs);
                }
                if(size > 0){
                    String childStr = "[";
                    for(Object childThing : childs){
                        if(!"[".equals(childStr)){
                            childStr = childStr + ",";
                        }
                        if(childThing instanceof DataObject){
                            childStr = childStr + "\n" + format((DataObject) childThing, ident + "    ");
                        }else{
                            childStr = childStr + "\n" + format((Thing) childThing, ident + "    ");
                        }
                    }
                    childStr = childStr + "\n    " + ident + "]";
                    if(!(ident + "{").equals(str)){
                        str = str + ",";
                    }
                    str = str + "\n    " + ident + "\"" + name + "\":" + childStr;
                }
            }else{
                Object childThing = dataObject.get(name);
                if(childThing != null){
                    if(!(ident + "{").equals(str)){
                        str = str + ",";
                    }
                    if(childThing instanceof DataObject){
                        str = str + "\n    \"" + name + "\":" + format((DataObject) childThing, "    " + ident);
                    }else{
                        str = str + "\n    \"" + name + "\":" + format((Thing) childThing, "    " + ident);
                    }
                }
            }
        }
        return str + "\n" + ident + "}";
    }
    
	//获取jason中要表示的属性值
	public static String getAttributeValue(Object data, Thing descriptor){
		Object value = null;
		String name = descriptor.getString("name");
		if(data instanceof DataObject){
			value = ((DataObject) data).get(name);
		}else{
			value = ((Thing) data).get(name);
		}
		
        if(value == null || "".equals(value)){
            //值为空
            return null;
        }
        
        //有些地方需要默认值，因此取消这句话
        //if(String.valueOf(value).equals(descriptor.get("default"))){
            //等于默认值
        //    return null;
        //}
            
        String type = descriptor.getString("type"); 
        if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "long".equals(type) ||
        		"float".equals(type) || "double".equals(type) || "boolean".equals(type)){
                return String.valueOf(value);
        }else if("date".equals(type)){
            if(value instanceof Date){
                String pattern = descriptor.getString("editPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "yyyy-MM-dd";
                }
                SimpleDateFormat sf = new SimpleDateFormat(pattern);
                return "\"" + sf.format(value) + "\"";
            }else{
                return "\"" + value + "\"";
            }
        }else if("datetime".equals(type)){
            if(value instanceof Date){
                String pattern = descriptor.getString("editPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "yyyy-MM-dd HH:mm:ss";
                }
                SimpleDateFormat sf = new SimpleDateFormat(pattern);
                return "\"" + sf.format(value) + "\"";
            }else{
                return "\"" + value + "\"";
            }
        }else if("time".equals(type)){
            if(value instanceof Date){
                String pattern = descriptor.getString("editPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "HH:mm:ss";
                }
                SimpleDateFormat sf = new SimpleDateFormat(pattern);
                return "\"" + sf.format(value) + "\"";
            }else{
                return "\"" + value + "\"";
            }
        }else{
        	String v = String.valueOf(value);
            v = v.replaceAll("[\\\\]", "\\\\\\\\");
            v = v.replaceAll("[\"]", "\\\\\"");
            //v = v.replaceAll("[']", "\\\\'");
            v = v.replaceAll("[\r\n]", "\\\\n");
            v = v.replaceAll("[\r]", "\\\\n");
            v = v.replaceAll("[\n]", "\\\\n");
            //log.info("value=" + value);
            return "\"" + v + "\"";            
        }
    }
	
	public static String formatString(String v){
		if(v == null){
			return v;
		}
		
		v = v.replaceAll("[\\\\]", "\\\\\\\\");
        v = v.replaceAll("[\"]", "\\\\\"");
        v = v.replaceAll("[']", "\\\\'");
        v = v.replaceAll("[\r\n]", "\\\\n");
        v = v.replaceAll("[\r]", "\\\\n");
        v = v.replaceAll("[\n]", "\\\\n");
        return v;
	}
	
    public static Object parse(String jsonText, ActionContext actionContext) throws Exception{
    	return JacksonFormator.parseObject(jsonText);
    	/*
        //数据对象实例
        StringCharacterIterator iter = new StringCharacterIterator(jsonText);
        if(jsonText.startsWith("{")){
            Map<String, Object> theData = new HashMap<String, Object>();
            parseData(iter, theData, actionContext);
            return theData;
        }else if(jsonText.startsWith("[")){
            List<Object> objList = new ArrayList<Object>();;
            parseDataList(iter, objList, actionContext);
            return objList;
        }else{
            return null;
        }*/
    }
    
    private static void parseData(StringCharacterIterator iter, Map<String, Object> dataObject, ActionContext actionContext){
        while(true){
            char c = iter.next();
            if(c == StringCharacterIterator.DONE || c == '}'){
                break;
            }
            if(c == '{'){
                continue;
            }
            if(c == '"'){
                String name = parseString(iter, new char[]{'"'});  
                if(name != null){
                    name = name.trim();
                }                         
                parseValue(iter, name, dataObject, new char[]{',', '}'}, actionContext);
                if(iter.current() == '}'){
                    iter.next();
                    break;
                }else{
                    //c = iter.next();
                    continue;
                }            
            }else if(c == '\''){
                String name = parseString(iter, new char[]{'\''});   
                if(name != null){
                    name = name.trim();
                }
                iter.next();
                parseValue(iter, name, dataObject, new char[]{',', '}'}, actionContext);
                if(iter.current() == '}'){
                    break;
                }else{
                    iter.next();
                    continue;
                }  
            }else if(c == ','){
                continue;
            }else if(c == ' '){
                continue;
            }else if(c == '\n'){
            	continue;
            }else{
                String name = "" + c + parseString(iter, new char[]{':'});
                if(name != null){
                    name = name.trim();
                }
                parseValue(iter, name, dataObject, new char[]{',', '}'}, actionContext);
                if(iter.previous() == '}'){
                    iter.next();
                    break;
                }else{
                    iter.next();
                    continue;
                }  
            }        
        }
    }
    
    private static void parseDataList(StringCharacterIterator iter, List<Object> dataObjectList, ActionContext actionContext){
        while(true){
            char c = iter.next();
            if(c == StringCharacterIterator.DONE){
                break;
            }    
            if(c == '{'){
                //log.info("list parse obj");
                Map<String, Object> child = new HashMap<String, Object>();
                parseData(iter, child, actionContext);
                dataObjectList.add(child);
                continue;
            }else if(c == '['){
                //log.info("list parse list");
                List<Object> child = new ArrayList<Object>();
                parseDataList(iter, child, actionContext);
                dataObjectList.add(child);
                continue;
            }else if(c == '"'){
                //log.info("list parse \" value");
                dataObjectList.add(parseString(iter, new char[]{'"'}));
                continue;
            }else if(c == '\''){
                //log.info("list parse ' value");
                dataObjectList.add(parseString(iter, new char[]{'\''}));
                continue;
            }else if(c == ']'){
                break;
            }else if(c == ','){        
                continue;
            }else if(c == ' '){
                continue;
            }else{          
                if(iter.previous() == ']'){
                    iter.next();
                    break;
                }else{
                    iter.next();
                    continue;
                }
            }
        }
    }
    
    private static String parseString(StringCharacterIterator iter, char[] endChars){
        boolean isMeta = false;
        StringBuilder str = new StringBuilder();
        //println "parse string endchars=" + endChars;
        while(true){
            char c = iter.next();
            if(c == StringCharacterIterator.DONE){
                return str.toString();
            }
            
            //println "parse string char=" + c;
            if(isMeta == false && isEndChar(c, endChars)){
                //println "parse stirng return";
                return str.toString();
            }else if(c == '\\' && isMeta == false){
                char nextc = iter.next();
                if(nextc == '"'){
                    str.append("\"");
                }else if(nextc == '\''){
                    str.append("'");
                }else if(nextc == 'n'){
                    str.append("\n");
                }else if(nextc == 'r'){
                    str.append("\r");
                }else if(nextc == 't'){
                    str.append("\t");
                }else{
                    str.append("\\");
                    iter.previous();
                }
            }else if(isMeta && isEndChar(c, endChars)){
                isMeta = false;
                str.append(c);
            }else if(isMeta){
                str.append('\\');    
                str.append(c);     
                isMeta = false; 
            }else{
                str.append(c);
            }
        }
    }
    
    private static boolean isEndChar(char ch, char[] endChars){
        for(char c : endChars){
            if(c == ch){
                return true;
            }
        }
        
        return false;
    }    
    
    //分析值
    private static void parseValue(StringCharacterIterator iter, String name,  Map<String, Object> dataObject, char[] endChars, ActionContext actionContext){
        while(true){
            char c = iter.next();
            if(c == StringCharacterIterator.DONE){
                return ;
            }
            if(c == '{'){
                //子事物
                Map<String, Object> child = new HashMap<String, Object>();
                parseData(iter, child, actionContext);
                dataObject.put(name, child);
                return;
            }else if(c == '"'){
                //普通属性
                String value = parseString(iter, new char[]{'"'});
                dataObject.put(name, value);
                return;
            }else if(c == '\''){
                //普通属性
                String value = parseString(iter, new char[]{'\''});
                dataObject.put(name, value);
                return;
            }else if(c == '['){
                //子事物列表
                List<Object> child = new ArrayList<Object>();
                parseDataList(iter, child, actionContext);
                dataObject.put(name, child);
                return;
            }else if(c == ' '){
                continue;
            }else if(c == ':'){
                continue;
            }else{
                String value = "" + c + parseString(iter, endChars);
                if(value != null && !"".equals(value)){                
                	//非字符串类型的数据，当做布尔值或数字
                	if(value.equals("true")){
                		dataObject.put(name, true);
                	}else if(value.equals("false")){
                		dataObject.put(name, false);
                	}else{
	                    try{
	                        dataObject.put(name, OgnlUtil.getValue(value, actionContext));
	                    }catch(Exception e){
	                        Executor.info(TAG, "JsonDataFormat get value error, value=" + value, e);
	                        //OgnlUtil.setValue(name, actionContext, dataObject, value);
	                    }
                	}
                }
                /*
               
                def v = actionContext.get(value);
                if(v != null){
                    dataObject.put(name, value);
                }else{
                    if(value != null && value.trim().toLowerCase() == "true"){
                        dataObject.put(name, true);
                    }else if(value != null && value.trim().toLowerCase() == "false"){
                        dataObject.put(name, false);
                    }else{
                        try{
                            dataObject.put(name, Double.parseDouble(value));
                        }catch(Exception e){
                            //dataObject.put(name, value);
                        }
                    }
                }*/             
                return;
            }
        }
    }
    
    /**
     * 把从json文本分析出来的对象转换成数据对象。
     * 
     * 如果json对象是Map，那么分析属性值放到数据对象中，如果Json对象是List,那么返回一个数据对象的列表。
     * 
     * @param jsonObject
     * @param dataObject
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Object jsonObjectToDataObject(Object jsonObject, DataObject dataObject){
    	if(jsonObject instanceof Map){
    		mapObjectToDataObject((Map<String, Object>) jsonObject, dataObject);
    		return dataObject;
    	}else if(jsonObject instanceof List){
    		Thing descriptor = dataObject.getMetadata().getDescriptor();
    		List<DataObject> datas = new ArrayList<DataObject>();
    		for(Map<String, Object> childData : (List<Map<String, Object>>) jsonObject){
				DataObject child = new DataObject(descriptor);
				mapObjectToDataObject(childData, child);
				datas.add(child);
			}
    		
    		return datas;
    	}else{
    		return null;
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void mapObjectToDataObject(Map<String, Object> data, DataObject dataObject){
    	Thing descriptor = dataObject.getMetadata().getDescriptor();
    	for(Thing attribute : descriptor.getChilds("attribute")){
    		String name = attribute.getString("name");
    		Object value = data.get(name);
    		if(value != null){
    			dataObject.put(name, value);
    		}
    	}
    	
    	for(Thing thing : descriptor.getChilds("thing")){
    		String name = thing.getString("name");
    		Object value = data.get(name);
    		if(value != null){
    			if(value instanceof Map && thing.getBoolean("many") == false){
    				DataObject child = new DataObject(thing);
    				mapObjectToDataObject((Map<String, Object>) value, child);
    				dataObject.put(name, child);
    			}else if(value instanceof List && thing.getBoolean("many")){
    				DataObjectList childs = new DataObjectList(thing);
    				for(Map<String, Object> childData : (List<Map<String, Object>>) value){
    					DataObject child = new DataObject(thing.getString("dataObjectPath"));
    					mapObjectToDataObject(childData, child);
        				childs.add(child);
    				}
    				
    				dataObject.put(name, childs);
    			}
    		}
    	}
    }
    
    /**
     * 一个简单的格式化Thing到json的方法，他把事物的属性当做已经格式化的json字符串，比如属性的值是'abc'等，
     * 它把属性的值直接添加到json串中。
     * 
     * @param thing
     * @param ident
     * @param haveDefaultValue 是否包含默认值
     * @return
     */
    public static String formatThingAttributeAsString(Thing thing, String ident, boolean haveDefaultValue){
    	String json = "{\n";
    	boolean have = false;
    	//属性
    	for(Thing descriptor : thing.getAllAttributesDescriptors()){
    		String path = descriptor.getMetadata().getPath();
    		if(path.startsWith("xworker.lang") || path.startsWith("_transient")){
                //事物的定义的属性不包括
                continue;
            }
    		
    		if(descriptor.getBoolean("notXmlAttribute")){
    			continue;
    		}
    		
    		String name = descriptor.getMetadata().getName();
    		String value = thing.getStringBlankAsNull(name);
    		
    		if(value == null){
    			continue;
    		}
    		
    		if(!haveDefaultValue && value.equals(descriptor.getStringBlankAsNull("default"))){
    			continue;
    		}
    		
    		if(value != null){
    			if(have){
        			json = json + ",\n";
        		}
    			
    			have = true;
    			json = json + ident + "    " + name + ": " + value; 
    		}
    	}    
    	
    	//子事物
    	for(Thing child : thing.getChilds()){
    		if(child.getDescriptor().getBoolean("notXmlAttribute")){
    			continue;
    		}
    		
    		String childJson = formatThingAttributeAsString(child, ident + "    ", haveDefaultValue);
    		if(childJson != null){
    			if(have){
        			json = json + ",\n";
        		}
    			
    			have = true;
    			json = json + ident + "    " + child.getDescriptor().getMetadata().getName() + ": " + childJson; 
    		}
    	}
    	
    	return json + "\n" + ident + "}";
    }
}