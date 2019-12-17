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
package xworker.db.jdbc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class DataSourceActions {
	private static Logger logger = LoggerFactory.getLogger(DataSourceActions.class);
		
	public static Map<String, String> dataSourceMap = new HashMap<String, String>();
	static{
		dataSourceMap.put("derby", "org.apache.derby.jdbc.ClientDataSource");
		dataSourceMap.put("mysql", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		dataSourceMap.put("oracle", "oracle.jdbc.pool.OracleDataSource");
		dataSourceMap.put("sqlserver2005", "com.microsoft.sqlserver.jdbc.SQLServerDataSource");
		dataSourceMap.put("DB2", "com.ibm.db2.jcc.DB2SimpleDataSource");
		dataSourceMap.put("DB2400", "");
		dataSourceMap.put("DB2390", "");
		dataSourceMap.put("FrontBase", "");
		dataSourceMap.put("Firebird", "org.firebirdsql.pool.FBSimpleDataSource");
		dataSourceMap.put("Informix", "com.informix.jdbcx.IfxDataSource");
		dataSourceMap.put("Ingres", "");
		dataSourceMap.put("Interbase", "");
		dataSourceMap.put("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDataSource");
		dataSourceMap.put("Mckoi", "");
		dataSourceMap.put("MySQLInnoDB", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		dataSourceMap.put("MySQLMyISAM", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		dataSourceMap.put("Oracle9", "oracle.jdbc.pool.OracleDataSource");
		dataSourceMap.put("SybaseAnywhere", "");
		dataSourceMap.put("SAPDB", "");
		dataSourceMap.put("sqlite", "org.sqlite.SQLiteDataSource");
		dataSourceMap.put("HSQL", "org.hsqldb.jdbc.JDBCDataSource");
		dataSourceMap.put("Progress", "org.postgresql.ds.PGSimpleDataSource");
		dataSourceMap.put("Pointbase", "");
	}
	
	public static Object getDataSource(ActionContext actionContext){
		World world = World.getInstance();
				
		Thing self = (Thing) actionContext.get("self");
		synchronized(self){
			//是否是引用的数据源
			String refDataSource = self.getStringBlankAsNull("refDataSource");
			if(refDataSource != null){
				Thing ref = world.getThing(refDataSource);
				if(ref == null){
					throw new ActionException("Reference DataSource is null, thing=" + self.getMetadata().getPath() + ",refDataSource=" + refDataSource);
				}else{
					return ref.doAction("getDataSource", actionContext);
				}
			}
			
			//是否是数据库配置的数据源
			String dbDataSourceName = self.getStringBlankAsNull("dbDataSourceName");
			if(dbDataSourceName != null){
				self = (Thing) self.doAction("getDbDataSourceThingByName", actionContext, UtilMap.toMap("name", dbDataSourceName));
				if(self == null){
					throw new ActionException("DbDataSource is null, name=" +  dbDataSourceName);
				}
			}
			
			String path = self.getMetadata().getPath();
			
			DataSource dataSource = (DataSource) world.getData(path);
			Long dataSourceLastmodified = (Long) world.getData(path + "Lastmodified");
			String type = self.getString("type");
			    
			if(dataSource == null){
			    //初始化
			    dataSource = (DataSource) self.doAction(type + "Init", actionContext);    			    
			    world.setData(path, dataSource);
			    world.setData(path + "Lastmodified", self.getMetadata().getLastModified());
			}else if(dataSourceLastmodified == null || dataSourceLastmodified != self.getMetadata().getLastModified()){
			    //配置已更新
			    self.doAction(type + "Close", actionContext, UtilMap.toMap(new Object[]{"datasource", dataSource,
			    		"dataSource", dataSource}));
			    dataSource = (DataSource) self.doAction(type + "Init", actionContext); 
			    world.setData(path, dataSource);
			    world.setData(path + "Lastmodified", self.getMetadata().getLastModified());
			}
			
			return dataSource;
		}
	}
	
	public static Object getConnection(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataSource dataSource = (DataSource) self.doAction("getDataSource", actionContext);

		if(dataSource == null){
		    return null;
		}

		try{
		    return dataSource.getConnection();
		}catch(SQLException e){			
			throw new SQLException("DataSource: " + self.getMetadata().getPath() + " error", e);
		}
	}
	
	public static void close(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		DataSource dataSource = (DataSource) self.doAction("getDataSource", actionContext);//world.getData(path);
		String type = self.getString("type");
		if(type == null){
			type = "c3p0";
		}

		if(dataSource != null){
		    self.doAction(type + "Close", actionContext, UtilMap.toMap(new Object[]{"dataSource", dataSource}));
		    World.getInstance().setData(self.getMetadata().getPath(), null);
		}
	}
	
	public static String getDbType(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return getDbType(self);
	}
	
	public static String getDbType(Thing self){
		String refDataSource  = self.getStringBlankAsNull("refDataSource");
		if(refDataSource != null){
			Thing ds = World.getInstance().getThing(refDataSource);
			if(ds != null){
				return getDbType(ds);
			}
		}
		
		return self.getString("dbType");
	}
	
	public static Properties getProperties(ActionContext actionContext) throws IOException{
		Thing self = actionContext.getObject("self");
		
		Properties p = new Properties();
		
		//从配置文件中获取
		String propertiesFile = self.getStringBlankAsNull("configPropertiesFile");
		if(propertiesFile != null){
			File file = new File(propertiesFile);
			if(file.exists()){				
				FileInputStream fin = new FileInputStream(file);
				try{
					p.load(fin);
				}finally{
					fin.close();
				}
			}
		}
		
		//从配置文件中获取
		String properties = self.getStringBlankAsNull("properties");
		if(properties != null){			
			ByteArrayInputStream bin = new ByteArrayInputStream(properties.getBytes());
			p.load(bin);
			bin.close();
		}
		
		return p;
	}
	
	/**
	 * 通过DbType获取相对应的DataSourceClassName。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getDataSourceClassName(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String dbType = self.getStringBlankAsNull("dbType");
		return dataSourceMap.get(dbType);
	}

}