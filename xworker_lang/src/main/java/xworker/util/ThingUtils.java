/*******************************************************************************
* Copyright 2007-2014 See AUTHORS file.
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.ThingUtil;
import org.xml.sax.SAXException;
import xworker.db.DbUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ThingUtils {
	private static final String TAG = ThingUtils.class.getName();
	private static IThingUtils thingUtils = null;
	public static void startRegistThingCache(){
		ThingRegistUtils.startRegistThingCache();
	}

	static{
		try {
			Class<?> cls = Class.forName("xworker.util.ThingUtilsImpl");
			thingUtils = (IThingUtils) cls.getConstructor().newInstance();
		}catch(Exception ignored) {
		}
	}

	public static boolean isFirstCacheFinised(){
		return ThingRegistUtils.isFirstCacheFinised();
	}

	/**
	 * 移除一个事物的actions子节点。
	 */
	public static void removeActions(Thing thing) {
		for(int i=0; i<thing.getChilds().size(); i++) {
			Thing child = thing.getChilds().get(i);
			if("actions".equals(child.getThingName())) {
				thing.removeChild(child);
				i--;
				continue;
			}

			removeActions(child);
		}
	}

	/**
	 * 返回注册到指定事物的所有注册类型，它所有的描述者和继承事物也作为注册者。
	 */
	public static List<String> getRegistTypes(Thing registorThing, List<String> keywords, boolean parent, ActionContext actionContext) throws SQLException{
		if(registorThing == null){
			return Collections.emptyList();
		}
		
		String sql = null;
		if(parent){
			sql = "select distinct registType from tblThingRegists where thingPath =?";
		}else{
			sql = "select distinct registType from tblThingRegists where registThingPath =?";
		}
		Thing dataSource = World.getInstance().getThing("xworker.ide.db.datasource.XWorkerDataSource");
		if(dataSource == null){
			return Collections.emptyList();
		}

		Connection con = dataSource.doAction("getConnection", actionContext);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			List<Thing> things = ThingUtil.getAllDescriptorsAndExtends(registorThing);
			for(int i=0; i<things.size(); i++){
				if(parent){
					sql = sql + " or thingPath =?";
				}else{
					sql = sql + " or registThingPath =?";
				}
			}
			pst  = con.prepareStatement(sql);
			pst.setString(1, registorThing.getMetadata().getPath());
			
			int index = 2;
			for(Thing thing : things){
				pst.setString(index, thing.getMetadata().getPath());
				index++;
			}
			
			rs = pst.executeQuery();
			List<String> types = new ArrayList<String>();
			while(rs.next()){
				types.add(rs.getString("registType"));
			}
			
			Collections.sort(types);
			return types;
		}finally{
			DbUtil.close(con, pst, rs);
		}
	}

	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  ActionContext actionContext){
		return searchRegistThings(registorThing, registType, keywords, parent, false, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  boolean noDescriptor, ActionContext actionContext){
		if(registorThing == null){
			return Collections.emptyList();
		}
		
		//noDescriptor默认是false，为什么？已经不知道原因了，在使用时应该注意。
		//默认值不能改成true，因为改成true有些现有的索引会出问题，如ThingIndex主索引
		
		List<Thing> thingList = new ArrayList<>();
		if("child".equals(registType) && !"xworker.lang.util.ThingIndex".equals(registorThing.getMetadata().getPath())){
			//如果是子节点，优先添加事物自身的以及继承的子节点，
			for(Thing child : registorThing.getChilds()){
				if(!child.getBoolean("th_registDisabled")) {
					thingList.add(child);
				}
			}
			
			for(Thing ext : registorThing.getExtends()){
				for(Thing child : ext.getChilds()){
					if(!child.getBoolean("th_registDisabled")) {
						thingList.add(child);
					}
				}
			}
		}else if("childThing".equals(registType) && !"xworker.lang.util.ThingIndex".equals(registorThing.getMetadata().getPath())){
			//如果是子节点，优先添加事物自身的以及继承的子节点，
			for(Thing child : registorThing.getAllChilds("thing")){
				if(!child.getBoolean("th_registDisabled")) {
					thingList.add(child);
				}
			}
			/*
			for(Thing ext : registorThing.getExtends()){
				for(Thing child : ext.getChilds("thing")){
					thingList.add(child);
				}
			}*/
			
			registType = "child";
		}
		
		String keyStr = "";
		if(keywords != null) {
			for(int i=0; i<keywords.size(); i++){
				if(i > 0){
					keyStr = keyStr + ",";
				}
				keyStr = keyStr + keywords.get(i);
			}
		}

		Map<String, Thing> context = new HashMap<>();
		if(ThingRegistUtils.getRegistThingCache() != null){
			//从缓存注册中查询
			ThingRegistUtils.initFromRegistCache(thingList, context, registorThing, registType);
		}

		if(thingUtils != null){
			//从数据库中查询
			thingUtils.searchRegistThings(thingList, context, registorThing, keyStr, registType, noDescriptor, parent, actionContext);
		}

		return thingList;
	}

	public static void setSearchRegistLocal(List<String> things) {
		ThingRegistUtils.setSearchRegistLocal(things);
	}

	public static List<String> getSearchRegistLocal() {
		return ThingRegistUtils.getSearchRegistLocal();
	}
	/**
	 * <p>根据注册类型（registType）查找注册到被注册事物（registorThing)下的事物。</p>
	 *
	 * 可以输入关键字查找注册的事物。
	 */
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext){
		return searchRegistThings(registorThing, registType, keywords, false, actionContext);
	}
	
	/**
     * 获取显示事物Description的URL地址。
     */
    public static String getThingDescUrl(Thing thing){
    	return XWorkerUtils.getThingDescUrl(thing);
    }
    
    /**
     * 分析XML的结构获取描述者。
     */
    public static Thing parseXMLStrucutre(String xml) throws ParserConfigurationException, SAXException, IOException{
    	Thing thing = createEmptyThing();
    	thing.parseXML(xml);
    	
    	return parseDescriptor(thing);
    }
    
    /**
     * 分析JSON的结构获取描述者。
     */
    public static Thing parseJSONStructure(String json) throws JsonParseException, JsonMappingException, IOException{
    	Thing thing = parseJson(json);
    	
    	return parseDescriptor(thing);
    }
    
    public static Thing parseDescriptor(Thing thing){
    	if(thing.getDescriptors().size() > 1){
    		return thing.getDescriptor().detach();
    	}else{
    		Thing desc = new Thing("xworker.lang.MetaDescriptor3");
    		String nodeName = thing.getStringBlankAsNull(XmlCoder.NODE_NAME);
    		if(nodeName != null){
    			desc.put("name", nodeName);
    		}else{
    			desc.put("name", "thing");
    		}
    		if(thing.getBoolean("json_isArray")){
    			desc.put("json_isArray", "true");
    		}
    		
    		
    		for(String key : thing.getAttributes().keySet()){
    			if(key.equals(XmlCoder.NODE_NAME)){
    				continue;
    			}
    			if("json_isArray".equals(key)){
    				continue;
    			}
    			
    			Thing attr = new Thing("xworker.lang.MetaDescriptor3/@attribute");
    			attr.put("name", key);
    			desc.addChild(attr);
    		}
    		
    		for(Thing child : thing.getChilds()){
    			desc.addChild(parseDescriptor(child));
    		}
    		return desc;
    	}
    }
    
    /**
     * 把事物转化成JSON字符串。
     */
    public static String toJson(Thing thing){
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode node = toJsonNode(thing, mapper);
    	return node.toString();
    }
    
    @SuppressWarnings("deprecation")
	public static JsonNode toJsonNode(Thing thing, ObjectMapper mapper){    	
    	boolean isArray = thing.getDescriptor().getBoolean("json_isArray");    	
    	if(isArray){
    		ArrayNode node = mapper.createArrayNode();
    		for(Thing child : thing.getChilds()){
    			node.add(toJsonNode(child, mapper));
    		}
    		
    		return node;
    	}else{
    		ObjectNode node = mapper.createObjectNode();
    		List<Thing> attributes = thing.getAttributesDescriptors();
    		for(Thing attr : attributes){
    			if(!attr.getBoolean("notXmlAttribute")){
    				String name = attr.getMetadata().getName();
    				Object value = thing.get(name);
    				if(value != null){
    					String type = attr.getString("string");
    					if(type == null || "".equals(type) || "string".equals(type)){
    						node.put(name, thing.getString(name));
    					}else if("bigDecimal".equals(type)){
    						node.put(name, thing.getBigDecimal(name));
    					}else if("bigInteger".equals(type)){
    						node.put(name, thing.getBigDecimal(name));    						
    					}else if("boolean".equals(type)){
    						node.put(name, thing.getBoolean(name));
    					}else if("byte".equals(type)){
    						node.put(name, thing.getByte(name));
    					}else if("char".equals(type)){
    						node.put(name, thing.getChar(name));
    					}else if("date".equals(type)){
    						node.put(name, thing.getString(name));
    					}else if("double".equals(type)){
    						node.put(name, thing.getDouble(name));
    					}else if("float".equals(type)){
    						node.put(name, thing.getFloat(name));
    					}else if("int".equals(type)){
    						node.put(name, thing.getInt(name));
    					}else if("long".equals(type)){
    						node.put(name, thing.getLong(name));
    					}else if("short".equals(type)){
    						node.put(name, thing.getShort(name));
    					}else{
    						throw new ActionException("Not support type '" + type + "'");
    					}
    				}
    			}
    		}
    		
    		for(Thing child : thing.getChilds()){
    			node.put(child.getThingName(), toJsonNode(child, mapper));
    		}
    		return node;
    	}
    }
    
    /**
     * 把一个JSON字符串转化为一个事物。
     */
    @SuppressWarnings("unchecked")
	public static Thing parseJson(String jsonString) throws JsonParseException, JsonMappingException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
    	jsonString = jsonString.trim();    	
    	
    	Thing thing = createEmptyThing();
    	ByteArrayInputStream in = new ByteArrayInputStream(jsonString.getBytes());
    	if(jsonString.startsWith("{")){
    		Map<String, Object> values = mapper.readValue(in, Map.class);
    		parseJson(thing, values);
    	}else{
    		List<Map<String, Object>> valueList = mapper.readValue(in, List.class);
    		Thing child = createEmptyThing();
    		thing.put("json_isArray", "true");
			thing.addChild(child);
			
			for(Map<String, Object> map : valueList){
				Thing mapChild = createEmptyThing();
				parseJson(mapChild, map);
				child.addChild(mapChild);
			}
    	}
    	
    	return thing;
    }
    
    public static Thing createEmptyThing(){
    	Thing thing = new Thing();
    	thing.getAttributes().remove("name");
    	thing.getAttributes().remove("label");
    	thing.getAttributes().remove("descriptors");
    	
    	return thing;
    }
    
    @SuppressWarnings("unchecked")
	public static void parseJson(Thing thing, Map<String, Object> values){
    	for(String key : values.keySet()){
    		Object value = values.get(key);
    		if(value instanceof Map){
    			Map<String, Object> v = (Map<String, Object>) value;
    			Thing child = createEmptyThing();
    			child.put(XmlCoder.NODE_NAME, key);
    			parseJson(child, v);
    			thing.addChild(child);
    		}else if(value instanceof List){
    			List<Object> list = (List<Object>) value;
    			parseJson(thing, list, key);
    		}else{
    			thing.put(key, value);
    			
    	    }
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static void parseJson(Thing thing, List<Object> list, String key){
    	Thing child = createEmptyThing();
		child.put(XmlCoder.NODE_NAME, key);
		thing.put("json_isArray", "true");
		thing.addChild(child);
		
		for(Object obj : list){
			if(obj instanceof Map){
				Map<String, Object> map = (Map<String, Object>) obj;
				Thing mapChild = createEmptyThing();
				parseJson(mapChild, map);
				child.addChild(mapChild);
			}else if(obj instanceof List){
				Thing newchild = createEmptyThing();
				parseJson(newchild, (List<Object>) obj, null);
			}
		}
	}
}
