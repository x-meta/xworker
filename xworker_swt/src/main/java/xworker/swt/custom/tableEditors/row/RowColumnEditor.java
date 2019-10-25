package xworker.swt.custom.tableEditors.row;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 行中的一个列，即一个单元格的编辑器。
 * 
 * @author zyx
 *
 */
public class RowColumnEditor {
	TableItem item;
	Thing editorThing;
	int column;
	Thing cursorThing;
	Control editor;
	ActionContext actionContext;
	TableEditor tableEditor;
	
	public RowColumnEditor(Thing cursorThing, Thing editorThing, TableItem item, int column, ActionContext parentContext) {
		this.cursorThing = cursorThing;
		this.editorThing = editorThing;
		this.item = item;
		this.column = column;
		this.actionContext = new ActionContext();
		
		actionContext.put("params", item.getData()); //下拉框联动等时，会使用data作为查询条件
		actionContext.put("parentContext", actionContext);
		actionContext.put("parent", item.getParent());
	    //ac.put("cursor", cursor);
		actionContext.put("item", item);
		actionContext.put("column", column);
		actionContext.put("table", item.getParent());
		actionContext.put("cursorThing", cursorThing);
		
		Control columnEditor = (Control) editorThing.doAction("create", actionContext);
		tableEditor = new TableEditor(item.getParent());
		
		tableEditor.horizontalAlignment = SWT.LEFT;
    	tableEditor.grabHorizontal = true;
    	//tableEditor.minimumWidth = 50;

    	tableEditor.setEditor(columnEditor, item, column);
	}
	
	
}
