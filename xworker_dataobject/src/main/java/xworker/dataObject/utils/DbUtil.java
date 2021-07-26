/*****************************************************************************
 Copyright 2007-2013 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package xworker.dataObject.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

/**
 * 数据库工具类。
 * 
 * @author zhangyuxiang
 *
 */
public class DbUtil {
	private static final String TAG = DbUtil.class.getName();			
	
	public static void close(PreparedStatement pst){
		if(pst != null){
			try{
				pst.close();
			}catch(Exception ignored){
			}
		}
	}
	
	public static void close(ResultSet rs){
		if(rs != null){
			try{
				rs.close();
			}catch(Exception ignored){
			}
		}
	}
	
	public static void close(Connection con){
		if(con != null){
			try{
				con.close();
			}catch(Exception ignored){
			}
		}
	}
	
	/**
	 * 返回条件表达式sql.
	 */
	public static String getConditionSql(Object condition, ActionContext actionContext, Object datas, Object cds){
		if(actionContext == null){
			actionContext = new ActionContext();
		}
		Map<String, Object> params = new HashMap<>();
		params.put("datas", datas);
		params.put("cds", cds);
		
		Action action = World.getInstance().getAction("xworker.dataObject.query.Condition/@actions/@toSql");
		Bindings bindings = actionContext.push(null);
		bindings.setCaller(condition, "getConditionSql");
		bindings.put("self", condition);
		
		try{
			return action.run(actionContext, params, false);
		}finally{
			actionContext.pop();
			//log.info("run action time " + actionThing.getMetadata().getPath() + " : " + (System.currentTimeMillis() - start));
		}
	}
	
	/**
	 * 从结果集中读取数据。
	 */
	public static Object getValue(ResultSet rs, Thing descriptor) throws SQLException{
		String name = descriptor.getString("fieldName");
		if(name != null) {
			int index = name.toLowerCase().lastIndexOf(" as ");
			if (index != -1) {
				name = name.substring(index + 2).trim();
			}
		}
		String type = descriptor.getString("type");
		Object object = null;
		try{
			object = getValue(rs, name, type);
		}catch(Exception e){
			Executor.warn(TAG, "get value error, name=" + name + ", type=" + type + ", path=" + descriptor.getMetadata().getPath(), e);			
		}
		
		if("attribute".equals(descriptor.getThingName())){
			//基本字段
			return object;
		}else{
			//事物字段
			DataObject dataObject = new DataObject(World.getInstance().getThing(descriptor.getString("dataObjectPath")));
			dataObject.setInited(false);
			dataObject.put(descriptor.getString("refAttributeName"), object);
			return dataObject;
		}
	}
	
	/**
	 * 根据java.sql.Types获取具体的对象的值。
	 * 
	 * @param rs 结果集
	 * @param columnIndex 索引
	 * @param sqlType sql类型
	 * @return 值
	 * @throws SQLException SQL异常 
	 */
	public static Object getValue(ResultSet rs, int columnIndex, int sqlType) throws SQLException{
		switch(sqlType){
		case Types.ARRAY:
			return rs.getArray(columnIndex);
		case Types.BIGINT:
			return rs.getBigDecimal(columnIndex).toBigInteger();
		case Types.BINARY:
			case Types.VARBINARY:
				return rs.getBinaryStream(columnIndex);
		case Types.BIT:
			case Types.SMALLINT:
			case Types.TINYINT:
				return rs.getByte(columnIndex);
		case Types.BLOB:
			return rs.getBlob(columnIndex);
		case Types.BOOLEAN:
			return rs.getBoolean(columnIndex);
		case Types.CHAR:
			case Types.REAL:
				return rs.getShort(columnIndex);
		case Types.CLOB:
			return rs.getClob(columnIndex);
		case Types.DATALINK:
			case Types.LONGNVARCHAR:
			case Types.LONGVARBINARY:
			case Types.LONGVARCHAR:
			case Types.NVARCHAR:
			case Types.REF:
			case Types.ROWID:
			case Types.SQLXML:
			case Types.STRUCT:
			case Types.VARCHAR:
				return rs.getString(columnIndex);
		case Types.DATE:
			return rs.getDate(columnIndex);
		case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
				return rs.getDouble(columnIndex);
				case Types.FLOAT:
			return rs.getFloat(columnIndex);
		case Types.INTEGER:
			return rs.getInt(columnIndex);
		case Types.JAVA_OBJECT:
			case Types.OTHER:
				return rs.getObject(columnIndex);
			case Types.NCHAR:
			return rs.getStatement();
		case Types.NCLOB:
			return rs.getNClob(columnIndex);
		case Types.NULL:
			return null;
			case Types.TIME:
			return rs.getTime(columnIndex);
		case Types.TIMESTAMP:
			return rs.getTimestamp(columnIndex);
		}		
		return rs.getString(columnIndex);
	}
	
