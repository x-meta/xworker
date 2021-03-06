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
package xworker.dataObject.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectException;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.Condition;
import xworker.dataObject.utils.DbUtil;
import xworker.db.jdbc.DataSouceActionContextActions;
import xworker.db.jdbc.DataSourceActions;
import xworker.lang.Configuration;
import xworker.lang.executor.Executor;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;

public class DbDataObject {
	//private static Logger log = LoggerFactory.getLogger(DbDataObject.class);
	private static final String TAG = DbDataObject.class.getName();
	
	/** 防止数据查询一次读取太多记录而设置的，太多记录应该使用iterator，而不是直接查询。*/
	private static int MAX_ROWS = 100000;  
	
	/**
	 * 设置参数。
	 * 
	 * @param actionContext 变量上下文
	 * @throws SQLException SQL异常
	 */
	@SuppressWarnings("unchecked")
	public static void setStatementParams(ActionContext actionContext) throws SQLException{
		Integer index = (Integer) actionContext.get("index");
		List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
		List<Thing> attributes = (List<Thing>) actionContext.get("attributes");
		PreparedStatement pst = (PreparedStatement) actionContext.get("pst");

		for(int i=0; i<cds.size(); i++){
			Map<String, Object> c = cds.get(i);

			//确定参数的类型，优先使用查询条件的类型			
			Thing condition = (Thing) c.get("condition");
			String type = condition.getStringBlankAsNull("type");
			if(type == null) {
				//从属性中获取参数的类型
			    for(Thing attr : attributes){
			    	String name = attr.getString("name");
			    	
			        if(name.equals(condition.get("attributeName")) || name.equals(condition.get("name"))){
			            type = attr.getStringBlankAsNull("type");
			            break;
			        }
			    }
			}
			if(type == null){
	    		Executor.warn(TAG, "Can not find determine condition value type, condition=" + condition);
	    		type = "string";
		    }

		    DbUtil.setParameterValue(pst, index + i, type , c.get("value"));
		}
	}
	
