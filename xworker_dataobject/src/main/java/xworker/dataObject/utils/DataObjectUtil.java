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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import com.csvreader.CsvWriter;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.db.DbDataObjectIterator;
import xworker.dataObject.db.DbDataObject;
import xworker.dataObject.query.QueryConfig;
import xworker.dataObject.query.UtilCondition;
import xworker.lang.executor.Executor;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

public class DataObjectUtil {
	private static final String TAG = DataObjectUtil.class.getName();
	
	/** 分页类型范围，从起始到截至，start = pageInfo.getStart() + 1， end = start + pageInfo.getLimit() - 1 */
	public static final byte PAGE_BETWEEN = 1;
	/** 分页类型范围，从开始到限制棵树，start = pageInfo.getStart(), end = pageInfo.getLimit() */
	public static final byte PAGE_LIMIT = 2;
	
	/**
	 * 把Map转化为Condition事物，其中map中的childs属性是子节点列表，子节点列表类型是List&lt;Map&lt;String, Object&gt;&gt;。
	 * 
	 * @param amap 集合
	 * @return 事物
	 */
	public static Thing mapToConditionThing(Map<String, Object> amap){
		Thing condition = new Thing("xworker.dataObject.query.Condition");
		condition.initDefaultValue();
		condition.getAttributes().putAll(amap);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> childs = (List<Map<String, Object>>) amap.get("childs");
		if(childs != null){
			for(Map<String, Object> child : childs){
				condition.addChild(mapToConditionThing(child));
			}
		}
		
		return condition;
	}
	
	/**
	 * 根据关键字查找记录，如果存在不插入返回已有对象，否则插入并返回插入的数据对象。
	 * 
	 * @param dataObject 数据对象
	 * @param keyFields 关键字列表
	 * @param actionContext 变上下文
	 * @return 数据对象
	 */
	public static DataObject createIfNotExists(DataObject dataObject, String[] keyFields, ActionContext actionContext){
		Thing condition = createConditionThing("", "");
		for(String key : keyFields){
			condition.addChild(createConditionThing(key, key));
		}
		
		Thing desc = dataObject.getMetadata().getDescriptor();
		List<DataObject> datas = query(desc, condition, dataObject, actionContext);
		if(datas != null && datas.size() > 0){
			return datas.get(0);
		}else{
			return dataObject.create(actionContext);
		}
	}
	
	/**
	 * 如果数据对象不存在则创建，否则更新，在更新时忽略值为null的属性，返回新创建的或已有的数据对象。
	 * 
	 * @param dataObject 数据对象
	 * @param keyFields 关键字的名称列表
	 * @param actionContext 变量上下文
	 * @return 返回新创建的或已有的数据对象
	 */
	public static DataObject createOrUpdate(DataObject dataObject, String[] keyFields, ActionContext actionContext) {
		Thing condition = createConditionThing("", "");
		for(String key : keyFields){
			condition.addChild(createConditionThing(key, key));
		}
		
		Thing desc = dataObject.getMetadata().getDescriptor();
		List<DataObject> datas = query(desc, condition, dataObject, actionContext);
		if(datas != null && datas.size() > 0){
			DataObject obj = datas.get(0);
			for(Thing attr : obj.getMetadata().getAttributes()) {
				boolean isKey = false;				
				String name = attr.getMetadata().getName();
				Object value = dataObject.get(name);
				if(value  == null) {
					continue;
				}
				
				for(String key : keyFields) {
					if(key.equals(name)) {
						isKey = true;
						break;
					}
				}
				if(!isKey) {
					obj.put(name, value);
				}
			}
			
			obj.update(actionContext);
			return obj;
		}else{
			return dataObject.create(actionContext);
		}
	}
	
	/**
	 * 创建一个查询配置事物。
	 * 
	 * @param attrName 属性名
	 * @param dataName 数据名
	 * @param operator 操作符
	 * @param join 是否是join
	 * @return 查询配置
	 */
	public static Thing createConditionThing(String attrName, String dataName, byte operator, String join){
		Thing condition = new Thing("xworker.dataObject.query.Condition");
		condition.initDefaultValue();
		condition.put("name", attrName);
		condition.put("attributeName", attrName);
		condition.put("dataName", dataName);
		condition.put("operator", String.valueOf(operator));
		if(join != null) {
			condition.put("join", join);
		}
		
		return condition;
	}
	
