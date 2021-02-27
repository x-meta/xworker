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
package xworker.app.view.extjs.widgets.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObjectThingUtils;
import xworker.lang.executor.Executor;

public class DataObjectFormPanelCreator {
	private static final String TAG = DataObjectFormPanelCreator.class.getName();
	
    @SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //-----------------获取数据对象的定义------------
        Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
        if(dataObject == null){
            Executor.warn(TAG, "DataObjectFormPanel: dataObject is null - " + self.getString("dataObject"));
            return null;//self.doAction("toJavaScriptCode", actionContext);
        }
        
        //组件标识，子控件可能会用到
        actionContext.peek().put("cmpId", self.doAction("getComponentId", actionContext));
        
        
        //-----------------要用到的数据对象的相关属性列表---------
        List<Thing> keys = DataObjectThingUtils.getKeyAttributes(dataObject); //(List<Thing>) dataObject.doAction("getKeyAttributes", actionContext);
        List<Map<String, Object>> editorAttributes = DataObjectThingUtils.getEditorAttributes(dataObject,  self.getString("df_editorType"));//(List<Map<String, Object>>) dataObject.doAction("getEditorAttributes", actionContext, UtilMap.toMap(new Object[]{"editorType", self.getString("df_editorType")}));
        List<Thing> readerAttributes = DataObjectThingUtils.getReaderAttributes(dataObject); //(List<Thing>) dataObject.doAction("getReaderAttributes", actionContext);
        
        //-----------------创建编辑表单---------------
        Thing formPanel = (Thing) self.doAction("createFormPanel", actionContext);
        //如果子节点有输入方式为file或filePath，那么把表单设置为提交文件的类型
        for(Thing attribute : dataObject.getChilds("attribute")){
        	String inputtype = attribute.getString("inputtype");
        	if("file".equals(inputtype) || "filePath".equals(inputtype)){
        		formPanel.put("fileUpload", "true");
        		break;
        	}
        }
        
