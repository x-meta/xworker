package xworker.swt.custom;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.custom.tableEditors.ItemEditorUtils;

/**
 * Item编辑器，Table、Tree、Grid等的编辑器的基础类。
 * 
 * 具体类型的编辑器在tableEditors包下，具体编辑只要实现create、setValue、getValue和saveValue等方法。
 * 
 * @author zyx
 *
 */
public abstract class ItemEditor {
	final public static String STORE_RECORD = "_store_record";
	
	/** 编辑器对应的事物 */
	protected Thing thing;	
	/** 列的属性定义 */
	protected List<Thing> columns;
	/** 数据仓库 */
	protected Thing dataStore;
	/** 变量上下文 */
	protected ActionContext actionContext;
	/** 控件编辑器，用于编辑条目 */
	protected ControlEditor editor;
	/** 当前正在编辑的条目 */
	protected Item currentItem;
	/** 当前正在编辑的列 */
	protected int currentColumn;
	/** 当前正在编辑的条目的变量上下文，是创建编辑器时创建的 */
	protected ActionContext currentItemContext;
	/** 当前正在编辑的条目的编辑器定义事物 */
	protected Thing currentItemEditor;
	/** TableCursor、TreeCursor或ItemCursor */
	protected Object cursor;
	boolean editorAlwaysExists = false;
	