	/**
	 * 创建一个查询配置事物，连接方式是and。
	 */
	public static Thing createConditionThing(String attrName, String dataName, byte operator){
		return createConditionThing(attrName, dataName, operator, UtilCondition.AND);
	}
	
	/**
	 * 创建一个查询配置事物，连接方式是and，匹配方式是eq。
	 */
	public static Thing createConditionThing(String attrName, String dataName){
		return createConditionThing(attrName, dataName, UtilCondition.eq, UtilCondition.AND);
	}
	
	/**
	 * 查询数据对象。
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> query(Thing dataObject, Thing conditionConfig, Map<String, Object> conditionData, PageInfo pageInfo, ActionContext actionContext){
		return (List<DataObject>) dataObject.doAction("query", actionContext, UtilMap.toMap(
				new Object[]{"conditionConfig", conditionConfig, "conditionData", conditionData, "pageInfo", pageInfo}));
	}
	
	/**
	 * 查询数据对象。
	 */
	public static List<DataObject> query(Thing dataObject, Thing conditionConfig, Map<String, Object> conditionData, ActionContext actionContext){
		return query(dataObject, conditionConfig, conditionData, null, actionContext);
	}
	
	/**
	 * 查询数据对象，使用Map&lt;String, Object&gt;生成查询条件和查询的参数值。
	 * 其中key为op_开头的为条件的操作类型，是UtilCondition的常量。
	 */
	public static List<DataObject> query(String dataObjectPath, Map<String, Object> conditions, ActionContext actionContext){
		if(conditions == null){
			conditions = Collections.emptyMap();
		}
		
		Thing dataObject = World.getInstance().getThing(dataObjectPath);
		Thing condition = new Thing("xworker.dataObject.query.Condition");
		
		for(String key : conditions.keySet()){
			if(key.startsWith("op_")){
				continue;
			}
			
			byte op = UtilData.getByte(conditions.get("op_" + key), UtilCondition.eq);
			Thing cdt = createConditionThing(key, key, op);
			Thing attr = null;
			for(Thing attribute : dataObject.getChilds("attribute")) {
				if(attribute.getMetadata().getName().equals(key)) {
					attr = attribute;
					break;
				}
			}
			if(attr != null) {
				cdt.put("sqlColumn", attr.get("fieldName"));
			}
			condition.addChild(cdt);
		}
		
		return query(dataObject, condition, conditions, actionContext);
	}
	
	/**
	 * 从一组DataObject中按照查询条件过滤数据，如果有分页也进行分页处理。
	 */
	public static List<DataObject> query(List<DataObject> dataObjects, ActionContext actionContext){
		List<DataObject> matchedDatas = null;
		
		//过滤数据
		if(actionContext.get("conditionConfig") == null){
			matchedDatas = dataObjects;
		}else{
			matchedDatas = new ArrayList<DataObject>();
			Thing conditionConfig = actionContext.getObject("conditionConfig");
			Object conditionData = actionContext.get("conditionData");
			for(DataObject dobj : dataObjects){
                Boolean matched = (Boolean) conditionConfig.doAction("isMatch", actionContext, "condition",conditionData, "data", dobj );        
                if(matched){
                    matchedDatas.add((DataObject) dobj);
                }        
            }
		}
		
		//分页
		return doPage(matchedDatas, actionContext);
	}
	
	/**
	 * 执行分页操作如果actionContext.get("pageInfo")不为null的话。
	 */
	public static List<DataObject> doPage(List<DataObject> dataObjects, ActionContext actionContext){
		if(actionContext.get("pageInfo") != null){
        	final PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
            //是否排序
            if(pageInfo.getSort() != null &&  !"".equals(pageInfo.getSort())){
            	Collections.sort(dataObjects, new Comparator<Object>(){
					@Override
					public int compare(Object o1, Object o2) {
						DataObject a = (DataObject) o1;
						DataObject b = (DataObject) o2;
						
						String av = (a == null ? null : a.getString((String) pageInfo.getSort()));
	                    String bv = (b == null ? null : b.getString((String) pageInfo.getSort()));
	                    if(av == null && bv == null){
	                        return 0;
	                    }else if(av == null && bv != null){
	                        return  "DESC".equals(pageInfo.getDir()) ? 1 : -1;
	                    }else if(av != null && bv == null){
	                        return "DESC".equals(pageInfo.getDir()) ? -1 : 1;
	                    }else{
	                        return "DESC".equals(pageInfo.getDir()) ? -av.compareTo(bv) : av.compareTo(bv);
	                    }            
					}
            	});
            
            }
            pageInfo.setTotalCount( dataObjects.size());
            if(pageInfo.getLimit() > 0){
                if(pageInfo.getStart() > dataObjects.size()){
                    pageInfo.setStart(dataObjects.size());
                }
                int toIndex = (int) (pageInfo.getStart() + pageInfo.getLimit());
                if(toIndex > dataObjects.size()){ 
                    toIndex = dataObjects.size();
                }
                int startIndex = (int) pageInfo.getStart();
                if(startIndex < 0){
                    startIndex = 0;
                }    
                pageInfo.setDatas(dataObjects.subList((int) pageInfo.getStart(), toIndex));
            }else{
                pageInfo.setDatas(dataObjects);
            }
            return pageInfo.getDatas();
        }else{
            if(actionContext.get("pageInfo") != null){
            	PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
                pageInfo.setTotalCount(dataObjects.size());
                pageInfo.setDatas(dataObjects);
            }
            return dataObjects;
        }
	}
	