        //------------------创建reader------------------
        self.doAction("createReader", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "keys",keys, "attributes",readerAttributes}));
        
        //-----------------创建基本参数-----------------
        self.doAction("createBaseParameters", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "dataObject",dataObject, "keys",keys, "attributes",readerAttributes}));
        
        //------------------创建表单内容---------------   
        self.doAction("createFormItems", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "keys",keys, "attributes",editorAttributes, "dataObject",dataObject}));
        
        //---------------创建装载、提交等函数-----------
        //装载函数
        //self.doAction("createLoadFunction", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "dataObject",dataObject, "keys",keys}));
        //创建函数
        //self.doAction("createCreateFunction", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "dataObject",dataObject, "keys",keys}));
        //更新函数
        //self.doAction("createUpdateFunction", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "dataObject",dataObject, "keys",keys}));
        //删除函数
        //self.doAction("createDestroyFunction", actionContext, UtilMap.toMap(new Object[]{"formPanel",formPanel, "dataObject",dataObject, "keys",keys}));
        
        //清除自定义的属性
        for(String key : formPanel.getAttributes().keySet()){
            if(key.startsWith("df_")){
                formPanel.put(key, null);
            }
        }
        
        //---------------生成JavaScript代码-------------
        return formPanel.doAction("toJavaScriptCode", actionContext);
    }

    public static Object getExtType(ActionContext actionContext){
        return "Ext.form.FormPanel";
    }

    public static Object getDataObject(ActionContext actionContext){    	
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        Thing dataObject = world.getThing(self.getString("df_dataObject"));
        if(dataObject == null){
        	dataObject = world.getThing(self.getString("dataObject"));
        }
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        
        return dataObject;
    }

    public static Object createFormPanel(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        Thing formPanel = self.detach();
        
        List<Thing> dataObjects = formPanel.getChilds("dataObjects@");
        for(Thing dobj : dataObjects){
            formPanel.removeChild(dobj);
        }
        formPanel.set("descriptors", "xworker.html.extjs.Ext.form.FormPanel");
        
        return formPanel;
    }

    public static Object createFormData(ActionContext actionContext){
        return null;
    }
    
    public static Map<String, List<Map<String, Object>>> toGroups(List<Map<String, Object>> attributes){
    	Map<String, List<Map<String, Object>>> groups = new HashMap<String, List<Map<String,Object>>>();
    	for(Map<String, Object> attr : attributes){
    		String group = (String) attr.get("group");
    		if(group == null || "".equals(group)){
    			group = "默认";
    		}
    		
    		for(String g : group.split("[,]")){
    			List<Map<String, Object>> attrs = groups.get(g);
    			if(attrs == null){
    				attrs = new ArrayList<Map<String, Object>>();
    				groups.put(g, attrs);
    			}
    			
    			attrs.add(attr);
    		}
    	}
    	
    	return groups;
    }

    @SuppressWarnings("unchecked")
	public static void createFormItems(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
    	List<Map<String, Object>> attributes = (List<Map<String, Object>>) actionContext.get("attributes");    	
        
        //创建界面定义，如果没有定义items子事物或是追加模式
        List<Thing> oldItems = (List<Thing>) formPanel.get("items@");
        if(oldItems.size() == 0 || self.getBoolean("df_appendItems")){
            //----------------表单布局参数-------------
            //编辑列数
            int column = self.getInt("df_column", 0);
            if(column == 0){
                column = dataObject.getInt("editCols", 2);
            }
            if(column == 0){
                column = 2;
            }
            
            //标签默认对齐方式
            String columnAlign = self.getString("df_columnAlign");
            if(columnAlign == null || columnAlign == ""){
                columnAlign = dataObject.getString("columnAlign");
            }
            String columnVAlign = self.getString("df_columnVAlign");
            if(columnVAlign == null || columnVAlign == ""){
                columnVAlign = dataObject.getString("columnVAlign");
            }
            if(columnAlign == null){
                columnAlign = "right";
            }
            if(columnVAlign == null){
                columnVAlign = "baseline";
            }
            
            //------------整理隐藏字段和要编辑的字段---------
            Map<String, List<Map<String, Object> >> fields = new HashMap<String, List<Map<String, Object> >>();
            fields.put("hiddens", new ArrayList<Map<String, Object> >());
            fields.put("rows", new ArrayList<Map<String, Object> >());
            
            for(Map<String, Object> attribute : attributes){
                if("hidden".equals(((Thing) attribute.get("viewConfig")).getString("inputtype"))){
                    fields.get("hiddens").add(attribute);
                }else{
                    fields.get("rows").add(attribute);
                }
            }
            
            //----------主表单使用fit布局，包含hiddenPanel和一个tablePanel----
            formPanel.put("layout", "'auto'");    
            Thing formItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            formItems.put("name", "items");
            formPanel.addChild(formItems);
            
            //创建隐藏字段用的面板
            Thing hiddenPanel = new Thing("xworker.html.extjs.Ext.Panel");
            //hiddenPanel.put("hidden", "true");    
            hiddenPanel.put("layout", "'table'");
            Thing hiddenItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            hiddenItems.put("name", "items");
            hiddenPanel.addChild(hiddenItems);
            formItems.addChild(hiddenPanel);
            //创建隐藏字段
            for(Map<String, Object> row : fields.get("hiddens")){
                Thing hidden = new Thing("xworker.html.extjs.Ext.form.Hidden");
                hidden.put("name", "'" + ((Thing) row.get("attribute")).getString("name") + "'");
                String defaultValue = ((Thing) row.get("attribute")).getStringBlankAsNull("default");
                if(defaultValue != null){
                	hidden.put("value", defaultValue);
                }
                
                hiddenItems.addChild(hidden);
            }
            
            Map<String, List<Map<String, Object>>> groups = toGroups(fields.get("rows"));
            //不具有分组的情况
            if(groups.size() <= 1){
            	createFormTablePanel(fields.get("rows"), formItems, oldItems, column, self, actionContext);
            }else{
            	//具有分组的情况，创建一个TabFolder
            	Thing tabFolder = new Thing("xworker.html.extjs.Items/@TabPanel");
            	formItems.addChild(tabFolder);
            	Thing tabItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            	tabFolder.addChild(tabItems);
            	for(String group : groups.keySet()){
            		Thing panel = createFormTablePanel(groups.get(group), tabItems, oldItems, column, self, actionContext);
            		panel.set("title", "'" + group + "'");
            	}
            	
            }
        }
    }
    
    /**
     * 创建一个form布局的面板。
     * 
     * @param rows
     * @param formItems
     * @param oldItems
     * @param column
     * @param self
     * @param actionContext
     */
    public static Thing createFormTablePanel(List<Map<String, Object>> rows, Thing formItems, List<Thing> oldItems, int column, Thing self, ActionContext actionContext){
    	  //创建表格布局面板
        Thing layoutPanel = new Thing("xworker.html.extjs.Ext.Panel");
        layoutPanel.put("forceLayout", "true");
        layoutPanel.put("layout", "'table'");
        formItems.addChild(layoutPanel);
        Thing layoutConfig = new Thing("xworker.html.extjs.Ext.Container/@24356");
        layoutConfig.put("name", "layoutConfig");
        Thing tableLayout = new Thing("xworker.html.extjs.Ext.Container/@24356/@TableLayout");
        tableLayout.put("columns", "" + (column * 2));
        Thing tableAttrs = new Thing("xworker.html.extjs.Ext.layout.TableLayout/@25174");
        tableAttrs.put("name", "tableAttrs");
        tableAttrs.put("code", "cellspacing: 5");
    
        layoutPanel.addChild(layoutConfig);
        layoutConfig.addChild(tableLayout);
        tableLayout.addChild(tableAttrs);
        
        //条目
        Thing items = new Thing("xworker.html.extjs.Ext.Container/@24354");
        layoutPanel.addChild(items);
        items.put("name", "items");
        if(oldItems != null){
            //加入已定义的条目
            for(Thing item : oldItems){
                items.addChild(item);
            }
        }
        for(Map<String, Object> row : rows){
        	Thing viewConfig = (Thing) row.get("viewConfig");
        	Thing attribute = (Thing) row.get("attribute");
        	
            String type = viewConfig.getString("inputtype");
            if(type == null || "".equals(type)){
                type = "text";
            }
            int rowspan = viewConfig.getInt("rowspan", 1);
            int colspan = viewConfig.getInt("colspan", 1);
            if(!viewConfig.getBoolean("showLabel")){
                colspan = 2 * colspan;
            }else{
                colspan = 2 * colspan - 1;
                //创建标签
                Thing label = new Thing("xworker.html.extjs.Items/@Label");
                label.put("text", "'" + attribute.getMetadata().getLabel() + ":'");
                label.put("cls", "'x-form-field'");
                //是标签的id完全随机，如果标签id相同，不同的界面之间会串
                int hashCode = attribute.getMetadata().getPath().hashCode();
                hashCode += label.getMetadata().getPath().hashCode();
                label.put("id", "'" + attribute.getString("name") + "-" + hashCode + "'");
                label.put("colspan", "1");
                label.put("rowspan",  "" + rowspan);
                items.addChild(label);
            }    
         
            Thing childItem = (Thing) self.doAction("createExtFieldEditor", actionContext, UtilMap.toMap(new Object[]{"field", row}));
            if(childItem != null){
                items.addChild(childItem, false);
            }
        }
        
        return layoutPanel;
    }

    public static void createBaseParameters(ActionContext actionContext){
        Thing formPanel = (Thing) actionContext.get("formPanel");
        Thing dataObject = (Thing) actionContext.get("dataObject");
        
        if(formPanel.getThing("baseParams@0") == null){
            Thing baseParams = new Thing("xworker.html.extjs.Ext.form.BasicForm/@24695");
            baseParams.put("name", "baseParams");
            String dataObjectPath = dataObject.getMetadata().getPath();
            baseParams.put("code", "dataObjectPath: '" + dataObjectPath + "'");
            formPanel.addChild(baseParams, false);
        }
    }

    @SuppressWarnings("unchecked")
	public static void createReader(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	//Thing dataObject = (Thing) actionContext.get("dataObject");
    	List<Thing> keys = (List<Thing>) actionContext.get("keys");
    	List<Thing> attributes = (List<Thing>) actionContext.get("attributes");
    	
        Thing reader = formPanel.getThing("reader@0");
        if(reader == null){
            reader = new Thing("xworker.html.extjs.Ext.form.BasicForm/@24701");
            reader.put("name", "reader");
            Thing jasonReader = new Thing("xworker.html.extjs.Ext.data.DataReaders/@JsonReader");
            if(keys != null && keys.size() > 0){
                jasonReader.put("idProperty", "'" + keys.get(0).getString("name") + "'");
            }
            jasonReader.put("root", "'data'");
            jasonReader.put("messageProperty", "'msg'");
            Thing fields = new Thing("xworker.html.extjs.Ext.data.DataReader/@24399");
            fields.put("name", "fields");
            for(Thing attribute : attributes){
                Thing field = (Thing) self.doAction("createExtStoreField", actionContext, UtilMap.toMap(new Object[]{"field", attribute}));
                if(field != null){
                    fields.addChild(field, false);
                }
            }
            
            reader.addChild(jasonReader, false);
            jasonReader.addChild(fields, false);
            formPanel.addChild(reader, false);
        }
    }

    @SuppressWarnings("unchecked")
	public static void createLoadFunction(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
    	List<Thing> keys = (List<Thing>) actionContext.get("keys");
    	//List<Map<String, Object>> attributes = (List<Map<String, Object>>) actionContext.get("attributes");
        
        //装载函数
        Thing loadFunction = new Thing("xworker.html.extjs.Function");
        loadFunction.put("name", "doLoad");
        String params = "";
        String ps = "";
        for(int i=0; i<keys.size(); i++){
            if(params != ""){
                params = params + ",";        
                ps = ps + " + '";
            }
            String paramName = keys.get(i).getString("name");    
            ps = ps + "&" + paramName + "=' + " + paramName;
            params = params + paramName;
        }
        if(keys.size() == 0){
            Executor.warn(TAG, "dataObject have no keys, dataObjectPath=" + self.getString("dataObject"));
        }
        loadFunction.put("params", params);
        String loadUrl = self.getString("df_formReadUrl");
        if(loadUrl == null || "".equals(loadUrl)){
            loadUrl = dataObject.getString("formReadUrl");
        }
        if(ps == ""){
            ps = "'";
        }
        loadUrl = loadUrl + ps;
        String msg = self.getString("df_readMsg");
        if(msg == null || "".equals(msg)){
            msg = dataObject.getString("readMsg");
        }
        if(msg == null || "".equals(msg)){
            msg = "正在加载，请稍候...";
        }
        
        loadFunction.put("code", "Ext.getCmp(" + self.getString("id") + ").getForm().load({url:'" + loadUrl + "', waitMsg:'" + msg + "'});");
        formPanel.addChild(loadFunction);
    }

    @SuppressWarnings("unchecked")
	public static void createCreateFunction(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
    	List<Thing> keys = (List<Thing>) actionContext.get("keys");
    	//List<Map<String, Object>> attributes = (List<Map<String, Object>>) actionContext.get("attributes");
        
        
        //创建
        Thing createFunction = new Thing("xworker.html.extjs.Function");
        createFunction.put("params", "");
        createFunction.put("name", "doCreate");
        String createUrl = self.getString("df_formCreateUrl");
        if(createUrl == null || "".equals(createUrl)){
            createUrl = dataObject.getString("formCreateUrl");
        }
        String msg = self.getString("df_readMsg");
        if(msg == null || "".equals(msg)){
            msg = dataObject.getString("readMsg");
        }
        if(msg == null || "".equals(msg)){
            msg = "正在保存数据，请稍候...";
        }
        
        String code = "";
        String keyName = "";
        if(keys.size() > 0){
            keyName = keys.get(0).getString("name");
        }
        if(!self.getBoolean("df_bind") || (self.getBoolean("df_bind") && self.getBoolean("df_createToServer"))){
            //先提交至服务器
            code = "//提交数据\n" +
"        Ext.getCmp(" + self.getString("id") +").getForm().submit({\n" +
"            url:'" + createUrl + "', \n" +
"            waitMsg:'" + msg + "',\n" +
"            success: function(form, action) {";
            if(self.getBoolean("df_bind")){
                String bindId = self.getString("df_bindId");
                if("grid".equals(self.getString("df_bindType"))){
                    code = code + "\n        var store = Ext.getCmp(" + bindId + ").store;";
                }else{
                    code = code + "\n        var store = Ext.StoreMgr.get(" + bindId + ");";
                }
                code = code + "\n        var formPanel = Ext.getCmp(" + self.getString("id") + ").getForm()";
                code = code + "\n        formPanel.setValues(action.result.record);";
                code = code + "\n        var record = new store.recordType(action.result.record, action.result.record." + keyName + "});";
                code = code + "\n        store.add(record);"; 
            }
        code = code  + "\n" +
"               Ext.Msg.alert('成功', action.result.msg);\n" +
"            },\n" +
"            failure: function(form, action) {\n" +
"                switch (action.failureType) {\n" +
"                    case Ext.form.Action.CLIENT_INVALID:\n" +
"                        Ext.Msg.alert('失败', '数据校验失败');\n" +
"                        break;\n" +
"                    case Ext.form.Action.CONNECT_FAILURE:\n" +
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" +
"                        break;\n" +
"                    case Ext.form.Action.SERVER_INVALID:\n" +
"                       Ext.Msg.alert('失败', action.result.msg);\n" +
"                }\n" +
"            }\n" +
"        });\n";

        }else if(self.getBoolean("df_bind")){
            String bindId = self.getString("df_bindId");
            code = "var formPanel = Ext.getCmp(" + self.getString("id") + ").getForm()";
            code = code + "\nvar values = formPanel.getValues();";
            if("grid".equals(self.getString("df_bindType"))){
                code = code + "\nvar store = Ext.getCmp(" + bindId + ").store;";
            }else{
                code = code + "\nvar store = Ext.StoreMgr.get(" + bindId + ");";
            }
            code = code + "\nvar record = new store.recordType(values);";
            code = code + "\nstore.insert(store.getCount() - 1, record);";
        }else{
            code = "//提交数据\n" +
"        Ext.getCmp(" + self.get("id") + ").getForm().submit({\n" +
"            url:'" + createUrl + "', \n" +
"            waitMsg:'" + msg + "',\n" +
"            success: function(form, action) {\n" + 
"               Ext.Msg.alert('成功', action.result.msg);\n" +
"            },\n" +
"            failure: function(form, action) {\n" +
"                switch (action.failureType) {\n" +
"                    case Ext.form.Action.CLIENT_INVALID:\n" +
"                        Ext.Msg.alert('失败', '数据校验失败');\n" +
"                        break;\n" +
"                    case Ext.form.Action.CONNECT_FAILURE:\n" +
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" +
"                        break;\n" +
"                    case Ext.form.Action.SERVER_INVALID:\n" +
"                       Ext.Msg.alert('失败', action.result.msg);\n" +
"                }\n" +
"            }\n" +
"        });\n";

        }
        createFunction.put("code", code);
        
        formPanel.addChild(createFunction);
    }

	public static void createUpdateFunction(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
    	//List<Thing> keys = (List<Thing>) actionContext.get("keys");
    	//List<Map<String, Object>> attributes = (List<Map<String, Object>>) actionContext.get("attributes");
        
        //创建
        Thing createFunction = new Thing("xworker.html.extjs.Function");
        createFunction.put("params", "");
        createFunction.put("name", "doUpdate");
        String createUrl = self.getString("df_formUpdateUrl");
        if(createUrl == null || "".equals(createUrl)){
            createUrl = dataObject.getString("formUpdateUrl");
        }
        String msg = self.getString("df_readMsg");
        if(msg == null || "".equals(msg)){
            msg = dataObject.getString("readMsg");
        }
        if(msg == null || "".equals(msg)){
            msg = "正在保存数据，请稍候...";
        }
        
        String code = "";
        //String idName = "";
        //if(keys.size() > 0){
        //    idName = keys.get(0).getString("name");
        //}
        if(!self.getBoolean("df_bind") || (self.getBoolean("df_bind") && self.getBoolean("df_updateToServer"))){
            //先提交至服务器
            code = "//提交数据\n" + 
"        Ext.getCmp(" + self.get("id") + ").getForm().submit({\n" + 
"            url:'" + createUrl + "', \n" + 
"            waitMsg:'" + msg + "',\n" + 
"            success: function(form, action) {";
            if(self.getBoolean("df_bind")){
                String bindId = self.getString("df_bindId");
                
                if( "grid".equals(self.getString("df_bindType"))){
                    code = code + "\n       var store = Ext.getCmp(" + bindId + ").store;";
                }else{
                    code = code + "\n       var store = Ext.StoreMgr.get(" + bindId + ");";
                }
                code = code + "\n       var formPanel = Ext.getCmp(" + self.get("id") + ").getForm();";
                code = code + "\n       formPanel.setValues(action.result.record);";
                code = code + "\n       var idValue = action.result.record.${idName};";
                code = code + "\n       var record = store.getById(idValue);";
                code = code + "\n       formPanel.updateRecord(record);";
                
            }
        code = code  + "\n" +     
"               Ext.Msg.alert('成功', action.result.msg);\n" + 
"            },\n" + 
"            failure: function(form, action) {\n" + 
"                switch (action.failureType) {\n" + 
"                    case Ext.form.Action.CLIENT_INVALID:\n" + 
"                        Ext.Msg.alert('失败', '数据校验失败');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.CONNECT_FAILURE:\n" + 
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.SERVER_INVALID:\n" + 
"                       Ext.Msg.alert('失败', action.result.msg);\n" + 
"                }\n" + 
"            }\n" + 
"        });\n";
        
        }else if(self.getBoolean("df_bind")){
            String bindId = self.getString("df_bindId");
            code = "var formPanel = Ext.getCmp(" + self.get("id") + ").getForm()";
            code = code + "\nvar values = formPanel.getValues();";
            if("grid".equals(self.getString("df_bindType"))){
                code = code + "\nvar store = Ext.getCmp(" + bindId + ").store;";
            }else{
                code = code + "\nvar store = Ext.StoreMgr.get(" + bindId + ");";
            }
            code = code + "\nvar idField = formPanel.findField('id');";
            code = code + "\nvar idValue = idField.getValue();";
            code = code + "\nvar record = store.getById(idValue);";
            code = code + "\nformPanel.updateRecord(record);";
        }else{
            code = "//提交数据\n" + 
"        Ext.getCmp(" + self.get("id") + ").getForm().submit({\n" + 
"            url:'" + createUrl + "', \n" + 
"            waitMsg:'" + msg + "',\n" + 
"            success: function(form, action) {\n" +  
"               Ext.Msg.alert('成功', action.result.msg);\n" + 
"            },\n" + 
"            failure: function(form, action) {\n" + 
"                switch (action.failureType) {\n" + 
"                    case Ext.form.Action.CLIENT_INVALID:\n" + 
"                        Ext.Msg.alert('失败', '数据校验失败');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.CONNECT_FAILURE:\n" + 
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.SERVER_INVALID:\n" + 
"                       Ext.Msg.alert('失败', action.result.msg);\n" + 
"                }\n" + 
"            }\n" + 
"        });\n";
        }
        createFunction.put("code", code);
        
        formPanel.addChild(createFunction);
    }

    @SuppressWarnings("unchecked")
	public static void createDestroyFunction(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing formPanel = (Thing) actionContext.get("formPanel");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
    	List<Thing> keys = (List<Thing>) actionContext.get("keys");
    	//List<Map<String, Object>> attributes = (List<Map<String, Object>>) actionContext.get("attributes");
        
        //创建
        Thing createFunction = new Thing("xworker.html.extjs.Function");
        createFunction.put("params", "");
        createFunction.put("name", "doDestroy");
        String createUrl = self.getString("df_formDestroyUrl");
        if(createUrl == null || "".equals(createUrl)){
            createUrl = dataObject.getString("formDestroyUrl");
        }
        String msg = self.getString("df_readMsg");
        if(msg == null || "".equals(msg)){
            msg = dataObject.getString("readMsg");
        }
        if(msg == null || "".equals(msg)){
            msg = "正在删除数据，请稍候...";
        }
        
        String code = "";
        String idName = "";
        if(keys.size() > 0){
            idName = keys.get(0).getString("name");
        }
        if(!self.getBoolean("df_bind") || (self.getBoolean("df_bind") && self.getBoolean("df_destroyToServer"))){
            //先提交至服务器
            code = "//提交数据\n" + 
"        Ext.getCmp(" + self.get("id") + ").getForm().submit({\n" + 
"            url:'" + createUrl + "', \n" + 
"            waitMsg:'" + msg + "',\n" + 
"            success: function(form, action) {";
            if(self.getBoolean("df_bind")){
                String bindId = self.getString("df_bindId");
                if("grid".equals(self.getString("df_bindType"))){
                    code = code + "\n        var store = Ext.getCmp(" + bindId + ").store;";
                }else{
                    code = code + "\n        var store = Ext.StoreMgr.get(" + bindId + ");";
                }
                code = code + "\n        var formPanel = Ext.getCmp(" + self.get("id") + ").getForm();";        
                code = code + "\n        var idField = formPanel.findField('" + idName + "');";
                code = code + "\n        var idValue = idField.getValue();";
                code = code + "\n        formPanel.setValues(action.result.record);";
                code = code + "\n        var record = store.getById(idValue);";
                code = code + "\n        store.remove(record)";
            }
        code = code  + "\n" + 
"                Ext.Msg.alert('成功', action.result.msg);\n" + 
"            },\n" + 
"            failure: function(form, action) {\n" + 
"                switch (action.failureType) {\n" + 
"                    case Ext.form.Action.CLIENT_INVALID:\n" + 
"                        Ext.Msg.alert('失败', '数据校验失败');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.CONNECT_FAILURE:\n" + 
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.SERVER_INVALID:\n" + 
"                       Ext.Msg.alert('失败', action.result.msg);\n" + 
"                }\n" + 
"            }\n" + 
"        });\n";
        
        }else if(self.getBoolean("df_bind")){
            String bindId = self.getString("df_bindId");
                if("grid".equals(self.getString("df_bindType"))){
                    code = code + "\n        var store = Ext.getCmp(" + bindId + ").store;";
                }else{
                    code = code + "\n        var store = Ext.StoreMgr.get(" + bindId + ");";
                }
                code = code + "\n        var formPanel = Ext.getCmp(" + self.get("id") + ").getForm();";        
                code = code + "\n        var idField = formPanel.findField('" + idName + "');";
                code = code + "\n        var idValue = idField.getValue();";
                code = code + "\n        var record = store.getById(idValue);";
                code = code + "\n        store.remove(record)";
        }else{
            code = "//提交数据\n" + 
"        Ext.getCmp(" + self.get("id") + ").getForm().submit({\n" + 
"            url:'" + createUrl + "', \n" + 
"            waitMsg:'" + msg + "',\n" + 
"            success: function(form, action) {\n" +  
"               Ext.Msg.alert('成功', action.result.msg);\n" + 
"            },\n" + 
"            failure: function(form, action) {\n" + 
"                switch (action.failureType) {\n" + 
"                    case Ext.form.Action.CLIENT_INVALID:\n" + 
"                        Ext.Msg.alert('失败', '数据校验失败');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.CONNECT_FAILURE:\n" + 
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.SERVER_INVALID:\n" + 
"                       Ext.Msg.alert('失败', action.result.msg);\n" + 
"                }\n" + 
"            }\n" + 
"        });\n";
        }
        createFunction.put("code", code);
        
        formPanel.addChild(createFunction);
    }

}