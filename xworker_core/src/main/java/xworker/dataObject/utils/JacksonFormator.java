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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.Thing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
}