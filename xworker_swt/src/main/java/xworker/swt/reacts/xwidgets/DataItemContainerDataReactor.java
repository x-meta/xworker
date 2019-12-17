package xworker.swt.reacts.xwidgets;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactor;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;
import xworker.swt.xwidgets.DataItemListener;

public class DataItemContainerDataReactor extends DataReactor implements DataItemListener{
	DataItemContainer dataItemContainer;
	
	public DataItemContainerDataReactor(DataItemContainer dataItemContainer, Thing self, ActionContext actionContext) {
		super(self, actionContext);

		this.dataItemContainer = dataItemContainer;
		this.dataItemContainer.addListener(this);
	}

	@Override
	public void onSelection(DataItemContainer container, DataItem dataItem) {
		Object data = dataItem.getData();
		if(data != null) {
			List<Object> datas = this.toList(data);
			this.fireSelected(datas, null);
		}
	}

	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = self.doAction("getBindTo", actionContext);
	
		if(dataItemContainer != null) {
			DataItemContainerDataReactor reactor = new DataItemContainerDataReactor(dataItemContainer, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);			
		}
	}

	@Override
	public void onDefaultSelection(DataItemContainer container, DataItem dataItem) {
		onSelection(container, dataItem);		
	}
}
