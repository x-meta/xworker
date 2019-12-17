package xworker.util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.XMetaException;
import org.xmeta.util.UtilData;

public class DbUtils {
	static final String TAG = DbUtils.class.getName();
	
	public static void close(PreparedStatement pst){
		if(pst != null){
			try{
				pst.close();
			}catch(Exception e){				
			}
		}
	}
	
	public static void close(ResultSet rs){
		if(rs != null){
			try{
				rs.close();
			}catch(Exception e){				
			}
		}
	}
	
	public static void close(Connection con){
		if(con != null){
			try{
				con.close();
			}catch(Exception e){				
			}
		}
	}
	
	/**
	 * 把ResultSet中的数据读到Map<String, Object>中。其中字段名都会转成小写。
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public static Map<String, Object> getValues(ResultSet rs) throws SQLException{
		Map<String, Object> values = new HashMap<String, Object>();
		ResultSetMetaData meta = rs.getMetaData();
		for(int i =0 ;i<meta.getColumnCount(); i++) {
			String name = meta.getColumnName(i + 1).toLowerCase();
			int type = meta.getColumnType(i + 1);
			
			values.put(name, getValue(rs, i + 1, type));
		}
		
		return values;
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
			return rs.getBinaryStream(columnIndex);
		case Types.BIT:
			return rs.getByte(columnIndex);
		case Types.BLOB:
			return rs.getBlob(columnIndex);
		case Types.BOOLEAN:
			return rs.getBoolean(columnIndex);
		case Types.CHAR:
			return rs.getShort(columnIndex);
		case Types.CLOB:
			return rs.getClob(columnIndex);
		case Types.DATALINK:
			return rs.getString(columnIndex);
		case Types.DATE:
			return rs.getDate(columnIndex);
		case Types.DECIMAL:
			return rs.getDouble(columnIndex);
		case Types.DOUBLE:
			return rs.getDouble(columnIndex);
		case Types.FLOAT:
			return rs.getFloat(columnIndex);
		case Types.INTEGER:
			return rs.getInt(columnIndex);
		case Types.JAVA_OBJECT:
			return rs.getObject(columnIndex);
		case Types.LONGNVARCHAR:
			return rs.getString(columnIndex);
		case Types.LONGVARBINARY:
			return rs.getString(columnIndex);
		case Types.LONGVARCHAR:
			return rs.getString(columnIndex);
		case Types.NCHAR:
			return rs.getStatement();
		case Types.NCLOB:
			return rs.getNClob(columnIndex);
		case Types.NULL:
			return null;
		case Types.NUMERIC:
			return rs.getDouble(columnIndex);
		case Types.NVARCHAR:			
			return rs.getString(columnIndex);
		case Types.OTHER:
			return rs.getObject(columnIndex);
		case Types.REAL:
			return rs.getShort(columnIndex);
		case Types.REF:
			return rs.getString(columnIndex);
		case Types.ROWID:
			return rs.getString(columnIndex);
		case Types.SMALLINT:
			return rs.getByte(columnIndex);
		case Types.SQLXML:
			return rs.getString(columnIndex);
		case Types.STRUCT:
			return rs.getString(columnIndex);
		case Types.TIME:
			return rs.getTime(columnIndex);
		case Types.TIMESTAMP:
			return rs.getTimestamp(columnIndex);
		case Types.TINYINT:
			return rs.getByte(columnIndex);
		case Types.VARBINARY:
			return rs.getBinaryStream(columnIndex);
		case Types.VARCHAR:
			return rs.getString(columnIndex);
		}		
		return rs.getString(columnIndex);
	}
	
	/**
	 * 从数据库的类型转化为XWorker的类型。
	 * 
	 * @param sqlType sql类型
	 * @param scale 小数的右边位数
	 * @return
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
	 * 
	 * @param rs
	 * @param name
	 * @param type
	 * @return
	 * @throws SQLException
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
	
	public static void setParameters(PreparedStatement pst, Object[] params) throws SQLException {
		if(params == null) {
			return;
		}
		
		for(int i=0; i<params.length; i++) {
			int parameterIndex = i+1;
			Object param = params[i];
			if(param instanceof String) {
				pst.setString(parameterIndex, (String) param);
			} else if(param instanceof Array) {
				pst.setArray(parameterIndex, (Array) param);
			} else if(param instanceof Byte) {
				pst.setByte(parameterIndex, (Byte) param);
			} else if(param instanceof byte[]) {
				pst.setBytes(parameterIndex, (byte[]) param);
			} else if(param instanceof Date) {
				java.sql.Timestamp date = new java.sql.Timestamp(((Date) param).getTime());
				pst.setTimestamp(parameterIndex, date);
			} else if(param instanceof java.sql.Date) {
				pst.setDate(parameterIndex, (java.sql.Date) param);
			} else if(param instanceof Integer) {
				pst.setInt(parameterIndex, (Integer) param);
			} else if(param instanceof Float) {
				pst.setFloat(parameterIndex, (Float) param);
			} else if(param instanceof Short) {
				pst.setShort(parameterIndex, (Short) param);
			} else if(param instanceof Double) {
				pst.setDouble(parameterIndex, (Double) param);
			} else if(param instanceof Long) {
				pst.setLong(parameterIndex, (Long) param);
			} else if(param instanceof InputStream) {
				pst.setBinaryStream(parameterIndex, (InputStream) param);
			} else if(param instanceof Blob) {
				pst.setBlob(parameterIndex, (Blob) param);
			} else if(param instanceof Reader) {
				pst.setCharacterStream(parameterIndex, (Reader) param);
			} else if(param instanceof BigDecimal) {
				pst.setBigDecimal(parameterIndex, (BigDecimal) param);
			} else if(param instanceof Clob) {
				pst.setClob(parameterIndex, (Clob) param);
			} else {
				if(param != null) {
					pst.setString(parameterIndex, String.valueOf(param));
				}else {
					pst.setNull(parameterIndex, Types.VARCHAR);
				}
			}
		}
	}
		
	/**
	 * 设置参数值。
	 * 
	 * @param pst
	 * @param index
	 * @param type
	 * @param value
	 * @throws SQLException
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
