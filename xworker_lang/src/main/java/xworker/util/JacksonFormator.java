package xworker.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.Thing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonFormator {
	private static  ObjectMapper mapper = new ObjectMapper();
	static{
		SimpleModule module = new SimpleModule();  
        module.addSerializer(Thing.class, new JsonSerializer<Thing>() {  
            @Override  
            public void serialize(Thing thing, JsonGenerator jgen,  
                    SerializerProvider pro) throws IOException,  
                    JsonProcessingException {
            	
            	jgen.writeStartObject();
            	
            	try{
	            	//属性
	            	for(String key : thing.getAttributes().keySet()){            		
	            		Object value = thing.getAttribute(key);            		
	            		if(value != null){
	            			jgen.writeFieldName(key);
	                		pro.defaultSerializeValue(value, jgen);
	            		}
	            	}
	            	
	            	//metadata，暂时没有使用，以后可能会用再实现
	            	
	            	//子节点
	            	jgen.writeFieldName("childs");
	            	pro.defaultSerializeValue(thing.getChilds(), jgen);
            	} finally{
            		jgen.writeEndObject();
            	}
            }  
        });  
        mapper.registerModule(module);  
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);	
	}
	
	
	public static ObjectMapper getObjectMapper(){
		return mapper;
	}
	
	public static String formatObject(Object object) throws Exception{
		return mapper.writeValueAsString(object);		
	}
	
	public static Object parseObject(String object) throws Exception{
		if(object.startsWith("[")){
			return mapper.readValue(object, List.class);
		}else{
			return mapper.readValue(object, Map.class);
		}
	}

	
	public static Object parseObject(HttpServletRequest request, String paramName) throws Exception{
		String str = request.getParameter(paramName);
		if(str != null){
			return parseObject(str);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("serial")
	static class NullKeySerializer extends StdSerializer<Object> {
        public NullKeySerializer() {
            this(null);
        }

        public NullKeySerializer(Class<Object> t) {
            super(t);
        }

        @Override
        public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeFieldName("");
        }
    }
}
