package xworker.app.view.swt.data.events;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
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
import xworker.dataObject.utils.DataObjectUtil;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtUtils;

public class TreeDataStoreListener {
	private static Logger log = LoggerFactory.getLogger(TreeDataStoreListener.class);
	
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
	 * 创建行编辑器，其实是按单元格编辑。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void createRowEditor(final ActionContext actionContext){
		//按单元格编辑
		Thing self = (Thing) actionContext.get("self");
		Thing cursorThing = new Thing("xworker.swt.widgets.Tree/@TreeCursorEditor");
		List<Thing> columns = (List<Thing>) actionContext.get("columns");
		Thing store = (Thing) actionContext.get("store");
		cursorThing.initDefaultValue();
		cursorThing.put("name", store.getString("name") + "CursorEditor");
		cursorThing.put("dataType", "store");

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
		        Thing columnEditor = new Thing("xworker.swt.custom.TreeCursorEditor/@ColumnEditor");
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
		    bindings.put("parent", (Tree) actionContext.get("tree"));
		    
		    cursorThing.doAction("create", actionContext);
		    //Clipboard.add(cursorThing);
		}finally{
		    actionContext.pop();
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
		final Tree tree = (Tree) self.get("tree");
		if(tree == null || tree.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");		
		
		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runAsync(store, tree.getDisplay(), new Runnable(){
			public void run(){
				try{
					DSSelectionListener.removeRecords(tree, records);
			        for(DataObject record : records){
			            for(TreeItem item : tree.getItems()){
			            	removeRecord(item, record);
			            }
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onRemove error", t);
			    }
			}
		});
	}
	
	public static TreeItem getTreeItemByRecord(TreeItem item, DataObject record){
		DataObject itemRecord = (DataObject) item.getData("_store_record");
        if(record == itemRecord){
             return item;
        }
        
        for(TreeItem child : item.getItems()){
       	    TreeItem ritem = getTreeItemByRecord(child, record);
       	    if(ritem != null){
       	    	return ritem;
       	    }
        }
        
        return null;
	}
	
	public static void removeRecord(TreeItem item, DataObject record){
		 DataObject itemRecord = (DataObject) item.getData("_store_record");
         if(record == itemRecord){
             item.dispose();
             return;
         }
         
         for(TreeItem child : item.getItems()){
        	 removeRecord(child, record);
         }
	}
	
	/**
	 * 更新数据的事件。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void onUpdate(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Tree tree = (Tree) self.get("tree");
		if(tree == null || tree.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");

		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runAsync(store, tree.getDisplay(), new Runnable(){
			public void run(){
				try{
			        for(DataObject record : records){
			            for(TreeItem item : tree.getItems()){
			            	TreeItem theItem = getTreeItemByRecord(item, record);
			            	if(theItem != null){
				                DataObject itemRecord = (DataObject) theItem.getData("_store_record");
				                if(record == itemRecord){
				                    //log.info("DataStore: update table item");
				                    String[] texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
				                    
				                    theItem.setText(texts);
				                }
				                
				                continue;
			            	}
			            }
			        }
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
		final Tree tree = (Tree) self.get("tree");
		if(tree == null || tree.isDisposed()){
		    return;
		}
		final List<DataObject> records = (List<DataObject>) actionContext.get("records");
		final Thing store = (Thing) actionContext.get("store");
		final String idName = store.getString("idName");
		final String parentIdName = store.getString("parentIdName");
		
		SwtStoreUtils.runAsync(store, tree.getDisplay(), new Runnable(){
			public void run(){
				try{
			        for(DataObject record : records){
			        	Object parentId = record.get(parentIdName);
			        	TreeItem parentItem = getTreeItemByParentId(tree, idName, parentId);
			        	
			        	//过滤重复
			        	boolean have = false;
			            if(parentItem == null){
			            	for(TreeItem item : tree.getItems()) {
			            		if(item.getData() == record) {
			            			have = true;
			            			break;
			            		}
			            	}
			            }else{
			            	for(TreeItem item : parentItem.getItems()) {
			            		if(item.getData() == record) {
			            			have = true;
			            			break;
			            		}
			            	}
			            }
			       
			        	if(have) {
			        		continue;
			        	}
			        	
			            String[] texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
			            
			            TreeItem treeItem = null;
			            if(parentItem == null){
			            	treeItem = new TreeItem(tree, SWT.NONE);
			            }else{
			            	treeItem = new TreeItem(parentItem, SWT.NONE);
			            }
			            treeItem.setText(texts);
			            treeItem.setData(record);
			            treeItem.setData("_store_record", record);
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onInsert error", t);
			    }
			}
		});
	}
	
	public static TreeItem getTreeItemByParentId(Tree tree, String idName, Object parentId){
		for(TreeItem childItem : tree.getItems()){
			TreeItem fitem = getTreeItemByParentId(childItem, idName, parentId);
			if(fitem != null){
				return fitem;
			}
		}
		
		return null;
	}
	
	private static TreeItem getTreeItemByParentId(TreeItem item, String idName, Object parentId){
		DataObject data = (DataObject) item.getData("_store_record");
		Object pid = data.get(idName);
		if(pid != null && pid.equals(parentId)){
			return item;
		}
		
		for(TreeItem childItem : item.getItems()){
			TreeItem fitem = getTreeItemByParentId(childItem, idName, parentId);
			if(fitem != null){
				return fitem;
			}
		}
		
		return null;
	}
	
	/**
	 * 装载前事件。
	 * 
	 * @param actionContext
	 */
	public static void beforeLoad(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Tree tree = (Tree) self.get("tree");
		if(tree == null || tree.isDisposed()){
		    return;
		}

		Thing store = (Thing) actionContext.get("store");
		SwtStoreUtils.runSync(store, tree.getDisplay(), new Runnable(){
			public void run(){
			    try{
			        //先清空数据
			    	tree.removeAll();
			        
			        TreeItem item = new TreeItem(tree, SWT.NONE);
			        item.setText("loading...");
			    }catch(Throwable t){
			        log.error("TableDataStoreListener beforeLoad error", t);
			    }
			}
		});
	}
	
	/**
	 * 数据仓库数据装载事件。
	 * 
	 * @param actionContext
	 */
	public static void onLoaded(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Tree tree = (Tree) self.get("tree");
		if(tree == null || tree.isDisposed()){
		    return;
		}
		final Thing store = (Thing) actionContext.get("store");		

		Runnable runner = new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
			    try{
			        //先清空数据
			    	tree.removeAll();
			    	DSSelectionListener.clear(tree);
			    	
			        List<DataObject> records = (List<DataObject>) store.get("records");
			        if(records != null){
			            //log.info("records=" + store.records);
			        	String idName = store.getStringBlankAsNull("idName");
			            String parentIdName = store.getStringBlankAsNull("parentIdName");
			            Map<String, String> idMap = new HashMap<String, String>();
			            for(DataObject record : records){
			            	idMap.put(record.getString(idName), record.getString(idName));
			            }
			            for(DataObject record : records){
			                //log.info("record=" + record);
			            	//如果存在父节点，那么不在根目录里显示
			            	String parentId = record.getString(parentIdName);
			            	if(idMap.get(parentId) != null){
			            		continue;
			            	}
			            	
			                String[] texts = null;
			                texts = (String[]) self.doAction("recordToRowTexts", actionContext, UtilMap.toMap(new Object[]{"record", record}));
			                //log.info("texts=" + texts);
			                TreeItem treeItem = new TreeItem(tree, SWT.NONE);
			                treeItem.setText(texts);
			                treeItem.setData(record);
			                treeItem.setData("_store_record", record);
			            }
			            
			            if(store.get("pageInfo") != null){
			                for(TreeColumn column : tree.getColumns()){
			                	PageInfo pageInfo = PageInfo.getPageInfo(store.get("pageInfo"));
			                    if(pageInfo.getSort() != null && pageInfo.getSort().equals(column.getData("name"))){
			                    	tree.setSortColumn(column);
			                        if("DESC".equals(pageInfo.getDir())){
			                        	tree.setSortDirection(SWT.DOWN);	
			                        }else{
			                        	tree.setSortDirection(SWT.UP);	
			                        }
			                        break;
			                    }
			                }
			            }
			            
			            if(self.getBoolean("dynamicLoadChilds") == false) {
			            	actionContext.peek().put("self", self);
			    	        String dataObjectPath = ((Thing) store.get("dataObject")).getMetadata().getPath();        
			    	        if(idName != null && parentIdName != null){
			    	        	//actionContext.peek().put("self", self);
			    	        	for(TreeItem item : tree.getItems()) {
			    	        		initChildItems(item, dataObjectPath, idName, parentIdName, actionContext);
			    	        	}
			    	        }else{
			    	        	log.warn("TreeDataStore idName or parnetIdName is null,not init child items, store=" + store.getMetadata().getPath());
			    	        }			    	        
			            }
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onLoaded error", t);
			    }
			}
		};
		
		SwtStoreUtils.runAsync(store, tree.getDisplay(), runner);
	}
	
	public static void initChildItems(TreeItem parentItem, String dataObjectPath, String idName, String parentIdName, ActionContext actionContext){
		DataObject parentData = (DataObject) parentItem.getData();
		Object parentId = parentData.get(idName);
		
		List<DataObject> datas = DataObjectUtil.query(dataObjectPath, UtilMap.toMap(parentIdName, parentId), actionContext);
        for(DataObject data: datas){
            actionContext.peek().put("record", data);
            String[] texts = TreeDataStoreListener.recordToRowTexts(actionContext);
            
    	    TreeItem treeItem = new TreeItem(parentItem, SWT.NONE);
            treeItem.setText(texts);
            treeItem.setData(data);
            treeItem.setData("_store_record", data);
        }
        
        for(TreeItem childItem : parentItem.getItems()){
        	initChildItems(childItem, dataObjectPath, idName, parentIdName, actionContext);
        }
	}
	
	/**
	 * 配置表格。
	 * 
	 * @param actionContext
	 */
	public static void onReconfig(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Tree tree = (Tree) self.get("tree");		
		final Thing store = (Thing) actionContext.get("store");
		final World world = World.getInstance();
		tree.setData("__TreeDataStoreListener__", self);
		
		SwtStoreUtils.runSync(store, tree.getDisplay(), new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
				try{
					DSSelectionListener.bind(tree, store, actionContext);
					tree.setData("_store", store);

			        //删除所有数据
					tree.removeAll();
			        //删除所有列
			        for(TreeColumn column : tree.getColumns()){
			            column.dispose();
			        }

			        //删除附加的监听器
			        Listener defaultSelectionListener = (Listener) self.get("defaultSelectionListener");
			        if(defaultSelectionListener != null){
			        	tree.removeListener(SWT.DefaultSelection, defaultSelectionListener);
			        }
			        Listener selectionListener = (Listener) self.get("selectionListener");
			        if(selectionListener != null){
			        	tree.removeListener(SWT.Selection, selectionListener);
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
			                
			            	TreeColumn column = new TreeColumn(tree, style);
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
			        tree.setData("_columns", columns);
			        
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
			            
			            //tree暂时只保留一种编辑方法
			            if("window".equals(editMethod)){
			                //添加DefaultSelection事件，实现双击表格编辑
			                Thing listenerThing = world.getThing("xworker.app.view.swt.data.events.TreeDataStoreListener/@listeners/@tableDefaultSelection/@tableEditAction1");
			                SwtListener listener = new SwtListener(listenerThing, actionContext, true);
			                tree.addListener(SWT.DefaultSelection, listener);
			                self.put("defaultSelectionListener", listener);    
			            }else{
			                //创建表格编辑
			                self.doAction("createRowEditor", actionContext, UtilMap.toMap(new Object[]{"columns", columns, "table", tree, "store", store}));
			            }
			        }
			        //添加选择事件
			        Thing listenerThing = world.getThing("xworker.app.view.swt.data.events.TreeDataStoreListener/@listeners/@tableSelection/@tabelSelectionAction1");
			        SwtListener listener = new SwtListener(listenerThing, actionContext, true);
			        tree.addListener(SWT.Selection, listener);
			        self.put("selectionListener", listener);  
			        
			        //初始化数据，如果存在
			        Boolean dataLoaded = (Boolean) store.get("dataLoaded"); 
			        if(dataLoaded == true){
			            self.doAction("onLoaded", actionContext, UtilMap.toMap(new Object[]{"store", store}));
			        }
			    }catch(Throwable t){
			        log.error("TableDataStoreListener onReconfig error", t);
			    }    	
			}
		});
	}
	
	public static void tableEditAction(ActionContext actionContext){
		//表格中的行
		Event event = (Event) actionContext.get("event");
		World world = World.getInstance();
		TreeItem item = (TreeItem) event.item;
		Tree table = item.getParent();

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
		TreeItem item = (TreeItem) event.item;
		//System.out.println(item.getExpanded());
		Tree tree = item.getParent();
		Thing store = (Thing) tree.getData("_store");
		
		Object record = event.item.getData();
		store.put("currentRecord", record);
		
		String idName = store.getStringBlankAsNull("idName");
        String parentIdName = store.getStringBlankAsNull("parentIdName");
                
        //初始化子节点
        Thing self = (Thing) tree.getData("__TreeDataStoreListener__");
        String initKey = "__TreeDataStoreListener_child_inited__";
	    if(self.getBoolean("dynamicLoadChilds") == true && UtilData.isTrue(item.getData(initKey)) == false){
	    	actionContext.peek().put("self", self);
	        String dataObjectPath = ((Thing) store.get("dataObject")).getMetadata().getPath();        
	        if(idName != null && parentIdName != null){
	        	//actionContext.peek().put("self", self);
        		initChildItems(item, dataObjectPath, idName, parentIdName, actionContext);
	        }else{
	        	log.warn("TreeDataStore idName or parnetIdName is null,not init child items, store=" + store.getMetadata().getPath());
	        }
	        
	        item.setData(initKey, true);
        }
	}
}
