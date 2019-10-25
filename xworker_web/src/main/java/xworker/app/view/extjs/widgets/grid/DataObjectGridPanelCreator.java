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
package xworker.app.view.extjs.widgets.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class DataObjectGridPanelCreator {
	private static Logger log = LoggerFactory.getLogger(DataObjectGridPanelCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        //----------------------------------数据对象-----------------------
        //数据对象
        Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
        if(dataObject == null){
            log.warn("DataObjectGridPanel: dataObject is null - " + self.getString("dataObject"));
            return null;//self.doAction("toJavaScriptCode", actionContext);
        }
        
        //组件标识，子控件可能会用到
        actionContext.peek().put("cmpId", self.doAction("getComponentId", actionContext));
        
        //----------------------------------创建表格 ----------------------
        //属性列表
        List<Map<String, Object>> fields = (List<Map<String, Object>>) dataObject.doAction("getEditorAttributes", actionContext, UtilMap.toMap(new Object[]{"editorType", "grid"}));
        
        //创建表格
        Thing grid = self.detach();
        if(dataObject.getBoolean("gridEditable") && !"window".equals(dataObject.getBoolean("gridEditType"))){
            grid.set("descriptors", "xworker.html.extjs.Ext.grid.EditorGridPanel");
            grid.put("extTypeName", "Ext.grid.EditorGridPanel");
        }else{
            grid.set("descriptors", "xworker.html.extjs.Items/@GridPanel");
            grid.put("extTypeName", "Ext.grid.GridPanel");
        }
        
        //设置数据对象中有而表格没有设定的参数
        setValue(dataObject, grid, "autoExpandColumn", "autoExpandColumn", true);
        
        //------------------------------设置插件----------------------------
        //列分组插件
        if(dataObject.getBoolean("columnGroup")){
            self.doAction("createColumnGroupPlugin", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
        }
        
        //RowExpander插件
        Thing rowExpander = null;
        if(dataObject.getBoolean("gridRowExpander")){
            rowExpander = (Thing) self.doAction("createRowExpander", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
            actionContext.getScope(0).put("rowExpander", rowExpander);
        }
        
        //CheckBox插件
        Thing checkSelectModel = null;
        if(dataObject.getBoolean("gridCheckable")){
            checkSelectModel = (Thing) self.doAction("createCheckBoxModel", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
        }
        
        //-----------------------------创建列定义--------------------------
        self.doAction("createExtGridColumns", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields, "checkSelectModel",checkSelectModel}));
        
        //---------------------------------创建store-------------------
        if(grid.getThing("store@0") == null){
            Thing store = (Thing) self.doAction("createExtStore", actionContext, UtilMap.toMap(new Object[]{"dataObject",dataObject, "cmpType","grid"}));
            if(store != null){
                grid.addChild(store, 0);
            }
        }
        
        //-----------------------------创建PagingToolbar------------------
        if(grid.getThing("bbar@0/PagingToolbar@0") == null){
            if("true".equals(dataObject.getString("paging"))){
                //创建分页
                self.doAction("createExtPagingToolbar", actionContext, UtilMap.toMap(new Object[]{"dataObject",dataObject, "control",grid}));
            }
        }
        
        //--------------------------------创建View-----------------------
        //列锁定模式
        if(dataObject.getBoolean("gridColumnLocking")){
            self.doAction("createColumnLockView", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
        }
        //行分组
        if(dataObject.getBoolean("gridGrouping")){
            self.doAction("createGroupingView", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
        }
        
        //-------------------------------laodMask------------------------
        if(self.get("loadMask@0") == null && self.getBoolean("loadMask")){
            String msg = self.getString("loadMaskMessage");
            if(msg == null || "".equals(msg)){
                msg = "'正在装载数据，请稍后...'";
            }else{
                msg = "'" + msg + "'";
            }
            Thing loadMask = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25025");
            loadMask.put("msg", msg);
            loadMask.put("name", "loadMask");
            grid.addChild(loadMask);
        }
        
        //-------------------------清除Ext.Grid本身没有的属性--------------
        grid.put("dataObject",  null); 
        for(String key : grid.getAttributes().keySet()){
            if(key.startsWith("dobj_")){
                grid.put(key, null);
            }
        }
        
        //删除多余的自定义子事物
        Thing selModelListener = (Thing) grid.get("selModelListener@0");
        if(selModelListener != null){
            grid.removeChild(selModelListener);
        }
        
        //--------------------------返回代码-----------------------
        return grid.doAction("toJavaScriptCode", actionContext);        
    }
    
    //从dataobject中拷贝参数到gird中
    public static void  setValue(Thing src, Thing tag, String objName, String gridName, boolean isString){
        if(tag.get(gridName) == null || !"".equals(tag.get(gridName))){
            String value = src.getString(objName);
            if(value != null && !"".equals(value)){
                if(isString && (!value.startsWith("'") && !value.startsWith("\""))){
                    tag.put(gridName, "'" + value + "'");
                }else{
                    tag.put(gridName, value);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static Object createExtGridColumn(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        Map<String, Object> field = (Map<String, Object>) actionContext.get("field");
        Thing viewConfig = (Thing) field.get("viewConfig");
        Thing attribute = (Thing) field.get("attribute");
        String cmpId = (String) actionContext.get("cmpId");
        Thing dataObject = (Thing) actionContext.get("dataObject");
        
        //创建列事物
        Thing column = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25014/@Column");
        column.initDefaultValue();
        
        //标识
        if(viewConfig != null && viewConfig.getString("editorId") != null &&  !"".equals(viewConfig.getString("editorId"))){
            actionContext.peek().put("cmpId", self.doAction("trimId", actionContext, UtilMap.toMap(new Object[]{"id", viewConfig.getString("editorId")})));
        }else if(actionContext.get("cmpId") != null){
            actionContext.peek().put("cmpId", cmpId + "_" + attribute.get("name"));
        }else{
            actionContext.peek().put("cmpId", attribute.get("name"));
        }
        
        //-----------------属性---------------
        //列标题
        if(viewConfig.get("gridHeader") != null &&  !"".equals(viewConfig.getString("gridHeader"))){
            column.put("header", viewConfig.getString("gridHeader"));
        }else{
            column.put("header", "'" + attribute.getMetadata().getLabel() + "'");
        }
        //列索引
        column.put("dataIndex", "'" + attribute.get("name") + "'");
        //列标识
        column.put("id", "'" + attribute.get("name") + "'");
        //是否可编辑，默认可编辑，设置false的情形
        if(viewConfig.getBoolean("editable") == false){
            column.put("editable", "false");
        }
        //其他属性
        setValue(viewConfig, column, "gridFixed", "fixed", false);
        setValue(viewConfig, column, "gridHidden", "hidden", false);
        setValue(viewConfig, column, "gridHideable", "hideable", false);
        setValue(viewConfig, column, "gridSortable", "sortable", false);
        setValue(viewConfig, column, "gridTooltip", "tooltip", false);
        setValue(viewConfig, column, "gridWidth", "width", false);
        setValue(viewConfig, column, "gridHeader", "header", false);
        setValue(viewConfig, column, "gridAlign", "align", false);
        setValue(viewConfig, column, "gridEmptyGroupText", "emptyGroupText", false);
        //setValue(field.viewConfig, column, "gridGroupName", "groupName");
        //setValue(field.viewConfig, column, "gridGroupable", "groupable");
        setValue(viewConfig, column, "gridMenuDisabled", "menuDisabled", false);
        setValue(viewConfig, column, "gridResizable", "resizable", false);
        setValue(viewConfig, column, "columnLocked", "locked", false);
        if("truefalse".equals(viewConfig.getString("inputtype"))){
            column.put("xtype", "'booleancolumn'");
        }
        
        //--------------子事物---------------
        //查看是否有其他子事物定义
        addChild(viewConfig, column, "Extjs@0/GridColumnRenderer@0");
        addChild(viewConfig, column, "Extjs@0/GridColumnGroupRenderer@0");
        
        //创建编辑器，修改为总是创建编辑器，这样为确保GridColumnRenderer能够被设置
        if(true){//dataObject.getBoolean("gridEditable")){
            Thing editorThing = new Thing("xworker.html.extjs.Ext.grid.Column/@24970");
            editorThing.put("name", "editor");
            column.addChild(editorThing);
             
            //首先判断属性字段是否已定义了编辑器
            Thing editor = viewConfig.getThing("Extjs@0/GridColumnEditor@0");
            if(editor != null){
                editor = editor.detach();
                ((Thing) editor.get("attribute")).put("name", "'" + attribute.get("name") + "'");        
                editorThing.addChild(editor, false);
            }else{
                //自动创建编辑器
                editor = (Thing) self.doAction("createExtFieldEditor", actionContext, UtilMap.toMap(new Object[]{"field",field, "editorType","grid"}));
                if(editor != null){
                    editor.put("name", "'" + attribute.get("name") + "'"); 
                    editorThing.addChild(editor, false);
                    
                    if(editor.get("__isCombo") == "true" && viewConfig.get("Extjs@0/GridColumnRenderer@0") == null){
                        //创建的是下拉列表，并且没有自定义表格显示的，要自动创建一个行显示
                        Thing renderer = new Thing("xworker.html.extjs.Ext.grid.Column/@24981");
                        renderer.put("name", "renderer");
                        renderer.put("params" ,"value, metaData, record, rowIndex, colIndex, store");
                        String code = "var store = Ext.StoreMgr.get(" + editor.get("__storeId") + ");";
                        code = code + "\nvar rec = store.getById(value);";
                        code = code + "\nif(rec){";
                        code = code + "\n    return rec.get('" + editor.get("__displayName") + "');";
                        code = code + "\n}else{\n    return value;\n}";
                        renderer.put("code", code);
                        column.addChild(renderer);
                    }
                }
            }
        }
        
        //日期格式
        if("datePick".equals(viewConfig.getString("inputtype"))){
            if(column.get("renderer@0") == null){
                Thing renderer = new Thing("xworker.html.extjs.Ext.grid.Column/@24981");
                renderer.put("name", "renderer");
                String format = viewConfig.getString("jsEditPattern");
                if(format == null || "".equals(format)){
                    format = "Y-m-d";
                }
                renderer.put("varref", "Ext.util.Format.dateRenderer('" + format + "')");
                column.addChild(renderer);
            }
        }
        
        if("dateTime".equals(viewConfig.getString("inputtype"))){
            if(column.get("renderer@0") == null){
                Thing renderer = new Thing("xworker.html.extjs.Ext.grid.Column/@24981");
                renderer.put("name", "renderer");
                String format = viewConfig.getString("jsEditPattern");
                if(format == null || "".equals(format)){
                    format = "Y-m-d H.i.s";
                }else{
                    String[] fs = format.split("[|]");
                    format = fs[0];
                    if(fs.length >= 2){
                        format = format + " " + fs[1];
                    }
                }
                renderer.put("varref", "Ext.util.Format.dateRenderer('" + format + "')");
                column.addChild(renderer);
            }
        }
        
        if("time".equals(viewConfig.getString("inputtype"))){
            if(column.get("renderer@0") == null){
                Thing renderer = new Thing("xworker.html.extjs.Ext.grid.Column/@24981");
                renderer.put("name", "renderer");
                String format = viewConfig.getString("jsEditPattern");
                if(format == null || format == ""){
                    format = "H.i.s";
                }
                renderer.put("varref", "Ext.util.Format.dateRenderer('" + format + "')");
                column.addChild(renderer);
            }
        }
        //--------------返回结果--------------
        return column;
        
        //---------------------------方法定义------------------
    }

    //添加在属性中定义的column子事物
    public static void  addChild(Thing field, Thing column, String path){
        Thing childThing = field.getThing(path);
        if(childThing != null){
            column.addChild(childThing, false);
        }
    }
    
    @SuppressWarnings("unchecked")
	public static Object createExtGridColumns(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");        
        Thing dataObject = (Thing) actionContext.get("dataObject");
        Thing grid = (Thing) actionContext.get("grid");
        
        //-----------------------------创建列定义--------------------------
        //创建columns
        List<Map<String, Object>> fields = (List<Map<String, Object>>) actionContext.get("fields");
        if(fields == null){
            fields = (List<Map<String, Object>> ) dataObject.doAction("getEditorAttributes", actionContext, UtilMap.toMap(new Object[]{"editorType","grid"}));
        }
            
        Thing theModel = null;
        Thing columns = grid.getThing("columns@0");
        if(grid.get("columns@0") == null && grid.get("colModel") == null){
            if(dataObject.getBoolean("gridColumnLocking")){
                //使用列锁定
                Thing columnModel = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25012");
                columnModel.put("name", "colModel");
                Thing cmodel = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25012/@LockingColumnModel");
                cmodel.initDefaultValue();
                columns = new Thing("xworker.html.extjs.Ext.grid.ColumnModel/@24990");
                columns.put("name", "columns");
                cmodel.addChild(columns);        
                columnModel.addChild(cmodel);
                theModel = columnModel;
                grid.addChild(columnModel);        
            }else{
                columns = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25014");
                columns.put("name", "columns");
                theModel = columns;
                grid.addChild(columns);
            }
            
            for(Map<String, Object> field : fields){
                Thing attribute = (Thing) field.get("attribute");
                if(attribute.getBoolean("showInTable")){
                    Thing column = (Thing) self.doAction("createExtGridColumn", actionContext, UtilMap.toMap(new Object[]{"field",field, "dataObject",dataObject}));
                    if(column != null){
                        columns.addChild(column);
                    }
                }
            }    
        }
        
        //如果有CheckBoxSelectModel
        Thing checkSelectModel = (Thing) actionContext.get("checkSelectModel");
        if(checkSelectModel == null && dataObject.getBoolean("gridCheckable")){
            checkSelectModel = (Thing) self.doAction("createCheckBoxModel", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dataObject, "fields",fields}));
        }
        
        if(checkSelectModel != null){
            Thing column = new Thing("xworker.app.test.web.extjs.widgets.grid.RowExapnderGrid/@prototype/@view/@ExtJs/@'grid'/@columns/@Column");
            column.put("varref", checkSelectModel.getString("name"));
            columns.addChild(column, 0);
        }
        //如果有RowExpander，加入到列首
        if(dataObject.getBoolean("gridRowExpander")){
            Thing column = new Thing("xworker.app.test.web.extjs.widgets.grid.RowExapnderGrid/@prototype/@view/@ExtJs/@'grid'/@columns/@Column");
            column.put("varref", ((Thing) actionContext.get("rowExpander")).getString("name"));
            columns.addChild(column, 0);
        }
        //如果显示行号
        if(dataObject.getBoolean("gridRowNumber")){
            Thing column = new Thing("xworker.app.test.web.extjs.widgets.grid.RowExapnderGrid/@prototype/@view/@ExtJs/@'grid'/@columns/@Column");
            column.put("varref", "new Ext.grid.RowNumberer()");
            columns.addChild(column, 0);
        }
        
        return theModel;
    }

    public static Object getExtType(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        //数据对象
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dos = self.getThing("dataObjects@0");
            if(dos != null && dos.getChilds().size() > 0){
                dataObject = dos.getChilds().get(0);
            }
        }
        
        if(dataObject == null){
            return "Ext.grid.GridPanel";
        }else if(dataObject.getBoolean("gridEditable") && !"window".equals(dataObject.getBoolean("gridEditType"))){
            return "Ext.grid.EditorGridPanel";
        }else{
            return "Ext.grid.GridPanel";
        }
    }

    @SuppressWarnings("unchecked")
	public static void createColumnGroupPlugin(ActionContext actionContext){    	
    	Thing grid = (Thing) actionContext.get("grid");
    	List<Map<String, Object>> fields = (List<Map<String, Object>>) actionContext.get("fields");
        
        //plugin
        Thing plugins = grid.getThing("plugins@0");
        if(plugins != null){
            Thing group = plugins.getThing("ColumnHeaderGroup@0"); 
            if(group != null){
                return;
            }
        }else{
            plugins = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@plugins");
            plugins.put("name", "plugins");
            grid.addChild(plugins);
        }
        
        //创建分组插件
        Thing group = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@plugins/@ColumnHeaderGroup");
        group.put("haveTypeInCode", true);
        Thing rowsThing = new Thing("xworker.html.extjs.Ext.ux.grid.ColumnHeaderGroup/@rows,xworker.html.extjs.Ext.ux.grid.ColumnHeaderGroup/@rows/@rows");
        rowsThing.put("name", "rows");
        group.addChild(rowsThing);
        plugins.addChild(group);
        
        int maxLevel = 1;
        for(Map<String, Object> field : fields){
            String groupName = ((Thing) field.get("attribute")).getString("columnGroup");
            if(groupName != null && groupName != ""){
                int l = groupName.split("[.]").length;
                if(l > maxLevel){
                    maxLevel = l;
                }
            }
        }
        
        List<List<Map<String, Object>>> rows = new ArrayList<List<Map<String, Object>>>();
        for(int i=0; i<maxLevel;i++){
            rows.add(new ArrayList<Map<String, Object>>());
        }
        for(Map<String, Object> field : fields){
        	Thing attribute = (Thing) field.get("attribute");
            String groupName = attribute.getString("columnGroup");
            if(groupName == null){
                Map<String, Object> row = new HashMap<String, Object>();
                List<Map<String, Object>> rowLevel = getRowLevel(rows, 1);
                rowLevel.add(row);
                for(int i=2; i<=maxLevel; i++){
                    List<Map<String, Object>> rl = getRowLevel(rows, i);
                    rl.add(new HashMap<String, Object>());
                }
            }else{
                String[] names = groupName.split("[.]");
                for(int i=0; i<names.length; i++){
                    List<Map<String, Object>> rowLevel = getRowLevel(rows, i+1);
                    Map<String, Object> row = null;
                    if(rowLevel.size() > 0 && names[i].equals(rowLevel.get(rowLevel.size() - 1).get("name"))){
                        row = rowLevel.get(rowLevel.size() - 1);
                        row.put("colspan", (Integer) row.get("colspan") + 1);
                    }else{
                    	row = new HashMap<String, Object>();
                    	row.put("name", names[i]);
                    	row.put("colspan", 1);
   
                        rowLevel.add(row);
                    }
                }
                for(int i=names.length+1; i<=maxLevel; i++){
                    List<Map<String, Object>> rl = getRowLevel(rows, i);
                    rl.add(new HashMap<String, Object>());
                }
            }    
        }
        
        //创建分组表头
        for(List<Map<String, Object>> rowLevel : rows){
            Thing rl = new Thing("xworker.html.extjs.Ext.ux.grid.ColumnHeaderGroup/@rows/@rows");
            rowsThing.addChild(rl);
            for(Map<String, Object> row : rowLevel){
                Thing rowThing = new Thing("xworker.html.extjs.Ext.ux.grid.ColumnHeaderGroup/@rows/@row");
                if(row.get("name") != null &&  !"".equals(row.get("name"))){
                    rowThing.put("header", "'" + row.get("name") + "'");
                }
                if(rowThing.get("header") != null){
                    rowThing.put("align", "'center'");
                }
                if(row.get("colspan") != null && (Integer) row.get("colspan") != 1){
                    rowThing.put("colspan", row.get("colspan"));
                }
                rl.addChild(rowThing);
            }
        }
    }
    
    public static List<Map<String, Object>> getRowLevel(List<List<Map<String, Object>>> rows, int level){
        if(rows.size() < level){
            List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
            rows.add(ls);
            return ls;
        }else{
            return rows.get(level-1);
        }
    }

    public static Object createRowExpander(ActionContext actionContext){
    	Thing grid = (Thing) actionContext.get("grid");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
        
        //创建RowExpander
        Thing rowExpander = new Thing("xworker.html.extjs.Ext.ux.grid.RowExpander");
        grid.addChild(rowExpander);
        rowExpander.initDefaultValue();
        String gridId = grid.getString("id");
        if(gridId != null){
            if(gridId.endsWith("'")){
                gridId = gridId.substring(1, gridId.length() - 1);
            }
        }else{
            gridId = "";
        }
        rowExpander.put("name", gridId + "_rowExpander");
        
        //创建模板
        Thing tpl = new Thing("xworker.html.extjs.Ext.ux.grid.RowExpander/@tpl");
        tpl.put("name", "tpl");
        rowExpander.addChild(tpl);
        Thing code = new Thing("xworker.html.extjs.Ext.Template/@code1");
        code.put("code", dataObject.getString("rowExpanderTpl"));
        tpl.addChild(code);
        
        //创建plugins
        Thing plugins = grid.getThing("plugins@0");
        if(plugins == null){
            plugins = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@plugins");
            plugins.put("name", "plugins");
            grid.addChild(plugins);
        }
        Thing ref = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@plugins/@PlugInRef");
        ref.put("varref", rowExpander.get("name"));
        plugins.addChild(ref);
        
        return rowExpander;
    }

    public static Object createCheckBoxModel(ActionContext actionContext){
    	Thing grid = (Thing) actionContext.get("grid");
    	@SuppressWarnings("unused")
		Thing dataObject = (Thing) actionContext.get("dataObject");
        
        //创建checkBoxSelectModel
        Thing checkBoxSModel = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25028/@CheckboxSelectionModel");
        grid.addChild(checkBoxSModel);
        checkBoxSModel.initDefaultValue();
        String gridId = grid.getString("id");
        if(gridId != null){
            if(gridId.endsWith("'")){
                gridId = gridId.substring(1, gridId.length() - 1);
            }
        }else{
            gridId = "";
        }
        checkBoxSModel.put("name", gridId + "_checkBox");
        
        //selModel
        Thing selModel = grid.getThing("selModel@0");
        Thing listener = null;
        if(selModel != null){
            listener = (Thing) selModel.get("@0/listeners@0");   
            if(listener != null){
                checkBoxSModel.addChild(listener, false);
            }
        }
        if(selModel == null){
            selModel = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25028");
            selModel.put("name",  "selModel");
            grid.addChild(selModel);
        }
        
        Thing ref = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25028/@CheckboxSelectionModel");
        ref.put("varref", checkBoxSModel.getString("name"));
        selModel.addChild(ref, 0);
        
        //事件
        listener = (Thing) grid.get("selModelListener@0");
        if(listener != null){
            listener = listener.detach();
            listener.put("name", "listeners");
            checkBoxSModel.addChild(listener, false);
        }
        return checkBoxSModel;
    }

    public static void createColumnLockView(ActionContext actionContext){
    	Thing grid = (Thing) actionContext.get("grid");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
        
        Thing view = grid.getThing("view@0");
        if(view == null){
            view = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25034");
            view.put("name", "view");
            grid.addChild(view);
        }
        
        Thing lockView = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25034/@LockingGridView");
        lockView.put("lockText", dataObject.getString("gridLockText"));
        lockView.put("unlockText", dataObject.getString("gridUnlockText"));
        view.addChild(lockView);
    }

    public static void createGroupingView(ActionContext actionContext){
    	Thing grid = (Thing) actionContext.get("grid");
    	Thing dataObject = (Thing) actionContext.get("dataObject");
        
        Thing view = grid.getThing("view@0");
        if(view == null){
            view = new Thing("xworker.html.extjs.Ext.grid.GridPanel/@25034");
            view.put("name", "view");
            grid.addChild(view);
        }
        
        Thing grouping = new Thing("xworker.html.extjs.Ext.grid.GroupingView");
        grouping.put("groupTextTpl", dataObject.getString("gridGroupTextTpl"));
        view.addChild(grouping, 0);
    }

}