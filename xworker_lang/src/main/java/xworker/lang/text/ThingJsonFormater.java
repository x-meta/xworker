package xworker.lang.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

public class ThingJsonFormater implements JsonDataFormater{
	static {
		JsonFormator.regist(Thing.class, new ThingJsonFormater());
	}

	@SuppressWarnings("unchecked")
	@Override
	public String format(Object data, String ident) {
		Thing thing = (Thing) data;
		
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
            Object value = JsonFormator.getAttributeValue(thing, attribute);
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

	@Override
	public Object parse(String json) {
		// TODO Auto-generated method stub
		return null;
	}

}
