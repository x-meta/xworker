package xworker.swt.custom;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;

public class TableCursorEditor extends ItemEditor{
	private final String CURSOR = "__TableCursorEditor_key_curor__";
	Table table;
	TableCursor tableCursor;
	
	@SuppressWarnings("unchecked")
	public TableCursorEditor(Table table, Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	
		this.table = table;
		this.setColumns((List<Thing>) table.getData("_columns")); 
		tableCursor = (TableCursor) table.getData(CURSOR);
		if(tableCursor == null) {
			int style = SwtUtils.getInitStyle(thing.getMetadata().getPath());
			if(thing.getBoolean("BORDER")){
			    style = style | SWT.BORDER;
			}
						
			tableCursor = new TableCursor(table, style);
			this.cursor = tableCursor;
			table.setData(CURSOR, cursor);			
			
			//背景颜色
			Color background = (Color) ResourceManager.createResource(thing.getString("background"), 
			        "xworker.swt.graphics.Color", "rgb", actionContext);
			if(background != null){
				tableCursor.setBackground(background);
			}
			
			//字体颜色
			Color foreground = (Color) ResourceManager.createResource(thing.getString("foreground"), 
			        "xworker.swt.graphics.Color", "rgb", actionContext);
			if(foreground != null){
				tableCursor.setForeground(foreground);
			}	
			
			editor = new ControlEditor(tableCursor);
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			tableCursor.setData("editor", editor);
			tableCursor.setData("thing", thing);
			tableCursor.setData("table", table);
						
			this.attachDefaultSelection(tableCursor);
			tableCursor.addListener(SWT.MouseDown, defaultSelection);
			this.attachSelection(tableCursor);
			//this.attachSelection(table);
		}
		
		this.setDataStore((Thing) actionContext.getObject("store"));
	}

	@Override
	protected Item getItem() {
		return tableCursor.getRow();
	}

	@Override
	protected int getColumn() {
		return tableCursor.getColumn();
	}

	@Override
	protected void setItemValue(Item item, int column, Object value) {
		//dataStore的情况已经在ItemEditor中处理了
		String text = "";
		if(value != null) {
			text = String.valueOf(value);
		}
		
		((TableItem) item).setText(column, text);
	}

	@Override
	protected Object getItemValue(Item item, int column) {
		return ((TableItem) item).getText(column);
	}

	@Override
	protected void onSetEditor() {
	}

	@Override
	protected Composite getControlEditorParent() {
		return tableCursor;
	} 
	
	
    public static void create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	Table table = actionContext.getObject("parent");
    	
    	TableCursorEditor editor = new TableCursorEditor(table, self, actionContext);
    	actionContext.g().put(self.getMetadata().getName(), editor);
    }

}
