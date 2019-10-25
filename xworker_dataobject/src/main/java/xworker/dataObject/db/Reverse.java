package xworker.dataObject.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.XMetaException;

import xworker.dataObject.utils.DbUtil;

/**
 * 逆向工程，给定连接池，把所有的表都转化成数据对象。
 * 
 * @author Administrator
 *
 */
public class Reverse {
	private static Logger logger = LoggerFactory.getLogger(Reverse.class);
	
	public static Thing createDataObject(Thing dataSource, String tableName,  ActionContext actionContext){
		Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
		Thing thing = null;
		try{
			DatabaseMetaData metadata = con.getMetaData();
			ResultSet rs = metadata.getTables(null, null, tableName, new String[]{"TABLE"});
			if(rs.next()){
				String type = rs.getString("TABLE_TYPE");
				if("TABLE".equals(type)){
					

					//创建新事物
					thing = new Thing("xworker.dataObject.db.DbDataObject");
					thing.put("dataSource", dataSource.getMetadata().getPath());
					thing.put("name", tableName);
					thing.put("label", tableName);
					thing.put("tableName", tableName);
					thing.put("gridEditable", "true");
					thing.put("paging", "true");
					thing.put("pageSize", "100");
					//thing.put("showSql", "true");
					
					String cat = rs.getString("TABLE_CAT");
					String schem = rs.getString("TABLE_SCHEM");					
					ResultSet crs = metadata.getColumns(cat, schem, tableName, null);										
					while(crs.next()){
						String columnName = crs.getString("COLUMN_NAME");
						//String typeName = crs.getString("TYPE_NAME");
						int columnType = crs.getInt("DATA_TYPE");
						String size = crs.getString("COLUMN_SIZE");
						//String nullAble = crs.getString("NULLABLE");
						//String inc = null;
						try{
						//	inc = crs.getString("IS_AUTOINCREMENT");
						}catch(Exception e){							
						}
						
						Thing column = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
						column.put("name", columnName);
						column.put("fieldName", columnName);
						column.put("type",  DbUtil.getXWorkerType(crs.getInt("DATA_TYPE"), crs.getInt("DECIMAL_DIGITS")));//getDataObjectType(columnType));
						column.put("length", size);
						column.put("precision",  crs.getString("DECIMAL_DIGITS"));
						column.put("inputtype", getDataObjectEditType(columnType, Integer.parseInt(size)));
						column.put("queryEditor", "false");
						thing.addChild(column);
					}
					crs.close();
					
					//获取主键
					ResultSet prs = metadata.getPrimaryKeys(cat, schem, tableName);
					while(prs.next()){
						String columnName = prs.getString("COLUMN_NAME");
						for(Thing child : thing.getChilds()){
							String name = child.getMetadata().getName();
							if(name.toUpperCase().equals(columnName.toUpperCase())){
							//if(child.getMetadata().getName().equals(columnName)){
								child.put("key", "true");
								child.put("editable", "false");
							}
						}
					}					
				}								
			}
			rs.close();
		}catch(Exception e){
			throw new XMetaException("Ceate dataobject error", e, actionContext);
		}finally{
			DbUtil.close(con);
		}
		
		return thing;
	}
	
