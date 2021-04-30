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
package xworker.dataObject.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.PageInfo;
import xworker.lang.executor.Executor;

public class MapDataObjectActions {
	private static final String TAG = MapDataObjectActions.class.getName();
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getMapData(Thing self, ActionContext actionContext) throws OgnlException{
		  //获取数据实例
        String mapVarName = self.getStringBlankAsNull("mapVarName");
        if(mapVarName == null){
        	throw new ActionException("mapVarName is null, thing=" + self.getMetadata().getPath());
        }
        
        Map<String, Object> mapData = ( Map<String, Object>) OgnlUtil.getValue(mapVarName, actionContext);
        return mapData;
	}
	
	/**
	 * 获取数据对象中对应Map的key-value对。
	 * 
	 * @param self
	 * @param theData
	 * @param mapData
	 * @return
	 */
	private static Object[] getKeyValue(Thing self, DataObject theData, Map<String, Object> mapData){
		String key = null;
        Object value = null;
        for(Thing attribute : self.getChilds("attribute")) {
        	String propertyPath = attribute.getString("propertyPath"); 
        	if("key".equals(propertyPath)) {
        		key = theData.getString(attribute.getMetadata().getName());
        	}else if("value".equals(propertyPath)) {
        		value = theData.get(attribute.getMetadata().getName());
        	}
        }
        
        return new Object[] {key, value};
	}
	
	public static boolean doUpdate(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
        
        Map<String, Object> mapData = getMapData(self, actionContext);
        if(mapData == null) {
        	return false;
        }
        
        //要更新的数据对象
        DataObject theData = actionContext.getObject("theData");
        if(theData == null) {
        	return false;
        }
        
        Object datas[] = getKeyValue(self, theData, mapData);
      
        if(datas[0] != null) {
        	mapData.put(String.valueOf(datas[0]), datas[1]);
        	return true;
        }
        
		return false;
	}
	
	public static boolean doDelete(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
        
        Map<String, Object> mapData = getMapData(self, actionContext);
        if(mapData == null) {
        	return false;
        }
        
        //要更新的数据对象
        DataObject theData = actionContext.getObject("theData");
        if(theData == null) {
        	return false;
        }
        
        Object datas[] = getKeyValue(self, theData, mapData);
      
        if(datas[0] != null) {
        	mapData.remove(String.valueOf(datas[0]));
        	return true;
        }
        
		return false;
	}
	
	public static DataObject doCreate(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
        
        Map<String, Object> mapData = getMapData(self, actionContext);
        if(mapData == null) {
        	return null;
        }
        
        //要更新的数据对象
        DataObject theData = actionContext.getObject("theData");
        if(theData == null) {
        	return null;
        }
        
        Object datas[] = getKeyValue(self, theData, mapData);
      
        if(datas[0] != null) {
        	mapData.put(String.valueOf(datas[0]), datas[1]);
        	return theData;
        }
        
		return null;
	}
	
    @SuppressWarnings("unchecked")
	public static Object doQuery(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        //获取数据实例
        String mapVarName = self.getStringBlankAsNull("mapVarName");
        if(mapVarName == null){
        	throw new ActionException("mapVarName is null, thing=" + self.getMetadata().getPath());
        }
        
        Map<String, Object> mapData = ( Map<String, Object>) OgnlUtil.getValue(mapVarName, actionContext);
        List<Object> instance = new ArrayList<Object>();
        if(mapData == null){
            Executor.info(TAG, "map data is null,mapVarName=" + mapVarName);
            return Collections.EMPTY_LIST;
        }
        for(String key : mapData.keySet()){
            instance.add(UtilMap.toMap("key", key, "value", mapData.get(key)));
        }
        
        
        List<DataObject> matchedDatas = new ArrayList<DataObject>();
        //log.info("instance=" + instance);
        //从实例中查询匹配的数据
        if(actionContext.get("conditionConfig") == null){
            //没有条件，返回全部
            for(Object child : instance){        
                Object dobj = child;
                dobj = self.doAction("createDataObjectFromObject", actionContext, "data", child, "descriptor", self);
                //log.info("dobj=" + dobj);
                if(dobj != null){
                    matchedDatas.add((DataObject) dobj);
                }
            }
        }else{
            for(Object child : instance){
                Object dobj = child;
                dobj = self.doAction("createDataObjectFromObject", actionContext, "data", child, "descriptor", self);
        
                if(dobj != null){
                    boolean matched = (Boolean) ((Thing) actionContext.get("conditionConfig")).doAction("isMatch", 
                    		actionContext, "condition", actionContext.get("conditionData"), "data", dobj);        
                    //log.info("matched=" + matched);
                    if(matched){
                        matchedDatas.add((DataObject) dobj);
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
                pageInfo.setDatas( matchedDatas.subList((int) pageInfo.getStart(), toIndex));
            }else{
                pageInfo.setDatas(matchedDatas);
            }
            return pageInfo.get("datas");
        }else{
            if(actionContext.get("pageInfo") != null){
                pageInfo.setTotalCount( matchedDatas.size());
                pageInfo.setDatas( matchedDatas);
            }
            return matchedDatas;
        }
    }

    public static Object createDataObjectFromObject(ActionContext actionContext) throws OgnlException{
        return createDataObject(actionContext.get("data"), (Thing) actionContext.get("descriptor"));           
    }
    
    public static DataObject createDataObject(Object data, Thing descriptor) throws OgnlException{
        DataObject dataObj = new DataObject(descriptor);
        for(Thing attribute : descriptor.getChilds("attribute")){
            String name = attribute.getString("name");
            Object d = OgnlUtil.getValue(attribute.getString("propertyPath"), data);
            dataObj.put(name, d);
            //log.info(name + "=" + d);
        }   
        
        //初始化多个属性列表
        List<Thing> things = dataObj.getMetadata().getThings();
        for(int i=0; i<things.size(); i++){
            Thing refThing = things.get(i);
            if(things.get(i).getBoolean("many")){
            	DataObjectList list = new DataObjectList(refThing, dataObj);
                list.setInited(false);
                dataObj.put(refThing.getString("name"), list);
            }else{
            	DataObject dchild = new DataObject(refThing.getString("dataObjectPath"));
                dchild.put(refThing.getString("refAttributeName"), dataObj.get(refThing.getString("localAttributeName")));
                dchild.setInited(false);
                dataObj.put(refThing.getString("name"), dchild);
            }
        }      
        
        return dataObj;
    }

    public static Object isMappingAble(ActionContext actionContext){
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        
        //获取全部的表
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        datas.add(UtilMap.toMap("colName", "key", "colTitle", "键"));
        datas.add(UtilMap.toMap("colName", "value", "colTitle", "值"));
        
        return datas;
    }

    public static Object getMappingAttributeName(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        return "propertyPath";
    }

}