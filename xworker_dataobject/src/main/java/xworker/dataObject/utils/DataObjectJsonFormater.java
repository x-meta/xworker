package xworker.dataObject.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import xworker.dataObject.DataObject;
import xworker.lang.text.JsonDataFormater;

public class DataObjectJsonFormater implements JsonDataFormater{
	static {
		xworker.lang.text.JsonFormator.regist(DataObject.class, new DataObjectJsonFormater());
	}
	
	@Override
	public String format(Object data, String ident) {
		DataObject dataObject = (DataObject) data;
				
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
            String value = JsonFormator.getAttributeValue(dataObject, attribute);
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

	@Override
	public Object parse(String json) {
		// TODO Auto-generated method stub
		return null;
	}

}
