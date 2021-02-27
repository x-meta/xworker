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
package xworker.dataObject.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class StatisticsQueryDataObjectActions {
	private static final String TAG = StatisticsQueryDataObjectActions.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Object query(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //数据对象
        Thing dataObject = World.getInstance().getThing(self.getString("dataObject"));
        if(dataObject == null){
            Executor.info(TAG, "QueryDataObject: dataObject is null, thing=" + self.getMetadata().getPath());
            return null;
        }
        
        List<Object> datas = new ArrayList<Object>();
        try{
            Bindings bindings = actionContext.push(null);
            
            //首先数据对象查询，返回所有匹配的结果，不分页
            bindings.put("pageInfo", null);    
            datas = (List<Object>) dataObject.doAction("query", actionContext);
            //log.info("orginal datas=" + datas);
            
            //如果有记录，进行分组和汇总操作
            if(datas != null && datas.size() > 0){
                bindings.put("datas", datas);
                
                //分组
                Thing group = new Thing("xworker.lang.actions.data.Group");
                group.put("listName", "datas");
                
                boolean hasGroup = false;
                for(Thing attr : self.getChilds("attribute")){
                    if("group".equals(attr.getString("queryFieldType"))){
                        Thing groupField = new Thing("xworker.lang.actions.data.Group/@GroupField");
                        groupField.put("name", attr.get("name"));
                        groupField.put("label", attr.get("label"));
                        groupField.put("groupFieldName", attr.get("groupFieldName"));
                        groupField.put("groupType", attr.get("groupType"));
                        groupField.put("groupPattern", attr.get("groupPattern"));
                        group.addChild(groupField);
                        
                        hasGroup = true;
                    }
                }
                
                if(hasGroup){
                    datas = (List<Object>) group.doAction("run", actionContext);
                    bindings.put("datas", datas);
                }
                
                //聚合汇总
                Thing aggregate = new Thing("xworker.lang.actions.data.Aggregate");
                aggregate.put("listName", "datas");
                boolean hasAggregate = false;
                for(Thing attr : self.getChilds("attribute")){
                    if("aggregate".equals(attr.getString("queryFieldType"))){
                        Thing aggregateField = new Thing("xworker.lang.actions.data.Aggregate/@AggregateField");
                        aggregateField.put("name", attr.get("name"));
                        aggregateField.put("label", attr.get("label"));
                        aggregateField.put("aggregateFunction", attr.get("aggregateFunction"));
                        aggregateField.put("aggregateDistinct", attr.get("aggregateDistinct"));
                        aggregateField.put("aggregateType", attr.get("aggregateType"));
                        aggregateField.put("aggregateExpression", attr.get("aggregateExpression"));
                        aggregate.addChild(aggregateField);
                        
                        hasAggregate = true;
                    }
                }
                
                if(hasAggregate){
                    if(hasGroup){
                        aggregate.put("listName", "data._data");
                        for(Object data : datas){
                            bindings.put("data", data);
                            Map<String, Object> agg = (Map<String, Object>) aggregate.doAction("run", actionContext);
                            if(agg != null){
                                ((Map<String, Object>) data).putAll(agg);
                            }else{
                                Executor.warn(TAG, "Aggregate returns null");
                            }
                        }
                    }else{
                        DataObject agg = (DataObject) aggregate.doAction("run", actionContext);
                        if(agg != null){
                            datas.add(agg);
                        }
                    }
                }
            }else{
                //如果没有原始数据，但是有聚合属性，此时是要返回一条记录的
                boolean haveAggregate = false;
                Map<String, Object> data = new HashMap<String, Object>();
                for(Thing attr : self.getChilds("attribute")){
                    if("aggregate".equals(attr.getString("queryFieldType"))){
                         haveAggregate = true;
                         data.put(attr.getString("name"), 0);
                    }
                }
                
                if(haveAggregate){
                    datas.add(data);    
                }
            }    
        }finally{
            actionContext.pop();    
        }
        
        PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        if(actionContext.get("pageInfo") != null){
            //是否排序
            //log.info("sort");        	
            if(pageInfo.getSort() != null && !"".equals(pageInfo.getSort())){
                actionContext.peek().put("datas", datas);
                
                //排序        
                Thing sort = new Thing("xworker.lang.actions.data.Sort");
                sort.put("listName", "datas");
                sort.put("sortField", pageInfo.get("sort"));
                sort.put("dir", pageInfo.getDir());
                datas = (List<Object>) sort.doAction("run", actionContext);
            }
            
            pageInfo.setTotalCount(datas.size());
            if(pageInfo.getLimit() > 0){
                if(pageInfo.getStart() > datas.size()){
                    pageInfo.setStart( datas.size());
                }
                int toIndex = pageInfo.getStart() + pageInfo.getLimit();
                if(toIndex > datas.size()){ 
                    toIndex = datas.size();
                }
                int startIndex = pageInfo.getStart();
                if(startIndex < 0){
                    startIndex = 0;
                }    
                pageInfo.put("datas", datas.subList(pageInfo.getStart(), toIndex));
            }else{
                pageInfo.put("datas", datas);
            }
            
            pageInfo.put("datas", toObjects(self, (List<Object>) pageInfo.get("datas")));
            //log.info("pageInfo.datas=" + pageInfo.get("datas"));
            return pageInfo.get("datas");
        }else{
            if(actionContext.get("pageInfo") != null){
                pageInfo.put("totalCount", datas.size());
                pageInfo.put("datas", datas);
            }    
            
            //logger.info("datas=" + datas);
            List<Object> ds = toObjects(self, datas);
            //log.info("ds=" + ds);
            return ds;
        }
    }
    
    //由于聚合和分组返回的map，需要转换成DataObject，这样才能是引用对象等正确显示
    @SuppressWarnings("unchecked")
	public static List<Object> toObjects(Thing self, List<Object> datas) throws OgnlException{
        List<Thing> finalExpressions = new ArrayList<Thing>();
        for(Thing attr : self.getChilds("attribute")){
            if("finalExpression".equals(attr.getString("queryFieldType"))){
                 finalExpressions.add(attr);
            }
        }
            
        List<Object> ds = new ArrayList<Object>();
        for(Object data : datas){
        	DataObject dataObject = new DataObject(self);
        	Map<String, Object> dd = (Map<String, Object>) data;
            for(String key : dd.keySet()){
                dataObject.put(key, dd.get(key));
            }
            for(Thing fe : finalExpressions){
                dataObject.put(fe.getString("name"), OgnlUtil.getValue(fe.getString("finalExpression"), data));
            }
            ds.add(dataObject);
        }                
        return ds;
    }

}