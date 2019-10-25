package xworker.swt.custom;

import java.util.List;

import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 表格单元格编辑器，用于ItemRowEditor，是只要TableItem存在就一直存在的编辑器。
 * 
 * @author zyx
 *
 */
public class TableCellEditor extends ItemEditor{
	TableItem item;
	Table table;
	int column;
	
	@SuppressWarnings("unchecked")
	public TableCellEditor(TableItem item, int column, Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	
		this.item = item;
		this.currentItem = item;
		this.currentColumn = column;
		this.table = item.getParent();
		this.column = column;
		this.setColumns((List<Thing>) table.getData("_columns")); 
		this.setDataStore((Thing) actionContext.getObject("store"));
		this.setEditorAlwaysExists(true);
		
		editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		this.cursor = editor;
		Thing editorThing = getColumnEditor(getColumn());
		//System.out.println("TableCursorEditorCreator editorThing=" + editorThing);
		if(editorThing != null){
			createEditor(editorThing);
			((TableEditor) editor).setItem(item);
			((TableEditor) editor).setColumn(column);
		} 	
	}

	@Override
	protected Item getItem() {
		return item;
	}

	@Override
	protected int getColumn() {
		return column;
	}

	@Override
	protected void setItemValue(Item item, int column, Object value) {
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
		return table;
	} 
}
