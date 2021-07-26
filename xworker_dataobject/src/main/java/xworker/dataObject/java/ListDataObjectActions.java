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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.Ognl;
import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectConstants;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.QueryConfig;
import xworker.lang.executor.Executor;

public class ListDataObjectActions {
	private static final String TAG = ListDataObjectActions.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Object doLoad(ActionContext actionContext) throws OgnlException{
    	//数据对象定义
        Thing self = actionContext.getObject("self");
        //数据对象，theData为约定的名字
        DataObject theData = actionContext.getObject("theData");
        
        //数据对象的描述者，应该就是self变量
        Thing descriptor = theData.getMetadata().getDescriptor();
        //主键的定义和主键的值
        Object[][] keyDatas = theData.getKeyAndDatas();
        if(keyDatas == null || keyDatas.length == 0){
            Executor.warn(TAG, "no keys data cannot load, listDataObjectPath=" + descriptor.getMetadata().getPath());
            throw new ActionException("No keys, data cannot laod");
        }
        
        //获取要管理的List数据
        List<Object> datas = self.doAction("getListData", actionContext);
        if(datas == null){
            Executor.warn(TAG, "no thing datas setted, listDataObjectPath=" + descriptor.getMetadata().getPath());
            throw new ActionException("No thing datas setted");
        }
        
        //通过主键查找匹配的对象
        Object data = null;
        Object keyExp = Ognl.parseExpression(((Thing) keyDatas[0][0]).getString("propertyPath"));
        for(Object child : datas){
            boolean have = true;
            for(int i=0; i<keyDatas.length; i++){
                if(!keyDatas[i][1].equals(OgnlUtil.getValue(keyExp, child))){
                   have = false;
                   break;
                }        
            }
            if(have){
                 data = child;
                 break;
            }
        }
        
        if(data != null){
        	//根据attribute（属性的定义）来设置DataObject的值，向DataObject填充属性值
            for(Thing attribute : self.getChilds("attribute")){
                String propertyPath = attribute.getStringBlankAsNull("propertyPath");
                String name = attribute.getString("name");
                if(propertyPath ==null){
                    theData.put(name, data);
                }else{
                    theData.put(name, OgnlUtil.getValue(attribute, "propertyPath", data));
                }
            }
            
            return theData;
        }else{
            //log.info("数据对象不存在");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object doQuery(ActionContext actionContext){
    	//数据对象定义
        Thing self = actionContext.getObject("self");
        
        //获取数据实例
        List<Object> instance = self.doAction("getListData", actionContext);
        List<DataObject> matchedDatas = new ArrayList<>();
        QueryConfig queryConfig = actionContext.getObject("queryConfig");
        for(Object child : instance){
            Object dobj = child;
            if(self.getString("extends") == null || "".equals(self.getString("extends"))){
                dobj = self.doAction("createDataObjectFromObject", actionContext, "data", child, "descriptor", self);
            }
            if(dobj != null){
                DataObject dataObject = (DataObject) dobj;
                if(queryConfig.getCondition().matches((dataObject))){
                    matchedDatas.add((DataObject) dataObject);
                }
            }
        }
        
        //分页，如果pageInfo存在则分页，否则是返回符合条件的全部记录
        queryConfig.setQueryResults(matchedDatas);
        return queryConfig.getPageInfo().getDatas();
    }

    @SuppressWarnings("unchecked")
	public static Object doUpdate(ActionContext actionContext) throws OgnlException{
    	//数据对象定义
        Thing self = actionContext.getObject("self");
        //数据对象
        DataObject theData = actionContext.getObject("theData");
        
        //根据主键查找对应的对象
        List<Object> datas = self.doAction("getListData", actionContext);
        Object[][] keyDatas = theData.getKeyAndDatas();
        boolean updated = false;
        if(datas == null) {
        	return updated;
        }
        
        for(Object child : datas){
            boolean ok = true;
            for(Object[] keyData : keyDatas){
                if(keyData[1] != OgnlUtil.getValue(((Thing) keyData[0]).getString("propertyPath"), child)){
                    ok = false;
                    break;
                }
            }
        
            if(ok){
            	//如果存在则更新对应的对象
                for(Thing attribute : self.getChilds("attribute")){
                    //log.info(attribute.name + "=" + OgnlUtil.getValue(attribute.name, theData));
                    //log.info(attribute.name + "=" + theData.get(attribute.name));
                    if(attribute.getBoolean("propertyReadOnly")){
                        continue;
                    }
                    OgnlUtil.setValue(attribute.getString("propertyPath"), child, OgnlUtil.getValue(attribute.getString("name"), theData));
                }
        
                for(Thing attribute : self.getChilds("attribute")){
                    theData.put(attribute.getString("name"), OgnlUtil.getValue(attribute.getString("propertyPath"), child));
                }
        
                updated = true;
                break;
            }
        }
        
        return updated;
    }

    @SuppressWarnings("unchecked")
	public static Object doCreate(ActionContext actionContext) throws OgnlException{
    	//数据对象定义
        Thing self = actionContext.getObject("self");
        //数据对象
        DataObject theData = actionContext.getObject("theData");
        
        //查找同主键的对象是否已经存在
        List<Object> dataObjects = self.doAction("getListData", actionContext);
        if(dataObjects == null){
            Executor.warn(TAG, "ListDataObject: list data not exists, name=" + self.get("listData"));
            return null;
        }
        
        Object[][] keyDatas = theData.getKeyAndDatas();
        boolean have = false;
        for(Object child : dataObjects){
            boolean ok = true;
            for(Object[] keyData : keyDatas){
                if(keyData[1] != null && keyData[1] != OgnlUtil.getValue(((Thing) keyData[0]).getString("propertyPath"), child)){
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
        	//对象不存在，创建新的对象，并从
            Object child = self.doAction("createInstance", actionContext);
            for(Thing attribute : self.getChilds("attribute")){
            	OgnlUtil.setValue(attribute.getString("propertyPath"), child, OgnlUtil.getValue(attribute.getString("name"), theData));
            }            
        }else{
            throw new ActionException("数据重复，不能创建新数据");
        }
        
        return theData;
    }

    @SuppressWarnings("unchecked")
	public static Object doDelete(ActionContext actionContext) throws OgnlException{
    	//数据对象定义
        Thing self = actionContext.getObject("self");
        //数据对象
        DataObject theData = actionContext.getObject("theData");
        
        //根据主键查找对象
        List<Object> datas = self.doAction("getListData", actionContext);
        Object[][] keyDatas = theData.getKeyAndDatas();
        boolean deleted = false;
        for(Object child : datas){
            boolean ok = true;
            for(Object[] keyData : keyDatas){
                if(keyData[1] != null && keyData[1] != OgnlUtil.getValue(((Thing) keyData[0]).getString("propertyPath"), child)){
                    ok = false;
                    break;
                }
            }
            
            if(ok){
            	//存在则删除该对象
                datas.remove(child);
                deleted = true;
                break;
            }
        }
        
        return deleted;
    }

    public static Object createInstance(ActionContext actionContext) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        if(actionContext.get("theData") != null && theData.get(DataObjectConstants.LIST_ORGINAL_DATA) != null){
            return theData.get(DataObjectConstants.LIST_ORGINAL_DATA);
        }
        
        Class<?> cls = Class.forName(self.getString("dataClassName"));
        return cls.newInstance();
    }

    public static Object toJavaObject(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        //需要有数据对象的实例theData
        if(actionContext.get("theData") == null){
            Executor.warn(TAG, "data object instance 'theData' not exists.");
            return null;
        }
        
        //创建实例
        Object obj = self.doAction("createInstance", actionContext);
        
        //属性赋值
        for(Thing attribute : self.getChilds("attribute")){
        	String propertyPath = attribute.getStringBlankAsNull("propertyPath");
            if(propertyPath != null){
            	OgnlUtil.setValue(propertyPath, obj, OgnlUtil.getValue(attribute.getString("name"), theData));
            }
        }
        
        //返回对象
        return obj;
    }

    public static Object createDataObjectFromObject(ActionContext actionContext){
        //log.info("descriptor=" + descriptor);
        return createDataObject(actionContext.get("data"), (Thing) actionContext.get("descriptor"));
    }

    public static DataObject createDataObject(Object data, Thing descriptor){
    	DataObject dataObj = new DataObject(descriptor);
        for(Thing attribute : descriptor.getChilds("attribute")){
            String name = attribute.getString("name");
            String propertyPath = attribute.getStringBlankAsNull("propertyPath");
            if(propertyPath == null){
                //log.warn("ListDataObject: propertyPath is null, attribute=" + name);
                dataObj.put(name, data);
                continue;
            }
            
            try{
                Object d = OgnlUtil.getValue(attribute, "propertyPath", data);
                dataObj.put(name, d);
            }catch(Exception e){
                //log.info("ListDataObject: get value error", e);
            }
            //log.info(name + "=" + d);
        }   
        
        //初始化多个属性列表
        List<Thing> things = dataObj.getMetadata().getThings();
        for(int i=0; i<things.size(); i++){
            Thing refThing = things.get(i);
            if(refThing.getBoolean("many")){
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
        
        dataObj.put(DataObjectConstants.LIST_ORGINAL_DATA, data);
        dataObj.setSource(data);
        return dataObj;
    }
    
    /**
     * 把对象包装成数据对象。
     * 
     * @param actionContext
     * @return
     */
    public static Object wrap(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	Object object = actionContext.getObject("object");
    	
    	return createDataObject(object, self);
    }
    
    public static void updateBatch(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        List<Object> dataObjects = (List<Object>) self.doAction("getInstances", actionContext);
        List<DataObject> datas = (List<DataObject>) self.doAction("query", actionContext);
        int count = 0;
        if(datas != null && datas.size() > 0){
            for(DataObject data : datas){
                for(String[] attrNames : theData.getMetadata().getDirtyFields()){
                	for(String attrName : attrNames){
	                    Thing attribute = theData.getMetadata().getDefinition(attrName);
	                    if(attribute != null){
	                        OgnlUtil.setValue(attribute.getString("propertyPath"), child, OgnlUtil.getValue(attribute.name, theData));
	                    }
                	}
                }
                count++;
            }
            
            dataObjects.save();
        }
        
        return count;
        */
    }

    public static void deleteBatch(ActionContext actionContext){
    	/*
        Thing self = actionContext.getObject("self");
        
        DataObject theData = actionContext.getObject("theData");
        if(theData == null){
            theData = new DataObject(self);
        }
        
        List<Object> datas = (List<Object>) OgnlUtil.getValue(self.getString("listData"), actionContext);
        int count = 0;
        if(datas != null && datas.size() > 0){
            for(Object theData : datas){
                def keyDatas = theData.getKeyAndDatas();
                def deleted = false;
                for(int i=0; i<datas.size(); i++){
                    def child = datas.get(i);
                    def ok = true;
                    for(keyData in keyDatas){
                        if(keyData[1] != null && keyData[1].toString() != child.getString(keyData[0].name)){
                            ok = false;
                            break;
                        }
                    }
                    
                    if(ok){
                        data.remove(child);
                        count++;      
                        i--;
                        break;
                    }
                }
            }
        }
        
        return count;*/
    }

    public static Object getMappingAttributeName(ActionContext actionContext){
        return "propertyPath";
    }

    public static Object isMappingAble(ActionContext actionContext){
        return true;
    }

    public static Object getMappingFields(ActionContext actionContext) throws ClassNotFoundException{
        Thing self = actionContext.getObject("self");
        
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        String dataClassName = self.getStringBlankAsNull("dataClassName");
        if(dataClassName != null){
            //获取类
            Class<?> cls = Class.forName(self.getString("dataClassName"));
            for(Field field : cls.getDeclaredFields()){
                datas.add(UtilMap.toMap("colName", field.getName(), "colTitle", field.getName()));
            }
        }else{
            throw new ActionException("dataCalssName is null");
        }
        
        return datas;
    }

    public static Object getListData(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        try{
            return OgnlUtil.getValue(self, "listData", actionContext);
        }catch(Exception e){
            Executor.error(TAG, "get list data error, path=" + self.getMetadata().getPath(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public static Object getData(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        //获取原始对象
        return theData.get(DataObjectConstants.LIST_ORGINAL_DATA);
    }

    public static void updateData(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        DataObject theData = actionContext.getObject("theData");
        
        Object obj = theData.get(DataObjectConstants.LIST_ORGINAL_DATA);
        for(Thing attribute : self.getChilds("attribute")){
            //log.info(attribute.name + "=" + OgnlUtil.getValue(attribute.name, theData));
            //log.info(attribute.name + "=" + theData.get(attribute.name));
            if(attribute.getBoolean("propertyReadOnly")){
                continue;
            }
            OgnlUtil.setValue(attribute.getString("propertyPath"), obj, OgnlUtil.getValue(attribute.getString("name"), theData));
        }
        
        for(Thing attribute : self.getChilds("attribute")){
            theData.put(attribute.getString("name"), OgnlUtil.getValue(attribute.getString("propertyPath"), obj));
        }
    }
    
	@SuppressWarnings("unchecked")
	public static void iterator(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		Object ac = actionContext.get("action");
		Action action = null;
		if(ac instanceof Action){
			action = (Action) ac;
		}else if(ac instanceof Thing){
			action = ((Thing) ac).getAction();
		}else if(ac instanceof String){
			action = World.getInstance().getAction((String) ac);
		}else{
			throw new ActionException("Iterator: action is null or not a (Action、Thing or String), path=" + self.getMetadata().getPath());
		}
		
		List<DataObject> datas = (List<DataObject>) ListDataObjectActions.doQuery(actionContext);
		if(datas != null){
			for(int i=0; i < datas.size(); i++){
				action.run(actionContext, UtilMap.toMap("index", (i + 1), "data", datas.get(i)));
			}
		}
	}

}