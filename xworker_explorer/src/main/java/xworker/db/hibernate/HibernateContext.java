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
package xworker.db.hibernate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;


public class HibernateContext {
	private static Logger log = LoggerFactory.getLogger(HibernateContext.class);
	private static World world = World.getInstance();
	
	/** Hibernate工厂实例 */
	private static Map<String, SessionFactory> factorys = new HashMap<String, SessionFactory>();
	
	/**
	 * 取系统默认的数据库工厂。
	 * 
	 * @return 数据库工厂
	 */
	public static SessionFactory getDbSessionFactory(){
		Thing dw = null;//UtilDefault.getDefaultWebapp();
		String dbName = "xworker";
		if(dw != null){
			dbName = dw.getMetadata().getName();
		}
		
		return getDbSessionFactory(dbName);
	}
	
	/**
     * 通过名称取hibernate SessionFactory。
     * 
     * @param name
     * @return
     */
    public static SessionFactory getDbSessionFactory(String name){
    	if(name == null) name = "xworker";
    	
    	SessionFactory f = factorys.get(name);
    	if(f == null){
    		initDbSessionFactory(name);
    		f = factorys.get(name);    		
    	}
    	
    	return f;
    }
    
    public static void initDbSessionFactory(String name){
    	try{
    		SessionFactory f = (SessionFactory) factorys.get(name);
    		if(f != null){
    			f.close();
    			f = null;
    			factorys.remove(name);
    		}
    		
    		File configFile = new File(world.getPath() + "/work/hibernate/" + name + "/hibernate.cfg.xml");
    		//URL url = World.class.getResource("../../" + name + "/hibernate.cfg.xml");
    		//if(url == null){
    		if(!configFile.exists()){
    			log.warn("指定的资源为空，无法初始化hibenate：" + configFile.getAbsolutePath());
    		}else{    			     			
				//Configuration c = new Configuration().configure(url);
    			Configuration c = new Configuration().configure(configFile);			    				
				f = c.buildSessionFactory();
				factorys.put(name, f);			
    		}
		}catch(Exception e){
			//log.error("get db session factory", e);		
			if(e instanceof RuntimeException){
				throw (RuntimeException) e;
			}else{				
				throw new ActionException(e);
			}
		}
    }
    
    public static void closeDBSessionFactory(String name){
    	SessionFactory f = (SessionFactory) factorys.get(name);
		if(f != null){
			f.close();
			f = null;
			factorys.remove(name);
		}
    }
    
	/**
	 * 继承，通过dbName是否相同来判断继承。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static ActionContext inherit(ActionContext actionContext){			
		ActionContext acContext = (ActionContext) actionContext.get("acContext");
		
		Thing context = (Thing) actionContext.get("self");		
		String dbName = UtilString.getString(context.getString("configName"), acContext);

		for(int i= acContext.getScopesSize() -1; i>=0; i--){
			Bindings bindings = acContext.getScope(i);
			for(Thing acontext : bindings.getContexts().keySet()){				 
				if(acontext.isThing("xworker.lang.context.HibernateContext")){
					String adbName = UtilString.getString(acontext.getString("configName"), acContext);
					if(dbName == adbName || (dbName != null && dbName.equals(adbName))){						
						ActionContext accontext = bindings.getContexts().get(acontext);
						String name = context.getMetadata().getName();
						acContext.peek().put(name, accontext.get("session"));
						acContext.peek().put(name + "Factory", accontext.get("factory"));
					}
				}
			}
		}
		return null;
	}
	
	public static void init(ActionContext actionContext){			
		Thing context = (Thing) actionContext.get("self");
		
		ActionContext acContext = (ActionContext) actionContext.get("acContext");
		String dbName = context.getString("configName");
		if(dbName == null || "".equals(dbName)) return;
		if(dbName.startsWith("self.")){
			dbName = dbName.substring(5, dbName.length());
			Thing acSelf = (Thing) acContext.get("self");
			if(acSelf != null){
				dbName = acSelf.getString(dbName);
			}
		}else if(dbName.startsWith("\"")){
			dbName = dbName.substring(1, dbName.length() - 1);
		}else{
			String dName = (String) acContext.get(dbName);
			if(dName != null && !"".equals(dName)){
				dbName = dName;
			}
		}
		
		if(dbName == null || "".equals(dbName)){
			return;
		}
		
		SessionFactory factory = getDbSessionFactory(dbName);
		if(factory == null){
			return;
		}

		boolean isNeedTransaction = context.getBoolean("isNeedTransaction");
		Session session = factory.openSession();
		if(isNeedTransaction){
			Transaction tx = session.beginTransaction();
			tx.begin();
			actionContext.getScope(0).put("transaction", tx);
		}

		actionContext.getScope(0).put("factory", factory);
		actionContext.getScope(0).put("session", session);			

		String name = context.getMetadata().getName();
		acContext.put(name + "Factory", factory);
		acContext.put(name, session);
	}
	
	public static void success(ActionContext actionContext){			
		Thing context = (Thing) actionContext.get("self");
		Session session = (Session) actionContext.get("session");
		boolean isNeedTransaction = context.getBoolean("isNeedTransaction");	
		Transaction tx = (Transaction) actionContext.get("transaction");
		try{			
			if(isNeedTransaction && tx != null){				
				tx.commit();
			}
		}catch(RuntimeException e){
			try{
				tx.rollback();
			}catch(Exception ee){				
			}
			throw e;
		}finally{
			if(session != null){
				session.close();
			}
		}	
		
		//System.out.println("hibernate context success");
	}
	
	public static void exception(ActionContext actionContext){			
		Thing context = (Thing) actionContext.get("self");
		Session session = (Session) actionContext.get("session");
		boolean isNeedTransaction = context.getBoolean("isNeedTransaction");	
		Transaction tx = (Transaction) actionContext.get("transaction");
		try{			
			if(isNeedTransaction && tx != null){				
				tx.rollback();
			}
		}catch(RuntimeException e){
			try{
				tx.rollback();
			}catch(Exception ee){				
			}
			throw e;
		}finally{
			if(session != null){
				session.close();
			}
		}	
	}
}