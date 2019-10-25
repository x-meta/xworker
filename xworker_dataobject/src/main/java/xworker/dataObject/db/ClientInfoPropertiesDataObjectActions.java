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
import org.xmeta.util.UtilMap;

public class ClientInfoPropertiesDataObjectActions {
    public static Object doQuery(ActionContext actionContext) throws SQLException{
    	Thing self = actionContext.getObject("self");        
        Connection con = actionContext.getObject("con");
        
        //获取全部的表
        List<Object> datas = new ArrayList<Object>();
        DatabaseMetaData metadata = con.getMetaData();
        ResultSet rs = metadata.getClientInfoProperties();
        try{    
            while(rs.next()){
            	Map<String, Object> data = new HashMap<String, Object>();
                data.put("NAME", rs.getString("NAME"));
                data.put("MAX_LEN", rs.getString("MAX_LEN"));
                data.put("DEFAULT_VALUE", rs.getString("DEFAULT_VALUE"));        
                data.put("DESCRIPTION", rs.getString("DESCRIPTION"));       
               
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

    public static Object isMappingAble(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        if(actionContext.get("con") == null){
            throw new ActionException("获取不到数据库连接，数据源不存在或没有正确设置。");
        }
        
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext){
    	List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
    	fields.add(UtilMap.toMap("colName","NAME", "colTitle","名称"));
    	fields.add(UtilMap.toMap("colName","MAX_LEN", "colTitle","最大长度"));
    	fields.add(UtilMap.toMap("colName","DEFAULT_VALUE", "colTitle","默认值"));
    	fields.add(UtilMap.toMap("colName","DESCRIPTION", "colTitle","描述"));
        
        return fields;
    }

}