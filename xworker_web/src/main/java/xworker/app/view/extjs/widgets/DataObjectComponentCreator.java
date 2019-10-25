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
package xworker.app.view.extjs.widgets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.html.HtmlUtil;

public class DataObjectComponentCreator {
	private static Logger log = LoggerFactory.getLogger(DataObjectComponentCreator.class);
	
    public static Object createExtFieldThing(ActionContext actionContext){
        Thing field = (Thing) actionContext.get("field");
        
        //字段事物
        Thing f = new Thing("xworker.html.extjs.Ext.data.DataReader/@24399/@Field");
        f.initDefaultValue();
        
        //字段名
        f.put("name", "'" + field.get("name") + "'");
        String type = field.getString("type");
        //字段类型
        if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "long".equals(type)){
            f.put("type", "'int'");
        }else if("float".equals(type)){
            f.put("type", "'float'");
        }else if("double".equals(type)){
            f.put("type", "'number'");
        }else if("boolean".equals(type)){
            f.put("type", "'boolean'");
        }else if("date".equals(type)){
            f.put("type", "'date'");
        }else if("datetime".equals(type)){
            f.put("type", "'date'");
        }else if("time".equals(type)){
            f.put("type", "'time'");
        }else{
            //默认string，不用设置
        }
        //默认值
        String defaultValue = field.getString("defaultValue");
        if(defaultValue != null && !"".equals(defaultValue)){
        	if("byte".equals(type) || "short".equals(type) || "int".equals(type) ||
        			"long".equals(type) || "float".equals(type) || "double".equals(type) || "boolean".equals(type)){
                    f.put("defaultValue", defaultValue);
        	}else if("date".equals(type) || "datetime".equals(type) || "time".equals(type)){
                    f.put("defaultValue","'" + defaultValue + "'");
        	}else{
                    f.put("defaultValue", "'" + defaultValue + "'");
            }
        }
        