	/**
	 * 批量删除数据。
	 */
	public static int deleteBatch(String dataObjectPath, Map<String, Object> conditions, ActionContext actionContext){
		if(conditions == null){
			conditions = Collections.emptyMap();
		}
		
		Thing dataObject = World.getInstance().getThing(dataObjectPath);
		Thing condition = new Thing("xworker.dataObject.query.Condition");
		
		for(String key : conditions.keySet()){
			if(key.startsWith("op_")){
				continue;
			}
			
			byte op = UtilData.getByte(conditions.get("op_" + key), UtilCondition.eq);
			condition.addChild(createConditionThing(key, key, op));
		}
		
		return (Integer) dataObject.doAction("deleteBatch", actionContext, UtilMap.toMap("conditionConfig", condition, "conditionData", conditions));
	}
	
	/**
	 * 删除数据。
	 */
	public static int delete(String dataObjectPath, Map<String, Object> conditions, ActionContext actionContext){
		if(conditions == null){
			conditions = Collections.emptyMap();
		}
		
		Thing dataObject = World.getInstance().getThing(dataObjectPath);
		Thing condition = new Thing("xworker.dataObject.query.Condition");
		
		for(String key : conditions.keySet()){
			if(key.startsWith("op_")){
				continue;
			}
			
			byte op = UtilData.getByte(conditions.get("op_" + key), UtilCondition.eq);
			condition.addChild(createConditionThing(key, key, op));
		}
		
		return (Integer) dataObject.doAction("delete", actionContext, UtilMap.toMap("conditionConfig", condition, "conditionData", conditions));
	}
	
	/**
	 * 使用标识读取一个数据对象。
	 */
	public static DataObject load(Thing dataObject, Object id, ActionContext actionContext){
		DataObject theData = new DataObject(dataObject);
		Thing[] keys = theData.getMetadata().getKeys();
		if(keys != null && keys.length > 0){
			theData.put(keys[0].getString("name"), id);
			return (DataObject) dataObject.doAction("load", actionContext, UtilMap.toMap(new Object[]{"theData", theData}));
		}else{
			throw new ActionException("DataObject has no keys defined, path=" + dataObject.getMetadata().getPath());
		}
	}
	
	public static DataObject load(String dataObjectPath, Object id, ActionContext actionContext){
		Thing dataObject = World.getInstance().getThing(dataObjectPath);
		if(dataObject != null){
			return load(dataObject, id, actionContext);
		}else{
			throw new ActionException("Load dataObject： thing not exists, path=" + dataObjectPath);
		}	
	}
	
	/**
	 * 相当于dataObject.toString()，返回数据对象的labelField设定的字段的值的toString()。
	 */
	public static String getDisplayString(DataObject dataObject) {
		if(dataObject == null) {
			return "";
		}
		
		Thing config = dataObject.getMetadata().getDescriptor();
		String labelField = config.getStringBlankAsNull("labelField");
        if(labelField == null || labelField == ""){
            labelField = dataObject.getString("displayName");
        }
        String text = "no label field";
        if(labelField != null && labelField != ""){
            text = dataObject.getString(labelField);
            if(text == null || text == ""){
                text = "";
            }else{
                text = String.valueOf(text);
            }
        }
        
        return text;
	}
	
