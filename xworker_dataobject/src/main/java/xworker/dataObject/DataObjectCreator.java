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
package xworker.dataObject;

import ognl.OgnlException;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilJava;
import xworker.dataObject.cache.DataObjectCache;
import xworker.dataObject.http.DataObjectHttpUtils;
import xworker.dataObject.iterators.PageDataObjectIterator;
import xworker.dataObject.query.QueryConfig;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.JsonFormator;
import xworker.dataObject.utils.UtilDate;
import xworker.lang.executor.Executor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataObjectCreator {
	private static final String TAG = DataObjectCreator.class.getName();
	private static long seq = 0;
	
    public static Object load(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	DataObject theData = (DataObject) self.doAction("doLoad", actionContext);
    	if(theData != null){
    		//初始化关系和触发加载事件
    		theData.fireOnLoaded(actionContext);
    	}
    	
        return theData;
    }

    public static Object createBatch(ActionContext actionContext) throws Exception{
    	int count = 0;
    	List<DataObject> datas = actionContext.getObject("datas");
    	if(datas != null) {
    		for(DataObject data : datas) {
    			if(data.create(actionContext) != null) {
    				count++;
    			}
    		}
    	}
    	
    	return count;
    }
    
    public static Object create(ActionContext actionContext) throws Exception{
    	Thing self = (Thing) actionContext.get("self");    	
    	DataObject theData = (DataObject) actionContext.get("theData");
    	
    	//是否需要自动初始化
		if(self.getBoolean("autoInit")){
		    theData.doAction(self.getString("autoInitAction"), actionContext);
		}

		//是否生成序列号
		Object[][] keyDatas = theData.getKeyAndDatas();
		
		if(self.getBoolean("autoGenerateId")){
		    Object id = null;
		    String idGenerateType = self.getString("idGenerateType");
		    if("action".equals(idGenerateType)){
		        id = self.doAction(self.getString("sequenceName"), actionContext);
		    }else if("sequence".equals(idGenerateType)){
		        id = self.doAction("generateSeqId", actionContext);     
		    }else if("native".equals(idGenerateType)){
		    	id = self.doAction("generateNativeId", actionContext);    	
		    }else{
		        throw new Exception("not support id generate type, type=" + idGenerateType);
		    }
		    
		    if(id != null){
			    theData.put(((Thing) keyDatas[0][0]).getString("name"), id);
			    keyDatas[0][1] = id;
		    }
		}
		
    	theData.fireBeforeCreate(actionContext);		
		theData = (DataObject) self.doAction("doCreate", actionContext);
		if(theData != null){
			theData.fireEvent("afterCreated", actionContext);
		}
		
		return theData;
    }

    public static Boolean update(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");    	
    	DataObject theData = (DataObject) actionContext.get("theData");
    	
    	theData.fireEvent("beforeUpdate", actionContext);		
    	Object result = self.doAction("doUpdate", actionContext);
		theData.fireEvent("afterUpdated", actionContext);
		
		//清空脏数据和重置主键
    	theData.getMetadata().updateKeyAndDatas();
    	theData.getMetadata().cleanDirty();	
		
		if(result instanceof Boolean){
			return (Boolean) result;
		}else{
			return false;
		}
    }

    public static void updateBatch(ActionContext actionContext){
    	Executor.info(TAG, "DataObject should implement updateBatch mehtod");
    }

    public static Boolean delete(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");    	
    	DataObject theData = (DataObject) actionContext.get("theData");
    	
    	theData.fireEvent("beforeDelete", actionContext);		
    	Object result = self.doAction("doDelete", actionContext);
		theData.fireEvent("afterDeleted", actionContext);
		
		if(result instanceof Boolean){
			return (Boolean) result;
		}else{
			return false;
		}
    }

    public static void deleteBatch(ActionContext actionContext){
        Executor.info(TAG, "DataObject should implement deleteBatch mehtod");
    }

    @SuppressWarnings("unchecked")
	public static List<DataObject> query(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");    	

    	Object config = actionContext.getObject("queryConfig");
        if(!(config instanceof QueryConfig)){
            //默认condtionData不为空
            Map<String, Object> conditionData = (Map<String, Object>) actionContext.get("conditionData");
            if (conditionData == null) {
                conditionData = new HashMap<>();
                actionContext.peek().put("conditionData", conditionData);
            }

            QueryConfig queryConfig = new QueryConfig((Thing) actionContext.get("conditionConfig"), conditionData, PageInfo.getPageInfo(actionContext), actionContext);
            actionContext.peek().put("queryConfig", queryConfig);
        }

    	if(self.getBoolean("beforeQuery")){
    		self.doAction("beforeQuery", actionContext);
    	}
    	List<DataObject> datas = (List<DataObject>) self.doAction("doQuery", actionContext);
    	if(datas != null){
            DataObjectCache.begin();
            try {
                for (DataObject data : datas) {
                    data.fireOnLoaded(actionContext);
                }
            }finally {
                DataObjectCache.finish();
            }
    	}else{
    		datas = Collections.EMPTY_LIST;
    	}
    	
    	try{
    		Bindings bindings = actionContext.push();
    		bindings.put("datas", datas);
    		
    		if(self.getBoolean("afterQueryed")){
    			self.doAction("afterQueryed", actionContext);
    		}
    	}finally{
    		actionContext.pop();
    	}
    	
    	return datas;
    }

    public static Object newInstance(ActionContext actionContext){
        return createDataObject((Thing) actionContext.get("self"));
    }
    
    public static DataObject createDataObject(Thing thing){
    	//原先是thing.getDescriptor()，原先的可能是有问题的
    	//DataObject dataObj = new DataObject(thing.getDescriptor());
    	DataObject dataObj = new DataObject(thing);
        for(Thing attribute : thing.getDescriptor().getChilds("attribute")){
            String name = attribute.getString("name");
            String d = attribute.getString("default");
            if(d != null && !"".equals(d)){
                dataObj.put(name, DataObjectUtil.getAttributeValue(d, attribute.getString("type")));
            }
        }   
        
        for(Thing child : thing.getChilds("thing")){
            Thing childDesc = child.getDescriptor();
            String name = childDesc.getString("name");
            if(childDesc.getBoolean("many")){
            	DataObjectList childs = new DataObjectList(childDesc, dataObj);
                dataObj.put(name, childs);
            }else{
                dataObj.put(name, new DataObject(child));
            }
        }
        
        return dataObj;
    }

    public static Object createDataObjectFromThing(ActionContext actionContext){
    	return createDataObject((Thing) actionContext.get("thing"), (Thing) actionContext.get("descriptor"));
    }
    
    public static DataObject createDataObject(Thing thing, Thing descriptor){
    	DataObject dataObj = new DataObject(descriptor);
        for(Thing attribute : descriptor.getChilds("attribute")){
            String name = attribute.getString("name");
            Object d = thing.get(name);
            if(d != null && d != ""){
                dataObj.put(name, DataObjectUtil.getValue(d, attribute));
            }
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
            	String refDescriporPath = refThing.getString("dataObjectPath");
            	if(World.getInstance().getThing(refDescriporPath) == null){
            		Executor.warn(TAG, "Ref thing is nul, path=" + refThing.getMetadata().getPath());
            	}else{
	            	DataObject dchild = new DataObject(refThing.getString("dataObjectPath"));
	                dchild.put(refThing.getString("refAttributeName"), dataObj.get(refThing.getString("localAttributeName")));
	                dchild.setInited(false);
	                dataObj.put(refThing.getString("name"), dchild);
            	}
            }
        }      
        
        return dataObj;
    }

    public static Object createDataObjectFromObject(ActionContext actionContext) throws OgnlException{
        return createDataObject(actionContext.get("data"), (Thing) actionContext.get("descriptor"));
    }
    
    public static DataObject createDataObject(Object data, Thing descriptor) throws OgnlException{
    	DataObject dataObj = new DataObject(descriptor);
        for(Thing attribute : descriptor.getChilds("attribute")){
            String name = attribute.getString("name");
            Object d = OgnlUtil.getValue(name, data);
            if(d != null &&  !"".equals(d)){
                dataObj.put(name, d);
            }
        }   
        dataObj.setSource(data);
        
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
        
        return dataObj;
    }

    public static Object toDataObject(ActionContext actionContext){
        return createDataObject((Thing) actionContext.get("self"));
    }

    public static Object createIterator(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        QueryConfig queryConfig = actionContext.getObject("queryConfig");

        return new PageDataObjectIterator(new DataObject(self), queryConfig, actionContext);
    }

    public static Object parseJsonData(ActionContext actionContext) throws Exception{        
        DataObject theData = (DataObject) actionContext.get("theData");
        if(theData == null){
        	theData = new DataObject((Thing) actionContext.get("self"));
        }
        String jsonText = (String) actionContext.get("jsonText");
        
        return JsonFormator.jsonObjectToDataObject(JsonFormator.parse(jsonText, actionContext), theData);
        /*
        JsonDataFormatCreator.format(actionContext)
        if(theData == null){
            theData = new DataObject(self);
            descriptor = self;
        }else{
            descriptor = theData.getMetadata().descriptor;
        }
        
        def iter = new StringCharacterIterator(jsonText);
        if(jsonText.startsWith("{")){
            parseDataObject(iter, theData);
            return theData;
        }else if(jsonText.startsWith("[")){
            def objList = new DataObjectList(null);
            parseDataObjectList(iter, objList, descriptor);
            return objList;
        }else{
            def keyName = theData.getMetadata().getKeys()[0].name;
            if(jsonText.startsWith("\"")){
                theData.put(keyName, parseName(iter));
            }else{
                theData.put(keyName, jsonText);
            }
            return theData;
        }
        
        def parseDataObject(iter, dataObject){
            while(true){
                def c = iter.next();
                if(c == StringCharacterIterator.DONE || c == "}"){
                    break;
                }
                if(c == '{'){
                    continue;
                }
                if(c == '"'){
                    def name = parseString(iter, ['"']);                           
                    parseValue(iter, name, dataObject, [',', '}']);
                    if(iter.current() == '}'){
                        iter.next();
                        break;
                    }else{
                        //c = iter.next();
                        continue;
                    }            
                }else if(c == '\''){
                    def name = parseString(iter, ['\'']);   
                    iter.next();
                    parseValue(iter, name, dataObject, [',', '}']);
                    if(iter.current() == '}'){
                        break;
                    }else{
                        iter.next();
                        continue;
                    }  
                }else if(c == ','){
                    continue;
                }else if(c == ' '){
                    continue;
                }else{
                    def name = "" + c + parseString(iter, [':']);
                    parseValue(iter, name, dataObject, [',', '}']);
                    if(iter.previous() == '}'){
                        iter.next();
                        break;
                    }else{
                        iter.next();
                        continue;
                    }  
                }        
            }
        }
        
        //分析值
        def parseValue(StringCharacterIterator iter, name, dataObject, endChars){
            while(true){
                def c = iter.next();
                if(c == StringCharacterIterator.DONE){
                    return;
                }
                if(c == '{'){
                    //子事物
                    def descriptor = dataObject.getMetadata().descriptor.get("attribute?" + name);
                    def child = new DataObject(descriptor);
                    parseDataObject(iter, child);
                    dataObject.put(name, child);
                    return;
                }else if(c == '"'){
                    //普通属性
                    def value = parseString(iter, ['"']);
                    dataObject.put(name, value);
                    return;
                }else if(c == '\''){
                    //普通属性
                    def value = parseString(iter, ['\'']);
                    dataObject.put(name, value);
                    return;
                }else if(c == '['){
                    //子事物列表
                    def descriptor = dataObject.getMetadata().descriptor.get("attribute?" + name);
                    def child = new DataObjectList(descriptor, dataObject);
                    def childDescriptor = world.getThing(descriptor.dataObjectPath);
                    parseDataObjectList(iter, child, childDescriptor);
                    dataObject.put(name, child);
                    return c;
                }else if(c == ' '){
                    continue;
                }else if(c == ':'){
                    continue;
                }else{
                    def value = "" + c + parseString(iter, endChars);
                    dataObject.put(name, value);
                    return;
                }
            }
        }
        
        def parseDataObjectList(iter, dataObjectList, descriptor){
            while(true){
                def c = iter.next();
                if(c == StringCharacterIterator.DONE){
                    break;
                }    
                if(c == '{'){
                    //log.info("list parse obj");
                    def child = new DataObject(descriptor);
                    parseDataObject(iter, child);
                    dataObjectList.add(child);
                    continue;
                }else if(c == '['){
                    //log.info("list parse list");
                    def child = [];
                    parseDataObjectList(iter, child, descriptor);
                    continue;
                }else if(c == '"'){
                    //log.info("list parse \" value");
                    def child = new DataObject(descriptor);
                    child.put(child.getMetadata().getKeys()[0].name, parseString(iter, ['"']));
                    dataObjectList.add(child);
                    continue;
                }else if(c == '\''){
                    //log.info("list parse ' value");
                    def child = new DataObject(descriptor);
                    child.put(child.getMetadata().getKeys()[0].name, parseString(iter, ['\'']));
                    dataObjectList.add(child);
                    continue;
                }else if(c == ']'){
                    break;
                }else if(c == ','){        
                    continue;
                }else if(c == ' '){
                    continue;
                }else{
                    //log.info("list parse value");
                    def child = new DataObject(descriptor);
                    child.put(child.getMetadata().getKeys()[0].name, "" + c + parseString(iter, [',', ']']));
                    dataObjectList.add(child);
                    if(iter.previous() == ']'){
                        iter.next();
                        break;
                    }else{
                        iter.next();
                        continue;
                    }
                }
            }
        }
        
        //分析参数名
        def parseName(StringCharacterIterator iter){
            return parseString(iter, ['"']);
        }
        
        def parseString(StringCharacterIterator iter, endChars){
            def isMeta = false;
            def str = new StringBuilder();
            //println "parse string endchars=" + endChars;
            while(true){
                def c = iter.next();
                if(c == StringCharacterIterator.DONE){
                    return str.toString();
                }
                
                //println "parse string char=" + c;
                if(isMeta == false && isEndChar(c, endChars)){
                    //println "parse stirng return";
                    return str.toString();
                }else if(c == '\\' && isMeta == false){
                    def nextc = iter.next();
                    if(nextc == '"'){
                        str.append("\"");
                    }else if(nextc == '\''){
                        str.append("'");
                    }else if(nextc == 'n'){
                        str.append("\n");
                    }else if(nextc == 'r'){
                        str.append("\r");
                    }else if(nextc == 't'){
                        str.append("\t");
                    }else{
                        str.append("\\");
                        iter.previous();
                    }
                }else if(isMeta && isEndChar(c, endChars)){
                    isMeta = false;
                    str.append(c);
                }else if(isMeta){
                    str.append('\\');    
                    str.append(c);     
                    isMeta = false; 
                }else{
                    str.append(c);
                }
            }
        }
        
        def isEndChar(ch, endChars){
            for(c in endChars){
                if(c == ch){
                    return true;
                }
            }
            
            return false;
        }*/
    }

    public static Object parseHttpRequestData(ActionContext actionContext){
        return DataObjectHttpUtils.parseHttpRequestData(actionContext);
    }

    public static Object getKeyAttributes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return DataObjectThingUtils.getKeyAttributes(self);
    }

    public static Object getEditorAttributes(ActionContext actionContext){
        String editorType = (String) actionContext.get("editorType");
        Thing self = (Thing) actionContext.get("self");
        
        return DataObjectThingUtils.getEditorAttributes(self, editorType);
    }

    public static Object getReaderAttributes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return DataObjectThingUtils.getReaderAttributes(self);
    }

    public static Object menu_editData(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{
    	return UtilJava.invokeMethod("xworker.dataObject.swt.DataObjectSwtUtils", "menu_editData", actionContext);
    }

    public static Object openSwtEditor(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{
    	return UtilJava.invokeMethod("xworker.dataObject.swt.DataObjectSwtUtils", "openSwtEditor", actionContext);
    }
    
    public static synchronized Object generateId(ActionContext actionContext){
    	seq++;
        return "" + System.currentTimeMillis() + "_" + seq;
    }

    public static Object autoInit(ActionContext actionContext){
        //log.info("DataObject: auto init, you should implement this method");
        init(actionContext);
        return null;
    }

    public static Object isMappingAble(ActionContext actionContext) throws Exception{
        throw new Exception("没有实现属性映射。");
    }

    public static Object getMappingFields(ActionContext actionContext){
        return Collections.emptyList();
    }

    public static Object getMappingAttributeName(ActionContext actionContext){
        return "name";
    }

    public static ValidateResult validate(ActionContext actionContext){
    	DataObject theData = (DataObject) actionContext.get("theData");
    	ValidateResult result = new ValidateResult(theData);
    	if(theData == null){
    		result.addInfo(null, "DataObject is not exists, please use dataObject call this method! Or set theData!");    		
    	}else{
    		//默认判断主键是否重复
    		Object[][] keys = theData.getKeyAndDatas();
    		
    		//创建一个新的未初始化的数据对象
    		DataObject aData = new DataObject(theData.getMetadata().getDescriptor());
    		String message = null;
    		for(int i=0;i<keys.length; i++){
    			Thing keyThing = (Thing) keys[i][0];
    			aData.put(keyThing.getString("name"), keys[i][1]);
    			if(message == null){
    				message = keyThing.getMetadata().getLabel() + "=" + keys[i][1];
    			}else{
    				message = message + "," + keyThing.getMetadata().getLabel() + "=" + keys[i][1];
    			}
    		}
    		    		
    		if(aData.load(actionContext) != null){
    			//已经初始化，说明存在记录
    			
    			result.addInfo(null, "记录已经存在，" + message + "。");
    		}
    	}
    	
        return result;
    }
    
    public static Object getAttributeDescriptor(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        String path = self.getMetadata().getPath();
        if(path.indexOf(":/") == -1){
            return path + "/@attribute";
        }else{
            return path + "/@attribute";
        }
    }

    public static void init(ActionContext actionContext){
        DataObject theData = (DataObject) actionContext.get("theData");
        
        if(theData == null){
            return;
        }
        
        //遍历属性定义，初始化默认值
        for(Thing attribute : theData.getMetadata().getAttributes()){
            //默认值，通常在属性定义中是字符串
        	String name = attribute.getString("name");
            String defaultValue = attribute.getString("default");
            if(theData.get(name) != null || defaultValue == null || "".equals(defaultValue)){
                continue;
            }
            
            
            String type = attribute.getString("type");    
            if("date".equals(type) || "datetime".equals(type) || "time".equals(type)){
                //日期的默认值
                defaultValue = defaultValue.toLowerCase();
                String dateStr = defaultValue;
                String numberStr = "";
                int index = defaultValue.indexOf("+");
                if(index != -1){
                    dateStr = defaultValue.substring(0, index).trim();
                    numberStr = defaultValue.substring(index, defaultValue.length()).trim();
                }else{
                    index = defaultValue.indexOf("-");
                    if(index != -1){
                        dateStr = defaultValue.substring(0, index).trim();
                        numberStr = defaultValue.substring(index, defaultValue.length()).trim();
                    }
                }
         
                Date date = null;
                if("now".equals(dateStr) || "sysdate".equals(dateStr)){
                    date = new Date();
                }else if("tomorrow".equals(dateStr)){
                    date = UtilDate.getTomorrow();
                }else if("yesterday".equals(dateStr)){
                    date = UtilDate.getYesterday();
                }else if("weekstart".equals(dateStr)){
                    date = UtilDate.getWeekStart();
                }else if("weekend".equals(dateStr)){
                    date = UtilDate.getWeekEnd();
                }else if("monthstart".equals(dateStr)){
                    date = UtilDate.getMonthStart();
                }else if("monthend".equals(dateStr)){
                    date = UtilDate.getMonthEnd();
                }else if("yearstart".equals(dateStr)){
                    date = UtilDate.getYearStart();
                }else if("yearend".equals(dateStr)){
                    date = UtilDate.getYearEnd();
                }else{
                    date = new Date();
                    try{
                        double d = (Double) OgnlUtil.getValue(dateStr, actionContext);
                        date = UtilDate.getDate(date, d);
                    }catch(Exception e){
                    }
                }
                if(numberStr != null && numberStr != ""){
                    try{
                        double d = (Double) OgnlUtil.getValue(numberStr, actionContext);
                        //log.info("d=" + d);
                        date = UtilDate.getDate(date, d);
                    }catch(Exception e){
                        //log.info("error", e);
                    }
                }
                theData.put(attribute.getString("name"), date);
            }else{
                theData.put(attribute.getString("name"), defaultValue);
            }
        }
    }
}