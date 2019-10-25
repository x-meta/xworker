package xworker.db.jdbc;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import xworker.util.UtilData;

public class HikariActions {
	private static Logger logger  = LoggerFactory.getLogger(HikariActions.class);	
	
	public static Object HikariCPInit(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//配置文件
		Thing thing = self;
		try{
			thing = UtilData.createPropertiesThing(self, self.getString("configPropertiesFile"));
		}catch(Exception e){
			logger.error("Reader datasource properties file error", e);
		}
			
		//创建Hikari配置
		HikariConfig config = new HikariConfig();
		//配置
		Properties p = (Properties) self.doAction("getProperties", actionContext);
		
		config.setAutoCommit(true);
		config.setDriverClassName((String) thing.doAction("getDriverClass", actionContext));
		config.setJdbcUrl((String) thing.doAction("getUrl", actionContext));
		config.setUsername((String) thing.doAction("getUserName", actionContext));
		config.setPassword((String) thing.doAction("getPassword", actionContext));
		config.setConnectionTimeout(thing.getInt("checkoutTimeout", 30000));
		config.setIdleTimeout(thing.getInt("maxIdleTime", 600000));
		config.setMaxLifetime(1800000);
		config.setMaximumPoolSize(thing.getInt("maxPoolSize", 10));
		config.setPoolName(self.getMetadata().getName());
		if(p != null){
			config.setDataSourceProperties(p);
		}		
		
		String testSql = thing.getString("testSql");
		if(testSql != null && !"".equals(testSql)){
		    config.setConnectionTestQuery(testSql);
		}
		
		HikariDataSource dataSource = new HikariDataSource(config);
		
		return dataSource;
	}
	
	public static void HikariCPClose(ActionContext actionContext){
		if(actionContext.get("dataSource") != null){
		    ((HikariDataSource) actionContext.get("dataSource")).close();
		}
	}
}
