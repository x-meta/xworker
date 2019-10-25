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

public class CatalogsDataObjectActions {
    public static Object doQuery(ActionContext actionContext) throws SQLException{
        Thing self = actionContext.getObject("self");        
        Connection con = actionContext.getObject("con");
        
        //获取全部的表
        List<Object> datas = new ArrayList<Object>();
        DatabaseMetaData metadata = con.getMetaData();
        ResultSet rs = metadata.getCatalogs();
        try{    
            while(rs.next()){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("TABLE_CAT", rs.getString("TABLE_CAT"));
               
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
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
        fields.add(UtilMap.toMap("colName", "TABLE_CAT", "colTitle", "表类别"));
        
        return fields;
    }

}