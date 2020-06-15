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
package xworker.app.view.swt.data.events;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.swt.custom.ItemRowEditor;
import xworker.swt.custom.TableCellEditor;
import xworker.swt.custom.TableCursorEditorCreator;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtUtils;

public class TableDataStoreListener {
	private static Logger log = LoggerFactory.getLogger(TableDataStoreListener.class);
	
	/**
	 * 列转要显示的字符串。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getColumnDisplayValue(final ActionContext actionContext){
		DataObject record = (DataObject) actionContext.get("record");
		String text = null;
		if(record == null){
		    return null;
		}

		Thing column = (Thing) actionContext.get("column");
		if(column.getData("__tableDataStoreToDisplayString") != null && ((Boolean) column.getData("__tableDataStoreToDisplayString")) == true){
			
		    text = (String) column.doAction("toDisplayString", actionContext, UtilMap.toMap(new Object[]{"record", record, "value", record.get(column.getString("name"))}));
		}else if(column.getData("__tableDataStoreFormater") != null){ 
		    Format formater = (Format) column.getData("__tableDataStoreFormater");
		    Object value = record.get(column.getString("name"));
		    if(value != null){
		        text = formater.format(value);
		    }
		}else{    
		    //先从显示字段中取
		    String disField = column.getStringBlankAsNull("displayField");
		    if(disField != null){
		        //log.info("display=" + disField);
		        String[] diss = disField.split("[.]");
		        Object v = record;
		        try{
		            for(String dis : diss){
		            	if(v instanceof DataObject){
		            		v = ((DataObject) v).get(dis);
		            	}else{
		            		v = null;
		            	}
		                
		                //log.info("value=" + v);
		                if(v == null){
		                    break;
		                }                        
		            }
		            if(v != null){
		                text = v.toString();
		            }
		        }catch(Exception e){
		            v = null;
		            log.warn("TableDataStoreListener: get display error," + disField, e);
		        }
		    }
		        
		    if(text == null){
		    	Object v = record.get(column.getString("name"));
		        text = v == null ? "" : v.toString();
		    }
		}

		if(text == null){
		    text = "";
		}else{
		    text = String.valueOf(text);
		}

		return text;
	}

	/**
	 * 创建拥有数据仓库的列。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing createColumnWithDataStore(final ActionContext actionContext){
		//在表格编辑时重新生成column的符合编辑的属性定义，尤其是生成数据仓库。
		//String type = "edit";
		Thing attribute = (Thing) actionContext.get("column");
		Thing column = (Thing) actionContext.get("column");
		Thing at = (Thing) column.get("EditConfig@0");

		String attributePath = attribute.getMetadata().getPath(); //保存原有的事物路径，方便Desinger能够正确打开原先的属性
		//log.info("attributePath=" + attributePath);
		if(at != null){
		    //由于config中只定义了样式，没有属性的基本数据等，所以复制
		    Thing attemp = at.detach();
		    Map<String, Object> temp = new HashMap<String, Object>();
		    temp.putAll(attribute.getAttributes());
		    temp.putAll(attemp.getAttributes());
		    attemp.getAttributes().putAll(temp);
		    attemp.put("name", attribute.getString("name"));
		    attemp.put("label", attribute.getString("label"));
		    attemp.put("pattern", attribute.getString("editPattern"));
		    attemp.put("descriptors", attribute.getString("descriptors"));
		    attribute = attemp;            
		}else{
		    attribute = attribute.detach();     
		    attribute.put("pattern", attribute.getString("editPattern"));      
		}
		attribute.setData("_originalityAttributePath", attributePath);

		String inputtype = attribute.getStringBlankAsNull("inputtype");
		if(UtilData.equalsOne(inputtype, new String[]{"select","inputSelect","multSelect"})){             
		     String dobj = attribute.getString("relationDataObject");
		     String inputattrs = attribute.getStringBlankAsNull("inputattrs");
		     if(dobj != null && !"".equals(dobj) && inputattrs == null){
		         //如果属性是多对一关联其他属性的，并且是下拉选择框，那么初始化相关下拉框的功能
		          Thing dataStore = new Thing("xworker.swt.Commons/@DataStore");
		          dataStore.initDefaultValue();                      
		          dataStore.put("paging", "no"); //下拉列表不分页
		          String store = attribute.getStringBlankAsNull("store");
		          if(store != null){
		              //字段定义了引擎其他数据仓库
		              dataStore.put("storeRef", attribute.getString("store"));
		              dataStore.put("attachToParent", "true");
		              dataStore.put("loadBackground", "true");
		          }else{
		              //创建数据仓库
		              dataStore.put("dataObject", attribute.getString("relationDataObject"));
		              dataStore.put("queryConfig", attribute.getString("relationQueryConfig"));
		              if(dataStore.getStringBlankAsNull("queryConfig") == null){
		                  Thing qcfg = (Thing) column.get("SelectCondition@0");
		                  if(qcfg != null){
		                      dataStore.put("queryConfig", qcfg.getMetadata().getPath());
		                  }
		              }
		              dataStore.put("autoLoad", "true");
		              dataStore.put("attachToParent", "true");
		              dataStore.put("loadBackground", "true");
		              dataStore.put("labelField", attribute.getString("relationLabelField"));
		          }
		          attribute.setData("dataStore", dataStore);
		          attribute.put("inputattrs", dataStore.getMetadata().getPath());
		     }             
		}

		return attribute;
	}
	
	/**
	 * 创建行编辑器，其实是按单元格编辑。本方法是在onReconfig方法中被调用的。
	 * 
	 * 在表格中，最终是TableCursor触发编辑，TableCursor默认选择事件里会取这里设置的列编辑器。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void createRowEditor(final ActionContext actionContext){
		//按单元格编辑
		Thing self = (Thing) actionContext.get("self");
		Thing cursorThing = new Thing("xworker.swt.widgets.Table/@TableCursorEditor");
		List<Thing> columns = (List<Thing>) actionContext.get("columns");
		Thing store = (Thing) actionContext.get("store");
		cursorThing.initDefaultValue();
		cursorThing.put("name", store.getString("name") + "CursorEditor");
		cursorThing.put("dataType", "store");
		self.setData("cursorThing", cursorThing);

		/*
		//默认使用Attribute编辑器
		def defaultEditor = new Thing("xworker.swt.custom.TableCursorEditor/@ColumnEditor");
		defaultEditor.name = "defaultEditor";
		cursorThing.addChild(defaultEditor);
		//默认是文本编辑
		def textEditor = new Thing("xworker.swt.custom.TableCursorEditor/@ColumnEditor/@TextEditor");
		defaultEditor.addChild(textEditor);
		*/

