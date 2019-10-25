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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class ColumnDataObjectActions {
    public static Object doQuery(ActionContext actionContext) throws SQLException{
    	Thing self = actionContext.getObject("self");        
        Connection con = actionContext.getObject("con");
        
        //获取全部的表
        List<Object> datas = new ArrayList<Object>();
        DatabaseMetaData metadata = con.getMetaData();
        String catalog = AttributesDataObjectActions.getConditionData(actionContext, "catalog");
        String schemaPattern = AttributesDataObjectActions.getConditionData(actionContext, "schemaPattern");
        String tableNamePattern = AttributesDataObjectActions.getConditionData(actionContext, "tableNamePattern");
        String columnNamePattern = AttributesDataObjectActions.getConditionData(actionContext, "columnNamePattern");
        ResultSet rs = metadata.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        try{    
            while(rs.next()){
            	Map<String, Object> data = new HashMap<String, Object>();
                data.put("TABLE_CAT", getData(rs, "TABLE_CAT"));
                data.put("TABLE_SCHEM", getData(rs, "TABLE_SCHEM"));
                data.put("TABLE_NAME", getData(rs, "TABLE_NAME"));        
                data.put("COLUMN_NAME", getData(rs, "COLUMN_NAME"));
                data.put("DATA_TYPE", getData(rs, "DATA_TYPE"));
                data.put("TYPE_NAME", getData(rs, "TYPE_NAME"));
                data.put("COLUMN_SIZE", getData(rs, "COLUMN_SIZE"));
                data.put("DECIMAL_DIGITS", getData(rs, "DECIMAL_DIGITS"));
                data.put("NUM_PREC_RADIX", getData(rs, "NUM_PREC_RADIX"));
                data.put("NULLABLE", getData(rs, "NULLABLE"));
                data.put("REMARKS", getData(rs, "REMARKS"));
                data.put("CHAR_OCTET_LENGTH", getData(rs, "CHAR_OCTET_LENGTH"));
                data.put("ORDINAL_POSITION", getData(rs, "ORDINAL_POSITION"));
                data.put("IS_NULLABLE", getData(rs, "IS_NULLABLE"));
                data.put("SCOPE_CATLOG", getData(rs, "SCOPE_CATLOG"));
                data.put("SCOPE_SCHEMA", getData(rs, "SCOPE_SCHEMA"));
                data.put("SCOPE_TABLE", getData(rs, "SCOPE_TABLE"));
                data.put("SOURCE_DATA_TYPE", getData(rs, "SOURCE_DATA_TYPE"));
                data.put("IS_AUTOINCREMENT", getData(rs, "IS_AUTOINCREMENT"));
                datas.add(data);
            }
        }finally{
            rs.close();
        }
        
        String listDataName = "__datas__";
        self.set("listData", listDataName);
        self.set("dataClassName", "java.util.HashMap");
        
        try{
            Bindings bindings = actionContext.push(null);
            bindings.put(listDataName, datas);
        
            //使用ListDataObject的load方法    
            Action action = World.getInstance().getAction("xworker.dataObject.java.ListDataObject/@actions1/@query");
            
            return action.run(actionContext);
        }finally{
            actionContext.pop();
        }
    }

    
    public static Object getData(ResultSet rs, String name){
        try{
            return rs.getString(name);
        }catch(Exception e){
            return null;
        }
    }
    
    public static Object isMappingAble(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext){
    	List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
    	fields.add(UtilMap.toMap("colName","TABLE_CAT", "colTitle","表类别"));
    	fields.add(UtilMap.toMap("colName","TABLE_SCHEM", "colTitle","表模式"));
    	fields.add(UtilMap.toMap("colName","TABLE_NAME", "colTitle","表名称"));
    	fields.add(UtilMap.toMap("colName","COLUMN_NAME", "colTitle","列名称"));
    	fields.add(UtilMap.toMap("colName","DATA_TYPE", "colTitle","数据类型"));
    	fields.add(UtilMap.toMap("colName","TYPE_NAME", "colTitle","类型名称"));
    	fields.add(UtilMap.toMap("colName","COLUMN_SIZE", "colTitle","列的大小"));
    	fields.add(UtilMap.toMap("colName","DECIMAL_DIGITS", "colTitle","小数位数"));
    	fields.add(UtilMap.toMap("colName","NUM_PREC_RADIX", "colTitle","基数"));
    	fields.add(UtilMap.toMap("colName","NULLABLE", "colTitle","是否允许使用 NULL"));
    	fields.add(UtilMap.toMap("colName","REMARKS", "colTitle","注释"));
    	fields.add(UtilMap.toMap("colName","COLUMN_DEF", "colTitle","默认值"));
    	fields.add(UtilMap.toMap("colName","CHAR_OCTET_LENGTH", "colTitle","Char最大字节数"));
    	fields.add(UtilMap.toMap("colName","ORDINAL_POSITION", "colTitle","表中的列的索引"));
    	fields.add(UtilMap.toMap("colName","IS_NULLABLE", "colTitle","ISO 规则用于确定列是否包括 null"));
    	fields.add(UtilMap.toMap("colName","SCOPE_CATLOG", "colTitle","作用域类别"));
    	fields.add(UtilMap.toMap("colName","SCOPE_SCHEMA", "colTitle","作用域模式称"));
    	fields.add(UtilMap.toMap("colName","SCOPE_TABLE", "colTitle","作用域表名称"));
    	fields.add(UtilMap.toMap("colName","SOURCE_DATA_TYPE", "colTitle","不同类型或用户生成 Ref 类型"));
    	fields.add(UtilMap.toMap("colName","IS_AUTOINCREMENT", "colTitle","是否自动增加"));
    	fields.add(UtilMap.toMap("colName","catalog", "colTitle","查询catalog"));
    	fields.add(UtilMap.toMap("colName","schemaPattern", "colTitle","查询schemaPattern"));
    	fields.add(UtilMap.toMap("colName","tableNamePattern", "colTitle","查询tableNamePattern"));
    	fields.add(UtilMap.toMap("colName","columnNamePattern", "colTitle","查询columnNamePattern"));
        
        return fields;
    }

}