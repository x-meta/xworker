package xworker.swt.custom.tableEditors;

import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.TreeCursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.custom.ItemCursor;
import xworker.swt.custom.ItemEditor;

public class ItemEditorUtils {
	private static Logger logger = LoggerFactory.getLogger(ItemEditorUtils.class);
	private static final String DISPOSE_KEY = "___ItemEditorUtils__dispose_onsavevaue__";
	
	/**
	 * 保存值到Item上。
	 * 
	 * @param item
	 * @param column
	 * @param value
	 * @param actionContext
	 */
	public static void saveValue(Item item, int column, Object value,  ActionContext actionContext) {
		//新的ItemEditor实现
		ItemEditor editor = actionContext.getObject("editor");
		if(editor != null) {
			editor.saveValue(value);
			return;
		}
		
		//旧的基于Cursor的实现，其中cursorThing是创建Cursor的事物，它负责从Item上存储和读取数据
		Thing cursorThing = actionContext.getObject("cursorThing");
		cursorThing.doAction("setValue", (ActionContext) actionContext.get("parentContext"), 
 	            UtilMap.toParams(new Object[]{"item", item, "column", column, "value", value}));
	}
	
	public static <T> T createInstance(Class<T> tcls, ActionContext actionContext) {
		
		String key = "key_" + tcls.getName();
		T editor = actionContext.getObject(key);
		if(editor == null) {
			try {
				editor = tcls.newInstance();
			} catch (Exception e) {
				logger.error("Create tableeditor instance error", e);
			}
			actionContext.g().put(key, editor);
		}
		
		return editor;
	}
	
	public static int getCursorColumn(Object cursor) {
		if(cursor instanceof TreeCursor) {
			return ((TreeCursor) cursor).getColumn();
		}else if(cursor instanceof TableCursor) {
			return ((TableCursor) cursor).getColumn();
		}else if(cursor instanceof ItemCursor) {
			return ((ItemCursor) cursor).getColumn();
		}
		
		return -1;
	}
	
	public static Object getCursorRow(Object cursor) {
		if(cursor instanceof TreeCursor) {
			return ((TreeCursor) cursor).getRow();
		}else if(cursor instanceof TableCursor) {
			return ((TableCursor) cursor).getRow();
		}else if(cursor instanceof ItemCursor) {
			return ((ItemCursor) cursor).getRow();
		}
		
		return null;
	}
	
	public static Point getCursorLocation(Object cursor) {
		if(cursor instanceof TreeCursor) {
			return ((TreeCursor) cursor).getLocation();
		}else if(cursor instanceof TableCursor) {
			return ((TableCursor) cursor).getLocation();
		}else if(cursor instanceof ItemCursor) {
			return ((ItemCursor) cursor).getLocation();
		}
		
		return null;
	}
	
	public static Point getCursorSize(Object cursor) {
		if(cursor instanceof TreeCursor) {
			return ((TreeCursor) cursor).getSize();
		}else if(cursor instanceof TableCursor) {
			return ((TableCursor) cursor).getSize();
		}else if(cursor instanceof ItemCursor) {
			return ((ItemCursor) cursor).getSize();
		}
		
		return null;
	}
	
	public static Composite getCursorParent(Object cursor) {
		if(cursor instanceof TreeCursor) {
			return ((TreeCursor) cursor).getParent();
		}else if(cursor instanceof TableCursor) {
			return ((TableCursor) cursor).getParent();
		}else if(cursor instanceof ItemCursor) {
			return ((ItemCursor) cursor).getParent();
		}else if(cursor instanceof ControlEditor ) {
			ControlEditor  editor = (ControlEditor ) cursor;
			if(editor.getEditor() != null) {
				return editor.getEditor().getParent();
			}
		}
		
		return null;
	}
	
	/**
	 * 返回是否在编辑保存之后销毁控件。如要设置不销毁控件，参看setDisposeOnSaveValue。
	 * 
	 * Item的按单元格编辑是需要消耗控件，而按行编辑时编辑器一直存在，故不需要销毁控件。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static boolean isNotDisposeOnSaveValue(ActionContext actionContext) {
		Boolean dis = actionContext.getBoolean(DISPOSE_KEY);
		if(dis == null || dis.booleanValue() == false) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 设置是否在保存之后销毁编辑器，在Item编辑器中使用。
	 * 
	 * @param actionContext
	 * @param dispose
	 */
	public static void setNotDisposeOnSaveValue(ActionContext actionContext, boolean dispose) {
		actionContext.g().put(DISPOSE_KEY, dispose);
	}
	
	public static  Thing getColumnEditor(Thing itemEditor, int column) {
		Thing defaultEditorThing = null;
		Thing editorThing = null;
		
		for(Thing child : itemEditor.getChilds("ColumnEditor")){
			String childColumn = child.getString("column");
		    if(childColumn == null || childColumn == ""){
		        defaultEditorThing = child;
		    }else if(childColumn.equals(String.valueOf(column))){
		        editorThing = child;
		        break;
		    }
		}
		
		if(editorThing  == null && defaultEditorThing != null){
		    editorThing = defaultEditorThing;
		}
		
		if(editorThing != null && editorThing.getChilds().size() > 0) {
			return editorThing.getChilds().get(0);
		}else {
			return null;
		}
	}
}