		int index = 0;
		for(Thing column : columns){
		    if(column.getBoolean("editable")){
		        //根据输入类型创建相应的编辑器
		        //String inputType = column.getStringBlankAsNull("inputtype");
		        Thing columnEditor = new Thing("xworker.swt.custom.TableCursorEditor/@ColumnEditor");
		        columnEditor.put("column", "" + index);
		        cursorThing.addChild(columnEditor);
		        
		        Thing attrEditor = null;
		        attrEditor = new Thing("xworker.swt.custom.tableEditors.AttributeEditor");
		        /*
		        if(false || "text".equals(inputType) || inputType == null){
		        	attrEditor = new Thing("xworker.swt.custom.tableEditors.TextEditor");  
		        	attrEditor.set("name", "text");
		        }else{
			        attrEditor = new Thing("xworker.swt.custom.tableEditors.AttributeEditor");  
			        
		        }*/
		        Thing attribute = (Thing) self.doAction("createColumnWithDataStore", actionContext, UtilMap.toMap(new Object[]{"column", column}));  
		        attrEditor.addChild(attribute, false);
		        columnEditor.addChild(attrEditor);
		    }
		    index++;
		}

		try{
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", (Table) actionContext.get("table"));
		    
		    cursorThing.doAction("create", actionContext);
		    //Clipboard.add(cursorThing);
		}finally{
		    actionContext.pop();
		}
	}
	
	/**
	 * 显示编辑器，如果一个属性的gridShowEditor=true，那么默认就显示它的编辑器。
	 * 
	 * @param table
	 * @param item
	 * @param record
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void showTableEditor(Thing self, Table table, TableItem item, DataObject record, ActionContext actionContext) {
		Thing cursorThing = (Thing) self.getData("cursorThing");
		//当表格可以编辑时同时也会有cursorThing，参看creatRowEditor(ActionContext)
		if(cursorThing != null) {
			List<Thing> columns = (List<Thing>) self.get("columns");
			for(int i=0; i<columns.size(); i++) {
				Thing column = columns.get(i);
				if(!column.getBoolean("gridShowEditor")) {
					continue;
				}
				
				//获取当前列的编辑事物
				Thing editorThing = TableCursorEditorCreator.getEditorThing1(cursorThing, i, item, actionContext);
				if(editorThing != null) {
				    ActionContext ac = new ActionContext();
				    Object data = item.getData();
				    ac.put("params", data); //下拉框联动等时，会使用data作为查询条件
				    ac.put("parentContext", actionContext);
				    ac.put("parent", table);
				    //ac.put("cursor", cursor);
				    ac.put("item", item);
				    ac.put("column", i);
				    ac.put("table", table);
				    ac.put("cursorThing", cursorThing);
				    Control columnEditor = (Control) editorThing.doAction("create", ac);
				    //log.info("columnEditor=" + columnEditor);
				    if(columnEditor != null){
				    	final TableEditor tableEditor = new TableEditor(table);
				    	tableEditor.horizontalAlignment = SWT.LEFT;
				    	tableEditor.grabHorizontal = true;
				    	//tableEditor.minimumWidth = 50;

				    	tableEditor.setEditor(columnEditor, item, i);
				    	item.addDisposeListener(new DisposeListener() {
							@Override
							public void widgetDisposed(DisposeEvent e) {
								System.out.println("table item dipsoed");
								try {
									tableEditor.getEditor().dispose();
									tableEditor.dispose();
								}catch(Exception ee) {
									ee.printStackTrace();
								}
							}
				    		
				    	});
				    }
				}
			}
		}
	}
	/**
	 * 把一条记录转化成表格要显示的文字数组。
	 * 
	 * @param actionContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String[] recordToRowTexts(final ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<Thing> columns = (List<Thing>) self.get("columns");
		List<String> texts = new ArrayList<String>();
		DataObject record = (DataObject) actionContext.get("record");
		for(Thing column : columns){
		    String text = SwtStoreUtils.getColumnDisplayText(record, column, actionContext);
		    
		    texts.add(text);
		}

		String ts[] = new String[texts.size()];
		for(int i=0; i<texts.size(); i++){
			ts[i] = texts.get(i);
		}
		return ts;
	}
	
	/**
	 * 删除数据的事件。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void onRemove(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");
		if(table == null || table.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		
		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runAsync(store, table.getDisplay(), new Runnable(){
			public void run(){
				try{
					DSSelectionListener.removeRecords(table, records);
					for(int i=0; i<records.size(); i++) {
						DataObject record = records.get(i);	
			            for(TableItem item : table.getItems()){
			                DataObject itemRecord = (DataObject) item.getData("_store_record");
			                if(record == itemRecord){
			                    item.dispose();
			                }
			            }			            			           
			        }
			        
			        //删除Item时，如果其它Item有TableEditor，使用下面的方法可以更新
			        table.pack();
			        table.getParent().layout();
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onRemove error", t);
			    }
			}
		});
	}
	
	/**
	 * 更新数据的事件。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void onUpdate(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");
		if(table == null || table.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");

		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runAsync(store, table.getDisplay(), new Runnable(){
			public void run(){
				try{
					for(int i=0; i<records.size(); i++) {
						DataObject record = records.get(i);					
			            for(TableItem item : table.getItems()){
			                DataObject itemRecord = (DataObject) item.getData("_store_record");
			                if(record == itemRecord){
			                	updateItem(self, item, itemRecord, actionContext);
			                    //log.info("DataStore: update table item");
			                	/*
			                    String[] texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
			                    
			                    item.setText(texts);			
			                    
			                    if((table.getStyle() & SWT.CHECK) == SWT.CHECK){
				                	if(record.getBoolean(DataObject.CHECKED_ATTRIBUTE_NAME)){
				                		item.setChecked(true);
				                	}else{
				                		item.setChecked(false);
				                	}
				                }*/
			                }
			            }
			        }
			        table.getParent().layout();
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onUpdate error", t);
			    }
			}
		});
	}
	
	/**
	 * 插入数据的事件。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void onInsert(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");
		if(table == null || table.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		final Thing store = (Thing) actionContext.get("store");
		final Integer index= actionContext.getObject("index");
		
		SwtStoreUtils.runAsync(store, table.getDisplay(), new Runnable(){
			public void run(){
				try{
					for(int i=0; i<records.size(); i++) {
						DataObject record = records.get(i);	
			        	createItem(self, table, record, index, actionContext);
			        	/*
			            String[] texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
			            
			            TableItem tableItem = new TableItem(table, SWT.NONE);
			            tableItem.setText(texts);
			            tableItem.setData(record);
			            tableItem.setData("_store_record", record);*/
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onInsert error", t);
			    }
			}
		});
	}
	
	/**
	 * 装载前事件。
	 * 
	 * @param actionContext
	 */
	public static void beforeLoad(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");
		if(table == null || table.isDisposed()){
		    return;
		}

		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runSync(store, table.getDisplay(), new Runnable(){
			public void run(){
			    try{
			        //先清空数据
			        table.removeAll();
			        
			        TableItem item = new TableItem(table, SWT.NONE);
			        item.setText("loading...");
			    }catch(Throwable t){
			        log.error("TableDataStoreListener beforeLoad error", t);
			    }
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private static void createItem(Thing self, Table table, DataObject record, Integer index, ActionContext actionContext) {
		//过滤重复的数据
		for(TableItem item : table.getItems()) {
			if(item.getData() == record) {
				return;
			}
		}
		
		 //log.info("record=" + record);
        String[] texts = null;
        texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
        //log.info("texts=" + texts);
        TableItem tableItem = null;
        if(index != null && index >= 0 && index <= table.getItemCount()) {
        	tableItem = new TableItem(table, SWT.NONE, index);
        }else {
        	tableItem = new TableItem(table, SWT.NONE);
        }
        tableItem.setText(texts);
        tableItem.setData(record);
        tableItem.setData("dataStore", actionContext.get("store"));
        
        updateItemICF(self, tableItem, record, actionContext);
        
        tableItem.setData("_store_record", record);
        if((table.getStyle() & SWT.CHECK) == SWT.CHECK){
        	if(record.getBoolean(DataObject.CHECKED_ATTRIBUTE_NAME)){
        		tableItem.setChecked(true);
        	}
        }
        
        //显示编辑器，如果表格可编辑，且字段gridShowEditor=true
    	Thing cursorThing = (Thing) self.getData("cursorThing");
		//当表格可以编辑时同时也会有cursorThing，参看creatRowEditor(ActionContext)
		if(cursorThing != null) {
			List<Thing> columns = (List<Thing>) self.get("columns");
			ItemRowEditor rowEditor = new ItemRowEditor(tableItem);
			for(int i=0; i<columns.size(); i++) {
				Thing column = columns.get(i);
				if(!column.getBoolean("gridShowEditor")) {
					continue;
				}
				
				TableCellEditor cellEditor = new TableCellEditor(tableItem, i, cursorThing,  actionContext);
				rowEditor.addItemEditor(cellEditor);
			}
			tableItem.setData("__TableDataStore_ItemRowEditor__", rowEditor);
		}
        //showTableEditor(self, table, tableItem, record, actionContext);
		
		//监听数据对象的变化，自动更新表格
		new TableDataStoreDataObjectListener(self, record, tableItem, actionContext);
	}
	
	/**
	 * 更新Item的图标、颜色和字体等。
	 * 
	 * @param self
	 * @param item
	 * @param record
	 * @param actionContext
	 */
	private static void updateItemICF(Thing self,  TableItem item, DataObject record, ActionContext actionContext) {
	    String icon = (String) record.doAction("getIcon", actionContext, "tableItem", item);
        if(icon != null && !"".equals(icon)) {
        	Image image = SwtUtils.createImage(item.getParent(), icon, actionContext);
        	if(image != null) {
        		item.setImage(image);
        	}
        }
        
        String color = (String) record.doAction("getColor", actionContext, "tableItem", item);
        if(color != null && !"".equals(color)) {
        	Color c = SwtUtils.createColor(item.getParent(), color, actionContext);
        	if(c != null) {
        		item.setBackground(c);
        	}
        }
        
        String value = (String) record.doAction("getFont", actionContext, "tableItem", item);
        if(value != null && !"".equals(value)) {
        	Font font = SwtUtils.createFont(item.getParent(), value, actionContext);
        	if(font != null) {
        		item.setFont(font);
        	}
        }
        
        //执行updateSWTTableItem
        record.doAction("updateSWTTableItem", actionContext, "tableItem", item);
        
	}
	
	protected static void updateItem(Thing self, TableItem item, DataObject record, ActionContext actionContext) {
		//log.info("DataStore: update table item");
        String[] texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
        
        item.setText(texts);			
        
        updateItemICF(self, item, record, actionContext);
        
        if((item.getParent().getStyle() & SWT.CHECK) == SWT.CHECK){
        	if(record.getBoolean(DataObject.CHECKED_ATTRIBUTE_NAME)){
        		item.setChecked(true);
        	}else{
        		item.setChecked(false);
        	}
        }
        
        ItemRowEditor rowEditor = (ItemRowEditor) item.getData("__TableDataStore_ItemRowEditor__");
        if(rowEditor != null) {
        	rowEditor.update();
        }
	}
	
	/**
	 * 数据仓库数据装载事件。
	 * 
	 * @param actionContext
	 */
	public static void onLoaded(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");
		if(table == null || table.isDisposed()){
		    return;
		}
		final Thing store = (Thing) actionContext.get("store");		

		Runnable runner = new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
			    try{
			    	if(table.isDisposed()) {
			    		return;
			    	}
			    	
			        //先清空数据
			        table.removeAll();
			        DSSelectionListener.clear(table);
			        actionContext.peek().put("store", store); //store有丢失现象，可能之前的store不是全局变量
			        
			        List<DataObject> records = (List<DataObject>) store.get("records");
			        if(records != null){
			            //log.info("records=" + store.records);
			            for(DataObject record : records){
			            	createItem(self, table, record, -1, actionContext);
			            }
			            
			            if(store.get("pageInfo") != null){
			                for(TableColumn column : table.getColumns()){
			                	PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
			                    if(pageInfo.getSort() != null && pageInfo.getSort().equals(column.getData("name"))){
			                        table.setSortColumn(column);
			                        if("DESC".equals(pageInfo.getDir())){
			                            table.setSortDirection(SWT.DOWN);	
			                        }else{
			                            table.setSortDirection(SWT.UP);	
			                        }
			                        break;
			                    }
			                }
			            }
			        }
			        
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onLoaded error", t);
			    }
			}
		};
		
		SwtStoreUtils.runAsync(store, table.getDisplay(), runner);
	}
	
	/**
	 * 配置表格。
	 * 
	 * @param actionContext
	 */
	public static void onReconfig(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Table table = (Table) self.get("table");		
		final Thing store = (Thing) actionContext.get("store");
		final World world = World.getInstance();
		
		SwtStoreUtils.runSync(store, table.getDisplay(), new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
				try{
					DSSelectionListener.bind(table, store, actionContext);
			        table.setData("_store", store);

			        //删除所有数据
			        table.removeAll();
			        //删除所有列
			        for(TableColumn column : table.getColumns()){
			            column.dispose();
			        }

			        //删除附加的监听器
			        Listener defaultSelectionListener = (Listener) self.get("defaultSelectionListener");
			        if(defaultSelectionListener != null){
			            table.removeListener(SWT.DefaultSelection, defaultSelectionListener);
			        }
			        Listener selectionListener = (Listener) self.get("selectionListener");
			        if(selectionListener != null){
			            table.removeListener(SWT.Selection, selectionListener);
			        }
			        
			        //删除TableCursor和相关Listener
			        Control tableCursor = (Control) table.getData("tableCursor");
			        if(tableCursor != null){
			        	tableCursor.dispose();
			        }
			        Listener tableCursorListener = (Listener) table.getData("tableCursorListener");
			        if(tableCursorListener != null){
			        	table.removeListener(SWT.Selection, tableCursorListener);
			        }
			        
			        //重新组建表格的列
			        Thing dataObject = (Thing) store.get("dataObject");
			        if(dataObject == null){
			            log.info("TableDataStoreListener store dataObject is null");
			            return;
			        }
			        
			        List<Thing> columns = new ArrayList<Thing>();
			        for(Thing attr : (List<Thing>) dataObject.get("attribute@")){
			        	int style = SWT.NONE;
			            if(attr.getBoolean("viewField") && attr.getBoolean("showInTable") && attr.getBoolean("gridEditor")){
			            	String gridAlign = attr.getString("gridAlign");
			            	if("left".equals(gridAlign)){
			            		style = style | SWT.LEFT;
			            	}else if("right".equals(gridAlign)){
			                    style = style | SWT.RIGHT;
			            	}else if("center".equals(gridAlign)){
			                    style = style | SWT.CENTER;
			                }
			                
			            	TableColumn column = new TableColumn(table, style);
			                column.setText(attr.getMetadata().getLabel());
			                column.setData("name", attr.getString("name"));
			                column.setData("store", store);
			                int width = attr.getInt("gridWidth");
			                if(width <= 0){
			                    width = 80;
			                }
			                if(width > 0){
			                    column.setWidth(width);
			                }
			                column.setResizable(attr.getBoolean("gridResizable"));
			                column.setMoveable(true);
			                
			                //是否可排序
			                Thing sortListener = world.getThing("xworker.app.view.swt.data.events.TableColumnSortListener");
			                SwtListener columnListener = new SwtListener(sortListener, actionContext, true);
			                //log.info(attr.getString("name") + "=" + attr.getBoolean("gridSortable"));
			                if(attr.getBoolean("gridSortable")){
			                    column.addListener(SWT.Selection, columnListener);
			                }
			                
			                //toDisplayString方法
			                if(attr.getActionThing("toDisplayString") != null){
			                    attr.setData("__tableDataStoreToDisplayString", true);
			                }else{
			                    attr.setData("__tableDataStoreToDisplayString", false);
			                }
			                //pattern
			                String pattern = attr.getStringBlankAsNull("viewPattern");
			                if(pattern == null){
			                    pattern = attr.getStringBlankAsNull("editPattern");
			                }
			                if(pattern != null){
			                	String type = attr.getString("type");
			                	if(UtilData.equalsOne(type, new String[]{"date", "datetime", "time"})){
			                		attr.setData("__tableDataStoreFormater", new SimpleDateFormat(pattern));
			                	}else if(UtilData.equalsOne(type, new String[]{"byte", "short", "int", "double", "float"})){
			                	    attr.setData("__tableDataStoreFormater", new DecimalFormat(pattern));
			                    }
			                }
			                columns.add(attr);
			            }
			        }
			        self.put("columns", columns);
			        table.setData("_columns", columns);
			        
			        //表格是否可编辑
			        boolean editable = false;
			        if(store.getString("editable") == null || "extend".equals(store.getString("editable"))){
			            editable = dataObject.getBoolean("gridEditable");
			        }else{
			            editable = store.getBoolean("editable");
			        }
			        if(editable){
			            String editMethod = store.getString("editmethod");  //编辑方式
			            if("extend".equals(editMethod)){
			                editMethod = dataObject.getString("gridEditType");
			            }
			            
			            if("window".equals(editMethod)){
			                //添加DefaultSelection事件，实现双击表格编辑
			                Thing listenerThing = world.getThing("xworker.app.view.swt.data.events.TableDataStoreListener/@listeners/@tableDefaultSelection/@tableEditAction1");
			                SwtListener listener = new SwtListener(listenerThing, actionContext, true);
			                table.addListener(SWT.DefaultSelection, listener);
			                self.put("defaultSelectionListener", listener);    
			            }else{
			                //创建表格编辑
			                self.doAction("createRowEditor", actionContext, UtilMap.toMap(new Object[]{"columns", columns, "table", table, "store", store}));
			            }
			        }
			        //添加选择事件
			        Thing listenerThing = world.getThing("xworker.app.view.swt.data.events.TableDataStoreListener/@listeners/@tableSelection/@tabelSelectionAction");
			        SwtListener listener = new SwtListener(listenerThing, actionContext, true);
			        table.addListener(SWT.Selection, listener);
			        self.put("selectionListener", listener);  
			        
			        //初始化数据，如果存在
			        Boolean dataLoaded = (Boolean) store.get("dataLoaded"); 
			        if(dataLoaded == true){
			            self.doAction("onLoaded", actionContext, UtilMap.toMap(new Object[]{"store", store}));
			        }else if(store.getBoolean("autoLoad")){
			        	TableItem item = new TableItem(table, SWT.NONE);
				        item.setText("loading...");
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onReconfig error", t);
			    }    	
			}
		});
	}
	
	/**
	 * 目前该方法没有使用到。编辑表格使用的是TableCusor，当TableCursor触发了默认选择事件时，触发createRowEditor(ActionContext)方法
	 * 编辑条目的单元格。
	 * 
	 * @param actionContext
	 */
	public static void tableEditAction(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		World world = World.getInstance();

		//表格中的行
		TableItem item = (TableItem) event.item;
		Table table = item.getParent();

		//创建编辑窗体
		ActionContext ac = new ActionContext();
		Thing store = (Thing) table.getData("_store");
		ac.put("store", store);
		ac.put("parent", table.getShell());
		Thing editorThing = world.getThing("xworker.app.view.swt.widgets.table.DataObjectGridRowEditor/@shell");
		editorThing.doAction("create", ac);
		((Thing) ac.get("form")).doAction("setDataObject", ac, UtilMap.toMap("dataObject", item.getData()));
		Shell shell = (Shell) ac.get("shell");
		shell.pack();
		SwtUtils.centerShell(shell);
		shell.open();
	}
	
	public static void tabelSelectionAction(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		if(!(event.item instanceof TableItem)){
			return;
		}
		
		TableItem item = (TableItem) event.item;
		Table table = item.getParent();
		Thing store = (Thing) table.getData("_store");
		
		Object record = event.item.getData();
		store.put("currentRecord", record);
	}
}