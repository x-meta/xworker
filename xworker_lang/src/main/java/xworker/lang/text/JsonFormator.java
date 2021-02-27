package xworker.lang.text;

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
import xworker.lang.executor.Executor;
import xworker.util.JacksonFormator;

public class JsonFormator {
	private static final String TAG = JsonFormator.class.getName();
	private static Map<Class<?>, JsonDataFormater> formaters = new HashMap<Class<?>, JsonDataFormater>();
	static{
		regist("xworker.lang.text.ThingJsonFormater");
		regist("xworker.dataObject.utils.DataObjectJsonFormater");
	}
	
	/**
	 * 在cls中写static{JsonFormator.regist(cls, formater);};
	 * @param data
	 * @return
	 * @throws OgnlException
	 */
	private static void regist(String cls) {
		try {
			Class.forName(cls);
		}catch(Exception e) {			
		}
	}
	
	public static void regist(Class<?> cls, JsonDataFormater formater) {
		formaters.put(cls, formater);
	}
	
	public static String format(Object data) throws Exception{
		return format(data, "");
	}
	
	@SuppressWarnings("unchecked")
	public static String format(Object data, String ident) throws Exception{
		if(data== null){
            return null;
        }

		JsonDataFormater formater = formaters.get(data.getClass());
		if(formater != null) {
			return formater.format(data, ident);
		}
		
		if(data instanceof Iterable){
            String str = "[";
            for(Object childThing : (Iterable<Object>) data){
                if(!"[".equals(str)){
                    str = str + ",";
                }

                format(childThing, ident + "    ");
            }
            str = str + "\n" + "]";
            return str;
        }else{
            return JacksonFormator.formatObject(data);
        }
	}
	
	//获取jason中要表示的属性值
	public static String getAttributeValue(Object data, Thing descriptor){
		Object value = null;
		String name = descriptor.getString("name");
		if(data instanceof Map){
			value = ((Map<String, Object>) data).get(name);
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
