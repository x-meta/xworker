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
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class DataObjectQueryConfigActions {
	private static final String TAG = DataObjectQueryConfigActions.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Object query(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //数据对象
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Executor.info(TAG, "DataObjectQueryConfig: dataObject is null, thing=" + self.getMetadata().getPath());
            return null;
        }
        
        //查看是否是聚合和分组
        Map<String, Object> conditionData = (Map<String, Object>) actionContext.get("conditionData");
        String aggregateColumnStr = actionContext.getObject("aggregateColumns");
        if(aggregateColumnStr == null && actionContext.get("conditionData") != null){
            aggregateColumnStr = (String) (conditionData.get("aggregateColumns"));
        }
        String groupColumnStr = actionContext.getObject("groupColumns");
        if(groupColumnStr == null && actionContext.get("conditionData") != null){
            groupColumnStr = (String) conditionData.get("groupColumns");
        }
        
        List<Thing> aggregateColumns = getValidAggregateColumns(self, aggregateColumnStr);
        List<Thing> groupColumns = getValidGroupColumns(self, groupColumnStr);
        boolean isAggregate = aggregateColumns.size() > 0 && groupColumns.size() > 0;
        
        PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        //log.info("query static=" + isAggregate + ",aggregateColumnStr=" + aggregateColumnStr + ",groupColumnStr=" + groupColumnStr);
        if(!isAggregate){
            //不是分组聚合查询的普通查询调用DataObject查询
            if(actionContext.get("pageInfo") != null){
                pageInfo.put("dynamicDataObject", dataObject);
            }
            return dataObject.doAction("query", actionContext);
        }else{    
            //有分组查询的生成新的数据对象
            dataObject = getAggregateDataObject(self, aggregateColumns, groupColumns);
            //log.info("queryDataObjectPath=" + dataObject.metadata.path);
        
            //动态数据对象，用于调用者动态生成界面等
            if(actionContext.get("pageInfo") != null){
                pageInfo.put("dynamicDataObject", dataObject);
            }
        
            return dataObject.doAction("query", actionContext);
        }
        
        
    }
    
    public static Thing getAggregateDataObject(Thing selfObj, List<Thing> aggregateColumns, List<Thing >groupColumns){
        //先从缓存中取
        String key = "aggregate:";
        for(int i=0; i<aggregateColumns.size(); i++){
            Thing aggregate = aggregateColumns.get(i);
            key = key + "," + aggregate.getString("name");
        }
        key  = key + ",group:";
        for(int i=0; i<groupColumns.size(); i++){
            Thing group = groupColumns.get(i);
            key = key + "," + group.getString("name");
        }
        
        //log.info("key=" + key);
        Thing dataObject1 = (Thing) selfObj.getData(key);
        if(dataObject1 != null && (Long) selfObj.getData(key + "LastModify") == selfObj.getMetadata().getLastModified()){
            //log.info("dataObject is not null, return from cache");
            return dataObject1;
        }
        
        
        //创建新的查询对象
        Thing dataObject = new Thing("xworker.dataObject.query.StatisticsQueryDataObject");
        dataObject.set("dataObject", selfObj.getString("dataObject"));
        for(int i=0; i<aggregateColumns.size(); i++){
            Thing aggregate = aggregateColumns.get(i);
            Thing agg = aggregate.detach();
            agg.put("descriptors", "xworker.dataObject.query.StatisticsQueryDataObject/@attribute");
            //def agg = new Thing();        
            //agg.name = aggregate.name;
            //agg.label = aggregate.label;
            //agg.aggregateFunction = aggregate.aggregateFunction;
            //agg.aggregateDistinct = aggregate.aggregateDistinct;
            //agg.aggregateType = aggregate.aggregateType;
            //agg.aggregateExpression = aggregate.aggregateExpression;
            //log.info(" agg.aggregateExpression=" + agg.aggregateExpression);
            agg.put("queryFieldType", "aggregate");
            
            dataObject.addChild(agg);
        }
    
         for(int i=0; i<groupColumns.size(); i++){
            Thing group = groupColumns.get(i);
            Thing g = group.detach();
            g.put("descriptors", "xworker.dataObject.query.StatisticsQueryDataObject/@attribute");
            //def g = new Thing("xworker.dataObject.query.StatisticsQueryDataObject/@attribute");
            //g.name = group.name;
            //g.label = group.label;
            //g.groupFieldName = group.groupFieldName;
            //g.groupType = group.groupType;
            //g.groupPattern = group.groupPattern;
            g.put("queryFieldType", "group");
            //log.info("g.filedName=" + group.groupFieldName);
            dataObject.addChild(g);
        }
        
        for(Thing thing : selfObj.getChilds("thing")){
            //子事物，通常是关联事物
            dataObject.addChild(thing.detach());
        }
        
        selfObj.setData(key, dataObject);
        selfObj.setData(key + "LastModify", selfObj.getMetadata().getLastModified());
        return dataObject;
    }
    
    /**
     * 返回有效的分组列。
     */
    public static List<Thing> getValidGroupColumns(Thing dataObject, String groupColumns){
        List<Thing> myGroupColumns = dataObject.getChilds("group");
        List<Thing> validGroupColumns = new ArrayList<Thing>();
        if(groupColumns != null && !"".equals(groupColumns)){
            //获取匹配的聚合列
            String[] ags = groupColumns.split("[,]");
            for(int i=0; i<ags.length; i++){
                String ag = ags[i];
                for(int n=0; n<myGroupColumns.size(); n++){
                    if(ag == myGroupColumns.get(n).getString("name")){
                        validGroupColumns.add(myGroupColumns.get(n));
                    }
                }
            }
        }
        
        return validGroupColumns;
    }
    
    /**
     * 返回有效的聚合列。
     */
    public static List<Thing> getValidAggregateColumns(Thing dataObject, String aggregateColumns){
        List<Thing> myAggregateColumns = dataObject.getChilds("aggregate");
        List<Thing> validAggregateColumns = new ArrayList<Thing>();
        if(aggregateColumns == null || "" == aggregateColumns){
            //在没有指定的情况下，返回第一个，如果有
            if(myAggregateColumns.size() > 0){            
                validAggregateColumns.add(myAggregateColumns.get(0));
            }
        }else{
            //获取匹配的聚合列
            String[] ags = aggregateColumns.split("[,]");
            for(int i=0; i<ags.length; i++){
                String ag = ags[i];
                for(int n=0; n<myAggregateColumns.size(); n++){
                    if(ag == myAggregateColumns.get(n).getString("name")){
                        validAggregateColumns.add(myAggregateColumns.get(n));
                    }
                }
            }
        }
        
        return validAggregateColumns;
    }

}