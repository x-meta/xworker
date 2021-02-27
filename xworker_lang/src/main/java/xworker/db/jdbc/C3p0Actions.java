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

import java.beans.PropertyVetoException;
import java.util.Properties;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class C3p0Actions {
	//private static Logger logger = LoggerFactory.getLogger(C3p0Actions.class);
	private static final String TAG = C3p0Actions.class.getName();
	
	public static Object init(ActionContext actionContext) throws PropertyVetoException{
		Thing self = (Thing) actionContext.get("self");
		
		//配置文件
		Thing thing = self;
		try{
			thing = UtilData.createPropertiesThing(self, self.getString("configPropertiesFile"));
		}catch(Exception e){
			Executor.error(TAG, "Reader datasource properties file error", e);
		}
			
		//创建c3p0DataSource
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		//配置
		Properties p = (Properties) self.doAction("getProperties", actionContext);
		if(p != null){
			dataSource.setProperties(p);
		}
		
		dataSource.setDriverClass((String) thing.doAction("getDriverClass", actionContext));
		dataSource.setJdbcUrl((String) thing.doAction("getUrl", actionContext));
		dataSource.setUser((String) thing.doAction("getUserName", actionContext));
		dataSource.setPassword((String) thing.doAction("getPassword", actionContext));
		dataSource.setCheckoutTimeout(thing.getInt("checkoutTimeout", 0));
		dataSource.setInitialPoolSize(thing.getInt("initPoolSize", 3));
		dataSource.setMaxPoolSize(thing.getInt("maxPoolSize", 15));
		dataSource.setMinPoolSize(thing.getInt("minPoolSize", 3));
		dataSource.setMaxIdleTime(thing.getInt("maxIdleTime", 0));
		dataSource.setIdleConnectionTestPeriod(thing.getInt("idleConnectionTestPeriod", 0));
		dataSource.setTestConnectionOnCheckout(thing.getBoolean("testOnCheckOut"));
		dataSource.setTestConnectionOnCheckin(thing.getBoolean("testOnCheckIn"));
		dataSource.setAcquireIncrement(thing.getInt("acquireIncrement", 3));
		dataSource.setAcquireRetryAttempts(thing.getInt("acquireRetryAttempts", 30));
		dataSource.setAcquireRetryDelay(thing.getInt("acquireRetryDelay", 1000));
		dataSource.setBreakAfterAcquireFailure(thing.getBoolean("breakAfterAcquireFailure", false));
		dataSource.setDebugUnreturnedConnectionStackTraces(thing.getBoolean("debugUnreturnedConnectionStackTraces", false));
	
		
		String testSql = thing.getString("testSql");
		if(testSql != null && !"".equals(testSql)){
		    dataSource.setPreferredTestQuery(testSql);
		}
		return dataSource;
	}
	
	public static void close(ActionContext actionContext){
		if(actionContext.get("dataSource") != null){
		    ((ComboPooledDataSource) actionContext.get("dataSource")).close();
		}
	}
}