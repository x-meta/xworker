package xworker.swt.nebula;

import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.custom.ItemCursor;
import xworker.swt.custom.ItemEditor;
import xworker.swt.util.SwtUtils;

public class GridColumnEditor extends ItemEditor{
	public static final String DATA_KEY = "__GridColumnEditor_for_gird__";
	
	Grid grid;
	
	@SuppressWarnings("unchecked")
	public GridColumnEditor(Grid grid, Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
		
		GridColumnEditor old = (GridColumnEditor) grid.getData(DATA_KEY);
		if(old != null) {
			//移除旧的
			old.detachDefaultSelection(grid);
			old.detachSelection(grid);
			old.editor.dispose();
		}
		
		this.setColumns((List<Thing>) grid.getData("_columns")); 
		grid.setData(DATA_KEY, this);
		this.grid = grid;
		this.attachDefaultSelection(grid);
		this.attachSelection(grid);
		this.setDataStore((Thing) actionContext.getObject("store"));
		
		//创建编辑器
		editor = new GridEditor(grid);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		this.cursor = new ItemCursor() {
			@Override
			public int getColumn() {
				return GridColumnEditor.this.getColumn();
			}

			@Override
			public Point getLocation() {
				int column = GridColumnEditor.this.getColumn();
				GridItem item = (GridItem) GridColumnEditor.this.getItem();
				Rectangle rec = item.getBounds(column);
				return new Point(rec.x, rec.y);			
			}

			@Override
			public Point getSize() {
				int column = GridColumnEditor.this.getColumn();
				GridItem item = (GridItem) GridColumnEditor.this.getItem();
				Rectangle rec = item.getBounds(column);
				return new Point(rec.width, rec.height);		
			}

			@Override
			public Item getRow() {
				return GridColumnEditor.this.getItem();
			}

			@Override
			public Composite getParent() {
				return GridColumnEditor.this.grid;
			}
			
		};
	}
	
	public Point getSelectedCell() {
		if(SwtUtils.isRWT()) {
			return null;
		}
		
		Point cells[] = grid.getCellSelection();
		if(cells != null && cells.length > 0) {
			return cells[0];
		}
		
		return null;
	}
	
	@Override
	protected Item getItem() {
		Point cell = getSelectedCell();
		if(cell != null) {
			return grid.getItem(cell.y);
		}
		
		return null;
	}

	@Override
	protected int getColumn() {		
		Point cell = getSelectedCell();
		if(cell != null) {
			return cell.x;
		}
		
		return 0;
	}

	@Override
	protected Object getItemValue(Item item, int column) {
		GridItem gitem = (GridItem) item;
		return gitem.getText(column);
	}

	@Override
	protected void setItemValue(Item item, int column, Object value) {
		String text = "";
		if(value != null) {
			text = String.valueOf(value);
		}
		
		GridItem gitem = (GridItem) item;
		gitem.setText(column, text);
	}

	@Override
	protected Composite getControlEditorParent() {
		return grid;
	}
	
    public static void create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	Grid grid = actionContext.getObject("parent");
    	
    	GridColumnEditor editor = new GridColumnEditor(grid, self, actionContext);
    	actionContext.g().put(self.getMetadata().getName(), editor);
    }

	@Override
	protected void onSetEditor() {
		GridEditor gedit = (GridEditor) editor;
		gedit.setColumn(currentColumn);
		gedit.setItem((GridItem) currentItem);
	}
}