	public ItemEditor(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
			
	/**
	 * 是否永远不销毁控件，比如当Text回车已保存值时。
	 * 普通单元格的编辑不需要，对编辑器一直存在的那种需要。
	 * 
	 * @param editorAlwaysExists
	 */
	public void setEditorAlwaysExists(boolean editorAlwaysExists) {
		this.editorAlwaysExists = editorAlwaysExists;
	}
	
	public void setCurrent(Thing editorThing, Item item, int column) {
		this.currentColumn = column;
		this.currentItem = item;
		this.createEditor(editorThing);
	}
	
	public Thing getThing() {
		return thing;
	}

	public List<Thing> getColumns() {
		return columns;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public ControlEditor getEditor() {
		return editor;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public int getCurrentColumn() {
		return currentColumn;
	}

	public ActionContext getCurrentItemContext() {
		return currentItemContext;
	}

	public Thing getCurrentItemEditor() {
		return currentItemEditor;
	}

	public Object getCursor() {
		return cursor;
	}

	public boolean isEditorAlwaysExists() {
		return editorAlwaysExists;
	}

	public Listener getDefaultSelection() {
		return defaultSelection;
	}

	public Listener getSeleciton() {
		return seleciton;
	}

	/**
	 * 绑定DefaultSelection，一般通过DefaultSelection触发编辑。
	 * 
	 * @param parent
	 */
	public void attachDefaultSelection(Composite parent) {
		parent.addListener(SWT.DefaultSelection, defaultSelection);	
	}
	
	/**
	 * 绑定Selection，一般通过Selection检测当前选中的Item是否是正在编辑的Item。
	 * 
	 * @param parent
	 */
	public void attachSelection(Composite parent) {
		parent.addListener(SWT.Selection, seleciton);
	}
	
	public void detachDefaultSelection(Composite parent) {
		parent.removeListener(SWT.DefaultSelection, defaultSelection);
	}
	
	public void detachSelection(Composite parent) {
		parent.removeListener(SWT.Selection, seleciton);
	}
	
	/**
	 * 返回组件当前正选中条目。
	 * 
	 * @return
	 */
	protected abstract Item getItem();	
	
	/**
	 * 返回组件当前正在选中的列。
	 * 
	 * @return
	 */
	protected abstract int getColumn();
	
	/**
	 * 返回列的属性定义，如果是DataStore绑定的，那么对应的是数据对象的属性。
	 * 
	 * @param column
	 * @return
	 */
	public Thing getColumnThing(int column) {
		return columns.get(column);
	}
	
	public void setColumns(List<Thing> columns) {
		this.columns = columns;
	}	

	public Thing getDataStore() {
		return dataStore;
	}
	
	public void setDataStore(Thing dataStore) {
		if(dataStore == null) {
			//System.out.println("ItemEditor: dataStore is null");
		}
		this.dataStore = dataStore;
	}

	
	/**
	 * 设置条目对应列的值，一般需要处理的是非DataStore的情形。
	 * 
	 * @param column
	 * @param value
	 */
	protected abstract void setItemValue(Item item, int column, Object value);
	
	/**
	 * 返回Item对应列的值，一般需要处理的是非DataStore的情形。
	 * 
	 * @param column
	 */
	protected abstract Object getItemValue(Item item, int column);
	
	/**
	 * 已设置Editor的事件。
	 */
	protected abstract void onSetEditor();
	
	/**
	 * 获取当前列的编辑器事物。
	 * 
	 * @param column
	 * @return
	 */
	protected Thing getColumnEditor(Item item, int column) {
		return ItemEditorUtils.getColumnEditor(thing, item, column, actionContext);
	}
	
	/**
	 * 返回条目的选择是否已经变更了。
	 * 
	 * @return
	 */
	protected boolean isSelectedChanged() {
		if(getItem() != currentItem || getColumn() != currentColumn) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 返回用于创建Editor的父。
	 * 
	 * @return
	 */
	protected abstract Composite getControlEditorParent();
	
	protected void cancelEdit() {
		if(editor != null && editor.getEditor() != null && !editor.getEditor().isDisposed()){
	        try{
	            //先设置当前编辑器的值，实现编辑器焦点移开后保存编辑结果
	            Thing editorThing = (Thing) editor.getEditor().getData("editorThing");
	            ActionContext editorContext = (ActionContext) editor.getEditor().getData("editorContext");
	            Object value = editorThing.doAction("getValue", editorContext);        
	            saveValue(value);		                
	        }finally{
	        	//销毁编辑器
	            editor.getEditor().dispose();
	            
	            currentItem = null;
	            currentColumn = 0;
	        }
		}
	}
	
	/**
	 * 销毁编辑控件。
	 * 
	 * 不需要手动调用该方法，相关的编辑器会自动关闭。
	 */
	public void dispose() {
		if(editor != null) {
			if(editor.getEditor() != null) {
				editor.getEditor().dispose();
			}
			
			editor.dispose();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object getValue(){
		Item item = getItem();
		int column = getColumn();		
		
		if("store".equals(thing.getString("dataType"))){
		    Map<String, Object> record =  (Map<String, Object>) item.getData();
		    Thing columnAttr = columns.get(column);
	    	if(record != null){
	    		Object value = record.get(columnAttr.getString("name"));
	    		return value;
	    	}else{
	    		return "";
	    	}
		}else{
			return getItemValue(item, column);
			/*
			if(item instanceof TableItem) {
				return ((TableItem) item).getText(column);
			}else if(item instanceof TreeItem) {
				return ((TreeItem) item).getText(column);
			}
			return item.getText();*/   
		}       
	}
	
	@SuppressWarnings("unchecked")
	public void saveValue(Object value) {
		Item item = this.currentItem;//this.getItem();
		if(item == null || item.isDisposed()){
		    return;
		}
		//item.setText(column, String.valueOf(value));
		    
		if("store".equals(thing.getString("dataType"))){
		    //store的有关数据
		    Map<String, Object> record = (Map<String, Object>) item.getData(STORE_RECORD);
		    int column = this.currentColumn;//this.getColumn();
		    Thing columnAttr = columns.get(column);
		    Object oldValue = null;
		    if(record != null){
		    	oldValue = record.get(columnAttr.getString("name"));
		    	record.put(columnAttr.getString("name"), value);
		    	
		    	if(oldValue != record.get(columnAttr.getString("name"))){
				    //如果数据变动了，把联动的数据清空
				    clearRelationData(columnAttr, record, columns, new ActionContext());
				}

		    	Thing store = getDataStore();
		    	this.setItemValue(item, currentColumn, value);
		    	store.doAction("update", actionContext, UtilMap.toParams(new Object[]{"record", record}));
		    }
		}else{
			this.setItemValue(item, currentColumn, value);
		}
	}
	
	/**
	 * 更新编辑器，重新获取值并设置到编辑器中。
	 */
	public void update() {
		if(currentItemEditor != null) {
			Object value = getValue();//getItemValue(currentItem, currentColumn);
			currentItemEditor.doAction("setValue", currentItemContext, UtilMap.toParams(new Object[]{"value", value}));
		}
	}
	
	protected void createEditor(Thing editorThing) {
		currentItemEditor = editorThing;
		currentItemContext = new ActionContext();
	    Object data =  currentItem.getData();
	    currentItemContext.put("params", data); //下拉框联动等时，会使用data作为查询条件
	    currentItemContext.put("parentContext", actionContext);
	    currentItemContext.put("parent", getControlEditorParent());
	    //ac.put("cursor", cursor);
	    currentItemContext.put("item", currentItem);
	    currentItemContext.put("editor", ItemEditor.this);
	    currentItemContext.put("cursor", cursor);
	    currentItemContext.put("column", currentColumn);
	    ItemEditorUtils.setNotDisposeOnSaveValue(currentItemContext, editorAlwaysExists);
	
	    Control columnEditor = (Control) editorThing.doAction("create", currentItemContext);
	    //log.info("columnEditor=" + columnEditor);
	    if(columnEditor != null){
	        Thing editorImplThing = editorThing;//.getChilds().get(0);
	        Object value = getValue();
	        editorImplThing.doAction("setValue", currentItemContext, UtilMap.toParams(new Object[]{"value", value}));
	        columnEditor.setData("item", currentItem);
	        columnEditor.setData("column", currentColumn);
	        columnEditor.setData("editorContext", currentItemContext);
	        columnEditor.setData("editorThing", editorImplThing);
	        /*
	        columnEditor.addListener(SWT.KeyDown, new Listener(){
				@Override
				public void handleEvent(Event event) {
					System.out.println("sddsfs");
					if(event.keyCode == (int) SWT.ESC) {
						
						event.doit = false;
					}
				}
	        	
	        });*/
	        editor.setEditor(columnEditor);
	        onSetEditor();
	        
	        //如果是直接弹出窗口，焦点到弹出窗口上
	        if(columnEditor.getData("window") != null){
	            Shell window = (Shell) columnEditor.getData("window");
	            window.forceActive();
	            window.setFocus();
	        }
	    }
	}
	
    public static void clearRelationData(Thing column, Map<String, Object> record, List<Thing> columns, ActionContext context){
	    if(context.get(column.getString("name")) != null){
	        //避免递归操作
	        return;
	    }
	    context.put(column.getString("name"), column);
	    
	    String modifyStoreListener =(String) column.getString("modifyStoreListener");
	    if(modifyStoreListener != null && modifyStoreListener != ""){
	        for(String lis : modifyStoreListener.split("[,]")){
	            Thing lisColumn = null;
	            for(Thing col : columns){
	                if(lis.equals(col.getString("name"))){
	                    lisColumn = col;
	                    break;
	                }
	            }
	            
	            if(lisColumn != null){
	                record.put(lisColumn.getString("name"), null);
	                clearRelationData(lisColumn, record, columns, context);
	            }
	        }
	    }
	}  
    
    
	/**
	 * Item父控件的默认选择事件，用来触发编辑操作。
	 */
	Listener defaultSelection = new Listener() {
		@Override
		public void handleEvent(Event event) {
			//取消先前的编辑
			cancelEdit();
			
			//要编辑的条目
			Item item = getItem();
			int column = getColumn();
			
			currentItem = item;
			currentColumn = column;
			
			//寻找适合的ColumnEditor
			Thing editorThing = getColumnEditor(item, getColumn());
			
			//System.out.println("TableCursorEditorCreator editorThing=" + editorThing);
			if(editorThing != null){
				createEditor(editorThing);
			}  
		}		
	};
	
	/**
	 * Item父控件的选择事件，如果正在编辑，但是单元格选中了其它地方，那么取消编辑。
	 */
	Listener seleciton = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if(editor == null || editor.getEditor() == null || editor.getEditor().isDisposed()) {
				//当前没有编辑中
				return;
			}
			
			if(isSelectedChanged()) {
				//不是当前选中的单元格，取消编辑
				cancelEdit();
			}
		}
	};
}
