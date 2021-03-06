/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.dataObject.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.DbUtil;
import xworker.lang.executor.Executor;

public class DbDataObjectActions {
	private static final String TAG = DbDataObjectActions.class.getName();
	
    public static Object doLoad(ActionContext actionContext) throws SQLException{
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        Connection con = actionContext.getObject("con");
        
        Object[][] keyDatas = theData.getKeyAndDatas();
        if(keyDatas == null || keyDatas.length == 0){
            Executor.warn(TAG, "no keys data cannot load, dataObjectPath=" + theData.getMetadata().getDescriptor().getMetadata().getPath());
            throw new ActionException("No keys, data cannot load");
        }
        
        //生成insert sql语句
        List<Thing> rattributes = theData.getMetadata().getAttributes();
        List<Thing> attributes = new ArrayList<Thing>();
        for(int i=0; i<rattributes.size(); i++){
            if(!rattributes.get(i).getBoolean("dataField")){
                continue;
            }
            
            attributes.add(rattributes.get(i));
        }
        
        String sql = "select ";
        for(int i=0; i<attributes.size(); i++){
            if(!attributes.get(i).getBoolean("dataField")){
                continue;
            }
            String fieldSql = attributes.get(i).getString("sql");
            if(fieldSql != null && !"".equals(fieldSql)){
                sql = sql + fieldSql;
            }else{
                sql = sql + attributes.get(i).getString("fieldName");
            }
            if(i < attributes.size() - 1){
                sql = sql + ",";
            }
        }
        String tableName = self.getString("tableName");
        String querySql = self.getStringBlankAsNull("querySql");
        if(querySql != null){
            tableName = querySql;
        }
        sql = sql + " from " + tableName + " where ";
        for(int i=0; i<keyDatas.length; i++){
            sql = sql + ((Thing) keyDatas[i][0]).get("fieldName") + "=?";
            if(i < keyDatas.length -1){
                sql = sql + " and ";
            }
        }
        if(self.getBoolean("showSql")){
        	Executor.info(TAG, sql);
        }
        
        //设置参数值
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            pst = con.prepareStatement(sql);
            int index = 1;
            for(int i=0; i<keyDatas.length; i++){
                //log.info(keyDatas[i][0].get("name") + "=" + keyDatas[i][1]);
                DbUtil.setParameterValue(pst, index + i, ((Thing) keyDatas[i][0]).getString("type"), keyDatas[i][1]);
            }
            
            //执行sql
            rs = pst.executeQuery();
            if(rs.next()){
                //构造对象
                theData.setInited(false);
                
                //设置属性值
                for(int i=0; i<attributes.size(); i++){
                    theData.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
                }
                
                theData.setInited(true);   
                return theData;    
            }else{
                return null;
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

    public static Object generateSeqId(ActionContext actionContext) throws SQLException, Exception{
    	Thing self = actionContext.getObject("self");
        Connection con = actionContext.getObject("con");
        
        String seqName = self.getStringBlankAsNull("sequenceName");
        if(seqName == null || seqName ==""){
            if("native".equals(self.getString("idGenerateType"))){
                //native是通过hibernate生成的
                seqName = "HIBERNATE_SEQUENCE";
            }
        }
        if(seqName == null || seqName ==""){
            throw new Exception("SequenceName is null");
        }
        
        int dotIndex = seqName.indexOf(".");
        if(dotIndex != -1){
        	//使用从表中读取Seq的方法
        	String tableName = seqName.substring(0, dotIndex);
        	String sName = seqName.substring(dotIndex + 1, seqName.length());
        	
        	ResultSet rs = null;
        	PreparedStatement ps = null;
        	try{
        		ps = con.prepareStatement("select seq from " + tableName + " where name = ? for update");
        		ps.setString(1, sName);
        		rs = ps.executeQuery();
        		if(rs.next()){
        			int seq = rs.getInt("seq");
        			
        			rs.close();
        			ps.close();
        			
        			ps = con.prepareStatement("update " + tableName + " set seq = seq +1 where name=?");
        			ps.setString(1, sName);
        			ps.execute();
        			
        			return seq +1;
        		}else{
        			rs.close();
        			ps.close();
        			
        			ps = con.prepareStatement("insert into " + tableName + "(name, seq) values(?, 1)");
        			ps.setString(1, sName);
        			ps.execute();
        			return 1;
        		}
        	}finally{
        	    if(rs != null){
        	        rs.close();
        	    }
        	    if(ps != null){
        	        ps.close();
        	    }
        	}
        }else{
            String driverName = con.getMetaData().getDriverName();
            if(driverName != null && driverName.indexOf("Oracle") != -1){
                //oracle数据库
                String sql = "select " + seqName + ".nextval as nextID from dual";
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = null;
                try{
                    rs = pst.executeQuery();
                    if(rs.next()){
                        return rs.getInt("nextID");
                    }else{
                        throw new Exception("cannot get seq from " + self.getString("sequenceName") + ", thing=" + self.getMetadata().getPath());
                    }
                }finally{
                     if(rs != null){
                         rs.close();
                     }
                     if(pst != null){
                         pst.close();
                     }
                }
            }else{
            	Executor.warn(TAG, "database driver " + driverName + " not supported sequence");
                //throw new Exception("database driver " + driverName + " not supported sequence");
            }
        }
        
        return null;
    }

    public static Object generateNativeId(ActionContext actionContext) throws SQLException{
    	Thing self = actionContext.getObject("self");
        Connection con = actionContext.getObject("con");
        
        String driverName = con.getMetaData().getDriverName();
        if(driverName != null && driverName.indexOf("Oracle") != -1){
        	return self.doAction("generateSeqId", actionContext);     
        }else{
            return null;
        }
    }

    public static void getCreateTableSql(ActionContext actionContext){
    	/*
    	Thing self = actionContext.getObject("self");
    	DataObject theData = actionContext.getObject("theData");
        Connection con = actionContext.getObject("con");
        
        String sqlType = "oracle";
        String tableName = self.getString("tableName");
        //数据库字段
        String sql = "create table " + tableName;
        for(Thing attr : self.getChilds("attribute")){
            if(attr.getBoolean("dataField")){
                String columnName = attr.getString("fieldName");
                String columnType = "";
                String type = attr.getStringBlankAsNull("type");
                if(type == null){
                    type = "string";
                }
                String length = attr.length;
                def precision = attr.precision;
                def primaryKey = attr.key;
                switch(type){
                    case "string":
                        columnType = "
                    case "byte":
                    case "short":
                    case "int":
                    case "long":
                    case "float":
                    case "double":
                    case "boolean":
                    case "date":
                    case "datetime":
                    case "time":
                    case "byte[]":
                    case "stream":            
                }
                if(primaryKey == ""){
                }
               
            }
        }*/
    }

    public static Object getDropTableSql(ActionContext actionContext){
        return null;    
    }

    public static void getOracleSqlType(ActionContext actionContext){
        Thing attribute = actionContext.getObject("attribute");
        
        String type = attribute.getStringBlankAsNull("type");
        if(type == null){
            type = "string";
        }
        /*
        String length = attr.geString("length");
        def precision = attr.precision;
        def primaryKey = attr.key;
        */
    }

    public static void getDerbySqlType(ActionContext actionContext){
    	/*未完成
        Thing self = actionContext.getObject("self");
        switch(type){
            case "string":
                return "varchar(" + length + ")";
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "date":
            case "datetime":
            case "time":
            case "byte[]":
            case "stream":    
        }*/
    }

	public static Object queryOraclePage(ActionContext actionContext) throws Exception{
    	String sql = actionContext.getObject("sql");
        String countSql = "select count(1) from (" + sql + ") ";
        String querySql = "select * from (select t.*, rownum rowno from (" + sql + ") t ) where rowno between ? and ?";
        
        return DbDataObjectActions.queryPage(countSql, querySql, DataObjectUtil.PAGE_BETWEEN, actionContext);              
    }

	public static Object queryDerbyPage(ActionContext actionContext) throws Exception{
    	String sql = actionContext.getObject("sql");
        String countSql = "select count(1) from (" + sql + ") as t";
        String querySql = "SELECT * FROM ( SELECT ROW_NUMBER() OVER() AS rownum, t.*  FROM (" + sql + " ) t) AS tmp WHERE rownum >= ? AND rownum <= ?";
        
        return DbDataObjectActions.queryPage(countSql, querySql, DataObjectUtil.PAGE_BETWEEN, actionContext);      
    }

    public static Object queryMysqlPage(ActionContext actionContext) throws Exception{
    	String sql = actionContext.getObject("sql");
        String countSql = "select count(1) from (" + sql + ") as t";
        String querySql = sql + " limit ? ,?";
        
        return DbDataObjectActions.queryPage(countSql, querySql, DataObjectUtil.PAGE_LIMIT, actionContext);        
    }
    
    public static Object queryCursorPage(ActionContext actionContext) throws Exception{
    	Thing self = actionContext.getObject("self");
        Connection con = actionContext.getObject("con");
        
        Object cds = actionContext.get("cds");
        List<Thing> attributes = actionContext.getObject("attributes");
        
        PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        String sql = actionContext.getObject("sql");
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            //查询总数
        	boolean hasTotalCount = false;
        	if(self.getBoolean("noTotalCount") == false){
	            String countSql = "select count(*) from (" + sql + ") t";
	            if(self.getBoolean("showSql")){
	            	Executor.info(TAG, countSql);
	            }
	            pst = con.prepareStatement(countSql);
	            
	            //设置查询参数
	            self.doAction("setStatementParams", actionContext, "cds", cds, "pst", pst, "attributes", attributes, "index", 1);
	            rs = pst.executeQuery();
	            rs.next();
	            pageInfo.setTotalCount(rs.getInt(1));
	            rs.close();
	            pst.close();
	            
	            hasTotalCount = true;
        	}else{
        		//pageInfo.setTotalCount((pageInfo.getPage() + 1) * pageInfo.getLimit());
        	}
            
            //分页查询
            if(self.getBoolean("showSql")){
            	Executor.info(TAG, sql);
            }
            
            boolean supportScroll = true;
            try{
            	pst = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }catch(SQLException e){
            	if(pst != null){
            		pst.close();
            	}
            	pst = con.prepareStatement(sql);
            	supportScroll = false;
            }
            self.doAction("setStatementParams", actionContext, "cds", cds, "pst", pst, "attributes", attributes, "index", 1);
  
            //执行sql
            rs = pst.executeQuery();
            List<DataObject> ds = new ArrayList<DataObject>();
            attributes = self.getChilds("attribute"); //取查询数据对象实际的列
            int start = pageInfo.getStart() + 1;
            int count = 0;
            boolean hasNext = false;
            if(supportScroll){
            	try{
            		hasNext = rs.absolute(start);
            	}catch(Exception e){
            		supportScroll = false;
            	}
            }
            
            if(!supportScroll){
            	int c = 1;
            	while(c < start && rs.next()){
            		c++;
            	}
            }
            
        	while(count < pageInfo.getLimit()){
        		if(supportScroll){
        			if(!hasNext){
        				break;
        			}        				

        		}else if(!rs.next()){
        			break;
        		}
                //构造对象
            	DataObject data = new DataObject(self);
                data.setInited(false);
                
                //设置属性值
                for(int i=0; i<attributes.size(); i++){
                    if(!attributes.get(i).getBoolean("dataField")){
                        continue;
                    }
                    
                    try{
                        data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
                    }catch(Exception e){
                    	Executor.error(TAG, "get result value error: " + e.getMessage() + ": " + attributes.get(i));
                        throw e;
                    }
                }
                
                data.setInited(true);
                
                ds.add(data);   
                count++;
                
                if(supportScroll){
                	//游标滚动是在末尾next，而无游标的是在前面
                	hasNext = rs.next();
                }
        	}
            
        	if(hasTotalCount == false){
	        	if(ds.size() < pageInfo.getLimit()){
	        		pageInfo.setTotalCount(pageInfo.getPage() * pageInfo.getLimit() + ds.size());
	        	}else{
	        		pageInfo.setTotalCount((pageInfo.getPage() + 1) * pageInfo.getLimit());
	        	}
        	}
        	/*
        	if(supportScroll){
	            //rs.last(); Oracle查询数量大时会缓存数据到内存，造成此方法几乎不会结束，所以先注释掉，等待更好的方法
	            //pageInfo.setTotalCount(rs.getRow());        		
        		pageInfo.setTotalCount(rs.getRow() + 100);
        	}else{
        		pageInfo.setTotalCount(Integer.MAX_VALUE);
        	}*/
            
            //log.info("data=" + ds);
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

	public static Object querySqlServer2005Page(ActionContext actionContext) throws Exception{
        String sql = actionContext.getObject("sql");
        String countSql = "select count(*) from (" + sql + ") ";
        String querySql = "select * from (select t.*, ROW_NUMBER() over(order by (select 0)) as rowno from (" + sql + ") t ) where rowno between ? and ?";
        
        return DbDataObjectActions.queryPage(countSql, querySql, DataObjectUtil.PAGE_BETWEEN, actionContext);
    }
        
	public static Object queryPage(String countSql, String querySql, int pageType, ActionContext actionContext) throws Exception{
    	Thing self = actionContext.getObject("self");
        Connection con = actionContext.getObject("con");
        
        Object cds = actionContext.get("cds");
        
        PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        
        return DataObjectUtil.dbQueryPage(self, con, cds, pageInfo, pageType, countSql, querySql, actionContext);        
    }
    
    public static void menu_reverse(ActionContext actionContext){
    	/*
    	Thing self = actionContext.getObject("self");
    	DataObject theData = actionContext.getObject("theData");
        Connection con = actionContext.getObject("con");
        
        def shell = event.widget.display.activeShell;
        def self = currentThing;
        def dataSource = world.getThing(self.dataSource);
        if(dataSource == null){
            def box = new MessageBox(shell, SWT.NONE);
            box.setText("操作信息");
            box.setMessage("数据源不存在！");
            box.open();
            return;
        }
        
        if(self.tableName == null || self.tableName == ""){
            def box = new MessageBox(shell, SWT.NONE);
            box.setText("操作信息");
            box.setMessage("没有设置表名！");
            box.open();
            return;
        }
        
        def columnMThing = world.getThing("xworker.db.jdbc.metadata.Columns");
        def columns = columnMThing.doAction("query", actionContext, ["dataSource":dataSource, "tableNamePattern": self.tableName.toUpperCase()]);
        for(column in columns){
            switch(column.dataType){
                case Types.ARRAY_LOCATOR:
                    break;
                case Types.BIGINT:
                    break;
                case Types.BINARY:
                    break;
                case Types.BIT:
                    break;
                case Types.BLOB_LOCATOR:
                    break;
                case Types.CHAR:
                    break;
                case Types.CLOB_LOCATOR:
                    break;
                case Types.DATE:
                    break;
                case Types.DECIMAL:
                    break;
                case Types.DISTINCT:
                    break;
                case Types.DOUBLE:
                    break;
                case Types.FLOAT:
                    break;
                case Types.INTEGER:
                    break;
                case Types.JAVA_OBJECT:
                    break;
                case Types.LONGVARBINARY:
                    break;
                case Types.LONGVARCHAR:
                    break;
                case Types.NULL:
                    break;
                case Types.NUMERIC:
                    break;
                case Types.OTHER:
                    break;
                case Types.REAL:
                    break;
                case Types.REF:
                    break;
                case Types.SMALLINT:
                    break;
                case Types.STRUCT:
                    break;
                case Types.STRUCT_LOCATOR:
                    break;
                case Types.TIME:
                    break;
                case Types.TIMESTAMP:
                    break;
                case Types.TINYINT:
                    break;
                case Types.VARBINARY:
                    break;
                case Types.VARCHAR:
                    break;
            }
            println column.columnName;
        }
        */
    }

    public static Object toHibernate(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
        
        //Category
        Thing thing = new Thing("xworker.db.hibernate.hibernate-mapping");
        thing.set("name", self.get("name"));
        
        //Class-entity
        Thing cls = new Thing("xworker.db.hibernate.hibernate-mapping/@class");
        cls.set("entity-name", self.get("name"));
        cls.set("table", self.get("tableName"));
        if(self.getStringBlankAsNull("comment") != null){
            Thing comment = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.class/@comment");
            comment.set("_value", self.getString("comment"));
            cls.addChild(comment);
        }
        
        //id
        Thing keyThing = null;  //目前XWorker只支持单个key的Hibernate，如果有多个key，会在ddl中创建
        for(Thing attr : self.getChilds("attribute")){
            if(attr.getBoolean("dataField") == false){
                continue;
            }
            
            if(attr.getBoolean("key")){
                Thing id = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.class/@id");
                id.set("name", attr.get("name"));
                //id.set("column", attr.get("fieldName"));
                id.set("type", getType(attr.getString("type")));
                id.set("length", attr.get("length"));

                if(attr.getStringBlankAsNull("comment") != null){
                    Thing column = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.id/@column1");
                    column.set("name", attr.get("fieldName"));
                    id.addChild(column);

                    Thing comment = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.class/@comment");
                    comment.set("_value", attr.getString("comment"));
                    column.addChild(comment);
                }

                if(attr.getStringBlankAsNull("generator") != null){
                    Thing gen = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.id/@generator");
                    gen.set("class", attr.get("generator"));
                    id.addChild(gen);
                }
                cls.addChild(id);
                keyThing = attr;


                break;
            }
        }
        
        //attribute
        for(Thing attr : self.getChilds("attribute")){
            if(attr.getBoolean("dataField") == false){
                continue;
            }
            
            if(attr != keyThing){//!attr.getBoolean("key")){
                Thing property = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.class/@property");
                property.set("name", attr.get("name"));
                //property.set("column", attr.get("fieldName"));
                property.set("type", getType(attr.getString("type")));
                property.set("length", attr.get("length"));
                property.set("precision", attr.get("precision"));
                property.set("index", attr.get("index"));
                property.set("unique-key", attr.getString("unique-key"));
                cls.addChild(property);

                if(attr.getStringBlankAsNull("comment") != null){
                    Thing column = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.id/@column1");
                    column.set("name", attr.get("fieldName"));
                    property.addChild(column);

                    Thing comment = new Thing("xworker.db.hibernate.hibernate-mapping-nodes.class/@comment");
                    comment.set("_value", attr.getString("comment"));
                    column.addChild(comment);
                }
            }
        }    
        
        thing.addChild(cls);
        return thing;
    }
    
    public static String getType(String type){
        if("datetime".equals(type)){
            return "timestamp";
        }else{
            return type;
        }
    }

    public static Object isMappingAble(ActionContext actionContext){
        Connection con = actionContext.getObject("con");
        
        if(actionContext.get("con") == null || con == null){
            throw new ActionException("获取不到数据库连接，数据源不存在或没有正确设置。");
        }
        
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext) throws SQLException{
    	Thing self = actionContext.getObject("self");
    	Connection con = actionContext.getObject("con");
        
        //获取全部的表
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        DatabaseMetaData metadata = con.getMetaData();
        String tableName = self.getStringBlankAsNull("tableName");
        if(tableName != null){
        	//设置了表名
	        ResultSet rs = metadata.getColumns(null, null, tableName, null);
	        getColumns(rs, datas);
	        if(datas.size() == 0){
	            //转化为大写重新读一次，比如oracle数据库有时表名是小写读不出来，大写可以
	            rs = metadata.getColumns(null, null, tableName.toUpperCase(), null);
	            getColumns(rs, datas);
	        }
	        if(datas.size() == 0){
	            //转化为小写重新读一次
	            rs = metadata.getColumns(null, null, tableName.toLowerCase(), null);
	            getColumns(rs, datas);
	        }
	        if(datas.size() == 0){
	        	PreparedStatement pst = null;
	        	try{
	        		pst = con.prepareStatement("select * from " + tableName);
	        		getColumnsFromMetadata(pst.getMetaData(), datas);
	        	}finally{
	        		if(pst != null){
	        			pst.close();
	        		}
	        	}
	        }	        
        }else{
        	String sql = self.getStringBlankAsNull("querySql");
        	if(sql != null){
        		PreparedStatement pst = null;
	        	try{
	        		pst = con.prepareStatement(sql);
	        		getColumnsFromMetadata(pst.getMetaData(), datas);
	        	}finally{
	        		if(pst != null){
	        			pst.close();
	        		}
	        	}
        	}
        }
        
        return datas;
        
        
    }
    
    public static void  getColumns(ResultSet rs, List<Map<String, Object>> columns) throws SQLException{
        try{    
            while(rs.next()){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("colName", rs.getString("COLUMN_NAME"));
                data.put("colTitle", rs.getString("REMARKS"));
                //数据类型
                data.put("type", DbUtil.getXWorkerType(rs.getInt("DATA_TYPE"), rs.getInt("DECIMAL_DIGITS")));
                columns.add(data);
            }
        }finally{
            rs.close();
        }
    }
    
    public static void  getColumnsFromMetadata(ResultSetMetaData meta, List<Map<String, Object>> columns) throws SQLException{
    	for(int i=1; i<=meta.getColumnCount(); i++){
    		Map<String, Object> data = new HashMap<String, Object>();
            data.put("colName", meta.getColumnName(i));
            data.put("colTitle", meta.getColumnLabel(i));
            data.put("size", meta.getColumnDisplaySize(i) * 5);
            //数据类型
            data.put("type", DbUtil.getXWorkerType(meta.getColumnType(i), meta.getScale(i)));
            columns.add(data);
    	}
   }
    

    public static Object getMappingAttributeName(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        return "fieldName";
    }

}