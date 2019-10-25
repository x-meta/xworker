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
package xworker.lang.actions.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class GroupCreator {
	private static Logger log = LoggerFactory.getLogger(GroupCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        //找到要分组的List数据
        String listName = self.getString("listName");
        Iterable<Object> listData = (Iterable<Object>) OgnlUtil.getValue(listName, actionContext);
        if(listData == null){
            log.info("GroupAction: listData  is null, listName=" + listName);
            return null;
        }
        
        //要分组的字段
        List<Map<String, Object>> gfields = new ArrayList<Map<String, Object>>();
        for(Thing child : (List<Thing>) self.get("GroupField@")){
            Map<String, Object> gf = new HashMap<String, Object>();
            gf.put("name", child.getString("name"));
            gf.put("fieldName", child.getString("groupFieldName"));
            if("date".equals(child.getString("groupType"))){
                gf.put("format", new SimpleDateFormat(child.getString("groupPattern")));
            }
            gfields.add(gf);
        }
        
        //进行分组
        Map<String, Object> groupedMap = new HashMap<String, Object>();
        for(Object data : listData){
            Map<String, Object> gmap = groupedMap;
            for(Map<String, Object> gf : gfields){
                Object value = "";
                String fieldName = (String) gf.get("fieldName");
                if(fieldName == null || "".equals(fieldName)){
                    log.warn("Group: group fieldName is null");
                    value = "fieldName is null";
                }else{
                    value = OgnlUtil.getValue(fieldName, data);
                }
                if(value != null && gf.get("format") != null && value instanceof Date){
                    value = ((SimpleDateFormat) gf.get("format")).format(value);
                }
                
                //log.info("value=" + value);
                Object nextMap = gmap.get(value);
                if(nextMap == null){
                    nextMap = new HashMap<String, Object>();
                    gmap.put(String.valueOf(value), nextMap);
                }
                
                gmap = (Map<String, Object>) nextMap;
            }
            
            //log.info("gmap=" + gmap);
            List<Object> list = (List<Object>) gmap.get("datas");
            if(list == null){
                list = new ArrayList<Object>();
                gmap.put("datas", list);
            }
            list.add(data);
        }
        
        //分组完毕，返回数据
        List<Object> groupedList = new ArrayList<Object>();
        //println groupedMap;
        getValues(groupedList, new HashMap<String, Object>(), gfields, groupedMap, 0);
        
        //log.info("groupedList=" + groupedList);
        String varName = self.getString("varName");        
        if(varName != null && !"".equals(varName)){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            bindings.put(varName, groupedList);
        }
        return groupedList;
    }

    @SuppressWarnings("unchecked")
	public static void getValues(List<Object> lists, Map<String, Object> dataMap, List<Map<String, Object>> gfields, Map<String, Object> groupedMap, int count){
        if(count < gfields.size()){
            //log.info("count=" + count + ",groupedMap=" + groupedMap);
            for(String key : groupedMap.keySet()){
                dataMap.put(String.valueOf(gfields.get(count).get("name")), key);
                 
                getValues(lists, dataMap, gfields, (Map<String, Object> ) groupedMap.get(key), count + 1);
            }
        }else{
        	List<Object>  datas = (List<Object> ) groupedMap.get("datas");
            Map<String, Object> d = new HashMap<String, Object>();
            d.putAll(dataMap);
            d.put("_data", datas);
            lists.add(d);        
        }
    }
}