	public static Object getValue(Object value, Thing attribute){
		String type = attribute.getString("type");
		if(type == null || "".equals(type)){
			type = "string";
		}

		if("string".equals(type)){
			return UtilData.getString(value, null);
		}
		
		//空的字符串当做null处理，不再使用其他默认值，2013-05-03
		if(value == null || "".equals(value)){
			return null;
		}
		
		if("byte".equals(type)){
			return UtilData.getByte(value, (byte) 0);
		}else if("char".equals(type)){
			return UtilData.getChar(value, (char) 0);
		}else if("short".equals(type)){
			return UtilData.getShort(value, (short) 0);
		}else if("int".equals(type)){
			return (int) UtilData.getLong(value, 0);
		}else if("long".equals(type)){
			return UtilData.getLong(value, 0);
		}else if("byte[]".equals(type)){
			return UtilData.getBytes(value, null);
		}else if("float".equals(type)){
			return UtilData.getFloat(value, 0);
		}else if("double".equals(type)){
			return UtilData.getDouble(value, 0);
		}else if("boolean".equals(type)){
			return UtilData.getBoolean(value, false);
		}else if("date".equals(type)){
			String pattern = attribute.getString("editPattern");
			if(pattern == null || "".equals(pattern)){
				pattern = "yyyy-MM-dd";
			}
			
			Date date = UtilData.getDate(value, null, pattern);
			if(date != null){
				return new java.sql.Date(date.getTime());
			}else{				
				return  null;
			}
		}else if("datetime".equals(type)){
			String pattern = attribute.getString("editPattern");
			if(pattern == null || "".equals(pattern)){
				pattern = "yyyy-MM-dd HH:mm:ss";
			}
			
			Date date = UtilData.getDate(value, null, pattern);
			if(date != null){
				return new java.sql.Timestamp(date.getTime());
			}else{
				return null;
			}
		}else if("time".equals(type)){
			String pattern = attribute.getString("editPattern");
			if(pattern == null || "".equals(pattern)){
				pattern = "HH:mm:ss";
			}
			
			Date date = UtilData.getDate(value, null, pattern);
			if(date != null){
				return new java.sql.Time(date.getTime());
			}else{
				return null;
			}
		}else{
			return UtilData.getString(value, null);
			//throw new XWorkerException("not implemented type " + type);
		}
	}
	