        return f;
    }


    public static void copyCommonAttributes(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing component = (Thing) actionContext.get("component");
    	
        component.put("name", self.get("name"));
        component.put("label", self.getString("label"));
        component.put("varname", self.getString("varname"));
        component.put("varref", self.get("varref"));
        component.put("title", self.get("title"));
        component.put("renderTo", self.get("renderTo"));
    }

    @SuppressWarnings("unchecked")
	public static Object createExtStore(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
        
        //----------------create store-----------------
        //数据仓库
        Thing stores = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25031");
        stores.initDefaultValue();
        stores.put("name", "store");
        
        //-------------create jsasonstore------------
        Thing store = null;
        if(dataObject.getBoolean("gridGrouping")){
            store = new Thing("xworker.html.extjs.Ext.data.GroupingStore");
        }else{
            store = new Thing("xworker.html.extjs.Stores/@JsonStore");    
        }
        store.initDefaultValue();
        
        stores.addChild(store);
        //log.info("storeId=" + actionContext.get("storeId"));
        String storeId = self.getStringBlankAsNull("storeId");
        String cmpId = (String) actionContext.get("cmpId");
        if(storeId != null){
        	
        }else if(cmpId != null &&  !"".equals(cmpId)){
        	storeId = cmpId + "_store";
        }else if(actionContext.get("storeId") != null && !"".equals(actionContext.get("storeId"))){
        	storeId = (String) actionContext.get("storeId");
        }
        
        if(storeId != null) {
	        store.put("storeId", "'" + storeId + "'");
	        store.put("varname", storeId + "");
	        store.put("varglobal", "true");
        }
        
        //--------------------HttpProxy--------------------
        Thing proxys = new Thing("xworker.html.extjs.Ext.data.Store/@24491");
        Thing proxy = new Thing("xworker.html.extjs.Ext.data.DataProxys/@HttpProxy");
        Thing api = new Thing("xworker.html.extjs.Ext.data.DataProxy/@24390");
        api.put("read", getExtString(dataObject.getString("readUrl")));
        api.put("create", getExtString(dataObject.getString("createUrl")));
        api.put("update", getExtString(dataObject.getString("updateUrl")));
        api.put("destroy", getExtString(dataObject.getString("destroyUrl")));
        api.put("name", "api");
        proxys.put("name", "proxy");
        proxy.addChild(api);
        proxy.set("timeout", 180000);
        proxys.addChild(proxy);
        store.addChild(proxys);
        
        if("grid".equals(actionContext.get("cmpType"))){
            //动态grid的重新初始化
        	Thing proxyListener = new Thing("xworker.html.extjs.Ext.util.Observable/@25649");
        	Thing proxyLisLoad = new Thing("xworker.html.extjs.Ext.util.Observable/@25649/@Function");
            proxyListener.put("name", "listeners");
            proxyLisLoad.put("name", "load");
            proxyLisLoad.put("params", "proxy, o, options");
            String code = "var grid = Ext.getCmp(" + self.get("id") + ");\n\n" +         
            		"var reader = grid.store.reader;\n" + 
            		"if(reader.jsonData){\n" + 
            		"    if(reader.jsonData.columnModel){\n" + 
            		"        var grid = Ext.getCmp(" + self.get("id") + ");\n" + 
            		"        var colModel = new Ext.grid.ColumnModel(reader.jsonData.columnModel);\n" + 
            		"        \n" + 
            		"        //判断新的表头列是否和原有列相同\n" + 
            		"        var same = colModel.config.length == grid.colModel.config.length;\n" + 
            		"        if(same){\n" + 
            		"            for(i=0; i<colModel.config.length; i++){\n" + 
            		"                if(colModel.config[i].id != grid.colModel.config[i].id){\n" + 
            		"                    same = false;\n" + 
            		"                    break;\n" + 
            		"                }\n" + 
            		"            }\n" + 
            		"        }\n" + 
            		"        \n" + 
            		"        if(!same){\n" + 
            		"            //不相同，重新初始化表格\n" +        
            		"            var store = grid.store;\n" + 
            		"            \n" + 
            		"            grid.reconfigure(store, colModel);\n" + 
            		"            Ext.apply(store.baseParams, store.lastOptions.params);\n" + 
            		"        }\n" + 
            		"    }\n" + 
            		"}\n";
            proxyLisLoad.put("code", code);
            proxy.addChild(proxyListener);
            proxyListener.addChild(proxyLisLoad);
        }
        
        //-------------------reader-----------------------
        Thing jsonReader = null;
        if(dataObject.getBoolean("gridGrouping")){
            Thing reader = new Thing("xworker.html.extjs.Ext.data.Store/@24493");
            reader.put("name", "reader");
            jsonReader = new Thing("xworker.html.extjs.Ext.data.JsonReader");
            jsonReader.initDefaultValue();
            reader.addChild(jsonReader);
            store.addChild(reader);
        }
        
        //-------------------writer-----------------------
        Thing writer = new Thing("xworker.html.extjs.Ext.data.Store/@24499");
        writer.put("name", "writer");
        Thing jsonWriter = new Thing("xworker.html.extjs.Ext.data.Writers/@JsonWriter");
        jsonWriter.initDefaultValue();
        jsonWriter.set("encodeDelete", "true");
        writer.addChild(jsonWriter);
        store.addChild(writer);
        
        //-------------------fields-------------------------
        Thing fields = null;
        if(dataObject.getBoolean("gridGrouping")){
            fields =new Thing("xworker.html.extjs.Ext.data.Store/@fields");
        }else{
            fields = new Thing("xworker.html.extjs.Ext.data.Store/@fields");
        }
        fields.put("name", "fields");
        
        for(Thing attribute : dataObject.getChilds("attribute")){
            Thing field = (Thing) self.doAction("createExtStoreField", actionContext, UtilMap.toMap(new Object[]{"field", attribute}));
            if(field != null){
                fields.addChild(field);
            }
        }
        
        /*
        def dataIdField = new Thing("xworker.dataObject.thing.ThingDataObject/@attribute");
        dataIdField.name = "extJsDataId";
        dataIdField.type = "string";
        fields.addChild(self.doAction("createExtStoreField", actionContext, ["field":dataIdField]));
        */
        if(dataObject.getBoolean("gridGrouping")){
            jsonReader.addChild(fields);
        }else{
            store.addChild(fields);
        }
        
        //------------------base parameters---------------
        Thing baseParameters = new Thing("xworker.html.extjs.Ext.data.Store/@24486");
        baseParameters.put("name", "baseParams");
        baseParameters.put("code", "dataObjectPath:'" + dataObject.getMetadata().getPath() + "'");
        String dobj_condition = self.getString("dobj_condition");
        if(dobj_condition == null || "".equals(dobj_condition)){
        	Thing condition = self.getThing("Condition@0");
        	if(condition != null){
        		dobj_condition = condition.getMetadata().getPath();
        	}
        }
        if(dobj_condition != null &&  !"".equals(dobj_condition)){
            baseParameters.put("code", baseParameters.getString("code") + ",\nconditionPath:'" + dobj_condition + "'");
        }
        if(dataObject.getBoolean("paging") || self.getBoolean("dobj_paging") || dataObject.getBoolean("splitPage")){
            String pageSize = self.getString("dobj_pageSize");
            if(pageSize == null || "".equals(pageSize)){
                pageSize = dataObject.getString("pageSize");
            }
            if(pageSize == null || "".equals(pageSize)){
                pageSize = "20";
            }
            baseParameters.put("code", baseParameters.get("code") + ",\nstart: 0");
            baseParameters.put("code", baseParameters.get("code") + ",\nlimit: " + pageSize);
        }
        String storeSortField = dataObject.getString("storeSortField");        
        if(storeSortField != null &&  !"".equals(storeSortField)){
            baseParameters.put("code", baseParameters.get("code") + ",\nsort: '" + storeSortField + "'");
            baseParameters.put("code", baseParameters.get("code") + ",\ndir: '" + dataObject.getString("storeSortDir") + "'");
        }
        store.addChild(baseParameters);
        
        //---------------set store attributes----------
        //设置属性
        setValue(dataObject, store, "storeAutoDestroy", "autoDestroy");
        setValue(dataObject, store, "storeAutoLoad", "autoLoad");
        setValue(dataObject, store, "storeAutoSave", "autoSave");
        setValue(dataObject, store, "storeBatch", "batch");
        setValue(dataObject, store, "storePruneModifiedRecords", "pruneModifiedRecords");
        setValue(dataObject, store, "storeRemoteSort", "remoteSort");
        setValue(dataObject, store, "storeRestful", "restful");
        setValue(dataObject, store, "gridGroupField", "groupField");
        //reader
        Thing reader = store;
        if(dataObject.getBoolean("gridGrouping")){
            reader = jsonReader;
        }
        //store.idProperty = "'extJsDataId'";
        //reader.idProperty = "'extJsDataId'";
        setValue(dataObject, reader, "storeIdProperty", "idProperty");
        setValue(dataObject, reader, "storeRoot", "root");
        setValue(dataObject, reader, "storeSuccessProperty", "successProperty");
        setValue(dataObject, reader, "storeTotalProperty", "totalProperty");
        setValue(dataObject, reader, "storeMessageProperty", "messageProperty");
        String idProperty = store.getString("idProperty");
        if(idProperty == null || "".equals(idProperty)){
            //log.info("dataObject="+ dataObject.metadata.path);
            List<Thing> keys = (List<Thing>) dataObject.doAction("getKeyAttributes", actionContext);
            if(keys.size() > 0){
                store.put("idProperty", "'" + keys.get(0).getString("name") + "'");
            }
        }
        
        //创建Store的监听
        try{
        	Bindings bindings = actionContext.push();
        	bindings.put("self", self);
        	bindings.put("store", store);
        	createStoreListener(actionContext);
        }finally{
        	actionContext.pop();
        }
        
        //如果是服务器自动生成标识，那么创建一个save监听，当客户端创建了新数据重新reload
        if(dataObject.getBoolean("autoGenerateId")){
            self.doAction("createStoreSaveListener", actionContext, UtilMap.toMap(new Object[]{"store",store}));
        }
        //生成错误监听
        self.doAction("createStoreExceptionListener", actionContext, UtilMap.toMap(new Object[]{"store",store}));
        return stores;
        
    }

    //dataObject中定义了和gird列相关的事物    
    public static void setValue(Thing field, Thing column, String filedName, String columnName){
        String value = field.getString(filedName);
        if(value != null && !"".equals(value)){
            column.put(columnName, value);
        }
    }
    
    public static String getExtString(String value){
        if(value != null &&  !"".equals(value)){
            return "'" + value + "'";
         }
        
        return null;
     }
    
    public static Object createExtStoreField(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing field = (Thing) actionContext.get("field");
        
        //---------------create Field Thing-------------
    	Thing f = new Thing("xworker.html.extjs.Ext.data.Field");
        f.initDefaultValue();
        
        //---------------设置属性-----------------------
        self.doAction("copyValue", actionContext, 
            UtilMap.toMap(new Object[]{"sourceName","validateAllowBlank", "targetName","allowBlank", "source",field, "target",f}));
        self.doAction("copyValue", actionContext, 
            UtilMap.toMap(new Object[]{"sourceName","gridSortDir", "targetName","sortDir", "source",field, "target",f}));
        //名称
        f.put("name", "'" + field.get("name") + "'");
        //字段的路径映射
        f.put("mapping", "'" + field.get("name") + "'");
        //类型
        String type = (String) self.doAction("getExtFieldType", actionContext, UtilMap.toMap(new Object[]{"field",field}));
        if(type != null){
            f.put("type", type);
            //println type;
            if("'date'".equals(type)){
                String pattern = field.getString("jsEditPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "'Y-m-d'";
                }else{
                    pattern = "'" + pattern + "'";
                }
                f.put("type", "'date'");
                f.put("dateFormat", pattern);
            }else if("'datetime'".equals(type)){
                f.put("type", "'date'");
                String pattern = field.getString("jsEditPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "'Y-m-d H:i:s'";
                }else{
                	/*
                    String[] ps = pattern.split("[|]");
                    pattern = ps[0] + " ";
                    if(ps.length >= 2){
                        pattern = pattern + ps[1];
                    }*/
                    pattern = "'" + pattern + "'";
                }
                f.put("dateFormat", pattern);
            }else if("'time'".equals(type)){
                f.put("type", "'date'");
                String pattern = field.getString("jsEditPattern");
                if(pattern == null || "".equals(pattern)){
                    pattern = "'H:i:s'";
                }else{
                    pattern = "'" + pattern + "'";
                }
                f.put("dateFormat", pattern);
            }
        }
        //缺省值
        String defaultValue = (String) self.doAction("getExtDefaultValue", actionContext, UtilMap.toMap(new Object[]{"field", field}));
        if(defaultValue != null && defaultValue != ""){
            f.put("defaultValue", defaultValue);
        }
        
        return f;
    }

    
    @SuppressWarnings("unchecked")
	public static Object createExtFieldEditor(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	Thing viewConfig = (Thing) field.get("viewConfig");
    	
        //编辑器的类型
        String editorType = (String) actionContext.get("editorType");
        if(editorType == null || "".equals(editorType)){
            editorType = "edit";
        }
        
        //---------------------创建编辑器-----------------
        //首先看属性是否已自定义了对应编辑器类型的编辑控件
        Thing editor = null;
        if("create".equals(editorType)){
            editor = attribute.getThing("Extjs@0/CreateEditor@0/@0");
        }else if("edit".equals(editorType)){
            editor = attribute.getThing("Extjs@0/EditEditor@0/@0");
        }else if("view".equals(editorType)){
            editor = attribute.getThing("Extjs@0/ViewEditor@0/@0");
        }else if("query".equals(editorType)){
            editor = attribute.getThing("Extjs@0/QueryEditor@0/@0");
        }else if("grid".equals(editorType)){
            editor = attribute.getThing("Extjs@0/GirdEditor@0/@0");
        }else{
            editor = attribute.getThing("Extjs@0/EditEditor@0/@0");
        }
        
        //如果未定义对应编辑类型的编辑控件，那么找通用的编辑控件定义
        if(editor == null){
            editor = attribute.getThing("Extjs@0/Editor@0/@0");
        }
        
        //如果已自定义编辑控件，那么返回，否则自动创建编辑控件
        if(editor != null){
            return editor;
        }
        
        //通过类型自动创建编辑器
        String type = viewConfig.getString("inputtype");
        if(type == null || "".equals(type)){
            type = "string";
        }
        
        if(!attribute.getBoolean("editable")){
        	editor = (Thing) self.doAction("createExt" + type, actionContext, UtilMap.toMap(new Object[]{"field",field}));
            //editor = (Thing) self.doAction("createExtlabel" + type, actionContext, UtilMap.toMap(new Object[]{"field",field}));
        	//editor.set("disabled", "true");
        }else{
            editor = (Thing) self.doAction("createExt" + type, actionContext, UtilMap.toMap(new Object[]{"field",field}));
        }
        
        if(editor == null){
            //按输入框默认创建
            editor = (Thing) self.doAction("createExttext", actionContext, UtilMap.toMap(new Object[]{"field", field}));
        }
        //查看是否定义了校验函数
        if(editor != null){
            Thing validator = attribute.getThing("Extjs@0/EditorValidator@0");
            if(validator != null){
                editor.addChild(validator, false);
            }
        }
        //查看是否定义的监听器
        if(editor != null){
            Thing listeners = attribute.getThing("Extjs@0/listeners@0");
            if(listeners != null){
                editor.addChild(listeners, false);
            }
        }
        
        //设置公共参数
        setValue(attribute, editor, "readOnly", "readOnly", false);
        setValue(attribute, editor, "validateAllowBlank", "allowBlank", false);
        setValue(attribute, editor, "invalidClass", "invalidClass", true);
        setValue(attribute, editor, "invalidText", "invalidText", true);
        setValue(attribute, editor, "validateOnBlur", "validateOnBlur", false);
        setValue(attribute, editor, "validationDelay", "validationDelay", false);
        setValue(attribute, editor, "validationEvent", "validationEvent", true);
        setValue(attribute, editor, "blankText", "blankText", true);
        setValue(attribute, editor, "maxLength", "maxLength", false);
        setValue(attribute, editor, "minLength", "minLength", false);
        setValue(attribute, editor, "regex", "regex", true);
        setValue(attribute, editor, "allowDecimals", "allowDecimals", false);
        setValue(attribute, editor, "allowNegative", "allowNegative", false);
        setValue(attribute, editor, "maxValue", "maxValue", false);
        setValue(attribute, editor, "minValue", "minValue", false);
        setValue(attribute, editor, "jsEditPattern", "format", true);
        int size = attribute.getInt("size");
        if(size > 0){
        	editor.put("width", String.valueOf(size * 8));
        }
        //setValue(attribute, editor, "size", "width", false);
        setValue(attribute, editor, "inputValue", "inputValue", false);
        setValue(attribute, editor, "disabled", "disabled", false);
        setValue(attribute, editor, "width", "width", false);
        setValue(attribute, editor, "height", "height", false);
        int colspan = attribute.getInt("colspan");
        if(colspan > 1){
            editor.put("colspan", "" + (1 + (colspan - 1) * 2));
        }
        int rowspan = attribute.getInt("rowspan");
        if(rowspan > 1){
            editor.put("rowspan", "" + (1 + (rowspan - 1) * 2));
        }
        
        return editor;
    }
    
    public static void  setValue(Thing obj, Thing editor, String objName, String editorName, boolean isString){
        String v = obj.getString(objName);
        if(v != null && !"".equals(v)){
            if(isString && !v.startsWith("'")){
                editor.put(editorName, "'" + v + "'");
            }else{
                editor.put(editorName, v);
            }
        }
    }

    public static void copyValue(ActionContext actionContext){
    	Thing source = (Thing) actionContext.get("source"); 
    	Thing target = (Thing) actionContext.get("target");
    	
        String value = source.getString((String) actionContext.get("sourceName"));
        if(value != null && !"".equals(value)){
            target.put((String) actionContext.get("targetName"), value);
        }
    }

    public static void getDataObjectKeys(ActionContext actionContext){
        
    }

    public static Object getExtFieldType(ActionContext actionContext){
    	Thing field = (Thing) actionContext.get("field");
    	
        if(actionContext.get("field") == null){
            Thread.dumpStack();
            return null;
        }
        
        String type = field.getString("type");
        if(field == null || type == null || "".equals(type)){
            return null;
        }
        
        if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "long".equals(type)){
            return "'int'";
        }else if("float".equals(type)){
            return "'float'";
        }else if("double".equals(type)){
            return "'number'";
        }else if("boolean".equals(type)){
            return "'boolean'";
        }else if("date".equals(type)){
            return "'date'";
        }else if("datetime".equals(type)){
            return "'datetime'";
        }else if("time".equals(type)){
            return "'time'";
        }else{
            return null;
        }
    }

    public static Object getExtDefaultValue(ActionContext actionContext){
    	Thing field = (Thing) actionContext.get("field");
    	
    	String type = field.getString("type");
        if(type == null || "".equals(type)){
            type = "string";
        }
        String defaultValue = field.getStringBlankAsNull("default");
    	Object value = DataObjectUtil.getDefaultValue(type, defaultValue, actionContext);
    	if(value != null){
    		SimpleDateFormat sf ;
    		if("date".equals(type)){
    			sf = new SimpleDateFormat("yyyy-MM-dd");
    			return "'" + sf.format((Date) value) + "'";
    		}else if("time".equals(type)){
    			sf = new SimpleDateFormat("HH:mm:ss");
    			return "'" + sf.format((Date) value) + "'";
    		}else if("datetime".equals(type)){
    			sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    			return "'" + sf.format((Date) value) + "'";
    		}
    		return value;
    	}else{
    		return value;
    	}
    	/*
        String type = field.getString("type");
        if(type == null || "".equals(type)){
            type = "string";
        }
        if("byte".equals(type) || "short".equals(type) || "int".equals(type) || "long".equals(type) ||
        		"float".equals(type) || "double".equals(type) || "boolean".equals(type)){
            return field.get("default");    
        }else{
                String v = field.getString("default");
            if(v != null && !"".equals(v)){
                return "'" + v + "'";
            }else{
                return null;
            }
        }*/
    }

    @SuppressWarnings("unchecked")
	public static Object createExttext(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.TextField");
        input.put("name", "'" + ((Thing) field.get("attribute")).get("name") + "'");
        return input;
    }
    
    @SuppressWarnings("unchecked")
    public static Object createExtfile(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.TextField");
        input.put("name", "'" + ((Thing) field.get("attribute")).get("name") + "'");
        input.put("inputType", "'file'");
        return input;
    }
    
    @SuppressWarnings("unchecked")
    public static Object createExtfilePath(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.TextField");
        input.put("name", "'" + ((Thing) field.get("attribute")).get("name") + "'");
        input.put("inputType", "'file'");
        return input;
    }
    
    @SuppressWarnings("unchecked")
	public static Object createExttextarea(ActionContext actionContext){
    	String editorType = (String) actionContext.get("editorType");
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
    	Thing input = null;
        if(actionContext.get("editorType") != null && editorType == "grid"){
            input = new Thing("xworker.html.extjs.Ext.form.TextArea");
            input.put("name", "'" + attribute.get("name") + "'");            
        }else{
            input = new Thing("xworker.html.extjs.Ext.form.TextArea");
            input.put("name", "'" + attribute.get("name") + "'");            
        }
        input.put("style", "'white-space: nowrap;overflow-x:  scroll;word-wrap: normal;'");
        
        //创建属性
        String inputattr = attribute.getString("inputattrs");
        if(inputattr != null && !"".equals(inputattr)){
        	Map<String, String> params = UtilString.getParams(inputattr, " ");
        	String cols= params.get("cols");
        	if(cols != null && !"".equals(cols)){
        		input.put("width", Integer.parseInt(cols) * 8);
        	}else{
        		input.put("width", "500");
        	}
        	String rows = params.get("rows");
        	if(rows != null && !"".equals(rows)){
        		input.put("height", Integer.parseInt(rows) * 20);
        	}else{
        		input.put("height", "200");
        	}
        }else{
        	input.put("width", "500");
        	input.put("height", "200");
        }
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExttruefalse(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.Checkbox");
        input.put("name", "'" + attribute.get("name") + "'");
        
        Thing handler = new Thing("xworker.html.extjs.Ext.form.Checkbox/@24715");
        handler.put("name", "handler");
        handler.put("params", "checkbox,checked");
        handler.put("code", "\n" + 
"        if(checked){\n" + 
"            checkbox.setRawValue('true');\n" + 
"        }else{\n" + 
"            checkbox.setRawValue('false');\n" + 
"        }\n" + 
"        ");
        input.addChild(handler);
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExthtml(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        
        Thing input = new Thing("xworker.html.extjs.Ext.form.HtmlEditor");
        input.put("name", "'" + attribute.get("name") + "'");
        input.put("width", "500");
        input.put("height", "300");
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExtdatePick(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.DateField");
        input.put("name", "'" + attribute.get("name") + "'");
        String format = attribute.getString("jsEditPattern");
        if(format == null || "".equals(format)){
            input.put("format", "'Y-m-d'");
        }else{
            input.put("format", format);
        }
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExtdateTime(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        
        Thing input = new Thing("xworker.html.extjs.Items/@DateTime");
        input.put("name", "'" + attribute.get("name") + "'");
        String format = attribute.getString("jsEditPattern");
        input.put("dtSeparator", "' '");
        if(format == null || "".equals(format)){
            input.put("dateFormat", "'Y-m-d'");
            input.put("timeFormat", "'H.i.s'");
            input.put("hiddenFormat", "'Y-m-d H.i.s'");
           
        }else{
            String[] fs = format.split("[|]");
            input.put("dateFormat", "'" + fs[0] + "'");
            input.put("hiddenFormat", "'" + fs[0]);
            if(fs.length >= 2){
                input.put("timeFormat", "'" + fs[1] + "'");
                input.put("hiddenFormat",  input.get("hiddenFormat") + " " + fs[1] + "'");
            }else{
                input.put("hiddenFormat", input.get("hiddenFormat") + "'");
            }    
        }
        
        return input;
    }


    @SuppressWarnings("unchecked")
	public static Object createExtpathSelector(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
        Thing input = new Thing("xworker.html.extjs.Items/@Field");
        input.put("name", "'" + attribute.get("name") + "'");
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExtcodeEditor(ActionContext actionContext){
    	return createExttextarea(actionContext);
    	/*
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
        Thing input = new Thing("xworker.html.extjs.Ext.form.TextArea");
        input.put("name", "'" + attribute.get("name") + "'");
        return input;*/
    }

    @SuppressWarnings("unchecked")
	public static Object createExtfontSelect(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	
        Thing input = new Thing("xworker.html.extjs.Items/@Field");
        input.put("name", "'" + attribute.get("name") + "'");
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExtcolorpicker(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        
        Thing input = new Thing("xworker.html.extjs.Items/@Field");
        input.put("name", "'" + attribute.get("name") + "'");
        return input;
    }

    @SuppressWarnings({ "unused", "unchecked" })
	public static Object createExtdataSelector(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	World world = World.getInstance();
    	
        //创建TriggerField
        Thing input = new Thing("xworker.html.extjs.xworker.form.SearchOpen");
        input.put("name", "'" + attribute.get("name") + "'");        
        
        input .put("windowNs", "'XWorker_SelectThingWindow'");
        input.put("windowUrl", "'do?sc=xworker.app.view.extjs.xworker.thingEditor.XWorker_SelectThingWindow'");
        input.put("hideMode", "'offsets'");
        //log.info("create extjs open window");
        return input;
    }
    
    @SuppressWarnings("unchecked")
	public static Object createExtopenWindow(ActionContext actionContext) throws UnsupportedEncodingException{
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	World world = World.getInstance();
    	
        //创建TriggerField
        Thing input = new Thing("xworker.html.extjs.xworker.form.SearchOpen");
        input.put("name", "'" + attribute.get("name") + "'");        
        
        //创建TriggerField的事件
        Thing clickFn = new Thing("xworker.html.extjs.Ext.form.TriggerField/@onTriggerClick");
        clickFn.put("name", "onTrigger2Click");
        input.addChild(clickFn);
        
        String inputAttrs = attribute.getString("inputattrs");
        if(inputAttrs == null || "".equals(inputAttrs)){
            inputAttrs = "xworker.swt.xworker.attributeEditor.OpenWindows";
        }
        
        String[] ws = inputAttrs.split("[|]");
        Thing winThing = world.getThing(ws[0]);
        if(winThing == null){
            clickFn.put("code", "Msg.alert('窗口不存在，" + ws[0] + "');");
        }else{
            //String winns = ws[0];
            String param = "";
            if(ws.length >= 2){
                param = ws[1];
            }    
            String scPath = URLEncoder.encode(ws[0], "utf-8");
            String params = URLEncoder.encode(param, "utf-8");    
            String ns = (String) winThing.doAction("getNamespace", actionContext);
            clickFn.put("code", "\n" + 
        "var xworker = Ext.ns('Ext.xworker');\n" +    
        "xworker.remote.openWindow('" + ns + "', 'do?sc=" + scPath + "&type=extjs&params=" + params + "', this.findParentByType('form'), this);\n");
        }
        
        //input.put("width", "500");
        //input.put("style", "'width: 450'");
        input.put("hideMode", "'offsets'");
        //log.info("create extjs open window");
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExtpassword(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        
        Thing input = new Thing("xworker.html.extjs.Items/@Field");
        input.put("name", "'" + attribute.get("name") + "'");
        input.put("inputType", "'password'");
        return input;
    }
    
    public static Object createExtradio(ActionContext actionContext) throws IOException, TemplateException{
    	//World world = World.getInstance();
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	//Thing viewConfig = (Thing) field.get("viewConfig");
        Thing input = new Thing("xworker.html.extjs.Items/@RadioGroup");
        input.put("name", "'" + attribute.get("name") + "groupRadio'");
        Thing items = new Thing("xworker.html.extjs.Ext.form.RadioGroup/@24875");
        List<Thing> alis= attribute.getChilds("value");
        if(alis.size()>0){
        	 for (int i = 0; i < alis.size(); i++) {
             	
            	 Thing item = new Thing("xworker.html.extjs.Ext.form.RadioGroup/@24875/@Checkbox");
                 item.put("name", "'"+attribute.get("name")+"'");
                 item.put("boxLabel", HtmlUtil.getJsString(alis.get(i).getMetadata().getLabel()));
                 item.put("inputValue", HtmlUtil.getJsString(alis.get(i).getString("value")));
                 
                 if(i==0){
                	  item.put("checked", true);
                 }
                 items.addChild(item);
    		}
        	items.put("name", "items");
        
        }
       
        input.addChild(items);
        input.put("width", 60);
        return input;
    }
    
    
    

    @SuppressWarnings("unchecked")
	public static Object createExtselect(ActionContext actionContext) throws IOException, TemplateException{
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	Thing viewConfig = (Thing) field.get("viewConfig");
    	
    	World world = World.getInstance();
    	
        //值名和要显示的字段名
        String valueName = null;
        String displayName = null;
        //引用的数据对象
        Thing relationDataObject = world.getThing(attribute.getString("relationDataObject"));
        
        //初始化值名称和显示字段名称sc
        Thing cfg = attribute.getThing("SelectConfig@0");
        if(cfg != null){
            valueName = cfg.getString("valueName");
            displayName = cfg.getString("displayName");
        }
        if(valueName == null || "".equals(valueName)){
            if(relationDataObject != null){
                valueName = relationDataObject.getString("valueName");
            }else{
                valueName = "id";
            }
        }
        if(displayName == null || "".equals(displayName)){
            if(relationDataObject != null){
                displayName = relationDataObject.getString("displayName");
            }else{
                displayName = "value";
            }
        }
        
        //查询配置
        Thing conditionCfg = attribute.getThing("SelectCondition@");
        
        //构建ComboBox
        Thing combo = new Thing("xworker.html.extjs.Items/@ComboBox");
        combo.put("name", HtmlUtil.getJsString(attribute.getString("name") + "_combobox"));
        combo.put("displayField", HtmlUtil.getJsString(displayName));
        combo.put("valueField", HtmlUtil.getJsString(valueName));
        combo.put("hiddenName", HtmlUtil.getJsString(attribute.getString("name")));
        combo.put("mode", "'local'");
        combo.put("triggerAction", "'all'"); 
        combo.put("width", "200");
        combo.put("tpl", "'<tpl for=\".\"><div ext:qtip=\"{"+displayName+"}\" class=\"x-combo-list-item\">{"+displayName+"}</div></tpl>'");
        if(attribute.getBoolean("optional")){
            combo.put("editable", "true");
        }else{
            combo.put("editable", "false");
        }
        
        //数据源        
        Thing store = new Thing("xworker.html.extjs.Ext.form.ComboBox/@24757");
        combo.addChild(store);
        store.put("name",  "store");
        String storeJs = viewConfig.getString("storeJs");
        if(storeJs != null && !"".equals(storeJs)){
            //参数引用    
            store.put("varref", storeJs);
            combo.put("__isCombo", "true");
            //log.info("valueField=" + combo.valueField);
            combo.put("__valueName", combo.getString("valueField"));    
            String storeId = storeJs;
            if(storeId.startsWith("Ext.StoreMgr")){
                storeId = storeId.substring(storeId.indexOf("(") + 1, storeId.lastIndexOf(")"));
            }
            combo.put("__storeId", storeId);
            String displayField = combo.getString("displayField");
            if(displayField.startsWith("'")){
                displayField = displayField.trim();
                displayField = displayField.substring(displayField.indexOf("'") + 1, displayField.lastIndexOf("'"));
            }
            combo.put("__displayName", displayField);
        }else{
            if(relationDataObject == null){
                //从本对象中的可选值创建
                Thing arrayStore = new Thing("xworker.html.extjs.Stores/@ArrayStore");
                store.addChild(arrayStore);
                arrayStore.put("idProperty", "'id'");
                arrayStore.put("idIndex", "0");
                arrayStore.put("autoDestroy", "true");
                String id = "'tmp_arraystore_" + System.currentTimeMillis() + "_" + Math.abs(attribute.getMetadata().getPath().hashCode()) + "'";
                arrayStore.put("storeId", id);
                combo.put("__isCombo", "true");
                combo.put("__valueName", valueName);
                combo.put("__displayName", displayName);
                combo.put("__storeId", id);
                
                //数据源的字段
                Thing fields = new Thing("xworker.html.extjs.Ext.data.Store/@fields");
                fields.put("code", "['" + valueName + "','" + displayName + "']");
                fields.put("name", "fields");
                //数据
                String dataStr = "";
                for(Thing valueChild : attribute.getChilds("value")){
                    if(dataStr != ""){
                        dataStr = dataStr + ",";
                    }
                    String value = valueChild.getString("value");
                    if(value != null){
                    	value = "'" + value.replaceAll("'", "\\\\'") + "'";
                    }
                    dataStr = dataStr + "[" + value + ", " + HtmlUtil.getJsString(valueChild.getMetadata().getLabel()) + "]";
                }
                dataStr = dataStr + "";
                Thing datas = new Thing("xworker.html.extjs.Ext.data.Store/@24488");
                datas.put("code", dataStr);
                datas.put("name", "data");
                arrayStore.addChild(fields);
                arrayStore.addChild(datas);
            }else{
                //创建新的store
            	/*
                Thing jsonStore = new Thing("xworker.html.extjs.Stores/@JsonStore");                
                store.addChild(jsonStore);
                jsonStore.put("autoLoad", "true");
                jsonStore.put("idProperty", relationDataObject.getString("storeIdProperty"));
                String url = "'do'";
                jsonStore.put("url",  url);
                //数据源的字段
                Thing fields = new Thing("xworker.html.extjs.Ext.data.Store/@fields");
                fields.put("code", "['" + valueName + "','" + displayName + "']");
                fields.put("name", "fields");
                jsonStore.addChild(fields);
                //------------------base parameters---------------
                Thing baseParameters = new Thing("xworker.html.extjs.Ext.data.Store/@24486");
                baseParameters.put("name",  "baseParams");
                baseParameters.put("code", "objPath:'" + relationDataObject.getMetadata().getPath() + "'");
                baseParameters.put("code", baseParameters.get("code") + ",\nsc:'xworker.appview.web.extjs.server.DataProvider/@queryData'");
                if(conditionCfg != null && !"".equals(conditionCfg)){
                    baseParameters.put("code", baseParameters.get("code") + ",\nconditionPath:'" + conditionCfg.getMetadata().getPath() + "'");
                }
                
                if(relationDataObject.getBoolean("paging") || relationDataObject.getBoolean("dobj_paging") || relationDataObject.getBoolean("splitPage")){
                    String pageSize = relationDataObject.getString("dobj_pageSize");
                    if(pageSize == null || "".equals(pageSize)){
                        pageSize = relationDataObject.getString("pageSize");
                    }
                    if(pageSize == null || "".equals(pageSize)){
                        pageSize = "20";
                    }
                    baseParameters.put("code", baseParameters.get("code") + ",\nstart: 0");
                    baseParameters.put("code", baseParameters.get("code") + ",\nlimit: " + pageSize);
                }
                String storeSortField = relationDataObject.getString("storeSortField"); 
                if(storeSortField != null && !"".equals(storeSortField)){
                    baseParameters.put("code", baseParameters.get("code") + ",\nsort: " + HtmlUtil.getJsString(storeSortField) + "");
                    baseParameters.put("code", baseParameters.get("code") + ",\ndir: " + HtmlUtil.getJsString(relationDataObject.getString("storeSortDir")) + "");
                }
                jsonStore.addChild(baseParameters);
                */
                Thing jsonStore = new Thing("xworker.app.view.extjs.data.DataObjectJsonStore/@DataObjectJsonStore");
                jsonStore.put("dataObject", relationDataObject.getMetadata().getPath());
                store.addChild(jsonStore);
                
                //标识
                String cmpId = (String) actionContext.get("cmpId");
                if(cmpId != null && !"".equals(cmpId)){
                	String storeId = cmpId + "_" + attribute.get("name") + "_store";
                	jsonStore.put("name", storeId);
                	jsonStore.put("storeId", storeId);
                	jsonStore.put("varglobal", "true");
                	//jsonStore.put("varname", storeId);
                	
                    //jsonStore.put("storeId", HtmlUtil.getJsString(cmpId + "_" + attribute.get("name") + "_store"));
                    //jsonStore.put("varname", cmpId + "_" + attribute.get("name") + "_store");
                    //                    
                    combo.put("__isCombo", "true");
                    combo.put("__valueName", valueName);
                    combo.put("__displayName", displayName);
                    combo.put("__storeId", jsonStore.getString("storeId"));
                }    
            }
        }
        
        return combo;
    }


    @SuppressWarnings("unchecked")
	public static Object createExtmultSelect(ActionContext actionContext) throws IOException, TemplateException{
    	World world = World.getInstance();
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
    	Thing viewConfig = (Thing) field.get("viewConfig");
        
        //值名和要显示的字段名
        String valueName = null;
        String displayName = null;
        //引用的数据对象
        Thing relationDataObject = world.getThing(attribute.getString("relationDataObject"));
        
        //初始化值名称和显示字段名称
        Thing cfg = attribute.getThing("SelectConfig@0");
        if(cfg != null){
            valueName = cfg.getString("valueName");
            displayName = cfg.getString("displayName");
        }
        if(valueName == null || "".equals(valueName)){
            if(relationDataObject != null){
                valueName = relationDataObject.getString("valueName");
            }else{
                valueName = "id";
            }
        }
        if(displayName == null || "".equals(displayName)){
            if(relationDataObject != null){
                displayName = relationDataObject.getString("displayName");
            }else{
                displayName = "value";
            }
        }
        
        //查询配置
        Thing conditionCfg = attribute.getThing("SelectCondition@");
        
        //构建ComboBox
        Thing combo = new Thing("xworker.html.extjs.Ext.ux.form.LovCombo");
        combo.put("name", HtmlUtil.getJsString(attribute.getString("name") + "_combobox"));
        combo.put("displayField", HtmlUtil.getJsString(displayName));
        combo.put("valueField", HtmlUtil.getJsString(valueName));
        combo.put("hiddenName", HtmlUtil.getJsString(attribute.getString("name")));
        combo.put("mode", "'local'");
        combo.put("triggerAction", "'all'"); 
        //数据源
        
        Thing store = new Thing("xworker.html.extjs.Ext.form.ComboBox/@24757");
        combo.addChild(store);
        store.put("name", "store");
        String storeJs = viewConfig.getString("storeJs");
        if(storeJs != null && !"".equals(storeJs)){
            //参数引用
            store.put("varref", storeJs);
            combo.put("__isCombo", "true");
            log.info("valueField=" + combo.getString("valueField"));
            combo.put("__valueName", combo.getString("valueField"));    
            String storeId = viewConfig.getString("storeJs");
            if(storeId.startsWith("Ext.StoreMgr")){
                storeId = storeId.substring(storeId.indexOf("(") + 1, storeId.lastIndexOf(")"));
            }
            combo.put("__storeId", storeId);
            String displayField = combo.getString("displayField");
            if(displayField.startsWith("'")){
                displayField = displayField.trim();
                displayField = displayField.substring(displayField.indexOf("'") + 1, displayField.lastIndexOf("'"));
            }
            combo.put("__displayName", displayField);
        }else{
            if(relationDataObject == null){
                //从本对象中的可选值创建
            	Thing arrayStore = new Thing("xworker.html.extjs.Stores/@ArrayStore");
                store.addChild(arrayStore);
                arrayStore.put("idProperty", "'id'");
                arrayStore.put("idIndex", "0");
                arrayStore.put("autoDestroy", "true");
                String id = "'tmp_arraystore_" + System.currentTimeMillis() + "_" + Math.abs(attribute.getMetadata().getPath().hashCode()) + "'";
                arrayStore.put("storeId", id);
                combo.put("__isCombo", "true");
                combo.put("__valueName", valueName);
                combo.put("__displayName", displayName);
                combo.put("__storeId", id);
                
                //数据源的字段
                Thing fields = new Thing("xworker.html.extjs.Ext.data.Store/@fields");
                fields.put("code", "['" + valueName + "','" + displayName + "']");
                fields.put("name", "fields");
                //数据
                String dataStr = "";
                for(Thing valueChild : attribute.getChilds("value")){
                    if(dataStr != ""){
                        dataStr = dataStr + ",";
                    }
                    dataStr = dataStr + "[" + HtmlUtil.getJsString(valueChild.getString("value")) + ", " + HtmlUtil.getJsString(valueChild.getMetadata().getLabel()) + "]";
                }
                dataStr = dataStr + "";
                Thing datas = new Thing("xworker.html.extjs.Ext.data.Store/@24488");
                datas.put("code", dataStr);
                datas.put("name", "data");
                arrayStore.addChild(fields);
                arrayStore.addChild(datas);
            }else{
                //创建新的store
            	Thing jsonStore = new Thing("xworker.html.extjs.Stores/@JsonStore");
                store.addChild(jsonStore);
                jsonStore.put("autoLoad", "true");
                jsonStore.put("idProperty", relationDataObject.getString("storeIdProperty"));
                String url = "'do'";
                jsonStore.put("url",  url);
                //数据源的字段
                Thing fields = new Thing("xworker.html.extjs.Ext.data.Store/@fields");
                fields.put("code", "['" + valueName + "','" + displayName + "']");
                fields.put("name", "fields");
                jsonStore.addChild(fields);
                //------------------base parameters---------------
                Thing baseParameters = new Thing("xworker.html.extjs.Ext.data.Store/@24486");
                baseParameters.put("name", "baseParams");
                baseParameters.put("code", "objPath:'" + relationDataObject.getMetadata().getPath()+ "'");
                baseParameters.put("code", baseParameters.get("code") + ",\nsc:'xworker.appview.web.extjs.server.DataProvider/@queryData'");
                if(conditionCfg != null && !"".equals(conditionCfg)){
                    baseParameters.put("code", baseParameters.get("code") + ",\nconditionPath:'" + conditionCfg.getMetadata().getPath() + "'");
                }
                if(relationDataObject.getBoolean("paging") || relationDataObject.getBoolean("dobj_paging") || relationDataObject.getBoolean("splitPage")){
                    String pageSize = relationDataObject.getString("dobj_pageSize");
                    if(pageSize == null || "".equals(pageSize)){
                        pageSize = relationDataObject.getString("pageSize");
                    }
                    if(pageSize == null || "".equals(pageSize)){
                        pageSize = "20";
                    }
                    baseParameters.put("code", baseParameters.get("code") + ",\nstart: 0");
                    baseParameters.put("code", baseParameters.get("code") + ",\nlimit: " + pageSize);
                }
                String storeSortField = relationDataObject.getString("storeSortField");
                if(storeSortField != null && !"".equals(storeSortField)){
                    baseParameters.put("code", baseParameters.get("code") + ",\nsort: " + HtmlUtil.getJsString(storeSortField) + "");
                    baseParameters.put("code", baseParameters.get("code") + ",\ndir: " + HtmlUtil.getJsString(relationDataObject.getString("storeSortDir")) + "");
                }
                jsonStore.addChild(baseParameters);
                //标识
                String cmpId = (String) actionContext.get("cmpId");
                if(cmpId != null && !"".equals(cmpId)){
                    jsonStore.put("storeId", HtmlUtil.getJsString(cmpId + "_" + attribute.get("name") + "_store"));
                    jsonStore.put("varname", cmpId + "_" + attribute.get("name") + "_store");
                    //默认生成到全局共享
                    jsonStore.put("varglobal", "true");
                    
                    combo.put("__isCombo", "true");
                    combo.put("__valueName", valueName);
                    combo.put("__displayName", displayName);
                    combo.put("__storeId", jsonStore.getString("storeId"));
                }    
            }
        }
        
        return combo;
    }
    
    public static Object createExtinputSelect(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing combo = (Thing) self.doAction("createExtselect", actionContext);
        combo.put("editable", "true");
        return combo;
    }

    public static void createExtPagingToolbar(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing control = (Thing) actionContext.get("control");
    	Thing dataObject= (Thing) actionContext.get("dataObject");
        
        Thing bbar = self.getThing("bbar@0");
        if(bbar == null){
            bbar = new Thing("xworker.html.extjs.Ext.Panel/@25317");    
            bbar.put("name", "bbar");
            control.addChild(bbar, false);
        }
        
        Thing pbar = new Thing("xworker.html.extjs.Ext.Panel/@25317/@PagingToolbar");
        pbar.initDefaultValue();
        bbar.addChild(pbar, false);
        
        //store，控件自带的store通常id规则是控件id_store
        Thing store = new Thing("xworker.html.extjs.Ext.PagingToolbar/@25309");        
        store.put("name", "store");
        String storeName = (String) self.get("id");
        if(storeName.startsWith("'")){
        	storeName = storeName.substring(1, storeName.length() - 1);
        }
        
    	storeName = storeName + "_store";
        store.put("varref", "'" + storeName + "'");
        pbar.addChild(store);
        
        //设置参数
        setValue("paging_afterPageText", "dobj_afterPageText", "afterPageText", true, dataObject, self, pbar);
        setValue("paging_beforePageText", "dobj_beforePageText", "beforePageText", true, dataObject, self, pbar);
        setValue("paging_displayInfo", "dobj_displayInfo", "displayInfo", false, dataObject, self, pbar);
        setValue("paging_displayMsg", "dobj_displayMsg", "displayMsg", true, dataObject, self, pbar);
        setValue("paging_emptyMsg", "dobj_emptyMsg", "emptyMsg", true, dataObject, self, pbar);
        setValue("paging_firstText", "dobj_firstText", "firstText", true, dataObject, self, pbar);
        setValue("paging_lastText", "dobj_lastText", "lastText", true, dataObject, self, pbar);
        setValue("paging_nextText", "dobj_nextText", "nextText", true, dataObject, self, pbar);
        setValue("pageSize", "dobj_pageSize", "pageSize", false, dataObject, self, pbar);
        setValue("paging_prependButtons", "dobj_prependButtons", "prependButtons", false, dataObject, self, pbar);
        setValue("paging_prevText", "dobj_prevText", "prevText", true, dataObject, self, pbar);
        setValue("paging_refreshText", "dobj_refreshText", "refreshText", true, dataObject, self, pbar);
        
        
    }
    
    public static void setValue(String objName, String gridName, String barName, boolean isString, Thing obj, Thing grid, Thing bar){
        String value = grid.getString(gridName);
        if(value == null || "".equals(value)){
            value = obj.getString(objName);
        }
        if(value != null && !"".equals(value)){
            if(isString && !value.startsWith("'")){
                bar.put(barName, "'" + value + "'");
            }else{
                bar.put(barName, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static Object createExtlabel(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        Thing input = new Thing("xworker.html.extjs.Ext.form.TextField");
        input.put("name", "'" + attribute.get("name") + "'");
        input.put("readOnly", "true");
        return input;
    }

    @SuppressWarnings("unchecked")
	public static Object createExttime(ActionContext actionContext){
    	Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
    	Thing attribute = (Thing) field.get("attribute");
        
        Thing input = new Thing("xworker.html.extjs.Items/@TimeField");
        input.put("name", "'" + attribute.get("name") + "'");
        String format = attribute.getString("jsEditPattern");
        if(format == null || "".equals(format)){
            input.put("format", "'H.i.s'");
        }else{
            input.put("format", format);
        }
        return input;
    }

    public static void createStoreListener(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing listenerThing = self.getThing("StoreListeners@0");
    	if(listenerThing != null){
    		Thing store = (Thing) actionContext.get("store");
            Thing listener = store.getThing("listeners@0");
            if(listener == null){
                listener = new Thing("xworker.html.extjs.Ext.util.Observable/@25649");
                listener.put("name", "listeners");
                store.addChild(listener);
            }
            for(Thing child : listenerThing.getChilds()){
            	listener.addChild(child.detach());
            }
    	}
    }
    
    public static void createStoreSaveListener(ActionContext actionContext){
    	Thing store = (Thing) actionContext.get("store");
        Thing listener = store.getThing("listeners@0");
        if(listener == null){
            listener = new Thing("xworker.html.extjs.Ext.util.Observable/@25649");
            listener.put("name", "listeners");
            store.addChild(listener);
        }
        
        for(Thing child : listener.getChilds()){
        	if("save".equals(child.getMetadata().getName())){
        		return;
        	}        	
        }
        
        Thing save = new Thing("xworker.html.extjs.Ext.util.Observable/@25649/@Function");
        save.put("name", "save");
        save.put("params", "store, batch, data");
        save.put("code", "if(data['create']){\n" + 
        		"    if(store.reloadOptions){\n" + 
        		"        store.reload(store.reloadOptions);\n" + 
        		"    }else{\n" + 
        		"        store.reload();\n" + 
        		"    }\n" + 
        		"}");
        listener.addChild(save);
    }

    public static void createStoreExceptionListener(ActionContext actionContext){
        Thing store = (Thing) actionContext.get("store");
        
        Thing listener = store.getThing("listeners@0");
        if(listener == null){
            listener = new Thing("xworker.html.extjs.Ext.util.Observable/@25649");
            listener.put("name", "listeners");
            store.addChild(listener);
        }
        
        for(Thing child : listener.getChilds()){
        	if("exception".equals(child.getMetadata().getName())){
        		return;
        	}        	
        }
        
        Thing save = new Thing("xworker.html.extjs.Ext.util.Observable/@25649/@Function");
        save.put("name", "exception");
        save.put("params", "proxy, type, action, options, response, arg");
        String code = "\n" +
        		"var msg = '';\n" +
        		"if(Ext.isDefined(response.responseText)){\n" +
        		"    var o = Ext.util.JSON.decode(response.responseText);\n" +
        		"    msg = o.msg;\n" +
        		"}else{\n" +
        		"    msg = response.message;\n" +
        		"}\n" +
        		//"alert(msg);\n" + 
        		"if(msg){\n" + 
        		"    Ext.Msg.alert(type, msg);\n" + 
        		"}else{\n" + 
        		"    Ext.Msg.alert(type, '超时或发生其他错误,responseText=' + response.responseText);\n" + 
        "}";
        save.put("code", code);
        listener.addChild(save);
    }

    public static Object getDataObject(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        
        if(dataObject == null){
        	log.info("DataObjectComponent does not set a DataObject, path=" + self.getMetadata().getPath());
        }
        
        return dataObject;
    }

    public static Object getComponentId(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String id = self.getString("id");
        if(id == null){
            return null;
        }else{
            if(id.startsWith("'") || id.startsWith("\"")){
                id = id.substring(1, id.length());
            }
            if(id.endsWith("'") || id.endsWith("\"")){
                id = id.substring(0, id.length() - 1);
            }
            
            return id;
        }
    }

    public static Object trimId(ActionContext actionContext){
    	String id = (String) actionContext.get("id");
    	
        if(id.startsWith("'") || id.startsWith("\"")){
            id = id.substring(1, id.length());
        }
        if(id.endsWith("'") || id.endsWith("\"")){
            id = id.substring(0, id.length() - 1);
        }
        
        return id;
    }

}