	/**
	 * 从数据库的类型转化为XWorker的类型。
	 * 
	 * @param sqlType sql类型
	 * @param scale 小数的右边位数
	 * @return 类型字符串
	 */
	public static String getXWorkerType(int sqlType, int scale){
		switch(sqlType){
		case Types.INTEGER:
			return "int";
		case Types.BIGINT:
			return "long";
		case Types.REAL:
		case Types.SMALLINT:
			return "short";
		case Types.BINARY:
			return "byte[]";
		case Types.BIT:
		case Types.TINYINT:
			return "byte";
		case Types.BOOLEAN:
			return "boolean";
		case Types.CHAR:
			return "char";
		case Types.DATE:
		case Types.TIME:
			return "date";
		case Types.TIMESTAMP:
			return "datetime";
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			if(scale <= 0){
				return "long";
			}else{
				return "double";
			}
		case Types.FLOAT:
			return "float";
		case Types.LONGNVARCHAR:
		case Types.LONGVARBINARY:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:	
		case Types.ROWID:
		case Types.SQLXML:
		case Types.VARCHAR:
			return "string";
		case Types.ARRAY:
		case Types.BLOB:
		case Types.CLOB:
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.NCLOB:
		case Types.NULL:
		case Types.OTHER:
		case Types.REF:
		case Types.STRUCT:
		case Types.VARBINARY:
			return null;
		}		
		return null;
	}
	
	/**
	 * 通过名称和类型获取结果集的值。
	 */
	public static Object getValue(ResultSet rs, String name, String type) throws SQLException{
		if("string".equals(type)){
			return rs.getString(name);
		}else if("byte".equals(type)){
			return rs.getByte(name);
		}else if("char".equals(type)){
			return rs.getShort(name);
		}else if("short".equals(type)){
			return rs.getShort(name);
		}else if("int".equals(type)){
			return rs.getInt(name);
		}else if("long".equals(type)){
			return rs.getLong(name);
		}else if("byte[]".equals(type)){
			return rs.getBytes(name);
		}else if("float".equals(type)){
			return rs.getFloat(name);
		}else if("double".equals(type)){
			return rs.getDouble(name);
		}else if("date".equals(type)){
			return rs.getDate(name);
		}else if("datetime".equals(type)){
			return rs.getTimestamp(name);
		}else if("time".equals(type)){
			return rs.getTime(name);
		}else if("boolean".equals(type)){
			return rs.getBoolean(name);
		}else{
			return rs.getString(name);
		}
	}
	
	
	/**
	 * 设置参数。
	 */
	public static void setParameter(PreparedStatement pst, int index, String key, DataObject obj) throws SQLException{
		Thing descriptor = obj.getMetadata().getDefinition(key);
		if(descriptor == null){
			throw new XMetaException("attribute " + key + " is not defined in " + obj.getMetadata().getDescriptor().getMetadata().getPath());
		}
		
		String type = descriptor.getString("type");
		Object value;
		if("attribute".equals(descriptor.getThingName())){
			value = obj.get(key);
		}else{
			value = obj.get(key);
			if(value != null){
				DataObject v = (DataObject) value;
				value = v.get(descriptor.getString("refAttributeName"));
			}
		}
		setParameterValue(pst, index, type, value);
	}
	
	/**
	 * 设置参数值。
	 * 
	 */
	public static void setParameterValue(PreparedStatement pst, int index, String type, Object value) throws SQLException{
		if(type == null || "string".equals(type) || "".equals(type)){
			pst.setString(index, UtilData.getString(value, null));			
		}else if("byte".equals(type)){
			pst.setByte(index, UtilData.getByte(value, (byte) 0));
		}else if("boolean".equals(type)){
			pst.setBoolean(index, UtilData.getBoolean(value, false));			
		}else if("char".equals(type)){
			pst.setInt(index, UtilData.getChar(value, (char) 0));
		}else if("short".equals(type)){
			pst.setShort(index, UtilData.getShort(value, (short) 0));
		}else if("int".equals(type)){
			pst.setInt(index, (int) UtilData.getLong(value, 0));
		}else if("long".equals(type)){
			pst.setLong(index, UtilData.getLong(value, 0));
		}else if("byte[]".equals(type)){
			pst.setBytes(index, UtilData.getBytes(value, null));
		}else if("float".equals(type)){
			pst.setFloat(index, UtilData.getFloat(value, 0));
		}else if("double".equals(type)){
			pst.setDouble(index, UtilData.getDouble(value, 0));
		}else if("date".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				pst.setDate(index, new java.sql.Date(date.getTime()));
			}else{
				pst.setDate(index, null);
			}
		}else if("datetime".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				pst.setTimestamp(index, new java.sql.Timestamp(date.getTime()));
			}else{
				pst.setTimestamp(index, null);
			}
		}else if("time".equals(type)){
			Date date = UtilData.getDate(value, null);
			if(date != null){
				pst.setTime(index, new java.sql.Time(date.getTime()));
			}else{
				pst.setTime(index, null);
			}
		}else if("number".equals(type)){
			Number numer = (Number) value;
			if(numer != null){
				pst.setDouble(index, numer.doubleValue());
			}else{
				pst.setDouble(index, 0);
			}
		}else{
			throw new XMetaException("not implemented type " + type);
		}
	}
}