	public static void reverse(Thing dataSource, String thingManagerName, String categroy, ActionContext actionContext){
		Connection con = (Connection) dataSource.doAction("getConnection", actionContext);
		try{
			DatabaseMetaData metadata = con.getMetaData();
			ResultSet rs = metadata.getTables(null, null, null, new String[]{});
			while(rs.next()){
				String type = rs.getString("TABLE_TYPE");
				if("TABLE".equals(type)){
					String tableName = rs.getString("TABLE_NAME");
					
					//检查数据对象是否已经存在
					String thingPath = categroy + "." + tableName;					
					Thing thing = World.getInstance().getThing(thingPath);
					if(thing != null){
						logger.info("DataObject not created, already exists : " + thingPath);
						continue;						
					}

					//创建新事物
					thing = new Thing("xworker.dataObject.db.DbDataObject");
					thing.put("dataSource", dataSource.getMetadata().getPath());
					thing.put("name", tableName);
					thing.put("label", tableName);
					thing.put("tableName", tableName);
					
					String cat = rs.getString("TABLE_CAT");
					String schem = rs.getString("TABLE_SCHEM");					
					ResultSet crs = metadata.getColumns(cat, schem, tableName, null);										
					while(crs.next()){
						String columnName = crs.getString("COLUMN_NAME");
						//String typeName = crs.getString("TYPE_NAME");
						int columnType = crs.getInt("DATA_TYPE");
						String size = crs.getString("COLUMN_SIZE");
						//String nullAble = crs.getString("NULLABLE");
						//String inc = null;
						try{
						//	inc = crs.getString("IS_AUTOINCREMENT");
						}catch(Exception e){							
						}
						
						Thing column = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
						column.put("name", columnName);
						column.put("fieldName", columnName);
						column.put("type",  DbUtil.getXWorkerType(crs.getInt("DATA_TYPE"), crs.getInt("DECIMAL_DIGITS")));//getDataObjectType(columnType));
						column.put("length", size);
						column.put("precision",  crs.getString("DECIMAL_DIGITS"));
						column.put("inputtype", getDataObjectEditType(columnType, Integer.parseInt(size)));
						thing.addChild(column);
					}
					crs.close();
					
					//获取主键
					ResultSet prs = metadata.getPrimaryKeys(cat, schem, tableName);
					while(prs.next()){
						String columnName = prs.getString("COLUMN_NAME");
						for(Thing child : thing.getChilds()){
							if(child.getMetadata().getName().equals(columnName)){
								child.put("key", "true");
								child.put("editable", "false");
							}
						}
					}
					
					//保存事物
					thing.saveAs(thingManagerName, thingPath);
					logger.info("DataObject saved : " + thingPath);
				}				
				/*
				System.out.println("TABLE_CAT=" + rs.getString("TABLE_CAT"));
				System.out.println("TABLE_SCHEM=" + rs.getString("TABLE_SCHEM"));
				System.out.println("TABLE_NAME=" + rs.getString("TABLE_NAME"));
				System.out.println("TABLE_TYPE=" + rs.getString("TABLE_TYPE"));
				System.out.println("REMARKS=" + rs.getString("REMARKS"));
				System.out.println("TYPE_CAT=" + rs.getString("TYPE_CAT"));
				System.out.println("TYPE_SCHEM=" + rs.getString("TYPE_SCHEM"));
				System.out.println("TYPE_NAME=" + rs.getString("TYPE_NAME"));
				System.out.println("SELF_REFERENCING_COL_NAME=" + rs.getString("SELF_REFERENCING_COL_NAME"));
				System.out.println("REF_GENERATION=" + rs.getString("REF_GENERATION"));
				*/
			}
			rs.close();
		}catch(Exception e){
			throw new XMetaException("Reverse databsae error", e, actionContext);
		}finally{
			DbUtil.close(con);
		}
	}
	
	public static String getDataObjectType(int javaType){
		switch(javaType){
		case Types.ARRAY:
			return "string";
		case Types.BIGINT:
			return "long";
		case Types.BINARY:
			return "byte[]";
		case Types.BIT:
			return "byte";
		case Types.BLOB:
			return "byte[]";
		case Types.BOOLEAN:
			return "boolean";
		case Types.CHAR:
			return "short";
		case Types.CLOB:
			return "byte[]";
		case Types.DATALINK:
			return "string";
		case Types.DATE:
			return "datetime";
		case Types.DECIMAL:
			return "double";
		case Types.DISTINCT :
			return "string";
		case Types.DOUBLE:
			return "double";
		case Types.FLOAT:
			return "flat";
		case Types.INTEGER:
			return "long";
		case Types.JAVA_OBJECT:
			return "byte[]";
		case Types.LONGNVARCHAR:
			return "string";
		case Types.LONGVARBINARY:
			return "string";
		case Types.LONGVARCHAR:
			return "string";
		case Types.NCHAR:
			return "string";
		case Types.NCLOB:
			return "byte[]";
		case Types.NULL:
			return "string";
		case Types.NUMERIC:
			return "double";
		case Types.NVARCHAR:
			return "string";
		case Types.OTHER:
			return "byte[]";
		case Types.REAL:
			return "short";
		case Types.REF:
			return "string";
		case Types.ROWID:
			return "string";
		case Types.SMALLINT:
			return "short";
		case Types.SQLXML:
			return "string";
		case Types.STRUCT:
			return "string";
		case Types.TIME:
			return "time";
		case Types.TIMESTAMP:
			return "datetime";
		case Types.TINYINT:
			return "short";
		case Types.VARBINARY:
			return "string";
		case Types.VARCHAR:
			return "string";		
		}
		
		return "string";
	}
	
	public static String getDataObjectEditType(int javaType, int size){
		switch(javaType){
		case Types.BOOLEAN:
			return "truefalse";
		case Types.DATE:
			return "datePick";
		case Types.LONGNVARCHAR:
			return "textarea";
		case Types.LONGVARBINARY:
			return "textarea";
		case Types.LONGVARCHAR:
			return "textarea";
		case Types.SQLXML:
			return "textarea";
		case Types.TIME:
			return "time";
		case Types.TIMESTAMP:
			return "dateTime";
		}
		
		if(size > 256){
			return "textarea";
		}else{
			return "text";
		}
	}
}
