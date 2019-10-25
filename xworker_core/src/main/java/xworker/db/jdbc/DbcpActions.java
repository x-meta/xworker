package xworker.db.jdbc;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class DbcpActions {
private static Logger logger = LoggerFactory.getLogger(DbcpActions.class);
	
	public static Object init(ActionContext actionContext) throws PropertyVetoException{
		Thing self = (Thing) actionContext.get("self");
		
		//配置文件
		Thing thing = self;
		try{
			thing = UtilData.createPropertiesThing(self, self.getString("configPropertiesFile"));
		}catch(Exception e){
			logger.error("Reader datasource properties file error", e);
		}
		
		//创建c3p0DataSource
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName((String) thing.doAction("getDriverClass", actionContext));
		dataSource.setUrl((String) thing.doAction("getUrl", actionContext));
		dataSource.setUsername((String) thing.doAction("getUserName", actionContext));
		dataSource.setPassword((String) thing.doAction("getPassword", actionContext));
		dataSource.setInitialSize(thing.getInt("initPoolSize", 3));
		dataSource.setMaxActive(thing.getInt("maxPoolSize", 15));
		dataSource.setMinIdle(thing.getInt("minPoolSize", 3));
		dataSource.setMaxWait(thing.getInt("maxIdleTime", 0));
		dataSource.setTimeBetweenEvictionRunsMillis(thing.getInt("idleConnectionTestPeriod", 0));
		dataSource.setTestOnBorrow(thing.getBoolean("testOnCheckOut"));
		dataSource.setTestOnReturn(thing.getBoolean("testOnCheckIn"));
		dataSource.setTestWhileIdle(thing.getBoolean("testWhileIdle"));
		//dataSource.set(thing.getInt("acquireIncrement", 3));
		//dataSource.setAcquireRetryAttempts(thing.getInt("acquireRetryAttempts", 30));
		//dataSource.setAcquireRetryDelay(thing.getInt("acquireRetryDelay", 1000));
		//dataSource.setBreakAfterAcquireFailure(thing.getBoolean("breakAfterAcquireFailure", false));
		//dataSource.setDebugUnreturnedConnectionStackTraces(thing.getBoolean("debugUnreturnedConnectionStackTraces", false));
		String testSql = thing.getString("testSql");
		if(testSql != null && !"".equals(testSql)){
		    dataSource.setValidationQuery(testSql);
		}
		return dataSource;
	}
	
	public static void close(ActionContext actionContext){
		if(actionContext.get("dataSource") != null){
		    try {
				((BasicDataSource) actionContext.get("dataSource")).close();
			} catch (SQLException e) {
				logger.warn("close dbcp error", e);
			}
		}
	}
}