	/**
	 * 根据属性的类型定义返回具体类型的值。
	 */
	public static Object getAttributeValue(Object value, String type){
		if(type == null || "".equals(type)){
			type = "string";
		}
		
		if("string".equals(type)){
			return UtilData.getString(value, null);
		}else if("byte".equals(type)){
			return UtilData.getByte(value, (byte) 0);
		}else if("char".equals(type)){
			return UtilData.getChar(value, (char) 0);
		}else if("short".equals(type)){
			return UtilData.getShort(value, (short) 0);
		}else if("int".equals(type)){
			return (int) UtilData.getLong(value, 0);
		}else if("long".equals(type)){
			return UtilData.getLong(value, 0);
		}else if("byte[]".equals(type)){
			return UtilData.getBytes(value, null);
		}else if("float".equals(type)){
			return UtilData.getFloat(value, 0);
		}else if("double".equals(type)){
			return UtilData.getDouble(value, 0);
		}else if("boolean".equals(type)){
			return UtilData.getBoolean(value, false);
		}else if("date".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				return new java.sql.Date(date.getTime());
			}else{
				return  null;
			}
		}else if("datetime".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				return new java.sql.Timestamp(date.getTime());
			}else{
				return null;
			}
		}else if("time".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				return new java.sql.Time(date.getTime());
			}else{
				return null;
			}
		}else{
			return UtilData.getString(value, null);
			//throw new XWorkerException("not implemented type " + type);
		}
	}
	
	public static Object getDefaultValue(String type, String defaultValue, ActionContext actionContext){
		if(defaultValue == null){
			return null;
		}
		
		if("date".equals(type) || "datetime".equals(type) || "time".equals(type)){
            //日期的默认值
            defaultValue = defaultValue.toLowerCase();
            String dateStr = defaultValue;
            String numberStr = "";
            int index = defaultValue.indexOf("+");
            if(index != -1){
                dateStr = defaultValue.substring(0, index).trim();
                numberStr = defaultValue.substring(index, defaultValue.length()).trim();
            }else{
                index = defaultValue.indexOf("-");
                if(index != -1){
                    dateStr = defaultValue.substring(0, index).trim();
                    numberStr = defaultValue.substring(index, defaultValue.length()).trim();
                }
            }
     
            Date date = null;
            if("now".equals(dateStr) || "sysdate".equals(dateStr)){
                date = new Date();
            }else if("tomorrow".equals(dateStr)){
                date = UtilDate.getTomorrow();
            }else if("yesterday".equals(dateStr)){
                date = UtilDate.getYesterday();
            }else if("weekstart".equals(dateStr)){
                date = UtilDate.getWeekStart();
            }else if("weekend".equals(dateStr)){
                date = UtilDate.getWeekEnd();
            }else if("monthstart".equals(dateStr)){
                date = UtilDate.getMonthStart();
            }else if("monthend".equals(dateStr)){
                date = UtilDate.getMonthEnd();
            }else if("yearstart".equals(dateStr)){
                date = UtilDate.getYearStart();
            }else if("yearend".equals(dateStr)){
                date = UtilDate.getYearEnd();
            }else{
                date = new Date();
                try{
                    double d = (Double) OgnlUtil.getValue(dateStr, actionContext);
                    date = UtilDate.getDate(date, d);
                }catch(Exception e){
                }
            }
            if(numberStr != ""){
                try{
                    double d = (Double) OgnlUtil.getValue(numberStr, actionContext);
                    //log.info("d=" + d);
                    date = UtilDate.getDate(date, d);
                }catch(Exception e){
                    //log.info("error", e);
                }
            }
            return date;
        }else{
            return defaultValue;
        }
	}

	public static List<DataObject> dbResultsToDataObjects(Thing dataObject, ResultSet rs) throws SQLException {
		return dbResultsToDataObjects(dataObject, dataObject.getChilds("attribute"), rs);
	}
	/**
	 * 数据库结果集到数据对象的转换。
	 */
	public static List<DataObject> dbResultsToDataObjects(Thing dataObject, List<Thing> attributes, ResultSet rs) throws SQLException {
		List<DataObject> ds = new ArrayList<>();
		UserTask userTask = DataObject.getUserTask(dataObject, null);
		
		DataObject.beginThreadCache();
		try{
			 long start = System.currentTimeMillis();
			while (rs.next()) {
				// 构造对象
				DataObject data = new DataObject(dataObject);
				data.setInited(false);
	
				// 设置属性值
				for (int i = 0; i < attributes.size(); i++) {
					if (!attributes.get(i).getBoolean("dataField")) {
						continue;
					}
					
					try{
                        data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
                    }catch(SQLException e){
                        Executor.error(TAG, "get result value error: " + e.getMessage() + ": " + attributes.get(i));
                        throw e;
                    }
				}
	
				data.setInited(true);
				ds.add(data);
				// log.info("data=" + data);
				
				if(userTask != null && System.currentTimeMillis() - start > 1000){
					UserTaskManager.setUserTaskLabelDetail(userTask, "Data has get " + ds.size() + " rows", null);
				}
			}
		}finally{
			DataObject.finishThreadCache();
		}

		return ds;
	}
	
	/**
	 * 用指定的字段对一个DataObject列表排序。
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> sort(List<DataObject> datas, String field, boolean asc){
	    Thing sort = new Thing("xworker.lang.actions.data.Sort");
        sort.put("listName", "datas");
        sort.put("sortField", field);
        sort.put("dir", asc ? "ASC" : "DESC");
        
        ActionContext ac = new ActionContext();
        ac.put("datas", datas);
        return (List<DataObject>) sort.doAction("run", ac);
	}
	
	/**
	 * 把一个数据对象数据转化为csv。
	 */
	public static String toCsv(DataObject data, boolean hasHeader) throws IOException{
		if(data == null){
			return null;
		}
		
		List<DataObject> datas = new ArrayList<DataObject>();
		datas.add(data);
		
		return toCsv(datas, hasHeader);
	}
	
	/**
	 * 把一列同结构的数据对象转化为csv格式的字符串。
	 * 
	 * @param datas 要转化的数据对象列表
	 * @param hasHeader 是否包含头
	 * 
	 * @return 如果数据不为空返回csv字符串，否则返回null
	 */
	public static String toCsv(List<DataObject> datas, boolean hasHeader) throws IOException{
		if(datas == null || datas.size() == 0){
			return null;
		}
		
		//创建csv
		OutputStream output = new ByteArrayOutputStream();
		
		CsvWriter writer = new CsvWriter(output, ',', StandardCharsets.UTF_8);
		Thing dataObject = datas.get(0).getMetadata().getDescriptor();
		
		//写入标题
		if(hasHeader){
			for(Thing attr : dataObject.getChilds("attribute")){
				String label = attr.getMetadata().getLabel();
				writer.write(label);
			}
		}
		
		//写入行数据
		for(DataObject data : datas){
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			writer.endRecord();
			for(Thing attr : dataObject.getChilds("attribute")){
				String type = attr.getString("type");
				String svalue = "";
				if("date".equals(type) || "datetime".equals(type)){
					Date value = data.getDate(attr.getMetadata().getName());
					if(value != null){
						svalue = sf.format(value);
					}
				}else{
					svalue = data.getString(attr.getMetadata().getName());
				}
				if(svalue == null){
					svalue = "";
				}

				writer.write(svalue);
			}

			writer.flush();
		}
		
		writer.flush();
		writer.close();
		
		return output.toString();
	}
	
	@SuppressWarnings({"unchecked"})
	public static List<DataObject> dbQueryPage(Thing dataObjectSelf, List<Thing> attributes, Connection con, QueryConfig queryConfig, PageInfo pageInfo, int pageType, String countSql, String querySql, ActionContext actionContext) throws Exception{
        return (List<DataObject>) doDbQueryPage(false, dataObjectSelf, attributes, con, queryConfig, pageInfo, pageType, countSql, querySql, actionContext);
    }

	public static Object doDbQueryPage(Boolean createIterator, Thing dataObjectSelf, List<Thing> attributes, Connection con, QueryConfig queryConfig, PageInfo pageInfo, int pageType, String countSql, String querySql, ActionContext actionContext) throws Exception{
		UserTask userTask = DataObject.getUserTask(dataObjectSelf, actionContext);

		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//查询总数
			if(countSql != null && !"".equals(countSql)){
				if(dataObjectSelf.getBoolean("showSql")){
					Executor.info(TAG, countSql);
				}

				UserTaskManager.setUserTaskLabelDetail(userTask, "Query total count", countSql);
				pst = con.prepareStatement(countSql);
				UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared, executing......", null);
				UserTaskManager.setUserTaskData(userTask, "pst", pst);

				//设置查询参数
				DbDataObject.setStatementParams(pst, 1, queryConfig, attributes);
				rs = pst.executeQuery();
				rs.next();
				pageInfo.setTotalCount(rs.getInt(1));
				rs.close();
				pst.close();
			}else{
				if(dataObjectSelf.getBoolean("showSql")){
					Executor.info(TAG, "No count sql, use page count totalCount");
				}

				//不知道总数的情况下，设置比当前总数大一
				//pageInfo.setTotalCount((pageInfo.getPage() + 1) * pageInfo.getLimit());
			}

			//分页查询
			String sql = querySql;
			if(dataObjectSelf.getBoolean("showSql")){
				Executor.info(TAG, sql);
			}
			pst = con.prepareStatement(sql);
			int index = DbDataObject.setStatementParams(pst, 1, queryConfig, attributes);
			if(pageInfo != null && pageInfo.getLimit() != 0){
				long start = pageInfo.getStart() + 1;

				switch(pageType){
					case DataObjectUtil.PAGE_BETWEEN:
						pst.setLong(index, start);
						pst.setLong(index + 1, start + pageInfo.getLimit() - 1);
						break;
					case DataObjectUtil.PAGE_LIMIT:
						pst.setLong(index, start-1);
						pst.setLong(index + 1, pageInfo.getLimit());
						break;
					default:
						throw new ActionException("Unknown page type " + pageType + ", dataObject=" + dataObjectSelf.getMetadata().getPath());
				}
			}

			//执行sql
			UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared, executing......", null);
			UserTaskManager.setUserTaskData(userTask, "pst", pst);
			rs = pst.executeQuery();

			//读取记录
			if(createIterator != null && createIterator){
				return new DbDataObjectIterator(dataObjectSelf, attributes, pageInfo, con, pst, rs, actionContext);
			}else {
				List<DataObject> ds = DataObjectUtil.dbResultsToDataObjects(dataObjectSelf, rs);
				pageInfo.setDatas(ds);

				return ds;
			}
		}finally{
			if(rs != null){
				rs.close();
			}
			if(pst != null){
				pst.close();
			}
		}
	}
}