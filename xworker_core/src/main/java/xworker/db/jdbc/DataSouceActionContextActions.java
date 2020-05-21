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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import xworker.lang.Configuration;

public class DataSouceActionContextActions {
	private static Logger log = LoggerFactory.getLogger(DataSouceActionContextActions.class);
	
	public static Thing getDataSource(Thing self, String dsPath, ActionContext acContext){
		World world = World.getInstance();
		Object dataSourceThing = null;
		Thing realSelf = null;
		if(dsPath.startsWith("self.")){
		    dsPath = dsPath.substring(5, dsPath.length());
		    realSelf= (Thing) acContext.get("self");
		    dsPath = realSelf.getString(dsPath);
		}
		
		if(dsPath.startsWith("action.")){
			String action = dsPath.substring(7, dsPath.length());
			Object obj = ((Thing) acContext.get("self")).doAction(action);
			if(obj instanceof Thing){
				dataSourceThing = (Thing) obj;
				dsPath = null;
			}else if(obj != null){
				dsPath = obj.toString();
			}
		}

		if(dsPath.startsWith("action:")){
			String action = dsPath.substring(7, dsPath.length());
			Action ac = World.getInstance().getAction(action);
			Object obj = ac.run(acContext);
			if(obj instanceof Thing){
				dataSourceThing = (Thing) obj;
				dsPath = null;
			}else if(obj != null){
				dsPath = obj.toString();
			}
		}
		
		if(dsPath.startsWith("var:")){
		    dsPath = dsPath.substring(4, dsPath.length());
		    dataSourceThing = acContext.get(dsPath);
		    if(dataSourceThing instanceof String){
		        dataSourceThing = world.getThing((String) dataSourceThing);
		    }
		}else if(dsPath.startsWith("ognl:")){
			dsPath = dsPath.substring(5, dsPath.length());
			dataSourceThing = OgnlUtil.getValue(dsPath, acContext);
		    if(dataSourceThing instanceof String){
		        dataSourceThing = world.getThing((String) dataSourceThing);
		    }
		}else if(dsPath.startsWith("parent:")){
			dsPath = dsPath.substring(7, dsPath.length());
			ActionContext ac = acContext;
			while(ac != null){
				dataSourceThing = ac.get(dsPath);
				if(dataSourceThing != null){
				    if(dataSourceThing instanceof String){
				        dataSourceThing = world.getThing((String) dataSourceThing);				        
				    }
				    break;
				}
									
				Object acParent = ac.getObject("parentActionContext");
				if(acParent instanceof ActionContext){
					ac = (ActionContext) acParent;
					continue;
				}
				
				acParent = ac.getObject("parentContext");
				if(acParent instanceof ActionContext){
					ac = (ActionContext) acParent;
					continue;
				}
			}
			
		}else if(dsPath.startsWith("_c_.")) {
			dsPath = dsPath.substring(4, dsPath.length());
			dataSourceThing = Configuration.getConfiguration(dsPath, realSelf != null ? realSelf : self, acContext);
		}

		if(dataSourceThing == null){
		    dataSourceThing = world.getThing(dsPath);
		}
		
		
		
		return getDataSourceRef((Thing) dataSourceThing);
	}
	
	public static Thing getDataSourceRef(Thing dataSourceThing){
		if(dataSourceThing != null){
			String refDataSource = dataSourceThing.getStringBlankAsNull("refDataSource");
			if(refDataSource != null){
				dataSourceThing = World.getInstance().getThing(refDataSource);
				
				return getDataSourceRef(dataSourceThing);
			}
		}
		
		return dataSourceThing;
	}
	
