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
package xworker.dataObject.thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class ThingDataObjectActions {
	private static final String TAG = ThingDataObjectActions.class.getName();
	
    public static Object doLoad(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        Thing descriptor = theData.getMetadata().getDescriptor();
        Object[][] keyDatas = theData.getKeyAndDatas();
        if(keyDatas == null || keyDatas.length == 0){
            Executor.warn(TAG, "no keys data cannot load, dataObjectPath=" + descriptor.getMetadata().getPath());
            throw new ActionException("No keys, data cannot laod");
        }
        
        Thing datas = (Thing) self.doAction("getInstances", actionContext);
        if(datas == null){
            Executor.warn(TAG, "no thing datas setted, dataObjectPath=" + descriptor.getMetadata().getPath());
            throw new ActionException("No thing datas setted");
        }
        
        synchronized(datas) {
	        Thing data = null;
	        for(Thing child : datas.getChilds()){
	            boolean have = false;
	            for(int i=0; i<keyDatas.length; i++){
	            	Object key1 = child.get(((Thing) keyDatas[i][0]).getString("name"));
	            	Object key2 =  keyDatas[i][1];
	            			
	                if(key1 != null && key1.equals(key2)){
	                   have = true;
	                   break;
	                }        
	            }
	            if(have){
	                 data = child;
	                 break;
	            }
	        }
	        
	        if(data != null){
	            for(String key : data.getAttributes().keySet()){
	                theData.put(key, data.get(key));
	            }
	            
	            return theData;
	        }else{
	            //log.info("数据对象不存在");
	            return null;
	        }
        }
    }

    public static Object doQuery(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //获取数据实例
        Thing instance = (Thing) self.doAction("getInstances", actionContext);
        
        List<DataObject> matchedDatas = new ArrayList<DataObject>();
        
        //log.info("conditionData=" + conditionData);
        //从实例中查询匹配的数据
        if(actionContext.get("conditionConfig") == null){
            //没有条件，返回全部
            for(Thing child : instance.getChilds()){
                DataObject dobj = (DataObject) self.doAction("createDataObjectFromThing", actionContext, "thing", child, "descriptor", self);
                if(dobj != null){
                    matchedDatas.add(dobj);
                }
            }
        }else{
        	Thing conditionConfig = actionContext.getObject("conditionConfig");
        	Object conditionData = actionContext.get("conditionData");
        	
            for(Thing child : instance.getChilds()){                
                Boolean matched = (Boolean) conditionConfig.doAction("isMatch", actionContext, "condition", conditionData, "data", child);        
                //log.info("thingName=" + child.getThingName() + ",conditionData=" + conditionData + ",matched=" + matched + ",provinceId=" + child.provinceId + ",cityId=" + child.cityId);
                if(matched){
                    DataObject dobj = (DataObject) self.doAction("createDataObjectFromThing", actionContext, "thing", child, "descriptor", self);
                    if(dobj != null){
                        matchedDatas.add(dobj);
                    }
                }        
            }
        }
        
        final PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        if(actionContext.get("pageInfo") != null){
            //是否排序
            if(pageInfo.getSort() != null && !"".equals(pageInfo.getSort())){
            	Collections.sort(matchedDatas, new Comparator<Object>(){
					@Override
					public int compare(Object o1, Object o2) {
						DataObject a = (DataObject) o1;
						DataObject b = (DataObject) o2;
						
						String av = (a == null ? null : a.getString((String) pageInfo.getSort()));
	                    String bv = (b == null ? null : b.getString((String) pageInfo.getSort()));
	                    if(av == null && bv == null){
	                        return 0;
	                    }else if(av == null && bv != null){
	                        return  "DESC".equals(pageInfo.getDir()) ? 1 : -1;
	                    }else if(av != null && bv == null){
	                        return "DESC".equals(pageInfo.getDir()) ? -1 : 1;
	                    }else{
	                        return "DESC".equals(pageInfo.getDir()) ? -av.compareTo(bv) : av.compareTo(bv);
	                    }            
					}
            	});
            
            }
            pageInfo.setTotalCount(matchedDatas.size());
            if(pageInfo.getLimit() > 0){
                if(pageInfo.getStart() > matchedDatas.size()){
                    pageInfo.setStart(matchedDatas.size());
                }
                int toIndex = (int) (pageInfo.getStart() + pageInfo.getLimit());
                if(toIndex > matchedDatas.size()){ 
                    toIndex = matchedDatas.size();
                }
                int startIndex = (int) pageInfo.getStart();
                if(startIndex < 0){
                    startIndex = 0;
                }    
                pageInfo.put("datas", matchedDatas.subList((int) pageInfo.getStart(), toIndex));
            }else{
                pageInfo.put("datas", matchedDatas);
            }
            return pageInfo.get("datas");
        }else{
            if(actionContext.get("pageInfo") != null){
                pageInfo.setTotalCount(matchedDatas.size());
                pageInfo.put("datas", matchedDatas);
            }
            return matchedDatas;
        }
    }

    public static void doUpdate(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing dataObjects = (Thing) self.doAction("getInstances", actionContext);
        DataObject theData = actionContext.getObject("theData");
        
        if(dataObjects == null) {
        	return;
        }
        
        synchronized(dataObjects) {
	        Object[][] keyDatas = theData.getKeyAndDatas();
	        boolean updated = false;
	        for(Thing child : dataObjects.getChilds()){
	        	boolean ok = true;
	            for(int i=0; i<keyDatas.length; i++){
	            	Object key1 = child.get(((Thing) keyDatas[i][0]).getString("name"));
	            	Object key2 =  keyDatas[i][1];
	            			
	                if(key1 != null && key1.equals(key2)){
	                	ok = false;
	                   break;
	                }        
	            }            
	            
	            if(ok){
	                child.getAttributes().putAll(theData);
	                //theData = child.doAction("toDataObject", actionContext);
	                theData.putAll(child.getAttributes());
	                updated = true;
	                
	                if(actionContext.get("result") != null){
	                    //result.success = true;
	                    //result.msg = "数据更新成功";
	                }
	                break;
	            }
	        }
	        
	        if(!updated && actionContext.get("result") != null){
	            //result.success = false;
	            //result.msg = "没有匹配的数据，更新失败";
	        }else{
	            dataObjects.save();
	        }
        }
    }

    public static Object doCreate(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");        
        Thing dataObjects = (Thing) theData.doAction("getInstances", actionContext);
        
        if(dataObjects == null) {
        	return null;
        }
        
        synchronized(dataObjects) {
	        Object[][] keyDatas = theData.getKeyAndDatas();
	        
	        boolean have = false;
	        for(Thing child : dataObjects.getChilds()){
	            boolean ok = true;
	            for(Object[] keyData : keyDatas){
	                if(keyData[1] != null && !keyData[1].toString().equals(child.getString(((Thing) keyData[0]).getString("name")))){
	                    ok = false;
	                    break;
	                }
	            }
	            
	            if(ok){        
	                have = true;
	                break;
	            }
	        }
	        
	        if(!have){        
	            Thing child = new Thing(self.getMetadata().getPath());
	            child.initDefaultValue();
	            child.getAttributes().putAll(theData);
	            dataObjects.addChild(child);
	            dataObjects.save();
	            
	            if(actionContext.get("result") != null){
	                //result.success = true;
	                //result.msg = "数据创建成功";
	            }
	            
	            return theData;
	        }else{
	            if(actionContext.get("result") != null){
	                //r/esult.success = false;
	            }            
	            throw new ActionException("数据重复，不能创建新数据");
	        }
        }
    }

    public static void doDelete(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        Thing dataObjects = (Thing) theData.doAction("getInstances", actionContext);
        if(dataObjects == null) {
        	return;
        }
        
        synchronized(dataObjects) {
	        Object[][] keyDatas = theData.getKeyAndDatas();
	        boolean deleted = false;
	        for(Thing child : dataObjects.getChilds()){
	            boolean ok = true;
	            for(Object[] keyData : keyDatas){
	                if(keyData[1] != null && !keyData[1].toString().equals(child.getString(((Thing) keyData[0]).getString("name")))){
	                    ok = false;
	                    break;
	                }
	            }
	            
	            if(ok){
	                dataObjects.removeChild(child);
	                deleted = true;
	                if(actionContext.get("result") != null){
	                    //result.success = true;
	                    //result.msg = "数据删除成功";
	                }
	                break;
	            }
	        }
	        
	        if(!deleted && actionContext.get("result") != null){
	            //result.success = true;
	            //result.msg = "没有匹配的记录";
	        }else{
	            dataObjects.save();
	        }
        }
    }

    public static Object getInstances(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        World world = World.getInstance();
        
        if(actionContext.get("theData") != null){
            return world.getThing(theData.getMetadata().getDescriptor().getString("dataObjects"));
        }else{
            return world.getThing(self.getString("dataObjects"));
        }
    }

    public static void updateBatch(ActionContext actionContext){
    	throw new ActionException("have not changed to java");
    	/*
        Thing self = actionContext.getObject("self");
        //获取数据实例
        Thing instance = (Thing) self.doAction("getInstances", actionContext);
        
        int count = 0;
        //从实例中查询匹配的数据
        if(actionContext.get("conditionConfig") == null){
            //没有条件，返回全部
            for(child in instance.childs){
                boolean ok = false;
                for(attrName in theData.getMetadata().getDirtyFields()){
                    def attribute = theData.getMetadata().getDefinition(attrName);
                    if(attribute != null){
                        child.put(attrName, theData.get(attrName));     
                        ok = true;          
                    }
                } 
                
                if(ok){
                    count++;
                }
            }
        }else{
            for(child in instance.childs){
                def matched = conditionConfig.doAction("isMatch", actionContext, ["condition":conditionData, "data":child]);        
                //log.info("matched=" + matched);
                if(matched){
                    boolean ok = false;
                    for(attrName in theData.getMetadata().getDirtyFields()){
                        def attribute = theData.getMetadata().getDefinition(attrName);
                        if(attribute != null){
                            child.put(attrName, theData.get(attrName));     
                            ok = true;          
                        }
                    } 
                    
                    if(ok){
                        count++;
                    }
                }        
            }
        }
        
        if(count > 0){
            instance.save();
        }
        
        return count;
        */
    }

    public static Object deleteBatch(ActionContext actionContext){
    	throw new ActionException("have not changed to java");
    	/*
        Thing self = actionContext.getObject("self");
        
        def dataObjects = self.doAction("getInstances", actionContext);
        def datas = self.doAction("query", actionContext);
        def count = 0;
        if(datas != null && datas.size() > 0){
            for(theData in datas){
                def keyDatas = theData.getKeyAndDatas();
                def deleted = false;
                for(child in dataObjects.getChilds()){
                    def ok = true;
                    for(keyData in keyDatas){
                        if(keyData[1] != null && keyData[1].toString() != child.getString(keyData[0].name)){
                            ok = false;
                            break;
                        }
                    }
                    
                    if(ok){
                        dataObjects.removeChild(child);       
                        count++;      
                        break;
                    }
                }
            }
            
            dataObjects.save();
        }
        
        return count;*/
    }

    public static Object generateId(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing instance = (Thing) self.doAction("getInstances", actionContext);
        if(instance != null){
        	synchronized(instance) {
	            Long seq = instance.getLong("seq", 0);
	            seq = seq + 1;
	            instance.set("seq", seq);
	            instance.save();
	            
	            return seq;
        	}
        }else{
            return null;
        }
    }

}