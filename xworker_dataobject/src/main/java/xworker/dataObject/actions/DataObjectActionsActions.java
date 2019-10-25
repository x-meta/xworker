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
package xworker.dataObject.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.util.UtilData;

public class DataObjectActionsActions {
	private static Logger logger = LoggerFactory.getLogger(DataObjectActionsActions.class);
	
    public static Object run(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) OgnlUtil.getValue(self.getString("dataObjectName"), actionContext);
        if(dataObject == null){
            logger.info("DataObjectActions: dataObject is null, path=" + self.getMetadata().getPath());
            return "failure";
        }
        
        String method = self.getStringBlankAsNull("method");
        if("laod".equals(method)){
            Object obj = dataObject.load(actionContext);
            if(obj != null){
                return "success";
            }else{
                return "failure";
            }
        }else if("create".equals(method)){
            dataObject.create(actionContext);
        }else if("update".equals(method)){
            dataObject.update(actionContext);
        }else if("delete".equals(method)){
            dataObject.delete(actionContext);
        }
        
        return "success";
    }

    public static Object run1(ActionContext actionContext) throws OgnlException{
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //数据对象
        Thing dataObject = null;
        try{
            dataObject = (Thing) OgnlUtil.getValue(self.getString("dataObject"), actionContext);
        }catch(Exception e){
        }
        if(dataObject == null){
            dataObject = world.getThing(self.getString("dataObject"));
        }
        if(dataObject == null){
            logger.info("DataObjectDeleteBatch: dataObject is null, action=" + self.getMetadata().getPath());
            return 0;
        }
        
        //查询配置
        Thing queryConfig = null;
        String queryConfigs = self.getStringBlankAsNull("queryConfig");
        if(queryConfigs != null){
            try{
                queryConfig = (Thing) OgnlUtil.getValue(queryConfigs, actionContext);
            }catch(Exception e){
            }
            if(queryConfig == null){
                queryConfig = world.getThing(queryConfigs);
            }
        }else{
            queryConfig = self.getThing("QueryConfig@0");
        }
        if(queryConfig == null){
            logger.info("DataObjectDeleteBatch: queryConfig is null, action=" + self.getMetadata().getPath());
            return 0;
        }
        
        //查询参数
        Object queryParams = OgnlUtil.getValue(self.getString("queryParams"), actionContext);
        if(queryParams == null){
            logger.info("DataObjectDeleteBatch: queryParams is null, action=" + self.getMetadata().getPath());
            return 0;
        }
        
        //执行批量删除
        return dataObject.doAction("deleteBatch", actionContext, "conditionConfig", queryConfig, "conditionData", queryParams);
    }

    public static Object run2(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) actionContext.get(self.getString("dataObjectVar"));
        if(dataObject == null){
            logger.info("dataobject is null, thing=" + self);
            return null;
        }
        
        //创建
        Object theData = dataObject.doAction("create", actionContext);
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), theData, actionContext);
        
        //返回值
        return theData;
    }

    public static Object run3(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) actionContext.get(self.getString("dataObjectVar"));
        if(dataObject == null){
            logger.info("dataobject is null, thing=" + self);
            return null;
        }
        
        //更新
        Object result = dataObject.doAction("update", actionContext);
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), result, actionContext);
        
        //返回值
        return result;
    }

    public static Object run4(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) actionContext.get(self.getString("dataObjectVar"));
        if(dataObject == null){
            logger.info("dataobject is null, thing=" + self);
            return null;
        }
        
        //装载
        Object theData = dataObject.doAction("load", actionContext);
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), theData, actionContext);
        
        //返回值
        return theData;
    }

    public static Object run5(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) actionContext.get(self.getString("dataObjectVar"));
        if(dataObject == null){
            logger.info("dataobject is null, thing=" + self);
            return null;
        }
        
        //删除
        Object result = dataObject.doAction("delete", actionContext);
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), result, actionContext);
        
        //返回值
        return result;
    }

    public static Object run6(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        DataObject dataObject = (DataObject) actionContext.get(self.getString("dataObjectVar"));
        if(dataObject == null){
            logger.info("dataobject is null, thing=" + self);
            return null;
        }
        
        //更新
        Object result = dataObject.doAction("update", actionContext);
        
        if(UtilData.isTrue(result) == false){
            //创建
            result = dataObject.doAction("create", actionContext);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), result, actionContext);
        
        //返回值
        return result;
    }

    @SuppressWarnings("unchecked")
	public static Object run7(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        List<DataObject> dataObjects = (List<DataObject>) actionContext.get(self.getString("dataObjectsVar"));
        if(dataObjects == null){
            logger.info("dataobjects is null, thing=" + self);
            return null;
        }
        
        List<Object> list = new ArrayList<Object>();
        boolean breakOnError = self.getBoolean("breakOnError");
        for(DataObject dataObject : dataObjects){
            //创建
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", dataObject);
            try{
                Object theData = dataObject.doAction("create", actionContext);        
                result.put("result", theData);          
            }catch(Exception e){
                result.put("result", e);  
                logger.info("create dataobjects error, thing=" + self);
                if(breakOnError){
                    break;
                }
            }
            list.add(result);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), list, actionContext);
        
        //返回值
        return list;
    }

    @SuppressWarnings("unchecked")
	public static Object run8(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        List<DataObject> dataObjects = (List<DataObject>) actionContext.get(self.getString("dataObjectsVar"));
        if(dataObjects == null){
            logger.info("dataobjects is null, thing=" + self);
            return null;
        }
        
        List<Object> list = new ArrayList<Object>();
        boolean breakOnError = self.getBoolean("breakOnError");
        for(DataObject dataObject : dataObjects){
            //创建
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", dataObject);
            try{
                Object theData = dataObject.doAction("update", actionContext);        
                result.put("result", theData);          
            }catch(Exception e){
                result.put("result", e);  
                logger.info("update dataobjects error, thing=" + self);
                if(breakOnError){
                    break;
                }
            }
            list.add(result);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), list, actionContext);
        
        //返回值
        return list;
    }

    public static Object run9(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        @SuppressWarnings("unchecked")
		List<DataObject> dataObjects = (List<DataObject>) actionContext.get(self.getString("dataObjectsVar"));
        if(dataObjects == null){
            logger.info("dataobjects is null, thing=" + self);
            return null;
        }
        
        List<Object> list = new ArrayList<Object>();
        boolean breakOnError = self.getBoolean("breakOnError");
        for(DataObject dataObject : dataObjects){
            //创建
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", dataObject);
            try{
                Object theData = dataObject.doAction("load", actionContext);        
                result.put("result", theData);  
            }catch(Exception e){
                result.put("result", e);  
                logger.info("load dataobjects error, thing=" + self);
                if(breakOnError){
                    break;
                }
            }
            list.add(result);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), list, actionContext);
        
        //返回值
        return list;
    }

    @SuppressWarnings("unchecked")
	public static Object run10(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        List<DataObject> dataObjects = (List<DataObject>) actionContext.get(self.getString("dataObjectsVar"));
        if(dataObjects == null){
            logger.info("dataobjects is null, thing=" + self);
            return null;
        }
        
        List<Object> list = new ArrayList<Object>();
        boolean breakOnError = self.getBoolean("breakOnError");
        for(DataObject dataObject : dataObjects){
            //创建
        	 Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", dataObject);
            try{
                Object theData = dataObject.doAction("delete", actionContext);        
                result.put("result", theData);  
            }catch(Exception e){
                result.put("result", e);  
                logger.info("update dataobjects error, thing=" + self);
                if(breakOnError){
                    break;
                }
            }
            list.add(result);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), list, actionContext);
        
        //返回值
        return list;
    }

    @SuppressWarnings("unchecked")
	public static Object run11(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        
        List<DataObject> dataObjects = (List<DataObject>) actionContext.get(self.getString("dataObjectsVar"));
        if(dataObjects == null){
            logger.info("dataobjects is null, thing=" + self);
            return null;
        }
        
        List<Object> list = new ArrayList<Object>();
        boolean breakOnError = self.getBoolean("breakOnError");
        for(DataObject dataObject : dataObjects){
            //创建
        	Map<String, Object> result = new HashMap<String, Object>();
            result.put("data", dataObject);
            try{
                //更新
                Object theData = dataObject.doAction("update", actionContext);
                
                if(UtilData.isTrue(theData) == false){
                    //创建
                	theData = dataObject.doAction("create", actionContext);
                }
           
                result.put("result", theData);          
            }catch(Exception e){
                result.put("result", e);  
                logger.info("load dataobjects error, thing=" + self);
                if(breakOnError){
                    break;
                }
            }
        
            list.add(result);
        }
        
        //保存变量
        UtilAction.putVarByActioScope(self, self.getString("varName"), list, actionContext);
        
        //返回值
        return list;
    }
    
    public static DataObject createDataObject(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
		
		DataObject dataObject = null;
		String key = "StaticDataObject";
		Thing staticThing = self.doAction("getStaticThing", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		if(staticThing == null) {
			staticThing = dataDesc;
		}
		if(staticThing != null) {
			dataObject = staticThing.getStaticData(key);
		}
		
		if(dataObject == null) {
			if(dataDesc != null) {
				String scope = self.doAction("getScope", actionContext);
				if("world".equals(scope)) {
					if(dataObject == null) {
						dataObject = new DataObject(dataDesc);
						dataObject.init(actionContext);
						staticThing.setStaticData(key, dataObject);
					}
				}else {
					dataObject = actionContext.getObject(dataDesc.getMetadata().getPath());
					if(dataObject == null) {
						dataObject = new DataObject(dataDesc);
						dataObject.init(actionContext);
					}
				}
			}else {
				dataObject = new DataObject();
			}
		}
		
		return dataObject;
    }

    public static DataObjectList createDataObjectList(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
		
		DataObjectList dataObjectList = null;
		Thing staticThing = self.doAction("getStaticThing", actionContext);
		Thing dataDesc = self.doAction("getDataObject", actionContext);
		if(staticThing == null) {
			staticThing = dataDesc;
		}
		
		if(staticThing != null) {
			dataObjectList = staticThing.getStaticData("StaticDataObjectList");
		}
		
		if(dataObjectList == null) {			
			if(dataDesc != null) {
				String scope = self.doAction("getScope", actionContext);
				if("world".equals(scope)) {
					if(dataObjectList == null) {
						dataObjectList = new DataObjectList(dataDesc);
						staticThing.setStaticData("StaticDataObjectList", dataObjectList);
					}
				}else {
					dataObjectList = actionContext.getObject(dataDesc.getMetadata().getPath());
					if(dataObjectList == null) {
						dataObjectList = new DataObjectList(dataDesc);						
					}
				}
			}else {
				dataObjectList = new DataObjectList();
			}
		}
		
		return dataObjectList;
    }
}