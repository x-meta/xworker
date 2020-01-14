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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.ThingManagerListener;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.ThingUtil;
import org.xmeta.util.UtilMap;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import xworker.dataObject.DataObject;
import xworker.db.DbUtil;
import xworker.task.TaskManager;

public class ThingUtils {
	private static Logger logger = LoggerFactory.getLogger(ThingUtils.class);
	
	/**
	 * 目前是对于临时项目下的事物的索引缓存，如用户在XWorker之外创建的项目。
	 * 
	 */
	private static Map<String, List<Thing>> registThingCache = null;	
	
	/**
	 * 事物注册
	 */
	private static ThreadLocal<List<String>> searchRegistLocal = new ThreadLocal<List<String>>();
	
	/**
	 * 移除一个事物的actions子节点。
	 * 
	 * @param thing
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
	 * 启动注册事物缓存，只针对XWorker之外项目下的事物注册缓存。
	 */
	public static void startRegistThingCache(){
		if(registThingCache == null){
			registThingCache = new HashMap<String, List<Thing>>();
			
			//应该不用频繁刷新的，当使用了ThingManagerListener后是否可以停止定时执行？
			TaskManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable(){
				public void run(){
					try{
						//logger.info("start refresh regist thing cache");
						refreshRegistThingCache();
					}catch(Exception e){
						logger.error("refreshRegistThingCache error", e);
					}
				}
			}, 0, 60000, TimeUnit.MILLISECONDS);
			
			//监控模型的改变，如果是非内部管理的项目的事物，更新索引			
			World.getInstance().registThingManagerListener("*", new ThingManagerListener() {

				@Override
				public void loaded(ThingManager thingManager, Thing thing) {
				}

				@Override
				public void saved(ThingManager thingManager, Thing thing) {
					if(thingManager != null && !isInnerThingManager(thingManager.getName())) {
						initRegistThingCache(thing);
					}
				}

				@Override
				public void removed(ThingManager thingManager, Thing thing) {					
				}
			});
		}
	}
	
	/**
	 * 返回是否是XWorker的内部事物管理器，内部事物管理器的索引是保存到数据库中的。
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isInnerThingManager(String name) {
		//innerThingManagers是XWorker在创建索引时加入的，创建索引时能够访问到的事务管理器为内部事物管理器
		Thing innerThingManagers = World.getInstance().getThing("_local.xworker.config.InnerThingManagers");
		if(innerThingManagers != null) {
			for(Thing child : innerThingManagers.getChilds()) {
				if(child.getMetadata().getName().equals(name)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 刷新注册缓存，这些注册缓存是保存到内存中的。
	 * 
	 * @param thingManager
	 */
	public static void refreshRegistThingCache(ThingManager thingManager) {
		thingManager.getCategory(null).refresh(true);
		Iterator<Thing> iter = thingManager.iterator(null, true);
		while(iter.hasNext()){
			Thing thing = iter.next();
						
			initRegistThingCache(thing);
		}
		
		//排除无效的
		for(String key : registThingCache.keySet()){
			List<Thing> things = registThingCache.get(key);
			for(int i=0; i<things.size(); i++){
				Thing thing = things.get(i);
				if((thing.getParent() != null && thing.getBoolean("th_createIndex") == false)
						|| thing.getMetadata().isRemoved()){
					//logger.info("removed " + thing.getMetadata().getPath());
					things.remove(i);
					i--;
				}
			}
		}
	}
	
	/**
	 * 刷新注册缓存，把非XWorker内部管理的事物管理刷新到内存缓存中。
	 * 刷新时会先清空已有的缓存。
	 */
	public static void refreshRegistThingCache(){		
		//先清空缓存
		if(registThingCache != null) {
			registThingCache.clear();
		}
		
		//ClasstThingManager是否包含在其中，如果不包含，那么应该初始化一次
		
		for(ThingManager thingManager : World.getInstance().getThingManagers()) {
			if(!isInnerThingManager(thingManager.getName())) {
				refreshRegistThingCache(thingManager);
			}
		}		
	}
	
	/**
	 * 刷新一个事物的注册缓存。
	 * 
	 * @param thing
	 */
	public static void initRegistThingCache(Thing thing){		
		if(thing.getBoolean("th_createIndex") || thing.getParent() == null){
            String th_registThing = thing.getStringBlankAsNull("th_registThing");
			//String th_registThing = thing.getStringBlankAsNull("th_registThing");
            //logger.info("registThing:" + th_registThing + ", tihng=" + thing.getMetadata().getPath());
			if(th_registThing != null){
				for(String regist : th_registThing.split("[,]")){
					String[] rs = regist.split("[|]");
					if(rs.length == 2){
						Thing registThing = World.getInstance().getThing(rs[1]);
						if(registThing != null){
							String key = rs[0] + "_" + rs[1];
							List<Thing> things = registThingCache.get(key);
							if(things == null){
								things = new ArrayList<Thing>();
								registThingCache.put(key, things);
							}
							
							things.add(thing);
						}
					}
				}				
			}
		}else{
			//logger.info("not checked : " + thing.getMetadata().getPath());
		}
		
		for(Thing child : thing.getChilds()){
			initRegistThingCache(child);
		}
	}
	
	public static void initFromRegistCache(List<Thing> thingList, Map<String, Thing> thingContext, Thing registorThing, String registType){
		if(registThingCache == null){
			return;
		}
		
		Map<String, String > context = new HashMap<String, String>();
	
		initFromRegistCache(thingList, thingContext, registorThing, registType, context);
			
		Thing thingIndex = World.getInstance().getThing("xworker.lang.util.ThingIndex");
		for(Thing desc : registorThing.getAllDescriptors()){
			if(thingIndex == desc) {
				continue;
			}
			
			initFromRegistCache(thingList, thingContext, desc, registType, context);
		}
		
		for(Thing ext : registorThing.getAllExtends()){
			initFromRegistCache(thingList, thingContext, ext, registType, context);
		}
	}
	
	public static void initFromRegistCache(List<Thing> thingList, Map<String, Thing> thingContext, Thing thing, String registType, Map<String, String> context){
		String path = thing.getMetadata().getPath();
		if(context.get(path) != null){
			return;
		}else{
			context.put(path, path);
		}
		
		if(registThingCache == null){
			return;
		}
		
		List<Thing> things = registThingCache.get(registType + "_" +  path);
		if(things != null){
			for(Thing th : things){
				String thpath = th.getMetadata().getPath();
				if(thingContext.get(thpath) != null){
					continue;
				}else{
					thingContext.put(thpath, th);
				}
				
				if(th.getBoolean("th_registMyChilds")){
					addThingsToRegistList(th.getChilds(), thingContext, thingList);					
				}else{
					thingList.add(th);
				}
			}
		}
	}
	
	/**
	 * 返回注册到指定事物的所有注册类型，它所有的描述者和继承事物也作为注册者。
	 * 
	 * @param registorThing
	 * @param keywords
	 * @param actionContext
	 * @return
	 * @throws SQLException
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
	
	/**
	 * 下一次searchRegistThings的额外加入的事物列表，只对当前线程起效，只作用一次。
	 * 
	 * @param things
	 */
	public static void setSearchRegistLocal(List<String> things) {
		searchRegistLocal.set(things);
	}
	
	public static List<String> getSearchRegistLocal(){
		return searchRegistLocal.get();
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
		
		List<Thing> thingList = new ArrayList<Thing>();
		if("child".equals(registType) && !"xworker.lang.util.ThingIndex".equals(registorThing.getMetadata().getPath())){
			//如果是子节点，优先添加事物自身的以及继承的子节点，
			for(Thing child : registorThing.getChilds()){
				if(child.getBoolean("th_registDisabled") == false) {
					thingList.add(child);
				}
			}
			
			for(Thing ext : registorThing.getExtends()){
				for(Thing child : ext.getChilds()){
					if(child.getBoolean("th_registDisabled") == false) {
						thingList.add(child);
					}
				}
			}
		}else if("childThing".equals(registType) && !"xworker.lang.util.ThingIndex".equals(registorThing.getMetadata().getPath())){
			//如果是子节点，优先添加事物自身的以及继承的子节点，
			for(Thing child : registorThing.getAllChilds("thing")){
				if(child.getBoolean("th_registDisabled") == false) {
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
		
		World world = World.getInstance();
		Thing registByThing = world.getThing("xworker.ide.db.dbindex.app.dataObject.RegistsByThing");
		List<DataObject> results = null;
		if(registByThing != null) {
			try {
				results = (List<DataObject>) registByThing.doAction("query", actionContext, UtilMap.toMap(
					new Object[]{"thing", registorThing, "keywords", keyStr, "registType", registType, 
							"noDescriptor", noDescriptor, "parent", parent}));
			}catch(Exception e) {
				logger.warn("query index form db exception", e);
				//return thingList;
				results = Collections.emptyList();
			}
		}else {
			results = Collections.emptyList();
		}
		
		//过滤重复事物的上下文
		Map<String, Thing> context = new HashMap<String, Thing>();
		for(DataObject obj : results){
			String path = (String) obj.get("path");
			Thing thing = world.getThing(path);
			if(thing != null){
				if(thing.getBoolean("th_registDisabled")){
					continue;
				}
				
				//用户自定义分组				
				thing.getMetadata().setUserGroup(obj.getString("userGroup"));
				if(context.get(path) != null){
					continue;
				}else{
					context.put(path, thing);
				}
				
				//是否注册是子事物，即把自己注册到某一个事物，其实是要注册自己的所有子事物到被注册的事物下
				if(thing.getBoolean("th_registMyChilds")){
					addThingsToRegistList(thing.getChilds(), context, thingList);					
				}else{
					String th_registActionChilds = thing.getStringBlankAsNull("th_registActionChilds");
					if(th_registActionChilds == null) {
						thingList.add(thing);
					}else {
						try {
						List<Thing> childs = thing.doAction(th_registActionChilds, actionContext);
						addThingsToRegistList(childs, context, thingList);
						}catch(Exception e) {
							logger.warn("Add action childs error, thing=" + thing.getMetadata().getPath(), e);
						}
					}
				}
			}
		}
		
		if(registThingCache != null){
			initFromRegistCache(thingList, context, registorThing, registType);
		}
		
		return thingList;
	}
	
	private static void addThingsToRegistList(List<Thing> things, Map<String, Thing> context, List<Thing> thingList) {
		if(things == null) {
			return;
		}
		
		for(Thing child : things){
			if(child.getBoolean("th_registDisabled")){
				continue;
			}
			
			String childPath = child.getMetadata().getPath();
			if(context.get(childPath) != null){
				continue;
			}else{
				context.put(childPath, child);
				thingList.add(child);
			}
		}
	}
	
	/**
	 * <p>根据注册类型（registType）查找注册到被注册事物（registorThing)下的事物。</p>
	 * 
	 * 可以输入关键字查找注册的事物。
	 * 
	 * @param registorThing
	 * @param registType
	 * @param keywords
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext){
		return searchRegistThings(registorThing, registType, keywords, false, actionContext);
	}
	
	/**
     * 获取显示事物Description的URL地址。
     * 
     * @param thing
     * @return
     */
    public static String getThingDescUrl(Thing thing){
    	return XWorkerUtils.getThingDescUrl(thing);
    }
    
    /**
     * 分析XML的结构获取描述者。
     * 
     * @param xml
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Thing parseXMLStrucutre(String xml) throws ParserConfigurationException, SAXException, IOException{
    	Thing thing = createEmptyThing();
    	thing.parseXML(xml);
    	
    	return parseDescriptor(thing);
    }
    
    /**
     * 分析JSON的结构获取描述者。
     * 
     * @param json
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
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
     * 
     * @param thing
     * @return
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
    			if(attr.getBoolean("notXmlAttribute") == false){
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
     * 
     * @param jsonString
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
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
