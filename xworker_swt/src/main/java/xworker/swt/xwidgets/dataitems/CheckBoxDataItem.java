package xworker.swt.xwidgets.dataitems;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class CheckBoxDataItem extends DataItem{

	public CheckBoxDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public int getStyle(Widget parent) {
		if(parent instanceof ToolBar) {
			return SWT.CHECK;
		}else if(parent instanceof Menu) {
			return SWT.CHECK;
		}else if(parent instanceof CoolBar) {
			return SWT.SEPARATOR;
		}
		
		return SWT.NONE;
	}

	@Override
	public void onBind(Widget parent, Widget item) {
		 if(item instanceof TreeItem) {
			TreeItem treeItem  = (TreeItem) item;
			TreeEditor editor = new TreeEditor(treeItem.getParent());
			editor.horizontalAlignment = SWT.LEFT;
	        editor.grabHorizontal = true;
	        editor.minimumWidth = 50;
	        Button text = new Button(treeItem.getParent(), SWT.CHECK);	
	        editor.setEditor(text, treeItem);
	        this.putBindControl(parent, text);
		}
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new CheckBoxDataItem(dataItemContainer, parentItem, self, actionContext);
	}

}
