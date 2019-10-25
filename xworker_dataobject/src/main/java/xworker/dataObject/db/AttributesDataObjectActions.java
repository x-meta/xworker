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
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

public class AttributesDataObjectActions {
    public static Object doQuery(ActionContext actionContext) throws SQLException{
        Thing self = actionContext.getObject("self");
        
        Connection con = actionContext.getObject("con");
        //获取全部的表
        List<Object> datas = new ArrayList<Object>();
        DatabaseMetaData metadata = con.getMetaData();
        String catalog = getConditionData(actionContext, "catalog");
        String schemaPattern = getConditionData(actionContext, "schemaPattern");
        String tableNamePattern = getConditionData(actionContext, "tableNamePattern");
        //String attributeNamePattern = getConditionData(actionContext, "attributeNamePattern");
        String columnNamePattern = getConditionData(actionContext, "columnNamePattern");
        ResultSet rs = metadata.getAttributes(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        try{    
            while(rs.next()){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("TYPE_CAT", rs.getString("TYPE_CAT"));
                data.put("TYPE_SCHEM", rs.getString("TYPE_SCHEM"));
                data.put("TYPE_NAME", rs.getString("TYPE_NAME"));        
                data.put("ATTR_NAME", rs.getString("ATTR_NAME"));
                data.put("DATA_TYPE", rs.getString("DATA_TYPE"));
                data.put("ATTR_TYPE_NAME", rs.getString("ATTR_TYPE_NAME"));
                data.put("ATTR_SIZE", rs.getString("ATTR_SIZE"));
                data.put("DECIMAL_DIGITS", rs.getString("DECIMAL_DIGITS"));
                data.put("NUM_PREC_RADIX", rs.getString("NUM_PREC_RADIX"));
                data.put("NULLABLE", rs.getString("NULLABLE"));
                data.put("REMARKS", rs.getString("REMARKS"));
                data.put("CHAR_OCTET_LENGTH", rs.getString("CHAR_OCTET_LENGTH"));
                data.put("ATTR_DEF", rs.getString("ATTR_DEF"));
                data.put("IS_NULLABLE", rs.getString("IS_NULLABLE"));
                data.put("SCOPE_CATLOG", rs.getString("SCOPE_CATLOG"));
                data.put("SCOPE_SCHEMA", rs.getString("SCOPE_SCHEMA"));
                data.put("SCOPE_TABLE", rs.getString("SCOPE_TABLE"));
                data.put("SOURCE_DATA_TYPE", rs.getString("SOURCE_DATA_TYPE"));
                data.put("ORDINAL_POSITION", rs.getString("ORDINAL_POSITION"));
               
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

    public static String getConditionData(ActionContext actionContext, String name){
        if(actionContext.get("conditionData") == null){
            return null;
        }else{
            try{
                Object v = OgnlUtil.getValue(name, actionContext.get("conditionData"));
                if(v instanceof String && "".equals(v)){
                    return null;
                }else{
                    return (String) v;
                }
            }catch(Exception e){
                return null;
            }
        }
    }
    
    public static Object isMappingAble(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        if(actionContext.get("con") == null){
            throw new ActionException("获取不到数据库连接，数据源不存在或没有正确设置。");
        }
        
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
    	List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
    	fields.add(UtilMap.toMap("colName", "TYPE_CAT", "colTitle", "类型的类别"));
    	fields.add(UtilMap.toMap("colName","TYPE_SCHEM", "colTitle","类型的模式"));
    	fields.add(UtilMap.toMap("colName","TYPE_NAME", "colTitle","类型名称"));
    	fields.add(UtilMap.toMap("colName","ATTR_NAME", "colTitle","属性名称"));
    	fields.add(UtilMap.toMap("colName","DATA_TYPE", "colTitle","数据类型"));
    	fields.add(UtilMap.toMap("colName","ATTR_TYPE_NAME", "colTitle","类型名称"));
    	fields.add(UtilMap.toMap("colName","ATTR_SIZE", "colTitle","列的大小"));
    	fields.add(UtilMap.toMap("colName","DECIMAL_DIGITS", "colTitle","小数位数"));
    	fields.add(UtilMap.toMap("colName","NUM_PREC_RADIX", "colTitle","基数"));
    	fields.add(UtilMap.toMap("colName","NULLABLE", "colTitle","是否允许使用 NULL"));
    	fields.add(UtilMap.toMap("colName","REMARKS", "colTitle","注释"));
    	fields.add(UtilMap.toMap("colName","ATTR_DEF", "colTitle","默认值"));
    	fields.add(UtilMap.toMap("colName","CHAR_OCTET_LENGTH", "colTitle","Char最大字节数"));
    	fields.add(UtilMap.toMap("colName","ORDINAL_POSITION", "colTitle","表中的列的索引"));
    	fields.add(UtilMap.toMap("colName","IS_NULLABLE", "colTitle","ISO 规则用于确定列是否包括 null"));
    	fields.add(UtilMap.toMap("colName","SCOPE_CATLOG", "colTitle","作用域类别"));
    	fields.add(UtilMap.toMap("colName","SCOPE_SCHEMA", "colTitle","作用域模式称"));
    	fields.add(UtilMap.toMap("colName","SCOPE_TABLE", "colTitle","作用域表名称"));
    	fields.add(UtilMap.toMap("colName","SOURCE_DATA_TYPE", "colTitle","不同类型或用户生成 Ref 类型"));
    	fields.add(UtilMap.toMap("colName","catalog", "colTitle","查询catalog"));
    	fields.add(UtilMap.toMap("colName","schemaPattern", "colTitle","查询schemaPattern"));
    	fields.add(UtilMap.toMap("colName","tableNamePattern", "colTitle","查询tableNamePattern"));
    	fields.add(UtilMap.toMap("colName","attributeNamePattern", "colTitle","查询attributeNamePattern"));
        
        return fields;
    }

}