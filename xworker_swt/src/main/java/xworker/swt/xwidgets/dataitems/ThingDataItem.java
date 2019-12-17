package xworker.swt.xwidgets.dataitems;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class ThingDataItem extends DataItem{
	Thing data;
	
	public ThingDataItem(DataItemContainer dataItemContainer, DataItem parentItem, Thing thing, String getDataMethod, ActionContext actionContext) {
		super(dataItemContainer, parentItem, true, thing, actionContext);
		
		data = thing.doAction(getDataMethod, actionContext);
	}
	
	@Override
	public Image getIcon(Control control) {
		if("none".equals(thing.getString("icon"))){
			return null;
		}
		
		if(data != null) {
			return SwtUtils.getIcon(data, control, actionContext);
		} else {
			return SwtUtils.getIcon(thing, control, actionContext);
		}
	}

	@Override
	public Object getData() {
		return data;
	}
	
	public static DataItem createComposite(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
				
		return new ThingDataItem(dataItemContainer, parentItem, self, "getComposite", actionContext);
	}
	
	public static DataItem createQuickContent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new ThingDataItem(dataItemContainer, parentItem, self, "getQuickContent", actionContext);
	}
	
	public static DataItem createDataSource(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new ThingDataItem(dataItemContainer, parentItem, self, "getDataSource", actionContext);
	}
	
	public static DataItem createThingRegistor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new ThingDataItem(dataItemContainer, parentItem, self, "getThingRegistor", actionContext);
	}
	
	public static DataItem createThingRegistContent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
		return new ThingDataItem(dataItemContainer, parentItem, self, "getRegistContent", actionContext);
	}
}
