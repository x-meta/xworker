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
package xworker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 数据库工具类。
 * 
 * @author zhangyuxiang
 *
 */
public class DbUtil {
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
	
	public static void close(Connection con, PreparedStatement pst){
		close(pst);
		close(con);		
	}
	
	public static void close(Connection con, PreparedStatement pst, ResultSet rs){
		close(rs);
		close(pst);
		close(con);
	}
	
	/**
	 * 根据java.sql.Types获取具体的对象的值。
	 * 
	 * @param rs 数据集
	 * @param columnIndex 索引
	 * @param sqlType 类型
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
	 * 通过名称和类型获取结果集的值。
	 * 
	 * @param rs 数据集
	 * @param name 字段名
	 * @param type 类型
	 * @return 值
	 * @throws SQLException SQL异常
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
}