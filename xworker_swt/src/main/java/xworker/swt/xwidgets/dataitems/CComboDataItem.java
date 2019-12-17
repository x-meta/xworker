package xworker.swt.xwidgets.dataitems;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class CComboDataItem extends DataItem{

	public CComboDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public int getStyle(Widget parent) {
		if(parent instanceof ToolBar) {
			return SWT.SEPARATOR;
		}else if(parent instanceof Menu) {
			return SWT.SEPARATOR;
		}else if(parent instanceof CoolBar) {
			return SWT.SEPARATOR;
		}
		
		return SWT.NONE;
	}

	@Override
	public void onBind(Widget parent, Widget item) {
		if(item instanceof ToolItem) {
			ToolItem toolItem = (ToolItem) item;
			CCombo text = new CCombo((ToolBar) parent, SWT.BORDER);			
			int width = thing.getInt("width");
			if(width > 0) {
				toolItem.setWidth(width);		
			}else {
				width = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
				toolItem.setWidth(width);
			}
			
			toolItem.setControl(text);

			this.putBindControl(parent, text);
		}else if(item instanceof TreeItem) {
			TreeItem treeItem  = (TreeItem) item;
			TreeEditor editor = new TreeEditor(treeItem.getParent());
			editor.horizontalAlignment = SWT.LEFT;
			int width = thing.getInt("width");
			if(width > 0) {
				editor.minimumWidth = width;
				editor.grabHorizontal = false;
			}else {
				editor.grabHorizontal = true;
			}
	        
	        CCombo text = new CCombo(treeItem.getParent(), SWT.BORDER);
	        editor.setEditor(text, treeItem);
	        this.putBindControl(parent, text);		
		}
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new CComboDataItem(dataItemContainer, parentItem, self, actionContext);
	}

}