	/**
	 * 删除数据。
	 * 
	 * @param actionContext 变量上下文
	 * @return 是否删除成功
	 * @throws SQLException SQL异常
	 */
	public static boolean delete(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		
		//键值
		Object[][] keyDatas = theData.getKeyAndDatas();
		if(keyDatas == null || keyDatas.length == 0){
		    throw new SQLException("no keys data cannot delete, dataObjectPath=" + theData.getMetadata().getDescriptor().getMetadata().getPath());
		}

		//生成insert sql语句
		String sql = "delete from " + self.getString("tableName");
		sql = sql + " where ";
		for(int i=0; i<keyDatas.length; i++){
		    sql = sql + getFieldName((Thing) keyDatas[i][0]) + "=?";
		    if(i < keyDatas.length -1){
		        sql = sql + " and ";
		    }
		}
		if(self.getBoolean("showSql")){
		    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}

		//设置参数值
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = con.prepareStatement(sql);
		try{
		    int index = 1;
		    for(int i=0; i<keyDatas.length; i++){
		        //log.info(keyDatas[i][0].get("name") + "=" + keyDatas[i][1]);
		        DbUtil.setParameterValue(pst, index + i, ((Thing) keyDatas[i][0]).getString("type"), keyDatas[i][1]);
		    }
		    
		    //执行sql
		    int count= pst.executeUpdate();
		    return count == 1;
		}finally{
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	/**
	 *　批量插入数据库。
	 *
	 * @param actionContext
	 * @return
	 * @throws SQLException 
	 */
	public static int createBatch(ActionContext actionContext) throws SQLException {
		Thing self = actionContext.getObject("self");
		List<DataObject> datas = actionContext.getObject("datas");
		List<Thing> attributes = new ArrayList<Thing>();
		for(Thing attr : self.getChilds("attribute")) {
			if(attr.getBoolean("dataField")) {
				attributes.add(attr);
			}
		}
		
		//生成insert sql语句
		String sql = "insert into " + self.getString("tableName") + "(";
		String valueSql = " values(";
		for(int i =0; i<attributes.size(); i++) {
			Thing attr = attributes.get(i);
			sql = sql + attr.getString("fieldName");
			valueSql = valueSql + "?";
			if(i < attributes.size() - 1) {
				sql = sql + ",";
				valueSql = valueSql + ",";
			}
		}
		
		sql = sql + ")";
        valueSql = valueSql + ")";
        sql = sql + valueSql;
		if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
			Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}
		
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = con.prepareStatement(sql);
		try {
			for(DataObject data : datas) {
				int index = 1;
				for(Thing attr : attributes) {
					DbUtil.setParameter(pst, index, attr.getMetadata().getName(), data);
					index++;
				}
				
				pst.addBatch();
			}
			
			int[] ids =  pst.executeBatch();
			return ids.length;
		}finally {
			if(pst != null) {
				pst.close();
			}
		}
	}
	
	/**
	 * 插入数据。
	 * 
	 * @param actionContext 变量上下文
	 * @throws Exception 异常
	 */
	public static Object create(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");

		//是否生成序列号
		Object[][] keyDatas = theData.getKeyAndDatas();
		
		//如果关键字不存在，那么有可能是自动增长的键值等，此时需要获取生成的主键
		boolean getGenerateKeys = false;
		for(Object[] keys :  keyDatas){
			if(keys[1] == null){
				getGenerateKeys = true;
				break;				
			}
		}

		//生成insert sql语句
		String sql = "insert into " + self.getString("tableName") + "(";
		String valueSql = " values(";
		String[] dirtyFields = theData.getMetadata().getDirtyFields();
		if(dirtyFields == null || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
			Executor.warn(TAG, "no value need insert to database");
		    return null;
		}
		for(int i=0; i<dirtyFields.length; i++){    
		    Thing descriptor = theData.getMetadata().getDefinition(dirtyFields[i]);
		    sql = sql + getFieldName(descriptor);
		    valueSql = valueSql + "?";
		    if(i < dirtyFields.length - 1){
		        sql = sql + ",";
		        valueSql = valueSql + ",";
		    }else{
		        sql = sql + ")";
		        valueSql = valueSql + ")";
		    }
		}
		sql = sql + valueSql;
		if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
			Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}

		//设置参数值
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = null;
		if(getGenerateKeys){
			pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}else{
			pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		try{
		    for(int i=0; i<dirtyFields.length; i++){         
		        DbUtil.setParameter(pst, i+1, dirtyFields[i], theData);
		    }
		    
		    //执行sql
		    pst.execute();
		    
		    //获取生成的key
		    if(getGenerateKeys){
			    ResultSet keys = pst.getGeneratedKeys();
			    if(keys.next()){
			    	for(int i=0; i<keyDatas.length; i++){
			    		Object[] key =  keyDatas[i];
			    		Thing thing = (Thing) key[0];
			    		String keyName = thing.getMetadata().getName();
			    		Object keyValue = keys.getObject(i + 1);
			    		theData.put(keyName, keyValue);
			    	}
			    }else{
			    	return null;
			    }
		    }
		}finally{
		    if(pst != null){
		        pst.close();
		    }
		}
		
		return theData;
	}
		
	/**
	 * 更新数据。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 */
	public static boolean update(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		
		//键值
		Object[][] keyDatas = theData.getKeyAndDatas();
		if(keyDatas == null || keyDatas.length == 0){
		    throw new SQLException("no keys data cannot update, dataObjectPath=" + theData.getMetadata().getDescriptor().getMetadata().getPath());
		}

		//生成insert sql语句
		String sql = "update " + self.getString("tableName") + " set ";
		String[] dirtyFields = theData.getMetadata().getDirtyFields();
		if(dirtyFields == null || dirtyFields.length == 0){    
		    return false;
		}
		for(int i=0; i<dirtyFields.length; i++){   
		    Thing descriptor = theData.getMetadata().getDefinition(dirtyFields[i]); 
		    sql = sql + getFieldName(descriptor) + "=?";    
		    if(i < dirtyFields.length - 1){
		        sql = sql + ",";
		    }
		}
		sql = sql + " where ";
		for(int i=0; i<keyDatas.length; i++){
		    sql = sql + getFieldName((Thing) keyDatas[i][0]) + "=?";
		    if(i < keyDatas.length -1){
		        sql = sql + " and ";
		    }
		}
		if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}

		//设置参数值
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = con.prepareStatement(sql);
		try{
		    int index = 1;
		    for(int i=0; i<dirtyFields.length; i++){  
		        //log.info(dirtyFields[i] + "=" + theData.get(dirtyFields[i]));
		        DbUtil.setParameter(pst, i+1, dirtyFields[i], theData);
		        index ++;
		    }
		    
		    for(int i=0; i<keyDatas.length; i++){
		        //log.info(keyDatas[i][0].get("name") + "=" + keyDatas[i][1]);
		        DbUtil.setParameterValue(pst, index + i, ((Thing) keyDatas[i][0]).getString("type"), keyDatas[i][1]);
		    }
		    
		    //执行sql
		    return pst.executeUpdate() == 1;
		}finally{
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	/**
	 * 批量更新。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static int updateBatch(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		if(theData == null){
			theData = new DataObject(self);
		}

		//生成insert sql语句
		String sql = "update " + self.getString("tableName") + " set ";
		String[] dirtyFields = theData.getMetadata().getDirtyFields();
		if(dirtyFields == null || dirtyFields.length == 0){    
		    return 0;
		}
		for(int i=0; i<dirtyFields.length; i++){   
		    Thing descriptor = theData.getMetadata().getDefinition(dirtyFields[i]); 
		    sql = sql + getFieldName(descriptor) + "=?";    
		    if(i < dirtyFields.length - 1){
		        sql = sql + ",";
		    }
		}

		Map<String, Object> datas = (Map<String, Object>) actionContext.get("conditionData");
		if(datas == null){
		    datas = Collections.emptyMap();
		}
		//log.info("" + datas);
		Object conditionConfig = (Object) actionContext.get("conditionConfig");
		if(conditionConfig == null){
		    conditionConfig = Collections.emptyMap();
		}
		List<Map<String, Object>> cds = new ArrayList<Map<String, Object>>();
		String clause = DbUtil.getConditionSql(conditionConfig, actionContext, datas, cds);
		//log.info("clause=" + clause);
		if(clause != null && clause != ""){
		    sql = sql + " where " + clause;
		}
		if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}

		//设置参数值
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = con.prepareStatement(sql);
		try{
		    int index = 1;
		    for(int i=0; i<dirtyFields.length; i++){  
		        //log.info(dirtyFields[i] + "=" + theData.get(dirtyFields[i]));
		        DbUtil.setParameter(pst, i+1, dirtyFields[i], theData);
		        index ++;
		    }
		    
		    for(int i=0; i<cds.size(); i++){
		        Map<String, Object> c = cds.get(i);
		        Thing thing = theData.getMetadata().getDefinition((String) c.get("name"));        
		        DbUtil.setParameterValue(pst, index + i, thing.getString("type"), c.get("value"));
		    }
		    
		    //执行sql
		    return pst.executeUpdate();
		}finally{
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	/**
	 * 批量删除数据。
	 * 
	 * @param actionContext
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static int deleteBatch(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DataObject theData = (DataObject) actionContext.get("theData");
		if(theData == null){
			theData = new DataObject(self);
		}

		//生成查询sql语句
		Map<String, Object> datas = (Map<String, Object>) actionContext.get("conditionData");
		if(datas == null){
		    datas = Collections.emptyMap();
		}
		//log.info("" + datas);
		Object conditionConfig = (Object) actionContext.get("conditionConfig");
		if(conditionConfig == null){
		    conditionConfig = Collections.emptyMap();
		}

		String sql = "delete from ";
		String tableName = self.getString("tableName");
		sql = sql + tableName;

		List<Map<String, Object>> cds = new ArrayList<Map<String, Object>>();
		String clause = DbUtil.getConditionSql(conditionConfig, actionContext, datas, cds);
		//log.info("clause=" + clause);
		if(clause != null && clause != ""){
		    sql = sql + " where " + clause;
		}

		if(self.getBoolean("showSql")){
		    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		}

		//设置参数值和查询
		Connection con = (Connection) actionContext.get("con");
		PreparedStatement pst = con.prepareStatement(sql);
		try{
		    int index = 1;
		    for(int i=0; i<cds.size(); i++){
		        Map<String, Object> c = cds.get(i);
		        Thing thing = theData.getMetadata().getDefinition((String) c.get("name"));        
		        DbUtil.setParameterValue(pst, index + i, thing.getString("type"), c.get("value"));
		    }
		    
		    //执行sql
		    return pst.executeUpdate();
		}finally{		
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	/**
	 * 获取数据库字段名。
	 * 
	 * @param thing
	 * @return
	 */
	public static String getFieldName(Thing thing){
		String fieldName = thing.getString("sql"); 
		if(fieldName == null || "".equals(fieldName)){
			fieldName = thing.getString("fieldName");
		}
		if(fieldName == null || "".equals(fieldName)){
			return thing.getString("name");
		}
		
		return fieldName;
	}
	
	/**
	 * 查询SQL数据对象的遍历方法。
	 * 
	 * @param actionContext
	 * @throws Exception
	 */
	public static void iterateSql(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		UserTask userTask = DataObject.getUserTask(self, actionContext);
		
		String sql = (String) self.doAction("getSql", actionContext);	
		if(sql == null || "".equals(sql)){
			throw new ActionException("Sql is null or blank, dataObject=" + self.getMetadata().getPath());
		}
		sql = sql .trim();
		if(sql.endsWith(";")){
			sql = sql.substring(0, sql.length() - 1);
		}			
		UserTaskManager.setUserTaskLabelDetail(userTask, "Sql setted", sql);
					
		//查看是否要生成属性
		//boolean changed = false;
		boolean isExecute = false;
		Connection con = (Connection) actionContext.get("con");
		if(self.getChilds("attribute").size() == 0 || !sql.equals(self.getString("querySql"))){
			//changed = true;
			//先移除
			List<Thing> attrs = self.getChilds("attribute");
			for(Thing attr : attrs){
				self.removeChild(attr);
			}
			
			self.put("querySql", sql);
			
			//生成			
			PreparedStatement pst = null;
        	try{
        		pst = con.prepareStatement(sql);
        		ResultSetMetaData meta = pst.getMetaData();
        		if(meta == null){
        			isExecute = true;
        			Thing attr = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
        			attr.put("name", "result");
        			
        			self.addChild(attr);
        			self.put("isExecute", isExecute);
        		}else{
	        		for(int i=1; i<=meta.getColumnCount(); i++){
	        			Thing attr = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
	        			attr.put("name", meta.getColumnName(i));
	        			attr.put("fieldName", meta.getColumnName(i));
	        			attr.put("label", meta.getColumnLabel(i));
	        			//attr.put("gridWidth",  meta.getColumnDisplaySize(i) * 3);
	        			attr.put("type", DbUtil.getXWorkerType(meta.getColumnType(i), meta.getScale(i)));
	        			
	        			self.addChild(attr);
	        			
	            	}
	        		self.put("isExecute", false);
        		}
        	}finally{
        		if(pst != null){
        			pst.close();
        		}
        	}
        	        	
		}
				
		if(self.getBoolean("isExecute")){
			PreparedStatement pst = null;
        	try{
        		pst = con.prepareStatement(sql);
        		UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared, executing......", null);
        		UserTaskManager.setUserTaskData(userTask, "pst", pst);
        		
        		boolean r = pst.execute();
        		UserTaskManager.setUserTaskLabelDetail(userTask, "Statement executed, get data", null);
        		
    			DataObject data = new DataObject(self);
    			data.put("result", r);
    			
    			Object ac = actionContext.get("action");
    			Action action = null;
    			if(ac instanceof Action){
    				action = (Action) ac;
    			}else if(ac instanceof Thing){
    				action = ((Thing) ac).getAction();
    			}else if(ac instanceof String){
    				action = World.getInstance().getAction((String) ac);
    			}else{
    				throw new ActionException("Iterator: action is null or not a (Action、Thing or String), path=" + self.getMetadata().getPath());
    			}
    			
    			action.run(actionContext, UtilMap.toMap("index", 1, "data", data));
        	}finally{
        		if(pst != null){
        			pst.close();
        		}
        	}
		}else{
			iterator(actionContext);
		}
	}
	
	/**
	 * 查询SQL数据对象的查询方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws Exception
	 */
	public static Object querySql(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		UserTask userTask = DataObject.getUserTask(self, actionContext);
		
		String sql = (String) self.doAction("getSql", actionContext);	
		if(sql == null || "".equals(sql)){
			throw new ActionException("Sql is null or blank, dataObject=" + self.getMetadata().getPath());
		}
		sql = sql .trim();
		if(sql.endsWith(";")){
			sql = sql.substring(0, sql.length() - 1);
		}			
		UserTaskManager.setUserTaskLabelDetail(userTask, "Sql setted", sql);
					
		//查看是否要生成属性
		boolean changed = false;
		boolean isExecute = false;
		Connection con = (Connection) actionContext.get("con");
		Object result = null;
		if(self.getChilds("attribute").size() == 0 || !sql.equals(self.getString("querySql"))){
			changed = true;
			//先移除
			List<Thing> attrs = self.getChilds("attribute");
			for(Thing attr : attrs){
				self.removeChild(attr);
			}
			
			self.put("querySql", sql);
			
			//生成			
			PreparedStatement pst = null;
        	try{
        		pst = con.prepareStatement(sql);
        		ResultSetMetaData meta =  pst.getMetaData();
        		
        		if(meta == null){
        			isExecute = true;
        			Thing attr = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
        			attr.put("name", "result");
        			
        			self.addChild(attr);
        			self.put("isExecute", isExecute);
        		}else{
	        		for(int i=1; i<=meta.getColumnCount(); i++){
	        			Thing attr = new Thing("xworker.dataObject.db.DbDataObject/@attribute");
	        			attr.put("name", meta.getColumnName(i));
	        			attr.put("fieldName", meta.getColumnName(i));
	        			attr.put("label", meta.getColumnLabel(i));
	        			//attr.put("gridWidth",  meta.getColumnDisplaySize(i) * 3);
	        			attr.put("type", DbUtil.getXWorkerType(meta.getColumnType(i), meta.getScale(i)));
	        			
	        			self.addChild(attr);
	        			
	            	}
	        		self.put("isExecute", false);
        		}
        	}finally{
        		if(pst != null){
        			pst.close();
        		}
        	}
        	        	
		}
				
		if(self.getBoolean("isExecute")){
			PreparedStatement pst = null;
        	try{
        		pst = con.prepareStatement(sql);
        		UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared, executing......", null);
        		UserTaskManager.setUserTaskData(userTask, "pst", pst);
        		
        		boolean r = pst.execute();
        		UserTaskManager.setUserTaskLabelDetail(userTask, "Statement executed, get data", null);
        		
    			DataObject data = new DataObject(self);
    			data.put("result", r);
    			List<DataObject> rs = new ArrayList<DataObject>();
    			rs.add(data);
    			result = rs;
    			
        	}finally{
        		if(pst != null){
        			pst.close();
        		}
        	}
		}else{
			result = query(actionContext);
		}
		PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
    	if(pageInfo != null && changed){
    		//通知界面变更
    		pageInfo.put("getdynamicDataObject", self);
	        pageInfo.put("dynamicDataObject", self.detach()); //detach是因为datastore需要判断是否是原来的数据对象
	    }
    	
    	return result;		
	}
	
	@SuppressWarnings("unchecked")
	public static Object query(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		UserTask userTask =  DataObject.getUserTask(self, actionContext);
		
		try{
			//---------------查询参数----------------
			Map<String, Object> datas = (Map<String, Object>) actionContext.get("conditionData");             //查询条件数据
			if(datas == null){
			    datas = Collections.emptyMap();
			}
			
			Object conditionConfig = actionContext.get("conditionConfig"); //查询配置
			if(conditionConfig == null){
			    conditionConfig = new Thing();
			}
			Executor.debug(TAG, "" +  conditionConfig);
			//分页信息
			Map<String, Object> pageInfo = (Map<String, Object>) actionContext.get("pageInfo");		
			
			List<Thing> attributes = (List<Thing>) self.get("attribute@");
			for(int i=0; i<attributes.size(); i++){
			    //过滤非数据字段和不需要显示的字段
			    if(!attributes.get(i).getBoolean("dataField")){
			    	attributes.remove(i);
			    	i--;
			    }
			}	
			
			List<Map<String, Object>> cds = new ArrayList<Map<String, Object>>();
			
			//----------------生成SQL语句--------------
			String sql = self.doAction("getQuerySqlAndParams", actionContext, "pageInfo", pageInfo,
					"attributes", attributes, "cds", cds, "conditionConfig", conditionConfig, "datas", datas);
			
			if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
			    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
			}
			UserTaskManager.setUserTaskLabelDetail(userTask, "Sql setted", sql);
			
			//----------------生成SQL完毕--------------------
	
			//----------------执行查询-----------------------
			if(pageInfo != null && (Integer) pageInfo.get("limit") != 0){
			    //有分页查询
			    String dbType = (String) actionContext.get("dbType"); //dbType是有DataSource上下文设置的
			    if(dbType != null &&  !"".equals(dbType) && self.getBoolean("pagineByCursor") == false){
			    	dbType = dbType.toLowerCase();
			        if("oracle".equals(dbType) || dbType.startsWith("oracle")){
			            return self.doAction("queryOraclePage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			        }else if("derby".equals(dbType)){
			            return self.doAction("queryDerbyPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			        }else if("mysql".equals(dbType)){
			            return self.doAction("queryMysqlPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			        }else if("sqlserver2005".equals(dbType)){
			        	return self.doAction("querySqlServer2005Page", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			        }else if("sqlite".equals(dbType)){
			        	return self.doAction("queryMysqlPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			        }
			    }
			    
			    //其他使用游标分页查询
			    return self.doAction("queryCursorPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes}));
			    //throw new Exception("dbType=" + dbType + ", not supported page query now");
			}else{
			    //没有分页查询
			    //设置参数值和查询
				Connection con = (Connection) actionContext.get("con");
				PreparedStatement pst = con.prepareStatement(sql);
				UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared, executing......", null);
        		UserTaskManager.setUserTaskData(userTask, "pst", pst);
        		
				ResultSet rs = null;
			    try{
			        //设置查询参数
			        self.doAction("setStatementParams", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "pst",pst, "attributes",attributes, "index",1}));
			        
			        //执行sql
			        rs = pst.executeQuery();			        
			        UserTaskManager.setUserTaskLabelDetail(userTask, "Statement executed", null);
			        
			        List<DataObject> ds = new ArrayList<DataObject>();
			        long start = System.currentTimeMillis();
			        int count = 0;
			        while(rs.next()){
			            //构造对象
			        	DataObject data = new DataObject(self);
			            data.setInited(false);
			            //设置属性值
			            for(int i=0; i<attributes.size(); i++){
			                data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
			            }
			            
			            data.setInited(true);
			            ds.add(data);    
			            //log.info("data=" + data);
			            
			            if(userTask != null && System.currentTimeMillis() - start > 1000){
			            	UserTaskManager.setUserTaskLabelDetail(userTask, ds.size() + " rows has getted", null);
			            	start = System.currentTimeMillis();
			            }
			            
			            count++;
			            if(count > MAX_ROWS) {
			            	break;
			            }
			        }
			        
			        return ds;
			    }finally{
			        if(rs != null){
			            rs.close();
			        }
			        if(pst != null){
			            pst.close();
			        }
			    }
			}
		}finally{
			DataObject.userTaskFinished();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void iterator(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		Object ac = actionContext.get("action");
		Action action = null;
		if(ac instanceof Action){
			action = (Action) ac;
		}else if(ac instanceof Thing){
			action = ((Thing) ac).getAction();
		}else if(ac instanceof String){
			action = World.getInstance().getAction((String) ac);
		}else{
			throw new ActionException("Iterator: action is null or not a (Action、Thing or String), path=" + self.getMetadata().getPath());
		}

		UserTask userTask = DataObject.getUserTask(self, actionContext);
		try{
		
			//---------------查询参数----------------
			Map<String, Object> datas = (Map<String, Object>) actionContext.get("conditionData");             //查询条件数据
			if(datas == null){
			    datas = Collections.emptyMap();
			}
			
			Object conditionConfig = actionContext.get("conditionConfig"); //查询配置
			if(conditionConfig == null){
			    conditionConfig = new Thing();
			}
			
			//分页信息
			Map<String, Object> pageInfo = (Map<String, Object>) actionContext.get("pageInfo");		
			
	
			//----------------生成SQL语句--------------
			//排序的列，一会要判断排序的列是否存在
			String sortField = null;
			String sortDir = null;
			if(pageInfo != null){
				sortField = (String) pageInfo.get("sort");
			    String dir = (String) pageInfo.get("dir");		    
			    if(dir != null && !"".equals(dir)){
			        sortDir = dir;
			    }
			}
	
			if(sortField == null || "".equals(sortField)){
			    //默认排序
			    sortField = self.getString("storeSortField");
			    sortDir = self.getString("storeSortDir");
			}
	
			List<Thing> attributes = (List<Thing>) self.get("attribute@");
			for(int i=0; i<attributes.size(); i++){
			    //过滤非数据字段和不需要显示的字段
			    if(!attributes.get(i).getBoolean("dataField")){
			    	attributes.remove(i);
			    	i--;
			    }
			}
			String sql = null;
			List<Map<String, Object>> cds = new ArrayList<Map<String, Object>>();
			String clause = DbUtil.getConditionSql(conditionConfig, actionContext, datas, cds);
			String querySql= self.getStringBlankAsNull("querySql");
			if(self.getBoolean("keepQuerySql") && querySql != null){
				sql = querySql;
			}else{
				sql = "select ";
				int atrCount = 0;
				//要选择的字段列表
				boolean haveSortField = false;
				for(int i=0; i<attributes.size(); i++){
				    if(atrCount > 0){
				       sql = sql + ",";
				    }
		
				    atrCount ++;
				    String fieldName = getFieldName(attributes.get(i)); 
				    sql = sql + fieldName;
				    
				    String name = attributes.get(i).getMetadata().getName();
				    if(!haveSortField && name.equals(sortField)){
				        haveSortField = true; //排序的列存在
				        sortField = fieldName;
				    }
				}
				/*
				if(!haveSortField){
				    sortField = null; //置为空，没有排序
				}*/
		
				//表名或子查询
				String tableName = UtilString.getString(self, "tableName", actionContext);//self.getString("tableName");
				if(querySql != null && !"".equals(querySql)){
				    tableName = querySql;
				}
				sql = sql + " from " + tableName;
		
				//查询条件
				
				if(clause != null && clause != ""){
				    sql = sql + " where " + clause;
				}
		
				//排序，排序的字段是否选择的列中呢？
				if(sortField  != null && !"".equals(sortField)){
				    sql = sql + " order by " + sortField;
				    if(sortDir != null){
				        sql = sql + " " + sortDir;
				    }
				}
			}
			
	
			if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
			    Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
			}
			UserTaskManager.setUserTaskLabelDetail(userTask, "sql setted", sql);
			
			//----------------生成SQL完毕--------------------
	
			//----------------执行查询-----------------------
			if(pageInfo != null && (Integer) pageInfo.get("limit") != 0){
			    //有分页查询
			    String dbType = (String) actionContext.get("dbType"); //dbType是有DataSource上下文设置的
			    if(dbType != null &&  !"".equals(dbType)){
			    	dbType = dbType.toLowerCase();
			        if("oracle".equals(dbType) || dbType.startsWith("oracle")){
			            self.doAction("iteratorOraclePage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes, "action", action}));
			            return;
			        }else if("derby".equals(dbType)){
			            self.doAction("iteratorDerbyPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes, "action", action}));
			            return;
			        }else if("mysql".equals(dbType)){
			            self.doAction("iteratorMysqlPage", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes, "action", action}));
			            return;
			        }else if("sqlserver2005".equals(dbType)){
			        	self.doAction("iteratorSqlServer2005Page", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "sql",sql, "attributes",attributes, "action", action}));
			        	return;
			        }
			    }
			    throw new Exception("dbType=" + dbType + ", not supported page query now");
			}else{
			    //没有分页查询
			    //设置参数值和查询
				Connection con = (Connection) actionContext.get("con");
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = null;
			    try{
			        //设置查询参数
			        self.doAction("setStatementParams", actionContext, UtilMap.toMap(new Object[]{"cds",cds, "pst",pst, "attributes",attributes, "index",1}));
			        
			        //执行sql
			        UserTaskManager.setUserTaskLabelDetail(userTask, "Statement prepared,  executing.....", sql);
			        rs = pst.executeQuery();
			        int index = 1;
			        
			        long start = System.currentTimeMillis();
			        while(rs.next()){
			            //构造对象
			        	DataObject data = new DataObject(self);
			            data.setInited(false);
			            //设置属性值
			            for(int i=0; i<attributes.size(); i++){
			                data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
			            }
			            
			            data.setInited(true);
			            
			            action.run(actionContext, UtilMap.toMap("index", index, "data", data));
			            index++;
			            //log.info("data=" + data);
			            
			            if(userTask != null && System.currentTimeMillis() - start > 1000){
			            	userTask.setLabel( index + " rows has iterated");
			            	start = System.currentTimeMillis();
			            }
			        }
			    }finally{
			        if(rs != null){
			            rs.close();
			        }
			        if(pst != null){
			            pst.close();
			        }
			    }
			}
		}finally{
			DataObject.userTaskFinished();
		}
		
	}
	
	public synchronized static void mapping2ddl(Thing dbDataObject, Thing dataSource, ActionContext actionContext) {
		//DataSource转hibernate config
		Thing cfg = new Thing("xworker.db.hibernate.hibernate_configuration");
		cfg.put("name", "DbDataObject2ddl");
		
		Thing factory = new Thing("xworker.db.hibernate.hibernate_configuration/@session_factory");
		cfg.addChild(factory);
		factory.put("name", "factory");
		String dbType = DataSourceActions.getDbType(dataSource);
		if("derby".equals(dbType)){
			factory.put("dialect", "org.hibernate.dialect.DerbyDialect");
		}else if("mysql".equals(dbType)){
			factory.put("dialect", "org.hibernate.dialect.MySQLDialect");
		}else if("oracle".equals(dbType)){
			factory.put("dialect", "org.hibernate.dialect.OracleDialect");
		}else if("sqlserver2005".equals(dbType)){
			factory.put("dialect", "org.hibernate.dialect.SQLServerDialect");
		}else if("sqlite".equals(dbType)){
			factory.put("dialect", "xworker.db.hibernate.dialects.SQLiteDialect");
		}else{
			factory.put("dialect","org.hibernate.dialect." + dbType + "Dialect");
		}
		factory.put("show_sql", "true");
		factory.put("format_sql", "true");
		factory.put("default_entity_mode", "dynamic-map");
		
		Thing conn = new Thing("xworker.db.hibernate.hibernate_configuration/@session_factory/@connection");
		factory.addChild(conn);
		conn.put("driver_class", (String) dataSource.doAction("getDriverClass", actionContext));
		conn.put("url", (String) dataSource.doAction("getUrl", actionContext));
		conn.put("username", (String) dataSource.doAction("getUserName", actionContext));
		conn.put("password", (String) dataSource.doAction("getPassword", actionContext));
		
		Thing ddl = new Thing("xworker.db.hibernate.hibernate_configuration/@session_factory/@hbm2ddl");
		factory.addChild(ddl);
		ddl.put("auto", "update");
		
		Thing mapping = new Thing("xworker.db.hibernate.hibernate_configuration/@dataObjectMappings");
		cfg.addChild(mapping);
		mapping.put("type", "multi");
		mapping.put("things", dbDataObject.getMetadata().getPath());
		
		cfg.doAction("deploy", actionContext);
		cfg.doAction("init", actionContext);
		cfg.doAction("close", actionContext);
	}
	
	/**
	 * DbDataObject通过Hibernate的ddl同步到数据库。
	 * 
	 * @param actionContext
	 */
	public synchronized static void mapping2ddl(ActionContext actionContext) {
		//数据对象和DataSource
		Thing self = (Thing) actionContext.get("self");
		Thing dataSource = DataSouceActionContextActions.getDataSource(self, self.getString("dataSource"), actionContext);
		if(dataSource == null){
			throw new DataObjectException("DbDataObject mapping2ddl: dataSource is null, thing=" + self.getMetadata().getPath());
		}
		
		mapping2ddl(self, dataSource, actionContext);
	}
	
	public static void menu_hibernateDDL(ActionContext actionContext){
		Thing currentThing = (Thing) actionContext.get("currentThing");
		currentThing.doAction("mapping2ddl", actionContext);
	}
	
	public static void doDDL(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		if(self.getBoolean("DDL")) {
			mapping2ddl(actionContext);
		}
	}
	
	/**
	 * 从ResultSet中读取数据。
	 * 
	 * @param actionContext
	 * @return
	 * @throws SQLException
	 */
	public static List<DataObject> loadFromResultSet(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		
		List<DataObject> datas = new ArrayList<DataObject>();
		List<Thing> attributes = self.getChilds("attribute");
		ResultSet rs = (ResultSet) actionContext.get("resultSet");
		while(rs.next()){
	        //构造对象
			DataObject theData = new DataObject(self);
	        theData.setInited(false);
	        
	        //设置属性值
	        for(int i=0; i<attributes.size(); i++){
	        	Thing attribute = attributes.get(i);
	        	if(attribute.getBoolean("dataField")){
	        		theData.put(attribute.getString("name"), DbUtil.getValue(rs, attribute));
	        	}
	        }
	        
	        theData.setInited(true);   
	        datas.add(theData);    
	    }
		
		return datas;
	}
	
	@SuppressWarnings("unchecked")
	public static void iteratorOraclePage(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		PageInfo pageInfo = (PageInfo) actionContext.get("pageInfo");
		Action action = (Action) actionContext.get("action");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Connection con = (Connection) actionContext.get("con");
			List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
			List<Thing> attributes = (List<Thing>) actionContext.get("attributes");
			
		    //查询总数
			String sql = (String) actionContext.get("sql");
		    String countSql = "select count(*) from (" + sql + ") ";
		    pst = con.prepareStatement(countSql);
		    
		    //设置查询参数
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    rs = pst.executeQuery();
		    rs.next();
		    pageInfo.setTotalCount(rs.getInt(1));
		    rs.close();
		    pst.close();
		    
		    //分页查询
		    sql = "select * from (select t.*, rownum rowno from (" + sql + ") t ) where rowno between ? and ?";
		    if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		        Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		    }
		    pst = con.prepareStatement(sql);
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    if(pageInfo != null && pageInfo.getLimit() != 0){
		        int index = cds.size() + 1;
		        int start = pageInfo.getStart() + 1;
		        pst.setInt(index, start);
		        pst.setInt(index + 1, start + pageInfo.getLimit() - 1);
		    }
		    
		    //执行sql
		    rs = pst.executeQuery();
		    iteratorResult(self, rs, action, actionContext);
		    
		}finally{
		    if(rs != null){
		        rs.close();
		    }
		    if(pst != null){
		        pst.close();
		    }
		}
	}

	public static void iteratorResult(Thing self, ResultSet rs, Action action, ActionContext actionContext) throws Exception{
		 List<Thing> attributes = self.getChilds("attribute"); //取查询数据对象实际的列
	    int index = 1;
	    while(rs.next()){
	        //构造对象
	        DataObject data = new DataObject(self);
	        data.setInited(false);
	        
	        //设置属性值
	        for(int i=0; i<attributes.size(); i++){
	        	Thing attribute = attributes.get(i);
	            if(!attribute.getBoolean("dataField")){
	                continue;
	            }
	            
	            try{
	                data.put(attribute.getString("name"), DbUtil.getValue(rs, attribute));
	            }catch(Exception e){
	                Executor.error(TAG, "get result value error: " + e.getMessage() + ": " + attribute);
	                throw e;
	            }
	        }
	        
	        data.setInited(true);
	        
	        action.run(actionContext, UtilMap.toMap("index", index, "data", data));
	        index++;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void iteratorDerbyPage(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		PageInfo pageInfo = (PageInfo) actionContext.get("pageInfo");
		Action action = (Action) actionContext.get("action");
		String sql = (String) actionContext.get("sql");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Connection con = (Connection) actionContext.get("con");
			List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
			List<Thing> attributes = (List<Thing>) actionContext.get("attributes");
			//查询总数
		    String countSql = "select count(*) from (" + sql + ") as t";
		    pst = con.prepareStatement(countSql);
		    
		    //设置查询参数
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes" ,attributes, "index", 1));
		    rs = pst.executeQuery();
		    rs.next();
		    pageInfo.setTotalCount(rs.getInt(1));
		    rs.close();
		    pst.close();
		    
		    //分页查询
		    sql = "SELECT * FROM ( SELECT ROW_NUMBER() OVER() AS rownum, t.*  FROM (" + sql + " ) t) AS tmp WHERE rownum >= ? AND rownum <= ?";
		    if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		        Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		    }
		    pst = con.prepareStatement(sql);
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    if(pageInfo != null && pageInfo.getLimit() != 0){
		        int index = cds.size() + 1;
		        int start = pageInfo.getStart() + 1;
		        pst.setInt(index, start);
		        pst.setInt(index + 1, start + pageInfo.getLimit() - 1);
		    }
		    
		    //执行sql
		    rs = pst.executeQuery();
		    iteratorResult(self, rs, action, actionContext);
		}finally{
		    if(rs != null){
		        rs.close();
		    }
		    if(pst != null){
		        pst.close();
		    }
		}
	}
		
	@SuppressWarnings("unchecked")
	public static void iteratorMysqlPage(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		PageInfo pageInfo = (PageInfo) actionContext.get("pageInfo");
		Action action = (Action) actionContext.get("action");
		String sql = (String) actionContext.get("sql");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Connection con = (Connection) actionContext.get("con");
			List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
			List<Thing> attributes = (List<Thing>) actionContext.get("attributes");
			//查询总数
		    String countSql = "select count(*) from (" + sql + ") as t";
		    pst = con.prepareStatement(countSql);
		    
		    //设置查询参数
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    rs = pst.executeQuery();
		    rs.next();
		    pageInfo.setTotalCount(rs.getInt(1));
		    rs.close();
		    pst.close();
		    
		    //分页查询
		    sql = sql + " limit ? ,?";
		    if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		        Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		    }
		    pst = con.prepareStatement(sql);
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    if(pageInfo != null && pageInfo.getLimit() != 0){
		        int index = cds.size() + 1;
		        int start = pageInfo.getStart() + 1;
		        pst.setInt(index, start-1);
		        pst.setInt(index + 1, pageInfo.getLimit());
		        //println("start=" + (start -1) + ",limit=" + (start + pageInfo.limit - 2));
		    }
		    
		    //执行sql
		    rs = pst.executeQuery();
		    
		    iteratorResult(self, rs, action, actionContext);
		}finally{
		    if(rs != null){
		        rs.close();
		    }
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void iteratorSqlServer2005Page(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		PageInfo pageInfo = (PageInfo) actionContext.get("pageInfo");
		Action action = (Action) actionContext.get("action");
		String sql = (String) actionContext.get("sql");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Connection con = (Connection) actionContext.get("con");
			List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
			List<Thing> attributes = (List<Thing>) actionContext.get("attributes");

			//查询总数
		    String countSql = "select count(*) from (" + sql + ") ";
		    pst = con.prepareStatement(countSql);
		    
		    //设置查询参数
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    rs = pst.executeQuery();
		    rs.next();
		    pageInfo.setTotalCount(rs.getInt(1));
		    rs.close();
		    pst.close();
		    
		    //分页查询
		    sql = "select * from (select t.*, ROW_NUMBER() over(order by (select 0)) as rowno from (" + sql + ") t ) where rowno between ? and ?";
		    if(self.getBoolean("showSql") || Executor.isLogLevelEnabled(TAG, Executor.DEBUG)){
		        Executor.info(TAG, self.getMetadata().getPath() + ":" + sql);
		    }
		    pst = con.prepareStatement(sql);
		    self.doAction("setStatementParams", actionContext, UtilMap.toMap("cds", cds, "pst", pst, "attributes", attributes, "index", 1));
		    if(pageInfo != null && pageInfo.getLimit() != 0){
		        int index = cds.size() + 1;
		        int start = pageInfo.getStart() + 1;
		        pst.setInt(index, start);
		        pst.setInt(index + 1, start + pageInfo.getLimit() - 1);
		    }
		    
		    //执行sql
		    rs = pst.executeQuery();
		    
		    iteratorResult(self, rs, action, actionContext);
		}finally{
		    if(rs != null){
		        rs.close();
		    }
		    if(pst != null){
		        pst.close();
		    }
		}
	}
	
	public static String getQuerySqlAndParams(ActionContext actionContext){
		//分页信息
		Map<String, Object> pageInfo =  actionContext.getObject("pageInfo");	
		List<Thing> attributes = actionContext.getObject("attributes");
		List<Map<String, Object>> cds = actionContext.getObject("cds");
		Thing self = actionContext.getObject("self");
		Object conditionConfig = actionContext.get("conditionConfig");
		Map<String, Object> datas = actionContext.getObject("datas");
		
		//排序的列，一会要判断排序的列是否存在
		String sortField = null;
		String sortDir = null;
		if(pageInfo != null){
			sortField = (String) pageInfo.get("sort");
		    String dir = (String) pageInfo.get("dir");		    
		    if(dir != null && !"".equals(dir)){
		        sortDir = dir;
		    }
		}

		if(sortField == null || "".equals(sortField)){
		    //默认排序
		    sortField = self.getString("storeSortField");
		    sortDir = self.getString("storeSortDir");
		}

			
		String sql = "";
		
		String querySql= self.getStringBlankAsNull("querySql");
		if(self.getBoolean("keepQuerySql") && querySql != null){
			sql = querySql;
		}else{
			sql = "select ";
			int atrCount = 0;
			//要选择的字段列表
			boolean haveSortField = false;
			for(int i=0; i<attributes.size(); i++){
			    if(atrCount > 0){
			       sql = sql + ",";
			    }
	
			    atrCount ++;
			    String fieldName = getFieldName(attributes.get(i)); 
			    sql = sql + fieldName;
			    
			    String name = attributes.get(i).getMetadata().getName();
			    if(!haveSortField && name.equals(sortField)){
			        haveSortField = true; //排序的列存在

			        Thing atrr = attributes.get(i);
			        String sortFiledName = atrr.getString("fieldName");
					
					if(sortFiledName == null || "".equals(sortFiledName)){
						sortFiledName = atrr.getString("name");
					}
			        sortField = sortFiledName;
			    }
			}
			if(!haveSortField){
			    sortField = null; //置为空，没有排序
			}
	
			//表名或子查询
			String tableName = UtilString.getString(self, "tableName", actionContext);//self.getString("tableName");
			
			if(querySql != null && !"".equals(querySql)){
				if(querySql.trim().startsWith("(")){
					tableName = querySql;
				}else{
					tableName = "(" + querySql + ") t";
				}
			}
			sql = sql + " from " + tableName;
		}
		
		//查询条件				
		String clause = DbUtil.getConditionSql(conditionConfig, actionContext, datas, cds);
		if(clause != null && clause != ""){
			int whereIndex = sql.indexOf("where"); 
			if(whereIndex == -1) {
				sql = sql + " where " + clause;
			}else {
				int rightKuoHaoIndex = sql.indexOf(")");
				if(rightKuoHaoIndex > whereIndex) {
					//where在括号内
					sql = sql + " where " + clause;
				}else {
					//where末尾的情况
					sql = sql + " and " + clause;
				}
			}
		}

		String sqlAppend = self.getStringBlankAsNull("sqlAppend");
		if(sqlAppend != null) {
			sql = sql + " " + sqlAppend;
		}
		//排序，排序的字段是否选择的列中呢？
		if(sortField  != null && !"".equals(sortField)){
		    sql = sql + " order by " + sortField;
		    if(sortDir != null){
		        sql = sql + " " + sortDir;
		    }
		}
		
		return sql;
	}
	
	
	/**
	 * 执行一个sql, 把第一条记录包装成数据对象并返回，如果没有则返回null。
	 * 
	 * 在本方法中数据源应该按照XWorker的Configuration设置。
	 * 
	 * @param dataSourceName 数据源名称
	 * @param configuration 配置模型
	 * @param sql
	 * @param actionContext
	 * @return
	 */
	public static DataObject loadSql(String dataSourceName, Thing configuration, String sql, ActionContext actionContext) {
		return loadSql(dataSourceName, configuration, sql, null, actionContext);
	}
	
	/**
	 * 执行一个sql, 把第一条记录包装成数据对象并返回，如果没有则返回null。
	 * 
	 * 在本方法中数据源应该按照XWorker的Configuration设置。
	 * 
	 * @param dataSourceName 数据源名称
	 * @param configuration 配置模型
	 * @param sql
	 * @param condition
	 * @param actionContext
	 * @return
	 */
	public static DataObject loadSql(String dataSourceName, Thing configuration, String sql, Condition condition, ActionContext actionContext) {
		Thing dataSource = Configuration.getConfiguration(dataSourceName, configuration, actionContext);
		
		return loadSql(dataSource, sql, condition, actionContext);
	}
	
	/**
	 * 执行一个sql, 把第一条记录包装成数据对象并返回，如果没有则返回null。
	 * 
	 * @param dataSource
	 * @param sql
	 * @param condition
	 * @param actionContext
	 * @return
	 */
	public static DataObject loadSql(Thing dataSource, String sql, Condition condition, ActionContext actionContext) {
		List<DataObject> datas = querySql(dataSource, sql, condition, actionContext);
		if(datas != null && datas.size() > 0) {
			return datas.get(0);
		}
		
		return null;
	}
	
	/**
	 * 执行一个sql, 把第一条记录包装成数据对象并返回，如果没有则返回null。
	 *  
	 * @param dataSource
	 * @param sql
	 * @param actionContext
	 * @return
	 */	
	public static DataObject loadSql(Thing dataSource, String sql, ActionContext actionContext) {
		return loadSql(dataSource, sql, actionContext);
	}
	
	/**
	 * 执行一个sql, 返回数据对象列表。
	 * 
	 * @param dataSource
	 * @param sql
	 * @param actionContext
	 * @return
	 */
	public static List<DataObject> querySql(Thing dataSource, String sql, ActionContext actionContext){
		return querySql(dataSource, sql, null, actionContext);
	}
	
	/**
	 * 执行一个sql, 返回数据对象列表。
	 * 
	 * @param dataSource
	 * @param sql
	 * @param condition
	 * @param actionContext
	 * @return
	 */
	public static List<DataObject> querySql(Thing dataSource, String sql, Condition condition, ActionContext actionContext){
		Thing queryDb = new Thing("xworker.dataObject.db.DbQueryDataObject");
		queryDb.set("dataSource", dataSource.getMetadata().getPath());
		queryDb.set("querySql", "var:sql");
		
		if(condition == null) {
			return queryDb.doAction("query", actionContext, "sql", sql);
		}else {
			return queryDb.doAction("query", actionContext, "sql", sql, 
				"conditionData", condition.getConditionValues(), "condition", condition.getConditionThing());
		}
	}
	
	/**
	 * 执行一个sql, 返回数据对象列表。在本方法中数据源应该按照XWorker的Configuration设置。
	 * 
	 * @param dataSourceName
	 * @param configuration
	 * @param sql
	 * @param condition
	 * @param actionContext
	 * @return
	 */
	public static List<DataObject> querySql(String dataSourceName, Thing configuration, String sql, Condition condition, ActionContext actionContext){
		Thing dataSource = Configuration.getConfiguration(dataSourceName, configuration, actionContext);
		
		return querySql(dataSource, sql, condition, actionContext);
	}
	
	/**
	 *  执行一个sql, 返回数据对象列表。在本方法中数据源应该按照XWorker的Configuration设置。
	 *  
	 * @param dataSourceName
	 * @param configuration
	 * @param sql
	 * @param actionContext
	 * @return
	 */
	public static List<DataObject> querySql(String dataSourceName, Thing configuration, String sql, ActionContext actionContext){
		return querySql(dataSourceName, configuration, sql, null, actionContext);
	}
}