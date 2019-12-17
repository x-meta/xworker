package xworker.swt.xwidgets.dataitems;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class SeparatorDataItem extends DataItem {

	public SeparatorDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public int getStyle(Widget widget) {
		return SWT.SEPARATOR;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new SeparatorDataItem(dataItemContainer, parentItem, self, actionContext); 
	}

}