	public static void init(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		//World world = World.getInstance();
		ActionContext acContext = (ActionContext) actionContext.get("acContext");
		
		//数据源事物		
		Object dataSourceThing = null;
		if(self == null){
			System.out.println("self is null");actionContext.getScopes();
		}
		String dsPath = self.getString("dataSourcePath");
		
		dataSourceThing = getDataSource(self, dsPath, acContext);

		if(dataSourceThing != null){
		    //数据库总是使用继承
		    ActionContext dsContext = null;
		    List<Bindings> scopes = acContext.getScopes();
		    for(int i=scopes.size() - 1; i>=0; i--){
		        Bindings scope = scopes.get(i);
		        dsContext = scope.getContexts().get(dataSourceThing);
		        if(dsContext != null){
		            break;
		        }
		    }
		    
		    boolean transaction = false;
		    boolean startByMe = false;
		    Connection con = null;
		    if(dsContext != null){
		        startByMe = false; //不是本上下文启动的
		        con = (Connection) dsContext.get("con");
		        //log.info(self.metadata.path + " connection inherited");
		        try{
		        if(con.getAutoCommit() && self.getBoolean("transaction")){
		            con.setAutoCommit(false);
		            transaction = true; //标记本事物开始了事物            
		            //log.info(self.metadata.path + " transaction started");
		        }else{
		            transaction = false;
		        }
		        }catch(SQLException e){
		        	//log.info("" + dataSourceThing);
		        	//acContext.printStackTrace();
		        	throw e;
		        }
		    }else{
		        con = (Connection) ((Thing) dataSourceThing).doAction("getConnection", actionContext);
		        //log.info(self.metadata.path + " connection getted,autoCommit=" + con.getAutoCommit() + ",transaction=" + self.getBoolean("transaction") );
		        startByMe = true; //是本上下文启动的
		        if(self.getBoolean("transaction")){
		            con.setAutoCommit(false);
		            transaction = true; //标记本事物开始了事物
		            //log.info(self.metadata.path + " transaction started");
		        }else{
		            //log.info("setaautocommit=true");
		            con.setAutoCommit(true);
		            transaction = false;
		        }
		        //把本上下文放入到动作上下文的上下文中，已被子动作判断继承
		        acContext.peek().getContexts().put((Thing) dataSourceThing, actionContext);
		        actionContext.getScope(0).put("con", con);
		    }
		    Bindings scope = acContext.peek();
		    scope.put(self.getString("connectionName"), con);    
		    scope.put("dbType", DataSourceActions.getDbType((Thing) dataSourceThing));
		    actionContext.getScope(0).put("transaction", transaction);
		    actionContext.getScope(0).put("startByMe", startByMe);
		}else{
		    log.warn("not set datasource thing path, contextThing=" + self.getMetadata().getName() + ",dataSourcePath=" + dsPath);
		}
	}
	
	public static void success(ActionContext actionContext) throws SQLException{
		//如果连接是自己启动的或者自己启动了新事务
		if(actionContext.get("con") != null && ((Boolean) actionContext.get("startByMe") || (Boolean) actionContext.get("transaction") == true)){
			Connection con = (Connection) actionContext.get("con");
		    try{
		        //本上下文取的连接
		        if((Boolean) actionContext.get("transaction")){
		            //启动了事物
		            con.commit();
		            con.setAutoCommit(true);
		            //log.info(self.metadata.path + " transaction commited");
		        }
		    }finally{
		        //log.info(self.metadata.path + " connection closed");
		        con.close();
		    }
		}
	}
	
	public static void exception(ActionContext actionContext) throws SQLException{
		//如果连接是自己启动的或者自己启动了新事务
		if(actionContext.get("con") != null && ((Boolean) actionContext.get("startByMe") || (Boolean) actionContext.get("transaction") == true)){
			Connection con = (Connection) actionContext.get("con");
		    try{
		        //本上下文取的连接
		        if((Boolean) actionContext.get("transaction")){
		            //启动了事物
		            con.rollback();
		            con.setAutoCommit(true);
		            //log.info(self.metadata.path + " transaction rollbacked");
		        }
		    }finally{
		        con.close();
		        //log.info(self.metadata.path + " connection closed");
		    }
		}
	}
}