package xworker.swt.xwidgets.dataitems;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class GroovyDataItem extends DataItem{

	public GroovyDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
	}

	@Override
	public void onSelection(DataItemContainer container) {
		thing.getAction().run(actionContext, "dataItemContainer", container, "dataItem", this);
	}

	@Override
	public void onDefaultSelection(DataItemContainer container) {
		thing.getAction().run(actionContext, "dataItemContainer", container, "dataItem", this);
	}

	@Override
	public Object getData() {
		return null;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new GroovyDataItem(dataItemContainer, parentItem, self, actionContext);
	}
}
