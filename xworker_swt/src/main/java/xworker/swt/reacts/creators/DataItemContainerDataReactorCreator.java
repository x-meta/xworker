package xworker.swt.reacts.creators;

import org.xmeta.ActionContext;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.xwidgets.DataItemContainer;

public class DataItemContainerDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		if(control instanceof DataItemContainer) {			
			DataItemContainer dataItem  = (DataItemContainer) control;
			return dataItem.getDataReactor();  //DataItemContainer自行创建DataReactor			
		}
		
		return null;
	}

}
